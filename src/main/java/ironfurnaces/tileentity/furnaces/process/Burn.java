package ironfurnaces.tileentity.furnaces.process;

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
import java.util.function.Predicate;

public abstract class Burn extends ProcessingInstance {
    private static final Map<RecipeType<?>, Factory> idMap = Util.make(() -> Map.of(
            RecipeType.BLASTING, Blasting::new,
            RecipeType.SMOKING, Smoking::new,
            RecipeType.SMELTING, Smelting::new
    ));
    protected final int expectedTick;
    private final int inputIndex;

    private final Predicate<InputCache> inputCacheCheck;
    private final InputCache cache;
    private final Container ingredients;
    private final Level level;
    @Getter
    private boolean isBlocking = false;
    protected int currentTick = 0;

    public Burn(int expectedTick, int inputIndex, Recipe<Container> recipe, InputCache cache, Level level, AugmentCache augment) {
        super(recipe);
        this.expectedTick = expectedTick;
        this.inputIndex = inputIndex;
        ItemStack stackInSlot = cache.getStackInSlot(inputIndex);
        this.ingredients = new SimpleContainer(stackInSlot);
        this.inputCacheCheck = x -> recipe.matches(new SimpleContainer(x.getStackInSlot(inputIndex)), level);
        this.cache = cache;
        this.level = level;

    }

    public static Burn create(int expectedTick, int inputIndex, Recipe<Container> recipe, InputCache cache, Level level, AugmentCache augment) {
        Factory factory = idMap.get(recipe.getType());
        if (factory != null) {
            return factory.create(expectedTick, inputIndex, recipe, cache, level, augment);
        }
        throw new RuntimeException("fail at creating Burn instance with RecipeType: " + recipe.getType());
    }

    @Override
    public boolean validate() {
        return this.inputCacheCheck.test(cache);
    }

    @Override
    public void whenStart(BlockIronFurnaceTileBaseV2 tile) {

    }

    @Override
    public void whenDone(BlockIronFurnaceTileBaseV2 tile) {
        super.whenDone(tile);
        ItemStack currentItem = tile.getInput().getStackInSlot(inputIndex);
        currentItem.shrink(1);
        ItemStack resultItem = recipe.getResultItem(level.registryAccess());
        tile.getOutput().insertItem(inputIndex, resultItem, false);
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

    }

    @Override
    public TickResult whenTick(BlockIronFurnaceTileBaseV2 tile) {
        ItemStack resultItem = recipe.getResultItem(level.registryAccess());
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
        Burn create(int expectedTick, int inputIndex, Recipe<Container> recipe, InputCache cache, Level level, AugmentCache augment);
    }

    public static class Smelting extends Burn {

        public Smelting(int expectedTick, int inputIndex, Recipe<Container> recipe, InputCache cache, Level level, AugmentCache augment) {
            super(expectedTick, inputIndex, recipe, cache, level, augment);
        }
    }

    public static class Smoking extends Burn {

        public Smoking(int expectedTick, int inputIndex, Recipe<Container> recipe, InputCache cache, Level level, AugmentCache augment) {
            super(expectedTick, inputIndex, recipe, cache, level, augment);
        }
    }

    public static class Blasting extends Burn {

        public Blasting(int expectedTick, int inputIndex, Recipe<Container> recipe, InputCache cache, Level level, AugmentCache augment) {
            super(expectedTick, inputIndex, recipe, cache, level, augment);
        }
    }

}
