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
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
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

    /**
     * 对应文件：
     * resources/assets/ironfurnaces/textures/gui/arrow.png
     *
     * 注意：这里传给 RenderType 的是完整纹理路径，不是 atlas sprite id。
     */
    private static final Identifier ARROW_TEXTURE =
            IronFurnaces.id("textures/gui/arrow.png");

    private static final float LEFT_ENTRY_X = 0.20F;
    private static final float RIGHT_ENTRY_X = 0.80F;
    private static final float ENTRY_Y = 0.50F;
    private static final float ENTRY_Z = 0.50F;

    private static final float ARROW_X = 0.50F;
    private static final float ARROW_Y = 0.50F;
    private static final float ARROW_Z = 0.51F;

    private static final float PATTERN_FRONT_SCALE = 0.32F;
    private static final float BLOCK_PREVIEW_SCALE = 0.36F;

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

        submitSideEntry(
                data.from(),
                poseStack,
                collector,
                packedLight,
                packedOverlay,
                outlineColor,
                LEFT_ENTRY_X,
                ENTRY_Y,
                ENTRY_Z
        );

        collector.submitCustomGeometry(
                poseStack,
                RenderTypes.entityCutout(ARROW_TEXTURE),
                new ArrowGeometry(
                        packedLight,
                        packedOverlay,
                        ARROW_X,
                        ARROW_Y,
                        ARROW_Z
                )
        );

        submitSideEntry(
                data.to(),
                poseStack,
                collector,
                packedLight,
                packedOverlay,
                outlineColor,
                RIGHT_ENTRY_X,
                ENTRY_Y,
                ENTRY_Z
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

    private static void submitSideEntry(
            Identifier id,
            PoseStack poseStack,
            SubmitNodeCollector collector,
            int packedLight,
            int packedOverlay,
            int outlineColor,
            float centerX,
            float centerY,
            float centerZ
    ) {
        if (PatternUpgradeRule.isPatternId(id)) {
            collector.submitCustomGeometry(
                    poseStack,
                    RenderTypes.cutoutMovingBlock(),
                    new PatternFrontGeometry(
                            id.getPath(),
                            packedLight,
                            packedOverlay,
                            centerX,
                            centerY,
                            centerZ
                    )
            );
            return;
        }

        if (PatternUpgradeRule.isBlockId(id)) {
            submitBlockPreview(
                    id,
                    poseStack,
                    collector,
                    packedLight,
                    packedOverlay,
                    outlineColor,
                    centerX,
                    centerY,
                    centerZ
            );
        }
    }

    private static void submitBlockPreview(
            Identifier id,
            PoseStack sourcePoseStack,
            SubmitNodeCollector collector,
            int packedLight,
            int packedOverlay,
            int outlineColor,
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

        PoseStack poseStack = copyPoseStack(sourcePoseStack);

        poseStack.translate(centerX, centerY, centerZ);
        poseStack.scale(BLOCK_PREVIEW_SCALE, BLOCK_PREVIEW_SCALE, BLOCK_PREVIEW_SCALE);
        poseStack.mulPose(Axis.YP.rotationDegrees(195.0F));
        poseStack.translate(-0.50D, -0.50D, -0.50D);

        BlockModelResolver blockModelResolver =
                Minecraft.getInstance().getBlockModelResolver();

        BlockModelRenderState blockModelRenderState =
                new BlockModelRenderState();

        blockModelResolver.update(
                blockModelRenderState,
                state,
                BlockDisplayContext.create()
        );

        blockModelRenderState.submit(
                poseStack,
                collector,
                packedLight,
                packedOverlay,
                outlineColor
        );
    }

    private static PoseStack copyPoseStack(PoseStack source) {
        PoseStack copy = new PoseStack();
        copy.last().set(source.last().copy());
        return copy;
    }

    private static final class PatternFrontGeometry implements SubmitNodeCollector.CustomGeometryRenderer {
        private final String patternPath;
        private final int packedLight;
        private final int packedOverlay;
        private final float centerX;
        private final float centerY;
        private final float centerZ;

        private PatternFrontGeometry(
                String patternPath,
                int packedLight,
                int packedOverlay,
                float centerX,
                float centerY,
                float centerZ
        ) {
            this.patternPath = patternPath;
            this.packedLight = packedLight;
            this.packedOverlay = packedOverlay;
            this.centerX = centerX;
            this.centerY = centerY;
            this.centerZ = centerZ;
        }

        @Override
        public void render(PoseStack.Pose rootPose, VertexConsumer consumer) {
            PoseStack.Pose pose = rootPose.copy();
            pose.translate(centerX, centerY, centerZ);
            pose.scale(0.32F, 0.32F, 0.32F);

            CubeTextures textures = PatternPreviewTextureResolver.resolve(
                    patternPath,
                    false,
                    JovialState.NONE,
                    SmeltRecipeTypeHandler.INSTANCE
            );

            TextureAtlasSprite sprite = blockAtlasSprite(textures.front());

            renderAtlasSpriteQuad(
                    sprite,
                    pose,
                    consumer,
                    packedLight,
                    packedOverlay,
                    -0.50F,
                    0.50F,
                    -0.50F,
                    0.50F,
                    0.001F
            );
        }
    }

    private static final class ArrowGeometry implements SubmitNodeCollector.CustomGeometryRenderer {
        private final int packedLight;
        private final int packedOverlay;
        private final float centerX;
        private final float centerY;
        private final float centerZ;

        private ArrowGeometry(
                int packedLight,
                int packedOverlay,
                float centerX,
                float centerY,
                float centerZ
        ) {
            this.packedLight = packedLight;
            this.packedOverlay = packedOverlay;
            this.centerX = centerX;
            this.centerY = centerY;
            this.centerZ = centerZ;
        }

        @Override
        public void render(PoseStack.Pose rootPose, VertexConsumer consumer) {
            PoseStack.Pose pose = rootPose.copy();
            pose.translate(centerX, centerY, centerZ);

            renderRawTextureQuad(
                    pose,
                    consumer,
                    packedLight,
                    packedOverlay,
                    -0.11F,
                    0.11F,
                    -0.11F,
                    0.11F,
                    0.002F
            );
        }
    }

    private static TextureAtlasSprite blockAtlasSprite(Identifier texture) {
        return Minecraft.getInstance()
                .getAtlasManager()
                .getAtlasOrThrow(AtlasIds.BLOCKS)
                .getSprite(texture);
    }

    private static void renderAtlasSpriteQuad(
            TextureAtlasSprite sprite,
            PoseStack.Pose pose,
            VertexConsumer consumer,
            int packedLight,
            int packedOverlay,
            float minX,
            float maxX,
            float minY,
            float maxY,
            float z
    ) {
        renderQuad(
                pose,
                consumer,
                packedLight,
                packedOverlay,
                minX,
                maxX,
                minY,
                maxY,
                z,
                sprite.getU0(),
                sprite.getU1(),
                sprite.getV0(),
                sprite.getV1()
        );
    }

    private static void renderRawTextureQuad(
            PoseStack.Pose pose,
            VertexConsumer consumer,
            int packedLight,
            int packedOverlay,
            float minX,
            float maxX,
            float minY,
            float maxY,
            float z
    ) {
        renderQuad(
                pose,
                consumer,
                packedLight,
                packedOverlay,
                minX,
                maxX,
                minY,
                maxY,
                z,
                0.0F,
                1.0F,
                0.0F,
                1.0F
        );
    }

    private static void renderQuad(
            PoseStack.Pose pose,
            VertexConsumer consumer,
            int packedLight,
            int packedOverlay,
            float minX,
            float maxX,
            float minY,
            float maxY,
            float z,
            float u0,
            float u1,
            float v0,
            float v1
    ) {
        putVertex(consumer, pose, packedLight, packedOverlay, minX, maxY, z, u0, v0, 0.0F, 0.0F, 1.0F);
        putVertex(consumer, pose, packedLight, packedOverlay, maxX, maxY, z, u1, v0, 0.0F, 0.0F, 1.0F);
        putVertex(consumer, pose, packedLight, packedOverlay, maxX, minY, z, u1, v1, 0.0F, 0.0F, 1.0F);
        putVertex(consumer, pose, packedLight, packedOverlay, minX, minY, z, u0, v1, 0.0F, 0.0F, 1.0F);

        putVertex(consumer, pose, packedLight, packedOverlay, minX, minY, -z, u0, v1, 0.0F, 0.0F, -1.0F);
        putVertex(consumer, pose, packedLight, packedOverlay, maxX, minY, -z, u1, v1, 0.0F, 0.0F, -1.0F);
        putVertex(consumer, pose, packedLight, packedOverlay, maxX, maxY, -z, u1, v0, 0.0F, 0.0F, -1.0F);
        putVertex(consumer, pose, packedLight, packedOverlay, minX, maxY, -z, u0, v0, 0.0F, 0.0F, -1.0F);
    }

    private static void putVertex(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            int packedLight,
            int packedOverlay,
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