package ironfurnaces.items.upgrades;

import ironfurnaces.registration.LegacyFurnaceBlocks;

public class ItemUpgradeNetherite extends ItemUpgrade {


    public ItemUpgradeNetherite(Properties properties) {
        super(properties, LegacyFurnaceBlocks.OBSIDIAN_FURNACE.get(), LegacyFurnaceBlocks.NETHERITE_FURNACE.get());
    }
}
