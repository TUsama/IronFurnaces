package ironfurnaces.registration;

import com.tterrag.registrate.util.entry.RegistryEntry;
import ironfurnaces.init.Registration;
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

                        entries.accept(Registration.IRON_UPGRADE.get());
                        entries.accept(Registration.GOLD_UPGRADE.get());
                        entries.accept(Registration.DIAMOND_UPGRADE.get());
                        entries.accept(Registration.EMERALD_UPGRADE.get());
                        entries.accept(Registration.OBSIDIAN_UPGRADE.get());
                        entries.accept(Registration.CRYSTAL_UPGRADE.get());
                        entries.accept(Registration.NETHERITE_UPGRADE.get());
                        entries.accept(Registration.COPPER_UPGRADE.get());
                        entries.accept(Registration.SILVER_UPGRADE.get());

                        entries.accept(Registration.OBSIDIAN2_UPGRADE.get());
                        entries.accept(Registration.IRON2_UPGRADE.get());
                        entries.accept(Registration.GOLD2_UPGRADE.get());
                        entries.accept(Registration.SILVER2_UPGRADE.get());
                        entries.accept(Registration.HEATER_ITEM.get());
                        entries.accept(Registration.ITEM_HEATER.get());
                        entries.accept(Registration.BLASTING_AUGMENT.get());
                        entries.accept(Registration.SMOKING_AUGMENT.get());
                        entries.accept(Registration.FACTORY_AUGMENT.get());

                        entries.accept(Registration.GENERATOR_AUGMENT.get());
                        entries.accept(Registration.SPEED_AUGMENT.get());
                        entries.accept(Registration.FUEL_AUGMENT.get());
                        entries.accept(Registration.ITEM_SPOOKY.get());
                        entries.accept(Registration.ITEM_XMAS.get());
                        entries.accept(Registration.ITEM_COPY.get());
                        entries.accept(Registration.RAINBOW_CORE.get());
                        entries.accept(Registration.RAINBOW_PLATING.get());

                        entries.accept(LegacyFurnaceBlocks.MILLION_FURNACE.get());
                        entries.accept(Registration.RAINBOW_COAL.get());
                    })
                    .title(REGISTRATE.addRawLang("itemGroup." + IronFurnaces.MOD_ID, "Iron Furnaces"))
                    .build()
            )
            .register();

    public static void register(){

    }
}
