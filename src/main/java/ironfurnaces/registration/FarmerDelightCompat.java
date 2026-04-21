package ironfurnaces.registration;

import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.entry.ItemEntry;
import ironfurnaces.items.augments.compat.ItemAugmentFarmerDelightCooking;
import ironfurnaces.items.upgrades.ItemUpgradeIron;
import ironfurnaces.loaders.IronFurnaces;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;

import static ironfurnaces.registration.ModItemTags.bindForge;


public class FarmerDelightCompat {
    public static final ItemEntry<ItemAugmentFarmerDelightCooking> FD_AUGMENT =
            ModItems.registerItem("augment_farmer_delight_cooking_pot", "Augment: Cooking Pot", ItemAugmentFarmerDelightCooking::new)
                    .recipe((ctx, provider) -> {
                        LegacyFurnaceBlocks.whenHasMod(x -> ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                                .pattern("###")
                                .pattern("#X#")
                                .pattern("###")
                                .define('#', bindForge("ingots/iron"))
                                .define('X', vectorwing.farmersdelight.common.registry.ModItems.COOKING_POT.get())
                                .unlockedBy("has_iron", RegistrateRecipeProvider.has(bindForge("ingots/iron")))
                                .unlockedBy("has_pot", RegistrateRecipeProvider.has(vectorwing.farmersdelight.common.registry.ModItems.COOKING_POT.get()))
                                .save(x, IronFurnaces.id("augments/" + ctx.getName())), ctx, "compat/farmersdelight", ctx.getName(), "farmersdelight", provider);

                    })
                    .register();


    public static void register() {

    }
}
