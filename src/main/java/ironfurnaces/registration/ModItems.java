package ironfurnaces.registration;

import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import ironfurnaces.items.*;
import ironfurnaces.items.augments.*;
import ironfurnaces.items.upgrades.*;
import ironfurnaces.items.upgrades.furnace_upgrade.ItemUpgradeTool;
import ironfurnaces.items.upgrades.furnace_upgrade.recipe.TierUpgradeRecipeBuilder;
import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.tileentity.furnaces.tier.upgrade.TierUpgradeRule;
import ironfurnaces.util.RainbowEnabledCondition;
import net.minecraft.Util;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.TagEmptyCondition;

import static ironfurnaces.loaders.IronFurnaces.REGISTRATE;
import static ironfurnaces.registration.ModItemTags.*;

public class ModItems {


    public static final ItemEntry<ItemUpgradeIron> IRON_UPGRADE =
            registerItem("upgrade_iron", "Upgrade: Stone -> Iron", ItemUpgradeIron::new)
                    .recipe((ctx, provider) -> {
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                                .pattern("###")
                                .pattern("#X#")
                                .pattern("###")
                                .define('#', bindForge("ingots/iron"))
                                .define('X', bindVanilla("stone_tool_materials"))
                                .unlockedBy("has_iron", RegistrateRecipeProvider.has(bindForge("ingots/iron")))
                                .save(provider, IronFurnaces.id("upgrades/" + ctx.getName()));
                    })
                    .register();

    public static final ItemEntry<ItemUpgradeGold> GOLD_UPGRADE =
            registerItem("upgrade_gold", "Upgrade: Iron -> Gold", ItemUpgradeGold::new)
                    .recipe((ctx, provider) -> {
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                                .pattern("###")
                                .pattern("#X#")
                                .pattern("#Y#")
                                .define('#', bindForge("ingots/gold"))
                                .define('X', bindForge("ingots/iron"))
                                .define('Y', bindForge("storage_blocks/gold"))
                                .unlockedBy("has_gold", RegistrateRecipeProvider.has(bindForge("ingots/gold")))
                                .save(provider, IronFurnaces.id("upgrades/" + ctx.getName()));
                    })
                    .register();

    public static final ItemEntry<ItemUpgradeDiamond> DIAMOND_UPGRADE =
            registerItem("upgrade_diamond", "Upgrade: Gold -> Diamond", ItemUpgradeDiamond::new)
                    .recipe((ctx, provider) -> {
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                                .pattern("###")
                                .pattern("GXG")
                                .pattern("###")
                                .define('#', bindForge("gems/diamond"))
                                .define('X', bindForge("ingots/gold"))
                                .define('G', bindForge("glass"))
                                .unlockedBy("has_diamond", RegistrateRecipeProvider.has(bindForge("gems/diamond")))
                                .save(provider, IronFurnaces.id("upgrades/" + ctx.getName()));
                    })
                    .register();

    public static final ItemEntry<ItemUpgradeEmerald> EMERALD_UPGRADE =
            registerItem("upgrade_emerald", "Upgrade: Diamond -> Emerald", ItemUpgradeEmerald::new)
                    .recipe((ctx, provider) -> {
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                                .pattern("###")
                                .pattern("#X#")
                                .pattern("###")
                                .define('#', bindForge("gems/emerald"))
                                .define('X', bindForge("gems/diamond"))
                                .unlockedBy("has_emerald", RegistrateRecipeProvider.has(bindForge("gems/emerald")))
                                .save(provider, IronFurnaces.id("upgrades/" + ctx.getName()));
                    })
                    .register();

    public static final ItemEntry<ItemUpgradeObsidian> OBSIDIAN_UPGRADE =
            registerItem("upgrade_obsidian", "Upgrade: Emerald -> Obsidian", ItemUpgradeObsidian::new).recipe((ctx, provider) -> {
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                                .pattern("#Y#")
                                .pattern("YXY")
                                .pattern("#Y#")
                                .define('#', bindForge("obsidian"))
                                .define('X', bindForge("gems/emerald"))
                                .define('Y', bindForge("rods/blaze"))
                                .unlockedBy("has_obsidian", RegistrateRecipeProvider.has(bindForge("obsidian")))
                                .save(provider, IronFurnaces.id("upgrades/" + ctx.getName()));
                    })
                    .register();

