//? if neoforge {
/*package ironfurnaces.loaders.neoforge;

import com.mojang.logging.LogUtils;
import ironfurnaces.Config;
import ironfurnaces.blocks.furnaces.BlockWorkSpeedSyncer;
import ironfurnaces.capability.ModCapabilities;
import ironfurnaces.loaders.CommonInit;
import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.loaders.PacketInit;
import ironfurnaces.registration.*;
import ironfurnaces.tileentity.BlockWirelessEnergyHeaterTile;
import ironfurnaces.tileentity.furnaces.BlockIronFurnaceTileBase;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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
import net.minecraftforge.common.NeoForge;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Mod(IronFurnaces.MOD_ID)
public class NeoforgeEntrypoint {
    private static final Logger LOGGER = LogUtils.getLogger();

    public NeoforgeEntrypoint(IEventBus eventBus, ModContainer modContainer) {

        ModContainer activeContainer = ModLoadingContext.get().getActiveContainer();
        activeContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        eventBus.<RegisterCapabilitiesEvent>addListener(x -> {
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

        ModCapabilities.ATTACHMENTS.register(eventBus);
        CommonInit.init();


    }
}
*///?}
