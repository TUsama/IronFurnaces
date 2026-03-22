package ironfurnaces.tileentity.furnaces.cache;

import ironfurnaces.tileentity.furnaces.FurnaceMode;
import ironfurnaces.tileentity.furnaces.pattern.FurnacePattern;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public abstract class PatternCache extends ItemStackHandler implements IModeSensitive, ICacheIndex, IPatternSensitive{
    protected FurnaceMode mode;
    protected int inputSlotAmount;
    @Nullable
    private int[] cacheSlotArray;


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
        if (slot > getSlots()) return;
        super.setStackInSlot(slot, stack);
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (slot > getSlots()) return stack;
        return super.insertItem(slot, stack, simulate);
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (slot > getSlots()) return ItemStack.EMPTY;
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

    @Override
    public void updateFurnaceMode(FurnaceMode mode) {
        this.mode = mode;
        int slots = this.getSlots();
        int[] ints = new int[slots];
        for (int i = 0; i < slots; i++) {
            ints[i] = i;
        }
        cacheSlotArray = ints;
    }

    @Override
    public int[] getCacheIndex() {
        return cacheSlotArray;
    }


}
