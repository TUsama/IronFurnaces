//? <1.21.11{
package ironfurnaces.blocks.furnaces;

import ironfurnaces.Config;
import ironfurnaces.registration.LegacyFurnaceBlocks;
import ironfurnaces.registration.ModBlocks;
import ironfurnaces.tileentity.furnaces.UnifiedTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class BlockObsidianFurnace extends BlockIronFurnaceBase {

    public static final String ID = "obsidian_furnace";

    public BlockObsidianFurnace(Properties properties) {
        super(properties);
    }

    @Override
    public String getId() {
        return ID;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new UnifiedTileEntity(ModBlocks.asBlockEntityType(LegacyFurnaceBlocks.OBSIDIAN_FURNACE), p_153215_, p_153216_, Config.obsidianFurnaceSpeed, Config.obsidianFurnaceTier, Config.obsidianFurnaceGeneration, BlockObsidianFurnace.ID);    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createFurnaceTicker(level, type, ModBlocks.asBlockEntityType(LegacyFurnaceBlocks.OBSIDIAN_FURNACE));
    }
}

//?}