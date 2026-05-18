package ironfurnaces.tileentity.furnaces.menu.slot;

import ironfurnaces.tileentity.furnaces.menu.Partition;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import org.jetbrains.annotations.NotNull;

public class PartitionAccessSlot extends FurnaceSlotBase {
    private final Partition partition;

    public PartitionAccessSlot(ItemStacksResourceHandler itemHandler, int index, int xPosition, int yPosition, Partition partition) {
        super(itemHandler, index, xPosition, yPosition);
        this.partition = partition;
    }


    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return partition.isAvailable() && super.mayPlace(stack);
    }

    @Override
    public boolean mayPickup(Player playerIn) {
        return partition.isAvailable() && super.mayPickup(playerIn);
    }

    @Override
    public boolean isActive() {
        return partition.isAvailable() && super.isActive();
    }
}
