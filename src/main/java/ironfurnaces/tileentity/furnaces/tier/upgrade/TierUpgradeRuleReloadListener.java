package ironfurnaces.tileentity.furnaces.tier.upgrade;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import ironfurnaces.tileentity.furnaces.tier.FurnacePatternManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

import java.util.*;

public final class TierUpgradeRuleReloadListener extends SimpleJsonResourceReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().create();

    public TierUpgradeRuleReloadListener() {
        super(GSON, TierUpgradeRuleManager.DIRECTORY);
    }

    @Override
    protected void apply(
            Map<ResourceLocation, JsonElement> jsonMap,
            ResourceManager resourceManager,
            ProfilerFiller profiler
    ) {
        Map<ResourceLocation, TierUpgradeRule> loaded = new HashMap<>();

        int ok = 0;
        int failed = 0;

        for (Map.Entry<ResourceLocation, JsonElement> e : jsonMap.entrySet()) {
            ResourceLocation fileId = e.getKey();
            JsonElement json = e.getValue();

            try {
                TierUpgradeRule rule = TierUpgradeRule.CODEC
                        .parse(JsonOps.INSTANCE, json)
                        .getOrThrow(false, msg -> {
                            throw new IllegalStateException(msg);
                        });

                // 一致性校验：json 内 rule.id 必须等于文件名推导出的 id
                //（因为你打算把 id 写进物品 NBT，必须稳定且唯一）
                if (!fileId.equals(rule.id())) {
                    throw new IllegalStateException(
                            "Rule id mismatch. fileId=" + fileId + ", json.id=" + rule.id()
                    );
                }

                TierUpgradeRule prev = loaded.put(fileId, rule);
                if (prev != null) {
                    // 理论上不该发生：同 fileId 在最终资源视图里只能有一个
                    LOGGER.warn("Duplicate upgrade rule id overwritten: {}", fileId);
                }

                ok++;
            } catch (Exception ex) {
                failed++;
                LOGGER.error("Failed to load TierUpgradeRule {}: {}", fileId, ex.getMessage());
            }
        }

        TierUpgradeRuleManager.setAll(loaded);
        FurnacePatternManager.sortPatternsByRules(loaded.values());
        LOGGER.info("Loaded TierUpgradeRule(s): ok={}, failed={}, total={}", ok, failed, jsonMap.size());
    }


}
