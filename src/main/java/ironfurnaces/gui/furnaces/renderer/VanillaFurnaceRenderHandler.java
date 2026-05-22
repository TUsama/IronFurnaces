package ironfurnaces.gui.furnaces.renderer;

import ironfurnaces.gui.furnaces.FurnacePatternScreen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class VanillaFurnaceRenderHandler extends AbstractPatternScreenRenderHandler{

    private static final Identifier LIT_PROGRESS_SPRITE = Identifier.withDefaultNamespace("container/furnace/lit_progress");
    private static final Identifier BURN_PROGRESS_SPRITE = Identifier.withDefaultNamespace("container/furnace/burn_progress");

    public VanillaFurnaceRenderHandler(FurnacePatternScreen screen) {
        super(screen);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, float partialTick, int mouseX, int mouseY) {
        int i = screen.getLeftPos();
        int j = screen.getTopPos();
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, VANILLA, i, j, 0.0F, 0.0F, 176, 166, 256, 256);
        if (screen.getMenu().isLit()) {
            int k = 14;
            int l = Mth.ceil(screen.getMenu().getLitProgress()) + 1;
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, LIT_PROGRESS_SPRITE, 14, 14, 0, 14 - l, i + 56, j + 36 + 14 - l, 14, l);
        }

        int l = screen.getMenu().getBurnProgress(0);
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, BURN_PROGRESS_SPRITE, 24, 16, 0, 0, i + 79, j + 34, l, 16);
    }
}
