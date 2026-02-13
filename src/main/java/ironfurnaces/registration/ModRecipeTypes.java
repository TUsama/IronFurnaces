package ironfurnaces.registration;

import ironfurnaces.recipes.GeneratorRecipe;
import ironfurnaces.registrate.RecipeTypeBuilder;
import ironfurnaces.registrate.RecipeTypeEntry;

import static ironfurnaces.loaders.IronFurnaces.REGISTRATE;

public class ModRecipeTypes {
    private static final String GENERATOR_ID = "generator_blasting";
    public static final RecipeTypeEntry<GeneratorRecipe> GENERATOR_RECIPE = REGISTRATE.entry(GENERATOR_ID, builderCallback -> new RecipeTypeBuilder<>(REGISTRATE, REGISTRATE, GENERATOR_ID, builderCallback, GeneratorRecipe.Serializer::new)).register();

    public static void register(){
    }
}
