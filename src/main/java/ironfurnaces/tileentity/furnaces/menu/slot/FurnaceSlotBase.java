package ironfurnaces.tileentity.furnaces.menu.slot;

import ironfurnaces.tileentity.furnaces.cache.ICacheFillStats;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

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
