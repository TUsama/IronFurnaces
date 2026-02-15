package ironfurnaces.registration;

import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.recipes.GeneratorRecipe;
import ironfurnaces.recipes.SimpleGeneratorRecipe;
import ironfurnaces.registrate.RecipeTypeBuilder;
import ironfurnaces.registrate.RecipeTypeEntry;

import static ironfurnaces.loaders.IronFurnaces.REGISTRATE;

public class ModRecipeTypes {
    private static final String GENERATOR_ID = "generator_blasting";
    public static final RecipeTypeEntry<GeneratorRecipe> GENERATOR_RECIPE = REGISTRATE
            .entry(GENERATOR_ID, builderCallback -> new RecipeTypeBuilder<>(REGISTRATE, REGISTRATE, GENERATOR_ID, builderCallback, GeneratorRecipe.Serializer::new))
            .jei(GeneratorRecipe.class)
            .register();
    public static mezz.jei.api.recipe.RecipeType<SimpleGeneratorRecipe> GENERATOR_SMOKING = mezz.jei.api.recipe.RecipeType.create(IronFurnaces.MOD_ID, "generator_smoking", SimpleGeneratorRecipe.class);
    public static mezz.jei.api.recipe.RecipeType<SimpleGeneratorRecipe> GENERATOR_REGULAR = mezz.jei.api.recipe.RecipeType.create(IronFurnaces.MOD_ID, "generator_regular", SimpleGeneratorRecipe.class);

    public static void register(){
    }
}
