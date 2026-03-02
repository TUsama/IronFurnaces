package ironfurnaces.tileentity.furnaces.handler;


import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import ironfurnaces.tileentity.furnaces.BlockIronFurnaceTileBaseV2;

import java.util.Map;

public interface IFurnaceLitHandler {
    Codec<IFurnaceLitHandler> CODEC = Codec.STRING.dispatch(IFurnaceLitHandler::getType, string ->
            {
                var codec = switch (string) {
                    case ItemFuelLitHandler.TYPE -> ItemFuelLitHandler.CODEC;
                    case EnergyLitHandler.TYPE -> EnergyLitHandler.CODEC;
                    default -> GeneratorLitHandler.CODEC;
                };
                return codec
                        //? 1.20.1
                        .codec()
                        ;
            }
    );

    void tick(BlockIronFurnaceTileBaseV2 tile);

    void refresh(BlockIronFurnaceTileBaseV2 tile);
    boolean isLit(BlockIronFurnaceTileBaseV2 tile);
    String getType();
}
