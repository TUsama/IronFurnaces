package ironfurnaces.items;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ironfurnaces.tileentity.furnaces.BlockIronFurnaceTileBaseV2;
import ironfurnaces.tileentity.furnaces.setting.FurnaceSettingsV2;
import ironfurnaces.util.DirectionUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        if (stack.hasTag() && stack.getTag().contains(WHOLE_KEY)) {
            StreamSetting.CODEC.parse(NbtOps.INSTANCE, stack.getTag().get(WHOLE_KEY))
                    .result()
                    .ifPresent(x -> {
                        x.settingsV2.IOSetting().forEach((direction, ioMode) -> {
                            tooltip.add(Component.translatable("ironfurnaces.item.item_copy.setting.direction." + direction.toString().toLowerCase(Locale.ROOT), Component.translatable(ioMode.translationKey))
                                    .withStyle(ChatFormatting.GRAY));

                        });
                        MutableComponent enable = Component.translatable("ironfurnaces.item.item_copy.setting.setting_enable");
                        MutableComponent disable = Component.translatable("ironfurnaces.item.item_copy.setting.setting_disable");
                        Function<Boolean, Component> choose = b -> b ? enable : disable;

                        tooltip.add(Component.translatable("ironfurnaces.item.item_copy.setting.auto_input", choose.apply(x.settingsV2.autoInput())).withStyle(ChatFormatting.GRAY));
                        tooltip.add(Component.translatable("ironfurnaces.item.item_copy.setting.auto_output", choose.apply(x.settingsV2.autoOutput())).withStyle(ChatFormatting.GRAY));
                        tooltip.add(Component.translatable("ironfurnaces.item.item_copy.setting.redstone_mode", Component.translatable(x.settingsV2.redStoneMode().translationKey)).withStyle(ChatFormatting.GRAY));
                        tooltip.add(Component.translatable("ironfurnaces.item.item_copy.setting.redstone_value", x.settingsV2.subtractionNumber()).withStyle(ChatFormatting.GRAY));
                        tooltip.add(Component.translatable("ironfurnaces.item.item_copy.setting.faced_direction", x.direction.toString().toLowerCase()).withStyle(ChatFormatting.GRAY));
                    });


        }
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("ironfurnaces.item.item_copy.usage.1").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("ironfurnaces.item.item_copy.usage.2").withStyle(ChatFormatting.GRAY));
    }


    @Override
    public InteractionResult useOn(UseOnContext ctx) {

        Level world = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        Player player = ctx.getPlayer();
        ItemStack copyItem = ctx.getItemInHand();
        if (!world.isClientSide && world.getBlockEntity(pos) instanceof BlockIronFurnaceTileBaseV2 v2 && !player.isCrouching()) {
            CompoundTag tag = copyItem.getTag();
            BlockState blockState = v2.getBlockState();
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

            } else {
                StreamSetting.CODEC.encodeStart(NbtOps.INSTANCE, new StreamSetting(v2.getSettingsV2(), blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)))
                        .resultOrPartial(string -> player.sendSystemMessage(Component.translatable("item.ironfurnaces.item_copy.error_on_write")))
                        .ifPresent(x -> {
                            copyItem.getOrCreateTag().put(WHOLE_KEY, x);
                        });
            }
            world.markAndNotifyBlock(pos, world.getChunkAt(pos), world.getBlockState(pos).getBlock().defaultBlockState(), world.getBlockState(pos), 3, 3);
            player.sendSystemMessage(Component.translatable("ironfurnaces.item.item_copy.tip.setting_applied"));
        }

        return super.useOn(ctx);
    }

    private record StreamSetting(FurnaceSettingsV2 settingsV2, Direction direction) {
        public static final Codec<StreamSetting> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        FurnaceSettingsV2.CODEC.fieldOf(SETTING_KEY).forGetter(x -> x.settingsV2),
                        Direction.CODEC.fieldOf(DIRECTION_KEY).forGetter(x -> x.direction)
                ).apply(instance, StreamSetting::new));
    }
}
