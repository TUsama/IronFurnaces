package ironfurnaces.tileentity.furnaces.handler;


import com.mojang.serialization.Codec;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.cache.IPatternSensitive;
import ironfurnaces.tileentity.furnaces.pattern.FurnacePattern;

public interface IFurnaceLitHandler extends IPatternSensitive {
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

    void tick(FurnacePatternBlockEntity tile);

    void refresh(FurnacePatternBlockEntity tile);
    boolean isLit(FurnacePatternBlockEntity tile);
    String getType();
    int getLitDuration();
    int getLitTime();

    @Override
    default void updateFurnacePattern(FurnacePattern pattern){

    }
}
