package ironfurnaces.tileentity.furnaces.cache;

import ironfurnaces.tileentity.furnaces.FurnaceMode;
import ironfurnaces.tileentity.furnaces.cache.stat.FillStats;
import ironfurnaces.tileentity.furnaces.tier.FurnacePattern;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

@Accessors(fluent = true, chain = true)
public class OutputCache extends TieredCache implements ICacheFillStats{
    @Setter
    private Consumer<OutputCache> contentChangeCallback;



    public OutputCache(FurnaceMode mode, FurnacePattern tier) {
        super(tier.inputSlotAmount(), mode, tier);
    }


    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);
        if (contentChangeCallback != null) {
            contentChangeCallback.accept(this);
        }
        recomputeFillStats();
    }
    private final FillStats fill_stats = new FillStats();

    public FillStats getFillStats() {
        return fill_stats;
    }

    /** 模式/tier改变、NBT load 后、或你不确定时调用 */
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
