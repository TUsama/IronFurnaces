//? if neoforge {
package ironfurnaces.loaders.neoforge;

import com.mojang.logging.LogUtils;
import dev.anvilcraft.lib.v2.registrum.util.entry.BlockEntry;
import ironfurnaces.Config;
import ironfurnaces.blocks.furnaces.BlockWorkSpeedSyncer;
import ironfurnaces.capability.LegacyPlayerFurnacesListChecker;
import ironfurnaces.capability.ModCapabilities;
import ironfurnaces.capability.PlayerDataHandler;
import ironfurnaces.config.GameplayConfig;
import ironfurnaces.loaders.ClientInit;
import ironfurnaces.loaders.CommonInit;
import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.registration.*;
import ironfurnaces.registration.data_component.PersistentEnergy;
import ironfurnaces.tileentity.heater.BlockWirelessEnergyHeaterTile;
import ironfurnaces.tileentity.furnaces.pattern.FurnacePatternDefinitionReloadListener;
import ironfurnaces.tileentity.furnaces.pattern.render.FurnaceTextureScanner;
import ironfurnaces.tileentity.furnaces.pattern.upgrade.PatternUpgradeRuleReloadListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.energy.ComponentEnergyStorage;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import org.slf4j.Logger;

@Mod(IronFurnaces.MOD_ID)
public class NeoforgeEntrypoint {
    private static final Logger LOGGER = LogUtils.getLogger();

    public NeoforgeEntrypoint(IEventBus modBus, ModContainer modContainer) {

        ModContainer activeContainer = ModLoadingContext.get().getActiveContainer();
        activeContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        //~ if >1.20.1 'Capabilities.ItemHandler' -> 'Capabilities.Item' {
        //~ if >1.20.1 'Capabilities.EnergyStorage' -> 'Capabilities.Energy' {
        modBus.<RegisterCapabilitiesEvent>addListener(x -> {
            x.registerBlockEntity(
                    Capabilities.Item.BLOCK,
                    ModBlockEntities.PATTERN_HOLDER.get(),
                    (o, direction) -> o.getSidedHandlers().get(direction));

            x.registerBlockEntity(
                    Capabilities.Energy.BLOCK,
                    ModBlockEntities.PATTERN_HOLDER.get(),
                    (o, direction) -> o.getFuel());


            x.registerBlock(Capabilities.Item.BLOCK,
                    (level, pos, state, be, side) -> (side == null ? new InvWrapper((Container) be) : new SidedInvWrapper((WorldlyContainer)be, side)),
                    // blocks to register for
                    ModBlocks.HEATER.get());

            x.registerBlock(Capabilities.Energy.BLOCK,
                    (level, pos, state, be, side) -> ((BlockWirelessEnergyHeaterTile) be).getWrapper(),
                    // blocks to register for
                    ModBlocks.HEATER.get());

            x.registerItem(Capabilities.Energy.ITEM,
                    (a, b) -> new PersistentEnergy(
                            a,
                            ModDataComponents.PERSISTENT_ENERGY.get(),
                            GameplayConfig.config.heater_item_capacity.get(),
                            1_000,
                            0
                    ),
                    ModItems.ITEM_HEATER.get());

            //? <1.21.11 {
            registerLegacyFurnaceCap(x);
//?}
        });
        //~}
        //~}



        NeoForge.EVENT_BUS.<EntityJoinLevelEvent>addListener(EventPriority.LOWEST, x -> {
            if (x.getEntity() instanceof ServerPlayer player && x.getLevel() instanceof ServerLevel level) {
                //? <1.21.11{
                BlockWorkSpeedSyncer.syncWhenPlayerJoin(player);
                //?}
                LegacyPlayerFurnacesListChecker.validateFurnacesList(player, level);
            }
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

    //? <1.21.11{
    private static void registerLegacyFurnaceCap(RegisterCapabilitiesEvent x) {
        registerLegacyFurnaceCap(x, LegacyFurnaceBlocks.IRON_FURNACE);
        registerLegacyFurnaceCap(x, LegacyFurnaceBlocks.GOLD_FURNACE);
        registerLegacyFurnaceCap(x, LegacyFurnaceBlocks.DIAMOND_FURNACE);
        registerLegacyFurnaceCap(x, LegacyFurnaceBlocks.EMERALD_FURNACE);
        registerLegacyFurnaceCap(x, LegacyFurnaceBlocks.COPPER_FURNACE);
        registerLegacyFurnaceCap(x, LegacyFurnaceBlocks.SILVER_FURNACE);
        registerLegacyFurnaceCap(x, LegacyFurnaceBlocks.MILLION_FURNACE);
        registerLegacyFurnaceCap(x, LegacyFurnaceBlocks.ALLTHEMODIUM_FURNACE);
        registerLegacyFurnaceCap(x, LegacyFurnaceBlocks.VIBRANIUM_FURNACE);
        registerLegacyFurnaceCap(x, LegacyFurnaceBlocks.UNOBTAINIUM_FURNACE);
        registerLegacyFurnaceCap(x, LegacyFurnaceBlocks.OBSIDIAN_FURNACE);
        registerLegacyFurnaceCap(x, LegacyFurnaceBlocks.CRYSTAL_FURNACE);
        registerLegacyFurnaceCap(x, LegacyFurnaceBlocks.NETHERITE_FURNACE);
    }

    private static void registerLegacyFurnaceCap(RegisterCapabilitiesEvent x, BlockEntry<?> entry) {
        x.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlocks.asBlockEntityType(entry),
                SidedInvWrapper::new
        );

        x.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlocks.asBlockEntityType(entry),
                (o, direction) -> o.energyStorage
        );
    }
//?}
}
//?}
