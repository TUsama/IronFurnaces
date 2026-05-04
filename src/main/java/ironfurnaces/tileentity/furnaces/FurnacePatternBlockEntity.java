//~ replace_block_entity
//~ replace_all_recipe
package ironfurnaces.tileentity.furnaces;

import com.clefal.nirvana_lib.network.newtoolchain.S2CModPacket;
import com.clefal.nirvana_lib.utils.NetworkUtils;
import ironfurnaces.adaptor.energy.FEnergyStorage;
import ironfurnaces.capability.VanillaCapabilityHandler;
import ironfurnaces.capability.rainbow.OwnerRainbowContextHelper;
import ironfurnaces.config.GameplayConfig;
import ironfurnaces.network.S2CSyncAugmentPacket;
import ironfurnaces.network.S2CSyncInstancesToMenuPackets;
import ironfurnaces.network.S2CSyncPatternAndStatsToMenuPackets;
import ironfurnaces.tileentity.furnaces.cache.*;
import ironfurnaces.tileentity.furnaces.data.ContainerDataBuilder;
import ironfurnaces.tileentity.furnaces.handler.IFurnaceLitHandler;
import ironfurnaces.tileentity.furnaces.handler.RecipeAwardHandler;
import ironfurnaces.tileentity.furnaces.menu.FurnacePatternMenu;
import ironfurnaces.tileentity.furnaces.pattern.*;
import ironfurnaces.tileentity.furnaces.pattern.mode.AbstractFurnaceModeHandler;
import ironfurnaces.tileentity.furnaces.pattern.mode.internal.VanillaFurnaceModeHandler;
import ironfurnaces.tileentity.furnaces.process.Generate;
import ironfurnaces.tileentity.furnaces.process.ProcessingInstanceManager;
import ironfurnaces.tileentity.furnaces.setting.FurnaceSettingsV2;
import it.unimi.dsi.fastutil.ints.IntSet;
import lombok.Getter;
import net.minecraft.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

//? 1.20.1 {
/*import net.minecraft.world.inventory.RecipeHolder;
import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.common.capabilities.ForgeCapabilities;
import net.neoforged.neoforge.common.util.LazyOptional;
import net.neoforged.neoforge.energy.IEnergyStorage;
*///? } else {
import net.minecraft.core.HolderLookup;
import net.minecraft.world.inventory.RecipeCraftingHolder;
import net.minecraft.core.NonNullList;
//?}
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.wrapper.EmptyItemHandler;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

@Getter
//~ if >1.20.1 'RecipeHolder' -> 'RecipeCraftingHolder'
public class FurnacePatternBlockEntity extends BaseContainerBlockEntity implements RecipeCraftingHolder, StackedContentsCompatible, WorldlyContainer, INeedUpdate {
    private final InputCache input;
    private final OutputCache output;
    private final FuelCache fuel;
    private final RemainingCache remaining;
    private final AugmentCache augments;
    private final ViewOnlyCache viewOnly;
    //~ if >1.20.1 'Container' -> 'SingleRecipeInput'
    private final Function<RecipeType<?>, RecipeManager.CachedCheck<SingleRecipeInput, ?>> quickCheck;
    private final IFCombinedCache allInv;
    private final IFCombinedCache allInvForAutomation;
    private final IFCombinedCache inputAndOutput;
    private final IFCombinedCache allOutput;
    private final RecipeAwardHandler recipeAwardHandler = new RecipeAwardHandler();
    @Getter
    private final ContainerData dataAccess;
    public IFurnaceStats<?> usedStats;
    public long usedRevision = 0L;
    private FurnacePattern pattern;
    private ProcessingInstanceManager instanceManager;
    private AbstractFurnaceModeHandler mode;
    private IFurnaceLitHandler litHandler;
    private FurnaceSettingsV2 settingsV2;
    //~ if >1.20.1 'LazyOptional<IItemHandlerModifiable>' -> 'IItemHandlerModifiable'
    private EnumMap<Direction, IItemHandlerModifiable> sidedHandlers;
    private Set<UUID> viewers = new HashSet<>();
    private int lastProcessedDirection = 0;
    //? if 1.20.1
    //private LazyOptional<IEnergyStorage> energyCap = LazyOptional.of(() -> getFuel());
    private boolean shouldAutoFill = false;
    private UUID ownerUuid;
    private boolean redstoneLastTimeCheck = false;
    private List<Consumer<Level>> levelRunnable = new ArrayList<>();
    private Tag savedPatternTag = null;

