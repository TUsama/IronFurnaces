package ironfurnaces.tileentity.furnaces.pattern.render;

import com.mojang.blaze3d.vertex.PoseStack;
import ironfurnaces.items.JovialState;
import ironfurnaces.registration.ModBlockState;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.cache.recipe_type_handlers.SmeltRecipeTypeHandler;
import ironfurnaces.tileentity.furnaces.pattern.FurnacePattern;
import ironfurnaces.tileentity.furnaces.pattern.render.refactor.FurnacePatternBlockEntityRenderState;
import ironfurnaces.tileentity.furnaces.pattern.render.refactor.FurnacePatternRenderData;
import ironfurnaces.tileentity.furnaces.pattern.render.refactor.FurnacePatternVisualSubmitter;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class PatternHolderBlockEntityRenderer implements BlockEntityRenderer<FurnacePatternBlockEntity, FurnacePatternBlockEntityRenderState> {
    public static PatternHolderBlockEntityRenderer INSTANCE;

    public static PatternHolderBlockEntityRenderer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PatternHolderBlockEntityRenderer();
        }
        return INSTANCE;
    }

    @Override
    public FurnacePatternBlockEntityRenderState createRenderState() {
        return new FurnacePatternBlockEntityRenderState();
    }

    @Override
    public void extractRenderState(FurnacePatternBlockEntity blockEntity, FurnacePatternBlockEntityRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        BlockState blockState = blockEntity.getBlockState();
        state.data = new FurnacePatternRenderData(blockEntity.getPattern().id(), blockState.getValue(BlockStateProperties.LIT), blockState.getValue(BlockStateProperties.HORIZONTAL_FACING), blockState.getValue(ModBlockState.JOVIAL_STATE), blockEntity.getAugments().getCurrentRecipeType());
    }

    @Override
    public void submit(FurnacePatternBlockEntityRenderState furnacePatternBlockEntityRenderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        FurnacePatternVisualSubmitter.submit(furnacePatternBlockEntityRenderState.data, poseStack, submitNodeCollector, furnacePatternBlockEntityRenderState.lightCoords, OverlayTexture.NO_OVERLAY, 0);
    }

}

