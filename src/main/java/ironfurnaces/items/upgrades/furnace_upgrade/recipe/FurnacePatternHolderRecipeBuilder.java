package ironfurnaces.items.upgrades.furnace_upgrade.recipe;

import ironfurnaces.blocks.furnaces.new_furnace.FurnacePatternHolderItem;
import ironfurnaces.registration.ModDataComponents;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;

public class FurnacePatternHolderRecipeBuilder {


    public static ShapedRecipeBuilder shaped(HolderGetter<Item> getter, RecipeCategory category, FurnacePatternHolderItem result, Identifier patternId) {
        return shaped(getter, category, result, patternId, 1);
    }

    public static ShapedRecipeBuilder shaped(
            HolderGetter<Item> getter,
            RecipeCategory category,
            FurnacePatternHolderItem result,
            Identifier patternId,
            int count
    ) {
        DataComponentPatch patch = DataComponentPatch.builder()
                .set(ModDataComponents.FURNACE_PATTERN_COMPONENT.get(), patternId)
                .build();

        ItemStackTemplate template = new ItemStackTemplate(
                result.asItem(),
                count,
                patch
        );

        return ShapedRecipeBuilder.shaped(getter, category, template);
    }
}

