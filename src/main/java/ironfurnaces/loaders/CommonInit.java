package ironfurnaces.loaders;

import com.clefal.nirvana_lib.utils.ModUtils;
import ironfurnaces.compat.farmer_delight.FDCompat;
import ironfurnaces.config.FurnaceConfig;
import ironfurnaces.config.GameplayConfig;
import ironfurnaces.config.RainbowConfig;
import ironfurnaces.registration.*;
import lombok.experimental.UtilityClass;
import net.minecraftforge.data.loading.DatagenModLoader;

@UtilityClass
public class CommonInit {

    public void init(){
        boolean runningDataGen = DatagenModLoader.isRunningDataGen();
        PacketInit.initPackets();
        ModMenus.register();
        ModCustomRecipe.register();
        ModBlocks.register();
        ModBlockEntities.register();
        ModItems.register();
        if (runningDataGen || ModUtils.isModLoaded("farmersdelight")){
            FDCompat.registerCommon();
        }
        LegacyFurnaceBlocks.register();
        ModItemGroups.register();
        ModAdvancements.register();




        if (runningDataGen) {
            ModLangs.register();
            ModNewFurnace.register();
        }

        if (ModUtils.isModLoaded("jei")){
            JEICompat.register();
        }

        //? >1.20.1
        //ModDataComponents.register();

        RainbowConfig.init();
        GameplayConfig.init();
        FurnaceConfig.init();
    }
}
