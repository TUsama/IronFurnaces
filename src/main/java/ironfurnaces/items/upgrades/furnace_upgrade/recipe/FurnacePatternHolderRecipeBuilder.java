package ironfurnaces.items.upgrades.furnace_upgrade.recipe;

import ironfurnaces.blocks.furnaces.new_furnace.FurnacePatternHolderItem;
import ironfurnaces.items.upgrades.furnace_pattern.IPatternAccessor;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

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
