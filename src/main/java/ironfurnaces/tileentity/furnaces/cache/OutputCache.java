package ironfurnaces.tileentity.furnaces.cache;

import ironfurnaces.tileentity.furnaces.FurnaceMode;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.cache.stat.FillStats;
import ironfurnaces.tileentity.furnaces.pattern.IFurnaceStats;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

import java.util.function.IntConsumer;

@Accessors(fluent = true, chain = true)
public class OutputCache extends PatternCache implements ICacheFillStats {
    @Setter
    private IntConsumer contentChangeCallback;


    public OutputCache(FurnaceMode mode, IFurnaceStats stats) {
        super(stats.inputSlotAmount(), mode, stats.inputSlotAmount());
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);
        if (contentChangeCallback != null) {
            contentChangeCallback.accept(slot);
        }
        recomputeFillStats();
    }


}
