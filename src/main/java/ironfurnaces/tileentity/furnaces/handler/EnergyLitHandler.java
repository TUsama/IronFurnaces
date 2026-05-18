package ironfurnaces.tileentity.furnaces.handler;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.cache.FuelCache;
import ironfurnaces.tileentity.furnaces.pattern.IFurnaceStats;
import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.transfer.transaction.Transaction;

@Getter(value = AccessLevel.PRIVATE)
public class EnergyLitHandler implements IFurnaceLitHandler{
    public static final MapCodec<EnergyLitHandler> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.BOOL.fieldOf("isLit").forGetter(EnergyLitHandler::isLit)
            ).apply(instance, EnergyLitHandler::new));

    private boolean isLit;
    public static final String TYPE = "energy";

    public EnergyLitHandler() {
        this(false);
    }

    private EnergyLitHandler(boolean isLit) {
        this.isLit = isLit;
    }

    @Override
    public void tick(FurnacePatternBlockEntity tile) {
        FuelCache fuel = tile.getFuel();
        int cost = tile.getAugments().getCurrentModifiers().energyWorkCostModifier().applyAsInt(tile.usedStats.energyConsumerPerTick());
        boolean b = tile.getInstanceManager().needLit(tile);
        if (b && tile.getInstanceManager().hasInstances() && fuel.getAmountAsLong() >= cost){
            try (var tx = Transaction.openRoot()) {
                fuel.extract(cost, tx);
                tx.commit();
            }

            isLit = true;
        } else {
            isLit = false;
        }
        ensureLitState(tile);
    }

    @Override
    public void refresh(FurnacePatternBlockEntity tile) {

    }

    @Override
    public boolean isLit(FurnacePatternBlockEntity tile) {
        return isLit;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public int getLitDuration() {
        return isLit ? 1 : 0;
    }

    @Override
    public int getLitTime() {
        return isLit ? 1 : 0;
    }



}
