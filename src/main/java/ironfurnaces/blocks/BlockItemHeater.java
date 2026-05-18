package ironfurnaces.blocks;

import ironfurnaces.capability.VanillaCapabilityHandler;
import ironfurnaces.registration.ModDataComponents;
import ironfurnaces.registration.data_component.HeaterBlockTooltip;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;


public class BlockItemHeater extends BlockItem {


    public BlockItemHeater(Block block, Properties properties) {
        super(block, properties.component(
                ModDataComponents.HEATER_BLOCK_TOOLTIP.get(),
                HeaterBlockTooltip.INSTANCE
        ));
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
