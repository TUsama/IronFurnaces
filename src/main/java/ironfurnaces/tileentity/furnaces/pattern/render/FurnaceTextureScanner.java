package ironfurnaces.tileentity.furnaces.pattern.render;

import com.clefal.nirvana_lib.utils.ResourceLocationUtils;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Set;
import java.util.stream.Collectors;

public class FurnaceTextureScanner extends SimplePreparableReloadListener<Set<Identifier>> {

    public static final FurnaceTextureScanner INSTANCE = new FurnaceTextureScanner();

    @Override
    protected Set<Identifier> prepare(ResourceManager manager, ProfilerFiller profiler) {
        return manager.listResources(
                        "textures/block",
                        path -> path.getPath().endsWith(".png")
                ).keySet().stream()
                .map(id -> ResourceLocationUtils.make(
                        id.getNamespace(),
                        id.getPath()
                                .replace("textures/", "")
                                .replace(".png", "")
                ))
                .collect(Collectors.toSet());
    }

    @Override
    protected void apply(Set<Identifier> textures, ResourceManager manager, ProfilerFiller profiler) {
        PatternPreviewTextureResolver.reload(textures);
    }
}
