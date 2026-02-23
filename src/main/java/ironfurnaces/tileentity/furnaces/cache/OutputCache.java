package ironfurnaces.tileentity.furnaces.cache;

import ironfurnaces.tileentity.furnaces.FurnaceMode;
import lombok.With;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.function.Consumer;

public class OutputCache extends TieredCache {
    private final Consumer<OutputCache> contentChangeCallback;

    public OutputCache(int size, FurnaceMode mode, ForgeConfigSpec.IntValue tier, Consumer<OutputCache> contentChangeCallback) {
        super(size, mode, tier);
        this.contentChangeCallback = contentChangeCallback;
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);
        contentChangeCallback.accept(this);
    }
}
