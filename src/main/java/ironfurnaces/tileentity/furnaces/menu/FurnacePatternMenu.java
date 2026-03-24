package ironfurnaces.tileentity.furnaces.menu;

import ironfurnaces.tileentity.furnaces.FurnaceMode;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.cache.RemainingCache;
import ironfurnaces.tileentity.furnaces.menu.slot.BooleanDataSlot;
import ironfurnaces.tileentity.furnaces.menu.slot.DynamicAccessSlot;
import ironfurnaces.tileentity.furnaces.menu.slot.IntDataSlot;
import ironfurnaces.tileentity.furnaces.menu.slot.PartitionAccessSlot;
import ironfurnaces.tileentity.furnaces.pattern.FurnacePattern;
import ironfurnaces.tileentity.furnaces.setting.FurnaceSettingsV2;
import it.unimi.dsi.fastutil.ints.Int2FloatLinkedOpenHashMap;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.util.List;
import java.util.function.Consumer;

public class FurnacePatternMenu extends DistributePartitionContainerMenu {
    public static final String ID = "furnace_pattern_menu";
    public final FurnacePatternBlockEntity blockEntity;
    public final Partition playerMainInv;
    public final Partition playerHotBarInv;
    public final Partition furnaceInput;
    public final Partition furnaceOutput;
    public final Partition factoryInput;
    public final Partition factoryOutput;
    public final Partition remaining;
    public final Partition fuel;
    public final Partition augment;
    public Int2FloatLinkedOpenHashMap instances = new Int2FloatLinkedOpenHashMap();
    public BlockPos bePos;
    @Getter
    public boolean openSetting = false;
    @Getter
    public boolean openAugment = false;
    @Getter
    public boolean openRemaining = false;
    int litTime;
    int litDuration;
    private ContainerData data;


