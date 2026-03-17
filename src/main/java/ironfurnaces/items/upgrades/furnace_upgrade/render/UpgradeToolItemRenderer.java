package ironfurnaces.items.upgrades.furnace_upgrade.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import ironfurnaces.items.JovialState;
import ironfurnaces.items.upgrades.furnace_upgrade.IUpgradeStorage;
import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.tileentity.furnaces.cache.AugmentCache;
import ironfurnaces.tileentity.furnaces.pattern.render.PatternRenderCache;
import ironfurnaces.tileentity.furnaces.pattern.upgrade.PatternUpgradeRule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class UpgradeToolItemRenderer extends BlockEntityWithoutLevelRenderer {
    private static final BlockState DUMMY_STATE = Blocks.FURNACE.defaultBlockState();

    public UpgradeToolItemRenderer() {
        super(
                Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels()
        );
    }

    private static void setupRootTransform(ItemDisplayContext displayContext, PoseStack poseStack) {
        // 第一版统一视觉，后续再按 GUI/HAND/FIXED 分开调
        poseStack.translate(0.0D, 0.08D, 0.0D);
        poseStack.mulPose(Axis.XP.rotationDegrees(15.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(-25.0F));
    }

    private static void renderMiniPatternModel(
            BakedModel model,
            PoseStack poseStack,
            MultiBufferSource buffers,
            int packedLight,
            int packedOverlay
    ) {
        Minecraft mc = Minecraft.getInstance();
        BlockRenderDispatcher dispatcher = mc.getBlockRenderer();

        System.out.println(111);

        poseStack.pushPose();

        // 以 block 中心旋转，和你 BE renderer 的思路一致
        poseStack.translate(0.5D, 0.0D, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        poseStack.translate(-0.5D, 0.0D, -0.5D);

        dispatcher.getModelRenderer().renderModel(
                poseStack.last(),
                buffers.getBuffer(RenderType.solid()),
                DUMMY_STATE,
                model,
                1.0F, 1.0F, 1.0F,
                packedLight,
                packedOverlay
        );

        poseStack.popPose();
    }

    private static void renderArrowPlaceholder(
            PoseStack poseStack,
            MultiBufferSource buffers,
            int packedLight,
            int packedOverlay
    ) {
        // 这里只做占位，不直接写死具体顶点实现
        // 你后面可以替换成：
        // 1. 一张 item/upgrade_arrow 贴图
        // 2. 或一个单独的小 model
        // 3. 或手写 quad
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

        Minecraft mc = Minecraft.getInstance();
        ModelManager modelManager = mc.getModelManager();

        // 第一版固定使用未点亮、无节日皮肤、NORMAL 类型
        // 因为升级工具展示的是“pattern 升级关系”，不是当前方块运行态
        BakedModel fromModel = PatternRenderCache.getBakedCached(
                IronFurnaces.MOD_ID,
                modelManager,
                rule.from().getPath(),
                false,
                JovialState.NONE,
                AugmentCache.HandlingRecipeType.NORMAL
        );

        BakedModel toModel = PatternRenderCache.getBakedCached(
                IronFurnaces.MOD_ID,
                modelManager,
                rule.to().getPath(),
                false,
                JovialState.NONE,
                AugmentCache.HandlingRecipeType.NORMAL
        );

        poseStack.pushPose();

        setupRootTransform(displayContext, poseStack);

        // 左侧 from
        poseStack.pushPose();
        poseStack.translate(-0.6D, -0.12D, 0.0D);
        poseStack.scale(0.42F, 0.42F, 0.42F);
        renderMiniPatternModel(fromModel, poseStack, buffers, packedLight, packedOverlay);
        poseStack.popPose();

        // 中间箭头占位
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.0D, 0.25D);
        renderArrowPlaceholder(poseStack, buffers, packedLight, packedOverlay);
        poseStack.popPose();

        // 右侧 to
        poseStack.pushPose();
        poseStack.translate(0.6D, -0.12D, 0.0D);
        poseStack.scale(0.42F, 0.42F, 0.42F);
        renderMiniPatternModel(toModel, poseStack, buffers, packedLight, packedOverlay);
        poseStack.popPose();

        poseStack.popPose();
    }
}