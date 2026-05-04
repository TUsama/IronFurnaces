//? if forge {
/*package ironfurnaces.loaders.forge;

import ironfurnaces.Config;
import ironfurnaces.blocks.furnaces.BlockWorkSpeedSyncer;
import ironfurnaces.capability.LegacyPlayerFurnacesListChecker;
import ironfurnaces.capability.ModCapabilities;
import ironfurnaces.loaders.CommonInit;
import ironfurnaces.loaders.ClientInit;
import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.tileentity.furnaces.pattern.FurnacePatternDefinitionReloadListener;
import ironfurnaces.tileentity.furnaces.pattern.render.FurnaceTextureScanner;
import ironfurnaces.tileentity.furnaces.pattern.upgrade.PatternUpgradeRuleReloadListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.common.MinecraftForge;
import net.neoforged.neoforge.common.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.eventbus.api.EventPriority;
import net.neoforged.neoforge.eventbus.api.IEventBus;
import net.neoforged.neoforge.fml.DistExecutor;
import net.neoforged.neoforge.fml.ModLoadingContext;
import net.neoforged.neoforge.fml.common.Mod;
import net.neoforged.neoforge.fml.config.ModConfig;
import net.neoforged.neoforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.fml.loading.FMLPaths;

@Mod(IronFurnaces.MOD_ID)
public class ForgeEntrypoint {

    public static IEventBus MOD_EVENT_BUS;

    public ForgeEntrypoint() {

        MOD_EVENT_BUS = FMLJavaModLoadingContext.get().getModEventBus();

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.COMMON_CONFIG);

        MinecraftForge.EVENT_BUS.<EntityJoinLevelEvent>addListener(EventPriority.LOWEST, x -> {
            if (x.getEntity() instanceof ServerPlayer player && x.getLevel() instanceof ServerLevel level) {
                BlockWorkSpeedSyncer.syncWhenPlayerJoin(player);
                LegacyPlayerFurnacesListChecker.validateFurnacesList(player, level);
            }
        });

        MinecraftForge.EVENT_BUS.<AddReloadListenerEvent>addListener(EventPriority.LOWEST, x -> {
            x.addListener(new FurnacePatternDefinitionReloadListener());
            x.addListener(new PatternUpgradeRuleReloadListener());
        });

        MinecraftForge.EVENT_BUS.<TickEvent.PlayerTickEvent>addListener(event -> {
            if (!(event.player instanceof ServerPlayer serverPlayer)) return;
            if (event.phase != TickEvent.Phase.END) return;

            serverPlayer.getCapability(ModCapabilities.PLAYER_RAINBOW_CONTEXT)
                    .ifPresent(cap -> cap.tick(serverPlayer));
        });

        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.<RegisterClientReloadListenersEvent>addListener(EventPriority.LOWEST, x -> {
            x.registerReloadListener(FurnaceTextureScanner.INSTANCE);
        });
        modBus.<RegisterCapabilitiesEvent>addListener(EventPriority.LOWEST, x -> {
            ModCapabilities.register(x);
        });


        CommonInit.init();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientInit.clientInit(MinecraftForge.EVENT_BUS, modBus));


        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> Config.loadConfig(Config.CLIENT_CONFIG, FMLPaths.CONFIGDIR.get().resolve("ironfurnaces-client.toml")));
        Config.loadConfig(Config.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve("ironfurnaces.toml"));
/^
        if (Config.checkUpdates.get()) {
            new UpdateChecker();
        } else {
            IronFurnaces.LOGGER.warn("You have disabled Iron Furnaces's Update Checker, to re-enable: change the value of Update Checker in .minecraft->config->ironfurnaces-client.toml to 'true'.");
        }^/
    }
}
*///?}
