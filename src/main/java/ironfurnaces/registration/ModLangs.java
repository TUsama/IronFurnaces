package ironfurnaces.registration;


import manifold.rt.api.DisableStringLiteralTemplates;
import net.minecraft.network.chat.Component;

import static ironfurnaces.loaders.IronFurnaces.REGISTRATE;

@DisableStringLiteralTemplates
public class ModLangs {
    public static final Component PERSISTENT_ENERGY = REGISTRATE.addRawLang("ironfurnaces.data_component." + "persistent_energy", "Energy: %1$s/%2$s");

    public static final Component CURRENT_LOCKED_RECIPE = REGISTRATE.addRawLang("screen.ironfurnaces.compat.farmer_delight." + "current_locked_recipe", "Current locked to recipe: %s");
    public static final Component NO_CURRENT_LOCKED_RECIPE = REGISTRATE.addRawLang("screen.ironfurnaces.compat.farmer_delight." + "no_current_locked_recipe", "No recipe locked");
    public static final Component LAST_RECIPE = REGISTRATE.addRawLang("screen.ironfurnaces.compat.farmer_delight." + "last_recipe", "The last recipe crafted is: %s");
    public static final Component CLICK_TO_LOCK = REGISTRATE.addRawLang("screen.ironfurnaces.compat.farmer_delight." + "click_to_lock", "Click to lock to the last crafted recipe");
    public static final Component NO_LAST_RECIPE = REGISTRATE.addRawLang("screen.ironfurnaces.compat.farmer_delight." + "no_last_recipe", "No last recipe can be locked");
    public static final Component TRANSFER_REMINDER = REGISTRATE.addRawLang("container.ironfurnaces.item_transfer." + "transfer_reminder", "The %1$s at %2$s has transferred items to your inventory due to the modification: ");
    public static final Component RAINBOW_FURNACE = REGISTRATE.addRawLang("block.ironfurnaces.rainbow_furnace", "Rainbow Furnace");
    public static final Component RESET_SETTING_HINT = REGISTRATE.addRawLang("item.ironfurnaces.furnace_pattern_holder." + "reset_setting_hint", "Sneak + shift will reset the Furnace setting.");
    public static final Component RESET_SETTING_FAILED = REGISTRATE.addRawLang("item.ironfurnaces.pattern_holder_item." + "reset_setting_failed", "Failed to reset settings!");
    public static final Component RESET_SETTING_SUCCESS = REGISTRATE.addRawLang("item.ironfurnaces.pattern_holder_item." + "reset_setting_success", "Reset Setting Successfully!");
    public static final Component SETTING = REGISTRATE.addRawLang("screen.ironfurnaces.side_tab." + "setting", "Setting");
    public static final Component REMAINING_ITEMS = REGISTRATE.addRawLang("screen.ironfurnaces.side_tab." + "remaining_items", "Remaining Cache");
    public static final Component AUGMENT = REGISTRATE.addRawLang("screen.ironfurnaces.side_tab." + "augment", "Augments");
    public static final Component WITHOUT_PATTERN = REGISTRATE.addRawLang("block.ironfurnaces.furnace_pattern_holder." + "without_pattern", "This furnace block haven't attach any patterns!");
    public static final Component REFUSE_OPEN_EMPTY_PATTERN = REGISTRATE.addRawLang("block.ironfurnaces.furnace_pattern_holder." + "refuse_open_empty_pattern", "Can't open the menu, because the furnace haven't attach any patterns!");
    public static final Component REFUSE_PLACE_EMPTY_TAG = REGISTRATE.addRawLang("item.ironfurnaces.furnace_pattern_holder." + "refuse_place_empty_tag", "You can't place down this furnace because this item haven't attach any pattern tags!");
    public static final Component READ_PATTERN_FAILED = REGISTRATE.addRawLang("item.ironfurnaces.pattern_holder_item." + "read_pattern_failed", "Failed to reading furnace pattern from furnace item!");

    public static final Component FRONT = REGISTRATE.addRawLang("ironfurnaces.furnace_setting.relative_face.front", "Front: %s");
    public static final Component BACK = REGISTRATE.addRawLang("ironfurnaces.furnace_setting.relative_face.back", "Back: %s");
    public static final Component LEFT = REGISTRATE.addRawLang("ironfurnaces.furnace_setting.relative_face.left", "Left: %s");
    public static final Component RIGHT = REGISTRATE.addRawLang("ironfurnaces.furnace_setting.relative_face.right", "Right: %s");
    public static final Component RELATIVE_UP = REGISTRATE.addRawLang("ironfurnaces.furnace_setting.relative_face.up", "Up: %s");
    public static final Component RELATIVE_DOWN = REGISTRATE.addRawLang("ironfurnaces.furnace_setting.relative_face.down", "Down: %s");
    public static final Component DOWN = REGISTRATE.addRawLang("ironfurnaces.furnace_setting.direction." + "down", "Down: %s");

