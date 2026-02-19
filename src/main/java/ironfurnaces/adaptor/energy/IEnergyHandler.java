package ironfurnaces.adaptor.energy;

public interface IEnergyHandler {
    int getEnergy();
    int getEnergyCapacity();
    void setEnergy(int energy);
    default void setMaxEnergy(){
        setEnergy(getEnergyCapacity());
    }
    default void removeEnergy(int energy){
        setEnergy(getEnergy() - energy);
    }
}
