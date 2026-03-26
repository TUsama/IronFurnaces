package ironfurnaces.tileentity.furnaces.cache;

import ironfurnaces.tileentity.furnaces.FurnaceMode;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.cache.stat.FillStats;
import ironfurnaces.tileentity.furnaces.pattern.IFurnaceStats;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.IntConsumer;

@Accessors(fluent = true, chain = true)
public class InputCache extends PatternCache {

    @Setter
    protected Function<ItemStack, Optional<? extends Recipe>> grabRecipeCallback;
    @Setter
    private IntConsumer contentChangeCallback;

    public InputCache(FurnaceMode mode, IFurnaceStats stats) {
        super(stats.inputSlotAmount(), mode,  stats.inputSlotAmount());
    }


    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return grabRecipeCallback.apply(stack).isPresent();
    }


    @Override
    protected void onContentsChanged(int slot) {
        if (contentChangeCallback != null) {
            contentChangeCallback.accept(slot);
        }
        recomputeFillStats();
    }






}
