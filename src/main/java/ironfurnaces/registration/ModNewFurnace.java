package ironfurnaces.registration;

import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.providers.ProviderType;
import ironfurnaces.tileentity.furnaces.pattern.FurnacePatternDatagen;
import ironfurnaces.tileentity.furnaces.pattern.upgrade.PatternUpgradeRuleDatagen;


import static ironfurnaces.loaders.IronFurnaces.REGISTRATE;

public class ModNewFurnace {
    public static final Registrate RAINBOW = REGISTRATE.addDataGenerator(ProviderType.GENERIC_SERVER, x -> {
        x.add(data -> new FurnacePatternDatagen(data.output()));
        x.add(data -> new PatternUpgradeRuleDatagen(data.output()));
    });


    public static void register(){

    }
}
