package ironfurnaces.adaptor.energy;

import net.neoforged.neoforge.transfer.energy.DelegatingEnergyHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;

import java.util.function.Consumer;

public class EnergyWrapper
        extends DelegatingEnergyHandler {

    public EnergyWrapper(int capacity) {
        super(new FEnergyStorage(capacity));
    }

    public EnergyWrapper(int capacity, int maxTransfer) {
        super(new FEnergyStorage(capacity, maxTransfer));
    }

    public EnergyWrapper(int capacity, int maxReceive, int maxExtract) {
        super(new FEnergyStorage(capacity, maxReceive, maxExtract));
    }


    public void setCapacity(int capacity) {
        if (delegate instanceof FEnergyStorage fEnergyStorage) fEnergyStorage.setCapacity(capacity);
    }

    public void setMaxReceive(int maxReceive) {
        if (delegate instanceof FEnergyStorage fEnergyStorage) fEnergyStorage.setMaxReceive(maxReceive);
    }

    public void setMaxExtract(int maxExtract) {
        if (delegate instanceof FEnergyStorage fEnergyStorage) fEnergyStorage.setMaxExtract(maxExtract);
    }

    public EnergyWrapper withCallback(Consumer<FEnergyStorage> callback){
        ((FEnergyStorage) this.delegate).callback(callback);
        return this;
    }

}
