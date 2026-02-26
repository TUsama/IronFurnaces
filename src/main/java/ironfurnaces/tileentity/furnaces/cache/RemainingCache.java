package ironfurnaces.tileentity.furnaces.cache;

import net.minecraftforge.items.ItemStackHandler;

import java.util.stream.IntStream;

public class RemainingCache extends ItemStackHandler implements ICacheIndex {
    private final static int[] cacheIndex = IntStream.range(0, 9).toArray();

    public RemainingCache() {
        super(9);
    }

    @Override
    public int[] getCacheIndex() {
        return cacheIndex;
    }
}
