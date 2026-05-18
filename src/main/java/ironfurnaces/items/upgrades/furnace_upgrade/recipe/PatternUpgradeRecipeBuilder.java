package ironfurnaces.items.upgrades.furnace_upgrade.recipe;

import ironfurnaces.items.upgrades.furnace_upgrade.IUpgradeStorage;
import ironfurnaces.items.upgrades.furnace_upgrade.ItemUpgradeTool;
import ironfurnaces.registration.ModDataComponents;
import ironfurnaces.registration.data_component.UpgradeRuleHolder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

import java.util.Optional;


public class PatternUpgradeRecipeBuilder {

    public static ShapedRecipeBuilder shaped(HolderGetter<Item> getter, RecipeCategory category, ItemUpgradeTool result, String upgradeId) {
        return shaped(getter, category, result, upgradeId, 1);
    }

    public static ShapedRecipeBuilder shaped(
            HolderGetter<Item> getter,
            RecipeCategory category,
            ItemUpgradeTool result,
            String upgradeId,
            int count
    ) {
        DataComponentPatch patch = DataComponentPatch.builder()
                .set(ModDataComponents.UPGRADE_RULE_HOLDER.get(), new UpgradeRuleHolder(Optional.of(Identifier.parse(upgradeId))))
                .build();

        ItemStackTemplate template = new ItemStackTemplate(
                result.asItem(),
                count,
                patch
        );

        return ShapedRecipeBuilder.shaped(getter, category, template);
    }


}
