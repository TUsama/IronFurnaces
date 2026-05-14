package ironfurnaces.registration;

import dev.anvilcraft.lib.v2.registrum.util.entry.MenuEntry;
import ironfurnaces.blocks.BlockWirelessEnergyHeater;
import ironfurnaces.container.BlockWirelessEnergyHeaterContainer;
import ironfurnaces.gui.BlockWirelessEnergyHeaterScreen;
import ironfurnaces.gui.furnaces.FurnacePatternScreen;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.menu.FurnacePatternMenu;
import ironfurnaces.tileentity.furnaces.pattern.FurnacePattern;
import ironfurnaces.tileentity.furnaces.pattern.IFurnaceStats;
import net.minecraft.core.BlockPos;

import static ironfurnaces.loaders.IronFurnaces.REGISTRATE;

public class ModMenus {

    public static final MenuEntry<BlockWirelessEnergyHeaterContainer> HEATER_MENU = REGISTRATE
            .<BlockWirelessEnergyHeaterContainer, BlockWirelessEnergyHeaterScreen>menu(BlockWirelessEnergyHeater.HEATER,

                    (type, windowId, inv, buf) -> new BlockWirelessEnergyHeaterContainer(windowId, inv, buf),

                    () -> BlockWirelessEnergyHeaterScreen::new)
            .register();


    public static final MenuEntry<FurnacePatternMenu> NEW_FURNACE_MENU = REGISTRATE
            .<FurnacePatternMenu, FurnacePatternScreen>menu(FurnacePatternMenu.ID,

                    (type, windowId, inv, buffer) -> {
                        FurnacePattern pattern = buffer.readLenientJsonWithCodec(FurnacePattern.REF_CODEC);
                        IFurnaceStats iFurnaceStats = buffer.readLenientJsonWithCodec(IFurnaceStats.CODEC);
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
