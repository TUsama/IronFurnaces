package ironfurnaces.registration;

import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.providers.ProviderType;
import ironfurnaces.tileentity.furnaces.tier.FurnacePatternDatagen;
import ironfurnaces.tileentity.furnaces.tier.upgrade.TierUpgradeRuleDatagen;


import static ironfurnaces.loaders.IronFurnaces.REGISTRATE;

public class ModNewFurnace {
    public static final Registrate RAINBOW = REGISTRATE.addDataGenerator(ProviderType.GENERIC_SERVER, x -> {
        x.add(data -> new FurnacePatternDatagen(data.output()));
        x.add(data -> new TierUpgradeRuleDatagen(data.output()));
    });


    public static void register(){

    }
}
