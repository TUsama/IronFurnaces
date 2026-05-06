//~ replace_block_entity
//~ replace_all_recipe
package ironfurnaces.tileentity.furnaces;

import com.clefal.nirvana_lib.network.newtoolchain.S2CModPacket;
import com.clefal.nirvana_lib.utils.NetworkUtils;
import ironfurnaces.adaptor.energy.FEnergyStorage;
import ironfurnaces.capability.PlayerDataHandler;
import ironfurnaces.capability.VanillaCapabilityHandler;
import ironfurnaces.capability.rainbow.OwnerRainbowContextHelper;
import ironfurnaces.config.GameplayConfig;
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
import lombok.Setter;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeCraftingHolder;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandlerUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.neoforge.transfer.item.PlayerInventoryWrapper;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

@Getter
public class FurnacePatternBlockEntity extends BaseContainerBlockEntity implements RecipeCraftingHolder, StackedContentsCompatible, WorldlyContainer, INeedUpdate {
    private static final String KEY_INPUT = "Input";
    private static final String KEY_OUTPUT = "Output";
    private static final String KEY_VIEW_ONLY = "ViewOnly";
    private static final String KEY_FUEL = "Fuel";
    private static final String KEY_REMAINING = "Remaining";
    private static final String KEY_AUGMENTS = "Augments";
    private static final String KEY_OWNER_UUID = "OwnerUUID";
    private static final String KEY_INSTANCE_MANAGER = "InstanceManager";
    private static final String KEY_LIT_HANDLER = "LitHandler";
    private final InputCache input;
    private final OutputCache output;
    private final FuelCache fuel;
    private final RemainingCache remaining;
    private final AugmentCache augments;
    private final ViewOnlyCache viewOnly;
    private final Function<RecipeType<?>, RecipeManager.CachedCheck<SingleRecipeInput, ?>> quickCheck;
    private final RecipeAwardHandler recipeAwardHandler = new RecipeAwardHandler();
    @Getter
    private final ContainerData dataAccess;
    public IFurnaceStats<?> usedStats;
    public long usedRevision = 0L;
    @Setter
    private IFCombinedCache allInv;
    @Setter
    private IFCombinedCache allInvForAutomation;
    @Setter
    private IFCombinedCache inputAndOutput;
    @Setter
    private IFCombinedCache allOutput;
    private FurnacePattern pattern;
    private ProcessingInstanceManager instanceManager;
    private AbstractFurnaceModeHandler mode;
    private IFurnaceLitHandler litHandler;
    private FurnaceSettingsV2 settingsV2;
    private EnumMap<Direction, ResourceHandler<ItemResource>> sidedHandlers;
    private Set<UUID> viewers = new HashSet<>();
    private int lastProcessedDirection = 0;
    private boolean shouldAutoFill = false;
    private UUID ownerUuid;
    private boolean redstoneLastTimeCheck = false;
    private List<Consumer<Level>> levelRunnable = new ArrayList<>();
    private Tag savedPatternTag = null;
    @Getter
    private boolean isWorking = false;

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
                        () -> this.getFuel().energy().getAmountAsInt(),
                        v -> this.getFuel().energy().setEnergy(v)
                )
                .intValue(
                        () -> this.getFuel().getCapacityAsInt(),
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

    private void updateHandleTick() {
        lastProcessedDirection = (lastProcessedDirection + 1) % Direction.values().length;
    }

    private static void saveHandler(
            ValueOutput output,
            String key,
            ValueIOSerializable handler
    ) {
        handler.serialize(output.child(key));
    }

    private static void loadHandler(
            ValueInput input,
            String key,
            ValueIOSerializable handler
    ) {
        handler.deserialize(input.childOrEmpty(key));
    }

    private static boolean canOutputThrough(@Nullable FurnaceSettingsV2.IOMode mode) {
        return mode == FurnaceSettingsV2.IOMode.OUTPUT
                || mode == FurnaceSettingsV2.IOMode.ALL
                || mode == FurnaceSettingsV2.IOMode.INPUT_AND_OUTPUT;
    }

    private static int getComparatorSlotCapacity(
            ResourceHandler<ItemResource> handler,
            int slot,
            ItemResource resource
    ) {
        if (resource.isEmpty()) {
            return 0;
        }

        int handlerCapacity = handler.getCapacityAsInt(slot, resource);
        int resourceMaxStackSize = resource.getMaxStackSize();

        if (handlerCapacity <= 0) {
            return resourceMaxStackSize;
        }

        return Math.min(handlerCapacity, resourceMaxStackSize);
    }

    public void setWorking(boolean working) {

        boolean oldState = isWorking;
        isWorking = working;
        if (oldState != working && this.getLevel() instanceof ServerLevel serverLevel) {

        }

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
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);

        saveInventories(output);
        saveOwner(output);
        saveRuntimeState(output);
        saveSettings(output);
        savePattern(output);
        recipeAwardHandler.save(output);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        loadInventories(input);
        loadOwner(input);
        loadRuntimeState(input);
        loadSettings(input);
        loadPattern(input);
        recipeAwardHandler.load(input);
        augments.refreshState();
        recomputeFillStat();
    }

    private void saveInventories(ValueOutput output) {
        saveHandler(output, KEY_INPUT, input);
        saveHandler(output, KEY_OUTPUT, this.output);
        saveHandler(output, KEY_VIEW_ONLY, viewOnly);
        saveHandler(output, KEY_FUEL, fuel);
        saveHandler(output, KEY_REMAINING, remaining);
        saveHandler(output, KEY_AUGMENTS, augments);
    }

    private void loadInventories(ValueInput input) {
        loadHandler(input, KEY_INPUT, this.input);
        loadHandler(input, KEY_OUTPUT, output);
        loadHandler(input, KEY_VIEW_ONLY, viewOnly);
        loadHandler(input, KEY_FUEL, fuel);
        loadHandler(input, KEY_REMAINING, remaining);
        loadHandler(input, KEY_AUGMENTS, augments);
    }

    private void saveOwner(ValueOutput output) {
        output.storeNullable(KEY_OWNER_UUID, UUIDUtil.CODEC, ownerUuid);
    }

    private void loadOwner(ValueInput input) {
        this.ownerUuid = input.read(KEY_OWNER_UUID, UUIDUtil.CODEC)
                .orElse(null);
    }

    private void saveRuntimeState(ValueOutput output) {
        output.store(KEY_INSTANCE_MANAGER, ProcessingInstanceManager.CODEC, instanceManager);
        output.store(KEY_LIT_HANDLER, IFurnaceLitHandler.CODEC, litHandler);
    }

    private void loadRuntimeState(ValueInput input) {
        input.read(KEY_INSTANCE_MANAGER, ProcessingInstanceManager.CODEC)
                .ifPresent(parsed -> this.instanceManager = parsed);

        input.read(KEY_LIT_HANDLER, IFurnaceLitHandler.CODEC)
                .ifPresent(parsed -> this.litHandler = parsed);
    }

    private void saveSettings(ValueOutput output) {
        output.store(FurnaceSettingsV2.NBT_KEY, FurnaceSettingsV2.CODEC, settingsV2);
    }

    private void loadSettings(ValueInput input) {
        input.read(FurnaceSettingsV2.NBT_KEY, FurnaceSettingsV2.CODEC)
                .ifPresent(parsed -> {
                    boolean ioChanged = !this.settingsV2.IOSetting().equals(parsed.IOSetting());

                    this.settingsV2 = parsed;

                    if (ioChanged) {
                        recalcSideIOCap();
                    }
                });
    }

    private void savePattern(ValueOutput output) {
        if (pattern != null && !pattern.id().equals(FurnacePattern.FALLBACK.id())) {
            output.store(FurnacePattern.NBT_KEY, FurnacePattern.REF_CODEC, pattern);
            return;
        }

    }

    private void loadPattern(ValueInput input) {
        input.read(FurnacePattern.NBT_KEY, Identifier.CODEC)
                .ifPresentOrElse(
                        this::loadPatternById,
                        this::loadFallbackPattern
                );
    }

    private void loadPatternById(Identifier patternId) {
        FurnacePattern parsed = FurnacePatternManager.getOrFallback(patternId);

        boolean unresolved =
                parsed.id().equals(FurnacePattern.FALLBACK.id())
                        && !patternId.equals(FurnacePattern.FALLBACK.id());


        if (!parsed.equals(this.pattern)) {
            updatePattern(parsed);
        }
    }

    private void loadFallbackPattern() {
        if (this.pattern != null && !this.pattern.equals(FurnacePattern.FALLBACK)) {
            updatePattern(FurnacePattern.FALLBACK);
        }
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.furnace");
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return null;
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
        if (this.level == null || this.level.isClientSide()) return false;
        if (!this.shouldWorkByRedstone()) return false;
        boolean lit = this.litHandler.isLit(this);
        if (!lit) {
            return false;
        }

        return mode.isRainbowActive(this);

    }

    private void recomputeFillStat() {
        //do this in client will trigger outofindex in rainbow furnace
        if (getLevel() != null && getLevel().isClientSide()) {
            return;
        }
        input.recomputeFillStats();
        output.recomputeFillStats();
        fuel.recomputeFillStats();
        remaining.recomputeFillStats();
    }

    @Nullable
    public Player getOwner() {
        if (hasLevel() && ownerUuid != null) {
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
        if (this.level == null || stacks == null || stacks.isEmpty()) {
            return;
        }

        Map<ItemResource, Integer> merged = mergeStacks(stacks);
        if (merged.isEmpty()) {
            return;
        }

        BlockPos pos = this.getBlockPos();

        for (Map.Entry<ItemResource, Integer> entry : merged.entrySet()) {
            ItemResource resource = entry.getKey();
            int remaining = entry.getValue();

            if (resource.isEmpty() || remaining <= 0) {
                continue;
            }

            int maxStackSize = resource.getMaxStackSize();

            while (remaining > 0) {
                int count = Math.min(maxStackSize, remaining);
                ItemStack stack = resource.toStack(count);

                if (player != null) {
                    giveOrDropToPlayer(player, stack, pos);
                } else {
                    dropStack(stack, pos);
                }

                remaining -= count;
            }
        }

        for (ItemStack stack : stacks) {
            if (stack != null) {
                stack.setCount(0);
            }
        }

        if (player != null) {
            sendReturnReminder(player, merged);
        }
    }

    private static Map<ItemResource, Integer> mergeStacks(Collection<ItemStack> stacks) {
        Map<ItemResource, Integer> merged = new LinkedHashMap<>();

        for (ItemStack stack : stacks) {
            if (stack == null || stack.isEmpty()) {
                continue;
            }

            ItemResource resource = ItemResource.of(stack);
            if (resource.isEmpty()) {
                continue;
            }

            merged.merge(resource, stack.getCount(), Integer::sum);
        }

        return merged;
    }

    private void giveOrDropToPlayer(Player player, ItemStack stack, BlockPos pos) {
        if (stack.isEmpty()) {
            return;
        }

        ItemStack remainder;

        try (Transaction tx = Transaction.openRoot()) {
            remainder = ItemUtil.insertItemReturnRemaining(
                    PlayerInventoryWrapper.of(player),
                    stack,
                    false,
                    tx
            );

            tx.commit();
        }

        if (!remainder.isEmpty()) {
            dropStack(remainder, pos);
        }
    }

    private void dropStack(ItemStack stack, BlockPos pos) {
        if (stack.isEmpty() || this.level == null) {
            return;
        }

        Containers.dropItemStack(
                this.level,
                pos.getX() + 0.5D,
                pos.getY() + 0.5D,
                pos.getZ() + 0.5D,
                stack
        );
    }
    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        Level level = this.getLevel();

        if (level == null) {
            return;
        }

        if (!level.isClientSide()) {
            UUID ownerUuid = this.getOwnerUuid();
            if (ownerUuid != null && level.getServer() != null) {
                Player owner = level.getServer().getPlayerList().getPlayer(ownerUuid);
                if (owner != null) {
                    PlayerDataHandler.editFurnacesList(owner, x -> x.remove(level.dimension(), pos));
                }
            }
        }

        for (int i = 0; i < this.getViewOnly().size(); i++) {
            this.getViewOnly().setAsEmpty(i);
        }

        super.preRemoveSideEffects(pos, state);

        if (level instanceof ServerLevel serverLevel) {
            this.getRecipeAwardHandler().grantStoredRecipeExperience(
                    serverLevel,
                    Vec3.atLowerCornerOf(pos)
            );
            OwnerRainbowContextHelper.markDirtyByOwnerUuid(serverLevel, this.getOwnerUuid());
        }
    }

    private void sendReturnReminder(Player player, Map<ItemResource, Integer> merged) {
        player.sendSystemMessage(Component.translatable(
                "container.ironfurnaces.item_transfer.transfer_reminder",
                FurnacePattern.toDisplayName(this.pattern.id()),
                this.getBlockPos().toShortString()
        ));

        for (Map.Entry<ItemResource, Integer> entry : merged.entrySet()) {
            ItemResource resource = entry.getKey();
            int count = entry.getValue();

            if (resource.isEmpty() || count <= 0) {
                continue;
            }

            ItemStack displayStack = resource.toStack(1);

            player.sendSystemMessage(
                    displayStack.getDisplayName()
                            .copy()
                            .append(" x")
                            .append(Integer.toString(count))
            );
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
    public void fillStackedContents(StackedItemContents stackedItemContents) {
        for (int i = 0; i < input.getSlots(); i++) {
            stackedItemContents.accountStack(input.getStackInSlot(i));
        }

    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return ((ICacheIndex) settingsV2.IOSetting().get(side).handlerSelector.apply(this)).getCacheIndex();
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack itemStack, @Nullable Direction direction) {
        FurnaceSettingsV2.IOMode ioMode = settingsV2.IOSetting().get(direction);
        return !ioMode.equals(FurnaceSettingsV2.IOMode.OUTPUT) && !ioMode.equals(FurnaceSettingsV2.IOMode.NONE) && ioMode.handlerSelector.apply(this).isValid(index, ItemResource.of(itemStack));
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

    private boolean allowPlaceItem(@NotNull ItemStack stack) {
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
        if (level == null || level.isClientSide()) {
            return;
        }

        if (!settingsV2.autoInput() && !settingsV2.autoOutput()) {
            return;
        }

        Direction direction = Direction.values()[lastProcessedDirection];

        FurnaceSettingsV2.IOMode mode = settingsV2.IOSetting().get(direction);
        if (mode == null || mode == FurnaceSettingsV2.IOMode.NONE) {
            return;
        }

        BlockEntity neighbor = level.getBlockEntity(worldPosition.relative(direction));
        if (neighbor == null) {
            return;
        }

        VanillaCapabilityHandler.withBlockItemHandler(
                neighbor,
                direction.getOpposite(),
                neighborHandler -> {
                    if (settingsV2.autoOutput()) {
                        ResourceHandler<ItemResource> outputHandler =
                                getAutoOutputHandler(mode);

                        transferItems(outputHandler, neighborHandler);
                    }

                    if (settingsV2.autoInput()) {
                        ResourceHandler<ItemResource> inputHandler =
                                getAutoInputHandler(mode);

                        transferItems(neighborHandler, inputHandler);
                    }
                }
        );
    }

    private int transferItems(
            @Nullable ResourceHandler<ItemResource> from,
            @Nullable ResourceHandler<ItemResource> to
    ) {
        if (from == null || to == null) {
            return 0;
        }

        try (Transaction tx = Transaction.openRoot()) {
            int moved = ResourceHandlerUtil.moveStacking(
                    from,
                    to,
                    resource -> true,
                    Integer.MAX_VALUE,
                    tx
            );

            if (moved > 0) {
                tx.commit();
                setChanged();
            }

            return moved;
        }
    }

    protected void energyOutPerTick() {
        if (level == null || level.isClientSide()) {
            return;
        }

        if (!settingsV2.autoOutput()) {
            return;
        }

        Direction direction = Direction.values()[lastProcessedDirection];

        FurnaceSettingsV2.IOMode mode = settingsV2.IOSetting().get(direction);
        if (!canOutputThrough(mode)) {
            return;
        }

        BlockEntity neighbor = level.getBlockEntity(worldPosition.relative(direction));
        if (neighbor == null) {
            return;
        }

        VanillaCapabilityHandler.withBlockEnergyStorage(
                neighbor,
                direction.getOpposite(),
                other -> transferEnergy(fuel, other)
        );
    }

    private int transferEnergy(
            @Nullable EnergyHandler from,
            @Nullable EnergyHandler to
    ) {
        if (from == null || to == null) {
            return 0;
        }

        int stored = from.getAmountAsInt();
        if (stored <= 0) {
            return 0;
        }

        try (Transaction tx = Transaction.openRoot()) {
            int moved = EnergyHandlerUtil.move(from, to, stored, tx);

            if (moved > 0) {
                tx.commit();
                setChanged();
            }

            return moved;
        }
    }

    @Nullable
    private ResourceHandler<ItemResource> getAutoOutputHandler(FurnaceSettingsV2.IOMode mode) {
        return switch (mode) {
            case OUTPUT, INPUT_AND_OUTPUT -> mode.handlerSelector.apply(this);
            case ALL -> FurnaceSettingsV2.IOMode.OUTPUT.handlerSelector.apply(this);
            default -> null;
        };
    }

    @Nullable
    private ResourceHandler<ItemResource> getAutoInputHandler(FurnaceSettingsV2.IOMode mode) {
        return switch (mode) {
            case INPUT, INPUT_AND_OUTPUT, FUEL -> mode.handlerSelector.apply(this);
            case ALL -> FurnaceSettingsV2.IOMode.INPUT.handlerSelector.apply(this);
            default -> null;
        };
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
        Set<ResourceHandler<ItemResource>> visited =
                Collections.newSetFromMap(new IdentityHashMap<>());

        int nonEmptySlots = 0;
        float fill = 0.0f;
        int totalSlots = 0;

        for (Direction dir : Direction.values()) {
            FurnaceSettingsV2.IOMode mode = settingsV2.IOSetting().get(dir);
            if (mode == null || mode == FurnaceSettingsV2.IOMode.NONE) {
                continue;
            }

            var handler = mode.handlerSelector.apply(this);
            if (handler == null) {
                continue;
            }

            // 同一个 handler 可能被多个面暴露，避免重复统计。
            if (!visited.add(handler)) {
                continue;
            }

            int slots = handler.size();
            if (slots <= 0) {
                continue;
            }

            totalSlots += slots;

            for (int slot = 0; slot < slots; slot++) {
                ItemResource resource = handler.getResource(slot);
                int amount = handler.getAmountAsInt(slot);

                if (resource.isEmpty() || amount <= 0) {
                    continue;
                }

                nonEmptySlots++;

                int maxStack = getComparatorSlotCapacity(handler, slot, resource);
                if (maxStack > 0) {
                    fill += (float) amount / (float) maxStack;
                }
            }
        }

        if (totalSlots == 0) {
            return 0;
        }

        fill /= (float) totalSlots;

        int signal = Mth.floor(fill * 14.0f) + (nonEmptySlots > 0 ? 1 : 0);
        return Mth.clamp(signal, 0, 15);
    }

    public boolean insertAugmentFromHand(ServerPlayer player) {
        ItemStack held = player.getMainHandItem();
        if (held.isEmpty()) {
            return false;
        }

        ItemResource heldResource = ItemResource.of(held);
        if (heldResource.isEmpty()) {
            return false;
        }

        int slot = findAugmentSlotFor(heldResource);
        if (slot < 0) {
            return false;
        }

        int insertAmount = Math.min(
                held.getCount(),
                getAugmentSlotCapacity(slot, heldResource)
        );

        if (insertAmount <= 0) {
            return false;
        }

        ItemResource oldResource = augments.getResource(slot);
        int oldAmount = augments.getAmountAsInt(slot);

        ItemStack oldStack = oldResource.isEmpty() || oldAmount <= 0
                ? ItemStack.EMPTY
                : oldResource.toStack(oldAmount);

        try (Transaction tx = Transaction.openRoot()) {
            if (!oldResource.isEmpty() && oldAmount > 0) {
                int extracted = augments.extract(slot, oldResource, oldAmount, tx);
                if (extracted != oldAmount) {
                    return false;
                }
            }

            int inserted = augments.insert(slot, heldResource, insertAmount, tx);
            if (inserted != insertAmount) {
                return false;
            }

            tx.commit();
        }

        ItemStack remainder = held.copy();
        remainder.shrink(insertAmount);

        player.setItemInHand(InteractionHand.MAIN_HAND, remainder);

        if (!oldStack.isEmpty()) {
            player.getInventory().placeItemBackInInventory(oldStack);
        }

        setChanged();

        if (level != null) {
            level.playSound(
                    null,
                    this.getBlockPos(),
                    SoundEvents.EXPERIENCE_ORB_PICKUP,
                    SoundSource.BLOCKS,
                    0.05F,
                    1.0F
            );
        }

        return true;
    }

    private int findAugmentSlotFor(ItemResource resource) {
        for (int slot = 0; slot < augments.size(); slot++) {
            if (getAugmentSlotCapacity(slot, resource) > 0) {
                return slot;
            }
        }

        return -1;
    }

    private int getAugmentSlotCapacity(int slot, ItemResource resource) {
        if (resource.isEmpty()) {
            return 0;
        }

        if (!augments.isValid(slot, resource)) {
            return 0;
        }

        return Math.min(
                augments.getCapacityAsInt(slot, resource),
                resource.getMaxStackSize()
        );
    }

    public void addLevelConsumer(Consumer<Level> runnable) {
        if (hasLevel()) {
            runnable.accept(this.getLevel());
        } else {
            this.levelRunnable.add(runnable);
        }
    }


    @Override
    public void update(AbstractFurnaceModeHandler mode, IRecipeTypeHandler recipeTypeHandler, IFurnaceStats<?> stats, FurnacePatternBlockEntity blockEntity) {
        this.mode = mode;
        for (var iItemHandlerModifiable : allInv.getHandlers()) {
            if (iItemHandlerModifiable instanceof INeedUpdate needUpdate) {
                needUpdate.update(mode, recipeTypeHandler, stats, blockEntity);
            }
        }
        allInv.update(mode, recipeTypeHandler, stats, blockEntity);
        allInvForAutomation.update(mode, recipeTypeHandler, stats, blockEntity);
        allOutput.update(mode, recipeTypeHandler, stats, blockEntity);
        inputAndOutput.update(mode, recipeTypeHandler, stats, blockEntity);

        allInv.tryRecreate(x -> this.allInv = x);
        allInvForAutomation.tryRecreate(x -> this.allInvForAutomation = x);
        allOutput.tryRecreate(x -> this.allOutput = x);
        inputAndOutput.tryRecreate(x -> this.inputAndOutput = x);

        this.selectLitHandler();
        this.usedStats = stats;
        syncToAllViewers();
    }

    private void syncToAllViewers() {
        if (this.hasLevel()) {
            if (!this.getLevel().isClientSide()) {
                syncPatternAndStatsToViewers();
                setChanged();
                markForClientUpdate();
            }
        }

    }

    public void closeMenu() {
        if (this.hasLevel()) {
            if (this.getLevel() instanceof ServerLevel serverLevel) {
                for (UUID viewer : viewers) {
                    Player playerByUUID = serverLevel.getPlayerByUUID(viewer);
                    if (playerByUUID != null && playerByUUID.containerMenu instanceof FurnacePatternMenu patternMenu) {
                        playerByUUID.closeContainer();
                    }
                }

            }
        }
    }


    public void updatePattern(FurnacePattern pattern) {
        this.pattern = pattern;
        if (pattern instanceof NormalFurnacePattern normalFurnacePattern) {
            updateFurnaceStats(normalFurnacePattern.toEffectiveFurnaceStats());
        } else if (pattern instanceof RainbowFurnacePattern rainbow) {
            addLevelConsumer(level -> {
                IFurnaceStats<?> furnaceStats = OwnerRainbowContextHelper.getFurnaceStats(level, ownerUuid, rainbow);
                if (furnaceStats != null) {
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

    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }


    public void markForClientUpdate() {
        setChanged();
        if (level != null && !level.isClientSide()) {
            BlockState state = getBlockState();
            level.sendBlockUpdated(worldPosition, state, state, 3);
        }
    }

    private void recalcSideIOCap() {
        addLevelConsumer(level1 -> {
            level1.invalidateCapabilities(this.getBlockPos());
            sidedHandlers.clear();
            for (Direction dir : Direction.values()) {
                FurnaceSettingsV2.IOMode mode = settingsV2.IOSetting().get(dir);
                var handler = mode.handlerSelector.apply(this);
                sidedHandlers.put(dir, handler);
            }
        });
    }

    @Override
    public int getContainerSize() {
        return this.allInv.size();
    }

    @Override
    public boolean isEmpty() {
        return this.allInv.getFillStats().fill_sum > 0.0F;
    }

    @Override
    public ItemStack getItem(int slot) {
        return ItemUtil.getStack(this.allInv, slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        if (amount <= 0) {
            return ItemStack.EMPTY;
        }

        ItemStack removed = extractStackFromSlot(slot, amount, true);
        if (!removed.isEmpty()) {
            setChanged();
        }

        return removed;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemResource resource = allInv.getResource(slot);
        int amount = allInv.getAmountAsInt(slot);

        if (resource.isEmpty() || amount <= 0) {
            return ItemStack.EMPTY;
        }

        return extractStackFromSlot(slot, amount, false);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        replaceSlotWithStack(slot, stack);
        setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        if (level == null) {
            return false;
        }

        if (level.getBlockEntity(worldPosition) != this) {
            return false;
        }

        return player.distanceToSqr(
                worldPosition.getX() + 0.5D,
                worldPosition.getY() + 0.5D,
                worldPosition.getZ() + 0.5D
        ) <= 64.0D;
    }

    @Override
    public void clearContent() {
        boolean changed = false;

        for (int slot = 0; slot < allInv.size(); slot++) {
            ItemResource resource = allInv.getResource(slot);
            int amount = allInv.getAmountAsInt(slot);

            if (resource.isEmpty() || amount <= 0) {
                continue;
            }

            ItemStack removed = extractStackFromSlot(slot, amount, false);
            if (!removed.isEmpty()) {
                changed = true;
            }
        }

        if (changed) {
            setChanged();
        }
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        NonNullList<ItemStack> items =
                NonNullList.withSize(allInv.size(), ItemStack.EMPTY);

        for (int slot = 0; slot < allInv.size(); slot++) {
            items.set(slot, getStackFromSlot(slot));
        }

        return items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        int slots = allInv.size();

        for (int slot = 0; slot < slots; slot++) {
            ItemStack stack = slot < items.size()
                    ? items.get(slot)
                    : ItemStack.EMPTY;

            replaceSlotWithStack(slot, stack);
        }

        setChanged();
    }

    private ItemStack getStackFromSlot(int slot) {
        ItemResource resource = allInv.getResource(slot);
        int amount = allInv.getAmountAsInt(slot);

        if (resource.isEmpty() || amount <= 0) {
            return ItemStack.EMPTY;
        }

        return resource.toStack(amount);
    }

    private ItemStack extractStackFromSlot(int slot, int amount, boolean limitByRequestedAmount) {
        ItemResource resource = allInv.getResource(slot);
        int stored = allInv.getAmountAsInt(slot);

        if (resource.isEmpty() || stored <= 0 || amount <= 0) {
            return ItemStack.EMPTY;
        }

        int toExtract = limitByRequestedAmount
                ? Math.min(amount, stored)
                : stored;

        try (Transaction tx = Transaction.openRoot()) {
            int extracted = allInv.extract(slot, resource, toExtract, tx);

            if (extracted <= 0) {
                return ItemStack.EMPTY;
            }

            tx.commit();
            return resource.toStack(extracted);
        }
    }

    private void replaceSlotWithStack(int slot, ItemStack stack) {
        try (Transaction tx = Transaction.openRoot()) {
            ItemResource oldResource = allInv.getResource(slot);
            int oldAmount = allInv.getAmountAsInt(slot);

            if (!oldResource.isEmpty() && oldAmount > 0) {
                int extracted = allInv.extract(slot, oldResource, oldAmount, tx);

                if (extracted != oldAmount) {
                    return;
                }
            }

            if (!stack.isEmpty()) {
                ItemResource newResource = ItemResource.of(stack);
                int amount = Math.min(
                        stack.getCount(),
                        getSlotCapacity(slot, newResource)
                );

                if (amount > 0) {
                    int inserted = allInv.insert(slot, newResource, amount, tx);

                    if (inserted != amount) {
                        return;
                    }
                }
            }

            tx.commit();
        }
    }

    private int getSlotCapacity(int slot, ItemResource resource) {
        if (resource.isEmpty()) {
            return 0;
        }

        if (!allInv.isValid(slot, resource)) {
            return 0;
        }

        return Math.min(
                allInv.getCapacityAsInt(slot, resource),
                resource.getMaxStackSize()
        );
    }

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
