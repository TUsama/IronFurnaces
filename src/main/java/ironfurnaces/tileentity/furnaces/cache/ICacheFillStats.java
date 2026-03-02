package ironfurnaces.tileentity.furnaces.cache;

import ironfurnaces.tileentity.furnaces.cache.stat.FillStats;

public interface ICacheFillStats {
    FillStats getFillStats();
    void recomputeFillStats();
}
