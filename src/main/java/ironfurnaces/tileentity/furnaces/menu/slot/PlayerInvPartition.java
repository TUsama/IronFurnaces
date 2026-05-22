package ironfurnaces.tileentity.furnaces.menu.slot;

import ironfurnaces.tileentity.furnaces.menu.Partition;
import net.minecraft.world.inventory.Slot;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.PlayerInventoryWrapper;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.joml.Vector2i;

public class PlayerInvPartition extends Partition {
    protected final int columns = 9;
    private final PlayerInventoryWrapper wrapper;
    private final Vector2i hotbarStartPoint = new Vector2i(8, 142);

    public PlayerInvPartition(PlayerInventoryWrapper wrapper) {
        super(36, new Vector2i(8, 66), () -> true);
        this.wrapper = wrapper;
    }

    @Override
    public Slot makeSlot(int index) {
        if (index < 9){
            int x = hotbarStartPoint.x() + (index % this.columns) * 18;
            int y = hotbarStartPoint.y() + (index / this.columns) * 18;

            return new ResourceHandlerSlot(wrapper, (a, b, c) -> {
                ResourceHandler<ItemResource> slot = wrapper.getSlot(a);
                try (var tx = Transaction.openRoot()) {
                    if (!slot.getResource(0).isEmpty()){
                        slot.extract(slot.getResource(0), slot.getAmountAsInt(0), tx);
                    }
                    if (!b.isEmpty()) slot.insert(b, c, tx);
                    tx.commit();
                }

            }, index, x, y);
        } else {
            int x = this.startPoint.x() + (index % this.columns) * 18;
            int y = this.startPoint.y() + (index / this.columns) * 18;

            return new ResourceHandlerSlot(wrapper, (a, b, c) -> {
                ResourceHandler<ItemResource> slot = wrapper.getSlot(a);
                try (var tx = Transaction.openRoot()) {
                    if (!slot.getResource(0).isEmpty()) slot.extract(slot.getResource(0), slot.getAmountAsInt(0), tx);
                    if (!b.isEmpty()) {
                        slot.insert(b, c, tx);
                    }
                    tx.commit();
                }

            }, index, x, y);
        }

    }
}
