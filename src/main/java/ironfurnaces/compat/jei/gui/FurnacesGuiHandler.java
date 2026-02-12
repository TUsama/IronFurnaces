package ironfurnaces.compat.jei.gui;

import ironfurnaces.gui.furnaces.BlockIronFurnaceScreenBase;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import net.minecraft.client.renderer.Rect2i;

import java.util.Collection;
import java.util.List;

public class FurnacesGuiHandler implements IGuiContainerHandler<BlockIronFurnaceScreenBase<?>> {
    private static final List<Rect2i> WHEN_ON = List.of(new Rect2i(79, 35, 24, 100));
    private static final List<Rect2i> WHEN_OFF = List.of(new Rect2i(110, 35, 10, 25));
    @Override
    public List<Rect2i> getGuiExtraAreas(BlockIronFurnaceScreenBase<?> containerScreen) {
        if (containerScreen.showInventoryButtons()){
            return WHEN_ON;
        }
        return WHEN_OFF;

    }

    @Override
    public Collection<IGuiClickableArea> getGuiClickableAreas(BlockIronFurnaceScreenBase<?> containerScreen, double guiMouseX, double guiMouseY) {
        return IGuiContainerHandler.super.getGuiClickableAreas(containerScreen, guiMouseX, guiMouseY);
    }
}
