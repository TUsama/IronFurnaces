package ironfurnaces.items.augments;

import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.tileentity.furnaces.cache.IRecipeTypeHandler;
import ironfurnaces.tileentity.furnaces.cache.recipe_type_handlers.SmokeRecipeTypeHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;



import java.util.List;
import java.util.function.Consumer;

public class ItemAugmentSmoking extends ItemAugmentRed {

    public ItemAugmentSmoking(Properties properties) {
        super(properties);
    }

    @Override
    public IRecipeTypeHandler getRecipeTypeHandler() {
        return SmokeRecipeTypeHandler.INSTANCE;
    }


    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, context, display, builder, tooltipFlag);
        builder.accept(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".augment_smoking").setStyle(Style.EMPTY.applyFormat((ChatFormatting.GOLD))));
    }
}
