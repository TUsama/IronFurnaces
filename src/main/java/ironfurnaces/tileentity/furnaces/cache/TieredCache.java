package ironfurnaces.tileentity.furnaces.cache;

import ironfurnaces.tileentity.furnaces.FurnaceMode;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class TieredCache extends ItemStackHandler implements IModeSensitive{
    protected FurnaceMode mode;
    private ForgeConfigSpec.IntValue tier;

    public TieredCache(int size, FurnaceMode mode, ForgeConfigSpec.IntValue tier) {
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
    public int getSlots() {
        if (mode.equals(FurnaceMode.FACTORY)) {
            return Math.min(6, 2 * (tier.get() + 1));
        } else {
            return 1;
        }
    }


    @Override
    public void updateFurnaceMode(FurnaceMode mode) {
        this.mode = mode;
    }
}
