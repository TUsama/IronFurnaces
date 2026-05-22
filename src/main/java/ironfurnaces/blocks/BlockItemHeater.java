package ironfurnaces.blocks;

import ironfurnaces.capability.VanillaCapabilityHandler;
import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.registration.ModDataComponents;
import ironfurnaces.registration.data_component.HeaterBlockTooltip;
import ironfurnaces.util.StringHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;


public class BlockItemHeater extends BlockItem {


    public BlockItemHeater(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, context, display, builder, tooltipFlag);
        if (Minecraft.getInstance().hasShiftDown()) {
            builder.accept(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".heater_block")
                    .withStyle(ChatFormatting.GRAY));

            builder.accept(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".heater_block1")
                    .withStyle(ChatFormatting.GRAY));
        } else {
            builder.accept(StringHelper.getShiftInfoText());
        }
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        var itemEnergyStorage = VanillaCapabilityHandler.getItemEnergyStorage(stack);
        return itemEnergyStorage != null && itemEnergyStorage.getAmountAsInt() > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        var itemEnergyStorage = VanillaCapabilityHandler.getItemEnergyStorage(stack);
        if (itemEnergyStorage != null) {
            int energy = itemEnergyStorage.getAmountAsInt();
            return (int) ((int) 13 * ((double) energy / (double) 1000000));
        }
        return 0;
    }

    @Override
    public int getBarColor(ItemStack p_150901_) {
        return 0xFF800600;
    }


}
