package ironfurnaces.loaders;

import com.clefal.nirvana_lib.utils.ResourceLocationUtils;
import com.tterrag.registrate.Registrate;
import ironfurnaces.registration.ModItemGroups;
import lombok.experimental.UtilityClass;
import net.minecraft.resources.ResourceLocation;
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
    public static final Registrate REGISTRATE = Registrate.create(MOD_ID);

    public ResourceLocation id(String path){
        return ResourceLocationUtils.make(MOD_ID, path);
    }
}
