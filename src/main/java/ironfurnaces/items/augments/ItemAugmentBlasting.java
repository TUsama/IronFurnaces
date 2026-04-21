package ironfurnaces.items.augments;

import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.tileentity.furnaces.cache.IRecipeTypeHandler;
import ironfurnaces.tileentity.furnaces.cache.recipe_type_handlers.BlastRecipeTypeHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;



import java.util.List;

public class ItemAugmentBlasting extends ItemAugmentRed {


    public ItemAugmentBlasting(Properties properties) {
        super(properties);
    }

    @Override
    public IRecipeTypeHandler getRecipeTypeHandler() {
        return BlastRecipeTypeHandler.INSTANCE;
    }

    @Override
    public int getType() {
        return 2;
    }

    
    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".augment_blasting").setStyle(Style.EMPTY.applyFormat((ChatFormatting.GOLD))));
    }
}