    public FurnacePatternBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        this.pattern = FurnacePattern.FALLBACK;
        this.usedStats = EffectiveFurnaceStats.fromBase(FurnacePattern.FALLBACK);
        this.mode = VanillaFurnaceModeHandler.INSTANCE;
        this.quickCheck = Util.memoize(x -> RecipeManager.createCheck((RecipeType) x));

        this.remaining = new RemainingCache();

        var callback = Util.memoize(this::allowPlaceItem);

        this.input = new InputCache(this.mode, usedStats).grabRecipeCallback(callback).contentChangeCallback(x -> {
            this.instanceManager.invalidateRecipeCache(x);
            setChanged();
        });

        this.output = new OutputCache(this.mode, usedStats)
                .contentChangeCallback(x -> {
                    this.instanceManager.refreshBlockingState(x);
                    setChanged();
                });

        this.viewOnly = new ViewOnlyCache();
        this.instanceManager = new ProcessingInstanceManager(new ArrayList<>());

        this.fuel = new FuelCache(mode, new FEnergyStorage(usedStats.energyCapacity()).callback(fEnergyStorage -> {
                    setChanged();
                    for (Generate allGenerateInstance : this.getInstanceManager().getAllGenerateInstances()) {
                        IntSet integers = getInstanceManager().blockingIndexes();
                        if (integers.contains(allGenerateInstance.fromIndex)) integers.remove(allGenerateInstance.fromIndex);
                    }
                }
        )).burnableFunction(x -> getAugments().getCurrentRecipeType().testBurnable(x, this))
                .contentChangeCallback(x -> setChanged());

        this.litHandler = this.mode.getLitHandler().get();

        //todo 这里我不去同步cache内的物品，是否会同步？打开menu的时候应该cache内的物品是自动同步的？
        this.augments = new AugmentCache((x, y) -> {
            updateFurnaceModeAndStats(x, usedStats);
            setChanged();
            markForClientUpdate();
        });

        this.allInv = new IFCombinedCache(this.input, output, fuel, remaining, augments, viewOnly);
        this.allInvForAutomation = new IFCombinedCache(this.input, output, fuel, remaining);
        this.allOutput = new IFCombinedCache(output, remaining);
        this.inputAndOutput = new IFCombinedCache(this.input, this.allOutput);
        this.augments.refreshState();

        this.sidedHandlers = new EnumMap<>(Direction.class);


        ContainerDataBuilder builder = ContainerDataBuilder.create()
                .intValue(
                        () -> this.getFuel().getEnergyStored(),
                        v -> this.getFuel().energy().setEnergy(v)
                )
                .intValue(
                        () -> this.getFuel().getMaxEnergyStored(),
                        v -> this.getFuel().energy().setCapacity(v)
                )
                /*
                .enumValue(
                        FurnaceMode.class,
                        this::getMode,
                        v -> {
                            if (this.mode != v) {
                                this.updateFurnaceMode(v);
                            }

                        }
                )*/;

        for (Direction direction : Direction.values()) {
            builder.enumValue(
                    FurnaceSettingsV2.IOMode.class,
                    () -> this.getSettingsV2().IOSetting().get(direction),
                    value -> this.setWholeSettingV2(
                            this.getSettingsV2().withDirectionChanged(direction, value)
                    ));
        }

