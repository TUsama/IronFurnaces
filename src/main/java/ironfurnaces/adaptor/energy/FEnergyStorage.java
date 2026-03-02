package ironfurnaces.adaptor.energy;

import lombok.Setter;
import lombok.With;
import lombok.experimental.Accessors;
import net.minecraftforge.energy.EnergyStorage;

import java.util.function.Consumer;

@Accessors(fluent = true, chain = true)
public class FEnergyStorage extends EnergyStorage {

    @Setter
    private Consumer<FEnergyStorage> callback;

    public FEnergyStorage(int capacity) {
        super(capacity);
    }

    public FEnergyStorage(int capacity, int maxTransfer) {
        super(capacity, maxTransfer);
    }

    public FEnergyStorage(int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
    }

    public FEnergyStorage(int capacity, int maxReceive, int maxExtract, int energy) {
        super(capacity, maxReceive, maxExtract, energy);
    }

    protected void onEnergyChanged() {
        if (callback != null) {
            callback.accept(this);
        }
    }

    public int getEnergy() {
        return this.getEnergyStored();
    }

    public void setEnergy(int energy) {
        int oldEnergy = this.energy;

        if (oldEnergy != energy){
            this.energy = energy;
            onEnergyChanged();
        }

        if (this.energy > capacity) {
            this.energy = capacity;
        } else if (this.energy < 0) {
            this.energy = 0;
        }


    }

    public int getCapacity() {
        return this.getMaxEnergyStored();
    }

    public EnergyStorage setCapacity(int capacity) {
        int old = this.capacity;
        if (old != capacity){
            this.capacity = capacity;
            onEnergyChanged();
        }

        if (energy > capacity) {
            energy = capacity;
        }

        return this;
    }

    public EnergyStorage setMaxTransfer(int maxTransfer) {

        setMaxReceive(maxTransfer);
        setMaxExtract(maxTransfer);
        return this;
    }

    public EnergyStorage setMaxReceive(int maxReceive) {

        this.maxReceive = maxReceive;
        return this;
    }

    public EnergyStorage setMaxExtract(int maxExtract) {

        this.maxExtract = maxExtract;
        return this;
    }
}
