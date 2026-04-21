package ironfurnaces.compat.jei.gui;

import ironfurnaces.gui.furnaces.FurnacePatternScreen;
import ironfurnaces.tileentity.furnaces.cache.IRecipeTypeHandler;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.recipe.IFocusFactory;
import mezz.jei.api.runtime.IRecipesGui;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;

public class FurnaceClickableAreaForNewSet implements IGuiClickableArea {
    private FurnacePatternScreen containerScreen;

    public FurnaceClickableAreaForNewSet(FurnacePatternScreen containerScreen) {
        this.containerScreen = containerScreen;
    }

    @Override
    public Rect2i getArea() {
        return containerScreen.getMenu().getMode().getArea(containerScreen.getMenu());
    }

    @Override
    public void onClick(IFocusFactory focusFactory, IRecipesGui recipesGui) {

        IRecipeTypeHandler type = containerScreen.getMenu()
                .blockEntity
                .getAugments()
                .getCurrentRecipeType();
        recipesGui.showTypes(type.getShownRecipeTypes(containerScreen.getMenu().getMode()));
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip) {
        tooltip.add(Component.translatable("jei.tooltip.show.recipes"));
    }
}
