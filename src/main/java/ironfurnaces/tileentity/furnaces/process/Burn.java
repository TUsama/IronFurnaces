package ironfurnaces.tileentity.furnaces.process;

import com.mojang.datafixers.util.Function5;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.Util;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.Map;

@Getter(value = AccessLevel.PROTECTED)
public abstract class Burn extends ProcessingInstance {
    private static final Map<RecipeType<?>, Factory> idMap = Util.make(() -> Map.of(
            RecipeType.BLASTING, Blasting::new,
            RecipeType.SMOKING, Smoking::new,
            RecipeType.SMELTING, Smelting::new
    ));

    protected final int expectedTick;
    protected int currentTick = 0;
    private float partialProgress = 0.0f;

    protected Burn(int inputIndex, int expectedTick) {
        this(inputIndex, false, expectedTick, 0, 0.0f);

    }

    private Burn(int fromIndex, boolean handledStart, int expectedTick, int currentTick, float partialProgress) {
        super(fromIndex, handledStart);
        this.expectedTick = expectedTick;
        this.currentTick = currentTick;
        this.partialProgress = partialProgress;
    }

    protected static <T extends Burn> MapCodec<T> simpleBurnCodec(Function5<Integer, Boolean, Integer, Integer, Float, T> factory) {
        return RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        ExtraCodecs.POSITIVE_INT.fieldOf("fromIndex").forGetter(Burn::getFromIndex),
                        Codec.BOOL.fieldOf("handledStart").forGetter(Burn::isHandledStart),
                        ExtraCodecs.POSITIVE_INT.fieldOf("expectedTick").forGetter(Burn::getExpectedTick),
                        ExtraCodecs.POSITIVE_INT.fieldOf("currentTick").forGetter(Burn::getCurrentTick),
                        ExtraCodecs.POSITIVE_FLOAT.fieldOf("partialProgress").forGetter(Burn::getPartialProgress)
                ).apply(instance, factory));
    }


    public static Burn create(int expectedTick, int inputIndex, Recipe<Container> recipe) {
        Factory factory = idMap.get(recipe.getType());
        if (factory != null) {
            return factory.create(inputIndex, expectedTick);
        }
        throw new RuntimeException("fail at creating Burn instance with RecipeType: " + recipe.getType());
    }


    @Override
    public boolean needLit(FurnacePatternBlockEntity tile) {
        Recipe recipe = tile.getInstanceManager()
                .getCachedCookingRecipe(tile, this);
        if (recipe == null) return false;
        ItemStack resultItem = recipe.getResultItem(tile.getLevel().registryAccess());
        ItemStack itemStack = tile.getOutput().insertItem(fromIndex, resultItem, true);
        return itemStack.isEmpty();
    }

    @Override
    public void whenStart(FurnacePatternBlockEntity tile) {

    }

    @Override
    public void whenDone(FurnacePatternBlockEntity tile) {
        super.whenDone(tile);
        Level level = tile.getLevel();
        tile.getRecipe(tile.getInput().getStackInSlot(fromIndex)).ifPresent(x -> {
            ItemStack resultItem = x.getResultItem(level.registryAccess()).copy();
            //可以确保这里是能完全存入的，因为whenDone会在whenTick后直接执行，而whenTick确保了有空位。
            tile.getOutput().insertItem(fromIndex, resultItem, false);
            tile.getInput().extractItem(fromIndex, 1, false);
            tile.setRecipeUsed(x);
        });

    }

    @Override
    public TickResult whenTick(FurnacePatternBlockEntity tile) {
        if (!validateSlot(tile.getInput(), tile)) return TickResult.DISCARD;
        ItemStack stackInSlot = tile.getInput().getStackInSlot(fromIndex);
        if (stackInSlot.isEmpty()) return TickResult.DISCARD;
        Recipe recipe = tile.getInstanceManager()
                .getCachedCookingRecipe(tile, this);
        if (recipe == null) return TickResult.DISCARD;
        ItemStack resultItem = recipe.getResultItem(tile.getLevel().registryAccess());
        ItemStack itemStack = tile.getOutput().insertItem(fromIndex, resultItem, true);
        if (itemStack.isEmpty()) {
            currentTick++;
            partialProgress = tile.getAugments().getCurrentModifiers().normalWorkTimeModifier().apply(partialProgress);
            int whole = (int) partialProgress;
            currentTick += whole;
            partialProgress -= whole;
            if (currentTick >= expectedTick) {
                return TickResult.DONE;
            }
            return TickResult.SUCCESS;
        } else {
            return TickResult.BLOCKED;
        }

    }

    @Override
    public float getDoneProgress() {
        return currentTick / (expectedTick * 1.0f);
    }

    @FunctionalInterface
    public interface Factory {
        Burn create(int inputIndex, int expectedTick);
    }

    public static class Smelting extends Burn {
        public static final MapCodec<Smelting> CODEC = simpleBurnCodec(Smelting::new);

        public static final String TYPE = "smelting";

        private Smelting(int inputIndex, int expectedTick) {
            super(inputIndex, expectedTick);
        }

        private Smelting(int fromIndex, boolean handledStart, int expectedTick, int currentTick, float partialProgress) {
            super(fromIndex, handledStart, expectedTick, currentTick, partialProgress);
        }

        @Override
        public String getType() {
            return TYPE;
        }
    }

    public static class Smoking extends Burn {
        public static final MapCodec<Smoking> CODEC = simpleBurnCodec(Smoking::new);

        public static final String TYPE = "smoking";

        private Smoking(int inputIndex, int expectedTick) {
            super(inputIndex, expectedTick);
        }

        private Smoking(int fromIndex, boolean handledStart, int expectedTick, int currentTick, float partialProgress) {
            super(fromIndex, handledStart, expectedTick, currentTick, partialProgress);
        }

        @Override
        public String getType() {
            return TYPE;
        }
    }

    public static class Blasting extends Burn {

        public static final MapCodec<Blasting> CODEC = simpleBurnCodec(Blasting::new);

        public static final String TYPE = "blasting";

        private Blasting(int inputIndex, int expectedTick) {
            super(inputIndex, expectedTick);
        }

        private Blasting(int fromIndex, boolean handledStart, int expectedTick, int currentTick, float partialProgress) {
            super(fromIndex, handledStart, expectedTick, currentTick, partialProgress);
        }

        @Override
        public String getType() {
            return TYPE;
        }
    }

}
