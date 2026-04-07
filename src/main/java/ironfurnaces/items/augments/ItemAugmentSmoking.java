package ironfurnaces.items.augments;

import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.tileentity.furnaces.cache.AugmentCache;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;



import java.util.List;

public class ItemAugmentSmoking extends ItemAugmentRed {

    public ItemAugmentSmoking(Properties properties) {
        super(properties);
    }

    @Override
    public AugmentCache.HandlingRecipeType getRecipeType() {
        return AugmentCache.HandlingRecipeType.SMOKE;
    }

    @Override
    public int getType() {
        return 1;
    }


    
    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".augment_smoking").setStyle(Style.EMPTY.applyFormat((ChatFormatting.GOLD))));
    }
}
