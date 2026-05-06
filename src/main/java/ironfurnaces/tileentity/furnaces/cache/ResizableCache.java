package ironfurnaces.tileentity.furnaces.cache;

import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.cache.stat.FillStats;
import ironfurnaces.tileentity.furnaces.pattern.IFurnaceStats;
import ironfurnaces.tileentity.furnaces.pattern.mode.AbstractFurnaceModeHandler;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.experimental.ExtensionMethod;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.function.Consumer;


@ExtensionMethod(ItemUtil.class)
public abstract class ResizableCache extends ItemStacksResourceHandler implements ICacheIndex,ICacheFillStats, INeedUpdate{
    protected AbstractFurnaceModeHandler mode;
    @Nullable
    private int[] cacheSlotArray;
    private final FillStats fill_stats = new FillStats();

    public ResizableCache(AbstractFurnaceModeHandler mode) {
        super(NonNullList.withSize(1, ItemStack.EMPTY));
        this.mode = mode;
    }

    public ResizableCache(int size, AbstractFurnaceModeHandler mode) {
        super(size);
        this.mode = mode;
    }

    public int getSlots(){
        return this.stacks.size();
    }

    @Override
    public void set(int index, ItemResource resource, int amount) {
        if (index >= getSlots()) return;
        super.set(index, resource, amount);
    }

    @Override
    public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
        if (index >= getSlots()) return 0;
        return super.insert(index, resource, amount, transaction);
    }

    @Override
    public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
        if (index >= getSlots()) return 0;
        return super.extract(index, resource, amount, transaction);
    }

    public ItemStack getStackInSlot(int index){
        return ItemUtil.getStack(this, index);
    }


    public void handleStacksInUnavailableSlots(Level level, Consumer<Int2ObjectMap<ItemStack>> consumer) {
        Int2ObjectMap<ItemStack> stacksInUnavailableSlots = findStacksInUnavailableSlots(level);
        consumer.accept(stacksInUnavailableSlots);
    }

    public Int2ObjectMap<ItemStack> findStacksInUnavailableSlots(Level level) {
        Int2ObjectOpenHashMap<ItemStack> objectInt2ObjectOpenHashMap = new Int2ObjectOpenHashMap<>();
        if (level == null) return objectInt2ObjectOpenHashMap;
        for (int i = getSlots(); i < stacks.size(); i++) {
            ItemStack stack = getStackInSlot(i);
            if (stack.isEmpty()) continue;
            objectInt2ObjectOpenHashMap.put(i, stack);
        }
        return objectInt2ObjectOpenHashMap;
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
            int cap = Math.min(getCapacityAsInt(i, getResource(i)), s.getMaxStackSize());
            if (cap > 0) {
                fill_stats.fill_sum += (float) s.getCount() / (float) cap;
            }
        }
    }

    public FillStats getFillStats() {
        return fill_stats;
    }

    @Override
    public void update(AbstractFurnaceModeHandler mode, IRecipeTypeHandler recipeTypeHandler, IFurnaceStats<?> stats, FurnacePatternBlockEntity blockEntity) {
        this.mode = mode;
        resizeSlots(mode, recipeTypeHandler, stats, blockEntity);
        int slots = this.getSlots();
        int[] ints = new int[slots];
        for (int i = 0; i < slots; i++) {
            ints[i] = i;
        }
        cacheSlotArray = ints;
    }

    protected abstract int updateSlotAmount(AbstractFurnaceModeHandler mode, IRecipeTypeHandler recipeTypeHandler, IFurnaceStats<?> stats, FurnacePatternBlockEntity blockEntity);

    private void resizeSlots(AbstractFurnaceModeHandler mode, IRecipeTypeHandler recipeTypeHandler, IFurnaceStats<?> stats, FurnacePatternBlockEntity blockEntity){
        int newSize = updateSlotAmount(mode, recipeTypeHandler, stats, blockEntity);
        NonNullList<ItemStack> oldStacks = this.stacks;
        if (oldStacks.size() != newSize){
            blockEntity.closeMenu();
            NonNullList<ItemStack> newList = NonNullList.withSize(newSize, ItemStack.EMPTY);

            int keepSize = Math.min(oldStacks.size(), newSize);
            for (int i = 0; i < keepSize; i++) {
                newList.set(i, oldStacks.get(i).copy());
            }

            // 先处理溢出部分，避免丢物品
            if (oldStacks.size() > newSize) {
                ArrayList<ItemStack> itemStacks = new ArrayList<>();
                for (int i = newSize; i < oldStacks.size(); i++) {
                    ItemStack overflow = oldStacks.get(i);
                    if (!overflow.isEmpty()) {
                        itemStacks.add(overflow.copy());
                    }
                }
                blockEntity.returnOrDropStack(itemStacks, blockEntity.getOwner());
            }

            this.stacks = newList;
            recomputeFillStats();
        }

    }

    @Override
    public int[] getCacheIndex() {
        return cacheSlotArray;
    }


}
