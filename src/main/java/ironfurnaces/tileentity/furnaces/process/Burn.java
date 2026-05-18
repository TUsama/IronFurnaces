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
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

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
        Recipe<?> recipe = tile.getInstanceManager()
                .getCachedCookingRecipe(tile, this);

        if (!(recipe instanceof AbstractCookingRecipe cookingRecipe)) {
            return false;
        }

        ItemStack input = tile.getInput().getStackInSlot(fromIndex);
        if (input.isEmpty()) {
            return false;
        }

        ItemStack resultItem = cookingRecipe.assemble(new SingleRecipeInput(input));
        if (resultItem.isEmpty()) {
            return false;
        }

        ItemStack result = resultItem.copyWithCount(batch * resultItem.getCount());

        ItemStack remainder = tile.getOutput().insertItemReturnRemaining(
                fromIndex,
                result,
                true,
                null
        );

        return remainder.isEmpty();
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
            ItemStack resultItem = x.value().assemble(new SingleRecipeInput(stackInSlot)).copy();
            //可以确保这里是能完全存入的，因为whenDone会在whenTick后直接执行，而whenTick确保了有空位。
            resultItem.setCount(resultItem.getCount() * batch);
            try (var tx = Transaction.openRoot()) {
                tile.getOutput().insert(fromIndex, ItemResource.of(resultItem), resultItem.getCount(), tx);
                int extract = tile.getInput().extract(fromIndex, ItemResource.of(stackInSlot), batch, tx);
                tx.commit();
                for (int i = 0; i < batch; i++) {
                    tile.setRecipeUsed(x);
                }
                ItemStack itemStack = new ItemStack(stackInSlot.getItem(), batch);
                CompatUtil.firePmmoSmeltedEvent(itemStack, resultItem,tile.getLevel(), tile.getBlockPos());
                if (tile.getOwner() != null) {
                    CompatUtil.handleVanillaWhenHasPlayer(tile.getOwner(), resultItem, batch);
                } else {
                    CompatUtil.handleVanillaWhenWithoutPlayer(resultItem, level);
                }
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
        ItemStack resultItem = recipe.assemble(new SingleRecipeInput(stackInSlot)).copy();
        resultItem.setCount(resultItem.getCount() * batch);
        try (var tx = Transaction.openRoot()) {
            int amount = tile.getOutput().insert(fromIndex, ItemResource.of(resultItem), resultItem.getCount(), tx);
            if (amount == resultItem.getCount()) {
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