    public static final Component UP = REGISTRATE.addRawLang("ironfurnaces.furnace_setting.direction." + "up", "Up: %s");

    public static final Component NORTH = REGISTRATE.addRawLang("ironfurnaces.furnace_setting.direction." + "north", "North: %s");
    public static final Component SOUTH = REGISTRATE.addRawLang("ironfurnaces.furnace_setting.direction." + "south", "South: %s");
    public static final Component WEST = REGISTRATE.addRawLang("ironfurnaces.furnace_setting.direction." + "west", "West: %s");
    public static final Component EAST = REGISTRATE.addRawLang("ironfurnaces.furnace_setting.direction." + "east", "East: %s");
    public static final Component AUTO_INPUT = REGISTRATE.addRawLang("ironfurnaces.furnace_setting." + "auto_input", "Auto Input: %s");
    public static final Component AUTO_OUTPUT = REGISTRATE.addRawLang("ironfurnaces.furnace_setting." + "auto_output", "Auto Output: %s");
    public static final Component REDSTONE_MODE = REGISTRATE.addRawLang("ironfurnaces.furnace_setting." + "redstone_mode", "Redstone Mode: %s");
    public static final Component REDSTONE_VALUE = REGISTRATE.addRawLang("ironfurnaces.furnace_setting." + "redstone_value", "Subtraction Value: %s");
    public static final Component USAGE_1 = REGISTRATE.addRawLang("ironfurnaces.item.item_copy." + "usage.1", "Right-click to copy settings");
    public static final Component USAGE_2 = REGISTRATE.addRawLang("ironfurnaces.item.item_copy." + "usage.2", "Sneak & right-click to apply settings");
    public static final Component NEW_USAGE_1 = REGISTRATE.addRawLang("ironfurnaces.item.new_item_copy." + "usage.1", "Sneak & right-click to copy settings");
    public static final Component NEW_USAGE_2 = REGISTRATE.addRawLang("ironfurnaces.item.new_item_copy." + "usage.2", "Right-click to apply settings");
    public static final Component TIP_SETTING_APPLIED = REGISTRATE.addRawLang("ironfurnaces.item.item_copy." + "tip.setting_applied", "Settings Applied");
    public static final Component TIP_SETTING_COPIED = REGISTRATE.addRawLang("ironfurnaces.item.item_copy." + "tip.setting_copied", "Settings Copied");
    public static final Component FACED_DIRECTION = REGISTRATE.addRawLang("ironfurnaces.furnace_setting." + "faced_direction", "Faced Direction: %s");

    public static final Component ERROR_ON_PARSE = REGISTRATE.addRawLang("item.ironfurnaces.item_copy." + "error_on_parse", "Failed to parsing furnace setting on Copy Tool!");
    public static final Component ERROR_ON_WRITE = REGISTRATE.addRawLang("item.ironfurnaces.item_copy." + "error_on_write", "Failed to writing furnace setting on Copy Tool! Please consider destroy the furnace you are coping and place it down again!");
    public static final Component LEGACY = REGISTRATE.addRawLang("item.ironfurnaces.furnace." + "legacy", "Legacy");
    public static final Component IO_MODE_NONE =
            REGISTRATE.addRawLang(
                    "ironfurnaces.furnace_setting.io_mode.none",
                    "None"
            );

    public static final Component IO_MODE_INPUT =
            REGISTRATE.addRawLang(
                    "ironfurnaces.furnace_setting.io_mode.input",
                    "Input"
            );

    public static final Component IO_MODE_OUTPUT =
            REGISTRATE.addRawLang(
                    "ironfurnaces.furnace_setting.io_mode.output",
                    "Output"
            );

    public static final Component IO_MODE_INPUT_AND_OUTPUT =
            REGISTRATE.addRawLang(
                    "ironfurnaces.furnace_setting.io_mode.input_and_output",
                    "Input And Output"
            );

    public static final Component IO_MODE_FUEL =
            REGISTRATE.addRawLang(
                    "ironfurnaces.furnace_setting.io_mode.fuel",
                    "Fuel"
            );

