package ironfurnaces.tileentity.furnaces.cache;

import ironfurnaces.tileentity.furnaces.FurnaceMode;
import ironfurnaces.tileentity.furnaces.cache.stat.FillStats;
import ironfurnaces.tileentity.furnaces.pattern.FurnacePattern;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;

@Accessors(fluent = true, chain = true)
public class InputCache extends PatternCache implements ICacheFillStats{

    private final FillStats fill_stats = new FillStats();
    @Setter
    protected Function<ItemStack, Optional<? extends Recipe>> grabRecipeCallback;
    @Setter
    private IntConsumer contentChangeCallback;


    public InputCache(FurnaceMode mode, FurnacePattern tier) {
        super(tier.inputSlotAmount(), mode, tier);
    }


    public void dropStacksInUnavailableSlots(Level level, BlockPos pos) {
        if (level == null) return;


        for (int i = getSlots(); i < stacks.size(); i++) {
            ItemStack stack = getStackInSlot(i);
            if (stack.isEmpty()) continue;

            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
            setStackInSlot(i, ItemStack.EMPTY);
        }
    }


    public void recomputeFillStats() {
        int slots = getSlots();
        fill_stats.slot_count = slots;
        fill_stats.fill_sum = 0.0f;
        fill_stats.non_empty = 0;

        for (int i = 0; i < slots; i++) {
            ItemStack s = getStackInSlot(i);
            if (s.isEmpty()) continue;

            fill_stats.non_empty++;
            int cap = Math.min(getSlotLimit(i), s.getMaxStackSize());
            if (cap > 0) {
                fill_stats.fill_sum += (float) s.getCount() / (float) cap;
            }
        }
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

    @Override
    public void updateFurnaceMode(FurnaceMode mode) {
        super.updateFurnaceMode(mode);
    }


    @Override
    public FillStats getFillStats() {
        return fill_stats;
    }

    @Override
    public void updateFurnacePattern(FurnacePattern pattern) {
        int i = pattern.inputSlotAmount();
        NonNullList<ItemStack> newList = NonNullList.withSize(i, ItemStack.EMPTY);
        for (int i1 = 0; i1 < this.getSlots(); i1++) {
            if (newList.size() - 1 >= i1) newList.set(i1, this.getStackInSlot(i1).copy());
        }
        this.stacks = newList;
        this.pattern = pattern;

        recomputeFillStats();

    }


}
