package ironfurnaces.recipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ironfurnaces.registration.ModCustomRecipe;
import lombok.Getter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
//? > 1.20.1 {
/*import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;

*///?}
//~ if >1.20.1 'Container' -> 'SingleRecipeInput'
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

    public GeneratorRecipe(int energy, Ingredient ingredient) {
        this.energy = energy;
        this.ingredient = ingredient;
    }

    @Override
    public boolean isIncomplete() {
        return ingredient.isEmpty();
    }



    //~ if >1.20.1 'Container' -> 'SingleRecipeInput' {

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
    //~ if >1.20.1 'RegistryAccess' -> 'HolderLookup.Provider' {
    @Override
    public ItemStack assemble(Container p_44001_, RegistryAccess p_267165_) {
        return ItemStack.EMPTY;
    }


    @Override

    public ItemStack getResultItem(RegistryAccess provider) {
        return ItemStack.EMPTY;
    }
    //~}
    //~}

    @Override
    public boolean canCraftInDimensions(int p_43999_, int p_44000_) {
        return true;
    }


    @Override
    public boolean isSpecial() {
        return true;
    }

    //? 1.20.1 {
    @Override
    public ResourceLocation getId() {
        return recipeId;
    }
    //?}

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModCustomRecipe.GENERATOR_RECIPE.asSerializer();
    }

    @Override
    public RecipeType<?> getType() {
        return ModCustomRecipe.GENERATOR_RECIPE.get();
    }

    public static class Serializer implements RecipeSerializer<GeneratorRecipe> {
        //? 1.20.1 {
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
            GeneratorRecipe recipe = new GeneratorRecipe(recipeId, buffer.readInt(), Ingredient.fromNetwork(buffer));
            return recipe;
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, GeneratorRecipe recipe) {
            buffer.writeInt(recipe.energy);
            recipe.ingredient.toNetwork(buffer);
        }
        //?} else {
        /*public static final MapCodec<GeneratorRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        ExtraCodecs.POSITIVE_INT.fieldOf("energy").forGetter(GeneratorRecipe::getEnergy),
                        Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(GeneratorRecipe::getIngredient)
                ).apply(instance, GeneratorRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, GeneratorRecipe> STREAM_CODEC = StreamCodec.of((x, y) -> {
            x.writeInt(y.energy);
            Ingredient.CONTENTS_STREAM_CODEC.encode(x, y.ingredient);
        }, (x) -> new GeneratorRecipe(x.readInt(), Ingredient.CONTENTS_STREAM_CODEC.decode(x)));

        @Override
        public MapCodec<GeneratorRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, GeneratorRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        *///?}
    }
}
