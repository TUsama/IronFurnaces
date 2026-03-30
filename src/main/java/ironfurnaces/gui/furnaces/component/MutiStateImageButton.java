package ironfurnaces.gui.furnaces.component;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class MutiStateImageButton<T extends Enum<T>> extends Button {
    protected final ResourceLocation resourceLocation;
    protected final int xTexStart;
    protected final int yTexStart;
    protected final int xDiffTex;
    protected final int yDiffTex;
    protected final int textureWidth;
    protected final int textureHeight;
    @Setter
    @Getter
    protected T currentState;

    public MutiStateImageButton(int x, int y, int width, int height, int xTexStart, int yTexStart, ResourceLocation resourceLocation, Button.OnPress onPress, T initState) {
        this(x, y, width, height, xTexStart, yTexStart, width, height, resourceLocation, 256, 256, onPress, initState);
    }

    public MutiStateImageButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int xDiffTex, int yDiffTex, ResourceLocation resourceLocation, Button.OnPress onPress, T initState) {
        this(x, y, width, height, xTexStart, yTexStart, xDiffTex, yDiffTex, resourceLocation, 256, 256, onPress, initState);
    }

    public MutiStateImageButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int xDiffTex, int yDiffTex, ResourceLocation resourceLocation, int textureWidth, int textureHeight, Button.OnPress onPress, T initState) {
        this(x, y, width, height, xTexStart, yTexStart, xDiffTex, yDiffTex, resourceLocation, textureWidth, textureHeight, onPress, CommonComponents.EMPTY, initState);
    }

    public MutiStateImageButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int xDiffTex, int yDiffTex, ResourceLocation resourceLocation, int textureWidth, int textureHeight, Button.OnPress onPress, Component message, T initState) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.xTexStart = xTexStart;
        this.yTexStart = yTexStart;
        this.yDiffTex = yDiffTex;
        this.resourceLocation = resourceLocation;
        this.currentState = initState;
        this.xDiffTex = xDiffTex;
    }

    public void renderTexture(GuiGraphics guiGraphics, ResourceLocation texture, int x, int y, int uOffset, int vOffset, int textureDifference, int width, int height, int textureWidth, int textureHeight) {
        int v = vOffset + textureDifference * currentState.ordinal();
        int u = uOffset;
        if (!this.isActive()) {
            u += xDiffTex * 2;
        } else if (this.isHovered()) {
            u += xDiffTex;
        }

        RenderSystem.enableDepthTest();
        guiGraphics.blit(texture, x, y, (float) u, (float) v, width, height, textureWidth, textureHeight);
    }

    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderTexture(guiGraphics, this.resourceLocation, this.getX(), this.getY(), this.xTexStart, this.yTexStart, this.yDiffTex, this.width, this.height, this.textureWidth, this.textureHeight);
    }

    public void updateState(T state){
        this.currentState = state;
    }
}
