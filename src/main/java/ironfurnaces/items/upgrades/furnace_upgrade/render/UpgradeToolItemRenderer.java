package ironfurnaces.items.upgrades.furnace_upgrade.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import ironfurnaces.items.JovialState;
import ironfurnaces.items.upgrades.furnace_upgrade.IUpgradeStorage;
import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.tileentity.furnaces.cache.recipe_type_handlers.SmeltRecipeTypeHandler;
import ironfurnaces.tileentity.furnaces.pattern.render.PatternPreviewTextureResolver;
import ironfurnaces.tileentity.furnaces.pattern.upgrade.PatternUpgradeRule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;

public class UpgradeToolItemRenderer extends BlockEntityWithoutLevelRenderer {

    private static final ResourceLocation ARROW = IronFurnaces.gui("arrow");

    public UpgradeToolItemRenderer() {
        super(
                Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels()
        );
    }

    private static void setupRootTransform(ItemDisplayContext ctx, PoseStack poseStack) {
        switch (ctx) {
            case GUI -> {
                poseStack.translate(0.5D, 0.5D, 0.0D);
                poseStack.scale(0.85F, 0.85F, 0.85F);
            }
            case THIRD_PERSON_LEFT_HAND, THIRD_PERSON_RIGHT_HAND -> {
                poseStack.translate(0.5D, 0.5D, 0.5D);
                poseStack.scale(0.6F, 0.6F, 0.6F);
            }
            case FIRST_PERSON_LEFT_HAND, FIRST_PERSON_RIGHT_HAND -> {
                poseStack.translate(0.5D, 0.7D, 0.5D);
                poseStack.scale(0.7F, 0.7F, 0.7F);
            }
            case GROUND -> {
                poseStack.translate(0.5D, 0.5D, 0.5D);
                poseStack.scale(0.5F, 0.5F, 0.5F);
            }
            default -> {
                poseStack.translate(0.5D, 0.5D, 0.5D);
                poseStack.scale(0.7F, 0.7F, 0.7F);
            }
        }
    }

    private static TextureAtlasSprite getBlockSprite(net.minecraft.resources.ResourceLocation texture) {
        return Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(texture);
    }