    public static final ItemEntry<ItemUpgradeCrystal> CRYSTAL_UPGRADE =
            registerItem("upgrade_crystal", "Upgrade: Diamond -> Crystal", ItemUpgradeCrystal::new)
                    .recipe((ctx, provider) -> {
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                                .pattern("###")
                                .pattern("#X#")
                                .pattern("###")
                                .define('#', bindForge("glass"))
                                .define('X', bindForge("gems/diamond"))
                                .unlockedBy("has_glass", RegistrateRecipeProvider.has(bindForge("glass")))
                                .save(provider, IronFurnaces.id("upgrades/" + ctx.getName()));
                    })
                    .register();

    public static final ItemEntry<ItemUpgradeNetherite> NETHERITE_UPGRADE =
            registerItem("upgrade_netherite", "Upgrade: Obsidian -> Netherite", ItemUpgradeNetherite::new).recipe((ctx, provider) -> {
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                                .pattern("N#N")
                                .pattern("#X#")
                                .pattern("NSN")
                                .define('#', Items.MAGMA_CREAM)
                                .define('X', bindForge("furnaces/obsidian"))
                                .define('S', bindVanilla("soul_fire_base_blocks"))
                                .define('N', Items.NETHERITE_INGOT)
                                .unlockedBy("has_netherite", RegistrateRecipeProvider.has(Items.NETHERITE_INGOT))
                                .save(provider, IronFurnaces.id("upgrades/" + ctx.getName()));
                    })
                    .register();

    public static final ItemEntry<ItemUpgradeCopper> COPPER_UPGRADE =
            registerItem("upgrade_copper", "Upgrade: Stone -> Copper", ItemUpgradeCopper::new)
                    .recipe((ctx, provider) -> {
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                                .pattern("###")
                                .pattern("#X#")
                                .pattern("###")
                                .define('#', bindForge("ingots/copper"))
                                .define('X', ModItemTags.PLAYER_WORKSTATIONS_FURNACE)
                                .unlockedBy("has_copper", RegistrateRecipeProvider.has(bindForge("ingots/copper")))
                                .save(provider, IronFurnaces.id("upgrades/" + ctx.getName()));
                    })
                    .register();

    public static final ItemEntry<ItemUpgradeSilver> SILVER_UPGRADE =
            registerItem("upgrade_silver", "Upgrade: Copper -> Silver", ItemUpgradeSilver::new).recipe((ctx, provider) -> {
                        LegacyFurnaceBlocks.whenHasTags(x -> ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                                .pattern("###")
                                .pattern("#S#")
                                .pattern("#X#")
                                .define('#', bindForge("ingots/silver"))
                                .define('X', bindForge("ingots/copper"))
                                .define('S', bindVanilla("stone_tool_materials"))
                                .unlockedBy("has_silver", RegistrateRecipeProvider.has(bindForge("ingots/silver")))
                                .save(x, IronFurnaces.id("upgrades/" + ctx.getName())), ctx, provider, "upgrades", ctx.getName(), ModItemTags.SILVER);

                    })
                    .register();

    public static final ItemEntry<ItemUpgradeObsidian2> OBSIDIAN2_UPGRADE =
            registerItem("upgrade_obsidian2", "Upgrade: Crystal -> Obsidian", ItemUpgradeObsidian2::new).recipe((ctx, provider) -> {
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                                .pattern("#Y#")
                                .pattern("YXY")
                                .pattern("#Y#")
                                .define('#', bindForge("obsidian"))
                                .define('X', bindForge("glass"))
                                .define('Y', bindForge("rods/blaze"))
                                .unlockedBy("has_obsidian", RegistrateRecipeProvider.has(bindForge("obsidian")))
                                .save(provider, IronFurnaces.id("upgrades/" + ctx.getName()));
                    })
                    .register();

    public static final ItemEntry<ItemUpgradeIron2> IRON2_UPGRADE =
            registerItem("upgrade_iron2", "Upgrade: Copper -> Iron", ItemUpgradeIron2::new)
                    .recipe((ctx, provider) -> {
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                                .pattern("###")
                                .pattern("GXG")
                                .pattern("###")
                                .define('#', bindForge("ingots/iron"))
                                .define('X', bindForge("ingots/copper"))
                                .define('G', bindForge("glass"))
                                .unlockedBy("has_diamond", RegistrateRecipeProvider.has(bindForge("gems/diamond")))
                                .save(provider, IronFurnaces.id("upgrades/" + ctx.getName()));
                    })
                    .register();

