package ironfurnaces.tileentity.furnaces.cache;

import ironfurnaces.tileentity.furnaces.cache.stat.FillStats;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.stream.IntStream;

public class RemainingCache extends ItemStackHandler implements ICacheIndex, ICacheFillStats {
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
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);
        recomputeFillStats();
    }

    public void recomputeFillStats() {
        int slots = getSlots();
        fill_stats.slot_count = slots;
        fill_stats.fill_sum = 0.0f;
        fill_stats.non_empty = 0;

        for (int i = 0; i < slots; i++) {
            ItemStack s = getStackInSlot(i);
            if (s.isEmpty()) continue;

            fill_stats.non_empty++;
            int cap = Math.min(getSlotLimit(i), s.getMaxStackSize());
            if (cap > 0) {
                fill_stats.fill_sum += (float) s.getCount() / (float) cap;
            }
        }
    }

}
