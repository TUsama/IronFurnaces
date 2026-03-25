package ironfurnaces.tileentity.furnaces.cache;

import ironfurnaces.adaptor.energy.FEnergyStorage;
import ironfurnaces.items.ItemHeater;
import ironfurnaces.tileentity.furnaces.FurnaceMode;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.cache.stat.FillStats;
import ironfurnaces.tileentity.furnaces.pattern.IFurnaceStats;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Predicate;

@Accessors(fluent = true, chain = true)
public class FuelCache extends ItemStackHandler implements IEnergyStorage, IModeSensitive, ICacheIndex, ICacheFillStats, IPatternSensitive {
    private final static int[] cacheIndex = new int[0];
        @Getter
    private FEnergyStorage energy;
    @Setter
    private Predicate<ItemStack> burnableFunction;
    @Setter
    private Consumer<FuelCache> contentChangeCallback;


    public FuelCache( FEnergyStorage energy) {
        this.energy = energy;
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
        return burnableFunction.test(stack) || stack.getItem() instanceof ItemHeater;
    }

    @Override
    public void updateFurnaceMode(FurnaceMode mode, FurnacePatternBlockEntity blockEntity) {
        if (mode.equals(FurnaceMode.FACTORY)){
            Containers.dropContents(blockEntity.getLevel(), blockEntity.getBlockPos(), this.stacks);
        }
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

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();

        tag.put("Items", super.serializeNBT());
        Tag tag1 = energy.serializeNBT();
        tag.put("Energy", tag1);

        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains("Items", Tag.TAG_COMPOUND)) {
            super.deserializeNBT(nbt.getCompound("Items"));
        }
        if (nbt.contains("Energy")) {
            energy.deserializeNBT(nbt.get("Energy"));
        }

        recomputeFillStats();
    }

    @Override
    public void updateFurnacePatternStats(IFurnaceStats<?> stats, FurnacePatternBlockEntity blockEntity) {
        this.energy.setCapacity(stats.energyCapacity());
        this.energy.setMaxTransfer(stats.energyCapacity());
    }
}