    public static final ItemEntry<ItemUpgradeGold2> GOLD2_UPGRADE =
            registerItem("upgrade_gold2", "Upgrade: Silver -> Gold", ItemUpgradeGold2::new)
                    .recipe((ctx, provider) -> {
                        LegacyFurnaceBlocks.whenHasTags(x -> ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                                .pattern("###")
                                .pattern("#X#")
                                .pattern("#Y#")
                                .define('#', bindForge("ingots/gold"))
                                .define('X', bindForge("ingots/silver"))
                                .define('Y', bindForge("storage_blocks/gold"))
                                .unlockedBy("has_gold", RegistrateRecipeProvider.has(bindForge("ingots/gold")))
                                .save(x, IronFurnaces.id("upgrades/" + ctx.getName())), ctx, provider, "upgrades", ctx.getName(), bindForge("ingots/silver"));
                        ;
                    })
                    .register();

    public static final ItemEntry<ItemUpgradeSilver2> SILVER2_UPGRADE =
            registerItem("upgrade_silver2", "Upgrade: Iron -> Silver", ItemUpgradeSilver2::new)
                    .recipe((ctx, provider) -> {
                        LegacyFurnaceBlocks.whenHasTags(x -> ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                                .pattern("#G#")
                                .pattern("GXG")
                                .pattern("#G#")
                                .define('#', bindForge("ingots/silver"))
                                .define('X', bindForge("ingots/iron"))
                                .define('G', bindForge("glass"))
                                .unlockedBy("has_silver", RegistrateRecipeProvider.has(bindForge("ingots/silver")))
                                .save(x, IronFurnaces.id("upgrades/" + ctx.getName())), ctx, provider, "upgrades", ctx.getName(), ModItemTags.SILVER);
                        ;
                    })
                    .register();

    public static final ItemEntry<ItemUpgradeAllthemodium> ALLTHEMODIUM_UPGRADE =
            registerItem("upgrade_allthemodium", "Upgrade: Netherite -> Allthemodium",
                    ItemUpgradeAllthemodium::new).recipe((ctx, provider) -> {
                        LegacyFurnaceBlocks.whenAllthemodium(x -> ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                                .pattern("B#B")
                                .pattern("#X#")
                                .pattern("B#B")
                                .define('#', bindForge("ingots/allthemodium"))
                                .define('B', bindForge("storage_blocks/allthemodium"))
                                .define('X', ModItemTags.NETHERITE_UPGRADE)
                                .unlockedBy("has_allthemodium", RegistrateRecipeProvider.has(bindForge("ingots/allthemodium")))
                                .save(x, IronFurnaces.id("upgrades/" + ctx.getName())), ctx, "upgrades", ctx.getName(), provider);
                    })
                    .register();

    public static final ItemEntry<ItemUpgradeVibranium> VIBRANIUM_UPGRADE =
            registerItem("upgrade_vibranium", "Upgrade: Allthemodium -> Vibranium",
                    ItemUpgradeVibranium::new).recipe((ctx, provider) -> {
                        LegacyFurnaceBlocks.whenAllthemodium(x -> ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                                .pattern("B#B")
                                .pattern("#X#")
                                .pattern("B#B")
                                .define('#', bindForge("ingots/vibranium"))
                                .define('B', bindForge("storage_blocks/vibranium"))
                                .define('X', bindForge("ingots/allthemodium"))
                                .unlockedBy("has_vibranium", RegistrateRecipeProvider.has(bindForge("ingots/vibranium")))
                                .save(x, IronFurnaces.id("upgrades/" + ctx.getName())), ctx, "upgrades", ctx.getName(), provider);

                    })
                    .register();

    public static final ItemEntry<ItemUpgradeUnobtainium> UNOBTAINIUM_UPGRADE =
            registerItem("upgrade_unobtainium", "Upgrade: Vibranium -> Unobtainium",
                    ItemUpgradeUnobtainium::new).recipe((ctx, provider) -> {
                        LegacyFurnaceBlocks.whenAllthemodium(x -> ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                                .pattern("B#B")
                                .pattern("#X#")
                                .pattern("B#B")
                                .define('#', bindForge("ingots/unobtainium"))
                                .define('B', bindForge("storage_blocks/unobtainium"))
                                .define('X', bindForge("ingots/vibranium"))
                                .unlockedBy("has_unobtainium", RegistrateRecipeProvider.has(bindForge("ingots/unobtainium")))
                                .save(x, IronFurnaces.id("upgrades/" + ctx.getName())), ctx, "upgrades", ctx.getName(), provider);

                    })
                    .register();


    public static final ItemEntry<ItemHeater> ITEM_HEATER =
            registerItem("item_heater", p -> new ItemHeater(p.stacksTo(1))).recipe((ctx, provider) -> {
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                                .pattern("#R#")
                                .pattern("RXR")
                                .pattern("#R#")
                                .define('#', bindForge("stone"))
                                .define('R', bindForge("dusts/redstone"))
                                .define('X', Items.COMPARATOR)
                                .unlockedBy("has_comparator", RegistrateRecipeProvider.has(Items.COMPARATOR))
                                .save(provider, IronFurnaces.id(ctx.getName()));
                    })
                    .register();

