package ironfurnaces.tileentity.furnaces;

import ironfurnaces.Config;
import ironfurnaces.adaptor.energy.EnergyWrapper;
import ironfurnaces.adaptor.energy.FEnergyStorage;
import ironfurnaces.recipes.GeneratorRecipe;
import ironfurnaces.tileentity.furnaces.cache.*;
import ironfurnaces.tileentity.furnaces.handler.IFurnaceLitHandler;
import ironfurnaces.tileentity.furnaces.handler.RecipeAwardHandler;
import ironfurnaces.tileentity.furnaces.process.Burn;
import ironfurnaces.tileentity.furnaces.process.Generate;
import ironfurnaces.tileentity.furnaces.process.ProcessingInstanceManager;
import ironfurnaces.tileentity.furnaces.setting.FurnaceSettingsV2;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.EmptyHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Optional;
import java.util.function.Function;

@Getter
public abstract class BlockIronFurnaceTileBaseV2 extends BaseContainerBlockEntity implements RecipeHolder, StackedContentsCompatible, WorldlyContainer {

    private final InputCache input;
    private final OutputCache output;
    private final FuelCache fuel;
    private final RemainingCache remaining;
    private final AugmentCache augments;
    private final Function<RecipeType<?>, RecipeManager.CachedCheck<Container, ?>> quickCheck;
    private final IFCombinedCache allInv;
    private final IFCombinedCache allOutput;
    private ProcessingInstanceManager instanceManager;

    @Setter
    private FurnaceMode mode;
    private IFurnaceLitHandler litHandler;
    private FurnaceSettingsV2 settingsV2 = FurnaceSettingsV2.DEFAULT;
    private final EnumMap<Direction, LazyOptional<IItemHandlerModifiable>> sidedHandlers = Util.make(() -> {
        EnumMap<Direction, LazyOptional<IItemHandlerModifiable>> enumMap = new EnumMap<>(Direction.class);
        for (Direction dir : Direction.values()) {
            FurnaceSettingsV2.IOMode mode = settingsV2.IOSetting().get(dir);
            IItemHandlerModifiable handler = mode.handlerSelector.apply(this);

            enumMap.put(dir, LazyOptional.of(() -> handler));
        }
        return enumMap;
    });

    protected BlockIronFurnaceTileBaseV2(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        this.mode = FurnaceMode.FURNACE;
        this.quickCheck = Util.memoize(x -> RecipeManager.createCheck((RecipeType) x));

        this.remaining = new RemainingCache();
        this.augments = new AugmentCache(this::updateFurnaceMode);
        Function<ItemStack, Optional<? extends Recipe>> callback = Util.memoize(this::getRecipe);

        this.input = new InputCache(6, this.mode, getTier()).withGrabRecipeCallback(callback).contentChangeCallback(x -> {
            setChanged();
            if (settingsV2.autoSplit()) x.splitStacks(false);
        });
        this.output = new OutputCache(6, this.mode, getTier()).contentChangeCallback(x -> {
            this.instanceManager.refreshBlockingState();
            setChanged();
        });

        this.fuel = new FuelCache(this, new FEnergyStorage(Config.furnaceEnergyCapacityTier2.get()).withCallback(fEnergyStorage -> setChanged()), mode).grabRecipeCallback(callback).contentChangeCallback(x -> setChanged());

        this.instanceManager = new ProcessingInstanceManager(new ArrayList<>(), this);

        this.litHandler = this.mode.litHandlerSelector.apply(this);

        //augment shouldn't be involved...?
        this.allInv = new IFCombinedCache(this.input, output, fuel, remaining);
        this.allOutput = new IFCombinedCache(output, remaining);
    }

    public BlockIronFurnaceTileBaseV2(BlockEntityType<?> type, BlockPos pos, BlockState blockState, FurnaceMode mode) {
        this(type, pos, blockState);
        this.mode = mode;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, BlockIronFurnaceTileBaseV2 blockEntity) {
        blockEntity.selectLitHandler();
        blockEntity.tryProcessInput();
        blockEntity.litHandler.tick();
        if (blockEntity.litHandler.isLit()) {
            blockEntity.getInstanceManager().manage();
        }
        blockEntity.autoIO();
        blockEntity.energyOutPerTick();
        blockEntity.updateHandleTick();
    }

    public abstract ForgeConfigSpec.IntValue getTier();

    public abstract String getId();

    private void updateFurnaceMode(FurnaceMode mode) {
        this.mode = mode;
        input.updateFurnaceMode(mode);
        input.dropStacksInUnavailableSlots(this.level, this.getBlockPos());
        output.updateFurnaceMode(mode);
        instanceManager.updateFurnaceMode(mode);
        fuel.updateFurnaceMode(mode);
        allInv.updateFurnaceMode(mode);
        allOutput.updateFurnaceMode(mode);
    }

    public abstract int getGenerationPerTick();

    @Override
    public @Nullable Recipe<?> getRecipeUsed() {
        return null;
    }


