package ironfurnaces.gui.furnaces.component;

import ironfurnaces.loaders.IronFurnaces;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.resources.Identifier;


public class BaseImageButton extends ImageButton {


    public BaseImageButton(int x, int y, int width, int height, String baseId, OnPress onPress) {
        this(x, y, width, height, IronFurnaces.sprite(baseId + "_off"), IronFurnaces.sprite(baseId + "_on"), IronFurnaces.sprite(baseId + "_inactive"), onPress);
    }

    public BaseImageButton(int x, int y, int width, int height, Identifier off, Identifier on, Identifier inactive, OnPress onPress) {
        super(x, y, width, height, new WidgetSprites(off, inactive, on, inactive), onPress);
    }

    protected int getTextureWidth() {
        return getWidth();
    }

    protected int getTextureHeight() {
        return getHeight();
    }


    protected boolean shouldHighlight() {
        return isHovered();
    }


}
