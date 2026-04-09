//? if neoforge {
/*package ironfurnaces.loaders.neoforge;

import com.mojang.logging.LogUtils;
import ironfurnaces.Config;
import ironfurnaces.blocks.furnaces.BlockWorkSpeedSyncer;
import ironfurnaces.capability.LegacyPlayerFurnacesListChecker;
import ironfurnaces.capability.ModCapabilities;
import ironfurnaces.capability.PlayerDataHandler;
import ironfurnaces.loaders.ClientInit;
import ironfurnaces.loaders.CommonInit;
import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.loaders.PacketInit;
import ironfurnaces.registration.*;
import ironfurnaces.tileentity.BlockWirelessEnergyHeaterTile;
import ironfurnaces.tileentity.furnaces.BlockIronFurnaceTileBase;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.pattern.FurnacePatternDefinitionReloadListener;
import ironfurnaces.tileentity.furnaces.pattern.render.FurnaceTextureScanner;
import ironfurnaces.tileentity.furnaces.pattern.upgrade.PatternUpgradeRuleReloadListener;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLPaths;
import net.minecraftforge.capabilities.Capabilities;
import net.minecraftforge.capabilities.IBlockCapabilityProvider;
import net.minecraftforge.capabilities.ICapabilityProvider;
import net.minecraftforge.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.common.NeoForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.tick.PlayerTickEvent;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Mod(IronFurnaces.MOD_ID)
public class NeoforgeEntrypoint {
    private static final Logger LOGGER = LogUtils.getLogger();

    public NeoforgeEntrypoint(IEventBus modBus, ModContainer modContainer) {

        ModContainer activeContainer = ModLoadingContext.get().getActiveContainer();
        activeContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        modBus.<RegisterCapabilitiesEvent>addListener(x -> {
            x.registerBlockEntity(
                    Capabilities.ItemHandler.BLOCK,
                    ModBlockEntities.PATTERN_HOLDER.get(),
                    (o, direction) -> o.getSidedHandlers().get(direction));

            x.registerBlockEntity(
                    Capabilities.EnergyStorage.BLOCK,
                    ModBlockEntities.PATTERN_HOLDER.get(),
                    (o, direction) -> o.getFuel());

            x.registerBlockEntity(
                    Capabilities.ItemHandler.BLOCK,
                    ModBlocks.asBlockEntityType(LegacyFurnaceBlocks.COPPER_FURNACE),
                    SidedInvWrapper::new);

            x.registerBlockEntity(
                    Capabilities.EnergyStorage.BLOCK,
                    ModBlocks.asBlockEntityType(LegacyFurnaceBlocks.COPPER_FURNACE),
                    (o, direction) -> o.energyStorage);

            x.registerBlock(Capabilities.ItemHandler.BLOCK,
                    (level, pos, state, be, side) -> (side == null ? new InvWrapper((Container) be) : new SidedInvWrapper((WorldlyContainer)be, side)),
                    // blocks to register for
                    ModBlocks.HEATER.get());

            x.registerBlock(Capabilities.EnergyStorage.BLOCK,
                    (level, pos, state, be, side) -> ((BlockWirelessEnergyHeaterTile) be).getWrapper(),
                    // blocks to register for
                    ModBlocks.HEATER.get());
        });

        NeoForge.EVENT_BUS.<EntityJoinLevelEvent>addListener(x -> {
            if (x.getEntity() instanceof ServerPlayer player){
                BlockWorkSpeedSyncer.syncWhenPlayerJoin(player);
            }
        });

        NeoForge.EVENT_BUS.<EntityJoinLevelEvent>addListener(EventPriority.LOWEST, x -> {
            if (x.getEntity() instanceof ServerPlayer player && x.getLevel() instanceof ServerLevel level) {
                BlockWorkSpeedSyncer.syncWhenPlayerJoin(player);
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
}
*///?}
