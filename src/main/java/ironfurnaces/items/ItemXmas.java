package ironfurnaces.items;

import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.registration.ModDataComponents;
import ironfurnaces.registration.data_component.XmasItemInfo;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;



import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class ItemXmas extends ItemJovial implements IJovialSetter{


    public ItemXmas(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        builder.accept(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".xmas_right_click")
                .setStyle(Style.EMPTY.applyFormat(ChatFormatting.GRAY)));

        builder.accept(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".xmas1")
                .setStyle(Style.EMPTY.applyFormat(ChatFormatting.GRAY)));

        builder.accept(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".xmas2")
                .setStyle(Style.EMPTY.applyFormat(ChatFormatting.GRAY)));
    }

    @Override
    public JovialState getJovialState() {
        return JovialState.XMAS;
    }
}
