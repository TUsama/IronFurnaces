package ironfurnaces.tileentity.furnaces.setting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
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
                                RedStoneMode redStoneMode, int subtractionNumber,
                                boolean autoFill) {
    public static final Codec<FurnaceSettingsV2> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.unboundedMap(Direction.CODEC, IOMode.CODEC).xmap(EnumMap::new, HashMap::new).fieldOf("io_setting").forGetter(x -> x.IOSetting),
                    Codec.BOOL.fieldOf("auto_input").forGetter(x -> x.autoInput),
                    Codec.BOOL.fieldOf("auto_output").forGetter(x -> x.autoOutput),
                    RedStoneMode.CODEC.fieldOf("redstone_mode").forGetter(x -> x.redStoneMode),
                    Codec.INT.fieldOf("subtraction_number").forGetter(x -> x.subtractionNumber),
                    Codec.BOOL.fieldOf("auto_fill").forGetter(x -> x.autoFill)
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
        return new FurnaceSettingsV2(directionIOModeEnumMap, false, false, RedStoneMode.IGNORE, 0, false);
    });

    public static final String NBT_KEY = "furnace_setting";

    public FurnaceSettingsV2 withDirectionChanged(Direction direction, IOMode ioMode){
        EnumMap<Direction, IOMode> directionIOModeEnumMap = new EnumMap<>(this.IOSetting);
        directionIOModeEnumMap.remove(direction);
        directionIOModeEnumMap.put(direction, ioMode);
        return this.withIOSetting(directionIOModeEnumMap);
    }

    public IOMode getRelative(Direction blockFacing, RelativeFace relativeFace) {
        Direction worldSide = RelativeFaceHelper.toWorld(blockFacing, relativeFace);
        return this.IOSetting.get(worldSide);
    }

    public FurnaceSettingsV2 withRelativeChanged(Direction blockFacing, RelativeFace relativeFace, IOMode ioMode) {
        Direction worldSide = RelativeFaceHelper.toWorld(blockFacing, relativeFace);
        return this.withDirectionChanged(worldSide, ioMode);
    }

    public void onChanged() {

    }

    public static boolean isLowSignal(int signal) {
        return signal > 0 && signal <= 7;
    }

    public static boolean isHighSignal(int signal) {
        return signal >= 8;
    }

    public static boolean hasSignal(int signal) {
        return signal > 0;
    }

    public enum IOMode implements StringRepresentable{
        NONE("none", x -> ((IItemHandlerModifiable) EmptyHandler.INSTANCE), "ironfurnaces.furnace_setting.io_mode.none"),
        INPUT("input", FurnacePatternBlockEntity::getInput, "ironfurnaces.furnace_setting.io_mode.input"),
        OUTPUT("output", FurnacePatternBlockEntity::getAllOutput, "ironfurnaces.furnace_setting.io_mode.output"),
        INPUT_AND_OUTPUT("input_and_output", FurnacePatternBlockEntity::getInputAndOutput, "ironfurnaces.furnace_setting.io_mode.input_and_output"),
        FUEL("fuel", FurnacePatternBlockEntity::getFuel, "ironfurnaces.furnace_setting.io_mode.fuel"),
        ALL("all", FurnacePatternBlockEntity::getAllInvForAutomation, "ironfurnaces.furnace_setting.io_mode.all");
        public static final EnumCodec<IOMode> CODEC = StringRepresentable.fromEnum(IOMode::values);
        public final String name;
        public final Function<FurnacePatternBlockEntity, IItemHandlerModifiable> handlerSelector;
        public final String translationKey;


        IOMode(String name, Function<FurnacePatternBlockEntity, IItemHandlerModifiable> handlerSelector, String translationKey) {
            this.name = name;
            this.handlerSelector = handlerSelector;
            this.translationKey = translationKey;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        public IOMode next() {
            return values()[(this.ordinal() + 1) % values().length];
        }

        public IOMode previous() {
            return values()[Math.floorMod(this.ordinal() - 1, values().length)];
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

        public RedStoneMode next() {
            return values()[(this.ordinal() + 1) % values().length];
        }

        public RedStoneMode previous() {
            return values()[Math.floorMod(this.ordinal() - 1, values().length)];
        }
    }

    public enum RelativeFace implements StringRepresentable {
        FRONT("front", "ironfurnaces.furnace_setting.relative_face.front"),
        BACK("back", "ironfurnaces.furnace_setting.relative_face.back"),
        LEFT("left", "ironfurnaces.furnace_setting.relative_face.left"),
        RIGHT("right", "ironfurnaces.furnace_setting.relative_face.right"),
        UP("up", "ironfurnaces.furnace_setting.relative_face.up"),
        DOWN("down", "ironfurnaces.furnace_setting.relative_face.down");

        public static final EnumCodec<RelativeFace> CODEC =
                StringRepresentable.fromEnum(RelativeFace::values);

        public final String name;
        public final String translationKey;

        RelativeFace(String name, String translationKey) {
            this.name = name;
            this.translationKey = translationKey;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}