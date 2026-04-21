//~ replace_entry_point
package ironfurnaces.loaders;

import com.clefal.nirvana_lib.utils.ModUtils;
import ironfurnaces.compat.farmer_delight.FDCompat;
import ironfurnaces.registration.ModBlockEntities;
import ironfurnaces.tileentity.furnaces.pattern.render.PatternHolderBlockEntityRenderer;
import lombok.experimental.UtilityClass;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;


@UtilityClass
public class ClientInit {
    public void clientInit(IEventBus gameBus, IEventBus modBus){
        modBus.<FMLClientSetupEvent>addListener(EventPriority.LOWEST, x -> {
            BlockEntityRenderers.register(ModBlockEntities.PATTERN_HOLDER.get(), context -> PatternHolderBlockEntityRenderer.getInstance());
        });

        if (ModUtils.isModLoaded("farmersdelight")){
            FDCompat.registerClient();
        }
    }

}
