package ironfurnaces.gui.furnaces.component;

import net.minecraft.client.Minecraft;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.resources.Identifier;

public class ReversibleImageButton extends BaseImageButton{
    protected final OnPress rightClick;

    public ReversibleImageButton(int x, int y, int width, int height, String baseId, OnPress onPress, OnPress rightClick) {
        super(x, y, width, height, baseId, onPress);
        this.rightClick = rightClick;
    }

    public ReversibleImageButton(int x, int y, int width, int height, Identifier off, Identifier on, Identifier inactive, OnPress onPress, OnPress rightClick) {
        super(x, y, width, height, off, on, inactive, onPress);
        this.rightClick = rightClick;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (this.active && this.visible && event.buttonInfo().button() == 1) {
            boolean flag = this.isMouseOver(event.x(), event.y());
            if (flag) {
                this.playDownSound(Minecraft.getInstance().getSoundManager());
                this.rightClick.onPress(this);
                return true;
            }
        }
        return super.mouseClicked(event, doubleClick);
    }
}
