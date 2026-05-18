package ironfurnaces.items;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ironfurnaces.registration.ModDataComponents;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.setting.FurnaceSettingsV2;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.List;
import java.util.function.Consumer;

public class ItemFurnaceCopyV2 extends Item {


    public static final String WHOLE_KEY = "copied_setting";
    public static final String SETTING_KEY = "furnace_setting";
    public static final String DIRECTION_KEY = "direction_setting";

    public ItemFurnaceCopyV2(Properties properties) {
        super(properties);
    }



    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        if (ctx.getLevel().isClientSide()) return InteractionResult.SUCCESS;

        Level world = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        BlockState blockState = world.getBlockState(pos);
        Player player = ctx.getPlayer();
        ItemStack copyItem = ctx.getItemInHand();
        if (world.getBlockEntity(pos) instanceof FurnacePatternBlockEntity v2) {
            if (player.isCrouching()) {
                PackedSetting packedSetting = new PackedSetting(v2.getSettingsV2(), blockState.getValue(BlockStateProperties.HORIZONTAL_FACING));
                copyItem.set(ModDataComponents.PERSISTENT_STREAM_SETTING.get(), packedSetting);
                player.sendSystemMessage(Component.translatable("ironfurnaces.item.item_copy.tip.setting_copied"));


            } else {
                PackedSetting setting = null;

                if (copyItem.has(ModDataComponents.PERSISTENT_STREAM_SETTING)) {
                    setting = copyItem.get(ModDataComponents.PERSISTENT_STREAM_SETTING);

                }


                if (setting != null) {
                    v2.setWholeSettingV2(setting.settingsV2());
                    if (!blockState.getValue(BlockStateProperties.HORIZONTAL_FACING).equals(setting.direction)) {
                        world.setBlock(pos, blockState.setValue(BlockStateProperties.HORIZONTAL_FACING, setting.direction), 3);
                    }
                    player.sendSystemMessage(Component.translatable("ironfurnaces.item.item_copy.tip.setting_applied"));
                }

            }
            return InteractionResult.CONSUME;
        }

        return super.useOn(ctx);
    }

    public record PackedSetting(FurnaceSettingsV2 settingsV2, Direction direction) implements TooltipProvider {
        public static final Codec<PackedSetting> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        FurnaceSettingsV2.CODEC.fieldOf(SETTING_KEY).forGetter(PackedSetting::settingsV2),
                        Direction.CODEC.fieldOf(DIRECTION_KEY).forGetter(PackedSetting::direction)
                ).apply(instance, PackedSetting::new));

        @Override
        public void addToTooltip(TooltipContext tooltipContext, Consumer<Component> consumer, TooltipFlag tooltipFlag, DataComponentGetter dataComponentGetter) {
            for (Component tooltip : this.settingsV2.toTooltips()) {
                consumer.accept(tooltip);
            }

            consumer.accept(Component.translatable("ironfurnaces.furnace_setting.faced_direction", direction.toString()).withStyle(ChatFormatting.GRAY));
            consumer.accept(Component.literal(""));
            consumer.accept(Component.translatable("ironfurnaces.item.new_item_copy.usage.1").withStyle(ChatFormatting.GRAY));
            consumer.accept(Component.translatable("ironfurnaces.item.new_item_copy.usage.2").withStyle(ChatFormatting.GRAY));
        }
    }
}
