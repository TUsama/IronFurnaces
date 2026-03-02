package ironfurnaces.items.upgrades.furnace_upgrade;

import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.tileentity.furnaces.tier.upgrade.TierUpgradeRule;
import ironfurnaces.tileentity.furnaces.tier.upgrade.TierUpgradeRuleManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public interface IUpgradeStorage {

    static TierUpgradeRule get(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(TierUpgradeRule.KEY, Tag.TAG_STRING)) {
            return TierUpgradeRule.backup;
        }

        String s = tag.getString(TierUpgradeRule.KEY);
        ResourceLocation id = ResourceLocation.tryParse(s);
        if (id == null) {
            IronFurnaces.LOGGER.warn("Invalid TierUpgradeRule id string: {}", s);
            return TierUpgradeRule.backup;
        }

        if (!TierUpgradeRuleManager.isValidRuleId(id)) {
            IronFurnaces.LOGGER.warn("Found unregistered rule: {}", id);
            return TierUpgradeRule.backup;
        }

        return TierUpgradeRuleManager.get(id);
    }

    static void writeRule(ItemStack stack, TierUpgradeRule rule) {
        if (rule == null || rule == TierUpgradeRule.backup) {
            CompoundTag tag = stack.getTag();
            if (tag != null) {
                tag.remove(TierUpgradeRule.KEY);
                if (tag.isEmpty()) {
                    stack.setTag(null);
                }
            }

        }
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString(TierUpgradeRule.KEY, rule.id().toString());

    }

    static void writeRule(ItemStack stack, ResourceLocation rule) {
        if (rule == null || rule == TierUpgradeRule.backup.id()) {
            CompoundTag tag = stack.getTag();
            if (tag != null) {
                tag.remove(TierUpgradeRule.KEY);
                if (tag.isEmpty()) {
                    stack.setTag(null);
                }
            }

        }
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString(TierUpgradeRule.KEY, rule.toString());

    }

}
