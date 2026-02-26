package ironfurnaces.tileentity.furnaces.setting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ironfurnaces.tileentity.furnaces.BlockIronFurnaceTileBaseV2;
import lombok.With;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.EmptyHandler;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@With
public record FurnaceSettingsV2(EnumMap<Direction, IOMode> IOSetting, boolean autoInput, boolean autoOutput,
                                RedStoneMode redStoneMode, int substractionNumber, boolean augmentGui,
                                boolean autoSplit) {
    public static final Codec<FurnaceSettingsV2> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.unboundedMap(Direction.CODEC, IOMode.CODEC).xmap(EnumMap::new, HashMap::new).fieldOf("io_setting").forGetter(x -> x.IOSetting),
                    Codec.BOOL.fieldOf("auto_input").forGetter(x -> x.autoInput),
                    Codec.BOOL.fieldOf("auto_output").forGetter(x -> x.autoOutput),
                    RedStoneMode.CODEC.fieldOf("redstone_mode").forGetter(x -> x.redStoneMode),
                    Codec.INT.fieldOf("substraction_number").forGetter(x -> x.substractionNumber),
                    Codec.BOOL.fieldOf("augment_gui").forGetter(x -> x.augmentGui),
                    Codec.BOOL.fieldOf("auto_split").forGetter(x -> x.autoSplit)
    ).apply(instance, FurnaceSettingsV2::new)
);
    public static final FurnaceSettingsV2 DEFAULT = Util.make(() -> {
        EnumMap<Direction, IOMode> directionIOModeEnumMap = new EnumMap<>(Map.of(
                Direction.UP, IOMode.INPUT,
                Direction.DOWN, IOMode.OUTPUT,
                Direction.EAST, IOMode.FUEL,
                Direction.WEST, IOMode.FUEL,
                Direction.NORTH, IOMode.FUEL,
                Direction.SOUTH, IOMode.FUEL
        ));
        return new FurnaceSettingsV2(directionIOModeEnumMap, false, false, RedStoneMode.IGNORE, 0, false, false);
    });

    public FurnaceSettingsV2 withDirectionChanged(Direction direction, IOMode ioMode){
        EnumMap<Direction, IOMode> directionIOModeEnumMap = new EnumMap<>(this.IOSetting);
        directionIOModeEnumMap.put(direction, ioMode);
        return this.withIOSetting(directionIOModeEnumMap);
    }


    public void onChanged() {

    }

    public enum IOMode implements StringRepresentable{
        NONE("none", x -> ((IItemHandlerModifiable) EmptyHandler.INSTANCE)),
        INPUT("input", BlockIronFurnaceTileBaseV2::getInput),
        OUTPUT("output", BlockIronFurnaceTileBaseV2::getAllOutput),
        FUEL("fuel", BlockIronFurnaceTileBaseV2::getFuel),
        ALL("all", BlockIronFurnaceTileBaseV2::getAllInv);
        public static final EnumCodec<IOMode> CODEC = StringRepresentable.fromEnum(IOMode::values);
        public final String name;
        public final Function<BlockIronFurnaceTileBaseV2, IItemHandlerModifiable> handlerSelector;

        IOMode(String name, Function<BlockIronFurnaceTileBaseV2, IItemHandlerModifiable> handlerSelector) {
            this.name = name;
            this.handlerSelector = handlerSelector;

        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }

    public enum RedStoneMode implements StringRepresentable{
        IGNORE("ignore"),
        HIGH_SIGNAL("high_signal"),
        LOW_SIGNAL("low_signal"),
        COMPARATOR("comparator"),
        COMPARATOR_SUBSTRACTION("comparator_substraction");
        public final String name;

        RedStoneMode(String name) {
            this.name = name;
        }

        public static final EnumCodec<RedStoneMode> CODEC = StringRepresentable.fromEnum(RedStoneMode::values);

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}