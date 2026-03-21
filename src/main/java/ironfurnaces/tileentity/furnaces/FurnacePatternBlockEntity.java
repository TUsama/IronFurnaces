package ironfurnaces.tileentity.furnaces;

import com.clefal.nirvana_lib.network.newtoolchain.S2CModPacket;
import com.clefal.nirvana_lib.utils.NetworkUtils;
import ironfurnaces.Config;
import ironfurnaces.adaptor.energy.FEnergyStorage;
import ironfurnaces.config.IronFurnacesConfig;
import ironfurnaces.network.S2CSyncInstancesToMenuPackets;
import ironfurnaces.network.S2CSyncPatternToMenuPackets;
import ironfurnaces.recipes.GeneratorRecipe;
import ironfurnaces.registration.ModBlockState;
import ironfurnaces.tileentity.furnaces.cache.*;
import ironfurnaces.tileentity.furnaces.data.ContainerDataBuilder;
import ironfurnaces.tileentity.furnaces.handler.IFurnaceLitHandler;
import ironfurnaces.tileentity.furnaces.handler.RecipeAwardHandler;
import ironfurnaces.tileentity.furnaces.menu.FurnacePatternMenu;
import ironfurnaces.tileentity.furnaces.pattern.EffectiveFurnaceStats;
import ironfurnaces.tileentity.furnaces.pattern.FurnacePattern;
import ironfurnaces.tileentity.furnaces.process.Burn;
import ironfurnaces.tileentity.furnaces.process.Generate;
import ironfurnaces.tileentity.furnaces.process.ProcessingInstanceManager;
import ironfurnaces.tileentity.furnaces.setting.FurnaceSettingsV2;
import it.unimi.dsi.fastutil.ints.IntSet;
import lombok.Getter;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
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

import java.util.*;
import java.util.function.Function;

@Getter
public class FurnacePatternBlockEntity extends BaseContainerBlockEntity implements RecipeHolder, StackedContentsCompatible, WorldlyContainer {
    private final InputCache input;
    private final OutputCache output;
    private final FuelCache fuel;
    private final RemainingCache remaining;
    private final AugmentCache augments;
    private final Function<RecipeType<?>, RecipeManager.CachedCheck<Container, ?>> quickCheck;
    private final IFCombinedCache allInv;
    private final IFCombinedCache allInvForAutomation;
    private final IFCombinedCache inputAndOutput;
    private final IFCombinedCache allOutput;
    private final RecipeAwardHandler recipeAwardHandler = new RecipeAwardHandler();
    private FurnacePattern pattern;
    private ProcessingInstanceManager instanceManager;
    private FurnaceMode mode;
    private IFurnaceLitHandler litHandler;
    private FurnaceSettingsV2 settingsV2 = FurnaceSettingsV2.DEFAULT;
    private EnumMap<Direction, LazyOptional<IItemHandlerModifiable>> sidedHandlers;
    private Set<UUID> viewers = new HashSet<>();
    private int lastProcessedDirection = 0;
    private LazyOptional<IEnergyStorage> energyCap = LazyOptional.of(() -> getFuel());
    private boolean shouldAutoFill = false;

