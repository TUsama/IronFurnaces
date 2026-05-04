//? fd {
/*package ironfurnaces.compat.farmer_delight;

import ironfurnaces.gui.furnaces.renderer.PatternScreenRenderHandlerManager;
import ironfurnaces.gui.furnaces.renderer.compat.FDRenderHandler;
import ironfurnaces.registration.FarmerDelightCompat;
import ironfurnaces.registration.ModItemGroups;
import ironfurnaces.tileentity.furnaces.pattern.mode.FurnaceModeManager;
import ironfurnaces.tileentity.furnaces.pattern.mode.compat.FDCompatModeHandler;
import lombok.experimental.UtilityClass;
import net.neoforged.neoforge.data.loading.DatagenModLoader;

@UtilityClass
public class FDCompat {
    public void registerCommon(){
        FarmerDelightCompat.register();
        FurnaceModeManager.INSTANCE.register(FDCompatModeHandler.ID, FDCompatModeHandler.INSTANCE);

    }

    public void registerClient(){
        PatternScreenRenderHandlerManager.INSTANCE.register(FDCompatModeHandler.ID, FDRenderHandler::new);
    }
}

*///?}