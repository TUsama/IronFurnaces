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

@Accessors(fluent = true, chain = true)
public class InputCache extends PatternCache implements ICacheFillStats{

    private final FillStats fill_stats = new FillStats();
    @Setter
    protected Function<ItemStack, Optional<? extends Recipe>> grabRecipeCallback;
    @Setter
    private Consumer<InputCache> contentChangeCallback;


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
            contentChangeCallback.accept(this);
        }
        recomputeFillStats();
    }

    @Override
    public void updateFurnaceMode(FurnaceMode mode) {
        super.updateFurnaceMode(mode);
    }


    public void splitStacks(boolean force) {
        int slots = getSlots();
        if (slots <= 1) return;

        // force=false：没有空槽则不做
        if (!force) {
            boolean has_empty = false;
            for (int i = 0; i < slots; i++) {
                if (getStackInSlot(i).isEmpty()) {
                    has_empty = true;
                    break;
                }
            }
            if (!has_empty) return;
        }

        // 找一个参考物品（第一个非空）
        ItemStack reference = ItemStack.EMPTY;
        for (int i = 0; i < slots; i++) {
            ItemStack s = getStackInSlot(i);
            if (!s.isEmpty()) {
                reference = s;
                break;
            }
        }
        if (reference.isEmpty()) return;

        // 参与均分的槽：空槽 + 同 Item 槽；并统计同 Item 总数量
        int[] participants = new int[slots];
        int p_count = 0;
        int total = 0;

        for (int i = 0; i < slots; i++) {
            ItemStack s = getStackInSlot(i);
            if (s.isEmpty()) {
                participants[p_count++] = i;
                continue;
            }

            // 与旧版一致：只按 Item 比较，不比较 NBT
            if (s.getItem() == reference.getItem()) {
                participants[p_count++] = i;
                total += s.getCount();
            }
        }

        if (p_count == 0 || total == 0) return;

        // 防溢出：总量不能超过“参与槽数 * 单堆最大值”
        // 否则会出现无法分配的问题（旧版多半也会出怪行为）
        int max_stack = reference.getMaxStackSize();
        int capacity = max_stack * p_count;
        if (total > capacity) {
            // 保守策略：不改动。若你希望“尽量填满，其余维持原堆”，我可以给另一策略实现。
            return;
        }

        int base = total / p_count;
        int rem = total % p_count;

        // 若已经均分则不做（避免无意义 setStackInSlot）
        boolean already_balanced = true;
        for (int idx = 0; idx < p_count; idx++) {
            int slot = participants[idx];
            int target = base + (idx < rem ? 1 : 0);

            ItemStack cur = getStackInSlot(slot);
            int cur_count = cur.isEmpty() ? 0 : cur.getCount();

            if (cur_count != target) {
                already_balanced = false;
                break;
            }
        }
        if (already_balanced) return;

        // 应用分配：只改参与槽；其他物品槽保持不动
        for (int idx = 0; idx < p_count; idx++) {
            int slot = participants[idx];
            int target = base + (idx < rem ? 1 : 0);

            if (target <= 0) {
                setStackInSlot(slot, ItemStack.EMPTY);
            } else {
                setStackInSlot(slot, reference.copyWithCount(target));
            }
        }
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
