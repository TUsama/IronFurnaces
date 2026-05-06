package ironfurnaces.tileentity.furnaces.cache;

import ironfurnaces.tileentity.furnaces.cache.stat.FillStats;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemUtil;

import java.util.stream.IntStream;

public class RemainingCache extends ItemStacksResourceHandler implements ICacheIndex, ICacheFillStats {
    private final static int[] cacheIndex = IntStream.range(0, 9).toArray();
    private final FillStats fill_stats = new FillStats();

    public RemainingCache() {
        super(9);
    }

    @Override
    public int[] getCacheIndex() {
        return cacheIndex;
    }

    public FillStats getFillStats() {
        return fill_stats;
    }

    @Override
    protected void onContentsChanged(int index, ItemStack previousContents) {
        super.onContentsChanged(index, previousContents);
        recomputeFillStats();
    }


    public void recomputeFillStats() {
        int slots = size();
        fill_stats.slot_count = slots;
        fill_stats.fill_sum = 0.0f;
        fill_stats.non_empty = 0;

        for (int i = 0; i < slots; i++) {
            ItemStack s = ItemUtil.getStack(this, i);
            if (s.isEmpty()) continue;

            fill_stats.non_empty++;
            int cap = Math.min(getCapacity(i, getResource(i)), s.getMaxStackSize());
            if (cap > 0) {
                fill_stats.fill_sum += (float) s.getCount() / (float) cap;
            }
        }
    }

}
