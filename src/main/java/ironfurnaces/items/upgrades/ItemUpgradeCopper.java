//? <1.21.11{
package ironfurnaces.items.upgrades;

import ironfurnaces.registration.LegacyFurnaceBlocks;
import net.minecraft.world.level.block.Blocks;

public class ItemUpgradeCopper extends ItemUpgrade {


    public ItemUpgradeCopper(Properties properties) {
        super(properties, Blocks.FURNACE, LegacyFurnaceBlocks.COPPER_FURNACE.get());
    }
}

//?}