    @Getter
    private final ContainerData dataAccess = Util.make(() -> {
        {
            FurnacePatternBlockEntity be = this;

            ContainerDataBuilder builder = ContainerDataBuilder.create()
                    .intValue(
                            () -> be.getFuel().getEnergyStored(),
                            v -> be.getFuel().energy().setEnergy(v)
                    )
                    .intValue(
                            () -> be.getFuel().getMaxEnergyStored(),
                            v -> be.getFuel().energy().setCapacity(v)
                    )
                    .enumValue(
                            FurnaceMode.class,
                            be::getMode,
                            v -> {
                                if (be.mode != v){
                                    be.updateFurnaceMode(v);
                                }

                            }
                    );

            for (Direction direction : Direction.values()) {
                builder.enumValue(
                        FurnaceSettingsV2.IOMode.class,
                        () -> be.getSettingsV2().IOSetting().get(direction),
                        value -> be.setWholeSettingV2(
                                be.getSettingsV2().withDirectionChanged(direction, value)
                ));
            }

            builder
                    .boolValue(
                            () -> be.getSettingsV2().autoInput(),
                            v -> be.setWholeSettingV2(be.getSettingsV2().withAutoInput(v))
                    )
                    .boolValue(
                            () -> be.getSettingsV2().autoOutput(),
                            v -> be.setWholeSettingV2(be.getSettingsV2().withAutoOutput(v))
                    )
                    .enumValue(
                            FurnaceSettingsV2.RedStoneMode.class,
                            () -> be.getSettingsV2().redStoneMode(),
                            v -> be.setWholeSettingV2(be.getSettingsV2().withRedStoneMode(v))
                    )
                    .intValue(
                            () -> be.getSettingsV2().subtractionNumber(),
                            v -> be.setWholeSettingV2(be.getSettingsV2().withSubtractionNumber(v))
                    )
                    .boolValue(
                            () -> be.getSettingsV2().autoFill(),
                            v -> be.setWholeSettingV2(be.getSettingsV2().withAutoFill(v))
                    )
                    .enumValue(AugmentCache.HandlingRecipeType.class,
                            () -> getAugments().getCurrentRecipeType(),
                            v -> getAugments().setCurrentRecipeType(v)
                            );

            return builder.build();
        }
    });

    private UUID ownerUuid;
    private RainbowRuntimeState rainbowState;

    public FurnacePatternBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        this.pattern = FurnacePattern.FALLBACK;
        this.mode = FurnaceMode.FURNACE;
        this.quickCheck = Util.memoize(x -> RecipeManager.createCheck((RecipeType) x));

        this.remaining = new RemainingCache();

        Function<ItemStack, Optional<? extends Recipe>> callback = Util.memoize(this::getRecipe);

        this.input = new InputCache(this.mode, pattern).grabRecipeCallback(callback).contentChangeCallback(x -> {
            this.instanceManager.invalidateRecipeCache(x);
            setChanged();
        });

        this.output = new OutputCache(this.mode, pattern)
                .contentChangeCallback(x -> {
                    this.instanceManager.refreshBlockingState(x);
                    setChanged();
                });

        this.fuel = new FuelCache(new FEnergyStorage(Config.furnaceEnergyCapacityTier2.get()).callback(fEnergyStorage -> setChanged()), mode).burnableFunction(x -> ForgeHooks.getBurnTime(x, getAugments().getCurrentRecipeType().recipeType) > 0).contentChangeCallback(x -> setChanged());

        this.instanceManager = new ProcessingInstanceManager(new ArrayList<>());

        this.litHandler = this.mode.litHandlerSelector.get();


        this.augments = new AugmentCache(x -> {
            this.updateFurnaceMode(x);
            setChanged();
            markForClientUpdate();
        }, () -> {
            if (level != null && !level.isClientSide) {
                BlockPos blockPos = this.getBlockPos();
                BlockState blockState1 = level.getBlockState(blockPos);
                level.setBlock(blockPos, blockState1.setValue(ModBlockState.HANDLING_RECIPE_TYPE, getAugments().getCurrentRecipeType()), 3);
            }
        });

