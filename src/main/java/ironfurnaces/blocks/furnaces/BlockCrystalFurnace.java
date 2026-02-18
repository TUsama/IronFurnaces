package ironfurnaces.blocks.furnaces;

import ironfurnaces.Config;
import ironfurnaces.registration.LegacyFurnaceBlocks;
import ironfurnaces.registration.ModBlocks;
import ironfurnaces.registration.ModItems;
import ironfurnaces.tileentity.furnaces.BlockIronFurnaceTileBase;
import ironfurnaces.tileentity.furnaces.LegacyUnifiedTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class BlockCrystalFurnace extends BlockIronFurnaceBase implements SimpleWaterloggedBlock {

    public static final String ID = "crystal_furnace";
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public BlockCrystalFurnace(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(BlockStateProperties.LIT, false).setValue(TYPE, 0).setValue(JOVIAL, 0).setValue(WATERLOGGED, Boolean.valueOf(false)));
    }
    @Override
    public String getId() {
        return ID;
    }
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createFurnaceTicker(level, type, ModBlocks.asBlockEntityType(LegacyFurnaceBlocks.CRYSTAL_FURNACE));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        FluidState fluidState = ctx.getLevel().getFluidState(ctx.getClickedPos());
        return (BlockState) this.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, ctx.getHorizontalDirection().getOpposite()).setValue(WATERLOGGED, Boolean.valueOf(fluidState.getType() == Fluids.WATER));
    }

    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource rand) {
        double d0 = (double) pos.getX() + 0.5D;
        double d1 = (double) pos.getY();
        double d2 = (double) pos.getZ() + 0.5D;

        Direction direction = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        Direction.Axis direction$axis = direction.getAxis();
        double d3 = 0.52D;
        double d4 = rand.nextDouble() * 0.6D - 0.3D;
        double d5 = direction$axis == Direction.Axis.X ? (double) direction.getStepX() * 0.52D : d4;
        double d6 = rand.nextDouble() * 6.0D / 16.0D;
        double d7 = direction$axis == Direction.Axis.Z ? (double) direction.getStepZ() * 0.52D : d4;
        world.addParticle(ParticleTypes.PORTAL, d0 + d5, d1 + d6 - 0.5D, d2 + d7, 0.0D, 0.0D, 0.0D);
        world.addParticle(ParticleTypes.PORTAL, d0 + d5, d1 + d6 - 0.5D, d2 + d7, 0.0D, 0.0D, 0.0D);

        if (world.getBlockEntity(pos) == null)
        {
            return;
        }
        if (!(world.getBlockEntity(pos) instanceof BlockIronFurnaceTileBase))
        {
            return;
        }
        BlockIronFurnaceTileBase tile = ((BlockIronFurnaceTileBase) world.getBlockEntity(pos));
        if (tile.getItem(3).getItem() == ModItems.SMOKING_AUGMENT.get()) {
            double lvt_5_1_ = (double) pos.getX() + 0.5D;
            double lvt_7_1_ = (double) pos.getY();
            double lvt_9_1_ = (double) pos.getZ() + 0.5D;

            world.addParticle(ParticleTypes.PORTAL, lvt_5_1_, lvt_7_1_ + 1.1D, lvt_9_1_, 0.0D, 0.0D, 0.0D);
        }

        super.animateTick(state, world, pos, rand);
    }

    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (stateIn.getValue(WATERLOGGED)) {
            worldIn.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
        }
        return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(WATERLOGGED));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new LegacyUnifiedTileEntity(ModBlocks.asBlockEntityType(LegacyFurnaceBlocks.CRYSTAL_FURNACE), p_153215_, p_153216_, Config.crystalFurnaceSpeed, Config.crystalFurnaceTier, Config.crystalFurnaceGeneration,BlockCrystalFurnace.ID);
    }
}
