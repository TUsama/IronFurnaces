package ironfurnaces.items;

import ironfurnaces.loaders.IronFurnaces;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;



import javax.annotation.Nullable;
import java.util.List;

public class ItemXmas extends ItemJovial implements IJovialSetter{


    public ItemXmas(Properties properties) {
        super(properties);
    }

    
    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".xmas_right_click").setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY))));
        tooltip.add(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".xmas1").setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY))));
        tooltip.add(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".xmas2").setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY))));
    }


    @Override
    public JovialState getJovialState() {
        return JovialState.XMAS;
    }
}
