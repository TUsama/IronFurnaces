package ironfurnaces.tileentity.furnaces.cache;

import ironfurnaces.tileentity.furnaces.FurnaceMode;
import lombok.Setter;
import lombok.With;
import lombok.experimental.Accessors;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
@Accessors(fluent = true, chain = true)
public class OutputCache extends TieredCache {
    @Setter
    private Consumer<OutputCache> contentChangeCallback;

    public OutputCache(FurnaceMode mode, ForgeConfigSpec.IntValue tier) {
        super(mode, tier);
    }


    public OutputCache(int size, FurnaceMode mode, ForgeConfigSpec.IntValue tier) {
        super(size, mode, tier);
    }


    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);
        if (contentChangeCallback != null) {
            contentChangeCallback.accept(this);
        }
    }
}
