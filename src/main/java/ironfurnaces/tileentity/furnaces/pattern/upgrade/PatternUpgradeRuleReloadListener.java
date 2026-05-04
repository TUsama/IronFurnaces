
package ironfurnaces.tileentity.furnaces.pattern.upgrade;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import ironfurnaces.tileentity.furnaces.pattern.FurnacePatternManager;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

import java.util.*;
//~ if > 1.21.11 'SimpleJsonResourceReloadListener' -> 'SimpleJsonResourceReloadListener<JsonElement>'
public final class PatternUpgradeRuleReloadListener extends SimpleJsonResourceReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().create();

    public PatternUpgradeRuleReloadListener() {
        //~ if > 1.21.11 'GSON, PatternUpgradeRuleManager.DIRECTORY' -> 'ExtraCodecs.JSON, FileToIdConverter.json(PatternUpgradeRuleManager.DIRECTORY)'
        super(GSON, PatternUpgradeRuleManager.DIRECTORY);
    }

    @Override
    protected void apply(
            Map<ResourceLocation, JsonElement> jsonMap,
            ResourceManager resourceManager,
            ProfilerFiller profiler
    ) {
        Map<ResourceLocation, PatternUpgradeRule> loaded = new HashMap<>();

        int ok = 0;
        int failed = 0;

        for (Map.Entry<ResourceLocation, JsonElement> e : jsonMap.entrySet()) {
            ResourceLocation fileId = e.getKey();
            JsonElement json = e.getValue();

            try {
                PatternUpgradeRule rule = PatternUpgradeRuleDefinition.CODEC
                        .parse(JsonOps.INSTANCE, json)
                        .getOrThrow(
                                //? 1.20.1
                                //false,
                                msg -> {
                            throw new IllegalStateException(msg);
                        }).toRuntime(e.getKey());
                loaded.put(fileId, rule);

                ok++;
            } catch (Exception ex) {
                failed++;
                LOGGER.error("Failed to load TierUpgradeRule {}: {}", fileId, ex.getMessage());
            }
        }

        PatternUpgradeRuleManager.setAll(loaded);
        LOGGER.info("Loaded TierUpgradeRule(s): ok={}, failed={}, total={}", ok, failed, jsonMap.size());
    }


}
