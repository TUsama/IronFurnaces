package ironfurnaces.config;

import ironfurnaces.loaders.IronFurnaces;
import me.fzzyhmstrs.fzzy_config.annotations.WithPerms;
import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.api.RegisterType;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt;

@WithPerms(opLevel = 2)
public class IronFurnacesConfig extends Config {

    public static IronFurnacesConfig config = ConfigApiJava.registerAndLoadConfig(IronFurnacesConfig::new, RegisterType.BOTH);
    public ValidatedInt max_rainbow_furnace_per_player = new ValidatedInt(1);
    public boolean force_load_chunk_when_check_furnace_kind_for_rainbow_furnace = false;
    public ValidatedInt stored_xp_level = new ValidatedInt(100, 1000, 1);

    public IronFurnacesConfig() {
        super(IronFurnaces.id("config"));
    }

    public static void init() {

    }
}
