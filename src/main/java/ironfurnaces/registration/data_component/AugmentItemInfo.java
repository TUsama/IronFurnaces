package ironfurnaces.registration.data_component;


import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import ironfurnaces.loaders.IronFurnaces;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

import java.util.function.Consumer;

public record AugmentItemInfo() implements TooltipProvider {
    public static final AugmentItemInfo INSTANCE = new AugmentItemInfo();

    public static final Codec<AugmentItemInfo> CODEC = MapCodec.unitCodec(INSTANCE);

    @Override
    public void addToTooltip(
            Item.TooltipContext context,
            Consumer<Component> tooltip,
            TooltipFlag flag,
            DataComponentGetter components
    ) {
        tooltip.accept(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".augment_right_click")
                .setStyle(Style.EMPTY.applyFormat(ChatFormatting.GRAY)));
    }
}
