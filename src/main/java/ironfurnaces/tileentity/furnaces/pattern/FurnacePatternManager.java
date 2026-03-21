package ironfurnaces.tileentity.furnaces.pattern;

import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.tileentity.furnaces.pattern.upgrade.PatternUpgradeRule;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.*;

public final class FurnacePatternManager {

    private static final LinkedHashMap<ResourceLocation, FurnacePattern> TIERS = new LinkedHashMap<>();
    public static final String DIRECTORY = "furnace_patterns";

    private FurnacePatternManager() {}

    /**
     * datapack 加载后的入口。这里注入 id。
     */
    public static void setAllDefinitions(Map<ResourceLocation, FurnacePatternDefinition> definitions) {
        TIERS.clear();
        definitions.forEach((id, def) -> TIERS.put(id, def.toRuntime(id)));
    }

    /**
     * 保留旧入口，方便少量兼容调用。
     */
    public static void setAll(Map<ResourceLocation, FurnacePattern> map) {
        TIERS.clear();
        TIERS.putAll(map);
    }

    @Nullable
    public static FurnacePattern get(ResourceLocation id) {
        return TIERS.get(id);
    }

    public static FurnacePattern getOrFallback(ResourceLocation id) {
        return id == null ? FurnacePattern.FALLBACK : TIERS.getOrDefault(id, FurnacePattern.FALLBACK);
    }

    public static boolean contains(ResourceLocation id) {
        return TIERS.containsKey(id);
    }

    public static Collection<FurnacePattern> allPossiblePattern() {
        return Collections.unmodifiableCollection(TIERS.values());
    }

    public static Map<ResourceLocation, FurnacePattern> snapshot() {
        return Map.copyOf(TIERS);
    }


}