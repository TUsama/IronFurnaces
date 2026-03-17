package ironfurnaces.tileentity.furnaces.menu;

import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.util.function.BooleanSupplier;

@Accessors(fluent = true, chain = true)
public abstract class Partition {
    public final int size;
    public Vector2ic startPoint;
    public BooleanSupplier outsideCondition;
    public boolean interactable = true;
    @Setter
    public int menuStartIndex = -1;

    public Partition(int size, Vector2i startPoint, BooleanSupplier outsideCondition) {
        this.size = size;
        this.startPoint = startPoint;
        this.outsideCondition = outsideCondition;
    }

    public final int getMenuEndIndexExclusive() {
        return menuStartIndex + size;
    }

    public final boolean containsMenuSlot(int slotIndex) {
        return menuStartIndex >= 0
                && slotIndex >= menuStartIndex
                && slotIndex < menuStartIndex + size;
    }

    public boolean isAvailable(){
        return interactable && outsideCondition.getAsBoolean();
    }


    public abstract Slot makeSlot(int index);

    public boolean contains(int index){
        return getRange().contains(index);
    }


    public PartitionRange getRange() {
        return new PartitionRange(this, menuStartIndex, menuStartIndex + size);
    }

    public NonNullList<ItemStack> getItemStacksInPartition(NonNullList<Slot> slots){
        NonNullList<ItemStack> objects = NonNullList.create();
        getRange().forEach(x -> objects.add(slots.get(x).getItem()));
        return objects;
    }
}
