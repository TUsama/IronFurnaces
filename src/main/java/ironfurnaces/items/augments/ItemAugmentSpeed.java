package ironfurnaces.items.augments;

import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.tileentity.furnaces.cache.AugmentCache;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;



import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntUnaryOperator;

public class ItemAugmentSpeed extends ItemAugmentGreen {

    public ItemAugmentSpeed(Item.Properties properties) {
        super(properties);
    }

    @Override
    public AugmentCache.GreenAugmentModifier getModifier() {
        return AugmentCache.GreenAugmentModifier.SPEED;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, context, display, builder, tooltipFlag);
        builder.accept(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".augment_speed_pro").setStyle(Style.EMPTY.applyFormat((ChatFormatting.GREEN))));
        builder.accept(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".augment_speed_con").setStyle(Style.EMPTY.applyFormat(ChatFormatting.DARK_RED)));
    }




}
