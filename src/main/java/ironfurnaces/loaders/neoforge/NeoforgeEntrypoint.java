//? if neoforge {
package ironfurnaces.loaders.neoforge;

import com.mojang.logging.LogUtils;
import ironfurnaces.capability.ModCapabilities;
import ironfurnaces.capability.PlayerDataHandler;
import ironfurnaces.config.GameplayConfig;
import ironfurnaces.loaders.ClientInit;
import ironfurnaces.loaders.CommonInit;
import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.registration.ModBlockEntities;
import ironfurnaces.registration.ModBlocks;
import ironfurnaces.registration.ModDataComponents;
import ironfurnaces.registration.ModItems;
import ironfurnaces.registration.data_component.PersistentEnergy;
import ironfurnaces.tileentity.furnaces.pattern.FurnacePatternDefinitionReloadListener;
import ironfurnaces.tileentity.furnaces.pattern.render.FurnaceTextureScanner;
import ironfurnaces.tileentity.furnaces.pattern.upgrade.PatternUpgradeRuleReloadListener;
import ironfurnaces.tileentity.heater.BlockWirelessEnergyHeaterTile;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.slf4j.Logger;

@Mod(IronFurnaces.MOD_ID)
public class NeoforgeEntrypoint {
    private static final Logger LOGGER = LogUtils.getLogger();

    public NeoforgeEntrypoint(IEventBus modBus, ModContainer modContainer) {

        modBus.<RegisterCapabilitiesEvent>addListener(x -> {
            x.registerBlockEntity(
                    Capabilities.Item.BLOCK,
                    ModBlockEntities.PATTERN_HOLDER.get(),
                    (o, direction) -> o.getSidedHandlers().get(direction));

            x.registerBlockEntity(
                    Capabilities.Energy.BLOCK,
                    ModBlockEntities.PATTERN_HOLDER.get(),
                    (o, direction) -> o.getFuel());

/*
            x.registerBlock(Capabilities.Item.BLOCK,
                    (level, pos, state, be, side) -> (side == null ? new WorldlyContainerWrapper(((BlockWirelessEnergyHeaterTile) be)) : new SidedInvWrapper((WorldlyContainer)be, side)),
                    ModBlocks.HEATER.get());
*/
            x.registerBlock(Capabilities.Energy.BLOCK,
                    (level, pos, state, be, side) -> ((BlockWirelessEnergyHeaterTile) be).getEnergy(),
                    // blocks to register for
                    ModBlocks.HEATER.get());

            x.registerItem(Capabilities.Energy.ITEM,
                    (a, b) -> new PersistentEnergy(
                            b,
                            ModDataComponents.PERSISTENT_ENERGY.get(),
                            GameplayConfig.config.heater_item_capacity.get()
                    ),
                    ModItems.ITEM_HEATER.get());


        });




        NeoForge.EVENT_BUS.<AddReloadListenerEvent>addListener(EventPriority.LOWEST, x -> {
            x.addListener(new FurnacePatternDefinitionReloadListener());
            x.addListener(new PatternUpgradeRuleReloadListener());
        });

        NeoForge.EVENT_BUS.<PlayerTickEvent.Post>addListener(event -> {
            if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) return;
            PlayerDataHandler.editRainbowContext(serverPlayer, x -> x.tick(serverPlayer));
        });

        modBus.<RegisterClientReloadListenersEvent>addListener(EventPriority.LOWEST, x -> {
            x.registerReloadListener(FurnaceTextureScanner.INSTANCE);
        });


        ModCapabilities.ATTACHMENTS.register(modBus);
        CommonInit.init();
        ClientInit.clientInit(NeoForge.EVENT_BUS, modBus);

    }


}
//?}
