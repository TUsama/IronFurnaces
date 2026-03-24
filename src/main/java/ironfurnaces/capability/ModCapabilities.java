package ironfurnaces.capability;

import ironfurnaces.capability.rainbow.OwnerRainbowContext;
import ironfurnaces.capability.rainbow.PlayerRainbowContextCapability;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

public class ModCapabilities {

    public static final Capability<IPlayerFurnacesList> FURNACES_LIST = CapabilityManager.get(new CapabilityToken<>(){});;
    public static final Capability<OwnerRainbowContext> PLAYER_RAINBOW_CONTEXT = CapabilityManager.get(new CapabilityToken<>(){});

    public static void register(RegisterCapabilitiesEvent event)
    {
        event.register(IPlayerFurnacesList.class);
        event.register(OwnerRainbowContext.class);
    }

}
