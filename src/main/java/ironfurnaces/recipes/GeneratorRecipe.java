package ironfurnaces.recipes;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ironfurnaces.registration.ModCustomRecipe;
import lombok.Getter;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

public class GeneratorRecipe implements Recipe<SingleRecipeInput> {

    @Getter
    private final int energy;

    @Getter
    private final Ingredient ingredient;

    private PlacementInfo placementInfo;

    public GeneratorRecipe(int energy, Ingredient ingredient) {
        this.energy = energy;
        this.ingredient = ingredient;
    }

    public static int getTotalCount(SingleRecipeInput input, Ingredient ingredient) {
        ItemStack stack = input.getItem(0);
        if (!stack.isEmpty() && ingredient.test(stack)) {
            return stack.getCount();
        }
        return 0;
    }

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return this.ingredient.test(input.getItem(0));
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public PlacementInfo placementInfo() {
        if (this.placementInfo == null) {
            this.placementInfo = PlacementInfo.create(this.ingredient);
        }
        return this.placementInfo;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.FURNACE_MISC;
    }

    @Override
    public RecipeSerializer<? extends Recipe<SingleRecipeInput>> getSerializer() {
        return ModCustomRecipe.GENERATOR_RECIPE.asSerializer();
    }

    @Override
    public RecipeType<? extends Recipe<SingleRecipeInput>> getType() {
        return ModCustomRecipe.GENERATOR_RECIPE.get();
    }

    public static final MapCodec<GeneratorRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ExtraCodecs.POSITIVE_INT
                            .fieldOf("energy")
                            .forGetter(GeneratorRecipe::getEnergy),
                    Ingredient.CODEC
                            .fieldOf("ingredient")
                            .forGetter(GeneratorRecipe::getIngredient)
            ).apply(instance, GeneratorRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, GeneratorRecipe> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    GeneratorRecipe::getEnergy,
                    Ingredient.CONTENTS_STREAM_CODEC,
                    GeneratorRecipe::getIngredient,
                    GeneratorRecipe::new
            );

    public static final RecipeSerializer<GeneratorRecipe> SERIALIZER =
            new RecipeSerializer<>(CODEC, STREAM_CODEC);
}