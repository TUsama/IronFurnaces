//~ replace_Registrate
package ironfurnaces.registration;

import dev.anvilcraft.lib.v2.registrum.Registrum;
import dev.anvilcraft.lib.v2.registrum.providers.ProviderType;
import ironfurnaces.tileentity.furnaces.pattern.FurnacePatternDatagen;
import ironfurnaces.tileentity.furnaces.pattern.upgrade.PatternUpgradeRuleDatagen;


import static ironfurnaces.loaders.IronFurnaces.REGISTRATE;

public class ModNewFurnace {
    public static final Registrum DELEGATE_DATAGEN = REGISTRATE.addDataGenerator(ProviderType.GENERIC_SERVER, x -> {
        x.add(data -> new FurnacePatternDatagen(data.output()));
        x.add(data -> new PatternUpgradeRuleDatagen(data.output()));
    });


    public static void register(){

    }
}
