package ironfurnaces.tileentity.furnaces.tier;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.HashMap;
import java.util.Map;

public class FurnacePatternReloadListener extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new GsonBuilder().create();

    public FurnacePatternReloadListener() {
        super(GSON, "furnace_tiers");
    }

    @Override
    protected void apply(
            Map<ResourceLocation, JsonElement> jsonMap,
            ResourceManager resourceManager,
            ProfilerFiller profiler
    ) {
        Map<ResourceLocation, FurnacePattern> loaded = new HashMap<>();

        for (Map.Entry<ResourceLocation, JsonElement> entry : jsonMap.entrySet()) {
            ResourceLocation id = entry.getKey();
            JsonElement json = entry.getValue();

            FurnacePattern tier = FurnacePattern.CODEC
                    .parse(JsonOps.INSTANCE, json)
                    .getOrThrow(false, msg -> {
                        throw new IllegalStateException("Failed to parse tier " + id + ": " + msg);
                    });

            loaded.put(id, tier);
        }

        FurnacePatternManager.setAll(loaded);
    }
}
