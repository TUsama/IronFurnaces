package ironfurnaces.items.upgrades.furnace_upgrade.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.mojang.serialization.MapCodec;
import ironfurnaces.items.JovialState;
import ironfurnaces.items.upgrades.furnace_upgrade.IUpgradeStorage;
import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.tileentity.furnaces.cache.recipe_type_handlers.SmeltRecipeTypeHandler;
import ironfurnaces.tileentity.furnaces.pattern.render.CubeTextures;
import ironfurnaces.tileentity.furnaces.pattern.render.PatternPreviewTextureResolver;
import ironfurnaces.tileentity.furnaces.pattern.upgrade.PatternUpgradeRule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.function.Consumer;

public final class UpgradeToolSpecialRenderer
        implements SpecialModelRenderer<UpgradeToolSpecialRenderer.UpgradeToolRenderData> {

    public static final UpgradeToolSpecialRenderer INSTANCE = new UpgradeToolSpecialRenderer();

    /*
     * 注意：
     * 这里默认箭头纹理被 stitched 到 BLOCK_ATLAS 里。
     *
     * 推荐路径：
     * assets/ironfurnaces/textures/item/upgrade_arrow.png
     *
     * 如果你仍然使用旧的 IronFurnaces.gui("arrow") 原始 GUI 纹理，
     * 那么 arrow 需要单独提交一个 entityCutoutNoCull 类型的 CustomGeometry。
     */
    private static final Identifier ARROW_SPRITE = IronFurnaces.id("item/upgrade_arrow");

    private UpgradeToolSpecialRenderer() {
    }

    @Override
    public void submit(
            @Nullable UpgradeToolRenderData data,
            PoseStack poseStack,
            SubmitNodeCollector collector,
            int packedLight,
            int packedOverlay,
            boolean hasFoil,
            int outlineColor
    ) {
        if (data == null) {
            return;
        }

        /*
         * GUI 下如果你想保持旧代码的满亮效果，可以强制 packedLight。
         *
         * 但 SpecialModelRenderer.submit 没有 ItemDisplayContext，
         * 所以这里无法像旧 BEWLR 那样只在 GUI 时判断。
         * 如果你希望所有场景都满亮，可以取消下一行注释。
         */
        // packedLight = 0xF000F0;

        collector.submitCustomGeometry(
                poseStack,
                RenderTypes.cutoutMovingBlock(),
                new UpgradeToolGeometry(data, collector, packedLight, packedOverlay)
        );
    }

    @Override
    public @Nullable UpgradeToolRenderData extractArgument(ItemStack stack) {
        PatternUpgradeRule rule = IUpgradeStorage.get(stack);
        if (rule == null) {
            return null;
        }

        return new UpgradeToolRenderData(rule.from(), rule.to());
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        output.accept(new Vector3f(-0.10F, -0.10F, -0.10F));
        output.accept(new Vector3f(1.10F, 1.10F, 1.10F));
    }

    public record UpgradeToolRenderData(
            Identifier from,
            Identifier to
    ) {
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked<UpgradeToolRenderData> {
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(new Unbaked());

        @Override
        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public SpecialModelRenderer<UpgradeToolRenderData> bake(BakingContext context) {
            return INSTANCE;
        }
    }

    private static final class UpgradeToolGeometry implements SubmitNodeCollector.CustomGeometryRenderer {
        private final UpgradeToolRenderData data;
        private final SubmitNodeCollector collector;
        private final int packedLight;
        private final int packedOverlay;

        private UpgradeToolGeometry(
                UpgradeToolRenderData data,
                SubmitNodeCollector collector,
                int packedLight,
                int packedOverlay
        ) {
            this.data = data;
            this.collector = collector;
            this.packedLight = packedLight;
            this.packedOverlay = packedOverlay;
        }

        @Override
        public void render(PoseStack.Pose rootPose, VertexConsumer consumer) {
            renderSideEntry(
                    data.from(),
                    rootPose,
                    consumer,
                    0.28F,
                    0.50F,
                    0.50F
            );

            renderArrow(
                    rootPose,
                    consumer,
                    0.50F,
                    0.50F,
                    0.51F
            );

            renderSideEntry(
                    data.to(),
                    rootPose,
                    consumer,
                    0.72F,
                    0.50F,
                    0.50F
            );
        }

        private void renderSideEntry(
                Identifier id,
                PoseStack.Pose rootPose,
                VertexConsumer consumer,
                float centerX,
                float centerY,
                float centerZ
        ) {
            if (PatternUpgradeRule.isPatternId(id)) {
                PoseStack.Pose pose = rootPose.copy();
                pose.translate(centerX, centerY, centerZ);
                pose.scale(0.32F, 0.32F, 0.32F);

                renderPatternFront(
                        id.getPath(),
                        pose,
                        consumer
                );
                return;
            }

            if (PatternUpgradeRule.isBlockId(id)) {
                renderBlockPreview(
                        id,
                        rootPose,
                        consumer,
                        centerX,
                        centerY,
                        centerZ
                );
            }
        }

        private void renderPatternFront(
                String patternPath,
                PoseStack.Pose pose,
                VertexConsumer consumer
        ) {
            CubeTextures textures = PatternPreviewTextureResolver.resolve(
                    patternPath,
                    false,
                    JovialState.NONE,
                    SmeltRecipeTypeHandler.INSTANCE
            );

            TextureAtlasSprite sprite = blockAtlasSprite(textures.front());
            renderSpriteQuad(
                    sprite,
                    pose,
                    consumer,
                    -0.50F,
                    0.50F,
                    -0.50F,
                    0.50F,
                    0.001F
            );
        }

        private void renderArrow(
                PoseStack.Pose rootPose,
                VertexConsumer consumer,
                float centerX,
                float centerY,
                float centerZ
        ) {
            PoseStack.Pose pose = rootPose.copy();
            pose.translate(centerX, centerY, centerZ);

            TextureAtlasSprite sprite = blockAtlasSprite(ARROW_SPRITE);

            renderSpriteQuad(
                    sprite,
                    pose,
                    consumer,
                    -0.11F,
                    0.11F,
                    -0.11F,
                    0.11F,
                    0.002F
            );
        }

        private void renderBlockPreview(
                Identifier id,
                PoseStack.Pose rootPose,
                VertexConsumer consumer,
                float centerX,
                float centerY,
                float centerZ
        ) {
            Block block = BuiltInRegistries.BLOCK
                    .get(id)
                    .map(reference -> reference.value())
                    .orElse(Blocks.AIR);

            if (block == Blocks.AIR) {
                return;
            }

            BlockState state = block.defaultBlockState();

            PoseStack poseStack = new PoseStack();
            poseStack.last().set(rootPose.copy());

            /*
             * 这里把旧 BEWLR 的 block preview 逻辑迁移到 CustomGeometryRenderer 内部。
             * 注意：因为 CustomGeometryRenderer 只给了一个 VertexConsumer，
             * 所以这里用一个简单 MultiBufferSource 适配器把所有 RenderType 都写进同一个 consumer。
             *
             * 对普通 furnace block preview 足够用。
             * 如果后续遇到复杂 block model 的 RenderType 不对，再改成真正的 BlockModelRenderState 提交。
             */
            poseStack.translate(centerX, centerY, centerZ);
            poseStack.scale(0.42F, 0.42F, 0.42F);
            poseStack.mulPose(Axis.YP.rotationDegrees(195.0F));
            poseStack.translate(-0.50D, -0.50D, -0.50D);

            MultiBufferSource forcedBuffer = renderType -> consumer;
            BlockModelResolver blockModelResolver = Minecraft.getInstance().getBlockModelResolver();
            BlockModelRenderState blockModelRenderState = new BlockModelRenderState();
            blockModelResolver.update(blockModelRenderState, state, BlockDisplayContext.create());
            blockModelRenderState.submit(poseStack, collector, 0, OverlayTexture.NO_OVERLAY, 0);

        }

        private static TextureAtlasSprite blockAtlasSprite(Identifier texture) {
            return Minecraft.getInstance()
                    .getAtlasManager()
                    .getAtlasOrThrow(AtlasIds.BLOCKS)
                    .getSprite(texture);
        }

        private void renderSpriteQuad(
                TextureAtlasSprite sprite,
                PoseStack.Pose pose,
                VertexConsumer consumer,
                float minX,
                float maxX,
                float minY,
                float maxY,
                float z
        ) {
            float u0 = sprite.getU0();
            float u1 = sprite.getU1();
            float v0 = sprite.getV0();
            float v1 = sprite.getV1();

            putVertex(consumer, pose, minX, maxY, z, u0, v0, 0.0F, 0.0F, 1.0F);
            putVertex(consumer, pose, maxX, maxY, z, u1, v0, 0.0F, 0.0F, 1.0F);
            putVertex(consumer, pose, maxX, minY, z, u1, v1, 0.0F, 0.0F, 1.0F);
            putVertex(consumer, pose, minX, minY, z, u0, v1, 0.0F, 0.0F, 1.0F);

            putVertex(consumer, pose, minX, minY, -z, u0, v1, 0.0F, 0.0F, -1.0F);
            putVertex(consumer, pose, maxX, minY, -z, u1, v1, 0.0F, 0.0F, -1.0F);
            putVertex(consumer, pose, maxX, maxY, -z, u1, v0, 0.0F, 0.0F, -1.0F);
            putVertex(consumer, pose, minX, maxY, -z, u0, v0, 0.0F, 0.0F, -1.0F);
        }

        private void putVertex(
                VertexConsumer consumer,
                PoseStack.Pose pose,
                float x,
                float y,
                float z,
                float u,
                float v,
                float normalX,
                float normalY,
                float normalZ
        ) {
            consumer.addVertex(pose, x, y, z)
                    .setColor(255, 255, 255, 255)
                    .setUv(u, v)
                    .setOverlay(packedOverlay)
                    .setLight(packedLight)
                    .setNormal(pose, normalX, normalY, normalZ);
        }
    }
}