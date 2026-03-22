package ironfurnaces.config;

import ironfurnaces.loaders.IronFurnaces;
import me.fzzyhmstrs.fzzy_config.annotations.WithPerms;
import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.api.RegisterType;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt;

@WithPerms(opLevel = 2)
public class RainbowConfig extends Config {

    public static RainbowConfig config = ConfigApiJava.registerAndLoadConfig(RainbowConfig::new, RegisterType.BOTH);
    public ValidatedInt max_rainbow_furnace_per_player = new ValidatedInt(1);

    public RainbowConfig() {
        super(IronFurnaces.id("rainbow_config"));
    }

    public static void init() {

    }
}
