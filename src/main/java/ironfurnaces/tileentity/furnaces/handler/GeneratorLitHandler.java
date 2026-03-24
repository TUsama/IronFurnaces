package ironfurnaces.tileentity.furnaces.handler;

import com.mojang.serialization.MapCodec;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.FurnaceMode;
import ironfurnaces.tileentity.furnaces.cache.FuelCache;
import ironfurnaces.tileentity.furnaces.process.Generate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.List;

public class GeneratorLitHandler implements IFurnaceLitHandler{
    public static final GeneratorLitHandler INSTANCE = new GeneratorLitHandler();
    public static final MapCodec<GeneratorLitHandler> CODEC = MapCodec.unit(INSTANCE);
    private int litTime;
    private int litDuration;

    public static final String TYPE = "generator";


    @Override
    public void tick(FurnacePatternBlockEntity tile) {
        FuelCache fuel = tile.getFuel();
        Level level = tile.getLevel();
        BlockPos blockPos = tile.getBlockPos();
        List<Generate> allGenerateInstances = tile.getInstanceManager().getAllGenerateInstances();
        if (litTime != 0) {
            if (!level.getBlockState(blockPos).getValue(BlockStateProperties.LIT)) {
                level.setBlock(blockPos, level.getBlockState(blockPos).setValue(BlockStateProperties.LIT, true), 3);
            }
        } else {
            if (level.getBlockState(blockPos).getValue(BlockStateProperties.LIT)) {
                level.setBlock(blockPos, level.getBlockState(blockPos).setValue(BlockStateProperties.LIT, false), 3);
            }
        }

        if (allGenerateInstances.isEmpty()){
            litDuration = 0;
            litTime = 0;
        } else {
            float i = 0;
            for (Generate allGenerateInstance : allGenerateInstances) {
                i += allGenerateInstance.getDoneProgress();
            }
            //pure placeholder
            //don't care the real number actually
            //only for rendering the bar
            litDuration = 200;
            litTime = (int)Math.max(200.0f - (litDuration * (i / allGenerateInstances.size())), 0);
        }
    }

    @Override
    public void refresh(FurnacePatternBlockEntity tile) {

    }

    @Override
    public boolean isLit(FurnacePatternBlockEntity tile) {
        return tile.getMode().equals(FurnaceMode.GENERATOR) && tile.getInstanceManager().isGeneratingEnergy();
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public int getLitDuration() {
        return litDuration;
    }

    @Override
    public int getLitTime() {
        return litTime;
    }

}
