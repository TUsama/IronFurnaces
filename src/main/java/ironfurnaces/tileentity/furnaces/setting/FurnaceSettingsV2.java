package ironfurnaces.tileentity.furnaces.setting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ironfurnaces.registration.ModDataComponents;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import lombok.With;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.EmptyHandler;

//? 1.20.1 {

//? } else {
/*import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
*///?}

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

import static net.minecraft.network.chat.Component.translatable;

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

    public ItemStack writeToStack(ItemStack stack) {
        //? 1.20.1 {
    CompoundTag beTag = stack.getOrCreateTagElement(BlockItem.BLOCK_ENTITY_TAG);
    FurnaceSettingsV2.CODEC.encodeStart(NbtOps.INSTANCE, this)
            .result()
            .ifPresent(tag -> beTag.put(FurnaceSettingsV2.NBT_KEY, tag));
    return stack;
    //? } else {
        /*// 先处理旧数据，主要目的是清掉历史遗留的 CUSTOM_DATA。
        // 最终写入值一定以当前 this 为准。
        migrateLegacySettingIfNeeded(stack);

        stack.set(ModDataComponents.PERSISTENT_SETTING.get(), this);
        removeLegacySettingData(stack);

        return stack;
        *///?}
    }

    public static FurnaceSettingsV2 getSettingFromStack(ItemStack stack) {
        //? 1.20.1 {
    CompoundTag beTag = stack.getTagElement(BlockItem.BLOCK_ENTITY_TAG);
    if (beTag == null || !beTag.contains(FurnaceSettingsV2.NBT_KEY, CompoundTag.TAG_COMPOUND)) {
        DEFAULT.writeToStack(stack);
        return DEFAULT;
    }
    return FurnaceSettingsV2.CODEC.parse(NbtOps.INSTANCE, beTag.getCompound(FurnaceSettingsV2.NBT_KEY))
            .result()
            .orElseGet(() -> {
                DEFAULT.writeToStack(stack);
                return DEFAULT;
            });
    //? } else {
        /*FurnaceSettingsV2 componentSetting = stack.get(ModDataComponents.PERSISTENT_SETTING.get());

        // 新组件存在时，新组件是权威数据。
        if (componentSetting != null) {
            removeLegacySettingData(stack);
            return componentSetting;
        }

        // 新组件不存在，但旧 CUSTOM_DATA 存在时，迁移旧数据。
        FurnaceSettingsV2 legacySetting = getLegacySetting(stack);
        if (legacySetting != null) {
            stack.set(ModDataComponents.PERSISTENT_SETTING.get(), legacySetting);
            removeLegacySettingData(stack);
            return legacySetting;
        }

        // 保留你原来的行为：没有任何设置时，写入 DEFAULT。
        DEFAULT.writeToStack(stack);
        return DEFAULT;
        *///?}
    }

    public static void removeSetting(ItemStack stack) {
        //? 1.20.1 {
    CompoundTag beTag = stack.getOrCreateTagElement(BlockItem.BLOCK_ENTITY_TAG);
    beTag.remove(FurnaceSettingsV2.NBT_KEY);
    //? } else {
        /*stack.remove(ModDataComponents.PERSISTENT_SETTING.get());
        removeLegacySettingData(stack);
        *///?}
    }

    //? >1.20.1 {
    /*private static void migrateLegacySettingIfNeeded(ItemStack stack) {
        FurnaceSettingsV2 componentSetting = stack.get(ModDataComponents.PERSISTENT_SETTING.get());
        FurnaceSettingsV2 legacySetting = getLegacySetting(stack);

        if (legacySetting == null) {
            return;
        }

        if (componentSetting == null) {
            stack.set(ModDataComponents.PERSISTENT_SETTING.get(), legacySetting);
        }

        removeLegacySettingData(stack);
    }

    @Nullable
    private static FurnaceSettingsV2 getLegacySetting(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null || !customData.contains(NBT_KEY)) {
            return null;
        }

        var tag = customData.copyTag().get(NBT_KEY);
        if (tag == null) {
            return null;
        }

        return CODEC.parse(NbtOps.INSTANCE, tag)
                .result()
                .orElse(null);
    }

    private static void removeLegacySettingData(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null || !customData.contains(NBT_KEY)) {
            return;
        }

        CompoundTag tag = customData.copyTag();
        tag.remove(NBT_KEY);

        if (tag.isEmpty()) {
            stack.remove(DataComponents.CUSTOM_DATA);
        } else {
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }
    }
*///?}

    public List<Component> toTooltips(){
        List<Component> tooltips = new ArrayList<>();
        tooltips.add(Component.literal(""));

        this.IOSetting().forEach((direction, ioMode) -> {
            tooltips.add(translatable(
                    "ironfurnaces.furnace_setting.direction." + direction.toString().toLowerCase(Locale.ROOT),
                    translatable(ioMode.translationKey).withStyle(ChatFormatting.GREEN)
            ).withStyle(ChatFormatting.GRAY));
        });

        MutableComponent enable = translatable("ironfurnaces.furnace_setting.setting_enable");
        MutableComponent disable = translatable("ironfurnaces.furnace_setting.setting_disable");
        Function<Boolean, MutableComponent> choose = b -> b ? enable.withStyle(ChatFormatting.GREEN) : disable.withStyle(ChatFormatting.RED);

        tooltips.add(translatable(
                "ironfurnaces.furnace_setting.auto_input",
                choose.apply(this.autoInput())
        ).withStyle(ChatFormatting.GRAY));

        tooltips.add(translatable(
                "ironfurnaces.furnace_setting.auto_output",
                choose.apply(this.autoOutput())
        ).withStyle(ChatFormatting.GRAY));

        tooltips.add(translatable(
                "ironfurnaces.furnace_setting.redstone_mode",
                translatable(this.redStoneMode().translationKey).withStyle(ChatFormatting.GREEN)
        ).withStyle(ChatFormatting.GRAY));

        tooltips.add(translatable(
                "ironfurnaces.furnace_setting.redstone_value",
                Component.literal(this.subtractionNumber() + "").withStyle(ChatFormatting.GREEN)
        ).withStyle(ChatFormatting.GRAY));

        tooltips.add(translatable(
                "ironfurnaces.furnace_setting.auto_fill",
                choose.apply(this.autoFill())
        ).withStyle(ChatFormatting.GRAY));

        return tooltips;
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