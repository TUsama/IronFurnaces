package ironfurnaces.tileentity.furnaces.process;

import com.mojang.datafixers.util.Function5;
import com.mojang.datafixers.util.Function6;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.util.CompatUtil;
import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.util.Util;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.Map;

@Getter(value = AccessLevel.PROTECTED)
public abstract class Burn extends ProcessingInstance {


    protected final int expectedTick;
    protected int currentTick = 0;
    private float partialProgress = 0.0f;
    private int batch = 1;

    protected Burn(int inputIndex, int expectedTick, int batch) {
        this(inputIndex, false, expectedTick, 0, 0.0f, batch);
    }

    private Burn(int fromIndex, boolean handledStart, int expectedTick, int currentTick, float partialProgress, int batch) {
        super(fromIndex, handledStart);
        this.expectedTick = expectedTick;
        this.currentTick = currentTick;
        this.partialProgress = partialProgress;
        this.batch = batch;
    }

    protected static <T extends Burn> MapCodec<T> simpleBurnCodec(Function6<Integer, Boolean, Integer, Integer, Float, Integer, T> factory) {
        return RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        ExtraCodecs.POSITIVE_INT.fieldOf("fromIndex").forGetter(Burn::getFromIndex),
                        Codec.BOOL.fieldOf("handledStart").forGetter(Burn::isHandledStart),
                        ExtraCodecs.POSITIVE_INT.fieldOf("expectedTick").forGetter(Burn::getExpectedTick),
                        ExtraCodecs.POSITIVE_INT.fieldOf("currentTick").forGetter(Burn::getCurrentTick),
                        Codec.FLOAT.fieldOf("partialProgress").forGetter(Burn::getPartialProgress),
                        ExtraCodecs.POSITIVE_INT.fieldOf("batch").forGetter(Burn::getBatch)
                ).apply(instance, factory));
    }




    @Override
    public boolean needLit(FurnacePatternBlockEntity tile) {
        Recipe recipe = tile.getInstanceManager()
                .getCachedCookingRecipe(tile, this);
        if (recipe == null) return false;
        ItemStack resultItem = recipe.getResultItem(tile.getLevel().registryAccess());
        ItemStack itemStack = tile.getOutput().insertItem(fromIndex, resultItem.copyWithCount(batch * resultItem.getCount()), true);
        return itemStack.isEmpty();
    }

    @Override
    public void whenStart(FurnacePatternBlockEntity tile) {

    }

    @Override
    public void whenDone(FurnacePatternBlockEntity tile) {
        super.whenDone(tile);
        Level level = tile.getLevel();
        ItemStack stackInSlot = tile.getInput().getStackInSlot(fromIndex);
        tile.getRecipe(stackInSlot).ifPresent(x -> {
            //~ if >1.20.1 'x.getResultItem' -> 'x.value().getResultItem'
            ItemStack resultItem = x.value().getResultItem(level.registryAccess()).copy();
            //可以确保这里是能完全存入的，因为whenDone会在whenTick后直接执行，而whenTick确保了有空位。
            resultItem.setCount(resultItem.getCount() * batch);
            tile.getOutput().insertItem(fromIndex, resultItem, false);
            tile.getInput().extractItem(fromIndex, batch, false);
            for (int i = 0; i < batch; i++) {
                tile.setRecipeUsed(x);
            }
            ItemStack itemStack = new ItemStack(stackInSlot.getItem(), batch);
            CompatUtil.firePmmoSmeltedEvent(itemStack, resultItem,tile.getLevel(), tile.getBlockPos());
            if (tile.getOwner() != null) {
                CompatUtil.handleVanillaWhenHasPlayer(tile.getOwner(), itemStack);
            } else {
                CompatUtil.handleVanillaWhenWithoutPlayer(resultItem, level);
            }

        });

    }

    @Override
    public TickResult whenTick(FurnacePatternBlockEntity tile) {
        if (!validateSlot(tile.getInput(), tile)) return TickResult.DISCARD;
        ItemStack stackInSlot = tile.getInput().getStackInSlot(fromIndex);
        if (stackInSlot.isEmpty()) return TickResult.DISCARD;
        if (batch > stackInSlot.getCount()) batch = stackInSlot.getCount();
        Recipe recipe = tile.getInstanceManager()
                .getCachedCookingRecipe(tile, this);
        if (recipe == null) return TickResult.DISCARD;
        ItemStack resultItem = recipe.getResultItem(tile.getLevel().registryAccess()).copy();
        resultItem.setCount(resultItem.getCount() * batch);
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


    public static class Smelting extends Burn {
        public static final MapCodec<Smelting> CODEC = simpleBurnCodec(Smelting::new);

        public static final String TYPE = "smelting";

        public Smelting(int inputIndex, int expectedTick, int batch) {
            super(inputIndex, expectedTick, batch);
        }

        private Smelting(int fromIndex, boolean handledStart, int expectedTick, int currentTick, float partialProgress, int batch) {
            super(fromIndex, handledStart, expectedTick, currentTick, partialProgress, batch);
        }

        @Override
        public String getType() {
            return TYPE;
        }
    }

    public static class Smoking extends Burn {
        public static final MapCodec<Smoking> CODEC = simpleBurnCodec(Smoking::new);

        public static final String TYPE = "smoking";

        public Smoking(int inputIndex, int expectedTick, int batch) {
            super(inputIndex, expectedTick, batch);
        }

        private Smoking(int fromIndex, boolean handledStart, int expectedTick, int currentTick, float partialProgress, int batch) {
            super(fromIndex, handledStart, expectedTick, currentTick, partialProgress, batch);
        }

        @Override
        public String getType() {
            return TYPE;
        }
    }

    public static class Blasting extends Burn {

        public static final MapCodec<Blasting> CODEC = simpleBurnCodec(Blasting::new);

        public static final String TYPE = "blasting";

        public Blasting(int inputIndex, int expectedTick, int batch) {
            super(inputIndex, expectedTick, batch);
        }

        private Blasting(int fromIndex, boolean handledStart, int expectedTick, int currentTick, float partialProgress, int batch) {
            super(fromIndex, handledStart, expectedTick, currentTick, partialProgress, batch);
        }

        @Override
        public String getType() {
            return TYPE;
        }
    }

}