    public static final Component IO_MODE_ALL =
            REGISTRATE.addRawLang(
                    "ironfurnaces.furnace_setting.io_mode.all",
                    "All"
            );

    public static final Component REDSTONE_MODE_IGNORE =
            REGISTRATE.addRawLang(
                    "ironfurnaces.furnace_setting.redstone_mode.ignore",
                    "Ignore"
            );

    public static final Component REDSTONE_MODE_HIGH_SIGNAL =
            REGISTRATE.addRawLang(
                    "ironfurnaces.furnace_setting.redstone_mode.high_signal",
                    "High Signal"
            );

    public static final Component REDSTONE_MODE_LOW_SIGNAL =
            REGISTRATE.addRawLang(
                    "ironfurnaces.furnace_setting.redstone_mode.low_signal",
                    "Low Signal"
            );

    public static final Component REDSTONE_MODE_COMPARATOR =
            REGISTRATE.addRawLang(
                    "ironfurnaces.furnace_setting.redstone_mode.comparator",
                    "Comparator"
            );

    public static final Component REDSTONE_MODE_COMPARATOR_SUBTRACTION =
            REGISTRATE.addRawLang(
                    "ironfurnaces.furnace_setting.redstone_mode.comparator_subtraction",
                    "Comparator Subtraction"
            );

    public static final Component INVALIDED_UPGRADE_TOOL = REGISTRATE.addRawLang("item.ironfurnaces.upgrade_tool." + "invalided_upgrade_tool", "Found Invalided Upgrade Tool!");

    public static final Component MISMATCH_PATTERN = REGISTRATE.addRawLang("item.ironfurnaces.upgrade_tool." + "mismatch_pattern", "This upgrade tool can only be used to %1$s, but the furnace you targeted is %2$s.");
    public static final Component BROKEN_UPGRADE_TOOL = REGISTRATE.addRawLang("item.ironfurnaces.upgrade_tool." + "broken_upgrade_tool", "Broken");

    public static final Component UPGRADE_RULE = REGISTRATE.addRawLang("item.ironfurnaces.upgrade_tool." + "upgrade_rule", "%1$s -> %2$s");

    public static final Component SUCCESS = REGISTRATE.addRawLang("item.ironfurnaces.upgrade_tool." + "success", "Upgrade successfully!");

    public static final Component READ_SETTING_FAILED = REGISTRATE.addRawLang("item.ironfurnaces.pattern_holder_item." + "read_setting_failed", "Failed to reading furnace setting from furnace item!");

    public static final Component SETTING_ENABLE = REGISTRATE.addRawLang("ironfurnaces.furnace_setting." + "setting_enable", "Enable");
    public static final Component SETTING_DISABLE = REGISTRATE.addRawLang("ironfurnaces.furnace_setting." + "setting_disable", "Disable");
    public static final Component WORK_SPEED = REGISTRATE.addRawLang("ironfurnaces.block.furnace." + "work_speed", "Base Cook Time: %s tick/per item");


    public static final Component UPGRADE_RIGHT_CLICK =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.upgrade_right_click", "Sneak & right-click to upgrade");

    public static final Component XMAS_RIGHT_CLICK =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.xmas_right_click", "Right-click on furnace to wrap it as a gift!");

    public static final Component XMAS_1 =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.xmas1", "But won't the wrappings catch fire?");

    public static final Component XMAS_2 =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.xmas2", "Sneaking and right-clicking on a furnace opens the gift");

    public static final Component SPOOKY_RIGHT_CLICK =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.spooky_right_click", "Right-click on furnace to spookalize");

    public static final Component SPOOKY_1 =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.spooky1", "3spooky5me");

    public static final Component SPOOKY_2 =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.spooky2", "Sneaking and right-clicking on a furnace will cleanse any spookalizations");

    public static final Component HEATER_BOUND =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.heater", "Bound to: ");

    public static final Component HEATER_X =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.heaterX", "X: ");

    public static final Component HEATER_Y =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.heaterY", "Y: ");

    public static final Component HEATER_Z =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.heaterZ", "Z: ");

    public static final Component HEATER_NOT_BOUND =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.heater_not_bound", "Has yet to be bound to an energy source!");

    public static final Component HEATER_TIP =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.heater_tip", "Place in the fuel slot of the furnace");

    public static final Component HEATER_TIP_1 =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.heater_tip1", "Only works from Iron tier and above");

    public static final Component AUGMENT_RIGHT_CLICK =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.augment_right_click", "Right-click to insert augment");

    public static final Component AUGMENT_SPEED_PRO =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.augment_speed_pro", "+Halves the cooktime for all recipes.");

