package ironfurnaces.items;

import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.registration.ModDataComponents;
import ironfurnaces.registration.data_component.SpookyItemInfo;
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

public class ItemSpooky extends ItemJovial implements IJovialSetter{


    public ItemSpooky(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, context, display, builder, tooltipFlag);
        builder.accept(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".spooky_right_click")
                .setStyle(Style.EMPTY.applyFormat(ChatFormatting.GRAY)));

        builder.accept(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".spooky1")
                .setStyle(Style.EMPTY.applyFormat(ChatFormatting.GRAY)));

        builder.accept(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".spooky2")
                .setStyle(Style.EMPTY.applyFormat(ChatFormatting.GRAY)));
    }

    @Override
    public JovialState getJovialState() {
        return JovialState.SPOOKY;
    }
}
