package ironfurnaces.tileentity.furnaces.tier.upgrade;

import ironfurnaces.loaders.IronFurnaces;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Map;

public final class TierUpgradeRuleManager {
    private TierUpgradeRuleManager() {}

    public static final String DIRECTORY = "furnace_tier_upgrades";

    private static volatile Map<ResourceLocation, TierUpgradeRule> RULES = Map.of();

    public static Map<ResourceLocation, TierUpgradeRule> snapshot() {
        return RULES;
    }

    @Nullable
    public static TierUpgradeRule get(ResourceLocation id) {
        TierUpgradeRule tierUpgradeRule = RULES.get(id);
        if (tierUpgradeRule == null) IronFurnaces.LOGGER.error("Found unavailable UpgradeRule: {}, player should consider to destroy the Upgrade Tool!", id);
        return tierUpgradeRule;
    }


    static void setAll(Map<ResourceLocation, TierUpgradeRule> newRules) {
        RULES = Map.copyOf(newRules);
    }

    public static boolean contains(ResourceLocation id) {
        return RULES.containsKey(id);
    }


    public static boolean isValidRuleId(ResourceLocation id) {
        return id != null && RULES.containsKey(id);
    }
}