package ironfurnaces.registration;

import dev.anvilcraft.lib.v2.registrum.providers.ProviderType;
import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.recipes.SimpleGeneratorRecipe;
import mezz.jei.api.recipe.types.IRecipeType;

import static ironfurnaces.loaders.IronFurnaces.REGISTRATE;

public class JEICompat {
    public static IRecipeType<SimpleGeneratorRecipe> GENERATOR_SMOKING = IRecipeType.create(IronFurnaces.MOD_ID, "generator_smoking", SimpleGeneratorRecipe.class);
    public static IRecipeType<SimpleGeneratorRecipe> GENERATOR_REGULAR = IRecipeType.create(IronFurnaces.MOD_ID, "generator_regular", SimpleGeneratorRecipe.class);

    public static void register(){
        REGISTRATE.addDataGenerator(ProviderType.LANG, x -> {
            x.add(IronFurnaces.MOD_ID + ".jei_" + "category_smoking", "Smoking Generation");
            x.add(IronFurnaces.MOD_ID + ".jei_" + "category_regular", "Smelting Generation");
        });
    }

}
