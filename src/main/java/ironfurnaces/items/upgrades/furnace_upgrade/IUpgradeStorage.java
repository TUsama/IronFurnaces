
package ironfurnaces.items.upgrades.furnace_upgrade;

import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.registration.ModDataComponents;
import ironfurnaces.registration.data_component.UpgradeRuleHolder;
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
import java.util.Optional;

public interface IUpgradeStorage {


    @Nullable
    static PatternUpgradeRule get(ItemStack stack) {

        var upgradeRuleHolder = stack.get(ModDataComponents.UPGRADE_RULE_HOLDER);

        if (upgradeRuleHolder != null && upgradeRuleHolder.upgradeRule().isPresent() && PatternUpgradeRuleManager.isValidRuleId(upgradeRuleHolder.upgradeRule().get())) {
            return PatternUpgradeRuleManager.get(upgradeRuleHolder.upgradeRule().get());
        }

        return null;
    }

    static void writeRule(ItemStack stack, PatternUpgradeRule rule) {
        writeRule(stack, rule.id());
    }

    static void writeRule(ItemStack stack, Identifier rule) {
        if (rule == null || rule.equals(PatternUpgradeRule.backup.id())) {
            stack.set(ModDataComponents.UPGRADE_RULE_HOLDER, new UpgradeRuleHolder(Optional.empty()));
        } else {
            stack.set(ModDataComponents.UPGRADE_RULE_HOLDER, new UpgradeRuleHolder(Optional.of(rule)));
        }


    }

}
