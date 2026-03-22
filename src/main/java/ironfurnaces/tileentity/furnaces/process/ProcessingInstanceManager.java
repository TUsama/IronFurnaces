package ironfurnaces.tileentity.furnaces.process;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.FurnaceMode;
import ironfurnaces.tileentity.furnaces.cache.IModeSensitive;
import ironfurnaces.tileentity.furnaces.cache.IPatternSensitive;
import ironfurnaces.tileentity.furnaces.pattern.EffectiveFurnaceStats;
import it.unimi.dsi.fastutil.ints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
@Accessors(fluent = true, chain = true)
public class ProcessingInstanceManager implements IModeSensitive, IPatternSensitive {

    public static final Codec<ProcessingInstanceManager> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ProcessingInstance.DISPATCH_CODEC.listOf().fieldOf("instances").forGetter(ProcessingInstanceManager::instances),
                    ExtraCodecs.POSITIVE_INT.listOf().xmap(IntOpenHashSet::new, ArrayList::new).fieldOf("blockingIndexes").forGetter(x -> (IntOpenHashSet) x.blockingIndexes())
            ).apply(instance, ProcessingInstanceManager::new));
    @Getter
    private final List<ProcessingInstance> instances;
    @Getter
    private IntSet blockingIndexes;
    @Setter
    private Consumer<ProcessingInstanceManager> clearInstanceCallback;
    private IntSet filledIndex = new IntOpenHashSet();
    private final Int2ObjectMap<CachedRecipeEntry> recipeCache = new Int2ObjectOpenHashMap<>();

    public ProcessingInstanceManager(List<ProcessingInstance> instances) {
        this.instances = instances;
        this.blockingIndexes = new IntOpenHashSet();
    }

    private ProcessingInstanceManager(List<ProcessingInstance> instances, IntSet blockingIndexes) {
        this.instances = new ArrayList<>(instances);
        this.blockingIndexes = blockingIndexes;
        for (ProcessingInstance instance : this.instances) {
            this.filledIndex.add(instance.fromIndex);
        }

    }

    public void manage(FurnacePatternBlockEntity tile){
        ListIterator<ProcessingInstance> iterator = instances.listIterator();
        while (iterator.hasNext()){
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
    }

    public void refreshBlockingState(int index){
        this.blockingIndexes.remove(index);
    }

    public void addInstance(ProcessingInstance instance){

        this.instances.add(instance);
        filledIndex.add(instance.fromIndex);
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
    public Recipe getCachedCookingRecipe(FurnacePatternBlockEntity tile, int fromIndex) {
        ItemStack current = tile.getInput().getStackInSlot(fromIndex);
        CachedRecipeEntry entry = getOrCreateCache(fromIndex);

        if (current.isEmpty()) {
            entry.clear();
            return null;
        }

        if (!entry.dirty() && entry.matches(current)) {
            return entry.recipe();
        }

        var lookedUp = tile.getRecipe(current)
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

    public boolean isWaiting(){
        return !this.instances.isEmpty();
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

    public List<Generate> getAllWorkingGenerateInstances(){
        ArrayList<Generate> generates = new ArrayList<>();
        for (ProcessingInstance instance : instances) {
            if (instance instanceof Generate generateInstances && !blockingIndexes.contains(instance.fromIndex)) generates.add(generateInstances);
        }
        return generates;
    }

    @Override
    public void updateFurnaceMode(FurnaceMode mode) {
        this.instances.clear();
        blockingIndexes.clear();
        filledIndex.clear();
        if (clearInstanceCallback != null){
            clearInstanceCallback.accept(this);
        }
        recipeCache.clear();
    }

    @Override
    public void updateFurnacePattern(EffectiveFurnaceStats stats) {
        for (ProcessingInstance instance : this.instances) {
            instance.whenChangePattern(stats);
        }
        for (CachedRecipeEntry entry : recipeCache.values()) {
            entry.invalidate();
        }
    }

    public static class CachedRecipeEntry {
        private ItemStack fingerprint = ItemStack.EMPTY;
        @Nullable
        private Recipe recipe;
        private boolean dirty = true;

        public boolean matches(ItemStack current) {
            if (fingerprint.isEmpty() || current.isEmpty()) return false;
            return ItemStack.isSameItemSameTags(fingerprint, current);
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
