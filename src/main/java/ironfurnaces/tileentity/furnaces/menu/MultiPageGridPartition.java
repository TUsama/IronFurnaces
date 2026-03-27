package ironfurnaces.tileentity.furnaces.menu;

import ironfurnaces.tileentity.furnaces.menu.slot.DynamicAccessSlot;
import ironfurnaces.tileentity.furnaces.menu.slot.PartitionAccessSlot;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.items.IItemHandler;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;

@Accessors(fluent = true, chain = true)
public class MultiPageGridPartition extends MovableGridPartition{



    public MultiPageGridPartition(int size, Vector2i startPoint, BooleanSupplier interactable, IItemHandler handler, int containerStartIndex, int columns) {
        super(size, startPoint, interactable, handler, containerStartIndex, columns);
    }



    @Override
    public Slot makeSlot(int localIndex) {
        int x = this.startPoint.x() + (localIndex % this.columns) * 18;
        int y = this.startPoint.y() + (localIndex / this.columns) * 18;
        int containerIndex = this.containerStartIndex + localIndex;
        Slot slot;
        if (creator != null){
            slot = creator.create(this.handler, containerIndex, x, y, this);
        } else {
            slot = new PartitionAccessSlot(this.handler, containerIndex, x, y, this);
        }
        this.trackedSlot.add(slot);
        return slot;
    }
}
