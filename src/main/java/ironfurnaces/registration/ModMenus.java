package ironfurnaces.registration;

import com.tterrag.registrate.util.entry.MenuEntry;
import ironfurnaces.blocks.BlockWirelessEnergyHeater;
import ironfurnaces.container.BlockWirelessEnergyHeaterContainer;
import ironfurnaces.container.furnaces.LegacyUnifiedMenu;
import ironfurnaces.gui.BlockWirelessEnergyHeaterScreen;
import ironfurnaces.gui.furnaces.FurnacePatternScreen;

import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.menu.FurnacePatternMenu;
import ironfurnaces.tileentity.furnaces.pattern.FurnacePattern;
import ironfurnaces.tileentity.furnaces.pattern.IFurnaceStats;

//? <1.21.11{
import ironfurnaces.gui.furnaces.LegacyUnifiedMenuScreen;
//?}

import net.minecraft.core.BlockPos;

import static ironfurnaces.loaders.IronFurnaces.REGISTRATE;

public class ModMenus {
    //? <1.21.11{
    public static final MenuEntry<LegacyUnifiedMenu> UNIFIED_MENU = REGISTRATE
            .<LegacyUnifiedMenu, LegacyUnifiedMenuScreen>menu("unified_furnace_menu", (type, windowId, inv, buffer) -> new LegacyUnifiedMenu(type, windowId, inv.player.level(), buffer.readBlockPos(), inv, inv.player),
                    () -> (menu, inv, displayName) -> new LegacyUnifiedMenuScreen(menu, inv, displayName, IronFurnaces.gui(menu.id)))
            .register();
//?}


    public static final MenuEntry<BlockWirelessEnergyHeaterContainer> HEATER_MENU = REGISTRATE
            .<BlockWirelessEnergyHeaterContainer, BlockWirelessEnergyHeaterScreen>menu(BlockWirelessEnergyHeater.HEATER,

                    (type, windowId, inv, buf) -> new BlockWirelessEnergyHeaterContainer(type, windowId, inv.player.level(), inv, inv.player, buf.readBlockPos()),

                    () -> BlockWirelessEnergyHeaterScreen::new)
            .register();


    public static final MenuEntry<FurnacePatternMenu> NEW_FURNACE_MENU = REGISTRATE
            .<FurnacePatternMenu, FurnacePatternScreen>menu(FurnacePatternMenu.ID,

                    (type, windowId, inv, buffer) -> {
                        FurnacePattern pattern = buffer.readJsonWithCodec(FurnacePattern.REF_CODEC);
                        IFurnaceStats iFurnaceStats = buffer.readJsonWithCodec(IFurnaceStats.CODEC);
                        BlockPos blockPos = buffer.readBlockPos();
                        FurnacePatternBlockEntity blockEntity = ((FurnacePatternBlockEntity) inv.player.level().getBlockEntity(blockPos));
                        blockEntity.getAugments().refreshState();
                        blockEntity.updatePattern(pattern);
                        blockEntity.updateFurnaceStats(iFurnaceStats);
                        return new FurnacePatternMenu(type, windowId, blockEntity, inv, blockPos, blockEntity.getDataAccess());
                    },

                    () -> FurnacePatternScreen::new)
            .register();


    public static void register() {

    }
}
