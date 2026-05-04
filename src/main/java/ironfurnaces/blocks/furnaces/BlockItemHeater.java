package ironfurnaces.blocks.furnaces;

//? <1.21.11{
/*import ironfurnaces.capability.VanillaCapabilityHandler;
import ironfurnaces.gui.furnaces.BlockIronFurnaceScreenBase;
*///?}
import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.registration.ModDataComponents;
import ironfurnaces.util.StringHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.energy.IEnergyStorage;
//? forge {
/*import net.neoforged.neoforge.common.capabilities.ICapabilityProvider;
import ironfurnaces.capability.ItemEnergyCapabilityProvider;
*///?}


import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

//~ if >1.20.1 'hasTag()' -> 'has(ModDataComponents.PERSISTENT_ENERGY)' {
//~ if >1.20.1 'getTag().getInt("Energy")' -> 'get(ModDataComponents.PERSISTENT_ENERGY)' {
public class BlockItemHeater extends BlockItem {


    public BlockItemHeater(Block block, Properties properties) {
        super(block, properties);
    }


    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {

        //? forge{
        /*VanillaCapabilityHandler.withItemEnergyStorage(stack, x -> {
            tooltip.add(Component.translatable("ironfurnaces.data_component.persistent_energy", x.getEnergyStored(), x.getMaxEnergyStored()));
        });
        *///?}

        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".heater_block").setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY))));
            tooltip.add(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".heater_block1").setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY))));
        } else {
            tooltip.add(StringHelper.getShiftInfoText());
        }
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        IEnergyStorage itemEnergyStorage = VanillaCapabilityHandler.getItemEnergyStorage(stack);
        return itemEnergyStorage!= null && itemEnergyStorage.getEnergyStored() > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        IEnergyStorage itemEnergyStorage = VanillaCapabilityHandler.getItemEnergyStorage(stack);
        if (itemEnergyStorage != null)
        {
            int energy = itemEnergyStorage.getEnergyStored();
            return (int) ((int)13 * ((double) energy / (double) 1000000));
        }
        return 0;
    }

    @Override
    public int getBarColor(ItemStack p_150901_) {
        return 0xFF800600;
    }

    //? forge{
    /*@Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        ItemEnergyCapabilityProvider provider = new ItemEnergyCapabilityProvider(
                GameplayConfig.config.heater_capacity.get(),
                1_000,
                0
        );

        if (nbt != null) {
            provider.deserializeNBT(nbt);
        }

        return provider;
    }
*///?}
}
//~}
//~}