    public static final Component AUGMENT_SPEED_CON =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.augment_speed_con", "-Uses up twice the amount of fuel.");

    public static final Component AUGMENT_SMOKING =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.augment_smoking", "Only allows for smoking recipes.");

    public static final Component AUGMENT_BLASTING =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.augment_blasting", "Only allows for blasting recipes.");

    public static final Component AUGMENT_XP =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.augment_xp", "Experience granted from smelting will be ejected to nearby fluid tanks.");

    public static final Component AUGMENT_XP_1 =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.augment_xp_1", "Experience granted from smelting will given to the player that placed the furnace.");

    public static final Component AUGMENT_XP_SWITCH =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.augment_xp_switch", "Sneak & Right click in the air to switch modes.");

    public static final Component AUGMENT_FUEL_PRO =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.augment_fuel_pro", "+Fuel heats up the furnace twice as much.");

    public static final Component AUGMENT_FUEL_CON =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.augment_fuel_con", "-Slows down the cooktime by 25% for all recipes.");

    public static final Component AUGMENT_FACTORY_PRO =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.augment_factory_pro", "+Converts the furnace into a factory.");

    public static final Component AUGMENT_FACTORY_CON =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.augment_factory_con", "-Uses power for heat instead of fuel.");

    public static final Component AUGMENT_GENERATOR_PRO =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.augment_generator_pro", "+Heat from the furnace will generate power.");

    public static final Component AUGMENT_GENERATOR_CON =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.augment_generator_con", "-The furnace can no longer smelt items.");

    public static final Component GUI_PRO =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.gui_pro", "+Adds configurable sides for item transfer");

    public static final Component HEATER_BLOCK =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.heater_block", "Link with a Wireless Heat Receiver (done by placing the receiver in the Wireless Heat Transmitter)");

    public static final Component HEATER_BLOCK_1 =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.heater_block1", "Provide this block with Forge Energy and it will power all Wireless Heat Receivers that are bound");

    public static final Component AUGMENT_REDSTONE_RIGHT_CLICK =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.augment_redstone_right_click", "Right-click in the air to change mode");

    public static final Component AUGMENT_REDSTONE_CONTROL =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.augment_redstone_control", "Redstone Control mode: will NOT smelt when supplied with a redstone signal");

    public static final Component AUGMENT_REDSTONE_CONTROL_INVERSE =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.augment_redstone_control_inverse", "Redstone Inverse Control mode: will ONLY smelt when supplied with a redstone signal");

    public static final Component AUGMENT_REDSTONE_COMPARATOR =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.augment_redstone_comparator", "Comparator mode: acts as an internal comparator, outputs the signal to all sides");

    public static final Component AUGMENT_REDSTONE_COMPARATOR_SUBTRACT =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.augment_redstone_comparator_subtract", "Comparator Subtraction mode: same as Comparator mode but you can subtract with a signal strength that can be configured in the furnace GUI");

    public static final Component AUGMENT_REDSTONE_MSG_MODE0 =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.augment_redstone_msg_mode0", "Redstone Control Mode");

    public static final Component AUGMENT_REDSTONE_MSG_MODE1 =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.augment_redstone_msg_mode1", "Redstone Inverse Control Mode");

    public static final Component AUGMENT_REDSTONE_MSG_MODE2 =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.augment_redstone_msg_mode2", "Comparator Mode");

    public static final Component AUGMENT_REDSTONE_MSG_MODE3 =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.augment_redstone_msg_mode3", "Comparator Subtraction Mode");

    public static final Component GUI_OPEN_AUGMENTS =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.gui_open_augments", "Augments");

    public static final Component GUI_OPEN_FURNACE =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.gui_open_furnace", "Furnace");

    public static final Component GUI_OPEN =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.gui_open", "Open Config");

    public static final Component GUI_CLOSE =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.gui_close", "Close Config");

    public static final Component GUI_AUTO_INPUT =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.gui_auto_input", "Auto Input");

    public static final Component GUI_AUTO_OUTPUT =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.gui_auto_output", "Auto Output");

    public static final Component GUI_TOP =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.gui_top", "Top");

    public static final Component GUI_BOTTOM =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.gui_bottom", "Bottom");

    public static final Component GUI_FRONT =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.gui_front", "Front");

    public static final Component GUI_BACK =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.gui_back", "Back");

    public static final Component GUI_LEFT =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.gui_left", "Left");

    public static final Component GUI_RIGHT =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.gui_right", "Right");

