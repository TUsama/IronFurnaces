package ironfurnaces.tileentity.furnaces.cache;

import ironfurnaces.adaptor.energy.FEnergyStorage;
import ironfurnaces.items.ItemHeater;
import ironfurnaces.tileentity.furnaces.BlockIronFurnaceTileBaseV2;
import ironfurnaces.tileentity.furnaces.FurnaceMode;
import ironfurnaces.tileentity.furnaces.cache.stat.FillStats;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@Accessors(fluent = true, chain = true)
public class FuelCache extends ItemStackHandler implements IEnergyStorage, IModeSensitive, ICacheIndex, ICacheFillStats {
    private final static int[] cacheIndex = new int[0];
    private BlockIronFurnaceTileBaseV2 tile;
    @Setter
    protected Function<ItemStack, Optional<? extends Recipe>> grabRecipeCallback;
    @Getter
    private FEnergyStorage energy;
    private FurnaceMode mode;
    @Setter
    private Consumer<FuelCache> contentChangeCallback;

    public FuelCache(BlockIronFurnaceTileBaseV2 tile, FEnergyStorage energy, FurnaceMode mode) {
        this.tile = tile;
        this.energy = energy;
        this.mode = mode;
    }

    public FuelCache(BlockIronFurnaceTileBaseV2 tile, FEnergyStorage energy, FurnaceMode mode, Function<ItemStack, Optional<? extends Recipe>> grabRecipeCallback) {
        this(tile, energy, mode);
        this.grabRecipeCallback = grabRecipeCallback;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return energy.receiveEnergy(maxReceive, simulate);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return energy.extractEnergy(maxExtract, simulate);
    }

    @Override
    public int getEnergyStored() {
        return energy.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored() {
        return energy.getMaxEnergyStored();
    }

    @Override
    public boolean canExtract() {
        return energy.canExtract();
    }

    @Override
    public boolean canReceive() {
        return energy.canReceive();
    }

    public boolean canReceive(int energy) {
        return (getEnergyStored() + energy) <= getMaxEnergyStored();
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return grabRecipeCallback.apply(stack).isPresent() || stack.getItem() instanceof ItemHeater;
    }

    @Override
    public void updateFurnaceMode(FurnaceMode mode) {
        this.mode = mode;
    }

    @Override
    protected void onContentsChanged(int slot) {
        if (contentChangeCallback != null) {
            contentChangeCallback.accept(this);
        }
        recomputeFillStats();
    }

    @Override
    public int[] getCacheIndex() {
        return cacheIndex;
    }

    private final FillStats fill_stats = new FillStats();

    @Override
    public FillStats getFillStats() {
        return fill_stats;
    }

    /** 模式/tier改变、NBT load 后、或你不确定时调用 */
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


}
