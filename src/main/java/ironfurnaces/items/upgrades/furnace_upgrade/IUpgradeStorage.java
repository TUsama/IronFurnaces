
package ironfurnaces.items.upgrades.furnace_upgrade;

import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.registration.ModDataComponents;
import ironfurnaces.tileentity.furnaces.pattern.FurnacePattern;
import ironfurnaces.tileentity.furnaces.pattern.upgrade.PatternUpgradeRule;
import ironfurnaces.tileentity.furnaces.pattern.upgrade.PatternUpgradeRuleManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

//? 1.20.1 {

//? } else {
import net.minecraft.world.item.component.CustomData;
import net.minecraft.core.component.DataComponents;
//?}

import javax.annotation.Nullable;

public interface IUpgradeStorage {


    @Nullable
    static PatternUpgradeRule get(ItemStack stack) {
        //? 1.20.1 {
        /*CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(PatternUpgradeRule.KEY, Tag.TAG_STRING)) {
            return null;
        }
        String s = tag.getString(PatternUpgradeRule.KEY);
        Identifier id = Identifier.tryParse(s);
        if (id == null) {
            IronFurnaces.LOGGER.warn("Invalid PatternUpgradeRule id string: {}", s);
            return null;
        }
        *///? } else {
        Identifier id = stack.get(ModDataComponents.FURNACE_UPGRADE_RULE_COMPONENT);
        //?}

        if (!PatternUpgradeRuleManager.isValidRuleId(id)) {
            //IronFurnaces.LOGGER.warn("Found unregistered rule: {}", id);
            return null;
        }

        return PatternUpgradeRuleManager.get(id);
    }

    static void writeRule(ItemStack stack, PatternUpgradeRule rule) {
        writeRule(stack, rule.id());
    }

    static void writeRule(ItemStack stack, Identifier rule) {
        if (rule == null || rule.equals(PatternUpgradeRule.backup.id())) {
            //? 1.20.1 {
            /*CompoundTag tag = stack.getTag();
            if (tag != null) {
                tag.remove(PatternUpgradeRule.KEY);
            }
            *///?}
            return;
        }
        //? 1.20.1 {
        /*CompoundTag tag = stack.getOrCreateTag();
        tag.putString(PatternUpgradeRule.KEY, rule.toString());
        *///? } else {
        stack.set(ModDataComponents.FURNACE_UPGRADE_RULE_COMPONENT, rule);
        //?}
    }

}
