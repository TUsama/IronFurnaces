package ironfurnaces.items.augments;

import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.registration.ModDataComponents;
import ironfurnaces.registration.data_component.AugmentItemInfo;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public class ItemAugment extends Item {

    public ItemAugment(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, context, display, builder, tooltipFlag);
        builder.accept(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".augment_right_click")
                .setStyle(Style.EMPTY.applyFormat(ChatFormatting.GRAY)));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (level.isClientSide()) {
            return super.useOn(context);
        }
        BlockPos pos = context.getClickedPos();
        var blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof FurnacePatternBlockEntity v2) {
            v2.insertAugmentFromHand(((ServerPlayer) context.getPlayer()));
            v2.transferStacksInUnavailableSlotsToPlayer(context.getPlayer());
            return InteractionResult.CONSUME;

        }
        return super.useOn(context);
    }
}
