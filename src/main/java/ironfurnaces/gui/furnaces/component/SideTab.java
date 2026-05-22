package ironfurnaces.gui.furnaces.component;

import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.tileentity.furnaces.menu.MenuConstant;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

import java.util.function.BooleanSupplier;

public class SideTab extends BaseImageButton implements IUpdateContext {
    private final SideTabPanel panel;
    private final BooleanSupplier openStateCallback;
    public PositionContext positionContext;
    private TabState state = TabState.CLOSE;

    public SideTab(int x, int y, int width, int height, String baseId, OnPress onPress, SideTabPanel panel, BooleanSupplier openStateCallback, PositionContext positionContext) {
        this(x, y, width, height, IronFurnaces.sprite(baseId), IronFurnaces.sprite(baseId), IronFurnaces.sprite(baseId), onPress, positionContext, panel, openStateCallback);

    }

    public SideTab(int x, int y, int width, int height, Identifier off, Identifier on, Identifier inactive, OnPress onPress, PositionContext positionContext, SideTabPanel panel, BooleanSupplier openStateCallback) {
        super(x, y, width, height, off, on, inactive, onPress);
        this.positionContext = positionContext;
        this.panel = panel;
        this.openStateCallback = openStateCallback;
    }

    @Override
    protected int getTextureWidth() {
        return 23;
    }

    @Override
    protected int getTextureHeight() {
        return 26;
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        this.setPosition(positionContext.buttonX, positionContext.y);
        TabState tabState = this.openStateCallback.getAsBoolean() ? TabState.OPEN : TabState.CLOSE;
        if (this.state != tabState) {
            this.state = tabState;
            WidgetGroup widgets = panel.getWidgets();
            if (widgets != null) {
                this.state.groupHandler.accept(widgets);
            }
        }
        switch (state) {
            case OPEN -> {
                panel.extractRenderState(graphics, mouseX, mouseY, a);
                panel.update(positionContext);
            }
            case CLOSE -> {
                super.extractContents(graphics, mouseX, mouseY, a);
                this.update(positionContext);
            }
        }

    }


    @Override
    public void update(PositionContext context) {
        context.moveDown(MenuConstant.SIDE_BUTTON_HEIGHT);
    }
}
