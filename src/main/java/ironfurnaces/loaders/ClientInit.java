//~ replace_entry_point
package ironfurnaces.loaders;

import com.clefal.nirvana_lib.utils.ModUtils;
import ironfurnaces.registration.ModBlockEntities;
import ironfurnaces.tileentity.furnaces.pattern.render.PatternHolderBlockEntityRenderer;
import lombok.experimental.UtilityClass;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
//? fd{
/*import ironfurnaces.compat.farmer_delight.FDCompat;
*///?}

//? >1.21.11{
import ironfurnaces.tileentity.furnaces.pattern.render.refactor.FurnacePatternBlockEntityRenderer;
import net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent;
import ironfurnaces.tileentity.furnaces.pattern.render.refactor.FurnacePatternHolderSpecialRenderer;
//?}

import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;



@UtilityClass
public class ClientInit {
    public void clientInit(IEventBus gameBus, IEventBus modBus){
        modBus.<FMLClientSetupEvent>addListener(EventPriority.LOWEST, x -> {
            BlockEntityRenderers.register(ModBlockEntities.PATTERN_HOLDER.get(), context -> PatternHolderBlockEntityRenderer.getInstance());
        });

        //? >1.21.11{
        BlockEntityRenderers.register(
                ModBlockEntities.PATTERN_HOLDER.get(),
                FurnacePatternBlockEntityRenderer::new
        );

        modBus.addListener((RegisterSpecialModelRendererEvent event) -> {
            event.register(
                    IronFurnaces.id("furnace_pattern_holder"),
                    FurnacePatternHolderSpecialRenderer.Unbaked.MAP_CODEC
            );
        });
        //?}

        //? fd {
        /*if (ModUtils.isModLoaded("farmersdelight")){
            FDCompat.registerClient();
        }
        *///?}
    }

}
