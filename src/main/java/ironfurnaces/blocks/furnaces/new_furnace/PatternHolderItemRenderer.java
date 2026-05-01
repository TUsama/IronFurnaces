package ironfurnaces.blocks.furnaces.new_furnace;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.MapCodec;
import ironfurnaces.items.JovialState;
import ironfurnaces.items.upgrades.furnace_pattern.IPatternAccessor;
import ironfurnaces.tileentity.furnaces.cache.recipe_type_handlers.SmeltRecipeTypeHandler;
import ironfurnaces.tileentity.furnaces.pattern.FurnacePattern;
import ironfurnaces.tileentity.furnaces.pattern.render.PatternHolderVisualRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
//? <1.21.11 {
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;
//?} else {
/*import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
*///?}


//~ if > 1.21.11 'extends' -> 'implements'
public class PatternHolderItemRenderer extends BlockEntityWithoutLevelRenderer {

    public static final PatternHolderItemRenderer INSTANCE =
            new PatternHolderItemRenderer();

    public PatternHolderItemRenderer() {
        //? <1.21.11 {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());
        //?}
    }
    //? <1.21.11 {
    @Override
    public void renderByItem(
            @NotNull ItemStack stack,
            @NotNull ItemDisplayContext displayContext,
            @NotNull PoseStack poseStack,
            @NotNull MultiBufferSource buffers,
            int packedLight,
            int packedOverlay
    ) {
        FurnacePattern pattern = IPatternAccessor.getFurnacePatternFromTag(stack);
        if (pattern == null) {
            return;
        }

        poseStack.pushPose();

        // GUI/手持/地面展示需要单独调姿态
        setupDisplayTransform(displayContext, poseStack);

        PatternHolderVisualRenderer.render(
                pattern.id().getPath(),
                false,
                net.minecraft.core.Direction.NORTH,
                JovialState.NONE,
                SmeltRecipeTypeHandler.INSTANCE,
                poseStack,
                buffers,
                packedLight,
                packedOverlay
        );

        poseStack.popPose();
    }
    //?} else {
    /*@Override
    public void submit(@Nullable FurnacePattern pattern, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int i, int i1, boolean b, int i2) {

    }

    public void getExtents(Consumer<Vector3fc> output) {
        output.accept(new Vector3f(-0.25F, -0.25F, -0.25F));
        output.accept(new Vector3f(1.25F, 1.25F, 1.25F));
    }

    @Override
    public @Nullable FurnacePattern extractArgument(ItemStack itemStack) {
        return null;
    }


    public record Unbaked() implements SpecialModelRenderer.Unbaked {
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(new Unbaked());

        @Override
        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public SpecialModelRenderer<?> bake(BakingContext context) {
            return new PatternHolderItemRenderer();
        }
    }
    *///?}




    private static void setupDisplayTransform(ItemDisplayContext displayContext, PoseStack poseStack) {
        switch (displayContext) {
            case GUI -> {
                poseStack.translate(0.5D, 0.5D, 0.5D);

                // 先把正方体转成常见的物品展示角度
                poseStack.mulPose(Axis.XP.rotationDegrees(25.0F));
                poseStack.mulPose(Axis.YP.rotationDegrees(-135.0F));

                // 适当缩小，避免贴边
                float v = 0.65F;
                poseStack.scale(v, v, v);

                // renderCube 是以 0..1 的 block 空间绘制，
                // 所以这里再平移回局部原点
                poseStack.translate(-0.5D, -0.5D, -0.5D);
            }
            case GROUND -> {
                poseStack.translate(0.5D, 0.5D, 0.5D);
                poseStack.mulPose(Axis.YP.rotationDegrees(0.0F));
                poseStack.scale(0.42F, 0.42F, 0.42F);
                poseStack.translate(-0.5D, -0.5D, -0.5D);
            }

            case FIXED -> {
                poseStack.translate(0.5D, 0.5D, 0.5D);
                poseStack.mulPose(Axis.XP.rotationDegrees(15.0F));
                poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
                poseStack.scale(0.65F, 0.65F, 0.65F);
                poseStack.translate(-0.5D, -0.5D, -0.5D);
            }

            case FIRST_PERSON_LEFT_HAND, FIRST_PERSON_RIGHT_HAND -> {
                poseStack.translate(0.5D, 0.35D, 0.5D);
                poseStack.mulPose(Axis.XP.rotationDegrees(10.0F));
                //poseStack.mulPose(Axis.YP.rotationDegrees(225.0F));
                poseStack.scale(0.48F, 0.48F, 0.48F);
                poseStack.translate(-0.5D, -0.5D, -0.5D);
            }

            case THIRD_PERSON_LEFT_HAND, THIRD_PERSON_RIGHT_HAND -> {
                poseStack.translate(0.5D, 0.25D, 0.5D);
                //poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
                poseStack.mulPose(Axis.YP.rotationDegrees(45.0F));
                poseStack.scale(0.38F, 0.38F, 0.38F);
                poseStack.translate(-0.5D, -0.5D, -0.5D);
            }

            default -> {
                poseStack.translate(0.5D, 0.5D, 0.5D);
                poseStack.scale(0.6F, 0.6F, 0.6F);
                poseStack.translate(-0.5D, -0.5D, -0.5D);
            }
        }
    }
}