        builder
                .boolValue(
                        () -> this.getSettingsV2().autoInput(),
                        v -> this.setWholeSettingV2(this.getSettingsV2().withAutoInput(v))
                )
                .boolValue(
                        () -> this.getSettingsV2().autoOutput(),
                        v -> this.setWholeSettingV2(this.getSettingsV2().withAutoOutput(v))
                )
                .enumValue(
                        FurnaceSettingsV2.RedStoneMode.class,
                        () -> this.getSettingsV2().redStoneMode(),
                        v -> this.setWholeSettingV2(this.getSettingsV2().withRedStoneMode(v))
                )
                .intValue(
                        () -> this.getSettingsV2().subtractionNumber(),
                        v -> this.setWholeSettingV2(this.getSettingsV2().withSubtractionNumber(v))
                )
                .boolValue(
                        () -> this.getSettingsV2().autoFill(),
                        v -> this.setWholeSettingV2(this.getSettingsV2().withAutoFill(v))
                )
                .intValue(
                        () -> this.getUsedStats().inputSlotAmount(),
                        v -> {
                            this.usedStats = this.usedStats.withInputSlotAmount(v);
                        }
                )
                .intValue(
                        () -> this.getUsedStats().energyCapacity(),
                        v -> this.usedStats = this.usedStats.withEnergyCapacity(v)
                )
                .intValue(
                        () -> this.getUsedStats().energyConsumerPerTick(),
                        v -> this.usedStats = this.usedStats.withEnergyConsumerPerTick(v)
                )
                .intValue(
                        () -> this.getUsedStats().energyGenerationPerTick(),
                        v -> this.usedStats = this.usedStats.withEnergyGenerationPerTick(v)
                )
                .intValue(
                        () -> this.getUsedStats().smeltTick(),
                        v -> this.usedStats = this.usedStats.withSmeltTick(v)
                )
        ;
        this.dataAccess = builder.build();
        this.setWholeSettingV2(FurnaceSettingsV2.DEFAULT);
        recalcSideIOCap();
        markForClientUpdate();
    }
    @Getter
    private boolean isWorking = false;

    public void setWorking(boolean working) {

        boolean oldState = isWorking;
        isWorking = working;
        if (oldState != working && this.getLevel() instanceof ServerLevel serverLevel){

        }

    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, FurnacePatternBlockEntity blockEntity) {
        if (!blockEntity.levelRunnable.isEmpty() && blockEntity.hasLevel()) {
            for (var consumer : blockEntity.levelRunnable) {
                consumer.accept(level);
            }
            blockEntity.levelRunnable.clear();
        }
        if (level.getGameTime() % 20 == 0 && blockEntity.settingsV2.autoFill() && blockEntity.mode.needAutoFill()) {
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
        blockEntity.syncProcessingInstancesManagerToViewers();

    }

    private int getInputRedstoneSignal() {
        return level == null ? 0 : level.getBestNeighborSignal(worldPosition);
    }

    private boolean shouldWorkByRedstone() {
        FurnaceSettingsV2.RedStoneMode mode = settingsV2.redStoneMode();
        int signal = getInputRedstoneSignal();
        var b = switch (mode) {
            case IGNORE, COMPARATOR, COMPARATOR_SUBTRACTION -> true;
            case HIGH_SIGNAL -> signal >= 8;
            case LOW_SIGNAL -> signal > 0 && signal <= 7;
        };
        if (b != redstoneLastTimeCheck) {
            redstoneLastTimeCheck = b;
            Level level = this.getLevel();
            if (level != null && this.ownerUuid != null) {
                Player playerByUUID = level.getPlayerByUUID(this.ownerUuid);
                if (playerByUUID instanceof ServerPlayer serverPlayer) {
                    OwnerRainbowContextHelper.markDirty(serverPlayer);
                }
            }
        }

        return b;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        recomputeFillStat();
    }

    private void checkIfInputHasEmptySlot() {
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
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {

        super.saveAdditional(tag, registries);

        // 1) inventories
        tag.put("Input", input.serializeNBT(registries));
        tag.put("Output", output.serializeNBT(registries));
        tag.put("ViewOnly", viewOnly.serializeNBT(registries));
        tag.put("Fuel", fuel.serializeNBT(registries));
        tag.put("Remaining", remaining.serializeNBT(registries));
        tag.put("Augments", augments.serializeNBT(registries));

        if (ownerUuid != null) {
            tag.putUUID("OwnerUUID", ownerUuid);
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

        if (!pattern.id().equals(FurnacePattern.FALLBACK.id())){
            FurnacePattern.REF_CODEC
                    .encodeStart(NbtOps.INSTANCE, pattern)
                    .result()
                    .ifPresent(nbt -> tag.put(FurnacePattern.NBT_KEY, nbt));
        } else {
            if (savedPatternTag != null){
                tag.put(FurnacePattern.NBT_KEY, savedPatternTag);
            }
        }


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
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains(FurnacePattern.NBT_KEY)) {
            savedPatternTag = tag.get(FurnacePattern.NBT_KEY);
        }
        // 1) inventories
        if (tag.contains("Input", Tag.TAG_COMPOUND)) {
            input.deserializeNBT(registries, tag.getCompound("Input"));
        }
        if (tag.contains("Output", Tag.TAG_COMPOUND)) {
            output.deserializeNBT(registries, tag.getCompound("Output"));
        }
        if (tag.contains("ViewOnly", Tag.TAG_COMPOUND)) {
            viewOnly.deserializeNBT(registries, tag.getCompound("ViewOnly"));
        }
        if (tag.contains("Fuel", Tag.TAG_COMPOUND)) {
            fuel.deserializeNBT(registries, tag.getCompound("Fuel"));
        }
        if (tag.contains("Remaining", Tag.TAG_COMPOUND)) {
            remaining.deserializeNBT(registries, tag.getCompound("Remaining"));
        }
        if (tag.contains("Augments", Tag.TAG_COMPOUND)) {
            augments.deserializeNBT(registries, tag.getCompound("Augments"));
        }

        if (tag.hasUUID("OwnerUUID")) {
            this.ownerUuid = tag.getUUID("OwnerUUID");
        } else {
            this.ownerUuid = null;
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
                            setWholeSettingV2(parsed);
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


    public void ensureOwner(@Nullable Player player) {
        if (player != null && this.ownerUuid == null) {
            this.ownerUuid = player.getUUID();
            setChanged();
        }
    }

    public boolean isActiveForRainbowCount() {
        if (this.isRemoved()) return false;
        if (this.pattern == null || this.pattern.equals(FurnacePattern.FALLBACK)) return false;
        if (this.pattern.isRainbow()) return false;
        if (this.level == null || this.level.isClientSide) return false;
        if (!this.shouldWorkByRedstone()) return false;
        boolean lit = this.litHandler.isLit(this);
        //System.out.println("current lithandler is " + litHandler + ", the lit result is " + lit);
        if (!lit) {
            return false;
        }

        return mode.isRainbowActive(this);

    }

    private void recomputeFillStat() {
        //do this in client will trigger outofindex in rainbow furnace
        if (getLevel() != null && getLevel().isClientSide) {
            return;
        }
        input.recomputeFillStats();
        output.recomputeFillStats();
        fuel.recomputeFillStats();
        remaining.recomputeFillStats();
    }
    @Nullable
    public Player getOwner(){
        if (hasLevel() && ownerUuid != null){
            return getLevel().getPlayerByUUID(ownerUuid);
        }
        return null;
    }

    public void transferStacksInUnavailableSlotsToPlayer(@Nullable Player player) {
        Set<ItemStack> values = new HashSet<>(output.findStacksInUnavailableSlots(this.getLevel()).values());
        values.addAll(input.findStacksInUnavailableSlots(this.getLevel()).values());
        returnOrDropStack(values, player);
    }


    public void returnOrDropStack(ItemStack stack, @Nullable Player player) {
        returnOrDropStack(List.of(stack), player);
    }

    public void returnOrDropStack(Collection<ItemStack> stacks, @Nullable Player player) {
        if ((stacks == null || stacks.isEmpty() || stacks.stream().allMatch(ItemStack::isEmpty)) || this.level == null) {
            return;
        }
        List<MergedStackEntry> mergedEntries = new ArrayList<>();

        for (ItemStack stack : stacks) {
            if (stack == null || stack.isEmpty()) {
                continue;
            }

            boolean merged = false;
            for (MergedStackEntry entry : mergedEntries) {
                if (ItemStack.isSameItemSameComponents(entry.sample(), stack)) {
                    entry.grow(stack.getCount());
                    merged = true;
                    break;
                }
            }

            if (!merged) {
                mergedEntries.add(new MergedStackEntry(stack.copy(), stack.getCount()));
            }
        }

        for (MergedStackEntry entry : mergedEntries) {
            int remaining = entry.totalCount();

            while (remaining > 0) {
                ItemStack split = entry.sample().copy();
                int giveCount = Math.min(split.getMaxStackSize(), remaining);
                split.setCount(giveCount);

                if (player != null) {
                    ItemHandlerHelper.giveItemToPlayer(player, split.copy());
                } else {
                    BlockPos pos = this.getBlockPos();
                    Containers.dropItemStack(this.level, pos.getX(), pos.getY(), pos.getZ(), split.copy());
                }

                remaining -= giveCount;
            }
        }

        for (ItemStack stack : stacks) {
            stack.setCount(0);
        }

        if (!stacks.isEmpty() && player != null) {
            player.sendSystemMessage(Component.translatable(
                    "container.ironfurnaces.item_transfer.transfer_reminder",
                    FurnacePattern.toDisplayName(this.pattern.id()),
                    this.getBlockPos().toShortString()
            ));
            for (MergedStackEntry entry : mergedEntries){
                player.sendSystemMessage(entry.sample.getDisplayName().copy().append(" x").append(entry.totalCount + ""));
            }
        }
    }

    private void syncProcessingInstancesManagerToViewers() {
        syncToViewer(new S2CSyncInstancesToMenuPackets(getInstanceManager().instances()));
    }

    private void syncPatternAndStatsToViewers() {
        syncToViewer(new S2CSyncPatternAndStatsToMenuPackets(pattern, mode.getId(), usedStats));
    }

    public void syncToViewer(S2CModPacket<?>... packet) {
        if (viewers.isEmpty()) return;
        Iterator<UUID> iterator = viewers.iterator();
        while (iterator.hasNext()) {
            UUID next = iterator.next();
            Player playerByUUID = this.level.getPlayerByUUID(next);
            if (playerByUUID == null) iterator.remove();
            if (playerByUUID instanceof ServerPlayer serverPlayer) {
                if (serverPlayer.containerMenu instanceof FurnacePatternMenu) {
                    for (S2CModPacket<?> s2CModPacket : packet) {
                        NetworkUtils.sendToClient(s2CModPacket, serverPlayer);
                    }

                } else {
                    iterator.remove();
                }
            }
        }
    }

    @Override
    public RecipeHolder<?> getRecipeUsed() {
        return null;
    }

    @Override
    public void setRecipeUsed(@Nullable RecipeHolder<?> recipe) {
        recipeAwardHandler.record(recipe, GameplayConfig.config.stored_xp_level.get());
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
        this.litHandler = this.mode.getLitHandler().get();
    }

    public Optional<? extends RecipeHolder> getRecipe(@NotNull ItemStack stack) {
        return this.getAugments().getCurrentRecipeType().getRecipe(this, List.of(stack));
    }

    private boolean allowPlaceItem(@NotNull ItemStack stack){
        return this.getAugments().getCurrentRecipeType().allowPlaceItem(this, List.of(stack));
    }

    private void tryProcessInput() {
        this.augments.getCurrentRecipeType().provideInstance(this);
    }


    public void setWholeSettingV2(FurnaceSettingsV2 setting) {
        boolean b = true;
        if (this.settingsV2 != null) {
            b = setting.IOSetting().equals(this.settingsV2.IOSetting());
        }

        this.settingsV2 = setting;
        if (!b) {
            recalcSideIOCap();
        }
        markForClientUpdate();
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

                    VanillaCapabilityHandler.withBlockItemHandler(neighbor, dir.getOpposite(), x -> {
                        if (settingsV2.autoOutput()) {
                            IItemHandler outputHandler = switch (mode) {
                                case OUTPUT, INPUT_AND_OUTPUT -> mode.handlerSelector.apply(this);
                                case ALL -> FurnaceSettingsV2.IOMode.OUTPUT.handlerSelector.apply(this);
                                default -> EmptyItemHandler.INSTANCE;
                            };
                            if (outputHandler != EmptyItemHandler.INSTANCE) {
                                transfer(outputHandler, x);
                            }
                        }

                        // 输入阶段
                        if (settingsV2.autoInput()) {
                            // 输入只拉 INPUT 或 FUEL
                            IItemHandler inputHandler = switch (mode) {
                                case INPUT, INPUT_AND_OUTPUT -> mode.handlerSelector.apply(this);
                                case ALL -> FurnaceSettingsV2.IOMode.INPUT.handlerSelector.apply(this);
                                default -> EmptyItemHandler.INSTANCE;
                            };
                            if (inputHandler != EmptyItemHandler.INSTANCE) {
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
        if (!settingsV2.autoOutput()) return;
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
                    VanillaCapabilityHandler.withBlockEnergyStorage(neighbor, direction.getOpposite(), other -> {
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
            //? if 1.20.1
            //if (handler == EmptyItemHandler.INSTANCE) continue;
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

        if (slot != -1) {
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

    public void addLevelConsumer(Consumer<Level> runnable){
        if (hasLevel()){
            runnable.accept(this.getLevel());
        } else {
            this.levelRunnable.add(runnable);
        }
    }


    @Override
    public void update(AbstractFurnaceModeHandler mode, IRecipeTypeHandler recipeTypeHandler, IFurnaceStats<?> stats, FurnacePatternBlockEntity blockEntity) {
        this.mode = mode;
        for (IItemHandlerModifiable iItemHandlerModifiable : allInv.getItemHandler()) {
            if (iItemHandlerModifiable instanceof INeedUpdate needUpdate){
                needUpdate.update(mode, recipeTypeHandler, stats, blockEntity);
            }
        }
        allInv.update(mode, recipeTypeHandler, stats, blockEntity);
        allInvForAutomation.update(mode, recipeTypeHandler, stats, blockEntity);
        allOutput.update(mode, recipeTypeHandler, stats, blockEntity);
        inputAndOutput.update(mode, recipeTypeHandler, stats, blockEntity);
        this.selectLitHandler();
        this.usedStats = stats;
        syncToAllViewers();


    }

    private void syncToAllViewers() {
        if (this.hasLevel()){
            if (!this.getLevel().isClientSide){
                syncPatternAndStatsToViewers();
                setChanged();
                markForClientUpdate();
            }
        }

    }

    public void closeMenu() {
        if (this.hasLevel()){
            if (this.getLevel() instanceof ServerLevel serverLevel){
                for (UUID viewer : viewers) {
                    Player playerByUUID = serverLevel.getPlayerByUUID(viewer);
                    if (playerByUUID != null && playerByUUID.containerMenu instanceof FurnacePatternMenu patternMenu) {
                        //todo 这里的关闭是否还需要？
                        //playerByUUID.closeContainer();
                    }
                }

            }
        }
    }


    public void updatePattern(FurnacePattern pattern) {
        this.pattern = pattern;
        if (pattern instanceof NormalFurnacePattern normalFurnacePattern){
            updateFurnaceStats(normalFurnacePattern.toEffectiveFurnaceStats());
        } else if (pattern instanceof RainbowFurnacePattern rainbow) {
            addLevelConsumer(level -> {
                IFurnaceStats<?> furnaceStats = OwnerRainbowContextHelper.getFurnaceStats(level, ownerUuid, rainbow);
                if (furnaceStats != null){
                    updateFurnaceStats(furnaceStats);
                }
            });
        }
    }

    public void updateRainbowStats(EffectiveFurnaceStats effectiveFurnaceStats, long revision) {
        if (this.usedRevision != revision) {
            updateFurnaceStats(effectiveFurnaceStats);
            this.usedRevision = revision;
        }
    }

    public void updateFurnaceStats(@Nonnull IFurnaceStats<?> stats) {
        update(mode, getAugments().getCurrentRecipeType(), stats, this);
    }

    public void updateFurnaceModeAndStats(@Nonnull AbstractFurnaceModeHandler mode, IFurnaceStats<?> stats) {
        update(mode, getAugments().getCurrentRecipeType(), stats, this);
    }

    @Override
            //~ if >1.20.1 '()' -> '(HolderLookup.Provider registries)'
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        //? 1.20.1 {
        /*CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        *///? } else {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries);
        //?}
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
        //? 1.20.1 {
        /*sidedHandlers.values().forEach(LazyOptional::invalidate);
        sidedHandlers.clear();
        for (Direction dir : Direction.values()) {
            FurnaceSettingsV2.IOMode mode = settingsV2.IOSetting().get(dir);
            IItemHandlerModifiable handler = mode.handlerSelector.apply(this);
            sidedHandlers.put(dir, LazyOptional.of(() -> handler));
        }
        *///?} else {
        addLevelConsumer(level1 -> {
            level1.invalidateCapabilities(this.getBlockPos());
            sidedHandlers.clear();
            for (Direction dir : Direction.values()) {
                FurnaceSettingsV2.IOMode mode = settingsV2.IOSetting().get(dir);
                IItemHandlerModifiable handler = mode.handlerSelector.apply(this);
                sidedHandlers.put(dir, handler);
            }
        });
        //?}
    }
    //? 1.20.1 {
    /*@Override
    public void invalidateCaps() {
        super.invalidateCaps();
        sidedHandlers.values().forEach(LazyOptional::invalidate);
        energyCap.invalidate();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        recalcSideIOCap();
        energyCap = LazyOptional.of(this::getFuel);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (this.isRemoved()) {
            return LazyOptional.empty();
        }
        if (pattern.equals(FurnacePattern.FALLBACK)) return LazyOptional.empty();

        if (side != null && cap == ForgeCapabilities.ITEM_HANDLER) {
            return sidedHandlers.getOrDefault(side, LazyOptional.empty()).cast();
        }
        if (cap == ForgeCapabilities.ENERGY) {
            return energyCap.cast();
        }
        return super.getCapability(cap, side);
    }
    *///?}
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

    //? >1.20.1 {
    @Override
    protected NonNullList<ItemStack> getItems() {
        NonNullList<ItemStack> itemStacks = NonNullList.withSize(this.allInv.getSlots(), ItemStack.EMPTY);
        for (int i = 0; i < this.allInv.getSlots(); i++) {
            itemStacks.set(i, allInv.getStackInSlot(i));
        }
        return itemStacks;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> nonNullList) {
        int slots = this.allInv.getSlots();
        for (int i = 0; i < nonNullList.size(); i++) {
            if (i >= slots) continue;
            this.allInv.setStackInSlot(i, nonNullList.get(i));
        }
    }
//?}
    private static class MergedStackEntry {
        private final ItemStack sample;
        private int totalCount;

        public MergedStackEntry(ItemStack sample, int totalCount) {
            this.sample = sample;
            this.totalCount = totalCount;
        }

        public ItemStack sample() {
            return sample;
        }

        public int totalCount() {
            return totalCount;
        }

        public void grow(int amount) {
            this.totalCount += amount;
        }
    }
}
