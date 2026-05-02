//~ replace_rl
package ironfurnaces.items.upgrades.furnace_upgrade.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import ironfurnaces.items.upgrades.furnace_pattern.IPatternAccessor;
import ironfurnaces.items.upgrades.furnace_upgrade.IUpgradeStorage;
import ironfurnaces.items.upgrades.furnace_upgrade.ItemUpgradeTool;
import ironfurnaces.mixin.ShapedRecipeBuilderMixin;

import net.minecraft.advancements.Advancement;

import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.Util;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import net.minecraft.world.level.ItemLike;
//? 1.20.1 {
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.advancements.RequirementsStrategy;
import ironfurnaces.mixin.ShapedRecipeBuilderResultAccessor;
//? } else {
/*import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.advancements.AdvancementRequirements;
//? <1.21.11 {

import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
//?}
*///?}
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
//~ if 26.1.2 'PatternUpgradeRecipeBuilder extends WriteDataToItemStackShapedRecipeBuilder' -> 'PatternUpgradeRecipeBuilder'
public class PatternUpgradeRecipeBuilder extends WriteDataToItemStackShapedRecipeBuilder {
    //? 26.1.2 {
    /*public static ShapedRecipeBuilder shaped(HolderGetter<Item> getter, RecipeCategory category, ItemUpgradeTool result, String upgradeId) {
        return shaped(getter, category, result, upgradeId, 1);
    }

    public static ShapedRecipeBuilder shaped(HolderGetter<Item> getter, RecipeCategory category, ItemUpgradeTool result, String upgradeId, int count) {
        return ShapedRecipeBuilder.shaped(getter, category, Util.make(() -> {
            ItemStack copy = result.asItem().getDefaultInstance();
            IUpgradeStorage.writeRule(copy, ResourceLocation.parse(upgradeId));
            copy.setCount(count);
            return copy.getItem();
        }), count);
    }

    *///?} else {
    public PatternUpgradeRecipeBuilder(RecipeCategory category, ItemLike result, int count, Consumer<ItemStack> writer) {
        super(category, result, count, writer);
    }

    public static PatternUpgradeRecipeBuilder shaped(RecipeCategory category, ItemUpgradeTool result, ResourceLocation upgradeId) {
        return shaped(category, result, upgradeId, 1);
    }

    public static PatternUpgradeRecipeBuilder shaped(RecipeCategory category, ItemUpgradeTool result, ResourceLocation upgradeId, int count) {
        return new PatternUpgradeRecipeBuilder(category, result, count, stack -> IUpgradeStorage.writeRule(stack, upgradeId));
    }


    //?}
}
