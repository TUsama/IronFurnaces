//~ replace_all_recipe
package ironfurnaces.tileentity.furnaces.cache;

import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.pattern.IFurnaceStats;
import ironfurnaces.tileentity.furnaces.pattern.mode.AbstractFurnaceModeHandler;
import ironfurnaces.tileentity.furnaces.pattern.mode.FurnaceModeManager;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jetbrains.annotations.NotNull;
//? 1.20.1 {
//? } else {
import net.minecraft.world.item.crafting.RecipeHolder;
        //?}

import java.util.Optional;
import java.util.function.Function;
import java.util.function.IntConsumer;

@Accessors(fluent = true, chain = true)
public class InputCache extends ResizableCache {

    @Setter
    protected Function<ItemStack, Boolean> grabRecipeCallback;
    @Setter
    private IntConsumer contentChangeCallback;

    public InputCache(AbstractFurnaceModeHandler mode, IFurnaceStats<?> stats) {
        super(stats.inputSlotAmount(), mode);
    }

    @Override
    public boolean isValid(int index, ItemResource resource) {
        return grabRecipeCallback.apply(resource.toStack());
    }

    @Override
    protected void onContentsChanged(int index, ItemStack previousContents) {
        if (contentChangeCallback != null) {
            contentChangeCallback.accept(index);
        }
        recomputeFillStats();
    }


    @Override
    protected int updateSlotAmount(AbstractFurnaceModeHandler mode, IRecipeTypeHandler recipeTypeHandler, IFurnaceStats<?> stats, FurnacePatternBlockEntity blockEntity) {
        return FurnaceModeManager.INSTANCE.getMaxInputSlot(stats);
    }

}
