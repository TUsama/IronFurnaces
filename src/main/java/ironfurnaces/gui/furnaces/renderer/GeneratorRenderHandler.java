package ironfurnaces.gui.furnaces.renderer;

import ironfurnaces.gui.furnaces.FurnacePatternScreen;
import ironfurnaces.tileentity.furnaces.menu.FurnacePatternMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

import java.util.List;

public class GeneratorRenderHandler extends AbstractPatternScreenRenderHandler{
    
    private final AbstractWidget energyArea = FactoryRenderHandler.energyAreaGetter.apply(screen);
    
    public GeneratorRenderHandler(FurnacePatternScreen screen) {
        super(screen);
    }

    @Override
    public List<AbstractWidget> getRenderableWidget() {
        return List.of(energyArea);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, float partialTick, int mouseX, int mouseY) {
        Identifier texture = pickTexture();
        int i = screen.getGuiLeft();
        int j = screen.getGuiTop();

        guiGraphics.blit(texture, i, j, 0, 0, screen.getXSize(), screen.getYSize(), 256, 256);
        FurnacePatternMenu menu = screen.getMenu();
        if (menu.isLit()) {
            int k = menu.getLitProgress();
            guiGraphics.blit(texture, i + 57, j + 36 - k, 176, 12 - k, 14, k + 1, 256, 256);
        }

        int barHeight = 42;
        int barX = i + 109;
        int barY = j + 22;

        int l = menu.getMaxEnergy() > 0
                ? menu.getEnergyStored() * barHeight / menu.getMaxEnergy()
                : 0;

        energyArea.setPosition(barX, barY);

        if (l > 0) {
            guiGraphics.blit(
                    texture,
                    barX,
                    barY + (barHeight - l),   // 目标 y 下移，保证从底部开始长
                    176,
                    14 + (barHeight - l),     // 纹理 v 也同步下移
                    14,
                    l
                    , 256, 256
            );
        }
    }


}
