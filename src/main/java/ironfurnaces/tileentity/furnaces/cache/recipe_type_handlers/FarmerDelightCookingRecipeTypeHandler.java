package ironfurnaces.tileentity.furnaces.cache.recipe_type_handlers;

import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.cache.IRecipeTypeHandler;
import ironfurnaces.tileentity.furnaces.cache.InputCache;
import ironfurnaces.tileentity.furnaces.pattern.IFurnaceStats;
import ironfurnaces.tileentity.furnaces.pattern.mode.AbstractFurnaceModeHandler;
import ironfurnaces.tileentity.furnaces.process.ProcessingInstanceManager;
import ironfurnaces.tileentity.furnaces.process.compat.Cooking;
import ironfurnaces.tileentity.furnaces.process.compat.MealTransfer;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.ArrayUtils;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;
import vectorwing.farmersdelight.common.registry.ModRecipeTypes;
import vectorwing.farmersdelight.integration.jei.FDRecipeTypes;

import java.util.*;
import java.util.function.Function;

public class FarmerDelightCookingRecipeTypeHandler implements IRecipeTypeHandler {
    @Setter
    @Getter
    private CookingPotRecipe lastRecipe;
    @Getter
    @Setter
    private CookingPotRecipe lockedRecipe;
    private ResourceLocation tempResourceLocation;

    public boolean isLocking;

    public static FarmerDelightCookingRecipeTypeHandler getInstance() {
        return new FarmerDelightCookingRecipeTypeHandler();
    }


    @Override
    public RecipeType<?> getRecipeType() {
        return ModRecipeTypes.COOKING.get();
    }

    @Override
    public Optional<? extends Recipe> getRecipe(FurnacePatternBlockEntity blockEntity, Function<RecipeType<?>, RecipeManager.CachedCheck<Container, ?>> quickCheck, List<ItemStack> stacks) {
        return quickCheck.apply(ModRecipeTypes.COOKING.get()).getRecipeFor(new RecipeWrapper(blockEntity.getInput()), blockEntity.getLevel());
    }

    @Override
    public boolean allowPlaceItem(FurnacePatternBlockEntity blockEntity, Function<RecipeType<?>, RecipeManager.CachedCheck<Container, ?>> quickCheck, List<ItemStack> stacks) {
        //let the LockedSlot handle the place
        return true;
    }

    @Override
    public StringBuilder buildTextureName(StringBuilder stringBuilder) {
        return stringBuilder;
    }

    @Override
    public boolean testBurnable(ItemStack stack, FurnacePatternBlockEntity blockEntity) {
        return false;
    }

    @Override
    public void provideInstance(FurnacePatternBlockEntity blockEntity) {
        IFurnaceStats<?> usedStats = blockEntity.getUsedStats();
        ProcessingInstanceManager instanceManager = blockEntity.getInstanceManager();
        blockEntity.getRecipe(ItemStack.EMPTY)
                .filter(x -> x instanceof CookingPotRecipe)
                .ifPresent(x -> {
                    if (!instanceManager.getWorkingIndexes().contains(0)) instanceManager.addInstance(new Cooking(usedStats.smeltTick(), 1));
                });
        ItemStack meal = blockEntity.getOutput().getStackInSlot(Cooking.MEAL);
        if (!blockEntity.getViewOnly().getStackInSlot(Cooking.PREMEAL).isEmpty() && meal.getCount() < meal.getMaxStackSize() && !instanceManager.getWorkingIndexes().contains(1)) {
            instanceManager.addInstance(new MealTransfer());
        }
    }


