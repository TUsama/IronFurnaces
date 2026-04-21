package ironfurnaces.tileentity.furnaces.menu.partition;

import ironfurnaces.tileentity.furnaces.menu.slot.DynamicAccessSlot;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.items.IItemHandler;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BooleanSupplier;

public class PagedGridPartition extends GridPartition {
    protected final int visibleRows;

    /**
     * key: page
     * value: slots that belong to this page
     */
    @Getter
    protected final Int2ObjectMap<List<Slot>> pageSlots = new Int2ObjectArrayMap<>();
    @Getter
    @Setter
    private int currentPage = 0;

    public PagedGridPartition(
            int size,
            Vector2i startPoint,
            BooleanSupplier interactable,
            IItemHandler handler,
            int containerStartIndex,
            int columns,
            int visibleRows
    ) {
        super(size, startPoint, interactable, handler, containerStartIndex, columns);
        this.visibleRows = visibleRows;
    }

    @Override
    public Slot makeSlot(int localIndex) {
        int pageSize = this.columns * this.visibleRows;

        int page = localIndex / pageSize;
        int indexInPage = localIndex % pageSize;

        int col = indexInPage % this.columns;
        int row = indexInPage / this.columns;

        int x = this.startPoint.x() + col * 18;
        int y = this.startPoint.y() + row * 18;
        int containerIndex = this.containerStartIndex + localIndex;
        Slot slot;
        if (creator != null) {
            slot = creator.create(this.handler, containerIndex, x, y, this);
            if (slot instanceof DynamicAccessSlot dynamicAccessSlot) {
                dynamicAccessSlot.appendIsActiveCallback(() -> currentPage == page);
            }
        } else {
            slot = new DynamicAccessSlot(this.handler, containerIndex, x, y, this)
                    .appendIsActiveCallback(() -> currentPage == page);
        }

        this.pageSlots
                .computeIfAbsent(page, k -> new ArrayList<>())
                .add(slot);

        return slot;
    }


    public List<Slot> getSlotsInPage(int page) {
        return pageSlots.getOrDefault(page, Collections.emptyList());
    }

    public int getPageByLocalIndex(int localIndex) {
        int pageSize = this.columns * this.visibleRows;
        return localIndex / pageSize;
    }

    public int getPageCount() {
        int pageSize = this.columns * this.visibleRows;
        return (this.size + pageSize - 1) / pageSize;
    }

    public void nextPage() {
        currentPage = (currentPage + 1) % getPageCount();
    }

    public void previousPage() {
        currentPage = (currentPage - 1 + getPageCount()) % getPageCount();
    }
}
