
package ironfurnaces.tileentity.furnaces.pattern.upgrade;

import ironfurnaces.loaders.IronFurnaces;
import net.minecraft.resources.Identifier;

import javax.annotation.Nullable;
import java.util.Map;

public final class PatternUpgradeRuleManager {
    private PatternUpgradeRuleManager() {}

    public static final String DIRECTORY = "furnace_pattern_upgrades";

    private static volatile Map<Identifier, PatternUpgradeRule> RULES = Map.of();

    public static Map<Identifier, PatternUpgradeRule> snapshot() {
        return RULES;
    }

    @Nullable
    public static PatternUpgradeRule get(Identifier id) {
        PatternUpgradeRule patternUpgradeRule = RULES.get(id);
        if (patternUpgradeRule == null) IronFurnaces.LOGGER.error("Found unavailable UpgradeRule: {}, player should consider to destroy the Upgrade Tool!", id);
        return patternUpgradeRule;
    }


    static void setAll(Map<Identifier, PatternUpgradeRule> newRules) {
        RULES = Map.copyOf(newRules);
    }

    public static boolean contains(Identifier id) {
        return RULES.containsKey(id);
    }


    public static boolean isValidRuleId(Identifier id) {
        return id != null && RULES.containsKey(id);
    }
}