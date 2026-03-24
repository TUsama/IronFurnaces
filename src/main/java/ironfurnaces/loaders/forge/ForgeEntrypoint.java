//? if forge {
package ironfurnaces.loaders.forge;

import ironfurnaces.Config;
import ironfurnaces.blocks.furnaces.BlockWorkSpeedSyncer;
import ironfurnaces.capability.LegacyPlayerFurnacesListChecker;
import ironfurnaces.capability.ModCapabilities;
import ironfurnaces.init.ClientSetup;
import ironfurnaces.loaders.CommonInit;
import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.loaders.PacketInit;
import ironfurnaces.registration.*;
import ironfurnaces.tileentity.furnaces.pattern.FurnacePatternDefinitionReloadListener;
import ironfurnaces.tileentity.furnaces.pattern.render.FurnaceTextureScanner;
import ironfurnaces.tileentity.furnaces.pattern.upgrade.PatternUpgradeRuleReloadListener;
import ironfurnaces.update.UpdateChecker;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod(IronFurnaces.MOD_ID)
public class ForgeEntrypoint {

    public static IEventBus MOD_EVENT_BUS;

    public ForgeEntrypoint() {
        PacketInit.initPackets();

        MOD_EVENT_BUS = FMLJavaModLoadingContext.get().getModEventBus();

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.COMMON_CONFIG);

        MinecraftForge.EVENT_BUS.<EntityJoinLevelEvent>addListener(EventPriority.LOWEST, x -> {
            if (x.getEntity() instanceof ServerPlayer player && x.getLevel() instanceof ServerLevel level){
                BlockWorkSpeedSyncer.syncWhenPlayerJoin(player);
                LegacyPlayerFurnacesListChecker.validateFurnacesList(player, level);
            }
        });

        MinecraftForge.EVENT_BUS.<AddReloadListenerEvent>addListener(EventPriority.LOWEST, x -> {
            x.addListener(new FurnacePatternDefinitionReloadListener());
            x.addListener(new PatternUpgradeRuleReloadListener());
        });


        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.<RegisterClientReloadListenersEvent>addListener(EventPriority.LOWEST, x -> {
            x.registerReloadListener(FurnaceTextureScanner.INSTANCE);
        });

        MinecraftForge.EVENT_BUS.<TickEvent.PlayerTickEvent>addListener(event -> {
            if (!(event.player instanceof ServerPlayer serverPlayer)) return;
            if (event.phase != TickEvent.Phase.END) return;
            if (event.side.isClient()) return;

            serverPlayer.getCapability(ModCapabilities.PLAYER_RAINBOW_CONTEXT)
                    .ifPresent(cap -> cap.tick(serverPlayer));
        });


        CommonInit.init();

        Config.loadConfig(Config.CLIENT_CONFIG, FMLPaths.CONFIGDIR.get().resolve("ironfurnaces-client.toml"));
        Config.loadConfig(Config.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve("ironfurnaces.toml"));

/*
        if (Config.checkUpdates.get()) {
            new UpdateChecker();
        } else {
            IronFurnaces.LOGGER.warn("You have disabled Iron Furnaces's Update Checker, to re-enable: change the value of Update Checker in .minecraft->config->ironfurnaces-client.toml to 'true'.");
        }*/
    }
}
//?}
