package ironfurnaces.gui.furnaces.component;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BooleanSupplier;

public class BaseBoolStatuImageButton extends ImageButton {
    private BooleanSupplier bool;

    public BaseBoolStatuImageButton(int x, int y, int width, int height, int xTexStart, int yTexStart, ResourceLocation resourceLocation, OnPress onPress, BooleanSupplier bool) {
        super(x, y, width, height, xTexStart, yTexStart, resourceLocation, onPress);
        this.bool = bool;
    }

    public BaseBoolStatuImageButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffTex, ResourceLocation resourceLocation, OnPress onPress, BooleanSupplier bool) {
        super(x, y, width, height, xTexStart, yTexStart, yDiffTex, resourceLocation, onPress);
        this.bool = bool;
    }

    public BaseBoolStatuImageButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffTex, ResourceLocation resourceLocation, int textureWidth, int textureHeight, OnPress onPress, BooleanSupplier bool) {
        super(x, y, width, height, xTexStart, yTexStart, yDiffTex, resourceLocation, textureWidth, textureHeight, onPress);
        this.bool = bool;
    }

    public BaseBoolStatuImageButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffTex, ResourceLocation resourceLocation, int textureWidth, int textureHeight, OnPress onPress, Component message, BooleanSupplier bool) {
        super(x, y, width, height, xTexStart, yTexStart, yDiffTex, resourceLocation, textureWidth, textureHeight, onPress, message);
        this.bool = bool;
    }

    @Override
    public void renderTexture(GuiGraphics guiGraphics, ResourceLocation texture, int x, int y, int uOffset, int vOffset, int textureDifference, int width, int height, int textureWidth, int textureHeight) {
        int i = vOffset;
        if (!this.isActive()) {
            i = vOffset + textureDifference * 2;
        } else if (this.isHovered() || bool.getAsBoolean()) {
            i = vOffset + textureDifference;
        }

        RenderSystem.enableDepthTest();
        guiGraphics.blit(texture, x, y, (float)uOffset, (float)i, width, height, textureWidth, textureHeight);
    }
}