    public FurnacePatternMenu(@Nullable MenuType<?> menuType, int containerId, FurnacePatternBlockEntity blockEntity, Inventory playerInventory, BlockPos pos, ContainerData data) {
        super(menuType, containerId);
        this.blockEntity = blockEntity;
        this.bePos = pos;
        this.data = data;
        addDataSlots(data);
        playerHotBarInv = this.addPartition(this.createPlayerHotbarPartition(playerInventory));
        playerMainInv = this.addPartition(this.createPlayerMainInventoryPartition(playerInventory));

        Consumer<DynamicAccessSlot> noPlace = x -> x.setMayPlaceCallback(() -> false);
        this.furnaceInput = this.addPartition(new GridPartition(1, new Vector2i(56, 17), () -> getMode().equals(FurnaceMode.FURNACE), blockEntity.getInput(), 0, 1));
        this.furnaceOutput = this.addPartition(new GridPartition(1, new Vector2i(116, 35), () -> getMode().equals(FurnaceMode.FURNACE), blockEntity.getOutput(), 0, 1)
                .setCreator((itemHandler, index, xPosition, yPosition, partition) -> {
                    DynamicAccessSlot dynamicAccessSlot = new DynamicAccessSlot(itemHandler, index, xPosition, yPosition, partition) {
                        @Override
                        public void onTake(Player player, ItemStack stack) {
                            super.onTake(player, stack);

                            if (!player.level().isClientSide
                                    && player instanceof ServerPlayer serverPlayer
                                    && blockEntity.getLevel() instanceof ServerLevel serverLevel) {
                                blockEntity.getRecipeAwardHandler()
                                        .unlockRecipes(serverPlayer);
                            }
                        }
                    };
                    noPlace.accept(dynamicAccessSlot);
                    return dynamicAccessSlot;
                }));
        this.factoryInput = addPartition(new GridPartition(blockEntity.usedStats.inputSlotAmount(), new Vector2i(36, 17), () -> getMode().equals(FurnaceMode.FACTORY), blockEntity.getInput(), 0, 3));

        this.factoryOutput = addPartition(new GridPartition(blockEntity.usedStats.inputSlotAmount(), new Vector2i(106, 17), () -> getMode().equals(FurnaceMode.FACTORY), blockEntity.getOutput(), 0, 3).setCreator((itemHandler, index, xPosition, yPosition, partition) -> {
            DynamicAccessSlot dynamicAccessSlot = new DynamicAccessSlot(itemHandler, index, xPosition, yPosition, partition) {
                @Override
                public void onTake(Player player, ItemStack stack) {
                    super.onTake(player, stack);

                    if (!player.level().isClientSide
                            && player instanceof ServerPlayer serverPlayer
                            && blockEntity.getLevel() instanceof ServerLevel serverLevel) {
                        blockEntity.getRecipeAwardHandler()
                                .unlockRecipes(serverPlayer);
                    }
                }
            };
            noPlace.accept(dynamicAccessSlot);
            return dynamicAccessSlot;
        }));

        this.fuel = addPartition(new MovableGridPartition(1, new Vector2i(56, 53), () -> getMode().equals(FurnaceMode.FURNACE) || getMode().equals(FurnaceMode.GENERATOR), blockEntity.getFuel(), 0, 1)
                .yMovementProvider(() -> {
                    int i = 53;
                    i += getMode().equals(FurnaceMode.GENERATOR) ? -13 : 0;
                    return i;

                })
                .menuSlots(this.slots)
        );


        this.augment = addPartition(new MovableGridPartition(3, new Vector2i(-50, 63), this::isOpenAugment, blockEntity.getAugments(), 0, 1) {
            @Override
            public Slot makeSlot(int localIndex) {
                int x = this.startPoint.x() + (localIndex % this.columns) * 18;
                //weird calc but it works for the texture
                int y = this.startPoint.y() + (localIndex / this.columns) * 30 + (localIndex / this.columns);
                int containerIndex = this.containerStartIndex + localIndex;
                Slot slot;
                if (creator != null) {
                    slot = creator.create(this.handler, containerIndex, x, y, this);
                } else {
                    slot = new PartitionAccessSlot(this.handler, containerIndex, x, y, this);
                }
                this.trackedSlot().add(slot);
                return slot;
            }
        }
                .yMovementProvider(() -> {
                    int i = 63;
                    i += isOpenSetting() ? 81 : 0;
                    i += isOpenRemaining() ? 81 : 0;
                    return i;
                }).menuSlots(this.slots));

        RemainingCache remaining = blockEntity.getRemaining();
        this.remaining = addPartition(new MovableGridPartition(remaining.getSlots(), new Vector2i(-55, 53), this::isOpenRemaining, remaining, 0, 3).yMovementProvider(() -> {
            int i = 53;
            //magic number 81 indicate that the y movement when panel open.
            i += isOpenSetting() ? 81 : 0;
            return i;

        }).menuSlots(this.slots).withDynamicAccessSlot(noPlace));


        PartitionGroup playerInv = PartitionGroup.of(playerMainInv, playerHotBarInv);
        PartitionGroup allInput = PartitionGroup.of(factoryInput, furnaceInput);
        PartitionGroup allOutput = PartitionGroup.of(factoryOutput, furnaceOutput);
        List<QuickMoveRule> build = QuickMoveRuleBuilder.create()
                .rule()
                .when(x -> blockEntity.getInput().isItemValid(0, x.stack()))
                .oneWay(playerInv, allInput)
                .rule()
                .oneWay(allInput, playerInv)
                .rule()
                .oneWay(allOutput, playerInv)
                .rule()
                .oneWay(this.remaining, playerInv)
                .rule()
                .oneWay(this.fuel, playerInv)
                .rule()
                .when(x -> ForgeHooks.getBurnTime(x.stack(), blockEntity.getAugments().getCurrentRecipeType().recipeType.get()) > 0 && this.fuel.isAvailable())
                .oneWay(playerInv, fuel)
                .rule()
                .bidirectional(playerInv, augment)
                .build();
        this.addQuickMoveRule(build);


        this.addDataSlot(new BooleanDataSlot(
                this::isOpenSetting,
                v -> {
                    if (this.openSetting != v) {
                        this.openSetting = v;
                        ((MovableGridPartition) this.remaining).handleSlot();
                        ((MovableGridPartition) this.augment).handleSlot();
                    }

                }
        ));

        this.addDataSlot(new BooleanDataSlot(
                this::isOpenAugment,
                v -> this.openAugment = v
        ));

        this.addDataSlot(new BooleanDataSlot(
                this::isOpenRemaining,
                v -> {
                    if (this.openRemaining != v) {
                        this.openRemaining = v;
                        ((MovableGridPartition) this.augment).handleSlot();
                    }
                }
        ));


        this.addDataSlot(new IntDataSlot(
                () -> blockEntity.getLitHandler().getLitTime(),
                v -> this.litTime = v
        ));
        this.addDataSlot(new IntDataSlot(
                () -> blockEntity.getLitHandler().getLitDuration(),
                v -> this.litDuration = v
        ));



    }

    public FurnaceMode getMode() {
        return this.blockEntity.getMode();
    }

    public FurnaceSettingsV2 getSettingsV2() {
        return this.blockEntity.getSettingsV2();
    }

    public int getEnergyStored() {
        return this.blockEntity.getFuel().getEnergyStored();
    }

    public int getMaxEnergy() {
        return this.blockEntity.getFuel().getMaxEnergyStored();
    }

    public boolean isLit() {
        return this.litTime > 0;
    }

    public FurnacePattern getPattern() {
        return this.blockEntity.getPattern();
    }

    public void updateMode() {
        ((MovableGridPartition) this.fuel).handleSlot();
    }

    public int getBurnProgress(int index) {
        if (index >= this.instances.size()) {
            return 0;
        }
        float progress = this.instances.get(index);
        return (int) (progress * 24);
    }


    public int getLitProgress() {
        int i = this.litDuration;
        if (i == 0) {
            i = 200;
        }

        return this.litTime * 13 / i;
    }


    @Override
    public boolean stillValid(Player player) {
        return true;
    }


}
