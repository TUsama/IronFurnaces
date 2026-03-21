package ironfurnaces.tileentity.furnaces.pattern.render;

import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

public class FurnaceCacheReloadListener extends SimplePreparableReloadListener<Void> {

    public static final FurnaceCacheReloadListener INSTANCE = new FurnaceCacheReloadListener();

    @Override
    protected Void prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        return null;
    }

    @Override
    protected void apply(Void object, ResourceManager resourceManager, ProfilerFiller profiler) {
        PatternPreviewTextureResolver.clear();
    }
}
