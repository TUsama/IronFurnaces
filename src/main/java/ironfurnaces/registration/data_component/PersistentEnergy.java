//? >=1.21.1{
package ironfurnaces.registration.data_component;

import ironfurnaces.capability.VanillaCapabilityHandler;
import ironfurnaces.registration.ModDataComponents;
import ironfurnaces.util.StringHelper;
import net.minecraft.ChatFormatting;
//? >1.21.11{
import net.minecraft.core.component.DataComponentGetter;
 //?}
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import net.neoforged.neoforge.common.MutableDataComponentHolder;
import net.neoforged.neoforge.energy.ComponentEnergyStorage;

import java.util.function.Consumer;

public class PersistentEnergy extends ComponentEnergyStorage implements TooltipProvider {

    public PersistentEnergy(MutableDataComponentHolder parent, DataComponentType<Integer> energyComponent, int capacity, int maxReceive, int maxExtract) {
        super(parent, energyComponent, capacity, maxReceive, maxExtract);
    }

    @Override
            //~ if >1.21.11 'Item.TooltipContext tooltipContext, Consumer<Component> consumer, TooltipFlag tooltipFlag' -> 'Item.TooltipContext tooltipContext, Consumer<Component> consumer, TooltipFlag tooltipFlag, DataComponentGetter dataComponentGetter'
    public void addToTooltip(Item.TooltipContext tooltipContext, Consumer<Component> consumer, TooltipFlag tooltipFlag, DataComponentGetter dataComponentGetter) {
        dataComponentGetter.get(this)
        consumer.accept(Component.translatable("ironfurnaces.data_component.persistent_energy", this.getEnergyStored(), this.getMaxEnergyStored()));
    }

}

//?}