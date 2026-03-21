package ironfurnaces.compat.jei.gui;

import ironfurnaces.gui.furnaces.FurnacePatternScreen;
import ironfurnaces.recipes.GeneratorRecipe;
import ironfurnaces.registration.JEICompat;
import ironfurnaces.registration.ModCustomRecipe;
import ironfurnaces.tileentity.furnaces.FurnaceMode;
import ironfurnaces.tileentity.furnaces.cache.AugmentCache;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.recipe.IFocusFactory;
import mezz.jei.api.runtime.IRecipesGui;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.List;

public class FurnaceClickableAreaForNewSet implements IGuiClickableArea {
    private FurnacePatternScreen containerScreen;

    public FurnaceClickableAreaForNewSet(FurnacePatternScreen containerScreen) {
        this.containerScreen = containerScreen;
    }

    @Override
    public Rect2i getArea() {
        return switch (containerScreen.getMenu().getMode()) {
            case FURNACE -> new Rect2i(79, 35, 23, 16);
            case FACTORY -> new Rect2i(89, 38, 15, 16);
            case GENERATOR -> new Rect2i(79, 35, 23, 16);
        };
    }

    @Override
    public void onClick(IFocusFactory focusFactory, IRecipesGui recipesGui) {
        AugmentCache.HandlingRecipeType type = containerScreen.getMenu()
                .blockEntity
                .getAugments()
                .getCurrentRecipeType();
        FurnaceMode mode = containerScreen.getMenu().getMode();

        switch (type) {
            case NORMAL -> {
                if (mode.equals(FurnaceMode.GENERATOR)) {
                    var type1 = JEICompat.GENERATOR_REGULAR;
                    recipesGui.showTypes(List.of(type1));
                } else {
                    recipesGui.showTypes(List.of(RecipeTypes.SMELTING));
                }

            }
            case SMOKE -> {
                if (mode.equals(FurnaceMode.GENERATOR)) {
                    var type1 = JEICompat.GENERATOR_SMOKING;
                    recipesGui.showTypes(List.of(type1));
                } else {
                    recipesGui.showTypes(List.of(RecipeTypes.SMOKING));
                }

            }
            case BLAST -> {
                if (mode.equals(FurnaceMode.GENERATOR)) {
                    var generatorRecipeRecipeType = ModCustomRecipe.GENERATOR_RECIPE.asJEIRecipeType().get();
                    recipesGui.showTypes(List.of(generatorRecipeRecipeType));
                } else {
                    recipesGui.showTypes(List.of(RecipeTypes.BLASTING));
                }
            }
        }


    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip) {
        tooltip.add(Component.translatable("jei.tooltip.show.recipes"));
    }
}
