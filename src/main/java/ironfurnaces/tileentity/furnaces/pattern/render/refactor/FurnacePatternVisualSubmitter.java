//? >1.21.11{
/*package ironfurnaces.tileentity.furnaces.pattern.render.refactor;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;

public final class FurnacePatternVisualSubmitter {

    private FurnacePatternVisualSubmitter() {
    }

    public static void submit(
            FurnacePatternRenderData data,
            PoseStack poseStack,
            SubmitNodeCollector collector,
            int packedLight,
            int packedOverlay,
            int outlineColor
    ) {
        if (data == null) {
            return;
        }

        var renderType = requiresCutout(data)
                ? RenderTypes.cutoutMovingBlock()
                : RenderTypes.solidMovingBlock();

        collector.submitCustomGeometry(
                poseStack,
                renderType,
                new FurnacePatternCubeGeometry(
                        data,
                        packedLight,
                        packedOverlay
                )
        );
    }

    private static boolean requiresCutout(FurnacePatternRenderData data) {
        return "crystal_furnace".equals(data.patternPath());
    }
}


*///?}