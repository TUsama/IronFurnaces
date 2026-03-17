package ironfurnaces.tileentity.furnaces.menu;

import ironfurnaces.util.BiIntConsumer;
import it.unimi.dsi.fastutil.ints.IntIterable;
import it.unimi.dsi.fastutil.ints.IntIterator;
import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;

public record PartitionRange(
        Partition partition,
        int startInclusive,
        int endExclusive
) implements IntIterable {
    public int size() {
        return endExclusive - startInclusive;
    }

    public boolean contains(int slotIndex) {
        return slotIndex >= startInclusive && slotIndex < endExclusive;
    }

    public void forEachWithLocalIndex(BiIntConsumer consumer){
        IntIterator iterator = iterator();
        while (iterator.hasNext()) {
            int menuIndex = iterator.nextInt();
            int localIndex = menuIndex - startInclusive;
            consumer.accept(menuIndex, localIndex);
        }
    }

    @Override
    public @NotNull IntIterator iterator() {
        return new IntIterator() {
            private int cursor = startInclusive;

            @Override
            public boolean hasNext() {
                return cursor < endExclusive;
            }

            @Override
            public int nextInt() {
                if (!hasNext()) throw new NoSuchElementException();
                return cursor++;
            }
        };
    }
}
