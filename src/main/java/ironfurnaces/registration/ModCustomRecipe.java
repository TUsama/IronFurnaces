package ironfurnaces.registration;

import com.google.gson.JsonObject;
import com.tterrag.registrate.providers.ProviderType;
import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.recipes.GeneratorRecipe;
import ironfurnaces.recipes.SimpleGeneratorRecipe;
import ironfurnaces.registrate.CustomRecipeBuilder;
import ironfurnaces.registrate.CustomRecipeEntry;
import net.minecraft.advancements.Advancement;

import net.minecraft.core.registries.BuiltInRegistries;


import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.conditions.ICondition;
//? 1.20.1 {
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import net.minecraftforge.common.crafting.conditions.TagEmptyCondition;
//?} else {
/*import net.minecraft.data.recipes.RecipeOutput;
import net.minecraftforge.common.conditions.NotCondition;
import net.minecraftforge.common.conditions.TagEmptyCondition;
import net.minecraftforge.common.crafting.ConditionalRecipeOutput;
import net.minecraft.advancements.AdvancementHolder;
*///?}

import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import static ironfurnaces.loaders.IronFurnaces.REGISTRATE;
import static ironfurnaces.registration.ModItemTags.bindC;
import static ironfurnaces.registration.ModItemTags.bindForge;

public class ModCustomRecipe {
    private static final String GENERATOR_ID = "generator_blasting";
    public static final CustomRecipeEntry<GeneratorRecipe> GENERATOR_RECIPE = REGISTRATE
            .entry(GENERATOR_ID, builderCallback -> new CustomRecipeBuilder<>(REGISTRATE, REGISTRATE, GENERATOR_ID, builderCallback, GeneratorRecipe.Serializer::new))
            .jei(GeneratorRecipe.class)
            .addMiscData(ProviderType.LANG, x -> {
                x.add(IronFurnaces.MOD_ID + ".jei_" + "category_blasting", "Cracking Generation");
            })
            .recipe((ctx, provider) -> {

                acceptWhenTagNotEmpty(provider, "amethyst", 40000, bindC("gems/amethyst"));
                acceptWhenTagNotEmpty(provider, "copper", 10000, bindC("ingots/copper"));
                acceptWhenTagNotEmpty(provider, "diamond", 500000, bindC("gems/diamond"));
                acceptWhenTagNotEmpty(provider, "emerald", 125000, bindC("gems/emerald"));
                acceptWhenTagNotEmpty(provider, "gold", 40000, bindC("ingots/gold"));
                acceptWhenTagNotEmpty(provider, "iron", 20000, bindC("ingots/iron"));
                acceptWhenTagNotEmpty(provider, "lapis", 40000, bindC("gems/lapis"));
                acceptWhenTagNotEmpty(provider, "prismarine", 40000, bindC("gems/prismarine"));
                acceptWhenTagNotEmpty(provider, "nether_star", 1000000, bindForge("nether_stars"));
                acceptWhenTagNotEmpty(provider, "netherite", 750000, bindC("ingots/netherite"));
                acceptWhenTagNotEmpty(provider, "quartz", 40000, bindC("gems/quartz"));
                acceptWhenTagNotEmpty(provider, "redstone", 10000, bindC("dusts/redstone"));

                //? 1.20.1 {
                acceptWhenTagNotEmpty(provider, "amethyst_forge", 40000, bindForge("gems/amethyst"));
                acceptWhenTagNotEmpty(provider, "copper_forge", 10000, bindForge("ingots/copper"));
                acceptWhenTagNotEmpty(provider, "diamond_forge", 500000, bindForge("gems/diamond"));
                acceptWhenTagNotEmpty(provider, "emerald_forge", 125000, bindForge("gems/emerald"));
                acceptWhenTagNotEmpty(provider, "gold_forge", 40000, bindForge("ingots/gold"));
                acceptWhenTagNotEmpty(provider, "iron_forge", 20000, bindForge("ingots/iron"));
                acceptWhenTagNotEmpty(provider, "lapis_forge", 40000, bindForge("gems/lapis"));
                acceptWhenTagNotEmpty(provider, "prismarine_forge", 40000, bindForge("gems/prismarine"));
                acceptWhenTagNotEmpty(provider, "netherite_forge", 750000, bindForge("ingots/netherite"));
                acceptWhenTagNotEmpty(provider, "quartz_forge", 40000, bindForge("gems/quartz"));
                acceptWhenTagNotEmpty(provider, "redstone_forge", 10000, bindForge("dusts/redstone"));
                //?}
            })
            .register();

    public static void register(){
    }

    //~ if >1.20.1 'Consumer<FinishedRecipe>' -> 'RecipeOutput'
    private static void acceptWhenTagNotEmpty(Consumer<FinishedRecipe> provider, String id, int value, TagKey<Item> tag) {
        //? 1.20.1 {
        ConditionalRecipe.builder()
                .addCondition(new NotCondition(new TagEmptyCondition(tag.location())))
                .addRecipe(new Result(id, value, tag))
                .build(provider, IronFurnaces.id(GENERATOR_ID + "/" + id));
        //? } else {
        /*provider.accept(IronFurnaces.id(GENERATOR_ID + "/" + id), new GeneratorRecipe(value, Ingredient.of(tag)), null, new NotCondition(new TagEmptyCondition(tag.location())));
        *///?}
    }

    //? forge {
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
    //?}
}
