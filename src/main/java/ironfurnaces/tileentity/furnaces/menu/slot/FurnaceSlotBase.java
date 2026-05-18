package ironfurnaces.tileentity.furnaces.menu.slot;

import ironfurnaces.tileentity.furnaces.cache.ICacheFillStats;
import net.neoforged.neoforge.transfer.IndexModifier;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;

public abstract class FurnaceSlotBase extends ResourceHandlerSlot {
    public FurnaceSlotBase(ItemStacksResourceHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, (index1, resource, amount) -> {
            itemHandler.set(index1, resource, amount);
            if (itemHandler instanceof ICacheFillStats cacheFillStats) {
                cacheFillStats.recomputeFillStats();
            }
        }, index, xPosition, yPosition);
    }

    public FurnaceSlotBase(ResourceHandler<ItemResource> itemHandler, IndexModifier<ItemResource> indexModifier, int index, int xPosition, int yPosition) {
        super(itemHandler, indexModifier, index, xPosition, yPosition);
    }


}
