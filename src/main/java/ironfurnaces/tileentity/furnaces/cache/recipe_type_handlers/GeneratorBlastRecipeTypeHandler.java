package ironfurnaces.tileentity.furnaces.cache.recipe_type_handlers;

import ironfurnaces.recipes.GeneratorRecipe;
import ironfurnaces.registration.ModCustomRecipe;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.cache.IRecipeTypeHandler;
import ironfurnaces.tileentity.furnaces.pattern.IFurnaceStats;
import ironfurnaces.tileentity.furnaces.pattern.mode.AbstractFurnaceModeHandler;
import ironfurnaces.tileentity.furnaces.process.Generate;
import ironfurnaces.tileentity.furnaces.process.ProcessingInstanceManager;
import it.unimi.dsi.fastutil.ints.IntSet;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.List;

public class GeneratorBlastRecipeTypeHandler implements IRecipeTypeHandler {

    public static final GeneratorBlastRecipeTypeHandler INSTANCE = new GeneratorBlastRecipeTypeHandler();

    @Override
    public RecipeType<?> getRecipeType() {
        return ModCustomRecipe.GENERATOR_RECIPE.get();
    }

    @Override
    public StringBuilder buildTextureName(StringBuilder stringBuilder) {
        return BlastRecipeTypeHandler.INSTANCE.buildTextureName(stringBuilder);
    }

    @Override
    public boolean testBurnable(ItemStack stack, FurnacePatternBlockEntity blockEntity) {
        return blockEntity.getRecipe(stack).isPresent();
    }

    @Override
    public void provideInstance(FurnacePatternBlockEntity blockEntity) {
        ProcessingInstanceManager instanceManager = blockEntity.getInstanceManager();
        IFurnaceStats<?> usedStats = blockEntity.getUsedStats();
        IntSet workingIndexes = instanceManager.getWorkingIndexes();
        if (blockEntity.getMode().isGenerator()){
            for (int i = 0; i < blockEntity.getFuel().getSlots(); i++) {
                if (workingIndexes.contains(i)) continue;
                ItemStack stackInSlot = blockEntity.getFuel().getStackInSlot(i);
                if (stackInSlot.isEmpty()) continue;
                int generation = usedStats.energyGenerationPerTick();
                var recipe = blockEntity.getRecipe(stackInSlot);
                //~ if >1.20.1 'recipe.get()' -> 'recipe.get().value()'
                if (recipe.isPresent() && recipe.get().value() instanceof GeneratorRecipe generatorRecipe) {
                    instanceManager.addInstance(new Generate.BlastGenerate(i, generatorRecipe.getEnergy(), generation));
                }

            }
        }
    }


    @Override
    public List<IRecipeType<?>> getShownRecipeTypes(AbstractFurnaceModeHandler mode) {
        return List.of(ModCustomRecipe.GENERATOR_RECIPE.asJEIRecipeType().get());
    }

    @Override
    public String toString() {
        return getRecipeType().toString();
    }
}
