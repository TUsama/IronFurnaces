package ironfurnaces.items.upgrades.furnace_upgrade.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import ironfurnaces.items.upgrades.furnace_upgrade.IUpgradeStorage;
import ironfurnaces.items.upgrades.furnace_upgrade.ItemUpgradeTool;
import ironfurnaces.mixin.ShapedRecipeBuilderMixin;
import ironfurnaces.mixin.ShapedRecipeBuilderResultAccessor;
import net.minecraft.Util;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class TierUpgradeRecipeBuilder extends ShapedRecipeBuilder {
    private ResourceLocation upgradeId;

    public TierUpgradeRecipeBuilder(RecipeCategory category, ItemLike result, int count, ResourceLocation upgradeId) {
        super(category, result, count);
        this.upgradeId = upgradeId;
    }

    public static TierUpgradeRecipeBuilder shaped(RecipeCategory category, ItemUpgradeTool result, ResourceLocation upgradeId) {
        return shaped(category, result, upgradeId, 1);
    }

    public static TierUpgradeRecipeBuilder shaped(RecipeCategory category, ItemUpgradeTool result, ResourceLocation upgradeId, int count) {
        return new TierUpgradeRecipeBuilder(category, result, count, upgradeId);
    }

    @Override
    public void save(Consumer<FinishedRecipe> finishedRecipeConsumer, ResourceLocation recipeId) {
        ShapedRecipeBuilderMixin accessor = (ShapedRecipeBuilderMixin) this;
        accessor.callEnsureValid(recipeId);
        accessor.getAdvancement().parent(ROOT_RECIPE_ADVANCEMENT).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).requirements(RequirementsStrategy.OR);
        finishedRecipeConsumer.accept(
                new UpgradeResult(
                        recipeId,
                        Util.make(() -> {
                            ItemStack itemStack = new ItemStack(accessor.getResult());
                            IUpgradeStorage.writeRule(itemStack, upgradeId);
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

    public static class UpgradeResult extends Result {
        private ItemStack stack;
        public UpgradeResult(ResourceLocation id, ItemStack result, int count, String group, CraftingBookCategory category, List<String> pattern, Map<Character, Ingredient> key, Advancement.Builder advancement, ResourceLocation advancementId, boolean showNotification) {
            super(id, result.getItem(), count, group, category, pattern, key, advancement, advancementId, showNotification);
            this.stack = result;
        }

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
}
