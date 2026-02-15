package ironfurnaces.registration;


import net.minecraft.network.chat.Component;

import static ironfurnaces.loaders.IronFurnaces.REGISTRATE;

public class ModLangs {

    public static final Component DOWN = REGISTRATE.addRawLang("ironfurnaces.item.item_copy.setting.direction." + "down", "Down: %s");

    public static final Component UP = REGISTRATE.addRawLang("ironfurnaces.item.item_copy.setting.direction." + "up", "Up: %s");

    public static final Component NORTH = REGISTRATE.addRawLang("ironfurnaces.item.item_copy.setting.direction." + "north", "North: %s");
    public static final Component SOUTH = REGISTRATE.addRawLang("ironfurnaces.item.item_copy.setting.direction." + "south", "South: %s");
    public static final Component WEST = REGISTRATE.addRawLang("ironfurnaces.item.item_copy.setting.direction." + "west", "West: %s");
    public static final Component EAST = REGISTRATE.addRawLang("ironfurnaces.item.item_copy.setting.direction." + "east", "East: %s");
    public static final Component AUTO_INPUT = REGISTRATE.addRawLang("ironfurnaces.item.item_copy.setting." + "auto_input", "Auto Input: %s");
    public static final Component AUTO_OUTPUT = REGISTRATE.addRawLang("ironfurnaces.item.item_copy.setting." + "auto_output", "Auto Input: %s");
    public static final Component REDSTONE_MODE = REGISTRATE.addRawLang("ironfurnaces.item.item_copy.setting." + "redstone_mode", "Redstone Mode: %s");
    public static final Component REDSTONE_VALUE = REGISTRATE.addRawLang("ironfurnaces.item.item_copy.setting." + "redstone_value", "Redstone Value: %s");
    public static final Component USAGE_1 = REGISTRATE.addRawLang("ironfurnaces.item.item_copy." + "usage.1", "Right-click to copy settings");
    public static final Component USAGE_2 = REGISTRATE.addRawLang("ironfurnaces.item.item_copy." + "usage.2", "Sneak & right-click to apply settings");
    public static final Component TIP_SETTING_APPLIED = REGISTRATE.addRawLang("ironfurnaces.item.item_copy." + "tip.setting_applied", "Settings Applied");
    public static final Component TIP_SETTING_COPIED = REGISTRATE.addRawLang("ironfurnaces.item.item_copy." + "tip.setting_copied", "Settings Copied");                public static final Component FACED_DIRECTION = REGISTRATE.addRawLang("ironfurnaces.item.item_copy.setting." + "faced_direction", "Faced Direction: %s");

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
            REGISTRATE.addRawLang("tooltip.ironfurnaces.gui_fuel", "Fuel Input");

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


    public static void register(){

    }

}