        this.allInv = new IFCombinedCache(this.input, output, fuel, remaining, augments);
        this.allInvForAutomation = new IFCombinedCache(this.input, output, fuel, remaining);
        this.allOutput = new IFCombinedCache(output, remaining);
        this.inputAndOutput = new IFCombinedCache(this.input, this.allOutput);
        this.augments.refreshState();
        this.sidedHandlers = new EnumMap<>(Direction.class);
        recalcSideIOCap();
        markForClientUpdate();
    }
    private int getInputRedstoneSignal() {
        return level == null ? 0 : level.getBestNeighborSignal(worldPosition);
    }

    private boolean shouldWorkByRedstone() {
        FurnaceSettingsV2.RedStoneMode mode = settingsV2.redStoneMode();
        int signal = getInputRedstoneSignal();

        return switch (mode) {
            case IGNORE, COMPARATOR, COMPARATOR_SUBTRACTION -> true;
            case HIGH_SIGNAL -> signal >= 8;
            case LOW_SIGNAL -> signal > 0 && signal <= 7;
        };
    }


    private RainbowRuntimeState rainbowState() {
        if (rainbowState == null) {
            rainbowState = new RainbowRuntimeState(this);
        }
        return rainbowState;
    }

    public EffectiveFurnaceStats getEffectiveStats() {
        if (pattern != null && pattern.isRainbow()) {
            return rainbowState().getCachedStats();
        }
        return EffectiveFurnaceStats.fromBase(pattern == null ? FurnacePattern.FALLBACK : pattern);
    }


    public static void serverTick(Level level, BlockPos pos, BlockState state, FurnacePatternBlockEntity blockEntity) {

        if (level.getGameTime() % 20 == 0 && blockEntity.settingsV2.autoFill() && blockEntity.mode.equals(FurnaceMode.FACTORY)) {
            blockEntity.checkIfInputHasEmptySlot();
        }

        if (blockEntity.shouldAutoFill) {
            HandlerRebalanceUtil.rebalanceForProcessing(blockEntity.input);
            blockEntity.shouldAutoFill = false;
        }

        boolean allowWork = blockEntity.shouldWorkByRedstone();

        if (allowWork) {
            blockEntity.tryProcessInput();

            blockEntity.litHandler.tick(blockEntity);
            if (blockEntity.litHandler.isLit(blockEntity)) {
                blockEntity.getInstanceManager().manage(blockEntity);
            }
        }
        blockEntity.autoIO();
        blockEntity.energyOutPerTick();
        blockEntity.updateHandleTick();

        if (!level.isClientSide && blockEntity.pattern != null && blockEntity.pattern.isRainbow()) {
            blockEntity.rainbowState().tickServer();
        }

        blockEntity.syncProcessingInstancesManagerToViewers();


    }

    private void checkIfInputHasEmptySlot(){
        int slots = input.getSlots();
        int emptyTime = 0;
        for (int i = 0; i < slots; i++) {
            ItemStack stackInSlot = input.getStackInSlot(i);
            if (stackInSlot.isEmpty()) emptyTime++;
        }
        if (emptyTime != 0 && emptyTime != slots) shouldAutoFill = true;
    }

    public void addPlayer(ServerPlayer player) {
        this.viewers.add(player.getUUID());
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        // 1) inventories
        tag.put("Input", input.serializeNBT());
        tag.put("Output", output.serializeNBT());
        tag.put("Fuel", fuel.serializeNBT());
        tag.put("Remaining", remaining.serializeNBT());
        tag.put("Augments", augments.serializeNBT());

        if (ownerUuid != null) {
            tag.putUUID("OwnerUUID", ownerUuid);
        }

        if (pattern != null && pattern.isRainbow()) {
            rainbowState().saveToTag(tag);
        }

        // 2) instanceManager via Codec
        ProcessingInstanceManager.CODEC
                .encodeStart(NbtOps.INSTANCE, instanceManager)
                .result()
                .ifPresent(nbt -> tag.put("InstanceManager", nbt));

        // 3) litHandler via Codec
        IFurnaceLitHandler.CODEC
                .encodeStart(NbtOps.INSTANCE, litHandler)
                .result()
                .ifPresent(nbt -> tag.put("LitHandler", nbt));

        FurnaceSettingsV2.CODEC
                .encodeStart(NbtOps.INSTANCE, settingsV2)
                .result()
                .ifPresent(nbt -> tag.put(FurnaceSettingsV2.NBT_KEY, nbt));

        FurnacePattern.REF_CODEC
                .encodeStart(NbtOps.INSTANCE, pattern)
                .result()
                .ifPresent(nbt -> tag.put(FurnacePattern.NBT_KEY, nbt));

        recipeAwardHandler.saveToTag(tag);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.furnace");
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return null;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        // 1) inventories
        if (tag.contains("Input", Tag.TAG_COMPOUND)) {
            input.deserializeNBT(tag.getCompound("Input"));
        }
        if (tag.contains("Output", Tag.TAG_COMPOUND)) {
            output.deserializeNBT(tag.getCompound("Output"));
        }
        if (tag.contains("Fuel", Tag.TAG_COMPOUND)) {
            fuel.deserializeNBT(tag.getCompound("Fuel"));
        }
        if (tag.contains("Remaining", Tag.TAG_COMPOUND)) {
            remaining.deserializeNBT(tag.getCompound("Remaining"));
        }
        if (tag.contains("Augments", Tag.TAG_COMPOUND)) {
            augments.deserializeNBT(tag.getCompound("Augments"));
        }

        if (tag.hasUUID("OwnerUUID")) {
            this.ownerUuid = tag.getUUID("OwnerUUID");
        } else {
            this.ownerUuid = null;
        }

        if (this.pattern != null && this.pattern.isRainbow()) {
            rainbowState().loadFromTag(tag);
        }

        // 2) instanceManager
        if (tag.contains("InstanceManager")) {
            ProcessingInstanceManager.CODEC
                    .parse(NbtOps.INSTANCE, tag.get("InstanceManager"))
                    .result()
                    .ifPresent(parsed -> this.instanceManager = parsed);
        }

        // 3) litHandler
        if (tag.contains("LitHandler")) {
            IFurnaceLitHandler.CODEC
                    .parse(NbtOps.INSTANCE, tag.get("LitHandler"))
                    .result()
                    .ifPresent(parsed -> this.litHandler = parsed);
        }

        if (tag.contains(FurnaceSettingsV2.NBT_KEY)) {
            FurnaceSettingsV2.CODEC
                    .parse(NbtOps.INSTANCE, tag.get(FurnaceSettingsV2.NBT_KEY))
                    .result()
                    .ifPresent(parsed -> {
                        if (!this.settingsV2.equals(parsed)) {
                            this.settingsV2 = parsed;
                            recalcSideIOCap();
                        }
                    });
        }

        if (tag.contains(FurnacePattern.NBT_KEY)) {
            FurnacePattern.REF_CODEC
                    .parse(NbtOps.INSTANCE, tag.get(FurnacePattern.NBT_KEY))
                    .result()
                    .ifPresent(parsed -> {
                        if (!parsed.equals(pattern)) {
                            updatePattern(parsed);
                        }
                    });
        }

        recipeAwardHandler.loadFromTag(tag);
        augments.refreshState();
        recomputeFillStat();

    }

    public @Nullable UUID getOwnerUuid() {
        return ownerUuid;
    }

    public void setOwner(@Nullable UUID ownerUuid) {
        if (!Objects.equals(this.ownerUuid, ownerUuid)) {
            this.ownerUuid = ownerUuid;
            setChanged();
        }
    }

    public void ensureOwner(@Nullable Player player) {
        if (player != null && this.ownerUuid == null) {
            this.ownerUuid = player.getUUID();
            setChanged();
        }
    }

    public boolean isActiveForRainbowCount() {
        if (this.isRemoved()) return false;
        if (this.pattern == null) return false;
        if (this.pattern.isRainbow()) return false;
        if (this.level == null || this.level.isClientSide) return false;
        if (!this.level.isLoaded(this.worldPosition)) return false;
        if (!this.shouldWorkByRedstone()) return false;
        if (!this.litHandler.isLit(this)) return false;

        return switch (this.mode) {
            case FURNACE, FACTORY -> this.instanceManager.isWaiting();
            case GENERATOR -> this.instanceManager.isGeneratingEnergy();
        };
    }

    public void recomputeFillStat() {
        input.recomputeFillStats();
        output.recomputeFillStats();
        fuel.recomputeFillStats();
        remaining.recomputeFillStats();
    }

    public void updateFurnaceMode(FurnaceMode mode) {
        this.mode = mode;
        input.updateFurnaceMode(mode);
        input.dropStacksInUnavailableSlots(this.level, this.getBlockPos());
        output.updateFurnaceMode(mode);
        instanceManager.updateFurnaceMode(mode);
        fuel.updateFurnaceMode(mode);
        allInv.updateFurnaceMode(mode);
        allInvForAutomation.updateFurnaceMode(mode);
        allOutput.updateFurnaceMode(mode);
        inputAndOutput.updateFurnaceMode(mode);
        this.selectLitHandler();

    }

    private void syncProcessingInstancesManagerToViewers() {
        syncToViewer(new S2CSyncInstancesToMenuPackets(getInstanceManager().instances()));
    }

    private void syncPatternToViewers() {
        syncToViewer(new S2CSyncPatternToMenuPackets(pattern));
    }

    private void syncToViewer(S2CModPacket<?> packet){
        Iterator<UUID> iterator = viewers.iterator();
        while (iterator.hasNext()) {
            UUID next = iterator.next();
            Player playerByUUID = this.level.getPlayerByUUID(next);
            if (playerByUUID == null) iterator.remove();
            if (playerByUUID instanceof ServerPlayer serverPlayer) {
                if (serverPlayer.containerMenu instanceof FurnacePatternMenu) {
                    NetworkUtils.sendToClient(packet, serverPlayer);
                } else {
                    iterator.remove();
                }
            }
        }
    }


    @Override
    public @Nullable Recipe<?> getRecipeUsed() {
        return null;
    }

    @Override
    public void setRecipeUsed(@Nullable Recipe<?> recipe) {
        recipeAwardHandler.record(recipe, IronFurnacesConfig.config.stored_xp_level.get());
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
        FurnaceSettingsV2.IOMode ioMode = settingsV2.IOSetting().get(direction);
        return !ioMode.equals(FurnaceSettingsV2.IOMode.OUTPUT) && !ioMode.equals(FurnaceSettingsV2.IOMode.NONE) && ioMode.handlerSelector.apply(this).isItemValid(index, itemStack);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        FurnaceSettingsV2.IOMode ioMode = settingsV2.IOSetting().get(direction);
        return !ioMode.equals(FurnaceSettingsV2.IOMode.NONE) && (ioMode.equals(FurnaceSettingsV2.IOMode.OUTPUT) || ioMode.equals(FurnaceSettingsV2.IOMode.ALL));
    }

    private void selectLitHandler() {
        this.litHandler = this.mode.litHandlerSelector.get();
    }

    public Optional<? extends Recipe> getRecipe(ItemStack stack) {
        return quickCheck.apply(augments.getCurrentRecipeType().recipeType).getRecipeFor(new SimpleContainer(stack), level);
    }

    private void tryProcessInput() {
        IntSet workingIndexes = instanceManager.getWorkingIndexes();
        if (!mode.equals(FurnaceMode.GENERATOR)) {
            for (int i = 0; i < input.getSlots(); i++) {
                if (workingIndexes.contains(i)) continue;
                ItemStack stackInSlot = input.getStackInSlot(i);
                if (stackInSlot.isEmpty()) continue;
                int finalI = i;
                getRecipe(stackInSlot)
                        .ifPresent(x -> {
                            if (x instanceof AbstractCookingRecipe cookingRecipe) {
                                this.instanceManager.addInstance(Burn.create(cookingRecipe.getCookingTime(), finalI, x));
                            }

                        });
            }
        } else {

            for (int i = 0; i < fuel.getSlots(); i++) {
                if (workingIndexes.contains(i)) continue;
                ItemStack stackInSlot = fuel.getStackInSlot(i);
                if (stackInSlot.isEmpty()) continue;
                int finalI = i;
                int generation = pattern.energyGenerationPerTick();

                if (augments.getCurrentRecipeType().recipeType != RecipeType.BLASTING){
                    int burnTime = ForgeHooks.getBurnTime(stackInSlot, augments.getCurrentRecipeType().recipeType);
                    if (burnTime > 0){
                        this.instanceManager.addInstance(new Generate.SmeltGenerate(finalI, burnTime * generation, generation));
                    }
                } else {
                    getRecipe(stackInSlot)
                            .ifPresent(x -> {
                                if (this.mode.equals(FurnaceMode.GENERATOR)) {
                                    if (x instanceof GeneratorRecipe generatorRecipe) {
                                        this.instanceManager.addInstance(new Generate.BlastGenerate(finalI, generatorRecipe.getEnergy(), generation));
                                    }
                                }
                            });
                }

            }
        }

    }


    public void setWholeSettingV2(FurnaceSettingsV2 setting) {
        boolean b = setting.IOSetting().equals(this.settingsV2.IOSetting());
        this.settingsV2 = setting;
        if (!b) {
            recalcSideIOCap();
        }
        setChanged();
    }

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
                                if (settingsV2.autoOutput()) {
                                    IItemHandler outputHandler = switch (mode) {
                                        case OUTPUT, INPUT_AND_OUTPUT -> mode.handlerSelector.apply(this);
                                        case ALL -> FurnaceSettingsV2.IOMode.OUTPUT.handlerSelector.apply(this);
                                        default -> EmptyHandler.INSTANCE;
                                    };
                                    if (outputHandler != null) {
                                        transfer(outputHandler, x);
                                    }
                                }

                                // 输入阶段
                                if (settingsV2.autoInput()) {
                                    // 输入只拉 INPUT 或 FUEL
                                    IItemHandler inputHandler = switch (mode) {
                                        case INPUT, INPUT_AND_OUTPUT -> mode.handlerSelector.apply(this);
                                        case ALL -> FurnaceSettingsV2.IOMode.INPUT.handlerSelector.apply(this);
                                        default -> EmptyHandler.INSTANCE;
                                    };
                                    if (inputHandler != null) {
                                        transfer(x, inputHandler);
                                    }
                                }


                            });
                });

    }

    private void updateHandleTick() {
        lastProcessedDirection = (lastProcessedDirection + 1) % Direction.values().length;
    }


    private void transfer(IItemHandler from, IItemHandler to) {
        if (from == null || to == null) return;

        for (int i = 0; i < from.getSlots(); i++) {
            ItemStack simulatedExtract = from.extractItem(i, from.getSlotLimit(i), true);
            if (simulatedExtract.isEmpty()) continue;

            ItemStack remainder = ItemHandlerHelper.insertItem(to, simulatedExtract, false);
            int inserted = simulatedExtract.getCount() - remainder.getCount();

            if (inserted > 0) {
                from.extractItem(i, inserted, false);
            }
        }
    }



    protected void energyOutPerTick() {
        Direction[] dirs = Direction.values();
        Direction dir = dirs[lastProcessedDirection];

        Optional.of(dir)
                .ifPresent(direction -> {
                    FurnaceSettingsV2.IOMode mode = settingsV2.IOSetting().get(direction);
                    if (mode == null) return;

                    // 能量输出只允许“具备输出语义”的模式
                    if (mode != FurnaceSettingsV2.IOMode.OUTPUT
                            && mode != FurnaceSettingsV2.IOMode.ALL
                            && mode != FurnaceSettingsV2.IOMode.INPUT_AND_OUTPUT) {
                        return;
                    }

                    BlockEntity neighbor = level.getBlockEntity(worldPosition.relative(direction));
                    if (neighbor == null) return;

                    neighbor.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite())
                            .ifPresent(other -> {
                                if (!other.canReceive()) return;

                                int stored = fuel.getEnergyStored();
                                if (stored <= 0) return;

                                // 先模拟对方最多能收多少，再实际扣自己，避免无意义调用
                                int accepted = other.receiveEnergy(stored, true);
                                if (accepted <= 0) return;

                                int extracted = fuel.extractEnergy(accepted, false);
                                if (extracted > 0) {
                                    other.receiveEnergy(extracted, false);
                                }
                            });
                });
    }

    public int getSideRedstoneSignal(Direction side) {
        FurnaceSettingsV2.RedStoneMode m = settingsV2.redStoneMode();
        if (m != FurnaceSettingsV2.RedStoneMode.COMPARATOR && m != FurnaceSettingsV2.RedStoneMode.COMPARATOR_SUBTRACTION)
            return 0;

        FurnaceSettingsV2.IOMode io = settingsV2.IOSetting().get(side);
        if (io == null || io == FurnaceSettingsV2.IOMode.NONE) return 0;

        int signal = io.handlerSelector.apply(this) instanceof ICacheFillStats cacheFillStats ? cacheFillStats.getFillStats().toRedstoneStrength() : 0;

        if (m == FurnaceSettingsV2.RedStoneMode.COMPARATOR_SUBTRACTION) {
            signal = Math.max(signal - settingsV2.subtractionNumber(), 0);
        }
        return signal;
    }

    public int getComparatorLikeOutput() {
        FurnaceSettingsV2.RedStoneMode m = settingsV2.redStoneMode();
        if (m == FurnaceSettingsV2.RedStoneMode.IGNORE || m == FurnaceSettingsV2.RedStoneMode.HIGH_SIGNAL || m == FurnaceSettingsV2.RedStoneMode.LOW_SIGNAL) {
            return 0;
        }

        int signal = computeSignalFromExposedItemHandlers();

        if (m == FurnaceSettingsV2.RedStoneMode.COMPARATOR_SUBTRACTION) {
            signal = Math.max(signal - settingsV2.subtractionNumber(), 0);
        }
        return signal;
    }

    private int computeSignalFromExposedItemHandlers() {
        IdentityHashMap<IItemHandler, Boolean> uniq = new IdentityHashMap<>();

        int non_empty_slots = 0;
        float fill = 0.0f;
        int total_slots = 0;

        for (Direction dir : Direction.values()) {
            FurnaceSettingsV2.IOMode mode = settingsV2.IOSetting().get(dir);
            if (mode == null || mode == FurnaceSettingsV2.IOMode.NONE) continue;

            IItemHandler handler = mode.handlerSelector.apply(this);
            if (handler == EmptyHandler.INSTANCE) continue;
            if (handler == null) continue;
            if (uniq.put(handler, Boolean.TRUE) != null) continue; // 已统计过

            int slots = handler.getSlots();
            if (slots <= 0) continue;

            total_slots += slots;

            for (int i = 0; i < slots; i++) {
                ItemStack stack = handler.getStackInSlot(i);
                if (stack.isEmpty()) continue;

                non_empty_slots++;
                int limit = handler.getSlotLimit(i);
                int max_stack = Math.min(limit, stack.getMaxStackSize());
                if (max_stack > 0) {
                    fill += (float) stack.getCount() / (float) max_stack;
                }
            }
        }

        if (total_slots == 0) return 0;

        fill /= (float) total_slots;
        int signal = Mth.floor(fill * 14.0f) + (non_empty_slots > 0 ? 1 : 0);
        return Mth.clamp(signal, 0, 15);
    }

    public boolean insertAugmentFromHand(ServerPlayer player) {
        ItemStack held = player.getMainHandItem();
        if (held.isEmpty()) {
            return false;
        }

        int slot = -1;
        for (int i = 0; i < augments.getSlots(); i++) {
            if (augments.isItemValid(i, held)) {
                slot = i;
            }
        }

        if (slot != -1){
            ItemStack itemStack = augments.extractItem(slot, getMaxStackSize(), false);
            ItemStack remain = ItemHandlerHelper.insertItem(augments, held, false);
            player.setItemInHand(player.getUsedItemHand(), remain);
            ItemHandlerHelper.giveItemToPlayer(player, itemStack);
            level.playSound(
                    null,
                    this.getBlockPos(),
                    SoundEvents.EXPERIENCE_ORB_PICKUP,
                    SoundSource.BLOCKS,
                    0.05F,
                    1.0F
            );
            return true;
        } else {
            return false;
        }
    }


    public void updatePattern(FurnacePattern pattern) {
        this.pattern = pattern;
        this.input.updateFurnacePattern(pattern);
        this.output.updateFurnacePattern(pattern);
        this.instanceManager.updateFurnacePattern(pattern);
        this.fuel.updateFurnacePattern(pattern);
        this.litHandler.updateFurnacePattern(pattern);
        this.allOutput.updateFurnacePattern(pattern);
        this.allInv.updateFurnacePattern(pattern);
        this.allInvForAutomation.updateFurnacePattern(pattern);
        this.inputAndOutput.updateFurnacePattern(pattern);
        if (this.pattern != null && this.pattern.isRainbow()) {
            rainbowState().onPatternChanged();
        } else if (this.rainbowState != null) {
            this.rainbowState.onPatternChanged();
        }
        syncPatternToViewers();
        setChanged();
        markForClientUpdate();
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }


    public void markForClientUpdate() {
        setChanged();
        if (level != null && !level.isClientSide) {
            BlockState state = getBlockState();
            level.sendBlockUpdated(worldPosition, state, state, 3);
        }
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

    @Override
    public int getContainerSize() {
        return this.allInv.getSlots();
    }

    @Override
    public boolean isEmpty() {
        return this.allInv.getFillStats().fill_sum > 0.0F;
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.allInv.getStackInSlot(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return this.allInv.extractItem(slot, amount, false);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack stackInSlot = this.allInv.getStackInSlot(slot);
        this.allInv.setStackInSlot(slot, ItemStack.EMPTY);
        return stackInSlot;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.allInv.setStackInSlot(slot, stack);
    }

    @Override
    public boolean stillValid(Player player) {
        return false;
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < this.allInv.getSlots(); i++) {
            this.allInv.setStackInSlot(i, ItemStack.EMPTY);
        }
    }


}
