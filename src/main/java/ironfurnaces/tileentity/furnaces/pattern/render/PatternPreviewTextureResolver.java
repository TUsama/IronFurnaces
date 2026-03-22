package ironfurnaces.tileentity.furnaces.pattern.render;

import ironfurnaces.items.JovialState;
import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.tileentity.furnaces.cache.AugmentCache;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class PatternPreviewTextureResolver {

    /**
     * 最终解析缓存
     */
    private static final Map<TextureKey, CubeTextures> TEXTURE_CACHE = new ConcurrentHashMap<>();
    /**
     * 所有存在的纹理
     */
    private static volatile Set<ResourceLocation> EXISTING_TEXTURES = Set.of();

    private PatternPreviewTextureResolver() {
    }

    /**
     * reload 时调用
     */
    public static void reload(Set<ResourceLocation> textures) {
        EXISTING_TEXTURES = textures;
        TEXTURE_CACHE.clear();

    }

    public static void clear() {
        TEXTURE_CACHE.clear();
    }

    /**
     * 主入口
     */
    public static CubeTextures resolve(
            String patternPath,
            boolean lit,
            JovialState jovial,
            AugmentCache.HandlingRecipeType type
    ) {

        TextureKey key = new TextureKey(patternPath, lit, jovial, type);

        return TEXTURE_CACHE.computeIfAbsent(key, PatternPreviewTextureResolver::resolveInternal);
    }

    /**
     * 实际解析
     */
    private static CubeTextures resolveInternal(TextureKey key) {

        var base = makeRL(key.patternPath(), key.jovial(), key.lit(), key.type());

        ResourceLocation front = rl("block/" + base[0] + "_front" + base[1]);

        ResourceLocation side = fallback(
                rl("block/" + base[0] + "_side" + base[1]),
                rl("block/" + base[0] + "_side")
        );

        ResourceLocation top = fallback(
                rl("block/" + base[0] + "_top" + base[1] ),
                rl("block/" + base[0] + "_top"),
                side
        );

        ResourceLocation bottom = fallback(
                rl("block/" + base[0] + "_bottom" + base[1]),
                rl("block/" + base[0] + "_bottom"),
                side
        );

        return CubeTextures.frontSideTopBottom(front, side, top, bottom);
    }

    private static String[] makeRL(String patternPath, JovialState jovial, boolean lit, AugmentCache.HandlingRecipeType type) {
        String[] strings = new String[2];

        strings[0] = switch (jovial){
            case NONE -> patternPath;
            case XMAS -> "xmas_furnace";
            case SPOOKY -> "spooky_furnace";
        };

        var s = new StringBuilder();
        if (lit){
            s.append("_on");
        }

        switch (type){
            case BLAST -> s.append("_blast");
            case SMOKE -> s.append("_smoke");
            default -> {}
        }
        strings[1] = s.toString();
        return strings;
    }


    /**
     * fallback 逻辑
     */
    private static ResourceLocation fallback(ResourceLocation primary, ResourceLocation secondary) {
        return exists(primary) ? primary : secondary;
    }

    private static ResourceLocation fallback(ResourceLocation primary, ResourceLocation secondary, ResourceLocation tertiary) {

        if (exists(primary)) return primary;
        if (exists(secondary)) return secondary;

        return tertiary;
    }

    /**
     * 查询纹理是否存在
     */
    private static boolean exists(ResourceLocation texture) {
        return EXISTING_TEXTURES.contains(texture);
    }

    private static ResourceLocation rl(String path) {
        return IronFurnaces.id(path);
    }

    /**
     * cache key
     */
    private record TextureKey(
            String patternPath,
            boolean lit,
            JovialState jovial,
            AugmentCache.HandlingRecipeType type
    ) {
    }
}
