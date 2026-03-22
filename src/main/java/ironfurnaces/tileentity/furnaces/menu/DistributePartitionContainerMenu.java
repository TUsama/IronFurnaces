package ironfurnaces.tileentity.furnaces.menu;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import lombok.Getter;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class DistributePartitionContainerMenu extends AbstractContainerMenu {
    protected final ArrayList<Partition> partitions = new ArrayList<>();
    protected final IntList baseIndex = new IntArrayList();
    @Getter
    protected int sizeCount = 0;
    protected final List<QuickMoveRule> quickMoveRules = new ArrayList<>();

    protected DistributePartitionContainerMenu(@Nullable MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
    }

    protected Partition addPartition(Partition partition) {
        int start = this.sizeCount;

        partition.menuStartIndex(start);
        this.partitions.add(partition);
        this.baseIndex.add(start);

        for (int i = 0; i < partition.size; i++) {
            this.addSlot(partition.makeSlot(i));
        }

        this.sizeCount += partition.size;
        return partition;
    }


    protected QuickMoveRule[] addQuickMoveRule(QuickMoveRule... rule) {
        this.quickMoveRules.addAll(Arrays.asList(rule));
        return rule;
    }

    protected Iterable<QuickMoveRule> addQuickMoveRule(Iterable<QuickMoveRule> rule) {
        for (QuickMoveRule quickMoveRule : rule) {
            this.quickMoveRules.add(quickMoveRule);
        }
        return rule;
    }


    public int findPartitionIndexBySlot(int slotIndex) {
        for (int i = 0; i < this.partitions.size(); i++) {
            if (this.partitions.get(i).contains(slotIndex)) {
                return i;
            }
        }
        return -1;
    }

    @Nullable
    public Partition findPartitionBySlot(int slotIndex) {
        int index = findPartitionIndexBySlot(slotIndex);
        return index >= 0 ? this.partitions.get(index) : null;
    }

    public boolean isSlotInteractable(int slotIndex) {
        Partition partition = this.findPartitionBySlot(slotIndex);
        return partition != null && partition.isAvailable();
    }


    public NonNullList<ItemStack> getInteractableItems() {
        NonNullList<ItemStack> result = NonNullList.create();

        for (int p = 0; p < this.partitions.size(); p++) {
            Partition partition = this.partitions.get(p);
            if (!partition.isAvailable()) {
                continue;
            }
            result.addAll(partition.getItemStacksInPartition(this.slots));
        }

        return result;
    }



    /*
    @Override
    public NonNullList<ItemStack> getItems() {
        return getInteractableItems();
    }
*/
/*
    @Override
    public void setItem(int slotId, int stateId, ItemStack stack) {
        int take = 0;
        for (Partition partition : this.partitions) {
            if (!partition.isAvailable() || !partition.containsMenuSlot(slotId)) {
                take += partition.size;
                continue;
            }
            System.out.println("total size: " + this.slots.size());
            System.out.println("take: " + take);
            System.out.println("slotId: " + slotId);
            super.setItem(slotId + take, stateId, stack);
        }

    }
*/

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        if (slotIndex < 0 || slotIndex >= this.slots.size()) {
            return ItemStack.EMPTY;
        }

        Slot sourceSlot = this.slots.get(slotIndex);
        if (!sourceSlot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copy = sourceStack.copy();

        Partition sourcePartition = this.findPartitionBySlot(slotIndex);
        if (sourcePartition == null) {
            return ItemStack.EMPTY;
        }

        QuickMoveContext context = new QuickMoveContext(
                this,
                player,
                slotIndex,
                sourceSlot,
                sourcePartition,
                sourceStack
        );

        boolean moved = false;

        for (QuickMoveRule rule : this.quickMoveRules) {
            if (!rule.matches(context)) {
                continue;
            }

            if (!rule.to().isAvailable()) {
                continue;
            }
            int start = rule.to().menuStartIndex;
            int end = rule.to().getMenuEndIndexExclusive();

            if (this.moveItemStackTo(sourceStack, start, end, rule.reverse())) {
                moved = true;
                if (sourceStack.isEmpty()) {
                    break;
                }
            }
        }

        if (!moved) {
            return ItemStack.EMPTY;
        }

        if (sourceStack.isEmpty()) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }

        sourceSlot.onTake(player, sourceStack);
        return copy;
    }


    protected GridPartition createPlayerMainInventoryPartition(Inventory playerInventory) {
        return new GridPartition(27, new Vector2i(8, 84), () -> true, new PlayerMainInvWrapper(playerInventory), 9, 9);
    }

    protected GridPartition createPlayerHotbarPartition(Inventory playerInventory) {
        return new GridPartition(9, new Vector2i(8, 142), () -> true, new PlayerMainInvWrapper(playerInventory), 0, 9);
    }
}
