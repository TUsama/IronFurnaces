package ironfurnaces.recipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ironfurnaces.registration.ModCustomRecipe;
import lombok.Getter;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class GeneratorRecipe implements Recipe<Container> {

    private ResourceLocation recipeId;
    @Getter
    private int energy;
    @Getter
    private Ingredient ingredient;

    public GeneratorRecipe(ResourceLocation recipeId, int energy, Ingredient stack)
    {
        this.recipeId = recipeId;
        this.energy = energy;
        this.ingredient = stack;
    }

    @Override
    public boolean isIncomplete() {
        return ingredient.isEmpty();
    }

    public static int getTotalCount(Container inventory, Ingredient input) {
        ItemStack stack = inventory.getItem(0);
        if (!stack.isEmpty() && input.test(stack)) {
            return stack.getCount();
        }
        return 0;
    }


    @Override
    public boolean matches(Container inv, Level level) {
        int required = ingredient.getItems().length;
        int found = getTotalCount(inv, ingredient);
        return found >= required;
    }

    @Override
    public ItemStack assemble(Container p_44001_, RegistryAccess p_267165_) {
        return ItemStack.EMPTY;
    }
    @Override
    public boolean canCraftInDimensions(int p_43999_, int p_44000_) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess p_267052_) {
        return ItemStack.EMPTY;
    }
    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public ResourceLocation getId() {
        return recipeId;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModCustomRecipe.GENERATOR_RECIPE.asSerializer();
    }

    @Override
    public RecipeType<?> getType() {
        return ModCustomRecipe.GENERATOR_RECIPE.get();
    }

    public static class Serializer implements RecipeSerializer<GeneratorRecipe> {
        @Override
        public GeneratorRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            int energy = GsonHelper.getAsInt(json, "energy", 10000);
            JsonElement ingredient = json.get("ingredient");
            if (ingredient != null){
                Ingredient ingredient1 = Ingredient.fromJson(ingredient);
                GeneratorRecipe recipe = new GeneratorRecipe(recipeId, energy, ingredient1);
                return recipe;
            }
            throw new RuntimeException("invalid Generator recipe: " + recipeId);
        }

        @Nullable
        @Override
        public GeneratorRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            GeneratorRecipe recipe = new GeneratorRecipe(recipeId, buffer.readVarInt(), Ingredient.fromNetwork(buffer));
            return recipe;
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, GeneratorRecipe recipe) {
            buffer.writeVarInt(recipe.energy);
            recipe.ingredient.toNetwork(buffer);
        }
    }
}
