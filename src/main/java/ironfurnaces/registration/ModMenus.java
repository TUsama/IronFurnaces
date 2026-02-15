package ironfurnaces.registration;

import com.tterrag.registrate.util.entry.MenuEntry;
import ironfurnaces.blocks.BlockWirelessEnergyHeater;
import ironfurnaces.container.BlockWirelessEnergyHeaterContainer;
import ironfurnaces.container.furnaces.LegacyUnifiedMenu;
import ironfurnaces.gui.BlockWirelessEnergyHeaterScreen;
import ironfurnaces.gui.furnaces.LegacyUnifiedMenuScreen;
import ironfurnaces.loaders.IronFurnaces;

import static ironfurnaces.loaders.IronFurnaces.REGISTRATE;

public class ModMenus {
    public static final MenuEntry<LegacyUnifiedMenu> UNIFIED_MENU = REGISTRATE
            .<LegacyUnifiedMenu, LegacyUnifiedMenuScreen>menu("unified_furnace_menu", (type, windowId, inv, buffer) -> new LegacyUnifiedMenu(type, windowId, inv.player.level(), buffer.readBlockPos(), inv, inv.player), () -> (menu, inv, displayName) -> new LegacyUnifiedMenuScreen(menu, inv, displayName, IronFurnaces.gui(menu.id)))
            .register();



        public static final MenuEntry<BlockWirelessEnergyHeaterContainer> HEATER_MENU = REGISTRATE
                .<BlockWirelessEnergyHeaterContainer, BlockWirelessEnergyHeaterScreen>menu(BlockWirelessEnergyHeater.HEATER,

                        (type, windowId, inv) -> new BlockWirelessEnergyHeaterContainer(type, windowId, inv.player.level(), inv, inv.player),

                        () -> BlockWirelessEnergyHeaterScreen::new)
                .register();


    public static void register() {

    }
}
