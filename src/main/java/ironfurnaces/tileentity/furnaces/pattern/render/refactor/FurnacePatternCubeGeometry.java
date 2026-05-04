//? >1.21.11{
/*package ironfurnaces.tileentity.furnaces.pattern.render.refactor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import ironfurnaces.tileentity.furnaces.pattern.render.CubeRenderUtil;
import ironfurnaces.tileentity.furnaces.pattern.render.CubeTextures;
import ironfurnaces.tileentity.furnaces.pattern.render.PatternPreviewTextureResolver;
import net.minecraft.client.renderer.SubmitNodeCollector;

public final class FurnacePatternCubeGeometry implements SubmitNodeCollector.CustomGeometryRenderer {

    private final FurnacePatternRenderData data;
    private final int packedLight;
    private final int packedOverlay;

    public FurnacePatternCubeGeometry(
            FurnacePatternRenderData data,
            int packedLight,
            int packedOverlay
    ) {
        this.data = data;
        this.packedLight = packedLight;
        this.packedOverlay = packedOverlay;
    }

    @Override
    public void render(PoseStack.Pose pose, VertexConsumer consumer) {
        CubeTextures textures = PatternPreviewTextureResolver.resolve(
                data.patternPath(),
                data.lit(),
                data.jovial(),
                data.recipeType()
        );

        CubeRenderUtil.renderCube(
                textures,
                data.facing(),
                pose,
                consumer,
                packedLight,
                packedOverlay
        );
    }
}

*///?}