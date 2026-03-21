package ironfurnaces.items.upgrades.furnace_upgrade;

import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.tileentity.furnaces.pattern.upgrade.PatternUpgradeRule;
import ironfurnaces.tileentity.furnaces.pattern.upgrade.PatternUpgradeRuleManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public interface IUpgradeStorage {


    @Nullable
    static PatternUpgradeRule get(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(PatternUpgradeRule.KEY, Tag.TAG_STRING)) {
            return null;
        }

        String s = tag.getString(PatternUpgradeRule.KEY);
        ResourceLocation id = ResourceLocation.tryParse(s);
        if (id == null) {
            IronFurnaces.LOGGER.warn("Invalid PatternUpgradeRule id string: {}", s);
            return null;
        }

        if (!PatternUpgradeRuleManager.isValidRuleId(id)) {
            IronFurnaces.LOGGER.warn("Found unregistered rule: {}", id);
            return null;
        }

        return PatternUpgradeRuleManager.get(id);
    }

    static void writeRule(ItemStack stack, PatternUpgradeRule rule) {
        if (rule == null) {
            CompoundTag tag = stack.getTag();
            if (tag != null) {
                tag.remove(PatternUpgradeRule.KEY);
                if (tag.isEmpty()) {
                    stack.setTag(null);
                }
            }
            return;
        }

        CompoundTag tag = stack.getOrCreateTag();
        tag.putString(PatternUpgradeRule.KEY, rule.id().toString());
    }

    static void writeRule(ItemStack stack, ResourceLocation rule) {
        if (rule == null || rule.equals(PatternUpgradeRule.backup.id())) {
            CompoundTag tag = stack.getTag();
            if (tag != null) {
                tag.remove(PatternUpgradeRule.KEY);
                if (tag.isEmpty()) {
                    stack.setTag(null);
                }
            }
            return;
        }

        CompoundTag tag = stack.getOrCreateTag();
        tag.putString(PatternUpgradeRule.KEY, rule.toString());
    }

}
