package ironfurnaces.tileentity.furnaces.menu.slot;

import ironfurnaces.tileentity.furnaces.menu.Partition;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.function.BooleanSupplier;


@Accessors(chain = true)
@Getter
public class DynamicAccessSlot extends PartitionAccessSlot{
    private BooleanSupplier mayPlaceCallback = () -> true;
    private BooleanSupplier mayPickupCallback = () -> true;
    private BooleanSupplier isActiveCallback = () -> true;

    public DynamicAccessSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, Partition partition) {
        super(itemHandler, index, xPosition, yPosition, partition);
    }

    public DynamicAccessSlot appendMayPlaceCallback(BooleanSupplier mayPlaceCallback) {
        BooleanSupplier old = this.mayPlaceCallback;
        this.mayPlaceCallback = () -> old.getAsBoolean() && mayPlaceCallback.getAsBoolean();
        return this;
    }

    public DynamicAccessSlot appendMayPickupCallback(BooleanSupplier mayPickupCallback) {
        BooleanSupplier old = this.mayPickupCallback;
        this.mayPickupCallback = () -> old.getAsBoolean() && mayPickupCallback.getAsBoolean();
        return this;
    }

    public DynamicAccessSlot appendIsActiveCallback(BooleanSupplier isActiveCallback) {
        BooleanSupplier old = this.isActiveCallback;
        this.isActiveCallback = () -> old.getAsBoolean() && isActiveCallback.getAsBoolean();
        return this;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return mayPlaceCallback.getAsBoolean() && super.mayPlace(stack);
    }

    @Override
    public boolean mayPickup(Player playerIn) {
        return mayPickupCallback.getAsBoolean() && super.mayPickup(playerIn);
    }

    @Override
    public boolean isActive() {
        return isActiveCallback.getAsBoolean() && super.isActive();
    }
}
