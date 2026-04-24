package ironfurnaces.tileentity.furnaces.pattern.render;

import com.mojang.blaze3d.vertex.PoseStack;
import ironfurnaces.items.JovialState;
import ironfurnaces.registration.ModBlockState;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.cache.recipe_type_handlers.SmeltRecipeTypeHandler;
import ironfurnaces.tileentity.furnaces.pattern.FurnacePattern;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class PatternHolderBlockEntityRenderer implements BlockEntityRenderer<FurnacePatternBlockEntity> {
    public static PatternHolderBlockEntityRenderer INSTANCE;

    public static PatternHolderBlockEntityRenderer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PatternHolderBlockEntityRenderer();
        }
        return INSTANCE;
    }

    @Override
    public void render(
            FurnacePatternBlockEntity be,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource buffers,
            int packedLight,
            int packedOverlay
    ) {
        if (be.getLevel() == null) return;

        FurnacePattern pattern = be.getPattern();
        if (pattern == null) return;

        BlockState state = be.getBlockState();

        boolean lit = state.getValue(BlockStateProperties.LIT);
        Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        JovialState jovial = state.getValue(ModBlockState.JOVIAL_STATE);
        poseStack.pushPose();

        //int light = LevelRenderer.getLightColor(be.getLevel(), be.getBlockPos().above());
        PatternHolderVisualRenderer.render(
                pattern.id().getPath(),
                lit,
                facing,
                jovial,
                be.getAugments().getCurrentRecipeType(),
                poseStack,
                buffers,
                packedLight,
                packedOverlay
        );

        poseStack.popPose();
    }

}

