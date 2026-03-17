package ironfurnaces.tileentity.furnaces.pattern;

import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.tileentity.furnaces.pattern.upgrade.PatternUpgradeRule;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.*;

public final class FurnacePatternManager {

    private static final LinkedHashMap<ResourceLocation, FurnacePattern> TIERS = new LinkedHashMap<>();
    private static final LinkedHashMap<FurnacePattern, Integer> ORDER = new LinkedHashMap<>();
    public static final String DIRECTORY = "furnace_patterns";

    public static void setAll(Map<ResourceLocation, FurnacePattern> map) {
        TIERS.clear();
        TIERS.putAll(map);
    }

    @Nullable
    public static FurnacePattern get(ResourceLocation id) {
        FurnacePattern furnacePattern = TIERS.get(id);
        if (furnacePattern == null) IronFurnaces.LOGGER.error("Found unavailable Pattern: {}", id);
        return furnacePattern;
    }

    public static boolean contains(ResourceLocation id) {
        return TIERS.containsKey(id);
    }

    public static int compare(FurnacePattern tier1, FurnacePattern tier2) {
        return ORDER.get(tier1).compareTo(ORDER.get(tier2));
    }

    public static Collection<FurnacePattern> allPossiblePattern(){
        return TIERS.values();
    }

    public static void fillFakePatternForBlockStateValidation(){
        TIERS.putAll(FurnacePatternDatagen.DUMMY_FURNACE_PATTERNS);
    }

    public static void sortPatternsByRules(Collection<PatternUpgradeRule> rules) {

        Map<ResourceLocation, Set<ResourceLocation>> adj = new HashMap<>();
        Map<ResourceLocation, Integer> indegree = new HashMap<>();

        for (ResourceLocation id : TIERS.keySet()) {
            adj.put(id, new LinkedHashSet<>());
            indegree.put(id, 0);
        }

        // 3) 加边 + 入度
        for (PatternUpgradeRule r : rules) {
            ResourceLocation from = r.from();
            ResourceLocation to = r.to();

            // 允许外部起点（stone/original）等：不在 TIERS 就忽略该边
            if (!TIERS.containsKey(from) || !TIERS.containsKey(to)) {
                continue;
            }

            if (adj.get(from).add(to)) {
                indegree.put(to, indegree.get(to) + 1);
            }
        }

        // 4) Kahn
        ArrayDeque<ResourceLocation> q = new ArrayDeque<>();
        for (var e : indegree.entrySet()) {
            if (e.getValue() == 0) q.add(e.getKey());
        }

        List<ResourceLocation> orderIds = new ArrayList<>(TIERS.size());
        while (!q.isEmpty()) {
            ResourceLocation cur = q.removeFirst();
            orderIds.add(cur);

            for (ResourceLocation nxt : adj.get(cur)) {
                int d = indegree.merge(nxt, -1, Integer::sum);
                if (d == 0) q.addLast(nxt);
            }
        }

        if (orderIds.size() != TIERS.size()) {
            List<ResourceLocation> cycleCandidates = indegree.entrySet().stream()
                    .filter(e -> e.getValue() > 0)
                    .map(Map.Entry::getKey)
                    .toList();
            throw new IllegalStateException(
                    "TierUpgradeRule contains cycle or conflicting edges. Candidates: " + cycleCandidates
            );
        }

        // 7) 根据排序结果重排 tier
        List<FurnacePattern> sorted = new ArrayList<>(orderIds.size());
        for (ResourceLocation id : orderIds) {
            sorted.add(TIERS.get(id));
        }

        ORDER.clear();
        for (int i = 0; i < sorted.size(); i++) {
            ORDER.put(sorted.get(i), i);
        }

        // =========================
        // 新增：列出并删除“完全孤立”的 tier
        // =========================

        // 仅统计“规则两端都在 TIERS 内”的引用，避免 stone/original 等外部 id 造成误判
        Set<ResourceLocation> referencedTierIds = new HashSet<>();
        for (PatternUpgradeRule r : rules) {
            ResourceLocation from = r.from();
            ResourceLocation to = r.to();
            if (TIERS.containsKey(from) && TIERS.containsKey(to)) {
                referencedTierIds.add(from);
                referencedTierIds.add(to);
            }
        }

        List<ResourceLocation> isolatedIds = TIERS.keySet().stream()
                .filter(id -> !referencedTierIds.contains(id))
                .toList();

        if (!isolatedIds.isEmpty()) {
            IronFurnaces.LOGGER.error(
                    "Found isolated FurnaceTiers (not referenced by any TierUpgradeRule). Removing from TIERS: {}",
                    isolatedIds
            );

            // 从 TIERS 移除
            for (ResourceLocation id : isolatedIds) {
                TIERS.remove(id);
            }

            // ORDER 里也同步删掉，并重新编号（否则 index 会出现空洞/旧映射）
            ORDER.clear();
            int idx = 0;
            for (FurnacePattern tier : sorted) {
                // tier 仍存在于 TIERS 的才保留
                if (TIERS.containsKey(tier.id())) {
                    ORDER.put(tier, idx++);
                }
            }
        }
    }

}
