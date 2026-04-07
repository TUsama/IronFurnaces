package ironfurnaces.adaptor.energy;

import lombok.Getter;
//? 1.20.1
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.function.Consumer;

public class EnergyWrapper
        implements IEnergyStorage
{

    @Getter
            //~ if >1.20.1 'LazyOptional<FEnergyStorage>' -> 'FEnergyStorage'
    private LazyOptional<FEnergyStorage> storage;
    //~ if >1.20.1 'LazyOptional.of(() -> new FEnergyStorage' -> '(new FEnergyStorage' {
    public EnergyWrapper(int capacity) {
        this.storage = LazyOptional.of(() -> new FEnergyStorage(capacity));
    }

    public EnergyWrapper(int capacity, int maxReceive, int maxExtract) {
        this.storage = LazyOptional.of(() -> new FEnergyStorage(capacity, maxReceive, maxExtract));
    }

    public EnergyWrapper(int capacity, int maxReceive, int maxExtract, int energy) {
        this.storage = LazyOptional.of(() -> new FEnergyStorage(capacity, maxReceive, maxExtract, energy));
    }

//~}


    //? forge {
    public void invalidate(){
        storage.invalidate();
    }


    public EnergyWrapper withCallback(Consumer<FEnergyStorage> callback){
        this.storage.ifPresent(x -> x.callback(callback));
        return this;
    }

    @Override
    public int receiveEnergy(int i, boolean b) {
        LazyOptional<Integer> integerLazyOptional = storage.lazyMap(x -> x.receiveEnergy(i, b));
        return integerLazyOptional.orElse(0);
    }

    @Override
    public int extractEnergy(int i, boolean b) {
        LazyOptional<Integer> integerLazyOptional = storage.lazyMap(x -> x.extractEnergy(i, b));
        return integerLazyOptional.orElse(0);
    }

    @Override
    public int getEnergyStored() {
        return storage.lazyMap(FEnergyStorage::getEnergy).orElse(0);
    }

    @Override
    public int getMaxEnergyStored() {
        return storage.lazyMap(FEnergyStorage::getMaxEnergyStored).orElse(0);
    }

    @Override
    public boolean canExtract() {
        return storage.lazyMap(EnergyStorage::canExtract).orElse(false);
    }

    @Override
    public boolean canReceive() {
        return storage.lazyMap(EnergyStorage::canReceive).orElse(false);
    }

    public void setCapacity(int capacity) {
        storage.lazyMap(x -> x.setCapacity(capacity));
    }
    //?} else {

    /*@Override
    public int receiveEnergy(int i, boolean b) {
        return storage.receiveEnergy(i, b);
    }

    @Override
    public int extractEnergy(int i, boolean b) {
        return storage.extractEnergy(i, b);
    }

    @Override
    public int getEnergyStored() {
        return storage.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored() {
        return storage.getMaxEnergyStored();
    }

    @Override
    public boolean canExtract() {
        return storage.canExtract();
    }

    @Override
    public boolean canReceive() {
        return storage.canReceive();
    }
    public EnergyWrapper withCallback(Consumer<FEnergyStorage> callback){
        this.storage.callback(callback);
        return this;
    }

    public void setCapacity(int capacity) {
        storage.setCapacity(capacity);
    }

    *///?}
}
