package ironfurnaces.tileentity.furnaces.cache.recipe_type_handlers;

import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.cache.IRecipeTypeHandler;
import ironfurnaces.tileentity.furnaces.cache.InputCache;
import ironfurnaces.tileentity.furnaces.pattern.IFurnaceStats;
import ironfurnaces.tileentity.furnaces.pattern.mode.AbstractFurnaceModeHandler;
import ironfurnaces.tileentity.furnaces.process.Burn;
import ironfurnaces.tileentity.furnaces.process.ProcessingInstanceManager;
import it.unimi.dsi.fastutil.ints.IntSet;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.List;

public class BlastRecipeTypeHandler implements IRecipeTypeHandler {
    public static final BlastRecipeTypeHandler INSTANCE = new BlastRecipeTypeHandler();

    @Override
    public RecipeType<?> getRecipeType() {
        return RecipeType.BLASTING;
    }

    @Override
    public StringBuilder buildTextureName(StringBuilder stringBuilder) {
        return stringBuilder.append("_blast");
    }

    @Override
    public boolean testBurnable(ItemStack stack, FurnacePatternBlockEntity blockEntity) {
        return SmeltRecipeTypeHandler.INSTANCE.testBurnable(stack, blockEntity);
    }

    @Override
    public void provideInstance(FurnacePatternBlockEntity blockEntity) {
        ProcessingInstanceManager instanceManager = blockEntity.getInstanceManager();
        IFurnaceStats<?> usedStats = blockEntity.getUsedStats();
        IntSet workingIndexes = instanceManager.getWorkingIndexes();
        InputCache input = blockEntity.getInput();
        if (!blockEntity.getMode().isGenerator()) {
            for (int i = 0; i < input.getSlots(); i++) {
                if (workingIndexes.contains(i)) continue;
                ItemStack stackInSlot = input.getStackInSlot(i);
                if (stackInSlot.isEmpty()) continue;
                int finalI = i;
                blockEntity.getRecipe(stackInSlot)
                        .ifPresent(x -> {
                            //~ if >1.20.1 'x instanceof' -> 'x.value() instanceof'
                            if (x.value() instanceof AbstractCookingRecipe recipe) {
                                instanceManager.addInstance(new Burn.Blasting(finalI, usedStats.smeltTick(), usedStats.batchHandle()));
                            }

                        });
            }
        }
    }


    @Override
    public List<IRecipeType<?>> getShownRecipeTypes(AbstractFurnaceModeHandler mode) {
        return List.of(RecipeTypes.BLASTING);
    }

    @Override
    public String toString() {
        return getRecipeType().toString();
    }
}
