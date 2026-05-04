package ironfurnaces.util;

import com.clefal.nirvana_lib.utils.ModUtils;
import harmonised.pmmo.api.events.FurnaceBurnEvent;
import harmonised.pmmo.events.impl.FurnaceHandler;
import lombok.experimental.UtilityClass;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;


//? 1.20.1 {
/*import static net.neoforged.neoforge.event.ForgeEventFactory.firePlayerSmeltedEvent;
*///? } else {
import static net.neoforged.neoforge.event.EventHooks.firePlayerSmeltedEvent;
//?}


@UtilityClass
public class CompatUtil {
    public void handleVanillaWhenHasPlayer(Player player, ItemStack smelted){
        smelted.onCraftedBy(player.level(), player, smelted.getCount());
        firePlayerSmeltedEvent(player, smelted);
    }

    public void handleVanillaWhenWithoutPlayer(ItemStack smelted, Level level){
        //? >1.20.1
        smelted.onCraftedBySystem(level);
    }

    public void firePmmoSmeltedEvent(ItemStack input, ItemStack output, Level level, BlockPos pos){
        if (ModUtils.isModLoaded("pmmo")){
            //~ if >1.20.1 'input, level, pos' -> 'input, output, level, pos'
            FurnaceHandler.handle(new FurnaceBurnEvent(input, output, level, pos));
        }

    }
}
