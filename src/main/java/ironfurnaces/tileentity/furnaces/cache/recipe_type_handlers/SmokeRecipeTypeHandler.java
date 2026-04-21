package ironfurnaces.tileentity.furnaces.cache.recipe_type_handlers;

import ironfurnaces.config.FurnaceConfig;
import ironfurnaces.registration.JEICompat;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.cache.IRecipeTypeHandler;
import ironfurnaces.tileentity.furnaces.cache.InputCache;
import ironfurnaces.tileentity.furnaces.pattern.IFurnaceStats;
import ironfurnaces.tileentity.furnaces.pattern.mode.AbstractFurnaceModeHandler;
import ironfurnaces.tileentity.furnaces.process.Burn;
import ironfurnaces.tileentity.furnaces.process.Generate;
import ironfurnaces.tileentity.furnaces.process.ProcessingInstanceManager;
import it.unimi.dsi.fastutil.ints.IntSet;
import mezz.jei.api.constants.RecipeTypes;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.List;

public class SmokeRecipeTypeHandler implements IRecipeTypeHandler {
    public static final SmokeRecipeTypeHandler INSTANCE = new SmokeRecipeTypeHandler();

    @Override
    public RecipeType<?> getRecipeType() {
        return RecipeType.SMOKING;
    }

    @Override
    public StringBuilder buildTextureName(StringBuilder stringBuilder) {
        return stringBuilder.append("_smoke");
    }

    @Override
    public boolean testBurnable(ItemStack stack, FurnacePatternBlockEntity blockEntity) {
        FoodProperties foodProperties = stack.getItem().getFoodProperties(stack, null);
        return foodProperties != null && foodProperties.getNutrition() > 0;
    }



    @Override
    public void provideInstance(FurnacePatternBlockEntity blockEntity) {
        ProcessingInstanceManager instanceManager = blockEntity.getInstanceManager();
        IFurnaceStats<?> usedStats = blockEntity.getUsedStats();
        IntSet workingIndexes = instanceManager.getWorkingIndexes();
        InputCache input = blockEntity.getInput();
        if (blockEntity.getMode().isGenerator()){
            for (int i = 0; i < blockEntity.getFuel().getSlots(); i++) {
                if (workingIndexes.contains(i)) continue;
                ItemStack stackInSlot = blockEntity.getFuel().getStackInSlot(i);
                if (stackInSlot.isEmpty()) continue;
                Item item = stackInSlot.getItem();
                FoodProperties foodProperties = item.getFoodProperties(stackInSlot, null);
                if (foodProperties != null && foodProperties.getNutrition() > 0) {
                    instanceManager.addInstance(new Generate.SmokingGenerate(i, foodProperties.getNutrition() * FurnaceConfig.config.nutrition_to_energy_factor, usedStats.energyGenerationPerTick()));
                }

            }
        } else {
            for (int i = 0; i < input.getSlots(); i++) {
                if (workingIndexes.contains(i)) continue;
                ItemStack stackInSlot = input.getStackInSlot(i);
                if (stackInSlot.isEmpty()) continue;
                int finalI = i;
                blockEntity.getRecipe(stackInSlot)
                        .ifPresent(x -> {
                            //~ if >1.20.1 'x instanceof' -> 'x.value() instanceof'
                            if (x instanceof AbstractCookingRecipe recipe) {
                                instanceManager.addInstance(new Burn.Smoking(finalI, usedStats.smeltTick(), usedStats.batchHandle()));
                            }

                        });
            }
        }
    }

    @Override
    public List<mezz.jei.api.recipe.RecipeType<?>> getShownRecipeTypes(AbstractFurnaceModeHandler mode) {
        if (mode.isGenerator()) {
            var type1 = JEICompat.GENERATOR_SMOKING;
            return List.of(type1);
        } else {
            return List.of(RecipeTypes.SMOKING);
        }
    }

    @Override
    public String toString() {
        return getRecipeType().toString();
    }
}
