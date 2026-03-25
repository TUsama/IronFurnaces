package ironfurnaces.tileentity.furnaces.cache;

import ironfurnaces.tileentity.furnaces.FurnaceMode;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;

public interface IModeSensitive {
    void updateFurnaceMode(FurnaceMode mode, FurnacePatternBlockEntity blockEntity);
}
