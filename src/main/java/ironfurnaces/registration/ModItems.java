package ironfurnaces.registration;

import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import ironfurnaces.items.*;
import ironfurnaces.items.augments.*;
import ironfurnaces.items.upgrades.*;
import net.minecraft.world.item.Item;

import static ironfurnaces.loaders.IronFurnaces.REGISTRATE;

public class ModItems {


    public static final ItemEntry<ItemUpgradeIron> IRON_UPGRADE =
            registerItem("upgrade_iron", "Upgrade: Stone -> Iron", ItemUpgradeIron::new);

    public static final ItemEntry<ItemUpgradeGold> GOLD_UPGRADE =
            registerItem("upgrade_gold", "Upgrade: Iron -> Gold", ItemUpgradeGold::new);

    public static final ItemEntry<ItemUpgradeDiamond> DIAMOND_UPGRADE =
            registerItem("upgrade_diamond", "Upgrade: Gold -> Diamond",ItemUpgradeDiamond::new);

    public static final ItemEntry<ItemUpgradeEmerald> EMERALD_UPGRADE =
            registerItem("upgrade_emerald", "Upgrade: Diamond -> Emerald",ItemUpgradeEmerald::new);

    public static final ItemEntry<ItemUpgradeObsidian> OBSIDIAN_UPGRADE =
            registerItem("upgrade_obsidian", "Upgrade: Emerald -> Obsidian",ItemUpgradeObsidian::new);

    public static final ItemEntry<ItemUpgradeCrystal> CRYSTAL_UPGRADE =
            registerItem("upgrade_crystal", "Upgrade: Diamond -> Crystal",ItemUpgradeCrystal::new);

    public static final ItemEntry<ItemUpgradeNetherite> NETHERITE_UPGRADE =
            registerItem("upgrade_netherite", "Upgrade: Obsidian -> Netherite",ItemUpgradeNetherite::new);

    public static final ItemEntry<ItemUpgradeCopper> COPPER_UPGRADE =
            registerItem("upgrade_copper", "Upgrade: Stone -> Copper",ItemUpgradeCopper::new);

    public static final ItemEntry<ItemUpgradeSilver> SILVER_UPGRADE =
            registerItem("upgrade_silver", "Upgrade: Copper -> Silver",ItemUpgradeSilver::new);

    public static final ItemEntry<ItemUpgradeObsidian2> OBSIDIAN2_UPGRADE =
            registerItem("upgrade_obsidian2", "Upgrade: Crystal -> Obsidian",ItemUpgradeObsidian2::new);

    public static final ItemEntry<ItemUpgradeIron2> IRON2_UPGRADE =
            registerItem("upgrade_iron2", "Upgrade: Copper -> Iron",ItemUpgradeIron2::new);

    public static final ItemEntry<ItemUpgradeGold2> GOLD2_UPGRADE =
            registerItem("upgrade_gold2", "Upgrade: Silver -> Gold",ItemUpgradeGold2::new);

    public static final ItemEntry<ItemUpgradeSilver2> SILVER2_UPGRADE =
            registerItem("upgrade_silver2", "Upgrade: Iron -> Silver",ItemUpgradeSilver2::new);

    public static final ItemEntry<ItemUpgradeAllthemodium> ALLTHEMODIUM_UPGRADE =
            registerItem("upgrade_allthemodium", "Upgrade: Netherite -> Allthemodium",
                    ItemUpgradeAllthemodium::new);

    public static final ItemEntry<ItemUpgradeVibranium> VIBRANIUM_UPGRADE =
            registerItem("upgrade_vibranium","Upgrade: Allthemodium -> Vibranium",
                    ItemUpgradeVibranium::new);

    public static final ItemEntry<ItemUpgradeUnobtainium> UNOBTAINIUM_UPGRADE =
            registerItem("upgrade_unobtainium","Upgrade: Vibranium -> Unobtainium",
                    ItemUpgradeUnobtainium::new);



    public static final ItemEntry<ItemHeater> ITEM_HEATER =
            registerItem("item_heater", p -> new ItemHeater(p.stacksTo(1)));

    public static final ItemEntry<ItemAugmentBlasting> BLASTING_AUGMENT =
            registerItem("augment_blasting", "Augment: Blasting", ItemAugmentBlasting::new);

    public static final ItemEntry<ItemAugmentSmoking> SMOKING_AUGMENT =
            registerItem("augment_smoking", "Augment: Smoking", ItemAugmentSmoking::new);

    public static final ItemEntry<ItemAugmentFactory> FACTORY_AUGMENT =
            registerItem("augment_factory", "Augment: Factory", ItemAugmentFactory::new);

    public static final ItemEntry<ItemAugmentGenerator> GENERATOR_AUGMENT =
            registerItem("augment_generator", "Augment: Generator", ItemAugmentGenerator::new);

    public static final ItemEntry<ItemAugmentSpeed> SPEED_AUGMENT =
            registerItem("augment_speed", "Augment: Speed", ItemAugmentSpeed::new);

    public static final ItemEntry<ItemAugmentFuel> FUEL_AUGMENT =
            registerItem("augment_fuel", "Augment: Fuel Efficiency", ItemAugmentFuel::new);

    public static final ItemEntry<ItemSpooky> ITEM_SPOOKY =
            registerItem("item_spooky", ItemSpooky::new);

    public static final ItemEntry<ItemXmas> ITEM_XMAS =
            registerItem("item_xmas", ItemXmas::new);

    public static final ItemEntry<ItemFurnaceCopy> ITEM_COPY =
            registerItem("item_copy", p -> new ItemFurnaceCopy(p.stacksTo(1)));

    public static final ItemEntry<Item> RAINBOW_CORE =
            registerItem("rainbow_core", Item::new);

    public static final ItemEntry<Item> RAINBOW_PLATING =
            registerItem("rainbow_plating", Item::new);

    public static final ItemEntry<ItemRainbowCoal> RAINBOW_COAL =
            registerItem("rainbow_coal", ItemRainbowCoal::new);



    private static <T extends Item> ItemEntry<T> registerItem(
            String name,
            String langName,
            NonNullFunction<Item.Properties, T> factory
    ) {
        return core(name, factory)
                .lang(langName)
                .register();
    }

    private static <T extends Item> ItemEntry<T> registerItem(
            String name,
            NonNullFunction<Item.Properties, T> factory
    ) {
        return core(name, factory)
                .register();
    }

    private static <T extends Item> ItemBuilder<T, Registrate> core(
            String name,
            NonNullFunction<Item.Properties, T> factory
    ) {
        return REGISTRATE
                .item(name, factory);
    }

    public static void register(){

    }
}
