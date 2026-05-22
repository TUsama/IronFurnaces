package ironfurnaces.gui;

import ironfurnaces.container.BlockWirelessEnergyHeaterContainer;
import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.util.StringHelper;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public abstract class BlockWirelessEnergyHeaterScreenBase<T extends BlockWirelessEnergyHeaterContainer> extends AbstractContainerScreen<T> {

    public Identifier GUI = IronFurnaces.id("textures/gui/heater.png");
    Inventory playerInv;
    Component name;

    public BlockWirelessEnergyHeaterScreenBase(T t, Inventory inv, Component name) {
        super(t, inv, name);
        playerInv = inv;
        this.name = name;
    }


    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int xm, int ym) {
        super.extractLabels(graphics, xm, ym);
        graphics.text(font, this.playerInv.getDisplayName(), 7, this.getImageHeight() - 93, 4210752, false);
        graphics.text(font, name, this.getImageWidth() / 2 - this.minecraft.font.width(name.getString()) / 2, 6, 4210752, false);

        int actualMouseX = xm - ((this.width - this.getImageWidth()) / 2);
        int actualMouseY = ym - ((this.height - this.getImageHeight()) / 2);
        if (actualMouseX >= 68 && actualMouseX <= 108 && actualMouseY >= 64 && actualMouseY <= 76) {
            int energy = ((BlockWirelessEnergyHeaterContainer) this.getMenu()).getEnergy();
            int capacity = ((BlockWirelessEnergyHeaterContainer) this.getMenu()).getMaxEnergy();
            graphics.setTooltipForNextFrame(this.font, Component.literal(StringHelper.displayEnergy(energy, capacity).get(0)), actualMouseX, actualMouseY);

        }
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractContents(graphics, mouseX, mouseY, a);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI, leftPos, topPos, 0, 0, this.getImageWidth(), this.getImageHeight(), 256, 256);

        int i;
        i = this.getMenu().getEnergyScaled(42);
        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI, leftPos + 67, topPos + 63, 176, 0, i + 1, 14, 256, 256);
    }


}
