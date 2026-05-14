package ironfurnaces.container;

import ironfurnaces.registration.ModBlocks;
import ironfurnaces.registration.ModMenus;
import ironfurnaces.tileentity.furnaces.data.ContainerDataBuilder;
import ironfurnaces.tileentity.furnaces.data.ContainerDataField;
import ironfurnaces.tileentity.heater.BlockWirelessEnergyHeaterTile;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;

public class BlockWirelessEnergyHeaterContainer extends AbstractContainerMenu {
    private static final int HEATER_SLOT = 0;

    private static final int PLAYER_INVENTORY_START = 1;
    private static final int PLAYER_INVENTORY_END = PLAYER_INVENTORY_START + 27;

    private static final int HOTBAR_START = PLAYER_INVENTORY_END;
    private static final int HOTBAR_END = HOTBAR_START + 9;

    private static final int DATA_ENERGY = 0;
    private static final int DATA_MAX_ENERGY = 1;
    private static final int DATA_COUNT = 2;

    @Getter
    private final BlockWirelessEnergyHeaterTile blockEntity;
    private final ContainerLevelAccess access;

    public BlockWirelessEnergyHeaterContainer(
            int containerId,
            Inventory playerInventory,
            RegistryFriendlyByteBuf buf
    ) {
        this(
                containerId,
                playerInventory,
                getBlockEntity(playerInventory, buf.readBlockPos()),
                new SimpleContainerData(DATA_COUNT)
        );
    }

    public BlockWirelessEnergyHeaterContainer(
            int containerId,
            Inventory playerInventory,
            BlockWirelessEnergyHeaterTile blockEntity
    ) {
        this(
                containerId,
                playerInventory,
                blockEntity,
                createServerData(blockEntity)
        );
    }

    private BlockWirelessEnergyHeaterContainer(
            int containerId,
            Inventory playerInventory,
            BlockWirelessEnergyHeaterTile blockEntity,
            ContainerData data
    ) {
        super(ModMenus.HEATER_MENU.get(), containerId);

        checkContainerDataCount(data, DATA_COUNT);

        this.blockEntity = blockEntity;
        this.access = ContainerLevelAccess.create(
                blockEntity.getLevel(),
                blockEntity.getBlockPos()
        );

        this.addSlot(new ResourceHandlerSlot(blockEntity.getItems(), (index, resource, amount) -> blockEntity.getItems().set(index, resource, amount), 0, 80, 37));

        addPlayerInventory(playerInventory, 8, 84);
        addPlayerHotbar(playerInventory, 8, 142);

        this.addDataSlots(data);
    }

    private static BlockWirelessEnergyHeaterTile getBlockEntity(
            Inventory playerInventory,
            BlockPos pos
    ) {
        Level level = playerInventory.player.level();
        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (blockEntity instanceof BlockWirelessEnergyHeaterTile heater) {
            return heater;
        }

        throw new IllegalStateException(
                "Expected BlockWirelessEnergyHeaterTile at " + pos
                        + ", got " + blockEntity
        );
    }

    private static ContainerData createServerData(BlockWirelessEnergyHeaterTile blockEntity) {
        return ContainerDataBuilder.create()
                .intValue(() -> blockEntity.getEnergy().getAmountAsInt(),
                        x -> blockEntity.getEnergy().setEnergy(x)
                )
                .intValue(() -> blockEntity.getEnergy().getCapacityAsInt(),
                        x -> blockEntity.getEnergy().setCapacity(x)
                ).build();
    }


    public int getEnergy() {
        return blockEntity.getEnergy().getAmountAsInt();
    }

    public int getMaxEnergy() {
        return blockEntity.getEnergy().getCapacityAsInt();
    }

    public int getEnergyScaled(int pixels) {
        int energy = getEnergy();
        int maxEnergy = getMaxEnergy();

        if (energy <= 0 || maxEnergy <= 0) {
            return 0;
        }

        return (int) ((long) energy * pixels / maxEnergy);
    }

    private void addPlayerInventory(Inventory playerInventory, int left, int top) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new Slot(
                        playerInventory,
                        column + row * 9 + 9,
                        left + column * 18,
                        top + row * 18
                ));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory, int left, int top) {
        for (int column = 0; column < 9; column++) {
            this.addSlot(new Slot(
                    playerInventory,
                    column,
                    left + column * 18,
                    top
            ));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;

        Slot slot = this.slots.get(index);
        if (slot == null || !slot.hasItem()) {
            return result;
        }

        ItemStack stackInSlot = slot.getItem();
        result = stackInSlot.copy();

        if (index == HEATER_SLOT) {
            if (!this.moveItemStackTo(stackInSlot, PLAYER_INVENTORY_START, HOTBAR_END, true)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (!this.moveItemStackTo(stackInSlot, HEATER_SLOT, HEATER_SLOT + 1, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (stackInSlot.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        if (stackInSlot.getCount() == result.getCount()) {
            return ItemStack.EMPTY;
        }

        slot.onTake(player, stackInSlot);
        return result;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, ModBlocks.HEATER.get());
    }
}