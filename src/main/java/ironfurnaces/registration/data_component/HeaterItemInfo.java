package ironfurnaces.registration.data_component;

import com.mojang.serialization.Codec;
import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.util.StringHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Consumer;

public record HeaterItemInfo(Optional<BlockPos> pos) implements TooltipProvider {
    public static final Codec<HeaterItemInfo> CODEC = BlockPos.CODEC
            .optionalFieldOf("pos")
            .xmap(HeaterItemInfo::new, HeaterItemInfo::pos)
            .codec();

    public HeaterItemInfo(BlockPos pos) {
        this(Optional.ofNullable(pos));
    }

    public @Nullable BlockPos getPosOrNull() {
        return pos.orElse(null);
    }

    @Override
    public void addToTooltip(
            Item.TooltipContext tooltipContext,
            Consumer<Component> consumer,
            TooltipFlag tooltipFlag,
            DataComponentGetter dataComponentGetter
    ) {
        Style gray = Style.EMPTY.applyFormat(ChatFormatting.GRAY);

        if (Minecraft.getInstance().hasShiftDown()) {
            if (pos.isPresent()) {
                BlockPos p = pos.get();

                consumer.accept(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".heater").setStyle(gray));
                consumer.accept(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".heaterX").setStyle(gray)
                        .append(Component.literal(String.valueOf(p.getX())).setStyle(gray)));
                consumer.accept(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".heaterY").setStyle(gray)
                        .append(Component.literal(String.valueOf(p.getY())).setStyle(gray)));
                consumer.accept(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".heaterZ").setStyle(gray)
                        .append(Component.literal(String.valueOf(p.getZ())).setStyle(gray)));
            } else {
                consumer.accept(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".heater_not_bound").setStyle(gray));
                consumer.accept(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".heater_tip").setStyle(gray));
                consumer.accept(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".heater_tip1").setStyle(gray));
            }
        } else {
            consumer.accept(StringHelper.getShiftInfoText());
        }
    }
}