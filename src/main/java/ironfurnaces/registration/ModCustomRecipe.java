package ironfurnaces.registration;

import com.google.gson.JsonObject;
import com.tterrag.registrate.providers.ProviderType;
import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.recipes.GeneratorRecipe;
import ironfurnaces.recipes.SimpleGeneratorRecipe;
import ironfurnaces.registrate.CustomRecipeBuilder;
import ironfurnaces.registrate.CustomRecipeEntry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

import static ironfurnaces.loaders.IronFurnaces.REGISTRATE;
import static ironfurnaces.registration.ModItemTags.bindC;
import static ironfurnaces.registration.ModItemTags.bindForge;

public class ModCustomRecipe {
    private static final String GENERATOR_ID = "generator_blasting";
    public static final CustomRecipeEntry<GeneratorRecipe> GENERATOR_RECIPE = REGISTRATE
            .entry(GENERATOR_ID, builderCallback -> new CustomRecipeBuilder<>(REGISTRATE, REGISTRATE, GENERATOR_ID, builderCallback, GeneratorRecipe.Serializer::new))
            .jei(GeneratorRecipe.class)
            .addMiscData(ProviderType.LANG, x -> {
                x.add(IronFurnaces.MOD_ID + ".jei_" + "category_blasting", "Blasting Generation");
            })
            .recipe((ctx, provider) -> {
                provider.accept(new Result("amethyst", 40000, bindC("gems/amethyst")));
                provider.accept(new Result("copper", 10000, bindC("ingots/copper")));
                provider.accept(new Result("diamond", 500000, bindC("gems/diamond")));
                provider.accept(new Result("emerald", 125000, bindC("gems/emerald")));
                provider.accept(new Result("gold", 40000, bindC("ingots/gold")));
                provider.accept(new Result("iron", 20000, bindC("ingots/iron")));
                provider.accept(new Result("lapis", 40000, bindC("gems/lapis")));
                provider.accept(new Result("prismarine", 40000, bindC("gems/prismarine")));
                provider.accept(new Result("nether_star", 1000000, bindForge("nether_stars")));
                provider.accept(new Result("netherite", 750000, bindC("ingots/netherite")));
                provider.accept(new Result("quartz", 40000, bindC("gems/quartz")));
                provider.accept(new Result("redstone", 10000, bindC("dusts/redstone")));
                //forge tag
                provider.accept(new Result("amethyst_forge", 40000, bindForge("gems/amethyst")));
                provider.accept(new Result("copper_forge", 10000, bindForge("ingots/copper")));
                provider.accept(new Result("diamond_forge", 500000, bindForge("gems/diamond")));
                provider.accept(new Result("emerald_forge", 125000, bindForge("gems/emerald")));
                provider.accept(new Result("gold_forge", 40000, bindForge("ingots/gold")));
                provider.accept(new Result("iron_forge", 20000, bindForge("ingots/iron")));
                provider.accept(new Result("lapis_forge", 40000, bindForge("gems/lapis")));
                provider.accept(new Result("prismarine_forge", 40000, bindForge("gems/prismarine")));
                provider.accept(new Result("netherite_forge", 750000, bindForge("ingots/netherite")));
                provider.accept(new Result("quartz_forge", 40000, bindForge("gems/quartz")));
                provider.accept(new Result("redstone_forge", 10000, bindForge("dusts/redstone")));
            })
            .register();

    public static void register(){
    }

    private static class Result implements FinishedRecipe {
        private final String id;
        private final int energy;
        private final Ingredient ingredient;




        private Result(int energy, Item item) {
            this.id = BuiltInRegistries.ITEM.getKey(item).getPath();
            this.energy = energy;
            this.ingredient = Ingredient.of(item.getDefaultInstance());
        }

        private Result(String id, int energy, TagKey<Item> item) {
            this.id = id;
            this.energy = energy;
            this.ingredient = Ingredient.of(item);
        }


        @Override
        public void serializeRecipeData(JsonObject json) {
            json.addProperty("energy", energy);
            json.add("ingredient", ingredient.toJson());
        }

        @Override
        public ResourceLocation getId() {
            return IronFurnaces.id(GENERATOR_ID + "/" +  id);
        }

        @Override
        public RecipeSerializer<?> getType() {
            return ModCustomRecipe.GENERATOR_RECIPE.asSerializer();
        }

        @Override
        public @Nullable JsonObject serializeAdvancement() {
            return null;
        }

        @Override
        public @Nullable ResourceLocation getAdvancementId() {
            return null;
        }
    }
}
