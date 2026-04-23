package ironfurnaces.gui.furnaces.renderer.compat;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.item.ItemStack;

public class LockedRecipeTooltipComponent implements ClientTooltipComponent {
    private ItemStack stack;

    public LockedRecipeTooltipComponent(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public int getHeight() {
        return 18;
    }

    @Override
    public int getWidth(Font font) {
        return 18;
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
        guiGraphics.renderItem(stack, 0, 0);
    }
}
