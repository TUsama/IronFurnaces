package ironfurnaces.items.augments;

import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.tileentity.furnaces.cache.AugmentCache;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;



import javax.annotation.Nullable;
import java.util.List;
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
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);
        tooltip.add(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".augment_speed_pro").setStyle(Style.EMPTY.applyFormat((ChatFormatting.GREEN))));
        tooltip.add(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".augment_speed_con").setStyle(Style.EMPTY.applyFormat(ChatFormatting.DARK_RED)));
    }



}
