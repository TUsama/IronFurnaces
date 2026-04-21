package ironfurnaces.tileentity.furnaces.process;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ironfurnaces.capability.rainbow.OwnerRainbowContextHelper;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.cache.*;
import ironfurnaces.tileentity.furnaces.pattern.IFurnaceStats;
import ironfurnaces.tileentity.furnaces.pattern.mode.AbstractFurnaceModeHandler;
import it.unimi.dsi.fastutil.ints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
//? 1.20.1 {
import net.minecraftforge.items.ItemHandlerHelper;
//? } else {
/*import net.minecraft.world.item.crafting.RecipeHolder;
*///?}


import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
@Accessors(fluent = true, chain = true)
public class ProcessingInstanceManager implements INeedUpdate {

    public static final Codec<ProcessingInstanceManager> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ProcessingInstance.DISPATCH_CODEC.listOf().fieldOf("instances").forGetter(ProcessingInstanceManager::instances),
                    ExtraCodecs.POSITIVE_INT.listOf().xmap(IntOpenHashSet::new, ArrayList::new).fieldOf("blockingIndexes").forGetter(ProcessingInstanceManager::blockingIndexes)
            ).apply(instance, ProcessingInstanceManager::new));
    @Getter
    private final List<ProcessingInstance> instances;
    @Getter
    private IntOpenHashSet blockingIndexes;
    @Setter
    private Consumer<ProcessingInstanceManager> clearInstanceCallback;
    private IntSet filledIndex = new IntOpenHashSet();
    private final Int2ObjectMap<CachedRecipeEntry> recipeCache = new Int2ObjectOpenHashMap<>();
    private boolean makeDirtyOnThisTimeClearAllInstances = false;

    public ProcessingInstanceManager(List<ProcessingInstance> instances) {
        this.instances = instances;
        this.blockingIndexes = new IntOpenHashSet();
    }

    private ProcessingInstanceManager(List<ProcessingInstance> instances, IntOpenHashSet blockingIndexes) {
        this.instances = new ArrayList<>(instances);
        this.blockingIndexes = blockingIndexes;
        for (ProcessingInstance instance : this.instances) {
            this.filledIndex.add(instance.fromIndex);
        }

    }

    public void manage(FurnacePatternBlockEntity tile){
        ListIterator<ProcessingInstance> iterator = instances.listIterator();
        //System.out.println("the instances size is " + instances.size() + ", the instances are " + instances);
        while (iterator.hasNext()){
            //并不多余，这一段是用来处理普通熔炉模式下的，在没有新启动lit状态下再次开始烧制物品的情况。
            //其他模式下不会，只有普通模式会出现虽然lit但什么也没在烧的情况。
            //会出现多余检查，但极少，因为一般来说只有非常前期的情况下才会用普通模式，而此时的熔炉数量不会很多。
            if (tile.getMode().isFurnace() && makeDirtyOnThisTimeClearAllInstances && !hasInstances() && tile.hasLevel() && tile.getLevel() instanceof ServerLevel serverLevel){
                OwnerRainbowContextHelper.markDirtyByOwnerUuid(serverLevel, tile.getOwnerUuid());
                makeDirtyOnThisTimeClearAllInstances = false;
            }
            ProcessingInstance next = iterator.next();
            int fromIndex = next.fromIndex;
            if (this.blockingIndexes.contains(fromIndex)) {
                continue;
            }

            switch (next.tick(tile)){
                case SUCCESS -> {}
                case BLOCKED -> this.blockingIndexes.add(fromIndex);
                case DISCARD -> {
                    iterator.remove();
                    this.blockingIndexes.remove(fromIndex);
                    this.filledIndex.remove(fromIndex);
                    recipeCache.remove(fromIndex);
                }
                case DONE -> {
                    next.whenDone(tile);
                    iterator.remove();
                    this.blockingIndexes.remove(fromIndex);
                    this.filledIndex.remove(fromIndex);
                    recipeCache.remove(fromIndex);
                }
            }
        }

        if (!makeDirtyOnThisTimeClearAllInstances && !hasInstances() && tile.hasLevel() && tile.getLevel() instanceof ServerLevel serverLevel){
            OwnerRainbowContextHelper.markDirtyByOwnerUuid(serverLevel, tile.getOwnerUuid());
            makeDirtyOnThisTimeClearAllInstances = true;
        }
    }

    public void refreshBlockingState(int index){
        this.blockingIndexes.remove(index);
    }

    public void addInstance(ProcessingInstance instance){
        if (filledIndex.add(instance.fromIndex)){
            this.instances.add(instance);
        }
    }

    private CachedRecipeEntry getOrCreateCache(int fromIndex) {
        return recipeCache.computeIfAbsent(fromIndex, i -> new CachedRecipeEntry());
    }

    public boolean needLit(FurnacePatternBlockEntity blockEntity) {
        for (ProcessingInstance instance : this.instances) {
            if (this.blockingIndexes.contains(instance.fromIndex)) continue;
            if (instance.needLit(blockEntity)) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    public Recipe getCachedCookingRecipe(FurnacePatternBlockEntity tile, ProcessingInstance instance) {
        int fromIndex = instance.fromIndex;
        InputCache input = tile.getInput();
        if (fromIndex >= input.getSlots()) return null;
        ItemStack current = input.getStackInSlot(fromIndex);
        CachedRecipeEntry entry = getOrCreateCache(fromIndex);

        if (current.isEmpty()) {
            entry.clear();
            return null;
        }

        if (!entry.dirty() && entry.matches(current)) {
            return entry.recipe();
        }

        var lookedUp = tile.getRecipe(current)
                //? if >1.20.1
                //.map(RecipeHolder::value)
                .orElse(null);

        entry.update(current, lookedUp);
        return lookedUp;
    }

    public void invalidateRecipeCache(int index) {
        CachedRecipeEntry entry = recipeCache.get(index);
        if (entry != null) {
            entry.invalidate();
        }
    }

    public boolean hasInstances(){
        return !this.instances.isEmpty();
    }

    public boolean isAllBlocking(){
        return hasInstances() && this.blockingIndexes.size() == this.instances.size();
    }

    public IntSet getWorkingIndexes(){
        return filledIndex;
    }

    public boolean isGeneratingEnergy() {
        //don't use getAllWorkingGenerateInstances().isEmpty()
        for (ProcessingInstance instance : instances) {
            if (blockingIndexes.contains(instance.fromIndex)) {
                continue;
            }
            if (instance instanceof Generate) {
                return true;
            }
        }
        return false;
    }



    public List<Generate> getAllGenerateInstances(){
        ArrayList<Generate> generates = new ArrayList<>();
        for (ProcessingInstance instance : instances) {
            if (instance instanceof Generate generateInstances) generates.add(generateInstances);
        }
        return generates;
    }


    @Override
    public void update(AbstractFurnaceModeHandler mode, IRecipeTypeHandler recipeTypeHandler, IFurnaceStats<?> stats, FurnacePatternBlockEntity blockEntity) {
        this.instances.clear();
        blockingIndexes.clear();
        filledIndex.clear();
        if (clearInstanceCallback != null){
            clearInstanceCallback.accept(this);
        }

        for (ProcessingInstance instance : this.instances) {
            instance.whenChangeStats(stats);
        }
        recipeCache.clear();
    }

    public static class CachedRecipeEntry {
        private ItemStack fingerprint = ItemStack.EMPTY;
        @Nullable
        private Recipe recipe;
        private boolean dirty = true;

        public boolean matches(ItemStack current) {
            if (fingerprint.isEmpty() || current.isEmpty()) return false;
            return ItemHandlerHelper.canItemStacksStack(fingerprint, current);
        }

        public void update(ItemStack current, @Nullable Recipe recipe) {
            this.fingerprint = current.copy();
            this.recipe = recipe;
            this.dirty = false;
        }

        public void invalidate() {
            this.dirty = true;
        }

        public void clear() {
            this.fingerprint = ItemStack.EMPTY;
            this.recipe = null;
            this.dirty = true;
        }

        @Nullable
        public Recipe recipe() {
            return recipe;
        }

        public boolean dirty() {
            return dirty;
        }
    }
}
