package ironfurnaces.tileentity.furnaces.cache;

import com.mojang.serialization.Codec;
import ironfurnaces.tileentity.furnaces.FurnaceMode;
import ironfurnaces.tileentity.furnaces.cache.stat.FillStats;
import ironfurnaces.tileentity.furnaces.tier.FurnacePattern;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@Accessors(fluent = true, chain = true)
public class InputCache extends TieredCache implements ICacheFillStats{

    private final FillStats fill_stats = new FillStats();
    @Setter
    protected Function<ItemStack, Optional<? extends Recipe>> grabRecipeCallback;
    private NonNullList<SlotState> slotState;
    @Setter
    private Consumer<InputCache> contentChangeCallback;


    public InputCache(FurnaceMode mode, FurnacePattern tier) {
        super(tier.inputSlotAmount(), mode, tier);
        this.slotState = NonNullList.withSize(tier.inputSlotAmount(), SlotState.IDLE);
    }

    @Override
    public int getSlots() {
        int slots = super.getSlots();
        for (int i = 0; i < slots; i++) {
            if (slotState.get(i).equals(SlotState.UNAVAILABLE)) {
                slotState.set(i, SlotState.IDLE);
            }
        }
        return slots;
    }

    public void dropStacksInUnavailableSlots(Level level, BlockPos pos) {
        for (int i = stacks.size(); i > getSlots(); i--) {
            ItemStack stackInSlot = getStackInSlot(i);
            if (!stackInSlot.isEmpty()) {
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stackInSlot);
                slotState.set(i, SlotState.UNAVAILABLE);
            }
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

    public boolean isSlotIdle(int index) {
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
        recomputeFillStats();
    }

    @Override
    public void updateFurnaceMode(FurnaceMode mode) {
        super.updateFurnaceMode(mode);
    }


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

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compoundTag = super.serializeNBT();
        ListTag tags = new ListTag();
        for (int i = 0; i < slotState.size(); i++) {
            CompoundTag compoundTag1 = new CompoundTag();
            compoundTag1.putInt("Index", i);
            compoundTag1.putString("Value", slotState.get(i).toString());
            tags.add(compoundTag1);
        }
        compoundTag.put("SlotState", tags);
        return compoundTag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);

        if (!nbt.contains("SlotState", net.minecraft.nbt.Tag.TAG_LIST)) {
            return;
        }

        ListTag list = nbt.getList("SlotState", net.minecraft.nbt.Tag.TAG_COMPOUND);

        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);

            int index = entry.getInt("Index");
            String value = entry.getString("Value");

            if (index >= 0 && index < slotState.size()) {
                slotState.set(index, SlotState.valueOf(value));
            }
        }
    }

    @Override
    public FillStats getFillStats() {
        return fill_stats;
    }



    private enum SlotState implements StringRepresentable {
        WORKING("working"),
        UNAVAILABLE("unavailable"),
        IDLE("idle");

        public static final Codec<SlotState> CODEC = StringRepresentable.fromEnum(SlotState::values);

        private final String name;

        SlotState(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }

}
