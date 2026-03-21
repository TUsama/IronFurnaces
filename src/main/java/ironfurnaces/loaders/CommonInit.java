package ironfurnaces.loaders;

import com.clefal.nirvana_lib.utils.ModUtils;
import ironfurnaces.config.IronFurnacesConfig;
import ironfurnaces.registration.*;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CommonInit {

    public void init(){
        ModMenus.register();
        ModCustomRecipe.register();
        ModBlocks.register();
        ModBlockEntities.register();
        ModItems.register();
        ModNewFurnace.register();
        LegacyFurnaceBlocks.register();
        ModItemGroups.register();
        ModAdvancements.register();
        ModLangs.register();
        if (ModUtils.isModLoaded("jei")){
            JEICompat.register();
        }

        IronFurnacesConfig.init();
    }
}
