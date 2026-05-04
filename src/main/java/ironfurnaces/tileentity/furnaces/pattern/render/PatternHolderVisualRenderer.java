//? <1.21.11{
/*package ironfurnaces.tileentity.furnaces.pattern.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import ironfurnaces.items.JovialState;
import ironfurnaces.tileentity.furnaces.cache.IRecipeTypeHandler;
import net.minecraft.client.renderer.MultiBufferSource;
//? >1.21.11{
import net.minecraft.client.renderer.SubmitNodeCollector;
//?}
import net.minecraft.core.Direction;

public final class PatternHolderVisualRenderer
{

    public static void render(
            String patternPath,
            boolean lit,
            Direction facing,
            JovialState jovial,
            IRecipeTypeHandler recipeType,
            PoseStack poseStack,
            MultiBufferSource buffers,
            int packedLight,
            int packedOverlay
    ) {
        CubeTextures textures = PatternPreviewTextureResolver.resolve(
                patternPath,
                lit,
                jovial,
                recipeType
        );

        CubeRenderUtil.renderCube(
                textures,
                facing,
                poseStack,
                buffers,
                packedLight,
                packedOverlay
        );
    }
}

*///?}