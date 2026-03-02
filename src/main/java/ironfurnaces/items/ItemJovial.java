package ironfurnaces.items;

import ironfurnaces.blocks.furnaces.new_furnace.AbstractFurnacePatternHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public abstract class ItemJovial extends Item implements IJovialSetter{
    public ItemJovial(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        BlockState blockState = level.getBlockState(clickedPos);
        if (blockState.getBlock() instanceof AbstractFurnacePatternHolder) {
            setJovial(level, clickedPos);
            return InteractionResult.CONSUME;
        }
        return super.useOn(context);
    }
}
