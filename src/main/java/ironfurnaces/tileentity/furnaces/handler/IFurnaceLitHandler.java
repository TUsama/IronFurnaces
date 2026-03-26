package ironfurnaces.tileentity.furnaces.handler;


import com.mojang.serialization.Codec;
import ironfurnaces.capability.rainbow.OwnerRainbowContextHelper;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.cache.IPatternSensitive;
import ironfurnaces.tileentity.furnaces.pattern.IFurnaceStats;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public interface IFurnaceLitHandler extends IPatternSensitive {
    Codec<IFurnaceLitHandler> CODEC = Codec.STRING.dispatch(IFurnaceLitHandler::getType, string ->
            {
                var codec = switch (string) {
                    case ItemFuelLitHandler.TYPE -> ItemFuelLitHandler.CODEC;
                    case EnergyLitHandler.TYPE -> EnergyLitHandler.CODEC;
                    default -> GeneratorLitHandler.CODEC;
                };
                return codec
                        //? 1.20.1
                        .codec()
                        ;
            }
    );

    void tick(FurnacePatternBlockEntity tile);

    void refresh(FurnacePatternBlockEntity tile);
    boolean isLit(FurnacePatternBlockEntity tile);
    String getType();
    int getLitDuration();
    int getLitTime();

    default void ensureLitState(FurnacePatternBlockEntity tile){
        Level level = tile.getLevel();
        BlockPos blockPos = tile.getBlockPos();
        if (isLit(tile)) {
            if (!level.getBlockState(blockPos).getValue(BlockStateProperties.LIT)) {
                level.setBlock(blockPos, level.getBlockState(blockPos).setValue(BlockStateProperties.LIT, true), 3);
                if (level instanceof ServerLevel serverLevel) OwnerRainbowContextHelper.markDirtyByOwnerUuid(serverLevel, tile.getOwnerUuid());
            }
        } else {
            if (level.getBlockState(blockPos).getValue(BlockStateProperties.LIT)) {
                level.setBlock(blockPos, level.getBlockState(blockPos).setValue(BlockStateProperties.LIT, false), 3);
                if (level instanceof ServerLevel serverLevel) OwnerRainbowContextHelper.markDirtyByOwnerUuid(serverLevel, tile.getOwnerUuid());
            }
        }
    }

    @Override
    default void updateFurnacePatternStats(IFurnaceStats<?> stats, FurnacePatternBlockEntity blockEntity){

    }
}
