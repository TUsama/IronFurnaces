package ironfurnaces.tileentity.furnaces.pattern.render.refactor;

import com.mojang.blaze3d.vertex.PoseStack;
import ironfurnaces.items.JovialState;
import ironfurnaces.registration.ModBlockState;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.cache.IRecipeTypeHandler;
import ironfurnaces.tileentity.furnaces.pattern.FurnacePattern;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public final class FurnacePatternBlockEntityRenderer
        implements BlockEntityRenderer<FurnacePatternBlockEntity, FurnacePatternBlockEntityRenderState> {

    public FurnacePatternBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        // 这里先留空。
        // 如果后续使用 ModelPart、ModelSet、BlockRenderDispatcher，可以从 context 或 Minecraft 实例取。
    }

    @Override
    public FurnacePatternBlockEntityRenderState createRenderState() {
        return new FurnacePatternBlockEntityRenderState();
    }

    @Override
    public void extractRenderState(
            FurnacePatternBlockEntity be,
            FurnacePatternBlockEntityRenderState state,
            float partialTick,
            Vec3 cameraPosition,
            @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay
    ) {
        BlockEntityRenderer.super.extractRenderState(
                be,
                state,
                partialTick,
                cameraPosition,
                crumblingOverlay
        );

        FurnacePattern pattern = be.getPattern();
        if (pattern == null) {
            state.clear();
            return;
        }

        BlockState blockState = be.getBlockState();

        Direction facing = blockState.hasProperty(HorizontalDirectionalBlock.FACING)
                ? blockState.getValue(HorizontalDirectionalBlock.FACING)
                : Direction.NORTH;

        boolean lit = blockState.hasProperty(BlockStateProperties.LIT)
                && blockState.getValue(BlockStateProperties.LIT);

        JovialState jovial = resolveJovialState(blockState);

        IRecipeTypeHandler recipeType = resolveRecipeType(be, blockState);

        state.data = new FurnacePatternRenderData(
                pattern.id(),
                lit,
                facing,
                jovial,
                recipeType
        );
    }

    @Override
    public void submit(
            FurnacePatternBlockEntityRenderState state,
            PoseStack poseStack,
            SubmitNodeCollector collector,
            CameraRenderState cameraRenderState
    ) {
        if (state.data == null) {
            return;
        }

        FurnacePatternVisualSubmitter.submit(
                state.data,
                poseStack,
                collector,
                state.lightCoords,
                OverlayTexture.NO_OVERLAY,
                0
        );
    }

    private static JovialState resolveJovialState(BlockState state) {
        return state.hasProperty(ModBlockState.JOVIAL_STATE)
                ? state.getValue(ModBlockState.JOVIAL_STATE)
                : JovialState.NONE;
    }

    private static IRecipeTypeHandler resolveRecipeType(
            FurnacePatternBlockEntity be,
            BlockState state
    ) {
        return be.getAugments().getCurrentRecipeType();
    }
}
