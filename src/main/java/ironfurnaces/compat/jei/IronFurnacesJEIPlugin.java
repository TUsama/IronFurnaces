package ironfurnaces.compat.jei;

import com.google.common.collect.Lists;
import ironfurnaces.compat.jei.gui.FurnacesGuiHandlerForNewSet;
import ironfurnaces.config.FurnaceConfig;
import ironfurnaces.gui.furnaces.FurnacePatternScreen;
import ironfurnaces.items.upgrades.furnace_pattern.IPatternAccessor;
import ironfurnaces.items.upgrades.furnace_upgrade.IUpgradeStorage;
import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.recipes.ClientRecipeCache;
import ironfurnaces.recipes.SimpleGeneratorRecipe;
import ironfurnaces.registration.JEICompat;
import ironfurnaces.registration.ModBlocks;
import ironfurnaces.registration.ModCustomRecipe;
import ironfurnaces.registration.ModItems;
import ironfurnaces.tileentity.furnaces.pattern.FurnacePattern;
import ironfurnaces.tileentity.furnaces.pattern.FurnacePatternManager;
import ironfurnaces.tileentity.furnaces.pattern.upgrade.PatternUpgradeRule;
import ironfurnaces.tileentity.furnaces.pattern.upgrade.PatternUpgradeRuleManager;
import ironfurnaces.util.FuelBurnTimeUtil;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.List;

@JeiPlugin

public class IronFurnacesJEIPlugin implements IModPlugin {

    @Override
    public Identifier getPluginUid() {
        return IronFurnaces.id("plugin_" + IronFurnaces.MOD_ID);
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        // pattern holder：按 pattern 区分
        registration.registerSubtypeInterpreter(
                ModBlocks.PATTERN_HOLDER.asItem(),
                (stack, context) -> {
                    FurnacePattern pattern = IPatternAccessor.getFurnacePatternFromTag(stack);
                    return pattern == null ? "" : pattern.id().toString();
                }
        );


        registration.registerSubtypeInterpreter(
                ModItems.UPGRADE_TOOL.asItem(),
                (stack, context) -> {
                    PatternUpgradeRule rule = IUpgradeStorage.get(stack);
                    return rule == null ? "" : rule.id().toString();
                }
        );


    }

    @Override
    public void registerExtraIngredients(IExtraIngredientRegistration registration) {

        List<ItemStack> stacks = Lists.newArrayList();

        // pattern holder
        FurnacePatternManager.allPossiblePattern().forEach(pattern -> {
            ItemStack stack = new ItemStack(ModBlocks.PATTERN_HOLDER.asItem());
            IPatternAccessor.writePatternToItemStack(stack, pattern);
            stacks.add(stack);
        });

        // upgrade tool
        PatternUpgradeRuleManager.snapshot().values().forEach(rule -> {
            ItemStack stack = new ItemStack(ModItems.UPGRADE_TOOL.asItem());
            IUpgradeStorage.writeRule(stack, rule);
            stacks.add(stack);
        });

        registration.addExtraItemStacks(stacks);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new RecipeCategoryGeneratorBlasting(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new RecipeCategoryGeneratorSmoking(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new RecipeCategoryGeneratorRegular(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        {

            List<SimpleGeneratorRecipe> recipes = Lists.newArrayList();
            for (Item item : BuiltInRegistries.ITEM) {
                if (FuelBurnTimeUtil.getBurnTime(new ItemStack(item), RecipeType.SMELTING) > 0) {
                    ItemStack stack = new ItemStack(item);
                    recipes.add(new SimpleGeneratorRecipe(FuelBurnTimeUtil.getBurnTime(new ItemStack(item), RecipeType.SMELTING) * 20, stack));
                }
            }
            registration.addRecipes(JEICompat.GENERATOR_REGULAR, recipes);


            registration.addRecipes(
                    ModCustomRecipe.GENERATOR_RECIPE.asJEIRecipeType().get(),
                    ClientRecipeCache.generatorRecipes()
            );

            List<SimpleGeneratorRecipe> recipes2 = Lists.newArrayList();
            for (Item item : BuiltInRegistries.ITEM) {
                if (item.getDefaultInstance().has(DataComponents.FOOD)) {
                    FoodProperties foodProperties = item.getDefaultInstance().get(DataComponents.FOOD);
                    if (foodProperties != null) {
                        if (foodProperties.nutrition() > 0) {
                            ItemStack stack = new ItemStack(item);

                            recipes2.add(new SimpleGeneratorRecipe(foodProperties.nutrition() * FurnaceConfig.config.nutrition_to_energy_factor, stack));
                        }
                    }
                }

            }
            registration.addRecipes(JEICompat.GENERATOR_SMOKING, recipes2);


        }
    }


    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {

        registry.addRecipeCatalyst(new ItemStack(ModItems.BLASTING_AUGMENT.get()), ModCustomRecipe.GENERATOR_RECIPE.asJEIRecipeType().get(), RecipeTypes.BLASTING);
        registry.addRecipeCatalyst(new ItemStack(ModItems.SMOKING_AUGMENT.get()), JEICompat.GENERATOR_SMOKING, RecipeTypes.SMOKING);
        registry.addRecipeCatalyst(ModBlocks.PATTERN_HOLDER, RecipeTypes.SMELTING, RecipeTypes.SMELTING_FUEL);

        registry.addRecipeCatalyst(new ItemStack(ModItems.GENERATOR_AUGMENT.get()), JEICompat.GENERATOR_REGULAR, ModCustomRecipe.GENERATOR_RECIPE.asJEIRecipeType().get(), JEICompat.GENERATOR_SMOKING);
        registry.addRecipeCatalyst(new ItemStack(ModItems.FACTORY_AUGMENT.get()), RecipeTypes.SMELTING);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registry) {

        registry.addGuiContainerHandler(FurnacePatternScreen.class, new FurnacesGuiHandlerForNewSet());
    }

}




