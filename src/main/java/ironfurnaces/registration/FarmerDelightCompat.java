//? fd {
/*package ironfurnaces.registration;

import dev.anvilcraft.lib.v2.registrum.util.entry.ItemEntry;
import ironfurnaces.items.augments.compat.ItemAugmentFarmerDelightCooking;
import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.registration.util.ConditionRecipeUtil;
import ironfurnaces.registration.util.CriterionUtil;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;

import static ironfurnaces.registration.ModItemTags.bindForge;


public class FarmerDelightCompat {
    public static final ItemEntry<ItemAugmentFarmerDelightCooking> FD_AUGMENT =
            ModItems.registerItem("augment_farmer_delight_cooking_pot", "Augment: Cooking Pot", ItemAugmentFarmerDelightCooking::new)
                    .recipe((ctx, provider) -> {
                        ConditionRecipeUtil.whenHasMod(x -> ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                                .pattern("###")
                                .pattern("#X#")
                                .pattern("###")
                                .define('#', bindForge("ingots/iron"))
                                .define('X', vectorwing.farmersdelight.common.registry.ModItems.COOKING_POT.get())
                                .unlockedBy("has_iron", CriterionUtil.has(bindForge("ingots/iron"), provider))
                                .unlockedBy("has_pot", CriterionUtil.has(vectorwing.farmersdelight.common.registry.ModItems.COOKING_POT.get(), provider))
                                .save(x, IronFurnaces.id("augments/" + ctx.getName())), ctx, "compat/farmersdelight", ctx.getName(), "farmersdelight", provider);

                    })
                    .register();


    public static void register() {

    }
}

*///?}