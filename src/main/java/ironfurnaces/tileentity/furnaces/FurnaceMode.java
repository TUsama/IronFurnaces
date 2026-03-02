package ironfurnaces.tileentity.furnaces;

import ironfurnaces.registration.ModBlockState;
import ironfurnaces.tileentity.furnaces.handler.EnergyLitHandler;
import ironfurnaces.tileentity.furnaces.handler.GeneratorLitHandler;
import ironfurnaces.tileentity.furnaces.handler.IFurnaceLitHandler;
import ironfurnaces.tileentity.furnaces.handler.ItemFuelLitHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;

public enum FurnaceMode implements StringRepresentable {
    FURNACE("furnace", ItemFuelLitHandler::new),
    GENERATOR("generator", EnergyLitHandler::new),
    FACTORY("factory", GeneratorLitHandler::new);
    public final String name;
    public final Function<BlockIronFurnaceTileBaseV2, IFurnaceLitHandler> litHandlerSelector;

    FurnaceMode(String name, Function<BlockIronFurnaceTileBaseV2, IFurnaceLitHandler> litHandlerSelector) {
        this.name = name;
        this.litHandlerSelector = litHandlerSelector;
    }

    public void setFurnaceModeBlockState(BlockPos pos, BlockState state, Level level){
        if (!state.getValue(ModBlockState.FURNACE_MODE).equals(this.ordinal())) {
            level.setBlock(pos, state.setValue(ModBlockState.FURNACE_MODE, this), 3);
        }
    }

    @Override
    public String getSerializedName() {
        return name;
    }

}
