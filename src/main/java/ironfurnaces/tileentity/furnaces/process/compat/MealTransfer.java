package ironfurnaces.tileentity.furnaces.process.compat;

import com.mojang.serialization.MapCodec;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.cache.recipe_type_handlers.FarmerDelightCookingRecipeTypeHandler;
import ironfurnaces.tileentity.furnaces.process.ProcessingInstance;
import net.minecraft.world.item.ItemStack;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;

public class MealTransfer extends ProcessingInstance {
    public static final String TYPE = "fd_meal_transfer";
    public static final MapCodec<MealTransfer> CODEC = MapCodec.unit(MealTransfer::new);

    public MealTransfer() {
        super(1);
    }

    @Override
    public boolean needLit(FurnacePatternBlockEntity tile) {
        var recipe = tile.getRecipe(ItemStack.EMPTY);
        ItemStack currentContainer = tile.getInput().getStackInSlot(Cooking.CONTAINER);
        ItemStack preMeal = tile.getViewOnly().getStackInSlot(Cooking.PREMEAL);
        //~ if >1.20.1 'recipe.get()' -> 'recipe.get().value()'
        if (recipe.isPresent() && recipe.get() instanceof CookingPotRecipe cookingPotRecipe) {
            ItemStack outputContainer = cookingPotRecipe.getOutputContainer();
            return currentContainer.is(outputContainer.getItem()) && preMeal.is(cookingPotRecipe.getResultItem(tile.getLevel().registryAccess()).getItem());
        }

        return !preMeal.isEmpty() && preMeal.hasCraftingRemainingItem() && currentContainer.is(preMeal.getCraftingRemainingItem().getItem());

    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public void whenStart(FurnacePatternBlockEntity tile) {
    }

    @Override
    public void whenDone(FurnacePatternBlockEntity tile) {
        var recipe = tile.getRecipe(ItemStack.EMPTY);

        ItemStack currentContainer = tile.getInput().getStackInSlot(Cooking.CONTAINER);
        ItemStack preMeal = tile.getViewOnly().getStackInSlot(Cooking.PREMEAL);
        //~ if >1.20.1 'recipe.get()' -> 'recipe.get().value()'
        if (recipe.isPresent() && recipe.get() instanceof CookingPotRecipe cookingPotRecipe) {

            ItemStack outputContainer = cookingPotRecipe.getOutputContainer();

            if (currentContainer.is(outputContainer.getItem()) && preMeal.is(cookingPotRecipe.getResultItem(tile.getLevel().registryAccess()).getItem())) {
                int min = Math.min(currentContainer.getCount(), preMeal.getCount());
                ItemStack last = tile.getOutput().insertItem(Cooking.MEAL, preMeal.copyWithCount(min), false);
                int i = min - last.getCount();
                tile.getInput().extractItem(Cooking.CONTAINER, i, false);
                tile.getViewOnly().extractItem(Cooking.PREMEAL, i, false);
                super.whenDone(tile);
            }
        } else if (!preMeal.isEmpty() && preMeal.hasCraftingRemainingItem() && currentContainer.is(preMeal.getCraftingRemainingItem().getItem())) {
            int min = Math.min(currentContainer.getCount(), preMeal.getCount());
            ItemStack last = tile.getOutput().insertItem(Cooking.MEAL, preMeal.copyWithCount(min), false);
            int i = min - last.getCount();
            tile.getInput().extractItem(Cooking.CONTAINER, i, false);
            tile.getViewOnly().extractItem(Cooking.PREMEAL, i, false);
            super.whenDone(tile);
        }

    }

    @Override
    public TickResult whenTick(FurnacePatternBlockEntity tile) {
        return TickResult.DONE;
    }

    @Override
    public float getDoneProgress() {
        return 0;
    }
}
