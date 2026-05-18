package ironfurnaces.registration.data_component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.util.StringHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import net.neoforged.api.distmarker.Dist;

import java.util.function.Consumer;

public enum HeaterBlockTooltip implements TooltipProvider {
    INSTANCE;

    public static final Codec<HeaterBlockTooltip> CODEC =
            MapCodec.unitCodec(INSTANCE);

    public static final StreamCodec<ByteBuf, HeaterBlockTooltip> STREAM_CODEC =
            StreamCodec.unit(INSTANCE);

    @Override
    public void addToTooltip(
            Item.TooltipContext tooltipContext,
            Consumer<Component> consumer,
            TooltipFlag tooltipFlag,
            DataComponentGetter dataComponentGetter
    ) {
        if (hasShiftDown()) {
            consumer.accept(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".heater_block")
                    .withStyle(ChatFormatting.GRAY));

            consumer.accept(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".heater_block1")
                    .withStyle(ChatFormatting.GRAY));
        } else {
            consumer.accept(StringHelper.getShiftInfoText());
        }
    }

    private static boolean hasShiftDown() {
        return Minecraft.getInstance().hasShiftDown();
    }
}
