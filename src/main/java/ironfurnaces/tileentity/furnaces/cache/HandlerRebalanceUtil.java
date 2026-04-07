package ironfurnaces.tileentity.furnaces.cache;

import lombok.experimental.UtilityClass;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.items.ItemStackHandler;
//? 1.20.1 {
import net.minecraftforge.items.ItemHandlerHelper;
//? } else {

//?}
import java.util.*;

@UtilityClass
public final class HandlerRebalanceUtil {


    public static void rebalanceForProcessing(ItemStackHandler handler) {
        int slotCount = handler.getSlots();
        if (slotCount <= 1) return;
        LinkedHashMap<StackKey, StackGroup> groups = new LinkedHashMap<>();
        int totalItems = 0;

        for (int i = 0; i < slotCount; i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (stack.isEmpty()) continue;

            StackKey key = new StackKey(stack);
            StackGroup group = groups.computeIfAbsent(key, k -> new StackGroup(stack.copy()));
            group.totalCount += stack.getCount();
            totalItems += stack.getCount();
        }

        if (groups.isEmpty()) {
            return;
        }

        List<Integer> slotOrder = new ArrayList<>(slotCount);
        for (int i = 0; i < slotCount; i++) {
            slotOrder.add(i);
        }
        slotOrder.sort((a, b) -> Integer.compare(handler.getSlotLimit(b), handler.getSlotLimit(a)));

        for (int i = 0; i < slotCount; i++) {
            handler.setStackInSlot(i, ItemStack.EMPTY);
        }

        if (totalItems <= slotCount) {
            fillOnePerSlot(handler, slotOrder, new ArrayList<>(groups.values()));
            return;
        }

        List<StackGroup> groupList = new ArrayList<>(groups.values());

        for (StackGroup g : groupList) {
            g.assignedSlots = 1;
        }

        int usedSlots = groupList.size();

        // 如果物品种类比槽位还多，只能前 slotCount 个槽非空，其余种类和已有种类混在逻辑上不可能
        // 这里退化为“一格一个”，后续多余物品靠已有栈继续叠
        if (usedSlots > slotCount) {
            // 截断到前 slotCount 个组先占坑
            groupList.sort(Comparator.comparingInt((StackGroup g) -> g.totalCount).reversed());
            groupList = new ArrayList<>(groupList.subList(0, slotCount));
            usedSlots = slotCount;
        }

        int remainingFreeSlots = slotCount - usedSlots;

        // 继续分配额外槽位：
        // 哪种物品“当前已分配槽位总容量 still 不够”，就优先再给一个槽
        while (remainingFreeSlots > 0) {
            StackGroup best = null;
            int bestOverflow = 0;

            for (StackGroup g : groupList) {
                int slotCap = estimatePerSlotCapacity(handler, g.prototype);
                int overflow = g.totalCount - g.assignedSlots * slotCap;
                if (overflow > bestOverflow) {
                    bestOverflow = overflow;
                    best = g;
                }
            }

            // 所有组都已经“理论上装得下”了，剩余槽位不强制再分
            if (best == null) {
                break;
            }

            best.assignedSlots++;
            remainingFreeSlots--;
        }

        // 6. 若还有空余槽位，为了“尽量每格不空”，继续分给数量最多的组
        while (remainingFreeSlots > 0) {
            StackGroup best = Collections.max(groupList, Comparator.comparingInt(g -> g.totalCount));
            best.assignedSlots++;
            remainingFreeSlots--;
        }

        // 7. 按“需要更多堆”的组优先占用大槽位
        groupList.sort((a, b) -> {
            int capA = estimatePerSlotCapacity(handler, a.prototype);
            int capB = estimatePerSlotCapacity(handler, b.prototype);

            int needA = ceilDiv(a.totalCount, capA);
            int needB = ceilDiv(b.totalCount, capB);

            int cmp = Integer.compare(needB, needA);
            if (cmp != 0) return cmp;
            return Integer.compare(b.totalCount, a.totalCount);
        });

        int slotPtr = 0;

        for (StackGroup group : groupList) {
            int assigned = Math.min(group.assignedSlots, slotCount - slotPtr);
            if (assigned <= 0) break;

            List<Integer> targetSlots = new ArrayList<>(assigned);
            for (int k = 0; k < assigned; k++) {
                targetSlots.add(slotOrder.get(slotPtr++));
            }

            spreadGroupIntoSlots(handler, group.prototype, group.totalCount, targetSlots);
        }
    }

