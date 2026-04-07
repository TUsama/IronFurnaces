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
            PoseStack.Pose pose,
            Matrix3f normal,
            float x, float y, float z,
            float u, float v,
            int packedLight, int packedOverlay,
            float nx, float ny, float nz
    ) {
        //? 1.20.1 {
        vc.vertex(pose.pose(), x, y, z)
                .color(255, 255, 255, 255)
                .uv(u, v)
                .overlayCoords(packedOverlay)
                .uv2(packedLight)
                .normal(normal, nx, ny, nz)
                .endVertex();
        //? } else {
        /*vc.addVertex(pose, x, y, z)
                .setColor(255, 255, 255, 255)
                .setUv(u, v)
                .setOverlay(packedOverlay)
                .setLight(packedLight)
                .setNormal(pose, nx, ny, nz);
        *///?}

    }

    private static void renderNorthFace(
            VertexConsumer vc, PoseStack.Pose pose, TextureAtlasSprite s, int light, int overlay
    ) {
        float u0 = s.getU0();
        float u1 = s.getU1();
        float v0 = s.getV0();
        float v1 = s.getV1();

        // 保持顶点顺序不变，仅水平翻转 U
        putVertex(vc, pose, pose.normal(), 0, 1, 0, u1, v0, light, overlay, 0, 0, -1);
        putVertex(vc, pose, pose.normal(), 1, 1, 0, u0, v0, light, overlay, 0, 0, -1);
        putVertex(vc, pose, pose.normal(), 1, 0, 0, u0, v1, light, overlay, 0, 0, -1);
        putVertex(vc, pose, pose.normal(), 0, 0, 0, u1, v1, light, overlay, 0, 0, -1);
    }

    private static void renderSouthFace(
            VertexConsumer vc, PoseStack.Pose pose, TextureAtlasSprite s, int light, int overlay
    ) {
        float u0 = s.getU0();
        float u1 = s.getU1();
        float v0 = s.getV0();
        float v1 = s.getV1();

        // 保持顶点顺序不变，仅水平翻转 U
        putVertex(vc, pose, pose.normal(), 1, 0, 1, u1, v1, light, overlay, 0, 0, 1);
        putVertex(vc, pose, pose.normal(), 1, 1, 1, u1, v0, light, overlay, 0, 0, 1);
        putVertex(vc, pose, pose.normal(), 0, 1, 1, u0, v0, light, overlay, 0, 0, 1);
        putVertex(vc, pose, pose.normal(), 0, 0, 1, u0, v1, light, overlay, 0, 0, 1);
    }

    private static void renderWestFace(
            VertexConsumer vc, PoseStack.Pose pose, TextureAtlasSprite s, int light, int overlay
    ) {
        float u0 = s.getU0();
        float u1 = s.getU1();
        float v0 = s.getV0();
        float v1 = s.getV1();

        // 保持顶点顺序不变，仅水平翻转 U
        putVertex(vc, pose, pose.normal(), 0, 1, 1, u1, v0, light, overlay, -1, 0, 0);
        putVertex(vc, pose, pose.normal(), 0, 1, 0, u0, v0, light, overlay, -1, 0, 0);
        putVertex(vc, pose, pose.normal(), 0, 0, 0, u0, v1, light, overlay, -1, 0, 0);
        putVertex(vc, pose, pose.normal(), 0, 0, 1, u1, v1, light, overlay, -1, 0, 0);
    }

    private static void renderEastFace(
            VertexConsumer vc, PoseStack.Pose pose, TextureAtlasSprite s, int light, int overlay
    ) {
        float u0 = s.getU0();
        float u1 = s.getU1();
        float v0 = s.getV0();
        float v1 = s.getV1();

        // 保持顶点顺序不变，仅水平翻转 U
        putVertex(vc, pose, pose.normal(), 1, 1, 0, u1, v0, light, overlay, 1, 0, 0);
        putVertex(vc, pose, pose.normal(), 1, 1, 1, u0, v0, light, overlay, 1, 0, 0);
        putVertex(vc, pose, pose.normal(), 1, 0, 1, u0, v1, light, overlay, 1, 0, 0);
        putVertex(vc, pose, pose.normal(), 1, 0, 0, u1, v1, light, overlay, 1, 0, 0);
    }

    private static void renderUpFace(
            VertexConsumer vc, PoseStack.Pose pose, TextureAtlasSprite s, int light, int overlay
    ) {
        float u0 = s.getU0();
        float u1 = s.getU1();
        float v0 = s.getV0();
        float v1 = s.getV1();

        // 顶面通常还需要调一下方向，这里按更常见的“上看时不镜像”去处理
        putVertex(vc, pose, pose.normal(), 0, 1, 1, u0, v1, light, overlay, 0, 1, 0);
        putVertex(vc, pose, pose.normal(), 1, 1, 1, u1, v1, light, overlay, 0, 1, 0);
        putVertex(vc, pose, pose.normal(), 1, 1, 0, u1, v0, light, overlay, 0, 1, 0);
        putVertex(vc, pose, pose.normal(), 0, 1, 0, u0, v0, light, overlay, 0, 1, 0);
    }

    private static void renderDownFace(
            VertexConsumer vc, PoseStack.Pose pose, TextureAtlasSprite s, int light, int overlay
    ) {
        float u0 = s.getU0();
        float u1 = s.getU1();
        float v0 = s.getV0();
        float v1 = s.getV1();

        // 底面按与顶面相协调的方向处理
        putVertex(vc, pose, pose.normal(), 0, 0, 0, u0, v1, light, overlay, 0, -1, 0);
        putVertex(vc, pose, pose.normal(), 1, 0, 0, u1, v1, light, overlay, 0, -1, 0);
        putVertex(vc, pose, pose.normal(), 1, 0, 1, u1, v0, light, overlay, 0, -1, 0);
        putVertex(vc, pose, pose.normal(), 0, 0, 1, u0, v0, light, overlay, 0, -1, 0);
    }
}