package ironfurnaces.adaptor.energy;

import lombok.Getter;
import net.minecraftforge.common.util.LazyOptional;

import java.util.function.Consumer;

public class EnergyWrapper
        implements IEnergyHandler
{


    public EnergyWrapper(int capacity) {
        this.storage = LazyOptional.of(() -> new FEnergyStorage(capacity));
    }

    public EnergyWrapper(int capacity, int maxTransfer) {
        this.storage = LazyOptional.of(() -> new FEnergyStorage(capacity, maxTransfer));
    }

    public EnergyWrapper(int capacity, int maxReceive, int maxExtract) {
        this.storage = LazyOptional.of(() -> new FEnergyStorage(capacity, maxReceive, maxExtract));
    }

    public EnergyWrapper(int capacity, int maxReceive, int maxExtract, int energy) {
        this.storage = LazyOptional.of(() -> new FEnergyStorage(capacity, maxReceive, maxExtract, energy));
    }

    @Getter
    private LazyOptional<FEnergyStorage> storage;


    @Override
    public int getEnergy() {
        return storage.lazyMap(FEnergyStorage::getEnergy).orElse(0);
    }

    @Override
    public int getEnergyCapacity() {
        return storage.lazyMap(FEnergyStorage::getMaxEnergyStored).orElse(0);
    }

    @Override
    public void setEnergy(int energy) {
        storage.ifPresent(x -> x.setEnergy(energy));
    }

    public void invalidate(){
        storage.invalidate();
    }

    public EnergyWrapper withCallback(Consumer<FEnergyStorage> callback){
        this.storage.ifPresent(x -> x.callback(callback));
        return this;
    }


}
