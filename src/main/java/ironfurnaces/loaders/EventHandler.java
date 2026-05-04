package ironfurnaces.loaders;


import ironfurnaces.registration.ModItems;
import ironfurnaces.tileentity.furnaces.UnifiedTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

//? if forge {

/*import ironfurnaces.capability.PlayerFurnacesListProvider;
import ironfurnaces.capability.rainbow.PlayerRainbowContextCapability;
import net.neoforged.neoforge.event.AttachCapabilitiesEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.neoforged.neoforge.eventbus.api.SubscribeEvent;
import net.neoforged.neoforge.fml.common.Mod;
*///?} else {
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ExplosionKnockbackEvent;
//?}
import java.util.List;
//$ if forge '@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)' else '@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME)'
@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME)
public class EventHandler {

    //? forge {
    
    /*@SubscribeEvent
    public static void playerEvent(AttachCapabilitiesEvent<Entity> event)
    {
        if (event.getObject() instanceof Player)
        {
            event.addCapability(new ResourceLocation(IronFurnaces.MOD_ID, "furnaces_list"), new PlayerFurnacesListProvider());
            event.addCapability(new ResourceLocation(IronFurnaces.MOD_ID, "rainbow_context"), new PlayerRainbowContextCapability());
        }
    }
    *///?}
    @SubscribeEvent
            //~ if >1.20.1 'ExplosionEvent' -> 'ExplosionKnockbackEvent'
    public static void explosionEvent(ExplosionKnockbackEvent event)
    {
        List<BlockPos> list = event.getExplosion().getToBlow();
        for (BlockPos pos : list)
        {
            Level world = event.getLevel();
            if (UnifiedTileEntity.isRainbow(world.getBlockEntity(pos)))
            {
                event.getExplosion().getToBlow().remove(pos);
                world.removeBlockEntity(pos);
                world.removeBlock(pos, false);

                world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY() + 6f, pos.getZ(), new ItemStack(ModItems.RAINBOW_COAL.get())));
            }
        }
    }

}
