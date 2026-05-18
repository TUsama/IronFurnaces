package ironfurnaces.items;

import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.registration.ModDataComponents;
import ironfurnaces.registration.data_component.XmasItemInfo;
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
        super(properties.component(
                ModDataComponents.XMAS_ITEM_INFO.get(),
                XmasItemInfo.INSTANCE
        ));
    }


    @Override
    public JovialState getJovialState() {
        return JovialState.XMAS;
    }
}
