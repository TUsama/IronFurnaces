//? <1.21.11{
package ironfurnaces.blocks.furnaces;

import ironfurnaces.Config;
import ironfurnaces.registration.LegacyFurnaceBlocks;
import ironfurnaces.registration.ModBlockState;
import ironfurnaces.registration.ModBlocks;
import ironfurnaces.registration.ModItems;
import ironfurnaces.tileentity.furnaces.BlockIronFurnaceTileBase;
import ironfurnaces.tileentity.furnaces.UnifiedTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import javax.annotation.Nullable;

public class BlockMillionFurnace extends BlockIronFurnaceBase {

    public static final String ID = "million_furnace";
    private static final BooleanProperty RAINBOW_GENERATING = BooleanProperty.create("rainbow");


    public BlockMillionFurnace(Properties properties) {
        super(properties);
    }
    public BlockEntity newBlockEntity(BlockPos p_153277_, BlockState p_153278_) {
        return new UnifiedTileEntity(ModBlocks.asBlockEntityType(LegacyFurnaceBlocks.MILLION_FURNACE), p_153277_, p_153278_, Config.millionFurnaceSpeed, Config.millionFurnaceTier, Config.millionFurnaceGeneration,BlockMillionFurnace.ID);    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource rand) {
        if (world.getBlockEntity(pos) != null && world.getBlockEntity(pos) instanceof BlockIronFurnaceTileBase base && base.isRainbowFurnace())
        {
            if (base.getItem(BlockIronFurnaceTileBase.AUGMENT_BLUE).getItem() == ModItems.GENERATOR_AUGMENT.get())
            {
                if (state.getValue(BlockMillionFurnace.RAINBOW_GENERATING)) {
                    for (Direction direction : Direction.values()) {
                        if (Direction.from3DDataValue(direction.get3DDataValue()) != Direction.UP
                                && Direction.from3DDataValue(direction.get3DDataValue()) != Direction.DOWN) {
                            double d0 = (double) pos.getX() + 0.5D;
                            double d1 = (double) pos.getY();
                            double d2 = (double) pos.getZ() + 0.5D;
                            Direction.Axis direction$axis = direction.getAxis();
                            double d3 = 0.52D;
                            double d4 = rand.nextDouble() * 0.6D - 0.3D;
                            double d5 = direction$axis == Direction.Axis.X ? (double) direction.getStepX() * 0.52D : d4;
                            double d6 = rand.nextDouble() * 6.0D / 16.0D;
                            double d7 = direction$axis == Direction.Axis.Z ? (double) direction.getStepZ() * 0.52D : d4;

                            for (int i = 0; i < 10; i++) {
                                world.addParticle(ParticleTypes.CRIT, d0 + d5, d1 + d6, d2 + d7, rand.nextGaussian() * 0.05D, 0.0D, rand.nextGaussian() * 0.05D);
                                //~ if >1.20.1 'AMBIENT_ENTITY_EFFECT' -> 'EFFECT'
                                world.addParticle(ParticleTypes.AMBIENT_ENTITY_EFFECT, d0 + d5, d1 + d6, d2 + d7, rand.nextGaussian() * 0.05D, 0.0D, rand.nextGaussian() * 0.05D);
                            }
                        }
                    }
                }
            }

        }
        super.animateTick(state, world, pos, rand);

    }

    public static boolean isGenerating(BlockState state){
        return state.getValue(RAINBOW_GENERATING);
    }

    public static BlockState modifyGenerating(BlockState state, boolean b){
        state.setValue(RAINBOW_GENERATING, b);
        return state;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createFurnaceTicker(level, type, ModBlocks.asBlockEntityType(LegacyFurnaceBlocks.MILLION_FURNACE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.LIT, ModBlockState.TYPE, ModBlockState.JOVIAL, RAINBOW_GENERATING);
    }
}

//?}