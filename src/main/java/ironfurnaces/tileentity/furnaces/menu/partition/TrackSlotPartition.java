package ironfurnaces.tileentity.furnaces.menu.partition;

import lombok.Getter;
import net.minecraft.world.inventory.Slot;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

public class TrackSlotPartition extends ItemStackGridPartition {
    @Getter
    protected final List<Slot> trackedSlot;


    public TrackSlotPartition(int size, Vector2i startPoint, BooleanSupplier interactable, ItemStacksResourceHandler handler, int containerStartIndex, int columns) {
        super(size, startPoint, interactable, handler, containerStartIndex, columns);
        this.trackedSlot = new ArrayList<>();
    }

    public void handleSlot() {
    }


    @Override
    public Slot makeSlot(int localIndex) {
        Slot slot = super.makeSlot(localIndex);
        this.trackedSlot.add(slot);
        return slot;
    }
}
