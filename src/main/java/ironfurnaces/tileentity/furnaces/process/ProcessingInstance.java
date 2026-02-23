package ironfurnaces.tileentity.furnaces.process;

import ironfurnaces.tileentity.furnaces.BlockIronFurnaceTileBaseV2;
import lombok.Getter;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

public abstract class ProcessingInstance {

    @Getter
    protected final RecipeType<?> recipeType;
    private boolean handledStart = false;
    protected final Recipe<?> recipe;

    public ProcessingInstance(Recipe<?> recipe) {
        this.recipe = recipe;
        this.recipeType = recipe.getType();
    }

    public abstract boolean validate();

    public abstract void whenStart(BlockIronFurnaceTileBaseV2 tile);

    public void whenDone(BlockIronFurnaceTileBaseV2 tile){
        tile.setRecipeUsed(recipe);
        tile.setChanged();
    }


    public TickResult tick(BlockIronFurnaceTileBaseV2 tile) {
        if (!validate()) return TickResult.DISCARD;
        if (!handledStart) {
            whenStart(tile);
            handledStart = true;
        }
        return whenTick(tile);
    }

    public TickResult whenTick(BlockIronFurnaceTileBaseV2 tile) {
        return TickResult.SUCCESS;
    }


    public enum TickResult {
        SUCCESS,
        DONE,
        BLOCKED,
        DISCARD;
    }
}
