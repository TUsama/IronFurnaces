package ironfurnaces.tileentity.furnaces.pattern.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public final class CubeRenderUtil {

    private static final float EPS = 0.001F;
    private static final float MIN = EPS;
    private static final float MAX = 1.0F - EPS;

    private CubeRenderUtil() {}

    public static void renderCube(
            CubeTextures textures,
            Direction facing,
            PoseStack poseStack,
            MultiBufferSource buffers,
            int packedLight,
            int packedOverlay
    ) {
        TextureAtlasSprite front = sprite(textures.front());
        TextureAtlasSprite back = sprite(textures.back());
        TextureAtlasSprite left = sprite(textures.left());
        TextureAtlasSprite right = sprite(textures.right());
        TextureAtlasSprite top = sprite(textures.top());
        TextureAtlasSprite bottom = sprite(textures.bottom());

        VertexConsumer vc = buffers.getBuffer(RenderType.cutout());

        poseStack.pushPose();

        // 以方块中心为轴旋转，使“默认 front 朝 north”的局部模型对齐到实际 facing
        poseStack.translate(0.5D, 0.5D, 0.5D);
        switch (facing) {
            case SOUTH -> poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            case WEST -> poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
            case EAST -> poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
            default -> {
            }
        }
        poseStack.translate(-0.5D, -0.5D, -0.5D);

        PoseStack.Pose pose = poseStack.last();

        // 默认局部朝向：
        // north = front
        // south = back
        // west  = left
        // east  = right
        // up    = top
        // down  = bottom
        renderNorthFace(vc, pose, front, packedLight, packedOverlay);
        renderSouthFace(vc, pose, back, packedLight, packedOverlay);
        renderWestFace(vc, pose, left, packedLight, packedOverlay);
        renderEastFace(vc, pose, right, packedLight, packedOverlay);
        renderUpFace(vc, pose, top, packedLight, packedOverlay);
        renderDownFace(vc, pose, bottom, packedLight, packedOverlay);

        poseStack.popPose();
    }

    private static TextureAtlasSprite sprite(ResourceLocation rl) {
        return Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(rl);
    }

    private static void putVertex(
            VertexConsumer vc,
            Matrix4f pose,
            Matrix3f normal,
            float x, float y, float z,
            float u, float v,
            int light, int overlay,
            float nx, float ny, float nz
    ) {
        vc.vertex(pose, x, y, z)
                .color(255, 255, 255, 255)
                .uv(u, v)
                .overlayCoords(overlay)
                .uv2(light)
                .normal(normal, nx, ny, nz)
                .endVertex();
    }

    private static void renderNorthFace(
            VertexConsumer vc, PoseStack.Pose pose, TextureAtlasSprite s, int light, int overlay
    ) {
        FaceUV uv = shrunkUV(s);
        float u0 = uv.u0();
        float u1 = uv.u1();
        float v0 = uv.v0();
        float v1 = uv.v1();

        putVertex(vc, pose.pose(), pose.normal(), MIN, MAX, MIN, u0, v0, light, overlay, 0, 0, -1);
        putVertex(vc, pose.pose(), pose.normal(), MAX, MAX, MIN, u1, v0, light, overlay, 0, 0, -1);
        putVertex(vc, pose.pose(), pose.normal(), MAX, MIN, MIN, u1, v1, light, overlay, 0, 0, -1);
        putVertex(vc, pose.pose(), pose.normal(), MIN, MIN, MIN, u0, v1, light, overlay, 0, 0, -1);
    }

    private static void renderSouthFace(
            VertexConsumer vc, PoseStack.Pose pose, TextureAtlasSprite s, int light, int overlay
    ) {
        FaceUV uv = shrunkUV(s);
        float u0 = uv.u0();
        float u1 = uv.u1();
        float v0 = uv.v0();
        float v1 = uv.v1();

        putVertex(vc, pose.pose(), pose.normal(), MAX, MIN, MAX, u0, v1, light, overlay, 0, 0, 1);
        putVertex(vc, pose.pose(), pose.normal(), MAX, MAX, MAX, u0, v0, light, overlay, 0, 0, 1);
        putVertex(vc, pose.pose(), pose.normal(), MIN, MAX, MAX, u1, v0, light, overlay, 0, 0, 1);
        putVertex(vc, pose.pose(), pose.normal(), MIN, MIN, MAX, u1, v1, light, overlay, 0, 0, 1);
    }

    private static void renderWestFace(
            VertexConsumer vc, PoseStack.Pose pose, TextureAtlasSprite s, int light, int overlay
    ) {
        FaceUV uv = shrunkUV(s);
        float u0 = uv.u0();
        float u1 = uv.u1();
        float v0 = uv.v0();
        float v1 = uv.v1();

        putVertex(vc, pose.pose(), pose.normal(), MIN, MAX, MAX, u0, v0, light, overlay, -1, 0, 0);
        putVertex(vc, pose.pose(), pose.normal(), MIN, MAX, MIN, u1, v0, light, overlay, -1, 0, 0);
        putVertex(vc, pose.pose(), pose.normal(), MIN, MIN, MIN, u1, v1, light, overlay, -1, 0, 0);
        putVertex(vc, pose.pose(), pose.normal(), MIN, MIN, MAX, u0, v1, light, overlay, -1, 0, 0);
    }

    private static void renderEastFace(
            VertexConsumer vc, PoseStack.Pose pose, TextureAtlasSprite s, int light, int overlay
    ) {
        FaceUV uv = shrunkUV(s);
        float u0 = uv.u0();
        float u1 = uv.u1();
        float v0 = uv.v0();
        float v1 = uv.v1();

        putVertex(vc, pose.pose(), pose.normal(), MAX, MAX, MIN, u0, v0, light, overlay, 1, 0, 0);
        putVertex(vc, pose.pose(), pose.normal(), MAX, MAX, MAX, u1, v0, light, overlay, 1, 0, 0);
        putVertex(vc, pose.pose(), pose.normal(), MAX, MIN, MAX, u1, v1, light, overlay, 1, 0, 0);
        putVertex(vc, pose.pose(), pose.normal(), MAX, MIN, MIN, u0, v1, light, overlay, 1, 0, 0);
    }

    private static void renderUpFace(
            VertexConsumer vc, PoseStack.Pose pose, TextureAtlasSprite s, int light, int overlay
    ) {
        FaceUV uv = shrunkUV(s);
        float u0 = uv.u0();
        float u1 = uv.u1();
        float v0 = uv.v0();
        float v1 = uv.v1();

        putVertex(vc, pose.pose(), pose.normal(), MIN, MAX, MAX, u0, v0, light, overlay, 0, 1, 0);
        putVertex(vc, pose.pose(), pose.normal(), MAX, MAX, MAX, u1, v0, light, overlay, 0, 1, 0);
        putVertex(vc, pose.pose(), pose.normal(), MAX, MAX, MIN, u1, v1, light, overlay, 0, 1, 0);
        putVertex(vc, pose.pose(), pose.normal(), MIN, MAX, MIN, u0, v1, light, overlay, 0, 1, 0);
    }

    private static void renderDownFace(
            VertexConsumer vc, PoseStack.Pose pose, TextureAtlasSprite s, int light, int overlay
    ) {
        FaceUV uv = shrunkUV(s);
        float u0 = uv.u0();
        float u1 = uv.u1();
        float v0 = uv.v0();
        float v1 = uv.v1();

        putVertex(vc, pose.pose(), pose.normal(), MIN, MIN, MIN, u0, v0, light, overlay, 0, -1, 0);
        putVertex(vc, pose.pose(), pose.normal(), MAX, MIN, MIN, u1, v0, light, overlay, 0, -1, 0);
        putVertex(vc, pose.pose(), pose.normal(), MAX, MIN, MAX, u1, v1, light, overlay, 0, -1, 0);
        putVertex(vc, pose.pose(), pose.normal(), MIN, MIN, MAX, u0, v1, light, overlay, 0, -1, 0);
    }

    private record FaceUV(float u0, float u1, float v0, float v1) {}

    private static FaceUV shrunkUV(TextureAtlasSprite s) {
        float r = s.uvShrinkRatio();
        float u0 = net.minecraft.util.Mth.lerp(r, s.getU0(), s.getU1());
        float u1 = net.minecraft.util.Mth.lerp(r, s.getU1(), s.getU0());
        float v0 = net.minecraft.util.Mth.lerp(r, s.getV0(), s.getV1());
        float v1 = net.minecraft.util.Mth.lerp(r, s.getV1(), s.getV0());
        return new FaceUV(u0, u1, v0, v1);
    }
}