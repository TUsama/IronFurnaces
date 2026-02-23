package ironfurnaces.tileentity.furnaces.cache;

import ironfurnaces.adaptor.energy.EnergyWrapper;
import ironfurnaces.adaptor.energy.IEnergyWrapperHolder;
import ironfurnaces.tileentity.furnaces.FurnaceMode;
import lombok.Getter;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.ItemStackHandler;

public class FuelCache extends ItemStackHandler implements IEnergyStorage, IModeSensitive{
    @Getter
    private EnergyWrapper energy;
    private FurnaceMode mode;

    public FuelCache(EnergyWrapper energy, FurnaceMode mode) {
        this.energy = energy;
        this.mode = mode;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return energy.getStorage().lazyMap(x -> x.receiveEnergy(maxReceive, simulate)).orElse(0);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return energy.getStorage().lazyMap(x -> x.extractEnergy(maxExtract, simulate)).orElse(0);
    }

    @Override
    public int getEnergyStored() {
        return energy.getStorage().lazyMap(EnergyStorage::getEnergyStored).orElse(0);
    }

    @Override
    public int getMaxEnergyStored() {
        return energy.getStorage().lazyMap(EnergyStorage::getMaxEnergyStored).orElse(0);
    }

    @Override
    public boolean canExtract() {
        return energy.getStorage().lazyMap(EnergyStorage::canExtract).orElse(false);
    }

    @Override
    public boolean canReceive() {
        return energy.getStorage().lazyMap(EnergyStorage::canReceive).orElse(false);
    }



    @Override
    public void updateFurnaceMode(FurnaceMode mode) {
        this.mode = mode;
    }
}
