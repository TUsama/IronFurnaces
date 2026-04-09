package ironfurnaces.compat.jei.gui;

import ironfurnaces.gui.furnaces.BlockIronFurnaceScreenBase;
import ironfurnaces.registration.JEICompat;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.recipe.IFocusFactory;
import mezz.jei.api.runtime.IRecipesGui;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;

import java.util.List;

public class FurnaceClickableArea implements IGuiClickableArea {
    private BlockIronFurnaceScreenBase<?> containerScreen;

    public FurnaceClickableArea(BlockIronFurnaceScreenBase<?> containerScreen) {
        this.containerScreen = containerScreen;
    }

    @Override
    public Rect2i getArea() {
        return new Rect2i(80, 35, 23, 16);
    }

    @Override
    public void onClick(IFocusFactory focusFactory, IRecipesGui recipesGui) {
        if (containerScreen.getMenu().getIsGenerator()) {
            var type1 = JEICompat.GENERATOR_REGULAR;
            recipesGui.showTypes(List.of(type1));
        } else {
            recipesGui.showTypes(List.of(RecipeTypes.SMELTING));
        }

    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip) {
        tooltip.add(Component.translatable("jei.tooltip.show.recipes"));
    }
}
