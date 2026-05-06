package ironfurnaces.tileentity.furnaces.cache;

import lombok.experimental.UtilityClass;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

@UtilityClass
public final class HandlerRebalanceUtil {

    public static void rebalanceForProcessing(ItemStacksResourceHandler handler) {
        int slotCount = handler.size();
        if (slotCount <= 1) return;

        List<SlotContent> snapshot = snapshot(handler);

        LinkedHashMap<ItemResource, StackGroup> groups = new LinkedHashMap<>();
        int totalItems = 0;

        for (int i = 0; i < slotCount; i++) {
            ItemResource resource = handler.getResource(i);
            int amount = handler.getAmountAsInt(i);

            if (resource.isEmpty() || amount <= 0) {
                continue;
            }

            StackGroup group = groups.computeIfAbsent(resource, StackGroup::new);
            group.totalCount += amount;
            totalItems += amount;
        }

        if (groups.isEmpty()) {
            return;
        }

        clear(handler);

        boolean success;

        if (totalItems <= slotCount) {
            success = fillOnePerSlot(handler, groups.values());
        } else {
            success = rebalanceGrouped(handler, new ArrayList<>(groups.values()));
        }

        // 兜底：如果因为槽位过滤、容量异常等原因没能完整写回，恢复原状态，避免吞物品。
        if (!success) {
            restore(handler, snapshot);
        }
    }

    private static boolean rebalanceGrouped(ItemStacksResourceHandler handler, List<StackGroup> groupList) {
        int slotCount = handler.size();

        for (StackGroup group : groupList) {
            group.assignedSlots = 1;
        }

        int usedSlots = groupList.size();

        // 正常情况下不会出现，因为每个非空 slot 最多只贡献一种 ItemResource，
        // distinct resource 数量不可能大于 slotCount。
        if (usedSlots > slotCount) {
            return false;
        }

        int remainingFreeSlots = slotCount - usedSlots;

        while (remainingFreeSlots > 0) {
            StackGroup best = null;
            int bestOverflow = 0;

            for (StackGroup group : groupList) {
                int slotCap = estimatePerSlotCapacity(handler, group.resource);
                int overflow = group.totalCount - group.assignedSlots * slotCap;

                if (overflow > bestOverflow) {
                    bestOverflow = overflow;
                    best = group;
                }
            }

            if (best == null) {
                break;
            }

            best.assignedSlots++;
            remainingFreeSlots--;
        }

        while (remainingFreeSlots > 0) {
            StackGroup best = groupList.stream()
                    .max(Comparator.comparingInt(group -> group.totalCount))
                    .orElse(null);

            if (best == null) {
                break;
            }

            best.assignedSlots++;
            remainingFreeSlots--;
        }

        groupList.sort((a, b) -> {
            int capA = estimatePerSlotCapacity(handler, a.resource);
            int capB = estimatePerSlotCapacity(handler, b.resource);

            int needA = ceilDiv(a.totalCount, capA);
            int needB = ceilDiv(b.totalCount, capB);

            int cmp = Integer.compare(needB, needA);
            if (cmp != 0) return cmp;

            return Integer.compare(b.totalCount, a.totalCount);
        });

        return writeGroups(handler, groupList);
    }

    /**
     * 总物品数 <= 槽位数时：
     * 每个物品占一个槽，尽量让最多的槽非空。
     */
    private static boolean fillOnePerSlot(ItemStacksResourceHandler handler, Collection<StackGroup> groups) {
        List<Integer> availableSlots = allSlots(handler);

        for (StackGroup group : groups) {
            int remaining = group.totalCount;

            while (remaining > 0) {
                Integer slot = takeBestSlot(handler, availableSlots, group.resource);
                if (slot == null) {
                    return false;
                }

                handler.set(slot, group.resource, 1);
                remaining--;
            }
        }

        return true;
    }

    private static boolean writeGroups(ItemStacksResourceHandler handler, List<StackGroup> groupList) {
        List<Integer> availableSlots = allSlots(handler);

        for (StackGroup group : groupList) {
            int wantedSlots = Math.min(group.assignedSlots, availableSlots.size());

            List<Integer> targetSlots = takeBestSlots(
                    handler,
                    availableSlots,
                    group.resource,
                    wantedSlots
            );

            // 如果预分配的槽容量不足，就继续拿可用槽补足。
            while (capacitySum(handler, group.resource, targetSlots) < group.totalCount) {
                Integer extra = takeBestSlot(handler, availableSlots, group.resource);
                if (extra == null) {
                    break;
                }
                targetSlots.add(extra);
            }

            int written = spreadGroupIntoSlots(
                    handler,
                    group.resource,
                    group.totalCount,
                    targetSlots
            );

            if (written != group.totalCount) {
                return false;
            }
        }

        return true;
    }

