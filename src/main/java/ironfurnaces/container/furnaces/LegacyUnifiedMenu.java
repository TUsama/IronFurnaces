//? <1.21.11{
package ironfurnaces.container.furnaces;

import ironfurnaces.blocks.furnaces.BlockIronFurnaceBase;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;

public class LegacyUnifiedMenu extends BlockIronFurnaceContainerBase{

    public final String id;

    public LegacyUnifiedMenu(MenuType<?> containerType, int windowId, Level world, BlockPos pos, Inventory playerInventory, Player player) {
        super(containerType, windowId, world, pos, playerInventory, player);
        this.id = ((BlockIronFurnaceBase) world.getBlockState(pos).getBlock()).getBackgroundID();

    }
}

//?}