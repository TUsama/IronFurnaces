package ironfurnaces.config;

import ironfurnaces.loaders.IronFurnaces;
import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.api.RegisterType;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt;

public class GameplayConfig extends Config {
    public static GameplayConfig config = ConfigApiJava.registerAndLoadConfig(GameplayConfig::new, RegisterType.BOTH);

    public boolean force_load_chunk_when_check_furnace_kind_for_rainbow_furnace = false;
    public ValidatedInt stored_xp_level = new ValidatedInt(100, 1000, 1);

    public GameplayConfig() {
        super(IronFurnaces.id("gameplay_config"));
    }

    public static void init() {

    }
}