    /**
     * 将一种 ItemResource 分布到若干槽位中。
     *
     * 目标：
     * 1. 每个被分配到的槽位尽量至少 1 个。
     * 2. 总量尽量均分。
     * 3. 不超过每个槽对该 resource 的容量。
     *
     * 返回实际写入数量。
     */
    private static int spreadGroupIntoSlots(
            ItemStacksResourceHandler handler,
            ItemResource resource,
            int totalCount,
            List<Integer> targetSlots
    ) {
        int n = targetSlots.size();
        if (resource.isEmpty() || n <= 0 || totalCount <= 0) {
            return 0;
        }

        int[] capacities = new int[n];
        int totalCapacity = 0;

        for (int i = 0; i < n; i++) {
            int slot = targetSlots.get(i);
            capacities[i] = getSlotCapacity(handler, slot, resource);
            totalCapacity += capacities[i];
        }

        int toPlace = Math.min(totalCount, totalCapacity);
        if (toPlace <= 0) {
            return 0;
        }

        int[] assigned = new int[n];

        if (toPlace < n) {
            int left = toPlace;

            for (int i = 0; i < n && left > 0; i++) {
                if (capacities[i] <= 0) continue;

                assigned[i] = 1;
                left--;
            }
        } else {
            int base = toPlace / n;
            int rem = toPlace % n;
            int left = toPlace;

            for (int i = 0; i < n; i++) {
                int want = base + (i < rem ? 1 : 0);
                int put = Math.min(want, capacities[i]);

                assigned[i] = put;
                left -= put;
            }

            for (int i = 0; i < n && left > 0; i++) {
                int room = capacities[i] - assigned[i];
                if (room <= 0) continue;

                int add = Math.min(room, left);
                assigned[i] += add;
                left -= add;
            }
        }

        int written = 0;

        for (int i = 0; i < n; i++) {
            int count = assigned[i];
            if (count <= 0) continue;

            handler.set(targetSlots.get(i), resource, count);
            written += count;
        }

        return written;
    }

    private static int estimatePerSlotCapacity(ItemStacksResourceHandler handler, ItemResource resource) {
        if (resource.isEmpty()) {
            return 0;
        }

        int max = 0;

        for (int i = 0; i < handler.size(); i++) {
            max = Math.max(max, getSlotCapacity(handler, i, resource));
        }

        return max;
    }

    private static int getSlotCapacity(ItemStacksResourceHandler handler, int slot, ItemResource resource) {
        if (resource.isEmpty()) {
            return 0;
        }

        if (!handler.isValid(slot, resource)) {
            return 0;
        }

        return Math.min(
                handler.getCapacityAsInt(slot, resource),
                resource.getMaxStackSize()
        );
    }

    private static int capacitySum(
            ItemStacksResourceHandler handler,
            ItemResource resource,
            List<Integer> slots
    ) {
        int sum = 0;

        for (int slot : slots) {
            sum += getSlotCapacity(handler, slot, resource);
        }

        return sum;
    }

    private static List<Integer> takeBestSlots(
            ItemStacksResourceHandler handler,
            List<Integer> availableSlots,
            ItemResource resource,
            int count
    ) {
        List<Integer> picked = new ArrayList<>(count);

        for (int i = 0; i < count; i++) {
            Integer slot = takeBestSlot(handler, availableSlots, resource);
            if (slot == null) {
                break;
            }
            picked.add(slot);
        }

        return picked;
    }

    private static Integer takeBestSlot(
            ItemStacksResourceHandler handler,
            List<Integer> availableSlots,
            ItemResource resource
    ) {
        int bestIndexInList = -1;
        int bestCapacity = 0;

        for (int i = 0; i < availableSlots.size(); i++) {
            int slot = availableSlots.get(i);
            int capacity = getSlotCapacity(handler, slot, resource);

            if (capacity > bestCapacity) {
                bestCapacity = capacity;
                bestIndexInList = i;
            }
        }

        if (bestIndexInList < 0) {
            return null;
        }

        return availableSlots.remove(bestIndexInList);
    }

    private static List<Integer> allSlots(ItemStacksResourceHandler handler) {
        List<Integer> slots = new ArrayList<>(handler.size());

        for (int i = 0; i < handler.size(); i++) {
            slots.add(i);
        }

        return slots;
    }

    private static void clear(ItemStacksResourceHandler handler) {
        for (int i = 0; i < handler.size(); i++) {
            handler.set(i, ItemResource.EMPTY, 0);
        }
    }

    private static List<SlotContent> snapshot(ItemStacksResourceHandler handler) {
        List<SlotContent> snapshot = new ArrayList<>(handler.size());

        for (int i = 0; i < handler.size(); i++) {
            snapshot.add(new SlotContent(
                    handler.getResource(i),
                    handler.getAmountAsInt(i)
            ));
        }

        return snapshot;
    }

    private static void restore(ItemStacksResourceHandler handler, List<SlotContent> snapshot) {
        for (int i = 0; i < snapshot.size(); i++) {
            SlotContent content = snapshot.get(i);
            handler.set(i, content.resource, content.amount);
        }
    }

    private static int ceilDiv(int a, int b) {
        if (b <= 0) return Integer.MAX_VALUE;
        return (a + b - 1) / b;
    }

    private static final class StackGroup {
        final ItemResource resource;
        int totalCount;
        int assignedSlots;

        StackGroup(ItemResource resource) {
            this.resource = resource;
        }
    }

    private record SlotContent(ItemResource resource, int amount) {
    }
}