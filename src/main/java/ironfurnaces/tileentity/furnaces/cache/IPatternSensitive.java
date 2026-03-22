package ironfurnaces.tileentity.furnaces.cache;

import ironfurnaces.tileentity.furnaces.pattern.EffectiveFurnaceStats;

public interface IPatternSensitive {
    void updateFurnacePattern(EffectiveFurnaceStats stats);
}
