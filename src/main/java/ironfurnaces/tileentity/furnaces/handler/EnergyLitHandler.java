package ironfurnaces.tileentity.furnaces.handler;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.cache.FuelCache;
import ironfurnaces.tileentity.furnaces.pattern.FurnacePattern;
import ironfurnaces.tileentity.furnaces.process.ProcessingInstanceManager;
import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
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
        Level level = tile.getLevel();
        BlockPos blockPos = tile.getBlockPos();
        if (isLit) {
            if (!level.getBlockState(blockPos).getValue(BlockStateProperties.LIT)) {
                level.setBlock(blockPos, level.getBlockState(blockPos).setValue(BlockStateProperties.LIT, true), 3);
            }
        } else {
            if (level.getBlockState(blockPos).getValue(BlockStateProperties.LIT)) {
                level.setBlock(blockPos, level.getBlockState(blockPos).setValue(BlockStateProperties.LIT, false), 3);
            }
        }

        ProcessingInstanceManager instanceManager = tile.getInstanceManager();
        boolean flag = false;
        //确保至少有槽位不blocking
        for (int i = 0; i < tile.getInput().getSlots(); i++) {
            if (instanceManager.blockingIndexes().contains(i)) continue;
            flag = true;
            break;

        }
        int cost = tile.getAugments().getCurrentModifiers().energyWorkCostModifier().applyAsInt(tile.getPattern().energyConsumerPerTick());
        if (flag && tile.getInstanceManager().isWaiting() && fuel.getEnergyStored() >= cost){
            fuel.extractEnergy(cost, false);
            isLit = true;
        } else {
            isLit = false;
        }
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

    @Override
    public void updateFurnacePattern(FurnacePattern pattern) {

    }


}
