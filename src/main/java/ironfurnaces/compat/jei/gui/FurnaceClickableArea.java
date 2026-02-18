package ironfurnaces.compat.jei.gui;

import ironfurnaces.gui.furnaces.BlockIronFurnaceScreenBase;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.recipe.IFocusFactory;
import mezz.jei.api.runtime.IRecipesGui;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;

public class FurnaceClickableArea implements IGuiClickableArea {
    private BlockIronFurnaceScreenBase<?> containerScreen;

    public FurnaceClickableArea(BlockIronFurnaceScreenBase<?> containerScreen) {
        this.containerScreen = containerScreen;
    }

    @Override
    public Rect2i getArea() {
        return new Rect2i(110, 35, 10, 30);
    }

    @Override
    public void onClick(IFocusFactory focusFactory, IRecipesGui recipesGui) {
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip) {
        //tooltip.add(Component.literal("111111"));
    }
}
