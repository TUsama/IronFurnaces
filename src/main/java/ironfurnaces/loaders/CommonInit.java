package ironfurnaces.loaders;

import com.clefal.nirvana_lib.utils.ModUtils;
import ironfurnaces.config.FurnaceConfig;
import ironfurnaces.config.GameplayConfig;
import ironfurnaces.config.RainbowConfig;
import ironfurnaces.registration.*;
import lombok.experimental.UtilityClass;
import net.minecraftforge.data.loading.DatagenModLoader;

@UtilityClass
public class CommonInit {

    public void init(){
        PacketInit.initPackets();
        ModMenus.register();
        ModCustomRecipe.register();
        ModBlocks.register();
        ModBlockEntities.register();
        ModItems.register();
        LegacyFurnaceBlocks.register();
        ModItemGroups.register();
        ModAdvancements.register();

        if (DatagenModLoader.isRunningDataGen()) {
            ModLangs.register();
            ModNewFurnace.register();
        }

        if (ModUtils.isModLoaded("jei")){
            JEICompat.register();
        }

        RainbowConfig.init();
        GameplayConfig.init();
        FurnaceConfig.init();
    }
}
