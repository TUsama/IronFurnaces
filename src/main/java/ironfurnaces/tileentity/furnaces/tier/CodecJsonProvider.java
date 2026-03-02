package ironfurnaces.tileentity.furnaces.tier;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class CodecJsonProvider<T> implements DataProvider {
    private final PackOutput packOutput;
    private final PackOutput.PathProvider pathProvider;
    private final Codec<T> codec;
    protected final Map<ResourceLocation, T> entries = new LinkedHashMap<>();

    protected CodecJsonProvider(PackOutput packOutput, String directory, Codec<T> codec) {
        this.packOutput = packOutput;
        this.pathProvider = packOutput.createPathProvider(PackOutput.Target.DATA_PACK, directory);
        this.codec = codec;
    }

    /** 子类在这里填充 entries */
    protected abstract void buildEntries();

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        entries.clear();
        buildEntries();

        CompletableFuture<?>[] futures = entries.entrySet().stream().map(e -> {
            ResourceLocation id = e.getKey();
            T value = e.getValue();

            JsonElement json = codec.encodeStart(JsonOps.INSTANCE, value)
                    .getOrThrow(false, msg -> {
                        throw new IllegalStateException("Failed to encode " + id + ": " + msg);
                    });

            Path path = pathProvider.json(id);
            return DataProvider.saveStable(output, json, path);
        }).toArray(CompletableFuture[]::new);

        return CompletableFuture.allOf(futures);
    }

    @Override
    public String getName() {
        return "Codec JSON Provider (" + pathProvider.toString() + ")";
    }
}
