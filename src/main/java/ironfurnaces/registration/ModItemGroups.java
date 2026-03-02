package ironfurnaces.registration;

import com.tterrag.registrate.util.entry.RegistryEntry;
import ironfurnaces.loaders.IronFurnaces;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;

import static ironfurnaces.loaders.IronFurnaces.REGISTRATE;

public class ModItemGroups {
    public static final RegistryEntry<CreativeModeTab> ALL_CONTENT = REGISTRATE
            .defaultCreativeTab("all_contents", builder -> builder
                    .icon(LegacyFurnaceBlocks.IRON_FURNACE::asStack)
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .displayItems((ctx, entries) -> {
                        entries.accept(LegacyFurnaceBlocks.IRON_FURNACE.get());
                        entries.accept(LegacyFurnaceBlocks.GOLD_FURNACE.get());
                        entries.accept(LegacyFurnaceBlocks.DIAMOND_FURNACE.get());
                        entries.accept(LegacyFurnaceBlocks.EMERALD_FURNACE.get());
                        entries.accept(LegacyFurnaceBlocks.OBSIDIAN_FURNACE.get());
                        entries.accept(LegacyFurnaceBlocks.CRYSTAL_FURNACE.get());
                        entries.accept(LegacyFurnaceBlocks.NETHERITE_FURNACE.get());
                        entries.accept(LegacyFurnaceBlocks.COPPER_FURNACE.get());
                        entries.accept(LegacyFurnaceBlocks.SILVER_FURNACE.get());

                        entries.accept(ModItems.IRON_UPGRADE.get());
                        entries.accept(ModItems.GOLD_UPGRADE.get());
                        entries.accept(ModItems.DIAMOND_UPGRADE.get());
                        entries.accept(ModItems.EMERALD_UPGRADE.get());
                        entries.accept(ModItems.OBSIDIAN_UPGRADE.get());
                        entries.accept(ModItems.CRYSTAL_UPGRADE.get());
                        entries.accept(ModItems.NETHERITE_UPGRADE.get());
                        entries.accept(ModItems.COPPER_UPGRADE.get());
                        entries.accept(ModItems.SILVER_UPGRADE.get());

                        entries.accept(ModItems.OBSIDIAN2_UPGRADE.get());
                        entries.accept(ModItems.IRON2_UPGRADE.get());
                        entries.accept(ModItems.GOLD2_UPGRADE.get());
                        entries.accept(ModItems.SILVER2_UPGRADE.get());
                        entries.accept(ModItems.ITEM_HEATER.get());
                        entries.accept(ModItems.BLASTING_AUGMENT.get());
                        entries.accept(ModItems.SMOKING_AUGMENT.get());
                        entries.accept(ModItems.FACTORY_AUGMENT.get());

                        entries.accept(ModItems.GENERATOR_AUGMENT.get());
                        entries.accept(ModItems.SPEED_AUGMENT.get());
                        entries.accept(ModItems.FUEL_AUGMENT.get());
                        entries.accept(ModItems.ITEM_SPOOKY.get());
                        entries.accept(ModItems.ITEM_XMAS.get());
                        entries.accept(ModItems.ITEM_COPY_V2.get());
                        entries.accept(ModItems.RAINBOW_CORE.get());
                        entries.accept(ModItems.RAINBOW_PLATING.get());

                        entries.accept(LegacyFurnaceBlocks.MILLION_FURNACE.get());
                        entries.accept(ModItems.RAINBOW_COAL.get());
                    })
                    .title(REGISTRATE.addRawLang("itemGroup." + IronFurnaces.MOD_ID, "Iron Furnaces"))
                    .build()
            )
            .register();

    public static void register(){

    }
}
