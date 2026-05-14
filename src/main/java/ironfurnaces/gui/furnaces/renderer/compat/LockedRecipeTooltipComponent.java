package ironfurnaces.gui.furnaces.renderer.compat;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.item.ItemStack;

public class LockedRecipeTooltipComponent implements ClientTooltipComponent {
    private ItemStack stack;

    public LockedRecipeTooltipComponent(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public int getHeight(Font font) {
        return 18;
    }

    @Override
    public int getWidth(Font font) {
        return 18;
    }

    @Override
    public void extractImage(Font font, int x, int y, int w, int h, GuiGraphicsExtractor graphics) {
        ClientTooltipComponent.super.extractImage(font, x, y, w, h, graphics);
        graphics.item(stack, 0, 0);
    }


}
