package ironfurnaces.gui.furnaces.renderer;

import com.clefal.nirvana_lib.utils.NetworkUtils;
import ironfurnaces.gui.furnaces.FurnacePatternScreen;
import ironfurnaces.gui.furnaces.component.BaseBoolStatuImageButton;
import ironfurnaces.gui.furnaces.component.WidgetGroup;
import ironfurnaces.network.C2SUpdateFurnaceSettingPacket;
import ironfurnaces.network.C2SUpdateMenuPacket;
import ironfurnaces.tileentity.furnaces.menu.FurnacePatternMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FastColor;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public class FactoryRenderHandler extends AbstractPatternScreenRenderHandler {
    public static final Function<FurnacePatternScreen, AbstractWidget> energyAreaGetter = furnacePatternScreen -> new AbstractWidget(0, 0, 14, 42, Component.empty()) {
        @Override
        protected void renderWidget(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
            this.setTooltip(Tooltip.create(
                    Component.translatable("screen.ironfurnaces.energy_slot", furnacePatternScreen.getMenu().getEnergyStored(), furnacePatternScreen.getMenu().getMaxEnergy())
            ));
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

        }

    };

    protected ImageButton autoFillButton = new BaseBoolStatuImageButton(0, 0, 14, 14, "auto_fill", button -> NetworkUtils.sendToServer(new C2SUpdateFurnaceSettingPacket(screen.getMenu().getSettingsV2().withAutoFill(!screen.getMenu().getSettingsV2().autoFill()), screen.getMenu().bePos)), () -> screen.getMenu().getSettingsV2().autoFill()) {
        @Override
        public @Nullable Tooltip getTooltip() {
            return Tooltip.create(Component.translatable("ironfurnaces.furnace_setting.auto_fill", screen.getMenu().getSettingsV2().autoFill()));
        }

        @Override
        public void renderWidget(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
            super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
            this.setTooltip(getTooltip());
        }
    };
    int y = screen.getGuiTop() + 70;
    int i1 = screen.getGuiLeft() + 70;
    private AbstractWidget energyArea = energyAreaGetter.apply(screen);
    private PageButton forwardButton = new PageButton(i1 + 38, y, true, (button) -> NetworkUtils.sendToServer(new C2SUpdateMenuPacket(3)), false);
    private PageButton backButton = new PageButton(i1 - 8, y, false, (button) -> NetworkUtils.sendToServer(new C2SUpdateMenuPacket(4)), false);
    private WidgetGroup pageGroup = new WidgetGroup(forwardButton, backButton);

    public FactoryRenderHandler(FurnacePatternScreen screen) {
        super(screen);
    }

    @Override
    public List<AbstractWidget> getRenderableWidget() {
        return List.of(energyArea, autoFillButton, forwardButton, backButton);
    }


    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, float partialTick, int mouseX, int mouseY) {

        Identifier texture = pickTexture();
        int i = screen.getGuiLeft();
        int j = screen.getGuiTop();
        guiGraphics.blit(texture, i, j, 0, 0, screen.getXSize(), screen.getYSize());
        int columns = 3;
        int visibleRows = 3;
        int pageSize = columns * visibleRows;
        FurnacePatternMenu menu = screen.getMenu();

        var input = menu.getFactoryInput();
        int currentPage = input.getCurrentPage();
        int startIndex = currentPage * pageSize;
        int endIndex = Math.min(startIndex + pageSize, input.size);
        int slotIndex = 0;
        for (int index = startIndex; index < endIndex; index++) {
            int indexInPage = index % pageSize;
            int col = indexInPage % columns;
            int row = indexInPage / columns;

            guiGraphics.blit(texture, i + 35 + col * 18, j + 16 + row * 18, 176, 55, 18, 18);
            int verticalBurnProgress = menu.getVerticalBurnProgress(slotIndex);
            if (verticalBurnProgress != 0) {
                guiGraphics.fill(i + 35 + col * 18 + 1, j + 16 + row * 18 + verticalBurnProgress, i + 35 + col * 18 + 18, j + 16 + row * 18 + 18, FastColor.ARGB32.color(100, 255, 255, 255));
                //guiGraphics.blit(texture, i + 35 + col * 18, j + 16 + row * 18 - verticalBurnProgress, 176, 55, 18, 18);
            }
            guiGraphics.blit(texture, i + 105 + col * 18, j + 16 + row * 18, 176, 55, 18, 18);
            slotIndex++;
        }

        if (menu.isLit()) {
            int k = menu.getLitProgress();
            guiGraphics.blit(texture, i + 89, j + 36 - k + 15, 176, 12 - k, 14, k + 1);
        }

        int barHeight = 42;
        int barX = i + 9;
        int barY = j + 7;

        energyArea.setPosition(barX, barY);
        this.autoFillButton.setPosition(i + 9, j + 56);

        int l = menu.getMaxEnergy() > 0
                ? menu.getEnergyStored() * barHeight / menu.getMaxEnergy()
                : 0;

        if (l > 0) {
            guiGraphics.blit(texture, barX, barY + (barHeight - l), 176, 14 + (barHeight - l), 14, l);
        }
        if (input.getPageCount() > 1) {
            pageGroup.activeAll();
        } else {
            pageGroup.deactivateAll();
        }
    }
}
