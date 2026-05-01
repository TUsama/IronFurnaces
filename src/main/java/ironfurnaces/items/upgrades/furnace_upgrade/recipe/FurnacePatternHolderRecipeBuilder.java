//~ replace_rl
package ironfurnaces.items.upgrades.furnace_upgrade.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import ironfurnaces.blocks.furnaces.new_furnace.FurnacePatternHolderItem;
import ironfurnaces.items.upgrades.furnace_pattern.IPatternAccessor;
import ironfurnaces.mixin.ShapedRecipeBuilderMixin;
import net.minecraft.Util;
import net.minecraft.advancements.Advancement;

import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;

import net.minecraft.world.level.ItemLike;
//? 1.20.1 {
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.advancements.RequirementsStrategy;
import ironfurnaces.mixin.ShapedRecipeBuilderResultAccessor;
//? } else {
/*import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
*///?}


import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class FurnacePatternHolderRecipeBuilder extends WriteDataToItemStackShapedRecipeBuilder {


    public FurnacePatternHolderRecipeBuilder(RecipeCategory category, ItemLike result, int count, Consumer<ItemStack> writer) {
        super(category, result, count, writer);
    }

    public static FurnacePatternHolderRecipeBuilder shaped(RecipeCategory category, FurnacePatternHolderItem result, ResourceLocation patternId) {
        return shaped(category, result, patternId, 1);
    }

    public static FurnacePatternHolderRecipeBuilder shaped(RecipeCategory category, FurnacePatternHolderItem result, ResourceLocation patternId, int count) {
        return new FurnacePatternHolderRecipeBuilder(category, result, count, stack -> IPatternAccessor.writePatternToItemStack(stack, patternId));
    }


}
