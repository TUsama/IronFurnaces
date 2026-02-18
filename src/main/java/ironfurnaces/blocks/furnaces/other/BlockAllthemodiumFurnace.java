package ironfurnaces.blocks.furnaces.other;

import ironfurnaces.Config;
import ironfurnaces.blocks.furnaces.BlockIronFurnaceBase;
import ironfurnaces.registration.LegacyFurnaceBlocks;
import ironfurnaces.registration.ModBlocks;
import ironfurnaces.tileentity.furnaces.LegacyUnifiedTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class BlockAllthemodiumFurnace extends BlockIronFurnaceBase {

    public static final String ID = "allthemodium_furnace";

    public BlockAllthemodiumFurnace(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new LegacyUnifiedTileEntity(ModBlocks.asBlockEntityType(LegacyFurnaceBlocks.ALLTHEMODIUM_FURNACE), p_153215_, p_153216_, Config.allthemodiumFurnaceSpeed, Config.allthemodiumFurnaceTier, Config.allthemodiumGeneration,BlockAllthemodiumFurnace.ID);       }

    @Override
    public String getBackgroundID() {
        return "furnace_allthemodium";
    }

    @Override
    public String getId() {
        return ID;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createFurnaceTicker(level, type, ModBlocks.asBlockEntityType(LegacyFurnaceBlocks.ALLTHEMODIUM_FURNACE));
    }
}
