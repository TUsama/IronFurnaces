package ironfurnaces.tileentity.furnaces.menu;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public record QuickMoveContext(
        DistributePartitionContainerMenu menu,
        Player player,
        int sourceSlotIndex,
        Slot sourceSlot,
        Partition sourcePartition,
        ItemStack stack
) {}
