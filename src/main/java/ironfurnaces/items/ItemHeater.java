package ironfurnaces.items;


import ironfurnaces.registration.ModDataComponents;
import ironfurnaces.registration.data_component.HeaterItemInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;

public class ItemHeater extends Item {


    public ItemHeater(Properties properties) {
        super(properties.component(ModDataComponents.HEATER_ITEM_INFO, new HeaterItemInfo(Optional.empty())));
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, context, display, builder, tooltipFlag);
        itemStack.addToTooltip(ModDataComponents.HEATER_ITEM_INFO, context, display, builder, tooltipFlag);
    }

    @Nullable
    public static BlockPos getBoundBlockPos(ItemStack stack) {
        return stack.get(ModDataComponents.HEATER_ITEM_INFO.get()).getPosOrNull();

    }

    public static void writeBoundBlockPos(ItemStack stack, BlockPos pos) {
        stack.set(ModDataComponents.HEATER_ITEM_INFO.get(), new HeaterItemInfo(pos));
    }


}
