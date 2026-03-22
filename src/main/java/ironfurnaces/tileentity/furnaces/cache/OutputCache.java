package ironfurnaces.tileentity.furnaces.cache;

import ironfurnaces.tileentity.furnaces.FurnaceMode;
import ironfurnaces.tileentity.furnaces.cache.stat.FillStats;
import ironfurnaces.tileentity.furnaces.pattern.EffectiveFurnaceStats;
import ironfurnaces.tileentity.furnaces.pattern.FurnacePattern;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

import java.util.function.IntConsumer;

@Accessors(fluent = true, chain = true)
public class OutputCache extends PatternCache implements ICacheFillStats {
    private final FillStats fill_stats = new FillStats();
    @Setter
    private IntConsumer contentChangeCallback;


    public OutputCache(FurnaceMode mode, FurnacePattern pattern) {
        super(pattern.inputSlotAmount(), mode, pattern.inputSlotAmount());
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);
        if (contentChangeCallback != null) {
            contentChangeCallback.accept(slot);
        }
        recomputeFillStats();
    }

    public FillStats getFillStats() {
        return fill_stats;
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


    @Override
    public void updateFurnacePattern(EffectiveFurnaceStats stats) {
        int i = stats.inputSlotAmount();
        NonNullList<ItemStack> newList = NonNullList.withSize(i, ItemStack.EMPTY);
        for (int i1 = 0; i1 < this.getSlots(); i1++) {
            if (newList.size() - 1 >= i1) newList.set(i1, this.getStackInSlot(i1).copy());
        }
        this.stacks = newList;
        this.inputSlotAmount = stats.inputSlotAmount();

        recomputeFillStats();

    }
}