    public static final ItemEntry<ItemAugmentBlasting> BLASTING_AUGMENT =
            registerItem("augment_blasting", "Augment: Blasting", ItemAugmentBlasting::new)
                    .recipe((ctx, provider) -> {
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                                .pattern("#R#")
                                .pattern("PXP")
                                .pattern("#R#")
                                .define('#', bindForge("stone"))
                                .define('P', Items.PAPER)
                                .define('R', bindForge("dusts/redstone"))
                                .define('X', Items.BLAST_FURNACE)
                                .unlockedBy("has_blast_furnace", RegistrateRecipeProvider.has(Items.BLAST_FURNACE))
                                .save(provider, IronFurnaces.id("augments/" + ctx.getName()));

                    })
                    .register();

    public static final ItemEntry<ItemAugmentSmoking> SMOKING_AUGMENT =
            registerItem("augment_smoking", "Augment: Smoking", ItemAugmentSmoking::new)
                    .recipe((ctx, provider) -> {
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                                .pattern("#R#")
                                .pattern("PXP")
                                .pattern("#R#")
                                .define('#', bindForge("stone"))
                                .define('P', Items.PAPER)
                                .define('R', bindForge("dusts/redstone"))
                                .define('X', Items.SMOKER)
                                .unlockedBy("has_smoker", RegistrateRecipeProvider.has(Items.SMOKER))
                                .save(provider, IronFurnaces.id("augments/" + ctx.getName()));


                    }).register();

    public static final ItemEntry<ItemAugmentFactory> FACTORY_AUGMENT =
            registerItem("augment_factory", "Augment: Factory", ItemAugmentFactory::new)
                    .recipe((ctx, provider) -> {
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                                .pattern("#R#")
                                .pattern("PXP")
                                .pattern("#R#")
                                .define('#', bindForge("stone"))
                                .define('P', Items.PAPER)
                                .define('R', bindForge("dusts/redstone"))
                                .define('X', Items.PISTON)
                                .unlockedBy("has_piston", RegistrateRecipeProvider.has(Items.PISTON))
                                .save(provider, IronFurnaces.id("augments/" + ctx.getName()));

                    }).register();

    public static final ItemEntry<ItemAugmentGenerator> GENERATOR_AUGMENT =
            registerItem("augment_generator", "Augment: Generator", ItemAugmentGenerator::new)
                    .recipe((ctx, provider) -> {
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                                .pattern("#R#")
                                .pattern("PXP")
                                .pattern("#R#")
                                .define('#', bindForge("stone"))
                                .define('P', Items.PAPER)
                                .define('R', bindForge("dusts/redstone"))
                                .define('X', Items.REPEATER)
                                .unlockedBy("has_repeater", RegistrateRecipeProvider.has(Items.REPEATER))
                                .save(provider, IronFurnaces.id("augments/" + ctx.getName()));


                    }).register();

    public static final ItemEntry<ItemAugmentSpeed> SPEED_AUGMENT =
            registerItem("augment_speed", "Augment: Speed", ItemAugmentSpeed::new)
                    .recipe((ctx, provider) -> {
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                                .pattern("#R#")
                                .pattern("PXP")
                                .pattern("#R#")
                                .define('#', bindForge("stone"))
                                .define('P', Items.PAPER)
                                .define('R', bindForge("dusts/redstone"))
                                .define('X', Items.SUGAR)
                                .unlockedBy("has_sugar", RegistrateRecipeProvider.has(Items.SUGAR))
                                .save(provider, IronFurnaces.id("augments/" + ctx.getName()));

                    }).register();

    public static final ItemEntry<ItemAugmentFuel> FUEL_AUGMENT =
            registerItem("augment_fuel", "Augment: Fuel Efficiency", ItemAugmentFuel::new)
                    .recipe((ctx, provider) -> {
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                                .pattern("#R#")
                                .pattern("PXP")
                                .pattern("#R#")
                                .define('#', bindForge("stone"))
                                .define('P', Items.PAPER)
                                .define('R', bindForge("dusts/redstone"))
                                .define('X', Items.COAL)
                                .unlockedBy("has_coal", RegistrateRecipeProvider.has(Items.COAL))
                                .save(provider, IronFurnaces.id("augments/" + ctx.getName()));

                    }).register();

