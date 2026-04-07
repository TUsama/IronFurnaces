package ironfurnaces.loaders;

import com.clefal.nirvana_lib.utils.ResourceLocationUtils;
import com.tterrag.registrate.Registrate;
import lombok.experimental.UtilityClass;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@UtilityClass
public class IronFurnaces {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String GITHUB_BRANCH = "1.20.1";
    public static final String MC_VERSION = "1.20.1";
    public static final String RELEASE_TYPE = "-beta";
    public static final String VERSION = "416";
    public static final String MOD_ID = "ironfurnaces";
    public static final Registrate REGISTRATE = Registrate.create(MOD_ID).defaultCreativeTab((ResourceKey<CreativeModeTab>) null);

    public ResourceLocation id(String path){
        return ResourceLocationUtils.make(MOD_ID, path);
    }

    public ResourceLocation gui(String id){
        return ResourceLocationUtils.make(MOD_ID, "textures/gui/" + id + ".png");
    }

    public ResourceLocation sprite(String id){
        //~ if >1.20.1 '"textures/gui/sprites/" + id + ".png"' -> 'id'
        return ResourceLocationUtils.make(MOD_ID, "textures/gui/sprites/" + id + ".png");
    }

    public ResourceLocation vanilla(String path){
        //? 1.20.1 {
        return new ResourceLocation(path);
        //? } else {
        /*return ResourceLocation.tryParse(path);
        *///?}

    }

}
