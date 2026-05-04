package ironfurnaces.tileentity.furnaces.menu.slot;

import ironfurnaces.tileentity.furnaces.cache.ICacheFillStats;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public abstract class FurnaceSlotBase extends SlotItemHandler {
    public FurnaceSlotBase(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (getItemHandler() instanceof ICacheFillStats cacheFillStats && isActive()) {
            cacheFillStats.recomputeFillStats();
        }
    }

}