    @Override
    public void fillStackedContents(StackedContents contents) {
        for (int i = 0; i < input.getSlots(); i++) {
            contents.accountStack(input.getStackInSlot(i));
        }

    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return ((ICacheIndex) settingsV2.IOSetting().get(side).handlerSelector.apply(this)).getCacheIndex();
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack itemStack, @Nullable Direction direction) {
        Optional<? extends Recipe> recipe = getRecipe(itemStack);
        return settingsV2.IOSetting().get(direction).handlerSelector.apply(this).isItemValid(index, itemStack);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return settingsV2.IOSetting().get(direction).equals(FurnaceSettingsV2.IOMode.OUTPUT);
    }

    private void selectLitHandler() {
        this.litHandler = this.mode.litHandlerSelector.apply(this);
    }

    private Optional<? extends Recipe> getRecipe(ItemStack stack) {
        return quickCheck.apply(augments.getCurrentRecipeType()).getRecipeFor(new SimpleContainer(stack), level);
    }

    private void tryProcessInput() {
        for (int i = 0; i < input.getSlots(); i++) {
            if (!input.isSlotIdle(i)) continue;
            ItemStack stackInSlot = input.getStackInSlot(i);
            int finalI = i;
            getRecipe(stackInSlot)
                    .ifPresent(x -> {
                        if (this.mode.equals(FurnaceMode.GENERATOR)) {
                            int actualPerTickGeneration = augments.getCurrentModifiers().generateOutputModifier().applyAsInt(getGenerationPerTick());
                            if (x instanceof GeneratorRecipe generatorRecipe) {
                                this.instanceManager.addInstance(new Generate.BlastGenerate(generatorRecipe.getEnergy(), generatorRecipe, stackInSlot, actualPerTickGeneration, augments));
                            } else {
                                this.instanceManager.addInstance(new Generate.SmeltGenerate(ForgeHooks.getBurnTime(stackInSlot, x.getType()) * getGenerationPerTick(), x, stackInSlot, actualPerTickGeneration, augments));
                            }
                        } else {
                            if (x instanceof AbstractCookingRecipe cookingRecipe) {
                                this.instanceManager.addInstance(Burn.create(augments.getCurrentModifiers().normalWorkTimeModifier().applyAsInt(cookingRecipe.getCookingTime()), finalI, x, input, this.level, augments));
                            }
                        }
                    });
        }
    }

    public void setSettingsV2(Function<FurnaceSettingsV2, FurnaceSettingsV2> changer) {
        FurnaceSettingsV2 apply = changer.apply(this.settingsV2);
        boolean b = apply.IOSetting().equals(this.settingsV2.IOSetting());
        this.settingsV2 = apply;
        if (b) {
            recalcSideIOCap();
        }
    }

    private int lastProcessedDirection = 0;

    protected void autoIO() {
        if (!settingsV2.autoInput() && !settingsV2.autoOutput()) return;
        Direction[] dirs = Direction.values();
        Direction dir = dirs[lastProcessedDirection];
        //I hate this but I also hate too many if
        Optional.of(dir)
                .ifPresent(direction -> {
                    FurnaceSettingsV2.IOMode mode = settingsV2.IOSetting().get(dir);
                    if (mode == null || mode == FurnaceSettingsV2.IOMode.NONE) return;

                    BlockEntity neighbor = level.getBlockEntity(worldPosition.relative(dir));
                    if (neighbor == null) return;

                    neighbor.getCapability(ForgeCapabilities.ITEM_HANDLER, dir.getOpposite())
                            .ifPresent(x -> {
                                // 输出阶段（优先）
                                if (settingsV2.autoOutput()) {
                                    // 输出只推 OUTPUT 或 FUEL
                                    IItemHandler outputHandler = getOutputHandlerForMode(mode);
                                    if (outputHandler != null) {
                                        transfer(outputHandler, x);
                                    }
                                }

                                // 输入阶段
                                if (settingsV2.autoInput()) {
                                    // 输入只拉 INPUT 或 FUEL
                                    IItemHandler inputHandler = getInputHandlerForMode(mode);
                                    if (inputHandler != null) {
                                        transfer(x, inputHandler);
                                    }
                                }


                            });
                });

    }

    private void updateHandleTick(){
        lastProcessedDirection = (lastProcessedDirection + 1) % Direction.values().length;
    }

    /**
     * 根据 IOMode 返回可作为输出的 handler
     */
    private IItemHandler getOutputHandlerForMode(FurnaceSettingsV2.IOMode mode) {
        return switch (mode) {
            case OUTPUT, FUEL -> mode.handlerSelector.apply(this);
            case ALL -> this.getAllOutput(); // ALL 模式输出阶段只用 OUTPUT
            default -> EmptyHandler.INSTANCE;
        };
    }

    /**
     * 根据 IOMode 返回可作为输入的 handler
     */
    private IItemHandler getInputHandlerForMode(FurnaceSettingsV2.IOMode mode) {
        return switch (mode) {
            case INPUT, FUEL -> mode.handlerSelector.apply(this);
            case ALL -> this.getInput(); // ALL 模式输入阶段只用 INPUT
            default -> EmptyHandler.INSTANCE;
        };
    }