    public static final ItemEntry<ItemSpooky> ITEM_SPOOKY =
            registerItem("item_spooky", ItemSpooky::new).recipe((ctx, provider) -> {
                        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ctx.get())
                                .requires(Items.CARVED_PUMPKIN)
                                .requires(ModItemTags.PLAYER_WORKSTATIONS_FURNACE)
                                .unlockedBy("has_furnace", RegistrateRecipeProvider.has(ModItemTags.PLAYER_WORKSTATIONS_FURNACE))
                                .save(provider, IronFurnaces.id(ctx.getName()));
                    })
                    .register();

    public static final ItemEntry<ItemXmas> ITEM_XMAS =
            registerItem("item_xmas", ItemXmas::new).recipe((ctx, provider) -> {
                        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ctx.get())
                                .requires(Items.YELLOW_DYE)
                                .requires(Items.RED_DYE)
                                .requires(Items.PAPER)
                                .requires(ModItemTags.PLAYER_WORKSTATIONS_FURNACE)
                                .unlockedBy("has_furnace", RegistrateRecipeProvider.has(ModItemTags.PLAYER_WORKSTATIONS_FURNACE))
                                .save(provider, IronFurnaces.id(ctx.getName()));
                    })
                    .register();

    public static final ItemEntry<ItemFurnaceCopy> ITEM_COPY =
            registerItem("item_copy", p -> new ItemFurnaceCopy(p.stacksTo(1)))
                    .recipe((ctx, provider) -> {
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                                .pattern(" # ")
                                .pattern("#X#")
                                .pattern(" # ")
                                .define('#', Items.PAPER)
                                .define('X', ModItemTags.PLAYER_WORKSTATIONS_FURNACE)
                                .unlockedBy("has_furnace", RegistrateRecipeProvider.has(ModItemTags.PLAYER_WORKSTATIONS_FURNACE))
                                .save(provider, IronFurnaces.id(ctx.getName()));
                    })
                    .register();

    public static final ItemEntry<ItemFurnaceCopyV2> ITEM_COPY_V2 =
            registerItem("item_copy", p -> new ItemFurnaceCopyV2(p.stacksTo(1)))
                    .recipe((ctx, provider) -> {
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                                .pattern("###")
                                .pattern("#X#")
                                .pattern("###")
                                .define('#', Items.PAPER)
                                .define('X', ModItemTags.PLAYER_WORKSTATIONS_FURNACE)
                                .unlockedBy("has_furnace", RegistrateRecipeProvider.has(ModItemTags.PLAYER_WORKSTATIONS_FURNACE))
                                .save(provider, IronFurnaces.id(ctx.getName()));
                    })
                    .lang("Copy Tool")
                    .register();

    public static final ItemEntry<Item> RAINBOW_CORE =
            registerItem("rainbow_core", Item::new).recipe((ctx, provider) -> {
                        ConditionalRecipe.builder()
                                .addCondition(Util.make(() -> (ICondition) new RainbowEnabledCondition()))
                                .addRecipe(x -> ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                                        .pattern("ROY")
                                        .pattern("FGF")
                                        .pattern("BPM")
                                        .define('R', Items.RED_STAINED_GLASS)
                                        .define('O', Items.ORANGE_STAINED_GLASS)
                                        .define('Y', Items.YELLOW_STAINED_GLASS)
                                        .define('F', LegacyFurnaceBlocks.NETHERITE_FURNACE)
                                        .define('G', Items.GREEN_STAINED_GLASS)
                                        .define('B', Items.BLUE_STAINED_GLASS)
                                        .define('P', Items.PURPLE_STAINED_GLASS)
                                        .define('M', Items.MAGENTA_STAINED_GLASS)
                                        .unlockedBy("has_netherite_furnace", RegistrateRecipeProvider.has(LegacyFurnaceBlocks.NETHERITE_FURNACE.get()))
                                        .save(x, IronFurnaces.id(ctx.getName())))
                                .build(provider, IronFurnaces.id(ctx.getName()));
                    })
                    .register();

    public static final ItemEntry<Item> RAINBOW_PLATING =
            registerItem("rainbow_plating", Item::new)
                    .recipe((ctx, provider) -> {
                        LegacyFurnaceBlocks.whenHasTags(x -> ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ctx.get(), 8)
                                .requires(LegacyFurnaceBlocks.IRON_FURNACE.get())
                                .requires(LegacyFurnaceBlocks.GOLD_FURNACE.get())
                                .requires(LegacyFurnaceBlocks.DIAMOND_FURNACE.get())
                                .requires(LegacyFurnaceBlocks.EMERALD_FURNACE.get())
                                .requires(LegacyFurnaceBlocks.OBSIDIAN_FURNACE.get())
                                .requires(LegacyFurnaceBlocks.CRYSTAL_FURNACE.get())
                                .requires(LegacyFurnaceBlocks.COPPER_FURNACE.get())
                                .requires(LegacyFurnaceBlocks.SILVER_FURNACE.get())
                                .unlockedBy("has_iron_furnace", RegistrateRecipeProvider.has(LegacyFurnaceBlocks.IRON_FURNACE.get()))
                                .save(x, IronFurnaces.id(ctx.getName())), ctx, provider, "", ctx.getName(), ModItemTags.SILVER);

                        ConditionalRecipe.builder()
                                .addCondition(new TagEmptyCondition(ModItemTags.SILVER.location()))
                                .addRecipe(x -> {
                                    ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ctx.get(), 8)
                                            .requires(LegacyFurnaceBlocks.IRON_FURNACE.get())
                                            .requires(LegacyFurnaceBlocks.GOLD_FURNACE.get())
                                            .requires(LegacyFurnaceBlocks.DIAMOND_FURNACE.get())
                                            .requires(LegacyFurnaceBlocks.EMERALD_FURNACE.get())
                                            .requires(LegacyFurnaceBlocks.OBSIDIAN_FURNACE.get())
                                            .requires(LegacyFurnaceBlocks.CRYSTAL_FURNACE.get())
                                            .requires(LegacyFurnaceBlocks.COPPER_FURNACE.get())
                                            .unlockedBy("has_iron_furnace", RegistrateRecipeProvider.has(LegacyFurnaceBlocks.IRON_FURNACE.get()))
                                            .save(x, IronFurnaces.id(ctx.getName() + "_no_silver"));
                                }).build(provider, IronFurnaces.id(ctx.getName() + "_no_silver"));
                    })
                    .register();

    public static final ItemEntry<ItemRainbowCoal> RAINBOW_COAL =
            registerItem("rainbow_coal", ItemRainbowCoal::new)
                    .recipe((ctx, provider) -> {
                LegacyFurnaceBlocks.whenHasTags(x -> ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ctx.get(), 8)
                        .requires(LegacyFurnaceBlocks.IRON_FURNACE.get())
                        .requires(LegacyFurnaceBlocks.GOLD_FURNACE.get())
                        .requires(LegacyFurnaceBlocks.DIAMOND_FURNACE.get())
                        .requires(LegacyFurnaceBlocks.EMERALD_FURNACE.get())
                        .requires(LegacyFurnaceBlocks.OBSIDIAN_FURNACE.get())
                        .requires(LegacyFurnaceBlocks.CRYSTAL_FURNACE.get())
                        .requires(LegacyFurnaceBlocks.COPPER_FURNACE.get())
                        .unlockedBy("has_iron_furnace", RegistrateRecipeProvider.has(LegacyFurnaceBlocks.IRON_FURNACE.get()))
                        .save(provider, IronFurnaces.id(ctx.getName())), ctx, provider, "", ctx.getName(), ModItemTags.SILVER);
            }).register();

    public static final ItemEntry<ItemUpgradeTool> UPGRADE_TOOL =
            registerItem("upgrade_tool", ItemUpgradeTool::new)
                    .recipe((ctx, provider) -> {

                        TierUpgradeRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get(), IronFurnaces.id("upgrade_gold"))
                                .pattern("###")
                                .pattern("#X#")
                                .pattern("#Y#")
                                .define('#', bindForge("ingots/gold"))
                                .define('X', bindForge("ingots/iron"))
                                .define('Y', bindForge("storage_blocks/gold"))
                                .unlockedBy("has_gold", RegistrateRecipeProvider.has(bindForge("ingots/gold")))
                                .save(provider, newUpgrade(IronFurnaces.id("upgrade_gold")));

                        TierUpgradeRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get(), IronFurnaces.id("upgrade_iron"))
                                .pattern("###")
                                .pattern("#X#")
                                .pattern("###")
                                .define('#', bindForge("ingots/iron"))
                                .define('X', bindVanilla("stone_tool_materials"))
                                .unlockedBy("has_iron", RegistrateRecipeProvider.has(bindForge("ingots/iron")))
                                .save(provider, newUpgrade(IronFurnaces.id("upgrade_iron")));

                        TierUpgradeRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get(), IronFurnaces.id("upgrade_diamond"))
                                .pattern("###")
                                .pattern("GXG")
                                .pattern("###")
                                .define('#', bindForge("gems/diamond"))
                                .define('X', bindForge("ingots/gold"))
                                .define('G', bindForge("glass"))
                                .unlockedBy("has_diamond", RegistrateRecipeProvider.has(bindForge("gems/diamond")))
                                .save(provider, newUpgrade(IronFurnaces.id("upgrade_diamond")));

                        TierUpgradeRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get(), IronFurnaces.id("upgrade_emerald"))
                                .pattern("###")
                                .pattern("#X#")
                                .pattern("###")
                                .define('#', bindForge("gems/emerald"))
                                .define('X', bindForge("gems/diamond"))
                                .unlockedBy("has_emerald", RegistrateRecipeProvider.has(bindForge("gems/emerald")))
                                .save(provider, newUpgrade(IronFurnaces.id("upgrade_emerald")));

                        TierUpgradeRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get(), IronFurnaces.id("upgrade_obsidian"))
                                .pattern("#Y#")
                                .pattern("YXY")
                                .pattern("#Y#")
                                .define('#', bindForge("obsidian"))
                                .define('X', bindForge("gems/emerald"))
                                .define('Y', bindForge("rods/blaze"))
                                .unlockedBy("has_obsidian", RegistrateRecipeProvider.has(bindForge("obsidian")))
                                .save(provider, newUpgrade(IronFurnaces.id("upgrade_obsidian")));

                        TierUpgradeRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get(), IronFurnaces.id("upgrade_crystal"))
                                .pattern("###")
                                .pattern("#X#")
                                .pattern("###")
                                .define('#', bindForge("glass"))
                                .define('X', bindForge("gems/diamond"))
                                .unlockedBy("has_glass", RegistrateRecipeProvider.has(bindForge("glass")))
                                .save(provider, newUpgrade(IronFurnaces.id("upgrade_crystal")));

                        TierUpgradeRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get(), IronFurnaces.id("upgrade_netherite"))
                                .pattern("N#N")
                                .pattern("#X#")
                                .pattern("NSN")
                                .define('#', Items.MAGMA_CREAM)
                                .define('X', bindForge("furnaces/obsidian"))
                                .define('S', bindVanilla("soul_fire_base_blocks"))
                                .define('N', Items.NETHERITE_INGOT)
                                .unlockedBy("has_netherite", RegistrateRecipeProvider.has(Items.NETHERITE_INGOT))
                                .save(provider, newUpgrade(IronFurnaces.id("upgrade_netherite")));

                        TierUpgradeRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get(), IronFurnaces.id("upgrade_copper"))
                                .pattern("###")
                                .pattern("#X#")
                                .pattern("###")
                                .define('#', bindForge("ingots/copper"))
                                .define('X', ModItemTags.PLAYER_WORKSTATIONS_FURNACE)
                                .unlockedBy("has_copper", RegistrateRecipeProvider.has(bindForge("ingots/copper")))
                                .save(provider, newUpgrade(IronFurnaces.id("upgrade_copper")));

                        LegacyFurnaceBlocks.whenHasTags(x ->
                                        TierUpgradeRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get(), IronFurnaces.id("upgrade_silver"))
                                                .pattern("###")
                                                .pattern("#S#")
                                                .pattern("#X#")
                                                .define('#', bindForge("ingots/silver"))
                                                .define('X', bindForge("ingots/copper"))
                                                .define('S', bindVanilla("stone_tool_materials"))
                                                .unlockedBy("has_silver", RegistrateRecipeProvider.has(bindForge("ingots/silver")))
                                                .save(x, newUpgrade(IronFurnaces.id("upgrade_silver"))),
                                ctx, provider, "new_upgrades", "upgrade_silver", ModItemTags.SILVER
                        );

                        TierUpgradeRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get(), IronFurnaces.id("upgrade_obsidian2"))
                                .pattern("#Y#")
                                .pattern("YXY")
                                .pattern("#Y#")
                                .define('#', bindForge("obsidian"))
                                .define('X', bindForge("glass"))
                                .define('Y', bindForge("rods/blaze"))
                                .unlockedBy("has_obsidian", RegistrateRecipeProvider.has(bindForge("obsidian")))
                                .save(provider, newUpgrade(IronFurnaces.id("upgrade_obsidian2")));

                        TierUpgradeRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get(), IronFurnaces.id("upgrade_iron2"))
                                .pattern("###")
                                .pattern("GXG")
                                .pattern("###")
                                .define('#', bindForge("ingots/iron"))
                                .define('X', bindForge("ingots/copper"))
                                .define('G', bindForge("glass"))
                                .unlockedBy("has_diamond", RegistrateRecipeProvider.has(bindForge("gems/diamond")))
                                .save(provider, newUpgrade(IronFurnaces.id("upgrade_iron2")));

                        LegacyFurnaceBlocks.whenHasTags(x ->
                                        TierUpgradeRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get(), IronFurnaces.id("upgrade_gold2"))
                                                .pattern("###")
                                                .pattern("#X#")
                                                .pattern("#Y#")
                                                .define('#', bindForge("ingots/gold"))
                                                .define('X', bindForge("ingots/silver"))
                                                .define('Y', bindForge("storage_blocks/gold"))
                                                .unlockedBy("has_gold", RegistrateRecipeProvider.has(bindForge("ingots/gold")))
                                                .save(x, newUpgrade(IronFurnaces.id("upgrade_gold2"))),
                                ctx, provider, "new_upgrades", "upgrade_gold2", bindForge("ingots/silver")
                        );

                        LegacyFurnaceBlocks.whenHasTags(x ->
                                        TierUpgradeRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get(), IronFurnaces.id("upgrade_silver2"))
                                                .pattern("#G#")
                                                .pattern("GXG")
                                                .pattern("#G#")
                                                .define('#', bindForge("ingots/silver"))
                                                .define('X', bindForge("ingots/iron"))
                                                .define('G', bindForge("glass"))
                                                .unlockedBy("has_silver", RegistrateRecipeProvider.has(bindForge("ingots/silver")))
                                                .save(x, newUpgrade(IronFurnaces.id("upgrade_silver2"))),
                                ctx, provider, "new_upgrades", "upgrade_silver2", ModItemTags.SILVER
                        );

                        LegacyFurnaceBlocks.whenAllthemodium(x ->
                                        TierUpgradeRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get(), IronFurnaces.id("upgrade_allthemodium"))
                                                .pattern("B#B")
                                                .pattern("#X#")
                                                .pattern("B#B")
                                                .define('#', bindForge("ingots/allthemodium"))
                                                .define('B', bindForge("storage_blocks/allthemodium"))
                                                .define('X', ModItemTags.NETHERITE_UPGRADE)
                                                .unlockedBy("has_allthemodium", RegistrateRecipeProvider.has(bindForge("ingots/allthemodium")))
                                                .save(x, newUpgrade(IronFurnaces.id("upgrade_allthemodium"))),
                                ctx, "new_upgrades", "upgrade_allthemodium", provider
                        );

                        LegacyFurnaceBlocks.whenAllthemodium(x ->
                                        TierUpgradeRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get(), IronFurnaces.id("upgrade_vibranium"))
                                                .pattern("B#B")
                                                .pattern("#X#")
                                                .pattern("B#B")
                                                .define('#', bindForge("ingots/vibranium"))
                                                .define('B', bindForge("storage_blocks/vibranium"))
                                                .define('X', bindForge("ingots/allthemodium"))
                                                .unlockedBy("has_vibranium", RegistrateRecipeProvider.has(bindForge("ingots/vibranium")))
                                                .save(x, newUpgrade(IronFurnaces.id("upgrade_vibranium"))),
                                ctx, "new_upgrades", "upgrade_vibranium", provider
                        );

                        LegacyFurnaceBlocks.whenAllthemodium(x ->
                                        TierUpgradeRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get(), IronFurnaces.id("upgrade_unobtainium"))
                                                .pattern("B#B")
                                                .pattern("#X#")
                                                .pattern("B#B")
                                                .define('#', bindForge("ingots/unobtainium"))
                                                .define('B', bindForge("storage_blocks/unobtainium"))
                                                .define('X', bindForge("ingots/vibranium"))
                                                .unlockedBy("has_unobtainium", RegistrateRecipeProvider.has(bindForge("ingots/unobtainium")))
                                                .save(x, newUpgrade(IronFurnaces.id("upgrade_unobtainium"))),
                                ctx, "new_upgrades", "upgrade_unobtainium", provider
                        );
                    }).register();


    private static ResourceLocation newUpgrade(ResourceLocation path){
        return IronFurnaces.id("new_upgrades/" + path.getPath());
    }


    private static <T extends Item> ItemBuilder<T, Registrate> registerItem(
            String name,
            String langName,
            NonNullFunction<Item.Properties, T> factory
    ) {
        return core(name, factory)
                .lang(langName);
    }

    private static <T extends Item> ItemBuilder<T, Registrate> registerItem(
            String name,
            NonNullFunction<Item.Properties, T> factory
    ) {
        return core(name, factory);
    }

    private static <T extends Item> ItemBuilder<T, Registrate> core(
            String name,
            NonNullFunction<Item.Properties, T> factory
    ) {
        return REGISTRATE
                .item(name, factory);
    }

    public static void register() {
        CraftingHelper.register(RainbowEnabledCondition.Serializer.INSTANCE);
    }

}
