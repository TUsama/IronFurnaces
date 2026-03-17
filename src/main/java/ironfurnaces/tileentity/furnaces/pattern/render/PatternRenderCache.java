package ironfurnaces.tileentity.furnaces.pattern.render;

import ironfurnaces.items.JovialState;
import ironfurnaces.tileentity.furnaces.cache.AugmentCache;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class PatternRenderCache {
    private PatternRenderCache() {}

    private static final Map<ModelKey, ModelResourceLocation> MRL_CACHE = new ConcurrentHashMap<>();
    private static final Map<ModelKey, BakedModel> BAKED_CACHE = new ConcurrentHashMap<>();

    public static void clear() {
        MRL_CACHE.clear();
        BAKED_CACHE.clear();
    }

    /**
     * 复用你 datagen 的命名规则：
     * modelName = "block/" + prefix + litPart + typePart
     *
     * typePart 现在由 HandlingRecipeType 决定：NORMAL "" / SMOKE "_smoke" / BLAST "_blast"
     */
    public static ResourceLocation computeModelRl(
            String modid,
            String patternPath,
            boolean lit,
            JovialState jovial,
            AugmentCache.HandlingRecipeType recipeType
    ) {
        String prefix = switch (jovial) {
            case SPOOKY -> "spooky_furnace";
            case XMAS -> "xmas_furnace";
            default -> patternPath;
        };

        String litPart = lit ? "_on" : "";

        String typePart = switch (recipeType) {
            case NORMAL -> "";
            case SMOKE -> "_smoke";
            case BLAST -> "_blast";
        };
        System.out.println(new ResourceLocation(modid, "block/" + prefix + litPart + typePart));
        return new ResourceLocation(modid, "block/" + prefix + litPart + typePart);
    }

    public static ModelResourceLocation getMrlCached(
            String modid,
            String patternPath,
            boolean lit,
            JovialState jovial,
            AugmentCache.HandlingRecipeType recipeType
    ) {
        ModelKey key = new ModelKey(patternPath, lit, jovial, recipeType);
        return MRL_CACHE.computeIfAbsent(key, k -> {
            ResourceLocation rl = computeModelRl(modid, k.patternPath(), k.lit(), k.jovial(), k.recipeType());
            // 1.20.1：如果 missing model，把 "" 改成 "normal"
            return new ModelResourceLocation(rl, "normal");
        });
    }

    public static BakedModel getBakedCached(
            String modid,
            ModelManager modelManager,
            String patternPath,
            boolean lit,
            JovialState jovial,
            AugmentCache.HandlingRecipeType recipeType
    ) {
        ModelKey key = new ModelKey(patternPath, lit, jovial, recipeType);
        return BAKED_CACHE.computeIfAbsent(key, k -> {
            ModelResourceLocation mrl = getMrlCached(modid, k.patternPath(), k.lit(), k.jovial(), k.recipeType());
            return modelManager.getModel(mrl);
        });
    }

    private record ModelKey(
            String patternPath,
            boolean lit,
            JovialState jovial,
            AugmentCache.HandlingRecipeType recipeType
    ) {}
}