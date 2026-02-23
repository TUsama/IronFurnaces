package ironfurnaces.tileentity.furnaces.handler;

import ironfurnaces.tileentity.furnaces.BlockIronFurnaceTileBaseV2;
import ironfurnaces.tileentity.furnaces.FurnaceMode;

public class GeneratorLitHandler implements IFurnaceLitHandler{
    private final BlockIronFurnaceTileBaseV2 tile;


    public GeneratorLitHandler(BlockIronFurnaceTileBaseV2 tile) {
        this.tile = tile;
    }

    @Override
    public void tick() {

    }

    @Override
    public void refresh() {

    }

    @Override
    public boolean isLit() {
        return tile.getMode().equals(FurnaceMode.GENERATOR) && !tile.getInstanceManager().isBlocking();
    }
}
