
//? <1.21.11 {
package ironfurnaces.items.upgrades.furnace_upgrade.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import ironfurnaces.blocks.furnaces.new_furnace.FurnacePatternHolderItem;
import ironfurnaces.items.upgrades.furnace_pattern.IPatternAccessor;
import ironfurnaces.mixin.ShapedRecipeBuilderMixin;
import net.minecraft.core.HolderGetter;
import net.minecraft.Util;
import net.minecraft.advancements.Advancement;

import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;

import net.minecraft.world.level.ItemLike;
//? 1.20.1 {
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.advancements.RequirementsStrategy;
import ironfurnaces.mixin.ShapedRecipeBuilderResultAccessor;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
//? } else {
/*import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
//? >1.21.11 {
/^import net.minecraft.world.item.ItemStackTemplate;
^///?}
*///?}


import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class WriteDataToItemStackShapedRecipeBuilder extends ShapedRecipeBuilder {
    private final Consumer<ItemStack> writer;
    //~ if > 1.21.11 '(RecipeCategory category' -> '(HolderGetter<Item> items, RecipeCategory category'
    public WriteDataToItemStackShapedRecipeBuilder(RecipeCategory category, ItemLike result, int count, Consumer<ItemStack> writer) {


        //? 1.20.1 {
        super(category, result, count);
         //?} else {
        /*super(category, Util.make(() -> {
            ItemStack copy = result.asItem().getDefaultInstance();
            writer.accept(copy);
            copy.setCount(count);
            return copy;
        }));
        *///?}


        this.writer = writer;
    }


    //? 1.20.1 {
    @Override
            //~ if >1.20.1 'Consumer<FinishedRecipe>' -> 'RecipeOutput'
    public void save(Consumer<FinishedRecipe> finishedRecipeConsumer, ResourceLocation recipeId) {
        ShapedRecipeBuilderMixin accessor = (ShapedRecipeBuilderMixin) this;
                accessor.callEnsureValid(recipeId);
        accessor.getAdvancement().parent(ROOT_RECIPE_ADVANCEMENT).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).requirements(RequirementsStrategy.OR);
        finishedRecipeConsumer.accept(
                new PatternResult(
                        recipeId,
                        Util.make(() -> {
                            ItemStack itemStack = new ItemStack(accessor.getResult(), accessor.getCount());
                            writer.accept(itemStack);
                            return itemStack;
                        }),
                        accessor.getCount(),
                        accessor.getGroup() == null ? "" : accessor.getGroup(),
                        determineBookCategory(accessor.getCategory()),
                        accessor.getRows(),
                        accessor.getKey(),
                        accessor.getAdvancement(),
                        recipeId.withPrefix("recipes/" + accessor.getCategory().getFolderName() + "/"),
                        accessor.isShowNotification()
                )
        );



    }
//?}


    //? 1.20.1 {
    public static class PatternResult extends Result {
        private ItemStack stack;
        public PatternResult(ResourceLocation id, ItemStack result, int count, String group, CraftingBookCategory category, List<String> pattern, Map<Character, Ingredient> key, Advancement.Builder advancement, ResourceLocation advancementId, boolean showNotification) {
            super(id, result.getItem(), count, group, category, pattern, key, advancement, advancementId, showNotification);
            this.stack = result;
        }
        @Override
        public void serializeRecipeData(JsonObject json) {
            ShapedRecipeBuilderResultAccessor acc =
                    (ShapedRecipeBuilderResultAccessor)(Object) this;

            if (!acc.getGroup().isEmpty()) {
                json.addProperty("group", acc.getGroup());
            }

            JsonArray jsonarray = new JsonArray();
            for (String s : acc.getPattern()) {
                jsonarray.add(s);
            }
            json.add("pattern", jsonarray);

            JsonObject jsonobject = new JsonObject();
            for (Map.Entry<Character, Ingredient> entry : acc.getKey().entrySet()) {
                jsonobject.add(
                        String.valueOf(entry.getKey()),
                        entry.getValue().toJson()
                );
            }
            json.add("key", jsonobject);

            JsonObject jsonobject1 = new JsonObject();
            jsonobject1.addProperty(
                    "item",
                    BuiltInRegistries.ITEM.getKey(acc.getResult()).toString()
            );

            if (acc.getCount() > 1) {
                jsonobject1.addProperty("count", acc.getCount());
            }

            if (stack.hasTag()) jsonobject1.addProperty("nbt", stack.getTag().toString());

            json.add("result", jsonobject1);
            json.addProperty("show_notification", acc.isShowNotification());
        }

    }
    //? }
}
//?}