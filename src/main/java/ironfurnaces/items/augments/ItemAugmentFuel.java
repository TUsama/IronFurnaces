package ironfurnaces.items.augments;

import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.tileentity.furnaces.cache.AugmentCache;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.function.IntUnaryOperator;

public class ItemAugmentFuel extends ItemAugmentGreen {

    public ItemAugmentFuel(Properties properties) {
        super(properties);
    }

    @Override
    public AugmentCache.GreenAugmentModifier getModifier() {
        return AugmentCache.GreenAugmentModifier.FUEL_EFFICIENCY;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".augment_fuel_pro").setStyle(Style.EMPTY.applyFormat((ChatFormatting.GREEN))));
        tooltip.add(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".augment_fuel_con").setStyle(Style.EMPTY.applyFormat(ChatFormatting.DARK_RED)));
    }
}