    /**
     * 总物品数 <= 槽位数时：
     * 直接做到“一个物品一个槽”，保证尽可能多的槽非空。
     */
    private static void fillOnePerSlot(ItemStackHandler handler, List<Integer> slotOrder, List<StackGroup> groups) {
        int slotPtr = 0;

        for (StackGroup g : groups) {
            int remaining = g.totalCount;

            while (remaining > 0 && slotPtr < slotOrder.size()) {
                int slot = slotOrder.get(slotPtr++);
                ItemStack out = g.prototype.copy();
                out.setCount(1);
                handler.setStackInSlot(slot, out);
                remaining--;
            }

            g.totalCount = remaining;
        }

        // 理论上这里不会剩余，因为 totalItems <= slotCount
        // 若还有剩余，就尝试回填到同类栈中
        for (StackGroup g : groups) {
            int remain = g.totalCount;
            if (remain <= 0) continue;

            for (int i = 0; i < handler.getSlots() && remain > 0; i++) {
                ItemStack stack = handler.getStackInSlot(i);
                if (stack.isEmpty()) continue;
                if (!canMergeStrict(stack, g.prototype)) continue;

                int cap = Math.min(handler.getSlotLimit(i), stack.getMaxStackSize());
                int room = cap - stack.getCount();
                if (room <= 0) continue;

                int add = Math.min(room, remain);
                stack.grow(add);
                remain -= add;
            }
        }
    }

    /**
     * 将一种物品分布到若干槽位中。
     * 目标：
     * - 每个被分配到的槽位至少 1 个
     * - 尽量让这一组在这些槽位里分布得较平
     */
    private static void spreadGroupIntoSlots(ItemStackHandler handler, ItemStack prototype, int totalCount, List<Integer> targetSlots) {
        int n = targetSlots.size();
        if (n <= 0 || totalCount <= 0) return;

        // 先算这些目标槽的真实容量
        int[] capacities = new int[n];
        int totalCapacity = 0;
        for (int i = 0; i < n; i++) {
            int slot = targetSlots.get(i);
            capacities[i] = Math.min(handler.getSlotLimit(slot), prototype.getMaxStackSize());
            totalCapacity += capacities[i];
        }

        totalCount = Math.min(totalCount, totalCapacity);
        if (totalCount <= 0) return;

        // 如果总数比目标槽少，那只能前 totalCount 个槽放 1 个
        if (totalCount < n) {
            for (int i = 0; i < totalCount; i++) {
                ItemStack out = prototype.copy();
                out.setCount(1);
                handler.setStackInSlot(targetSlots.get(i), out);
            }
            return;
        }

        // 尽量均衡分配：base/base+1
        int base = totalCount / n;
        int rem = totalCount % n;

        int[] assigned = new int[n];
        int left = totalCount;

        for (int i = 0; i < n; i++) {
            int want = base + (i < rem ? 1 : 0);
            int put = Math.min(want, capacities[i]);
            assigned[i] = put;
            left -= put;
        }

        // 若有容量较小的槽导致没分完，再补到有空间的槽里
        for (int i = 0; i < n && left > 0; i++) {
            int room = capacities[i] - assigned[i];
            if (room <= 0) continue;

            int add = Math.min(room, left);
            assigned[i] += add;
            left -= add;
        }

        for (int i = 0; i < n; i++) {
            int count = assigned[i];
            if (count <= 0) continue;

            ItemStack out = prototype.copy();
            out.setCount(count);
            handler.setStackInSlot(targetSlots.get(i), out);
        }
    }

    /**
     * 估算某种物品在该 handler 里单槽最多能放多少。
     * 因为你说没有额外过滤/运行时限制，所以这里直接取：
     * max(slotLimit) 与 itemMaxStackSize 的较小值。
     */
    private static int estimatePerSlotCapacity(ItemStackHandler handler, ItemStack prototype) {
        int maxSlotLimit = 0;
        for (int i = 0; i < handler.getSlots(); i++) {
            if (handler.isItemValid(i, prototype)) {
                maxSlotLimit = Math.max(maxSlotLimit, handler.getSlotLimit(i));
            }
        }
        return Math.min(maxSlotLimit, prototype.getMaxStackSize());
    }

    private static int ceilDiv(int a, int b) {
        if (b <= 0) return Integer.MAX_VALUE;
        return (a + b - 1) / b;
    }

    private static boolean canMergeStrict(ItemStack a, ItemStack b) {
        return ItemHandlerHelper.canItemStacksStack(a, b);
    }

    private static final class StackGroup {
        final ItemStack prototype;
        int totalCount;
        int assignedSlots = 0;

        StackGroup(ItemStack prototype) {
            this.prototype = prototype.copy();
            this.prototype.setCount(1);
        }
    }

    private static final class StackKey {
        final ItemStack item;

        StackKey(ItemStack stack) {
            this.item = stack.copyWithCount(1);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof StackKey other)) return false;
            return ItemHandlerHelper.canItemStacksStack(other.item, this.item);
        }

        @Override
        public int hashCode() {
            return 31 * System.identityHashCode(item);
        }
    }
}
