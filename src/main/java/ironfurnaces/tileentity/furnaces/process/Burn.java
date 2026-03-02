package ironfurnaces.tileentity.furnaces.process;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ironfurnaces.tileentity.furnaces.BlockIronFurnaceTileBaseV2;
import ironfurnaces.tileentity.furnaces.cache.AugmentCache;
import ironfurnaces.tileentity.furnaces.cache.InputCache;
import lombok.Getter;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class Burn extends ProcessingInstance {
    private static final Map<RecipeType<?>, Factory> idMap = Util.make(() -> Map.of(
            RecipeType.BLASTING, Blasting::new,
            RecipeType.SMOKING, Smoking::new,
            RecipeType.SMELTING, Smelting::new
    ));

    protected final int expectedTick;
    protected final int inputIndex;
    protected int currentTick = 0;

    public Burn(int expectedTick, int inputIndex) {
        super();
        this.expectedTick = expectedTick;
        this.inputIndex = inputIndex;

    }

    public static Burn create(int expectedTick, int inputIndex, Recipe<Container> recipe) {
        Factory factory = idMap.get(recipe.getType());
        if (factory != null) {
            return factory.create(expectedTick, inputIndex);
        }
        throw new RuntimeException("fail at creating Burn instance with RecipeType: " + recipe.getType());
    }



    @Override
    public void whenStart(BlockIronFurnaceTileBaseV2 tile) {

    }

    @Override
    public void whenDone(BlockIronFurnaceTileBaseV2 tile) {
        super.whenDone(tile);
        Level level = tile.getLevel();
        tile.getRecipe(tile.getInput().getStackInSlot(inputIndex)).ifPresent(x -> {
            ItemStack resultItem = x.getResultItem(level.registryAccess());
            tile.getOutput().insertItem(inputIndex, resultItem, false);
            ItemStack currentItem = tile.getInput().getStackInSlot(inputIndex);
            currentItem.shrink(1);
            if (currentItem.hasCraftingRemainingItem()){
                ItemStack craftingRemainingItem = currentItem.getCraftingRemainingItem();
                boolean inserted = false;
                for (int i = 0; i < tile.getRemaining().getSlots(); i++) {
                    ItemStack itemStack = tile.getRemaining().insertItem(i, craftingRemainingItem, false);
                    if (itemStack.isEmpty()) {
                        inserted = true;
                        break;
                    }
                }
                if (!inserted){
                    BlockPos blockPos = tile.getBlockPos();
                    Containers.dropItemStack(level, blockPos.getX(), blockPos.getY(), blockPos.getZ(), craftingRemainingItem);
                }
            }
        });

    }

    @Override
    public TickResult whenTick(BlockIronFurnaceTileBaseV2 tile) {
        Optional<? extends Recipe> recipe = tile.getRecipe(tile.getInput().getStackInSlot(inputIndex));
        if (recipe.isEmpty()) return TickResult.DISCARD;
        Recipe recipe1 = recipe.get();
        ItemStack resultItem = recipe1.getResultItem(tile.getLevel().registryAccess());
        ItemStack itemStack = tile.getOutput().insertItem(inputIndex, resultItem, true);
        if (itemStack.isEmpty()){
            currentTick++;
        } else {
            return TickResult.BLOCKED;
        }
        if (currentTick >= expectedTick){
            return TickResult.DONE;
        }
        return TickResult.SUCCESS;
    }

    @FunctionalInterface
    public interface Factory {
        Burn create(int expectedTick, int inputIndex);
    }

    public static class Smelting extends Burn {
        public static final MapCodec<Smelting> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Codec.INT.fieldOf("expectedTick").forGetter(x -> x.expectedTick),
                        Codec.INT.fieldOf("inputIndex").forGetter(x -> x.inputIndex),
                        Codec.INT.fieldOf("currentOutput").forGetter(x -> x.currentTick)
                ).apply(instance, (a, b, c) -> {
                    Smelting smelting = new Smelting(a, b);
                    smelting.currentTick = c;
                    return smelting;
                }));
        public static final String TYPE = "smelting";

        public Smelting(int expectedTick, int inputIndex) {
            super(expectedTick, inputIndex);
        }

        @Override
        public String getType() {
            return TYPE;
        }
    }

    public static class Smoking extends Burn {
        public static final MapCodec<Smoking> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Codec.INT.fieldOf("expectedTick").forGetter(x -> x.expectedTick),
                        Codec.INT.fieldOf("inputIndex").forGetter(x -> x.inputIndex),
                        Codec.INT.fieldOf("currentOutput").forGetter(x -> x.currentTick)
                ).apply(instance, (a, b, c) -> {
                    Smoking smelting = new Smoking(a, b);
                    smelting.currentTick = c;
                    return smelting;
                }));
        public static final String TYPE = "smoking";
        public Smoking(int expectedTick, int inputIndex) {
            super(expectedTick, inputIndex);
        }
        @Override
        public String getType() {
            return TYPE;
        }
    }

    public static class Blasting extends Burn {

        public static final MapCodec<Blasting> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Codec.INT.fieldOf("expectedTick").forGetter(x -> x.expectedTick),
                        Codec.INT.fieldOf("inputIndex").forGetter(x -> x.inputIndex),
                        Codec.INT.fieldOf("currentOutput").forGetter(x -> x.currentTick)
                ).apply(instance, (a, b, c) -> {
                    Blasting smelting = new Blasting(a, b);
                    smelting.currentTick = c;
                    return smelting;
                }));

        public static final String TYPE = "blasting";
        public Blasting(int expectedTick, int inputIndex) {
            super(expectedTick, inputIndex);
        }

        @Override
        public String getType() {
            return TYPE;
        }
    }

}
