package ironfurnaces.registration;

import com.google.gson.JsonObject;
import dev.anvilcraft.lib.v2.registrum.providers.ProviderType;
import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.recipes.GeneratorRecipe;
import ironfurnaces.recipes.SimpleGeneratorRecipe;
import ironfurnaces.registrate.CustomRecipeBuilder;
import ironfurnaces.registrate.CustomRecipeEntry;
import net.minecraft.advancements.Advancement;

import net.minecraft.core.registries.BuiltInRegistries;


import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.common.conditions.ICondition;
//? 1.20.1 {
/*import net.minecraft.data.recipes.FinishedRecipe;
import net.neoforged.neoforge.common.crafting.ConditionalRecipe;
import net.neoforged.neoforge.common.crafting.conditions.NotCondition;
import net.neoforged.neoforge.common.crafting.conditions.TagEmptyCondition;
*///?} else {
import net.minecraft.data.recipes.RecipeOutput;
import net.neoforged.neoforge.common.conditions.NotCondition;
import net.neoforged.neoforge.common.conditions.TagEmptyCondition;
import net.neoforged.neoforge.common.crafting.ConditionalRecipeOutput;
import net.minecraft.advancements.AdvancementHolder;
//?}

import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import static ironfurnaces.loaders.IronFurnaces.REGISTRATE;
import static ironfurnaces.registration.ModItemTags.bindC;
import static ironfurnaces.registration.ModItemTags.bindForge;

public class ModCustomRecipe {
    private static final String GENERATOR_ID = "generator_blasting";
    public static final CustomRecipeEntry<GeneratorRecipe> GENERATOR_RECIPE = REGISTRATE
            .entry(GENERATOR_ID, builderCallback -> new CustomRecipeBuilder<>(REGISTRATE, REGISTRATE, GENERATOR_ID, builderCallback, () -> GeneratorRecipe.SERIALIZER))
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
            })
            .register();

    public static void register(){
    }

    private static void acceptWhenTagNotEmpty(RecipeOutput provider, String id, int value, TagKey<Item> tag) {
        BuiltInRegistries.ITEM.get(tag).ifPresent(x -> {
            provider.accept(ResourceKey.create(Registries.RECIPE, Identifier.parse(id)), new GeneratorRecipe(value, Ingredient.of(x)), null, new NotCondition(new TagEmptyCondition(tag)));
        });


    }

}
