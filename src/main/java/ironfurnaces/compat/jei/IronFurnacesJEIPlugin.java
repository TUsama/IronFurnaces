package ironfurnaces.compat.jei;

import com.google.common.collect.Lists;
import ironfurnaces.Config;
import ironfurnaces.compat.jei.gui.FurnacesGuiHandler;
import ironfurnaces.gui.furnaces.BlockIronFurnaceScreenBase;
import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.recipes.GeneratorRecipe;
import ironfurnaces.recipes.SimpleGeneratorRecipe;
import ironfurnaces.registration.LegacyFurnaceBlocks;
import ironfurnaces.registration.ModItems;
import ironfurnaces.registration.ModCustomRecipe;
import ironfurnaces.tileentity.furnaces.BlockIronFurnaceTileBase;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

@JeiPlugin
public class IronFurnacesJEIPlugin implements IModPlugin {

	@Override
	public ResourceLocation getPluginUid() {
		return new ResourceLocation(IronFurnaces.MOD_ID, "plugin_" + IronFurnaces.MOD_ID);
	}



    @Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		if (Config.enableJeiPlugin.get())
		{
			registration.addRecipeCategories(new RecipeCategoryGeneratorBlasting(registration.getJeiHelpers().getGuiHelper()));
			registration.addRecipeCategories(new RecipeCategoryGeneratorSmoking(registration.getJeiHelpers().getGuiHelper()));
			registration.addRecipeCategories(new RecipeCategoryGeneratorRegular(registration.getJeiHelpers().getGuiHelper()));

		}
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		if (Config.enableJeiPlugin.get())
		{

			List<SimpleGeneratorRecipe> recipes = Lists.newArrayList();
			for (Item item : ForgeRegistries.ITEMS.getValues())
			{
				if (ForgeHooks.getBurnTime(new ItemStack(item), RecipeType.SMELTING) > 0)
				{
					ItemStack stack = new ItemStack(item);
					recipes.add(new SimpleGeneratorRecipe(ForgeHooks.getBurnTime(new ItemStack(item), RecipeType.SMELTING) * 20, stack));
				}
			}
			registration.addRecipes(ModCustomRecipe.GENERATOR_REGULAR, recipes);

			List<GeneratorRecipe> recipes1 = Lists.newArrayList();
			List<GeneratorRecipe> list = Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(ModCustomRecipe.GENERATOR_RECIPE.get()).stream().toList();
			for (GeneratorRecipe item : list)
			{
				recipes1.add(item);
			}
			registration.addRecipes(ModCustomRecipe.GENERATOR_RECIPE.asJEIRecipeType().get(), recipes1);

			List<SimpleGeneratorRecipe> recipes2 = Lists.newArrayList();
			for (Item item : ForgeRegistries.ITEMS.getValues())
			{
				if (item.getFoodProperties() != null)
				{
					if (item.getFoodProperties().getNutrition() > 0)
					{
						ItemStack stack = new ItemStack(item);
						recipes2.add(new SimpleGeneratorRecipe(BlockIronFurnaceTileBase.getSmokingBurn(stack) * 40, stack));
					}
				}
			}
			registration.addRecipes(ModCustomRecipe.GENERATOR_SMOKING, recipes2);


		}
	}


	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
		if (Config.enableJeiPlugin.get() && Config.enableJeiCatalysts.get()) {
			registry.addRecipeCatalyst(new ItemStack(ModItems.BLASTING_AUGMENT.get()), RecipeTypes.BLASTING);
			registry.addRecipeCatalyst(new ItemStack(ModItems.SMOKING_AUGMENT.get()), RecipeTypes.SMOKING);

			registry.addRecipeCatalyst(new ItemStack(ModItems.GENERATOR_AUGMENT.get()), ModCustomRecipe.GENERATOR_REGULAR);
			registry.addRecipeCatalyst(new ItemStack(ModItems.GENERATOR_AUGMENT.get()), ModCustomRecipe.GENERATOR_RECIPE.asJEIRecipeType().get());
			registry.addRecipeCatalyst(new ItemStack(ModItems.GENERATOR_AUGMENT.get()), ModCustomRecipe.GENERATOR_SMOKING);

			registry.addRecipeCatalyst(new ItemStack(ModItems.FACTORY_AUGMENT.get()), RecipeTypes.SMELTING);

			registry.addRecipeCatalyst(new ItemStack(LegacyFurnaceBlocks.IRON_FURNACE.get()), RecipeTypes.SMELTING);
			registry.addRecipeCatalyst(new ItemStack(LegacyFurnaceBlocks.GOLD_FURNACE.get()), RecipeTypes.SMELTING);
			registry.addRecipeCatalyst(new ItemStack(LegacyFurnaceBlocks.DIAMOND_FURNACE.get()), RecipeTypes.SMELTING);
			registry.addRecipeCatalyst(new ItemStack(LegacyFurnaceBlocks.EMERALD_FURNACE.get()), RecipeTypes.SMELTING);
			registry.addRecipeCatalyst(new ItemStack(LegacyFurnaceBlocks.OBSIDIAN_FURNACE.get()), RecipeTypes.SMELTING);
			registry.addRecipeCatalyst(new ItemStack(LegacyFurnaceBlocks.CRYSTAL_FURNACE.get()), RecipeTypes.SMELTING);
			registry.addRecipeCatalyst(new ItemStack(LegacyFurnaceBlocks.NETHERITE_FURNACE.get()), RecipeTypes.SMELTING);
			registry.addRecipeCatalyst(new ItemStack(LegacyFurnaceBlocks.COPPER_FURNACE.get()), RecipeTypes.SMELTING);
			registry.addRecipeCatalyst(new ItemStack(LegacyFurnaceBlocks.SILVER_FURNACE.get()), RecipeTypes.SMELTING);

			if (Config.enableRainbowContent.get()) {
				registry.addRecipeCatalyst(new ItemStack(LegacyFurnaceBlocks.MILLION_FURNACE.get()), RecipeTypes.SMELTING);
			}

			registry.addRecipeCatalyst(new ItemStack(LegacyFurnaceBlocks.IRON_FURNACE.get()), RecipeTypes.FUELING);
			registry.addRecipeCatalyst(new ItemStack(LegacyFurnaceBlocks.GOLD_FURNACE.get()), RecipeTypes.FUELING);
			registry.addRecipeCatalyst(new ItemStack(LegacyFurnaceBlocks.DIAMOND_FURNACE.get()), RecipeTypes.FUELING);
			registry.addRecipeCatalyst(new ItemStack(LegacyFurnaceBlocks.EMERALD_FURNACE.get()), RecipeTypes.FUELING);
			registry.addRecipeCatalyst(new ItemStack(LegacyFurnaceBlocks.OBSIDIAN_FURNACE.get()), RecipeTypes.FUELING);
			registry.addRecipeCatalyst(new ItemStack(LegacyFurnaceBlocks.CRYSTAL_FURNACE.get()), RecipeTypes.FUELING);
			registry.addRecipeCatalyst(new ItemStack(LegacyFurnaceBlocks.NETHERITE_FURNACE.get()), RecipeTypes.FUELING);
			registry.addRecipeCatalyst(new ItemStack(LegacyFurnaceBlocks.COPPER_FURNACE.get()), RecipeTypes.FUELING);
			registry.addRecipeCatalyst(new ItemStack(LegacyFurnaceBlocks.SILVER_FURNACE.get()), RecipeTypes.FUELING);

			if (Config.enableRainbowContent.get()) {
				registry.addRecipeCatalyst(new ItemStack(LegacyFurnaceBlocks.MILLION_FURNACE.get()), RecipeTypes.FUELING);
			}

			registry.addRecipeCatalyst(new ItemStack(ModItems.BLASTING_AUGMENT.get()), ModCustomRecipe.GENERATOR_RECIPE.asJEIRecipeType().get());
			registry.addRecipeCatalyst(new ItemStack(ModItems.SMOKING_AUGMENT.get()), ModCustomRecipe.GENERATOR_SMOKING);



			if (ModList.get().isLoaded("allthemodium"))
			{
				registry.addRecipeCatalyst(new ItemStack(LegacyFurnaceBlocks.ALLTHEMODIUM_FURNACE.get()), RecipeTypes.SMELTING);
				registry.addRecipeCatalyst(new ItemStack(LegacyFurnaceBlocks.VIBRANIUM_FURNACE.get()), RecipeTypes.SMELTING);
				registry.addRecipeCatalyst(new ItemStack(LegacyFurnaceBlocks.UNOBTAINIUM_FURNACE.get()), RecipeTypes.SMELTING);
				registry.addRecipeCatalyst(new ItemStack(LegacyFurnaceBlocks.ALLTHEMODIUM_FURNACE.get()), RecipeTypes.FUELING);
				registry.addRecipeCatalyst(new ItemStack(LegacyFurnaceBlocks.VIBRANIUM_FURNACE.get()), RecipeTypes.FUELING);
				registry.addRecipeCatalyst(new ItemStack(LegacyFurnaceBlocks.UNOBTAINIUM_FURNACE.get()), RecipeTypes.FUELING);
			}
		}
	}

	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registry) {
        registry.addGenericGuiContainerHandler(BlockIronFurnaceScreenBase.class, new FurnacesGuiHandler());
	}

}




