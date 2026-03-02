package ironfurnaces.tileentity.furnaces.handler;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ironfurnaces.tileentity.furnaces.BlockIronFurnaceTileBaseV2;
import ironfurnaces.tileentity.furnaces.cache.FuelCache;

public class EnergyLitHandler implements IFurnaceLitHandler{
    public static final MapCodec<EnergyLitHandler> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.INT.fieldOf("costPerTick").forGetter(x -> x.costPerTick),
                    Codec.BOOL.fieldOf("isList").forGetter(x -> x.isLit)
            ).apply(instance, EnergyLitHandler::new));

    private int costPerTick;
    private boolean isLit;
    public static final String TYPE = "energy";

    public EnergyLitHandler(BlockIronFurnaceTileBaseV2 tile) {
        this.costPerTick = tile.getAugments().getCurrentModifiers().energyWorkCostModifier().applyAsInt(20);
    }

    private EnergyLitHandler(int costPerTick, boolean isLit) {
        this.costPerTick = costPerTick;
        this.isLit = isLit;
    }

    @Override
    public void tick(BlockIronFurnaceTileBaseV2 tile) {
        FuelCache fuel = tile.getFuel();
        if (!tile.getInstanceManager().isBlocking() && fuel.getEnergyStored() >= costPerTick){
            fuel.extractEnergy(costPerTick, false);
            isLit = true;
        } else {
            isLit = false;
        }
    }

    @Override
    public void refresh(BlockIronFurnaceTileBaseV2 tile) {

    }

    @Override
    public boolean isLit(BlockIronFurnaceTileBaseV2 tile) {
        return isLit;
    }

    @Override
    public String getType() {
        return TYPE;
    }


}
