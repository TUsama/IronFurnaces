package ironfurnaces.tileentity.furnaces.menu.slot;

import ironfurnaces.tileentity.furnaces.menu.Partition;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.With;
import lombok.experimental.Accessors;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.function.BooleanSupplier;


@Accessors(chain = true)
@Setter
@Getter
public class DynamicAccessSlot extends PartitionAccessSlot{
    private BooleanSupplier mayPlaceCallback = () -> true;
    private BooleanSupplier mayPickupCallback = () -> true;
    private BooleanSupplier isActiveCallback = () -> true;

    public DynamicAccessSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, Partition partition) {
        super(itemHandler, index, xPosition, yPosition, partition);
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
