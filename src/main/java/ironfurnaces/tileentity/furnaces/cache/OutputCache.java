package ironfurnaces.tileentity.furnaces.cache;

import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.pattern.IFurnaceStats;
import ironfurnaces.tileentity.furnaces.pattern.mode.AbstractFurnaceModeHandler;
import ironfurnaces.tileentity.furnaces.pattern.mode.FurnaceModeManager;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.function.IntConsumer;

@Accessors(fluent = true, chain = true)
public class OutputCache extends ResizableCache implements ICacheFillStats {
    @Setter
    private IntConsumer contentChangeCallback;


    public OutputCache(AbstractFurnaceModeHandler mode, IFurnaceStats stats) {
        super(stats.inputSlotAmount(), mode);
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);
        if (contentChangeCallback != null) {
            contentChangeCallback.accept(slot);
        }
        recomputeFillStats();
    }

    @Override
    public int getSlots() {
        return mode.expectedOutputSlots(usedStats);
    }

    @Override
    protected int updateSlotAmount(AbstractFurnaceModeHandler mode, IRecipeTypeHandler recipeTypeHandler, IFurnaceStats<?> stats, FurnacePatternBlockEntity blockEntity) {
        int maxOutputSlot = FurnaceModeManager.INSTANCE.getMaxOutputSlot(stats);
        return maxOutputSlot;
    }


}
