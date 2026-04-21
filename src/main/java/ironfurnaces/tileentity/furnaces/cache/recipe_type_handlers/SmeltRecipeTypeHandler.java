package ironfurnaces.tileentity.furnaces.cache.recipe_type_handlers;

import ironfurnaces.registration.JEICompat;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.cache.IRecipeTypeHandler;
import ironfurnaces.tileentity.furnaces.cache.InputCache;
import ironfurnaces.tileentity.furnaces.pattern.IFurnaceStats;
import ironfurnaces.tileentity.furnaces.pattern.mode.AbstractFurnaceModeHandler;
import ironfurnaces.tileentity.furnaces.process.Burn;
import ironfurnaces.tileentity.furnaces.process.Generate;
import ironfurnaces.tileentity.furnaces.process.ProcessingInstanceManager;
import ironfurnaces.util.FuelBurnTimeUtil;
import it.unimi.dsi.fastutil.ints.IntSet;
import mezz.jei.api.constants.RecipeTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.List;

public class SmeltRecipeTypeHandler implements IRecipeTypeHandler {

    public static final SmeltRecipeTypeHandler INSTANCE = new SmeltRecipeTypeHandler();

    @Override
    public RecipeType<?> getRecipeType() {
        return RecipeType.SMELTING;
    }

    @Override
    public StringBuilder buildTextureName(StringBuilder stringBuilder) {
        return stringBuilder;
    }

    @Override
    public boolean testBurnable(ItemStack stack, FurnacePatternBlockEntity blockEntity) {
        return FuelBurnTimeUtil.getBurnTime(stack, blockEntity.getAugments().getCurrentRecipeType().getRecipeType()) > 0;
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
                int generation = usedStats.energyGenerationPerTick();
                int burnTime = FuelBurnTimeUtil.getBurnTime(stackInSlot, blockEntity.getAugments().getCurrentRecipeType().getRecipeType());
                if (burnTime > 0){
                    instanceManager.addInstance(new Generate.SmeltGenerate(i, burnTime, generation));
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
                                instanceManager.addInstance(new Burn.Smelting(finalI, usedStats.smeltTick(), usedStats.batchHandle()));
                            }

                        });
            }
        }


    }

    @Override
    public List<mezz.jei.api.recipe.RecipeType<?>> getShownRecipeTypes(AbstractFurnaceModeHandler mode) {
        if (mode.isGenerator()) {
            var type1 = JEICompat.GENERATOR_REGULAR;
            return List.of(type1);
        } else {
            return List.of(RecipeTypes.SMELTING);
        }
    }

    @Override
    public String toString() {
        return getRecipeType().toString();
    }
}
