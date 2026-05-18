package ironfurnaces.util;

import com.clefal.nirvana_lib.utils.ModUtils;
import harmonised.pmmo.api.events.FurnaceBurnEvent;
import harmonised.pmmo.events.impl.FurnaceHandler;
import lombok.experimental.UtilityClass;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;



import static net.neoforged.neoforge.event.EventHooks.firePlayerSmeltedEvent;



@UtilityClass
public class CompatUtil {
    public void handleVanillaWhenHasPlayer(Player player, ItemStack smelted, int i){
        smelted.onCraftedBy(player, smelted.getCount());
        firePlayerSmeltedEvent(player, smelted, i);
    }

    public void handleVanillaWhenWithoutPlayer(ItemStack smelted, Level level){
        smelted.onCraftedBySystem(level);
    }

    public void firePmmoSmeltedEvent(ItemStack input, ItemStack output, Level level, BlockPos pos){
        if (ModUtils.isModLoaded("pmmo")){
            FurnaceHandler.handle(new FurnaceBurnEvent(input, level, pos));
        }

    }
}
