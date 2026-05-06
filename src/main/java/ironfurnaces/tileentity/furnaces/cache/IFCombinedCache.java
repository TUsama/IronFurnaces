package ironfurnaces.tileentity.furnaces.cache;

import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.cache.stat.FillStats;
import ironfurnaces.tileentity.furnaces.pattern.IFurnaceStats;
import ironfurnaces.tileentity.furnaces.pattern.mode.AbstractFurnaceModeHandler;
import lombok.Getter;
import net.neoforged.neoforge.transfer.CombinedResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.IntStream;

// a copy from CombinedInvWrapper, but with recalc when furnace mode updated.
public class IFCombinedCache extends CombinedResourceHandler<ItemResource> implements ICacheIndex, ICacheFillStats, INeedUpdate {
    private final int[] cacheIndex;
    @Getter
    private final ResourceHandler<ItemResource>[] handlers;
    private boolean needRecreate = false;

    @SafeVarargs
    public IFCombinedCache(ResourceHandler<ItemResource>... handlers) {
        super(handlers);
        this.handlers = handlers;
        cacheIndex = IntStream.range(0, size()).toArray();
    }

    @Override
    public int[] getCacheIndex() {
        return cacheIndex;
    }


    public FillStats getFillStats() {
        return Arrays.stream(handlers).filter(x -> x instanceof ICacheFillStats)
                .map(x -> ((ICacheFillStats) x).getFillStats())
                .reduce(FillStats::add).orElse(FillStats.EMPTY);
    }

    public void recomputeFillStats() {
        //don't need here
    }

    public void tryRecreate(Consumer<IFCombinedCache> setter) {
        if (needRecreate) setter.accept(new IFCombinedCache(handlers));
    }

    @Override
    public void update(AbstractFurnaceModeHandler mode, IRecipeTypeHandler recipeTypeHandler, IFurnaceStats<?> stats, FurnacePatternBlockEntity blockEntity) {
        int index = 0;
        for (int i = 0; i < handlers.length; i++) {
            index += handlers[i].size();

        }
        if (this.size() != index) this.needRecreate = true;
    }
}
