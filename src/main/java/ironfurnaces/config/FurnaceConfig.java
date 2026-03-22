package ironfurnaces.config;

import ironfurnaces.loaders.IronFurnaces;
import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.api.RegisterType;
import me.fzzyhmstrs.fzzy_config.config.Config;

public class FurnaceConfig extends Config {

    public static FurnaceConfig config = ConfigApiJava.registerAndLoadConfig(FurnaceConfig::new, RegisterType.BOTH);

    public int nutrition_to_energy_factor = 32000;

    public FurnaceConfig() {
        super(IronFurnaces.id("furnace_config"));
    }

    public static void init() {

    }
}
