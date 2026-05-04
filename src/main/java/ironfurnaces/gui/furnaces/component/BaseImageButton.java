package ironfurnaces.gui.furnaces.component;

import ironfurnaces.loaders.IronFurnaces;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

//? 1.20.1 {

//? } else {
import net.minecraft.client.gui.components.WidgetSprites;
//?}

public class BaseImageButton extends ImageButton {
    //? 1.20.1
    //private WidgetSprites sprites;

    public BaseImageButton(int x, int y, int width, int height, String baseId, OnPress onPress) {
        this(x, y, width, height, IronFurnaces.sprite(baseId + "_off"), IronFurnaces.sprite(baseId + "_on"), IronFurnaces.sprite(baseId + "_inactive"), onPress);
    }

    public BaseImageButton(int x, int y, int width, int height, ResourceLocation off, ResourceLocation on, ResourceLocation inactive, OnPress onPress) {
        //? 1.20.1 {
        /*super(x, y, width, height, 0, 0, 0, off, onPress);
        this.sprites = new WidgetSprites(off, inactive, on, inactive);
        *///? } else {
        super(x, y, width, height, new WidgetSprites(off, inactive, on, inactive), onPress);
        //?}

    }

    protected int getTextureWidth(){
        return getWidth();
    }

    protected int getTextureHeight(){
        return getHeight();
    }


    protected boolean shouldHighlight(){
        return isHovered();
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        ResourceLocation resourcelocation = this.sprites.get(this.isActive(), this.shouldHighlight());
        //? 1.20.1 {
        /*guiGraphics.blit(resourcelocation, getX(), getY(), 0, 0, getWidth(), getHeight(), getTextureWidth(), getTextureHeight());
        *///? } else {
        guiGraphics.blitSprite(resourcelocation, this.getX(), this.getY(), this.width, this.height);
        //?}
    }
}
