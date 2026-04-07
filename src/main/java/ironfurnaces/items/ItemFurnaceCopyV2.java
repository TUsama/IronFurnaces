package ironfurnaces.items;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ironfurnaces.registration.ModDataComponents;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.setting.FurnaceSettingsV2;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;



import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

public class ItemFurnaceCopyV2 extends Item {


    public static final String WHOLE_KEY = "copied_setting";
    public static final String SETTING_KEY = "furnace_setting";
    public static final String DIRECTION_KEY = "direction_setting";

    public ItemFurnaceCopyV2(Properties properties) {
        super(properties);
    }

    
    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        //? 1.20.1 {
        if (stack.hasTag() && stack.getTag().contains(WHOLE_KEY)) {
            StreamSetting.CODEC.parse(NbtOps.INSTANCE, stack.getTag().get(WHOLE_KEY))
                    .result()
                    .ifPresent(x -> {
                        x.settingsV2.IOSetting().forEach((direction, ioMode) -> {
                            tooltip.add(Component.translatable("ironfurnaces.furnace_setting.direction." + direction.toString().toLowerCase(Locale.ROOT), Component.translatable(ioMode.translationKey))
                                    .withStyle(ChatFormatting.GRAY));

                        });
                        MutableComponent enable = Component.translatable("ironfurnaces.furnace_setting.setting_enable");
                        MutableComponent disable = Component.translatable("ironfurnaces.furnace_setting.setting_disable");
                        Function<Boolean, Component> choose = b -> b ? enable : disable;

                        tooltip.add(Component.translatable("ironfurnaces.furnace_setting.auto_input", choose.apply(x.settingsV2.autoInput())).withStyle(ChatFormatting.GRAY));
                        tooltip.add(Component.translatable("ironfurnaces.furnace_setting.auto_output", choose.apply(x.settingsV2.autoOutput())).withStyle(ChatFormatting.GRAY));
                        tooltip.add(Component.translatable("ironfurnaces.furnace_setting.redstone_mode", Component.translatable(x.settingsV2.redStoneMode().translationKey)).withStyle(ChatFormatting.GRAY));
                        tooltip.add(Component.translatable("ironfurnaces.furnace_setting.redstone_value", x.settingsV2.subtractionNumber()).withStyle(ChatFormatting.GRAY));
                        tooltip.add(Component.translatable("ironfurnaces.furnace_setting.faced_direction", x.direction.toString().toLowerCase()).withStyle(ChatFormatting.GRAY));
                    });


        }
        //? } else {


        //?}

        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("ironfurnaces.item.item_copy.usage.1").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("ironfurnaces.item.item_copy.usage.2").withStyle(ChatFormatting.GRAY));
    }


    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        if (ctx.getLevel().isClientSide) return InteractionResult.PASS;

        Level world = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        BlockState blockState = world.getBlockState(pos);
        Player player = ctx.getPlayer();
        ItemStack copyItem = ctx.getItemInHand();
        if (world.getBlockEntity(pos) instanceof FurnacePatternBlockEntity v2) {
            StreamSetting streamSetting = new StreamSetting(v2.getSettingsV2(), blockState.getValue(BlockStateProperties.HORIZONTAL_FACING));
            if (player.isCrouching()) {
                //? 1.20.1 {
                StreamSetting.CODEC.encodeStart(NbtOps.INSTANCE, streamSetting)
                        .resultOrPartial(string -> player.sendSystemMessage(Component.translatable("item.ironfurnaces.item_copy.error_on_write")))
                        .ifPresent(x -> {
                            copyItem.getOrCreateTag().put(WHOLE_KEY, x);
                            player.sendSystemMessage(Component.translatable("ironfurnaces.item.item_copy.tip.setting_copied"));

                        });
                //? } else {
                /*copyItem.set(ModDataComponents.PERSISTENT_STREAM_SETTING.get(), streamSetting);
                player.sendSystemMessage(Component.translatable("ironfurnaces.item.item_copy.tip.setting_copied"));
                *///?}

            } else {
                //? 1.20.1 {
                CompoundTag tag = copyItem.getTag();
                if (tag != null && tag.contains(WHOLE_KEY)) {
                    StreamSetting.CODEC.parse(NbtOps.INSTANCE, tag.get(WHOLE_KEY))
                            .resultOrPartial((string -> player.sendSystemMessage(Component.translatable("item.ironfurnaces.item_copy.error_on_parse"))))
                            .ifPresentOrElse(x -> {
                                v2.setWholeSettingV2(x.settingsV2());
                                if (blockState.getValue(BlockStateProperties.HORIZONTAL_FACING).equals(x.direction)) {
                                    blockState.setValue(BlockStateProperties.HORIZONTAL_FACING, x.direction);
                                }
                                player.sendSystemMessage(Component.translatable("ironfurnaces.item.item_copy.tip.setting_applied"));
                            }, () -> {
                                copyItem.getTag().remove(WHOLE_KEY);
                            });
                            }
                //? } else {
                    /*if (copyItem.has(ModDataComponents.PERSISTENT_STREAM_SETTING)) {
                        var s = copyItem.get(ModDataComponents.PERSISTENT_STREAM_SETTING);
                        v2.setWholeSettingV2(s.settingsV2());
                        if (blockState.getValue(BlockStateProperties.HORIZONTAL_FACING).equals(s.direction)) {
                            blockState.setValue(BlockStateProperties.HORIZONTAL_FACING, s.direction);
                        }
                        player.sendSystemMessage(Component.translatable("ironfurnaces.item.item_copy.tip.setting_applied"));
                    }


                *///?}

                    world.setBlock(pos, blockState, 3);
                    player.sendSystemMessage(Component.translatable("ironfurnaces.item.item_copy.tip.setting_applied"));

            }
            return InteractionResult.CONSUME;
        }

        return super.useOn(ctx);
    }

    public record StreamSetting(FurnaceSettingsV2 settingsV2, Direction direction){
        public static final Codec<StreamSetting> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        FurnaceSettingsV2.CODEC.fieldOf(SETTING_KEY).forGetter(x -> x.settingsV2),
                        Direction.CODEC.fieldOf(DIRECTION_KEY).forGetter(x -> x.direction)
                ).apply(instance, StreamSetting::new));
    }
}
