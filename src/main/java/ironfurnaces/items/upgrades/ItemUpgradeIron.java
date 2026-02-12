package ironfurnaces.items.upgrades;

import ironfurnaces.registration.LegacyFurnaceBlocks;
import net.minecraft.world.level.block.Blocks;

public class ItemUpgradeIron extends ItemUpgrade {


    public ItemUpgradeIron(Properties properties) {
        super(properties, Blocks.FURNACE, LegacyFurnaceBlocks.IRON_FURNACE.get());
    }
}
