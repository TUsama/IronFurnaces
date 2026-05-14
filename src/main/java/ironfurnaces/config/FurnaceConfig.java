package ironfurnaces.config;

import ironfurnaces.loaders.IronFurnaces;
import me.fzzyhmstrs.fzzy_config.annotations.Action;
import me.fzzyhmstrs.fzzy_config.annotations.RequiresAction;
import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.api.RegisterType;
import me.fzzyhmstrs.fzzy_config.config.Config;

public class FurnaceConfig extends Config {

    public static FurnaceConfig config = ConfigApiJava.registerAndLoadConfig(FurnaceConfig::new, RegisterType.BOTH);
    @RequiresAction(action = Action.RELOAD_BOTH)
    public int nutrition_to_energy_factor = 32000;

    public FurnaceConfig() {
        super(IronFurnaces.id("furnace_config"));
    }

    public static void init() {

    }
}
