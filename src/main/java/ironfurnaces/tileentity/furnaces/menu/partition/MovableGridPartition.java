package ironfurnaces.tileentity.furnaces.menu.partition;

import ironfurnaces.tileentity.furnaces.menu.slot.DynamicAccessSlot;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.Slot;
import net.neoforged.neoforge.items.IItemHandler;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;

@Accessors(fluent = true, chain = true)
public class MovableGridPartition extends TrackSlotPartition{
    @Setter
    private IntSupplier yMovementProvider;
    @Setter
    NonNullList<Slot> menuSlots;

    public MovableGridPartition(int size, Vector2i startPoint, BooleanSupplier interactable, IItemHandler handler, int containerStartIndex, int columns) {
        super(size, startPoint, interactable, handler, containerStartIndex, columns);
    }

    public void handleSlot(){
        if (yMovementProvider == null) return;
        int yMovement = yMovementProvider.getAsInt();
        trackedSlot.clear();
        getRange().forEachWithLocalIndex((integer, integer2) -> {
            Vector2ic startPoint = this.startPoint;
            this.startPoint = new Vector2i(startPoint.x(), yMovement);
            //只有走addSlot()才会自动赋值index
            Slot newSlot = makeSlot(integer2);
            Slot old = this.menuSlots.set(integer, newSlot);
            newSlot.index = old.index;
            if (newSlot instanceof DynamicAccessSlot newSlot1 && old instanceof DynamicAccessSlot old1){
                newSlot1.appendMayPlaceCallback(old1.getMayPlaceCallback())
                        .appendIsActiveCallback(old1.getIsActiveCallback())
                        .appendMayPickupCallback(old1.getMayPickupCallback());
            }
        });

    }

}
