package ironfurnaces.init;

import ironfurnaces.capability.CapabilityPlayerFurnacesList;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModSetup {


    public static final Map<Holder.Reference<Item>, Integer> SMOKING_BURNS = new HashMap<>();
    public static final Map<Holder.Reference<Item>, Boolean> HAS_RECIPE = new HashMap<>();
    public static final Map<Holder.Reference<Item>, Boolean> HAS_RECIPE_SMOKING = new HashMap<>();
    public static final Map<Holder.Reference<Item>, Boolean> HAS_RECIPE_BLASTING = new HashMap<>();


    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event)
    {
        CapabilityPlayerFurnacesList.register(event);
    }


}
