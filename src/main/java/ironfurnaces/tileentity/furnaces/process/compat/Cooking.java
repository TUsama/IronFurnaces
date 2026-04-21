package ironfurnaces.tileentity.furnaces.process.compat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.cache.recipe_type_handlers.FarmerDelightCookingRecipeTypeHandler;
import ironfurnaces.tileentity.furnaces.process.ProcessingInstance;
import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;

import java.util.Optional;

@Getter(AccessLevel.PRIVATE)
public class Cooking extends ProcessingInstance {
    public static final int PREMEAL = 0;
    public static final int MEAL = 0;
    public static final int CONTAINER = 6;

    public static final String TYPE = "fd_cooking";

    public static final MapCodec<Cooking> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ExtraCodecs.POSITIVE_INT.fieldOf("fromIndex").forGetter(Cooking::getFromIndex),
                    Codec.BOOL.fieldOf("handledStart").forGetter(Cooking::isHandledStart),
                    ExtraCodecs.POSITIVE_INT.fieldOf("expectedTick").forGetter(Cooking::getExpectedTick),
                    ExtraCodecs.POSITIVE_INT.fieldOf("currentTick").forGetter(Cooking::getCurrentTick),
                    Codec.FLOAT.fieldOf("partialProgress").forGetter(Cooking::getPartialProgress),
                    ExtraCodecs.POSITIVE_INT.fieldOf("batch").forGetter(Cooking::getBatch)
            ).apply(instance, Cooking::new));

    protected final int expectedTick;
    protected int currentTick = 0;
    private float partialProgress = 0.0f;
    private int batch = 1;

    public Cooking(int expectedTick, int batch) {
        this(0, false, expectedTick, 0, 0.0f, 1);
    }

    private Cooking(int fromIndex, boolean handledStart, int expectedTick, int currentTick, float partialProgress, int batch) {
        super(fromIndex, handledStart);
        this.expectedTick = expectedTick;
        this.currentTick = currentTick;
        this.partialProgress = partialProgress;
        this.batch = batch;
    }

    @Override
    public boolean needLit(FurnacePatternBlockEntity tile) {
        if (tile.getInput().getFillStats().fillRatio() > 0f) {
            Optional<? extends Recipe> recipe = tile.getRecipe(ItemStack.EMPTY);
            if (recipe.isPresent() && recipe.get() instanceof CookingPotRecipe cookingPotRecipe) {
                ItemStack resultItem = cookingPotRecipe.getResultItem(tile.getLevel().registryAccess()).copy();
                if (resultItem.isEmpty()) return false;
                resultItem.setCount(resultItem.getCount() * batch);
                ItemStack itemStack = tile.getOutput().insertItem(PREMEAL, resultItem, true);
                return itemStack.isEmpty();
            }
        }
        return false;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public void whenStart(FurnacePatternBlockEntity tile) {
        //todo batch
    }

    @Override
    public TickResult whenTick(FurnacePatternBlockEntity tile) {
        if (!validateSlot(tile.getInput(), tile)) return TickResult.DISCARD;
        //这里不用能cache，性能会稍微差一些，不过应该不会有人堆厨锅阵列……
        //因为cache存的只有一个物品
        var recipe = tile.getRecipe(ItemStack.EMPTY);
        if (recipe.isEmpty() || !(recipe.get() instanceof CookingPotRecipe cookingPotRecipe)) return TickResult.DISCARD;
        ItemStack resultItem = cookingPotRecipe.getResultItem(tile.getLevel().registryAccess()).copy();
        resultItem.setCount(resultItem.getCount() * batch);
        ItemStack itemStack = tile.getViewOnly().insertItem(PREMEAL, resultItem, true);
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
    public void whenDone(FurnacePatternBlockEntity tile) {

        Level level = tile.getLevel();
        Optional<? extends Recipe> recipe = tile.getRecipe(ItemStack.EMPTY);
        if (recipe.isPresent()){
            Recipe recipe1 = recipe.get();
            //~ if >1.20.1 'x.getResultItem' -> 'x.value().getResultItem'
            ItemStack resultItem = recipe1.getResultItem(level.registryAccess()).copy();
            resultItem.setCount(resultItem.getCount() * batch);
            tile.getViewOnly().insertItem(PREMEAL, resultItem, false);
            for (int i = 0; i < tile.getInput().getSlots(); i++) {
                ItemStack stackInSlot1 = tile.getInput().getStackInSlot(i);
                if (!stackInSlot1.isEmpty()) tile.getInput().extractItem(i, 1, false);
            }

            for (int i = 0; i < batch; i++) {
                tile.setRecipeUsed(recipe1);
            }
            if (tile.getAugments().getCurrentRecipeType() instanceof FarmerDelightCookingRecipeTypeHandler handler) {
                handler.setLastRecipe(((CookingPotRecipe) recipe1));
            }
            super.whenDone(tile);
        }

    }

    @Override
    public float getDoneProgress() {
        return currentTick / (expectedTick * 1.0f);
    }
}
