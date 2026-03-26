package ironfurnaces.tileentity.furnaces.cache;

import ironfurnaces.tileentity.furnaces.FurnaceMode;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.cache.stat.FillStats;
import ironfurnaces.tileentity.furnaces.pattern.IFurnaceStats;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.function.Consumer;

public abstract class PatternCache extends ItemStackHandler implements IModeSensitive, ICacheIndex, IPatternSensitive, ICacheFillStats{
    protected FurnaceMode mode;
    protected int inputSlotAmount;
    @Nullable
    private int[] cacheSlotArray;
    private final FillStats fill_stats = new FillStats();

    public PatternCache(FurnaceMode mode, int inputSlotAmount) {
        this.mode = mode;
        this.inputSlotAmount = inputSlotAmount;
    }

    public PatternCache(int size, FurnaceMode mode, int inputSlotAmount) {
        super(size);
        this.mode = mode;
        this.inputSlotAmount = inputSlotAmount;
    }


    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        if (slot >= getSlots()) return;
        super.setStackInSlot(slot, stack);
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (slot >= getSlots()) return stack;
        return super.insertItem(slot, stack, simulate);
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (slot >= getSlots()) return ItemStack.EMPTY;
        return super.extractItem(slot, amount, simulate);
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);
    }

    @Override
    public int getSlots() {
        return switch (mode){
            case FURNACE -> 1;
            case GENERATOR -> 0;
            case FACTORY -> inputSlotAmount;
        };
    }

    public void handleStacksInUnavailableSlots(Level level, Consumer<Int2ObjectMap<ItemStack>> consumer) {
        Int2ObjectMap<ItemStack> stacksInUnavailableSlots = findStacksInUnavailableSlots(level);
        consumer.accept(stacksInUnavailableSlots);
    }

    public Int2ObjectMap<ItemStack> findStacksInUnavailableSlots(Level level) {
        Int2ObjectOpenHashMap<ItemStack> objectInt2ObjectOpenHashMap = new Int2ObjectOpenHashMap<>();
        if (level == null) return objectInt2ObjectOpenHashMap;
        for (int i = getSlots(); i < stacks.size(); i++) {
            ItemStack stack = getStackInSlot(i);
            if (stack.isEmpty()) continue;
            objectInt2ObjectOpenHashMap.put(i, stack);
        }
        return objectInt2ObjectOpenHashMap;
    }

    public void recomputeFillStats() {
        int slots = getSlots();
        fill_stats.slot_count = slots;
        fill_stats.fill_sum = 0.0f;
        fill_stats.non_empty = 0;

        for (int i = 0; i < slots; i++) {
            ItemStack s = getStackInSlot(i);
            if (s.isEmpty()) continue;

            fill_stats.non_empty++;
            int cap = Math.min(getSlotLimit(i), s.getMaxStackSize());
            if (cap > 0) {
                fill_stats.fill_sum += (float) s.getCount() / (float) cap;
            }
        }
    }

    public FillStats getFillStats() {
        return fill_stats;
    }

    @Override
    public void updateFurnaceMode(FurnaceMode mode, FurnacePatternBlockEntity blockEntity) {
        this.mode = mode;
        int slots = this.getSlots();
        int[] ints = new int[slots];
        for (int i = 0; i < slots; i++) {
            ints[i] = i;
        }
        cacheSlotArray = ints;
    }

    @Override
    public void updateFurnacePatternStats(IFurnaceStats<?> stats, FurnacePatternBlockEntity blockEntity) {
        int newSize = stats.inputSlotAmount();
        NonNullList<ItemStack> oldStacks = this.stacks;
        NonNullList<ItemStack> newList = NonNullList.withSize(newSize, ItemStack.EMPTY);

        int keepSize = Math.min(oldStacks.size(), newSize);
        for (int i = 0; i < keepSize; i++) {
            newList.set(i, oldStacks.get(i).copy());
        }

        // 先处理溢出部分，避免丢物品
        if (oldStacks.size() > newSize) {
            ArrayList<ItemStack> itemStacks = new ArrayList<>();
            for (int i = newSize; i < oldStacks.size(); i++) {
                ItemStack overflow = oldStacks.get(i);
                if (!overflow.isEmpty()) {
                    itemStacks.add(overflow.copy());
                }
            }
            blockEntity.returnOrDropStack(itemStacks, blockEntity.getOwner());
        }

        this.stacks = newList;
        this.inputSlotAmount = newSize;
        recomputeFillStats();
    }
    @Override
    public int[] getCacheIndex() {
        return cacheSlotArray;
    }


}
