package ironfurnaces.tileentity.furnaces.cache;

import ironfurnaces.tileentity.furnaces.FurnaceMode;
import ironfurnaces.tileentity.furnaces.process.Generate;
import ironfurnaces.tileentity.furnaces.process.ProcessingInstance;
import lombok.Setter;
import lombok.With;
import lombok.experimental.Accessors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.lang.module.InvalidModuleDescriptorException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

@Accessors(fluent = true, chain = true)
public class InputCache extends TieredCache {

    private NonNullList<SlotState> slotState;
    @With
    protected Function<ItemStack, Optional<? extends Recipe>> grabRecipeCallback;
    @Setter
    private Consumer<InputCache> contentChangeCallback;

    public InputCache(FurnaceMode mode, ForgeConfigSpec.IntValue tier) {
        this(1, mode, tier);
    }

    public InputCache(int size, FurnaceMode mode, ForgeConfigSpec.IntValue tier) {
        super(size, mode, tier);
        this.slotState = NonNullList.withSize(size, SlotState.IDLE);
    }


    @Override
    public int getSlots() {
        int slots = super.getSlots();
        for (int i = 0; i < slots; i++) {
            if (slotState.get(i).equals(SlotState.UNAVAILABLE)){
                slotState.set(i, SlotState.IDLE);
            }
        }
        return slots;
    }

    public void dropStacksInUnavailableSlots(Level level, BlockPos pos){
        for (int i = stacks.size(); i > getSlots(); i--) {
            ItemStack stackInSlot = getStackInSlot(i);
            if (!stackInSlot.isEmpty()){
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stackInSlot);
                slotState.set(i, SlotState.UNAVAILABLE);
            }
        }
    }

    public boolean isSlotIdle(int index){
        return slotState.get(index).equals(SlotState.IDLE);
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
    }

    @Override
    public void updateFurnaceMode(FurnaceMode mode) {
        super.updateFurnaceMode(mode);
    }


    /**
     * 对当前所有可操作输入槽（[0, getSlots())）进行“同物品均分”。
     *
     * @param force
     *  - true: 强制执行均分
     *  - false: 仅当存在空槽时执行（原 fullCheck=false 的语义）
     */
    public void splitStacks(boolean force) {
        int slots = getSlots();
        if (slots <= 1) return; // 0/1 槽无意义（GENERATOR/FURNACE 基本不会用到）

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

    private enum SlotState {
        WORKING,
        UNAVAILABLE,
        IDLE;
    }

}
