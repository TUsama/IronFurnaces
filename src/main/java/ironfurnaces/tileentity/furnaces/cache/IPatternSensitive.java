package ironfurnaces.tileentity.furnaces.cache;

import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.pattern.IFurnaceStats;

public interface IPatternSensitive {
    void updateFurnacePatternStats(IFurnaceStats<?> stats, FurnacePatternBlockEntity blockEntity);
}