    public static final Component GUI_NONE =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.gui_none", "NONE");

    public static final Component GUI_INPUT =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.gui_input", "Input");

    public static final Component GUI_OUTPUT =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.gui_output", "Output");

    public static final Component GUI_INPUT_OUTPUT =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.gui_input_output", "Input/Output");

    public static final Component GUI_FUEL =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.gui_fuel", "Fuel Input/Eject");

    public static final Component GUI_RESET =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.gui_reset", "Reset All");

    public static final Component GUI_REDSTONE_IGNORED =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.gui_redstone_ignored", "Redstone Ignored");

    public static final Component GUI_REDSTONE_LOW =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.gui_redstone_low", "Redstone Low");

    public static final Component GUI_REDSTONE_HIGH =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.gui_redstone_high", "Redstone High");

    public static final Component GUI_REDSTONE_COMPARATOR =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.gui_redstone_comparator", "Comparator");

    public static final Component GUI_REDSTONE_COMPARATOR_SUB =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.gui_redstone_comparator_sub", "Comparator with Subtraction");

    public static final Component GUI_HOLD_SHIFT =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.gui_hold_shift", "Hold ");

    public static final Component GUI_SHIFT_MORE_OPTIONS =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.gui_shift_more_options", " for more options");

    public static final Component HOLD =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.hold", "Hold ");

    public static final Component FOR_DETAILS =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.for_details", " for Details.");

    public static final Component AUGMENT_SLOT =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.augment_slot", "Augment Slot");

    public static final Component RAINBOW_GEN_1 =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.rainbow_gen1", "Will generate");

    public static final Component RAINBOW_GEN_2 =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.rainbow_gen2", "RF/tick if all other furnaces are generating power");

    public static final Component RAINBOW_GEN_3 =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.rainbow_gen3", "For power generation, one of each tier of furnace must be an active generator and not have a full buffer");

    public static final Component RAINBOW_GEN_4 =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.rainbow_gen4", "Outputs energy to any side that is configured as output, it wont add energy to the internal buffer");

    public static final Component RAINBOW_GEN_5 =
            REGISTRATE.addRawLang("tooltip.ironfurnaces.rainbow_gen5", "You can only have one Rainbow Generator");
    public static final Component UPDATE_SPEECH =
            REGISTRATE.addRawLang("ironfurnaces.update.speech",
                    "[{\"text\":\"There is an Update for \"},{\"text\":\"Iron Furnaces \",\"color\":\"dark_green\"},{\"text\":\"available!\",\"color\":\"none\"}]");

    public static final Component UPDATE_VERSION =
            REGISTRATE.addRawLang("ironfurnaces.update.version",
                    "[{\"text\":\"Current Version: \"},{\"text\":\"%s\",\"color\":\"dark_red\"},{\"text\":\", newest Version: \",\"color\":\"none\"},{\"text\":\"%s\",\"color\":\"dark_green\"}]");

    public static final Component UPDATE_BUTTONS =
            REGISTRATE.addRawLang("ironfurnaces.update.buttons",
                    "[{\"text\":\"[\"},{\"text\":\"Click for Changelog\",\"color\":\"green\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"%s\"}},{\"text\":\"] [\",\"color\":\"none\"},{\"text\":\"Click for Download\",\"color\":\"green\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"%s\"}},{\"text\":\"]\",\"color\":\"none\"}]");

    public static final Component UPDATE_BUTTON_OPTIONS =
            REGISTRATE.addRawLang("ironfurnaces.update.buttonOptions",
                    "Click: Changelog, Shift-Click: Download! (In Browser)");

    public static final Component UPDATE_FAILED =
            REGISTRATE.addRawLang("ironfurnaces.update.failed",
                    "[{\"text\":\"The Update Check for \"},{\"text\":\"Iron Furnaces \",\"color\":\"dark_green\"},{\"text\":\"failed! Check Logs for more Info!\",\"color\":\"none\"}]");


    public static final Component AUTO_SPLIT = REGISTRATE.addRawLang("ironfurnaces.furnace_setting." + "auto_fill", "Auto Fill Empty Slot: %s");

    public static final Component ENERGY_SLOT = REGISTRATE.addRawLang("screen.ironfurnaces." + "energy_slot", "Energy: %1$s/%2$s");

    public static final Component RAINBOW_LIMIT_REACHED = REGISTRATE.addRawLang("item.ironfurnaces.upgrade_tool." + "rainbow_limit_reached", "You have reached the maximum number of Rainbow Furnaces: %s");

    public static void register() {

    }

}
