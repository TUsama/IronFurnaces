package ironfurnaces.registration;

import com.tterrag.registrate.providers.ProviderType;
import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.recipes.SimpleGeneratorRecipe;

import static ironfurnaces.loaders.IronFurnaces.REGISTRATE;

public class JEICompat {
    public static mezz.jei.api.recipe.RecipeType<SimpleGeneratorRecipe> GENERATOR_SMOKING = mezz.jei.api.recipe.RecipeType.create(IronFurnaces.MOD_ID, "generator_smoking", SimpleGeneratorRecipe.class);
    public static mezz.jei.api.recipe.RecipeType<SimpleGeneratorRecipe> GENERATOR_REGULAR = mezz.jei.api.recipe.RecipeType.create(IronFurnaces.MOD_ID, "generator_regular", SimpleGeneratorRecipe.class);

    public static void register(){
        REGISTRATE.addDataGenerator(ProviderType.LANG, x -> {
            x.add(IronFurnaces.MOD_ID + ".jei_" + "category_smoking", "Smoking Generation");
            x.add(IronFurnaces.MOD_ID + ".jei_" + "category_regular", "Smelting Generation");
        });
    }

}