    private static void putVertex(
            VertexConsumer vc,
            PoseStack.Pose pose,
            Matrix3f normal,
            float x, float y, float z,
            float u, float v,
            int packedLight, int packedOverlay
    ) {
        putVertex(vc, pose, normal, x, y, z, u, v, packedLight, packedOverlay, 0.0F, 0.0F, 1.0F);
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

    /**
     * 画一个朝向玩家的正面贴图面片
     */
    private static void renderFrontSpriteQuad(
            TextureAtlasSprite sprite,
            PoseStack poseStack,
            MultiBufferSource buffers,
            int packedLight,
            int packedOverlay
    ) {
        VertexConsumer vc = buffers.getBuffer(RenderType.entityCutoutNoCull(InventoryMenu.BLOCK_ATLAS));
        PoseStack.Pose pose = poseStack.last();

        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();

        float minX = -0.5F;
        float maxX =  0.5F;
        float minY = -0.5F;
        float maxY =  0.5F;
        float z = 0.001F;

        // 正面
        putVertex(vc, pose, pose.normal(), minX, maxY, z, u0, v0, packedLight, packedOverlay);
        putVertex(vc, pose, pose.normal(), maxX, maxY, z, u1, v0, packedLight, packedOverlay);
        putVertex(vc, pose, pose.normal(), maxX, minY, z, u1, v1, packedLight, packedOverlay);
        putVertex(vc, pose, pose.normal(), minX, minY, z, u0, v1, packedLight, packedOverlay);

        // 背面
        putVertex(vc, pose, pose.normal(), minX, minY, -z, u0, v1, packedLight, packedOverlay);
        putVertex(vc, pose, pose.normal(), maxX, minY, -z, u1, v1, packedLight, packedOverlay);
        putVertex(vc, pose, pose.normal(), maxX, maxY, -z, u1, v0, packedLight, packedOverlay);
        putVertex(vc, pose, pose.normal(), minX, maxY, -z, u0, v0, packedLight, packedOverlay);
    }

    private static void renderPatternFront(
            String patternPath,
            PoseStack poseStack,
            MultiBufferSource buffers,
            int packedLight,
            int packedOverlay
    ) {
        var textures = PatternPreviewTextureResolver.resolve(
                patternPath,
                false,
                JovialState.NONE,
                SmeltRecipeTypeHandler.INSTANCE
        );

        TextureAtlasSprite sprite = getBlockSprite(textures.front());
        renderFrontSpriteQuad(sprite, poseStack, buffers, packedLight, packedOverlay);
    }

    private static void renderArrow(
            PoseStack poseStack,
            MultiBufferSource buffers,
            int packedLight,
            int packedOverlay
    ) {
        VertexConsumer vc = buffers.getBuffer(RenderType.entityCutoutNoCull(ARROW));
        PoseStack.Pose pose = poseStack.last();

        // 你这张纹理是 16x16，但这里只需要按 0~1 UV 全图采样
        float u0 = 0.0F;
        float u1 = 1.0F;
        float v0 = 0.0F;
        float v1 = 1.0F;

        // 先做一个比较小的箭头面片
        float minX = -0.16F;
        float maxX =  0.16F;
        float minY = -0.16F;
        float maxY =  0.16F;
        float z = 0.001F;

        // 正面
        putVertex(vc, pose, pose.normal(), minX, maxY,  z, u0, v0, packedLight, packedOverlay, 0.0F, 0.0F,  1.0F);
        putVertex(vc, pose, pose.normal(), maxX, maxY,  z, u1, v0, packedLight, packedOverlay, 0.0F, 0.0F,  1.0F);
        putVertex(vc, pose, pose.normal(), maxX, minY,  z, u1, v1, packedLight, packedOverlay, 0.0F, 0.0F,  1.0F);
        putVertex(vc, pose, pose.normal(), minX, minY,  z, u0, v1, packedLight, packedOverlay, 0.0F, 0.0F,  1.0F);

        // 背面
        putVertex(vc, pose, pose.normal(), minX, minY, -z, u0, v1, packedLight, packedOverlay, 0.0F, 0.0F, -1.0F);
        putVertex(vc, pose, pose.normal(), maxX, minY, -z, u1, v1, packedLight, packedOverlay, 0.0F, 0.0F, -1.0F);
        putVertex(vc, pose, pose.normal(), maxX, maxY, -z, u1, v0, packedLight, packedOverlay, 0.0F, 0.0F, -1.0F);
        putVertex(vc, pose, pose.normal(), minX, maxY, -z, u0, v0, packedLight, packedOverlay, 0.0F, 0.0F, -1.0F);
    }

    @Override
    public void renderByItem(
            @NotNull ItemStack stack,
            @NotNull ItemDisplayContext displayContext,
            @NotNull PoseStack poseStack,
            @NotNull MultiBufferSource buffers,
            int packedLight,
            int packedOverlay
    ) {
        PatternUpgradeRule rule = IUpgradeStorage.get(stack);
        if (rule == null) {
            return;
        }

        if (displayContext == ItemDisplayContext.GUI) {
            packedLight = 0xF000F0;
        }

        poseStack.pushPose();
        setupRootTransform(displayContext, poseStack);



        // 左侧 from
        poseStack.pushPose();
        poseStack.translate(-0.28D, 0.0D, 0.0D);
        if (PatternUpgradeRule.isPatternId(rule.from())) {
            poseStack.scale(0.32F, 0.32F, 0.32F);
        } else {
            poseStack.scale(0.42F, 0.42F, 0.42F);
        }
        renderPatternOrBlock(rule.from(), poseStack, buffers, packedLight, packedOverlay);
        poseStack.popPose();

        // 中间箭头

        poseStack.pushPose();
        poseStack.scale(0.7f, 0.7f, 1.0f);
        renderArrow(poseStack, buffers, packedLight, packedOverlay);
        poseStack.popPose();

        // 右侧 to
        poseStack.pushPose();
        poseStack.translate(0.28D, 0.0D, 0.0D);
        if (PatternUpgradeRule.isPatternId(rule.to())) {
            poseStack.scale(0.32F, 0.32F, 0.32F);
        } else {
            poseStack.scale(0.42F, 0.42F, 0.42F);
        }

        renderPatternOrBlock(rule.to(), poseStack, buffers, packedLight, packedOverlay);
        poseStack.popPose();

        poseStack.popPose();
    }

    private static void renderBlockPreview(
            BlockState state,
            PoseStack poseStack,
            MultiBufferSource buffers,
            int packedLight,
            int packedOverlay
    ) {
        Minecraft mc = Minecraft.getInstance();
        BlockRenderDispatcher dispatcher = mc.getBlockRenderer();

        poseStack.pushPose();

        // 让方块缩到和原先 front sprite 差不多的视觉大小
        poseStack.translate(-0.35D, -0.25D, -0.25D);
        poseStack.scale(0.5F, 0.5F, 0.5F);

        // 适当给一个角度，避免 GUI 里完全正着看显得太死板
        poseStack.translate(0.5D, 0.5D, 0.5D);
        //poseStack.mulPose(Axis.XP.rotationDegrees(20.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(195.0F));
        poseStack.translate(-0.5D, -0.5D, -0.5D);

        dispatcher.renderSingleBlock(
                state,
                poseStack,
                buffers,
                packedLight,
                packedOverlay
        );

        poseStack.popPose();
    }

    private static void renderPatternOrBlock(
            ResourceLocation id,
            PoseStack poseStack,
            MultiBufferSource buffers,
            int packedLight,
            int packedOverlay
    ) {
        if (PatternUpgradeRule.isPatternId(id)) {
            renderPatternFront(id.getPath(), poseStack, buffers, packedLight, packedOverlay);
            return;
        }

        if (PatternUpgradeRule.isBlockId(id)) {
            Block block = BuiltInRegistries.BLOCK.get(id);
            BlockState state = block.defaultBlockState();
            renderBlockPreview(state, poseStack, buffers, packedLight, packedOverlay);
            return;
        }

        // 兜底：未知 id 时可以渲染一个占位 pattern，或者直接不渲染
    }
}