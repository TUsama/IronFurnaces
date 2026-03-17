package ironfurnaces.tileentity.furnaces.process;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.FurnaceMode;
import ironfurnaces.tileentity.furnaces.cache.IModeSensitive;
import ironfurnaces.tileentity.furnaces.cache.IPatternSensitive;
import ironfurnaces.tileentity.furnaces.pattern.FurnacePattern;
import it.unimi.dsi.fastutil.ints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.util.ExtraCodecs;

import java.util.*;
import java.util.function.Consumer;
@Accessors(fluent = true, chain = true)
public class ProcessingInstanceManager implements IModeSensitive, IPatternSensitive {

    public static final Codec<ProcessingInstanceManager> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ProcessingInstance.DISPATCH_CODEC.listOf().fieldOf("instances").forGetter(ProcessingInstanceManager::instances),
                    ExtraCodecs.POSITIVE_INT.listOf().xmap(x -> ((IntSet) new IntOpenHashSet(x)), ArrayList::new).fieldOf("blockingIndexes").forGetter(ProcessingInstanceManager::blockingIndexes)
            ).apply(instance, ProcessingInstanceManager::new));
    @Getter
    private final List<ProcessingInstance> instances;
    @Getter
    private IntSet blockingIndexes;
    @Setter
    private Consumer<ProcessingInstanceManager> clearInstanceCallback;
    private IntSet filledIndex = new IntOpenHashSet();

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
            System.out.println("ready to test contains for " + fromIndex);
            if (this.blockingIndexes.contains(fromIndex)) {
                System.out.println(fromIndex + "is blocking!");
                continue;
            }

            switch (next.tick(tile)){
                case SUCCESS -> {}
                case BLOCKED -> {

                    this.blockingIndexes.add(fromIndex);

                }
                case DISCARD -> {
                    iterator.remove();
                    this.blockingIndexes.remove(fromIndex);
                    this.filledIndex.remove(fromIndex);
                }
                case DONE -> {
                    next.whenDone(tile);
                    iterator.remove();
                    this.blockingIndexes.remove(fromIndex);
                    this.filledIndex.remove(fromIndex);
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

    public boolean isWaiting(){
        return !this.instances.isEmpty();
    }

    public IntSet getWorkingIndexes(){
        return filledIndex;
    }

    public boolean isGeneratingEnergy() {
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
    public void updateFurnaceMode(FurnaceMode mode) {
        this.instances.clear();
        blockingIndexes.clear();
        filledIndex.clear();
        if (clearInstanceCallback != null){
            clearInstanceCallback.accept(this);
        }
    }

    @Override
    public void updateFurnacePattern(FurnacePattern pattern) {
        for (ProcessingInstance instance : this.instances) {
            instance.whenChangePattern(pattern);
        }
    }
}
