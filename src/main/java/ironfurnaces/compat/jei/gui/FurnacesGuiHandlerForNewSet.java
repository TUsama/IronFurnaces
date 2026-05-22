package ironfurnaces.compat.jei.gui;

import ironfurnaces.gui.furnaces.FurnacePatternScreen;
import ironfurnaces.tileentity.furnaces.menu.FurnacePatternMenu;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import net.minecraft.client.renderer.Rect2i;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FurnacesGuiHandlerForNewSet implements IGuiContainerHandler<FurnacePatternScreen> {

    @Override
    public List<Rect2i> getGuiExtraAreas(FurnacePatternScreen containerScreen) {
        List<Rect2i> rect2is = new ArrayList<>();
        int guiLeft = containerScreen.getLeftPos();
        int guiTop = containerScreen.getTopPos();
        int width = 13;
        int sidePanelHeight = 100;
        int sidePanelWidth = 35;
        int currentTop = guiTop;

        FurnacePatternMenu menu = containerScreen.getMenu();
        if (menu.openSetting) {
            rect2is.add(new Rect2i(guiLeft - width - sidePanelWidth, currentTop, sidePanelWidth, sidePanelHeight));
            currentTop += sidePanelHeight;
        } else {
            rect2is.add(new Rect2i(guiLeft - width, currentTop, width, width));
            currentTop += width;
        }

        if (menu.openRemaining) {
            rect2is.add(new Rect2i(guiLeft - width - sidePanelWidth, currentTop + width, sidePanelWidth, sidePanelHeight));
            currentTop += sidePanelHeight;
        } else {
            rect2is.add(new Rect2i(guiLeft - width, currentTop + width, width, width));
            currentTop += width;
        }

        if (menu.openAugment) {
            rect2is.add(new Rect2i(guiLeft - width - sidePanelWidth, currentTop + width + width, sidePanelWidth, sidePanelHeight));
        } else {
            rect2is.add(new Rect2i(guiLeft - width, currentTop + width + width, width, width));
        }
        return rect2is;

    }


    @Override
    public Collection<IGuiClickableArea> getGuiClickableAreas(FurnacePatternScreen containerScreen, double guiMouseX, double guiMouseY) {
        return List.of(new FurnaceClickableAreaForNewSet(containerScreen));
    }
}
