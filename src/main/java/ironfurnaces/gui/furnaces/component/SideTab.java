package ironfurnaces.gui.furnaces.component;

import ironfurnaces.tileentity.furnaces.menu.MenuConstant;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BooleanSupplier;

public class SideTab extends ImageButton implements IUpdateContext {
    private final SideTabPanel panel;
    public PositionContext positionContext;
    private TabState state = TabState.CLOSE;
    private final BooleanSupplier openStateCallback;



    public SideTab(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffTex, ResourceLocation resourceLocation, OnPress onPress, PositionContext positionContext, SideTabPanel panel, BooleanSupplier openStateCallback) {
        super(x, y, width, height, xTexStart, yTexStart, yDiffTex, resourceLocation, onPress);
        this.positionContext = positionContext;
        this.panel = panel;
        this.openStateCallback = openStateCallback;
    }


    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.setPosition(positionContext.buttonX, positionContext.y);
        TabState tabState = this.openStateCallback.getAsBoolean() ? TabState.OPEN : TabState.CLOSE;
        if (this.state != tabState) {
            this.state = tabState;
            WidgetGroup widgets = panel.getWidgets();
            if (widgets != null){
                this.state.groupHandler.accept(widgets);
            }
        }
        switch (state) {
            case OPEN -> {
                panel.render(guiGraphics, mouseX, mouseY, partialTick);
                panel.update(positionContext);
            }
            case CLOSE -> {
                super.render(guiGraphics, mouseX, mouseY, partialTick);
                this.update(positionContext);
            }
        }
    }

    @Override
    public void update(PositionContext context) {
        context.moveDown(MenuConstant.SIDE_BUTTON_HEIGHT);
    }
}
