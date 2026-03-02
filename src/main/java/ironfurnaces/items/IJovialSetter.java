package ironfurnaces.items;

import ironfurnaces.blocks.furnaces.new_furnace.AbstractFurnacePatternHolder;
import ironfurnaces.registration.ModBlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface IJovialSetter {
    JovialState getJovialState();

    default boolean setJovial(Level level, BlockPos pos){
        var state = level.getBlockState(pos);
        if (state.getBlock() instanceof AbstractFurnacePatternHolder){
            state.setValue(ModBlockState.JOVIAL_STATE, getJovialState());
            return true;
        }
        return false;
    }

    static void clearJovial(Level level, BlockPos pos){
        var state = level.getBlockState(pos);
        if (state.getBlock() instanceof AbstractFurnacePatternHolder){
            state.setValue(ModBlockState.JOVIAL_STATE, JovialState.NONE);
        }
    }
}
