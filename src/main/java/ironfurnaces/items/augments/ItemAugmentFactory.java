package ironfurnaces.items.augments;

import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.tileentity.furnaces.pattern.mode.AbstractFurnaceModeHandler;
import ironfurnaces.tileentity.furnaces.pattern.mode.internal.FactoryModeHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;


import java.util.List;

public class ItemAugmentFactory extends ItemAugmentBlue {

    public ItemAugmentFactory(Properties properties) {
        super(properties);
    }

    @Override
    public AbstractFurnaceModeHandler getModeHandler() {
        return FactoryModeHandler.INSTANCE;
    }

    
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);
        tooltip.add(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".augment_factory_pro").setStyle(Style.EMPTY.applyFormat((ChatFormatting.GREEN))));
        tooltip.add(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".augment_factory_con").setStyle(Style.EMPTY.applyFormat(ChatFormatting.DARK_RED)));
    }



}
