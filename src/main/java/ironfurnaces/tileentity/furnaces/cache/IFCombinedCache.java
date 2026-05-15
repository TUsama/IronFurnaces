package ironfurnaces.tileentity.furnaces.cache;

import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.cache.stat.FillStats;
import ironfurnaces.tileentity.furnaces.pattern.IFurnaceStats;
import ironfurnaces.tileentity.furnaces.pattern.mode.AbstractFurnaceModeHandler;
import lombok.Getter;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import net.minecraftforge.items.wrapper.EmptyHandler;

import java.util.*;
import java.util.stream.IntStream;

// a copy from CombinedInvWrapper, but with recalc when furnace mode updated.
public class IFCombinedCache implements ICacheIndex, IItemHandlerModifiable, ICacheFillStats, INeedUpdate{
    private int[] cacheIndex;
    @Getter
    protected final IItemHandlerModifiable[] itemHandler;
    protected final int[] baseIndex;
    protected int slotCount;

    public IFCombinedCache(IItemHandlerModifiable... itemHandler) {
        this.itemHandler = itemHandler;
        this.baseIndex = new int[itemHandler.length];
        int index = 0;
        for (int i = 0; i < itemHandler.length; i++)
        {
            if (itemHandler[i] instanceof ViewOnlyCache) continue;
            index += itemHandler[i].getSlots();
            baseIndex[i] = index;
        }
        this.slotCount = index;
        cacheIndex = IntStream.range(0, getSlots()).toArray();
    }

    @Override
    public int[] getCacheIndex() {
        return cacheIndex;
    }


    // returns the handler index for the slot
    protected int getIndexForSlot(int slot)
    {
        if (slot < 0)
            return -1;

        for (int i = 0; i < baseIndex.length; i++)
        {
            if (slot - baseIndex[i] < 0)
            {
                return i;
            }
        }
        return -1;
    }

    public int[] getSlotIndexesOf(IItemHandlerModifiable target) {
        if (target == null) {
            return new int[0];
        }

        if (target == this) {
            return IntStream.range(0, getSlots()).toArray();
        }

        Set<IItemHandlerModifiable> targetLeaves = Collections.newSetFromMap(new IdentityHashMap<>());
        collectLeafHandlers(
                target,
                targetLeaves,
                Collections.newSetFromMap(new IdentityHashMap<>())
        );

        if (targetLeaves.isEmpty()) {
            return new int[0];
        }

        List<Integer> result = new ArrayList<>();
        collectMatchingSlotIndexes(
                this,
                0,
                targetLeaves,
                result,
                Collections.newSetFromMap(new IdentityHashMap<>())
        );

        return result.stream().mapToInt(Integer::intValue).toArray();
    }

    private static void collectLeafHandlers(
            IItemHandlerModifiable handler,
            Set<IItemHandlerModifiable> result,
            Set<IFCombinedCache> visiting
    ) {
        if (handler == null || handler instanceof ViewOnlyCache) {
            return;
        }

        if (handler instanceof IFCombinedCache combined) {
            if (!visiting.add(combined)) {
                return;
            }

            for (IItemHandlerModifiable child : combined.itemHandler) {
                collectLeafHandlers(child, result, visiting);
            }

            visiting.remove(combined);
            return;
        }

        result.add(handler);
    }

    private static void collectMatchingSlotIndexes(
            IItemHandlerModifiable current,
            int globalOffset,
            Set<IItemHandlerModifiable> targetLeaves,
            List<Integer> result,
            Set<IFCombinedCache> visiting
    ) {
        if (current == null || current instanceof ViewOnlyCache) {
            return;
        }

        if (current instanceof IFCombinedCache combined) {
            if (!visiting.add(combined)) {
                return;
            }

            int offset = globalOffset;

            for (IItemHandlerModifiable child : combined.itemHandler) {
                if (child instanceof ViewOnlyCache) {
                    continue;
                }

                collectMatchingSlotIndexes(child, offset, targetLeaves, result, visiting);
                offset += child.getSlots();
            }

            visiting.remove(combined);
            return;
        }

        if (!targetLeaves.contains(current)) {
            return;
        }

        for (int i = 0; i < current.getSlots(); i++) {
            result.add(globalOffset + i);
        }
    }

    protected IItemHandlerModifiable getHandlerFromIndex(int index)
    {
        if (index < 0 || index >= itemHandler.length)
        {

            return (IItemHandlerModifiable) EmptyHandler.INSTANCE;
        }
        return itemHandler[index];
    }

    protected int getSlotFromIndex(int slot, int index)
    {
        if (index <= 0 || index >= baseIndex.length)
        {
            return slot;
        }
        return slot - baseIndex[index - 1];
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        int index = getIndexForSlot(slot);
        IItemHandlerModifiable handler = getHandlerFromIndex(index);
        slot = getSlotFromIndex(slot, index);
        handler.setStackInSlot(slot, stack);
    }

    @Override
    public int getSlots() {
        return slotCount;
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        int index = getIndexForSlot(slot);
        IItemHandlerModifiable handler = getHandlerFromIndex(index);
        slot = getSlotFromIndex(slot, index);
        return handler.getStackInSlot(slot);
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        int index = getIndexForSlot(slot);
        IItemHandlerModifiable handler = getHandlerFromIndex(index);
        slot = getSlotFromIndex(slot, index);
        return handler.insertItem(slot, stack, simulate);
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        int index = getIndexForSlot(slot);
        IItemHandlerModifiable handler = getHandlerFromIndex(index);
        slot = getSlotFromIndex(slot, index);
        return handler.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        int index = getIndexForSlot(slot);
        IItemHandlerModifiable handler = getHandlerFromIndex(index);
        int localSlot = getSlotFromIndex(slot, index);
        return handler.getSlotLimit(localSlot);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        int index = getIndexForSlot(slot);
        IItemHandlerModifiable handler = getHandlerFromIndex(index);
        int localSlot = getSlotFromIndex(slot, index);
        return handler.isItemValid(localSlot, stack);
    }


    public FillStats getFillStats() {
        return Arrays.stream(itemHandler).filter(x -> x instanceof ICacheFillStats)
                .map(x -> ((ICacheFillStats) x).getFillStats())
                .reduce(FillStats::add).orElse(FillStats.EMPTY);
    }

    public void recomputeFillStats() {
        //don't need here
    }


    @Override
    public void update(AbstractFurnaceModeHandler mode, IRecipeTypeHandler recipeTypeHandler, IFurnaceStats<?> stats, FurnacePatternBlockEntity blockEntity) {
        int index = 0;
        for (int i = 0; i < itemHandler.length; i++)
        {
            index += itemHandler[i].getSlots();
            baseIndex[i] = index;
        }
        this.slotCount = index;
        cacheIndex = IntStream.range(0, getSlots()).toArray();
    }
}
