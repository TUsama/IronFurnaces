package ironfurnaces.tileentity.furnaces.handler;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ironfurnaces.tileentity.furnaces.BlockIronFurnaceTileBaseV2;
import ironfurnaces.tileentity.furnaces.FurnaceMode;

public class GeneratorLitHandler implements IFurnaceLitHandler{
    public static final GeneratorLitHandler INSTANCE = new GeneratorLitHandler(null);
    public static final MapCodec<GeneratorLitHandler> CODEC = MapCodec.unit(INSTANCE);

    public static final String TYPE = "generator";

    public GeneratorLitHandler(BlockIronFurnaceTileBaseV2 tile) {
    }

    @Override
    public void tick(BlockIronFurnaceTileBaseV2 tile) {

    }

    @Override
    public void refresh(BlockIronFurnaceTileBaseV2 tile) {

    }

    @Override
    public boolean isLit(BlockIronFurnaceTileBaseV2 tile) {
        return tile.getMode().equals(FurnaceMode.GENERATOR) && !tile.getInstanceManager().isBlocking();
    }

    @Override
    public String getType() {
        return TYPE;
    }

}
