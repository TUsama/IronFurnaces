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
                                RedStoneMode redStoneMode, int subtractionNumber, boolean augmentGui,
                                boolean autoSplit) {
    public static final Codec<FurnaceSettingsV2> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.unboundedMap(Direction.CODEC, IOMode.CODEC).xmap(EnumMap::new, HashMap::new).fieldOf("io_setting").forGetter(x -> x.IOSetting),
                    Codec.BOOL.fieldOf("auto_input").forGetter(x -> x.autoInput),
                    Codec.BOOL.fieldOf("auto_output").forGetter(x -> x.autoOutput),
                    RedStoneMode.CODEC.fieldOf("redstone_mode").forGetter(x -> x.redStoneMode),
                    Codec.INT.fieldOf("subtraction_number").forGetter(x -> x.subtractionNumber),
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
        NONE("none", x -> ((IItemHandlerModifiable) EmptyHandler.INSTANCE), "ironfurnaces.furnace_setting.io_mode.none"),
        INPUT("input", BlockIronFurnaceTileBaseV2::getInput, "ironfurnaces.furnace_setting.io_mode.none"),
        OUTPUT("output", BlockIronFurnaceTileBaseV2::getAllOutput, "ironfurnaces.furnace_setting.io_mode.input"),
        FUEL("fuel", BlockIronFurnaceTileBaseV2::getFuel, "ironfurnaces.furnace_setting.io_mode.output"),
        ALL("all", BlockIronFurnaceTileBaseV2::getAllInv, "ironfurnaces.furnace_setting.io_mode.all");
        public static final EnumCodec<IOMode> CODEC = StringRepresentable.fromEnum(IOMode::values);
        public final String name;
        public final Function<BlockIronFurnaceTileBaseV2, IItemHandlerModifiable> handlerSelector;
        public final String translationKey;


        IOMode(String name, Function<BlockIronFurnaceTileBaseV2, IItemHandlerModifiable> handlerSelector, String translationKey) {
            this.name = name;
            this.handlerSelector = handlerSelector;
            this.translationKey = translationKey;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }

    public enum RedStoneMode implements StringRepresentable{
        IGNORE("ignore", "ironfurnaces.furnace_setting.redstone_mode.ignore"),
        HIGH_SIGNAL("high_signal", "ironfurnaces.furnace_setting.redstone_mode.high_signal"),
        LOW_SIGNAL("low_signal", "ironfurnaces.furnace_setting.redstone_mode.low_signal"),
        COMPARATOR("comparator", "ironfurnaces.furnace_setting.redstone_mode.comparator"),
        COMPARATOR_SUBTRACTION("comparator_subtraction", "ironfurnaces.furnace_setting.redstone_mode.comparator_subtraction");
        public final String name;
        public final String translationKey;

        RedStoneMode(String name, String translationKey) {
            this.name = name;
            this.translationKey = translationKey;
        }

        public static final EnumCodec<RedStoneMode> CODEC = StringRepresentable.fromEnum(RedStoneMode::values);

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}