
package ironfurnaces.tileentity.furnaces.pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import ironfurnaces.loaders.IronFurnaces;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.profiling.ProfilerFiller;
//? >1.21.11 {
import net.minecraft.resources.FileToIdConverter;
//?}
import java.util.HashMap;
import java.util.Map;
//~ if > 1.21.11 'SimpleJsonResourceReloadListener' -> 'SimpleJsonResourceReloadListener<JsonElement>'
public class FurnacePatternDefinitionReloadListener extends SimpleJsonResourceReloadListener<JsonElement> {

    private static final Gson GSON = new GsonBuilder().create();

    public FurnacePatternDefinitionReloadListener() {
        //~ if > 1.21.11 'GSON, FurnacePatternManager.DIRECTORY' -> 'ExtraCodecs.JSON, FileToIdConverter.json(FurnacePatternManager.DIRECTORY)'
        super(ExtraCodecs.JSON, FileToIdConverter.json(FurnacePatternManager.DIRECTORY));
    }

    @Override
    protected void apply(
            Map<Identifier, JsonElement> jsonMap,
            ResourceManager resourceManager,
            ProfilerFiller profiler
    ) {
        Map<Identifier, FurnacePatternDefinition> loaded = new HashMap<>();
        int ok = 0;
        int failed = 0;
        for (Map.Entry<Identifier, JsonElement> entry : jsonMap.entrySet()) {
            Identifier id = entry.getKey();
            JsonElement json = entry.getValue();
            try {
                FurnacePatternDefinition definition = FurnacePatternDefinition.CODEC
                        .parse(JsonOps.INSTANCE, json)
                        .getOrThrow(
                                //? 1.20.1
                                //false,
                                msg -> {
                                    throw new IllegalStateException("Failed to parse pattern definition " + id + ": " + msg);
                                });
                loaded.put(id, definition);
                ok++;
            } catch (Exception ex) {
                failed++;
                IronFurnaces.LOGGER.error("Failed to load Pattern {}: {}", id, ex.getMessage());
            }

        }

        FurnacePatternManager.setAllDefinitions(loaded);
        IronFurnaces.LOGGER.info("Loaded Pattern(s): ok={}, failed={}, total={}", ok, failed, jsonMap.size());
    }
}
