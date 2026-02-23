package ironfurnaces.tileentity.furnaces.handler;

import ironfurnaces.tileentity.furnaces.BlockIronFurnaceTileBaseV2;
import ironfurnaces.tileentity.furnaces.cache.FuelCache;

public class EnergyLitHandler implements IFurnaceLitHandler{
    private final BlockIronFurnaceTileBaseV2 tile;
    private int costPerTick;
    private boolean isLit;

    public EnergyLitHandler(BlockIronFurnaceTileBaseV2 tile) {
        this.tile = tile;
        this.costPerTick = tile.getAugments().getCurrentModifiers().energyWorkCostModifier().applyAsInt(20);
    }

    @Override
    public void tick() {
        FuelCache fuel = tile.getFuel();
        if (!tile.getInstanceManager().isBlocking() && fuel.getEnergyStored() >= costPerTick){
            fuel.extractEnergy(costPerTick, false);
            isLit = true;
        } else {
            isLit = false;
        }
    }

    @Override
    public void refresh() {

    }

    @Override
    public boolean isLit() {
        return isLit;
    }


}
