package ironfurnaces.gui.furnaces.component;

import ironfurnaces.gui.furnaces.FurnacePatternScreen;
import ironfurnaces.tileentity.furnaces.menu.MenuConstant;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.renderer.RenderPipelines;

public class SideTabPanel implements Renderable, IUpdateContext {
    @Getter
    private final WidgetGroup widgets;
    private final PositionContext context;
    private final int index;

    public SideTabPanel(WidgetGroup widgets, PositionContext context, int index) {
        this.widgets = widgets;
        this.context = context;
        this.index = index;
    }


    @Override
    public void update(PositionContext context) {
        context.moveDown(MenuConstant.SIDE_PANEL_HEIGHT);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphicsExtractor, int i, int i1, float v) {
        guiGraphicsExtractor.blit(RenderPipelines.GUI_TEXTURED, FurnacePatternScreen.WIDGET, context.panelX, context.y, 59 * index, 68, MenuConstant.SIDE_PANEL_WIDTH, MenuConstant.SIDE_PANEL_HEIGHT, 256, 256);
    }
}