    /**
     * 统一 transfer 函数：将 from 中可提取物品插入到 to
     */
    private void transfer(IItemHandler from, IItemHandler to) {
        if (from == null || to == null) return;

        for (int i = 0; i < from.getSlots(); i++) {
            ItemStack stack = from.getStackInSlot(i).copy();
            if (stack.isEmpty()) continue;

            ItemStack last = ItemHandlerHelper.insertItem(to, stack, false);
            boolean matches = ItemStack.matches(stack, last);
            if (!matches) {
                int newCount = last.getCount();
                int oldCount = stack.getCount();
                from.extractItem(i, oldCount - newCount, false);
            }

/*
            // 预模拟提取
            ItemStack extractable = from instanceof IItemHandlerModifiable mod ?
                    mod.extractItem(i, stack.getCount(), true) : stack.copy();

            if (extractable.isEmpty()) continue;

            // 尝试插入到 to
            int remaining = insertStackToHandler(to, extractable);

            // 真正提取成功数量
            int extractedAmount = extractable.getCount() - remaining;
            if (extractedAmount > 0 && from instanceof IItemHandlerModifiable mod) {
                mod.extractItem(i, extractedAmount, false);
            }*/
        }
    }

    /**
     * 将 stack 插入 handler 中，返回未插入的剩余数量
     */
    private int insertStackToHandler(IItemHandler handler, ItemStack stack) {
        int remaining = stack.getCount();
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack slotStack = handler.getStackInSlot(i);
            if (!handler.isItemValid(i, stack)) continue;

            if (slotStack.isEmpty()) {
                // 完全放入
                if (handler instanceof IItemHandlerModifiable mod) {
                    mod.insertItem(i, stack.copy(), false);
                    return 0;
                }
            } else if (ItemHandlerHelper.canItemStacksStack(slotStack, stack)) {
                int space = handler.getSlotLimit(i) - slotStack.getCount();
                int toInsert = Math.min(space, remaining);
                if (handler instanceof IItemHandlerModifiable mod) {
                    slotStack.grow(toInsert);
                    mod.insertItem(i, slotStack, false);
                }
                remaining -= toInsert;
                if (remaining <= 0) return 0;
            }
        }
        return remaining;
    }


    protected void energyOutPerTick() {
        Direction[] dirs = Direction.values();
        Direction dir = dirs[lastProcessedDirection]; // 本 tick 处理的方向

        BlockEntity tile = level.getBlockEntity(worldPosition.relative(dir));
        if (tile != null) {
            FurnaceSettingsV2.IOMode mode = settingsV2.IOSetting().get(dir);
            // 只处理输出方向（OUTPUT 或 ALL）
            if (mode == FurnaceSettingsV2.IOMode.OUTPUT || mode == FurnaceSettingsV2.IOMode.ALL) {
                tile.getCapability(ForgeCapabilities.ENERGY, dir.getOpposite()).ifPresent(other -> {
                    if (other.canReceive() && other.getEnergyStored() < other.getMaxEnergyStored()) {
                        // 计算可输出能量
                        int maxExtract = getCapability(ForgeCapabilities.ENERGY)
                                .map(IEnergyStorage::getEnergyStored)
                                .orElse(0);
                        int energyToSend = Math.min(maxExtract, fuel.getEnergyStored());

                        // 输出能量，并从自己减少
                        int accepted = other.receiveEnergy(energyToSend, false);
                        fuel.extractEnergy(accepted, false);
                    }
                });
            }
        }

    }
    private final RecipeAwardHandler recipeAwardHandler = new RecipeAwardHandler();

    @Override
    public void setRecipeUsed(@Nullable Recipe<?> recipe) {
        recipeAwardHandler.record(recipe, Config.recipeMaxXPLevel.get());
    }
    private void recalcSideIOCap() {
        sidedHandlers.values().forEach(LazyOptional::invalidate);
        sidedHandlers.clear();
        for (Direction dir : Direction.values()) {
            FurnaceSettingsV2.IOMode mode = settingsV2.IOSetting().get(dir);
            IItemHandlerModifiable handler = mode.handlerSelector.apply(this);
            sidedHandlers.put(dir, LazyOptional.of(() -> handler));
        }
    }
    private LazyOptional<IEnergyStorage> energyCap = LazyOptional.of(() -> getFuel());
    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        sidedHandlers.values().forEach(LazyOptional::invalidate);
        energyCap.invalidate();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        recalcSideIOCap();
        energyCap = LazyOptional.of(() -> getFuel());
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (this.isRemoved()) {
            return LazyOptional.empty();
        }

        if (side != null && cap == ForgeCapabilities.ITEM_HANDLER) {
            return sidedHandlers.getOrDefault(side, LazyOptional.empty()).cast();
        }
        if (cap == ForgeCapabilities.ENERGY) {
            return energyCap.cast();
        }
        return super.getCapability(cap, side);
    }

    public record FurnaceProperties(String id, int tier, int generationPerTick) {
    }

}
