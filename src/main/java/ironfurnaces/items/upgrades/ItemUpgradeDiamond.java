package ironfurnaces.items.upgrades;

import ironfurnaces.registration.LegacyFurnaceBlocks;

public class ItemUpgradeDiamond extends ItemUpgrade {


    public ItemUpgradeDiamond(Properties properties) {
        super(properties, LegacyFurnaceBlocks.GOLD_FURNACE.get(), LegacyFurnaceBlocks.DIAMOND_FURNACE.get());
    }
}
