package ironfurnaces.gui.furnaces.renderer;

import ironfurnaces.gui.furnaces.FurnacePatternScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class VanillaFurnaceRenderHandler extends AbstractPatternScreenRenderHandler{

    //? >1.20.1
    private final Identifier VANILLA_LIT_PROGRESS = Identifier.withDefaultNamespace("container/furnace/lit_progress");

    public VanillaFurnaceRenderHandler(FurnacePatternScreen screen) {
        super(screen);
    }

    @Override
    public void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int i = screen.getGuiLeft();
        int j = screen.getGuiTop();
        guiGraphics.blit(VANILLA, i, j, 0, 0, screen.getXSize(), screen.getYSize());
        if (screen.getMenu().isLit()) {
            //? 1.20.1 {
            /*int k = screen.getMenu().getLitProgress();
            guiGraphics.blit(VANILLA, i + 56, j + 36 + 12 - k, 176, 12 - k, 14, k + 1);
            *///? } else {
            int k = 14;
            int l = Mth.ceil(screen.getMenu().getLitProgress()) + 1;
            guiGraphics.blitSprite(VANILLA_LIT_PROGRESS, 14, 14, 0, 14 - l, i + 56, j + 36 + 14 - l, 14, l);
            //?}

        }

        int l = screen.getMenu().getBurnProgress(0);
        guiGraphics.blit(VANILLA, i + 79, j + 34, 176, 14, l + 1, 16);
    }
}
