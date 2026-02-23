package ironfurnaces.tileentity.furnaces;

import ironfurnaces.tileentity.furnaces.handler.EnergyLitHandler;
import ironfurnaces.tileentity.furnaces.handler.GeneratorLitHandler;
import ironfurnaces.tileentity.furnaces.handler.IFurnaceLitHandler;
import ironfurnaces.tileentity.furnaces.handler.ItemFuelLitHandler;

import java.util.function.Function;

public enum FurnaceMode {
    FURNACE(ItemFuelLitHandler::new),
    GENERATOR(EnergyLitHandler::new),
    FACTORY(GeneratorLitHandler::new);

    public final Function<BlockIronFurnaceTileBaseV2, IFurnaceLitHandler> litHandlerSelector;

    FurnaceMode(Function<BlockIronFurnaceTileBaseV2, IFurnaceLitHandler> litHandlerSelector) {
        this.litHandlerSelector = litHandlerSelector;
    }
}