    @Override
    public List<mezz.jei.api.recipe.RecipeType<?>> getShownRecipeTypes(AbstractFurnaceModeHandler mode) {
        return List.of(FDRecipeTypes.COOKING);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compoundTag = new CompoundTag();
        if (lockedRecipe != null) {
            compoundTag.putString("CookingPotRecipeId", lockedRecipe.getId().toString());
        }

        return compoundTag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains("CookingPotRecipeId")) {
            this.tempResourceLocation = IronFurnaces.parse(nbt.getString("CookingPotRecipeId"));
        }

    }

    public void resetLock() {
        this.lockedRecipe = null;
        this.tempResourceLocation = null;
        this.isLocking = false;
    }

    public void setLockedToLastRecipe() {
        if (lastRecipe != null){
            this.lockedRecipe = lastRecipe;
            this.isLocking = true;
        }

    }

    public boolean canInsertToThisSlot(int i, ItemStack stack, Level level) {
        if (lockedRecipe == null) {
            if (tempResourceLocation != null) {
                level.getRecipeManager().byKey(tempResourceLocation)
                        .filter(x -> x instanceof CookingPotRecipe)
                        .ifPresent(x -> lockedRecipe = (CookingPotRecipe) x);
                tempResourceLocation = null;
            } else {
                isLocking = false;
                return true;
            }

        }
        isLocking = true;
        NonNullList<Ingredient> ingredients = lockedRecipe.getIngredients();
        if (i > ingredients.size() - 1) {
            return false;
        } else {
            return ingredients.get(i).test(stack);
        }

    }

    public ResourceLocation showAvailableItem(int i, long gameTime) {
        if (lockedRecipe == null) return null;
        if (i < 0 || i >= lockedRecipe.getIngredients().size()) return null;

        ItemStack[] items = lockedRecipe.getIngredients().get(i).getItems();
        if (ArrayUtils.isEmpty(items)) return null;

        int period = 20; // 每秒切换一次
        int index = (int) ((gameTime / period) % items.length);

        return ForgeRegistries.ITEMS.getKey(items[index].getItem());
    }

    public List<Component> buildTooltips(Level level){
        ArrayList<Component> components = new ArrayList<>();
        if (this.lockedRecipe != null){
            ItemStack resultItem = lockedRecipe.getResultItem(level.registryAccess());
            components.add(Component.translatable("screen.ironfurnaces.compat.farmer_delight.current_locked_recipe", Component.literal(resultItem.getCount() + "x ").append(resultItem.getDisplayName())));
        } else {
            components.add(Component.translatable("screen.ironfurnaces.compat.farmer_delight.no_current_locked_recipe"));
            if (lastRecipe != null){
                ItemStack resultItem = lastRecipe.getResultItem(level.registryAccess());
                components.add(Component.translatable("screen.ironfurnaces.compat.farmer_delight.last_recipe", Component.literal(resultItem.getCount() + "x ").append(resultItem.getDisplayName())));
                components.add(Component.translatable("screen.ironfurnaces.compat.farmer_delight.click_to_lock"));
            } else {
                components.add(Component.translatable("screen.ironfurnaces.compat.farmer_delight.no_last_recipe"));
            }
        }
        return components;
    }

    public List<ItemStack> sortByCurrentLockedRecipe(IItemHandlerModifiable modifiable) {


        List<ItemStack> leftovers = new ArrayList<>();

        if (this.lockedRecipe == null) {
            for (int slot = 0; slot < modifiable.getSlots(); slot++) {
                ItemStack stack = modifiable.getStackInSlot(slot);
                if (!stack.isEmpty()) {
                    leftovers.add(stack.copy());
                    modifiable.setStackInSlot(slot, ItemStack.EMPTY);
                }
            }
            return leftovers;
        }

        NonNullList<Ingredient> ingredients = lockedRecipe.getIngredients();
        int totalSlots = modifiable.getSlots();
        int arrangeSlots = Math.min(totalSlots, ingredients.size());

        // 读取原物品，并按“同物品 + 同NBT/组件”合并
        Map<ItemStackKey, ItemEntry> entryMap = new LinkedHashMap<>();
        for (int slot = 0; slot < totalSlots; slot++) {
            ItemStack stack = modifiable.getStackInSlot(slot);
            if (stack.isEmpty()) continue;

            ItemStackKey key = new ItemStackKey(stack);
            entryMap.computeIfAbsent(key, k -> new ItemEntry(stack.copyWithCount(1)))
                    .totalCount += stack.getCount();
        }

        // 清空原槽位
        for (int slot = 0; slot < totalSlots; slot++) {
            modifiable.setStackInSlot(slot, ItemStack.EMPTY);
        }

        // 1. 检查所有物品，记录它们能够进入的槽位
        List<ItemEntry> entries = new ArrayList<>();
        for (ItemEntry entry : entryMap.values()) {
            for (int slot = 0; slot < arrangeSlots; slot++) {
                if (ingredients.get(slot).test(entry.prototype)) {
                    entry.acceptableSlots.add(slot);
                }
            }

            if (entry.acceptableSlots.isEmpty()) {
                leftovers.addAll(splitStacks(entry.prototype, entry.totalCount));
            } else {
                entries.add(entry);
            }
        }

        // 2. 按数量从大到小排序
        entries.sort(Comparator
                .comparingInt((ItemEntry e) -> e.totalCount).reversed()
                .thenComparingInt(e -> e.acceptableSlots.get(0)));

        ItemStack[] arranged = new ItemStack[arrangeSlots];
        Arrays.fill(arranged, ItemStack.EMPTY);

        // 记录每个槽位当前放的是哪个 entry
        ItemEntry[] ownerBySlot = new ItemEntry[arrangeSlots];

        // 3. 第一轮，依次塞入它能够塞入的第一个空槽位
        for (ItemEntry entry : entries) {
            for (int slot : entry.acceptableSlots) {
                if (arranged[slot].isEmpty() && entry.totalCount > 0) {
                    int amount = Math.min(
                            entry.totalCount,
                            Math.min(modifiable.getSlotLimit(slot), entry.prototype.getMaxStackSize())
                    );
                    if (amount > 0) {
                        arranged[slot] = entry.prototype.copyWithCount(amount);
                        ownerBySlot[slot] = entry;
                        entry.totalCount -= amount;
                    }
                    break;
                }
            }
        }

        // 4. 检查空槽位，若该槽位本可放入某些物品，则尝试从已有候选槽中均分数量补进去
        for (int emptySlot = 0; emptySlot < arrangeSlots; emptySlot++) {
            if (!arranged[emptySlot].isEmpty()) continue;

            // 找所有“本来能进这个空槽”的 entry
            List<ItemEntry> candidates = new ArrayList<>();
            for (ItemEntry entry : entries) {
                if (entry.acceptableSlots.contains(emptySlot)) {
                    candidates.add(entry);
                }
            }

            if (candidates.isEmpty()) {
                continue;
            }

            // 优先找已经占据了其兼容槽位的 entry，从中拆分
            ItemEntry selected = null;
            List<Integer> occupiedCompatibleSlots = Collections.emptyList();

            for (ItemEntry candidate : candidates) {
                List<Integer> occupied = new ArrayList<>();
                for (int slot : candidate.acceptableSlots) {
                    if (slot >= arrangeSlots) continue;
                    if (ownerBySlot[slot] == candidate && !arranged[slot].isEmpty()) {
                        occupied.add(slot);
                    }
                }

                if (!occupied.isEmpty()) {
                    selected = candidate;
                    occupiedCompatibleSlots = occupied;
                    break;
                }
            }

            // 如果没有已占用兼容槽位，但该物品还有剩余，也直接从剩余里塞
            if (selected == null) {
                for (ItemEntry candidate : candidates) {
                    if (candidate.totalCount > 0) {
                        selected = candidate;
                        break;
                    }
                }

                if (selected != null) {
                    int cap = Math.min(modifiable.getSlotLimit(emptySlot), selected.prototype.getMaxStackSize());
                    int amount = Math.min(cap, selected.totalCount);
                    if (amount > 0) {
                        arranged[emptySlot] = selected.prototype.copyWithCount(amount);
                        ownerBySlot[emptySlot] = selected;
                        selected.totalCount -= amount;
                    }
                }

                continue;
            }

            // 从 selected 的“已占用兼容槽”里均分到当前空槽
            List<Integer> allRelevantSlots = new ArrayList<>(occupiedCompatibleSlots);
            allRelevantSlots.add(emptySlot);
            allRelevantSlots.sort(Integer::compareTo);

            int totalCount = selected.totalCount;
            for (int slot : occupiedCompatibleSlots) {
                totalCount += arranged[slot].getCount();
            }

            int[] caps = new int[allRelevantSlots.size()];
            for (int i = 0; i < allRelevantSlots.size(); i++) {
                int slot = allRelevantSlots.get(i);
                caps[i] = Math.min(modifiable.getSlotLimit(slot), selected.prototype.getMaxStackSize());
            }

            int[] redistributed = distributeFairly(totalCount, caps);

            // 清空原来 selected 占据的兼容槽
            for (int slot : occupiedCompatibleSlots) {
                arranged[slot] = ItemStack.EMPTY;
                ownerBySlot[slot] = null;
            }

            selected.totalCount = 0;

            // 回填重新分配后的结果
            for (int i = 0; i < allRelevantSlots.size(); i++) {
                int slot = allRelevantSlots.get(i);
                int count = redistributed[i];
                if (count <= 0) continue;

                arranged[slot] = selected.prototype.copyWithCount(count);
                ownerBySlot[slot] = selected;
            }
        }

        // 5. 剩余未用完的匹配物品作为 leftovers 返回
        for (ItemEntry entry : entries) {
            if (entry.totalCount > 0) {
                leftovers.addAll(splitStacks(entry.prototype, entry.totalCount));
            }
        }

        // 写回 cache
        for (int slot = 0; slot < arrangeSlots; slot++) {
            modifiable.setStackInSlot(slot, arranged[slot]);
        }
        for (int slot = arrangeSlots; slot < totalSlots; slot++) {
            modifiable.setStackInSlot(slot, ItemStack.EMPTY);
        }

        return leftovers;
    }

    private static int[] distributeFairly(int total, int[] caps) {
        int n = caps.length;
        int[] result = new int[n];
        if (n == 0 || total <= 0) return result;

        int remaining = Math.min(total, Arrays.stream(caps).sum());

        // 先尽量让更多槽位非空
        for (int i = 0; i < n && remaining > 0; i++) {
            if (caps[i] > 0) {
                result[i]++;
                remaining--;
            }
        }

        // 再轮转补齐，达到尽量均分
        boolean progressed = true;
        while (remaining > 0 && progressed) {
            progressed = false;
            for (int i = 0; i < n && remaining > 0; i++) {
                if (result[i] < caps[i]) {
                    result[i]++;
                    remaining--;
                    progressed = true;
                }
            }
        }

        return result;
    }

    private static List<ItemStack> splitStacks(ItemStack prototype, int totalCount) {
        List<ItemStack> result = new ArrayList<>();
        int max = prototype.getMaxStackSize();

        while (totalCount > 0) {
            int take = Math.min(max, totalCount);
            result.add(prototype.copyWithCount(take));
            totalCount -= take;
        }
        return result;
    }

    private static final class ItemEntry {
        final ItemStack prototype;
        int totalCount;
        final List<Integer> acceptableSlots = new ArrayList<>();

        ItemEntry(ItemStack prototype) {
            this.prototype = prototype;
        }
    }

    private static final class ItemStackKey {
        private final Item item;
        private final CompoundTag tag;

        ItemStackKey(ItemStack stack) {
            this.item = stack.getItem();
            this.tag = stack.getTag() == null ? null : stack.getTag().copy();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof ItemStackKey other)) return false;
            return this.item == other.item && Objects.equals(this.tag, other.tag);
        }

        @Override
        public int hashCode() {
            return Objects.hash(item, tag);
        }
    }


    @Override
    public String toString() {
        return getRecipeType().toString();
    }
}
