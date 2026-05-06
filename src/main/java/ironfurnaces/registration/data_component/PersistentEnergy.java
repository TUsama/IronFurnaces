package ironfurnaces.registration.data_component;

import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.energy.ItemAccessEnergyHandler;

import java.util.function.Consumer;

public class PersistentEnergy extends ItemAccessEnergyHandler implements TooltipProvider {

    public PersistentEnergy(ItemAccess itemAccess, DataComponentType<Integer> energyComponent, int capacity) {
        super(itemAccess, energyComponent, capacity);
    }

    @Override
    public void addToTooltip(Item.TooltipContext tooltipContext, Consumer<Component> consumer, TooltipFlag tooltipFlag, DataComponentGetter dataComponentGetter) {
        consumer.accept(Component.translatable("ironfurnaces.data_component.persistent_energy", this.getAmountAsInt(), this.getCapacityAsInt()));
    }

}
