package ironfurnaces.util;


import ironfurnaces.capability.PlayerFurnacesListProvider;
import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.registration.ModItems;
import ironfurnaces.tileentity.furnaces.UnifiedTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EventHandler {

    @SubscribeEvent
    public static void playerEvent(AttachCapabilitiesEvent<Entity> event)
    {
        if (event.getObject() instanceof Player)
        {
            event.addCapability(new ResourceLocation(IronFurnaces.MOD_ID, "furnaces_list"), new PlayerFurnacesListProvider());
        }
    }

    @SubscribeEvent
    public static void explosionEvent(ExplosionEvent event)
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
