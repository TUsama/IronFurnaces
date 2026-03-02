package ironfurnaces.tileentity.furnaces.cache;

import ironfurnaces.tileentity.furnaces.FurnaceMode;
import ironfurnaces.tileentity.furnaces.tier.FurnacePattern;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public abstract class TieredCache extends ItemStackHandler implements IModeSensitive, ICacheIndex {
    protected FurnaceMode mode;
    private FurnacePattern tier;
    @Nullable
    private int[] cacheSlotArray;

    public TieredCache(FurnaceMode mode, FurnacePattern tier) {
        this.mode = mode;
        this.tier = tier;
    }

    public TieredCache(int size, FurnaceMode mode, FurnacePattern tier) {
        super(size);
        this.mode = mode;
        this.tier = tier;
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
            case FACTORY -> tier.inputSlotAmount();
        };
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
