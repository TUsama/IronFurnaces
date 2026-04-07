package ironfurnaces.items.augments;

import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.tileentity.furnaces.FurnaceMode;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;



import javax.annotation.Nullable;
import java.util.List;

public class ItemAugmentGenerator extends ItemAugmentBlue {

    public ItemAugmentGenerator(Properties properties) {
        super(properties);
    }

    @Override
    public FurnaceMode getMode() {
        return FurnaceMode.GENERATOR;
    }

    
    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".augment_generator_pro").setStyle(Style.EMPTY.applyFormat((ChatFormatting.GREEN))));
        tooltip.add(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".augment_generator_con").setStyle(Style.EMPTY.applyFormat(ChatFormatting.DARK_RED)));
    }



}
