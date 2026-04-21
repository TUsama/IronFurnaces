package ironfurnaces.tileentity.furnaces.cache.recipe_type_handlers;

import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.cache.IRecipeTypeHandler;
import ironfurnaces.tileentity.furnaces.pattern.IFurnaceStats;
import ironfurnaces.tileentity.furnaces.pattern.mode.AbstractFurnaceModeHandler;
import ironfurnaces.tileentity.furnaces.process.ProcessingInstanceManager;
import ironfurnaces.tileentity.furnaces.process.compat.Cooking;
import ironfurnaces.tileentity.furnaces.process.compat.MealTransfer;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.ArrayUtils;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;
import vectorwing.farmersdelight.common.registry.ModRecipeTypes;
import vectorwing.farmersdelight.integration.jei.FDRecipeTypes;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class FarmerDelightCookingRecipeTypeHandler implements IRecipeTypeHandler {
    @Setter
    private CookingPotRecipe lastRecipe;
    @Getter
    private CookingPotRecipe lockedRecipe;
    private ResourceLocation tempResourceLocation;

    public boolean isLocking;

    public static FarmerDelightCookingRecipeTypeHandler getInstance() {
        return new FarmerDelightCookingRecipeTypeHandler();
    }


    @Override
    public RecipeType<?> getRecipeType() {
        return ModRecipeTypes.COOKING.get();
    }

    @Override
    public Optional<? extends Recipe> getRecipe(FurnacePatternBlockEntity blockEntity, Function<RecipeType<?>, RecipeManager.CachedCheck<Container, ?>> quickCheck, List<ItemStack> stacks) {
        return quickCheck.apply(ModRecipeTypes.COOKING.get()).getRecipeFor(new RecipeWrapper(blockEntity.getInput()), blockEntity.getLevel());
    }

    @Override
    public boolean allowPlaceItem(FurnacePatternBlockEntity blockEntity, Function<RecipeType<?>, RecipeManager.CachedCheck<Container, ?>> quickCheck, List<ItemStack> stacks) {
        if (lockedRecipe != null) {
            return lockedRecipe.matches(new RecipeWrapper(blockEntity.getInput()), blockEntity.getLevel());
        }
        return true;
    }

    @Override
    public StringBuilder buildTextureName(StringBuilder stringBuilder) {
        return stringBuilder;
    }

    @Override
    public boolean testBurnable(ItemStack stack, FurnacePatternBlockEntity blockEntity) {
        return false;
    }

    @Override
    public void provideInstance(FurnacePatternBlockEntity blockEntity) {
        IFurnaceStats<?> usedStats = blockEntity.getUsedStats();
        ProcessingInstanceManager instanceManager = blockEntity.getInstanceManager();
        blockEntity.getRecipe(ItemStack.EMPTY)
                .filter(x -> x instanceof CookingPotRecipe)
                .ifPresent(x -> {
                    if (!instanceManager.getWorkingIndexes().contains(0)) instanceManager.addInstance(new Cooking(usedStats.smeltTick(), 1));
                });
        ItemStack meal = blockEntity.getOutput().getStackInSlot(Cooking.MEAL);
        if (!blockEntity.getViewOnly().getStackInSlot(Cooking.PREMEAL).isEmpty() && meal.getCount() < meal.getMaxStackSize() && !instanceManager.getWorkingIndexes().contains(1)) {
            instanceManager.addInstance(new MealTransfer());
        }
    }


    @Override
    public List<mezz.jei.api.recipe.RecipeType<?>> getShownRecipeTypes(AbstractFurnaceModeHandler mode) {
        return List.of(FDRecipeTypes.COOKING);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compoundTag = new CompoundTag();
        if (lockedRecipe != null) {
            compoundTag.putString("CookingPotRecipeId", lockedRecipe.getId().toString());
        }

        return compoundTag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains("CookingPotRecipeId")) {
            this.tempResourceLocation = IronFurnaces.parse(nbt.getString("CookingPotRecipeId"));
        }

    }

    public void resetLock() {
        this.lockedRecipe = null;
        this.tempResourceLocation = null;
        this.isLocking = false;
    }

    public void setLockedToLastRecipe() {
        if (lastRecipe != null){
            this.lockedRecipe = lastRecipe;
            this.isLocking = true;
        }

    }

    public boolean canInsertToThisSlot(int i, ItemStack stack, Level level) {
        if (lockedRecipe == null) {
            if (tempResourceLocation != null) {
                level.getRecipeManager().byKey(tempResourceLocation)
                        .filter(x -> x instanceof CookingPotRecipe)
                        .ifPresent(x -> lockedRecipe = (CookingPotRecipe) x);
                tempResourceLocation = null;
            } else {
                isLocking = false;
                return true;
            }

        }
        isLocking = true;
        NonNullList<Ingredient> ingredients = lockedRecipe.getIngredients();
        if (i > ingredients.size() - 1) {
            return false;
        } else {
            return ingredients.get(i).test(stack);
        }

    }

    public ResourceLocation showAvailableItem(int i) {
        if (lockedRecipe == null) return null;
        ItemStack[] items = lockedRecipe.getIngredients().get(i).getItems();
        if (ArrayUtils.isEmpty(items)) return null;
        return ForgeRegistries.ITEMS.getKey(items[0].getItem());
    }




    @Override
    public String toString() {
        return getRecipeType().toString();
    }
}
