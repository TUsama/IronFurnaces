//? if neoforge {
/*package ironfurnaces.loaders.neoforge;

import com.example.mymod.ExampleMod;
import com.mojang.logging.LogUtils;
import ironfurnaces.Config;
import ironfurnaces.blocks.furnaces.BlockWorkSpeedSyncer;
import ironfurnaces.init.ClientSetup;
import ironfurnaces.loaders.PacketInit;
import ironfurnaces.registration.*;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLPaths;
import net.minecraftforge.common.NeoForge;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import org.slf4j.Logger;

@Mod("examplemod")
public class NeoforgeEntrypoint {
    private static final Logger LOGGER = LogUtils.getLogger();

    public NeoforgeEntrypoint(IEventBus eventBus) {
        PacketInit.initPackets();

        ModContainer activeContainer = ModLoadingContext.get().getActiveContainer();
        activeContainer.registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);
        activeContainer.registerConfig(ModConfig.Type.SERVER, Config.COMMON_CONFIG);

        eventBus.addListener(ClientSetup::init);

        NeoForge.EVENT_BUS.<EntityJoinLevelEvent>addListener(x -> {
            if (x.getEntity() instanceof ServerPlayer player){
                BlockWorkSpeedSyncer.syncWhenPlayerJoin(player);
            }
        });

        ModMenus.register();
        ModCustomRecipe.register();
        ModBlocks.register();
        ModItems.register();
        LegacyFurnaceBlocks.register();
        ModItemGroups.register();
        ModAdvancements.register();
        ModLangs.register();


    }
}
*///?}
