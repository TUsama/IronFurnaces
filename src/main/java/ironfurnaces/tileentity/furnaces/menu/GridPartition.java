package ironfurnaces.tileentity.furnaces.menu;

import ironfurnaces.tileentity.furnaces.menu.slot.DynamicAccessSlot;
import ironfurnaces.tileentity.furnaces.menu.slot.PartitionAccessSlot;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.items.IItemHandler;
import org.joml.Vector2i;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

@Accessors(chain = true)
public class GridPartition extends Partition {
    protected final IItemHandler handler;
    protected final int containerStartIndex;
    protected final int columns;
    protected SlotCreator creator;

    public GridPartition(
            int size,
            Vector2i startPoint,
            BooleanSupplier interactable,
            IItemHandler handler,
            int containerStartIndex,
            int columns
    ) {
        super(size, startPoint, interactable);
        this.handler = handler;
        this.containerStartIndex = containerStartIndex;
        this.columns = columns;
    }

    @Override
    public Slot makeSlot(int localIndex) {
        int x = this.startPoint.x() + (localIndex % this.columns) * 18;
        int y = this.startPoint.y() + (localIndex / this.columns) * 18;
        int containerIndex = this.containerStartIndex + localIndex;
        if (creator != null){
            return creator.create(this.handler, containerIndex, x, y, this);
        }
        return new PartitionAccessSlot(this.handler, containerIndex, x, y, this);
    }

    public <T extends Partition> T withDynamicAccessSlot(Consumer<DynamicAccessSlot> slotConsumer){
        setCreator((itemHandler, index, xPosition, yPosition, partition) -> {
            DynamicAccessSlot dynamicAccessSlot = new DynamicAccessSlot(itemHandler, index, xPosition, yPosition, partition);
            slotConsumer.accept(dynamicAccessSlot);
            return dynamicAccessSlot;
        });
        return (T) this;
    }

    public <T extends Partition> T setCreator(SlotCreator creator) {
        this.creator = creator;
        return (T) this;
    }

    public interface SlotCreator {
        Slot create(IItemHandler itemHandler, int index, int xPosition, int yPosition, Partition partition);
    }


}
