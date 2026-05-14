package ironfurnaces.tileentity.furnaces.pattern.render.refactor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import ironfurnaces.items.JovialState;
import ironfurnaces.items.upgrades.furnace_pattern.IPatternAccessor;
import ironfurnaces.tileentity.furnaces.cache.recipe_type_handlers.SmeltRecipeTypeHandler;
import ironfurnaces.tileentity.furnaces.pattern.FurnacePattern;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.function.Consumer;

public final class FurnacePatternHolderSpecialRenderer
        implements SpecialModelRenderer<FurnacePatternRenderData> {

    public static final FurnacePatternHolderSpecialRenderer INSTANCE =
            new FurnacePatternHolderSpecialRenderer();

    private FurnacePatternHolderSpecialRenderer() {
    }

    @Override
    public void submit(
            @Nullable FurnacePatternRenderData data,
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

        FurnacePatternVisualSubmitter.submit(
                data,
                poseStack,
                collector,
                packedLight,
                packedOverlay,
                outlineColor
        );
    }

    @Override
    public @Nullable FurnacePatternRenderData extractArgument(ItemStack stack) {
        FurnacePattern pattern = IPatternAccessor.getFurnacePatternFromTag(stack);
        if (pattern == null) {
            return null;
        }

        return new FurnacePatternRenderData(
                pattern.id(),
                false,
                Direction.NORTH,
                JovialState.NONE,
                SmeltRecipeTypeHandler.INSTANCE
        );
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        output.accept(new Vector3f(-0.25F, -0.25F, -0.25F));
        output.accept(new Vector3f(1.25F, 1.25F, 1.25F));
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked<FurnacePatternRenderData> {
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(new Unbaked());

        @Override
        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public SpecialModelRenderer<FurnacePatternRenderData> bake(BakingContext context) {
            return INSTANCE;
        }
    }
}