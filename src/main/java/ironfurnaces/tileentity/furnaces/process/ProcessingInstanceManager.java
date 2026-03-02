package ironfurnaces.tileentity.furnaces.process;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ironfurnaces.tileentity.furnaces.BlockIronFurnaceTileBaseV2;
import ironfurnaces.tileentity.furnaces.FurnaceMode;
import ironfurnaces.tileentity.furnaces.cache.IModeSensitive;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ProcessingInstanceManager implements IModeSensitive {

    public static final Codec<ProcessingInstanceManager> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ProcessingInstance.DISPATCH_CODEC.listOf().fieldOf("instances").forGetter(x -> x.instances),
                    Codec.BOOL.fieldOf("blocking").forGetter(x -> x.isBlocking)
            ).apply(instance, ProcessingInstanceManager::new));

    private final List<ProcessingInstance> instances;
    @Getter
    private boolean isBlocking;

    public ProcessingInstanceManager(List<ProcessingInstance> instances) {
        this.instances = instances;
    }

    private ProcessingInstanceManager(List<ProcessingInstance> instances, boolean isBlocking) {
        this.instances = instances;
        this.isBlocking = isBlocking;
    }

    public void manage(BlockIronFurnaceTileBaseV2 tile){
        if (isBlocking) return;
        Iterator<ProcessingInstance> iterator = instances.iterator();
        while (iterator.hasNext()){
            ProcessingInstance next = iterator.next();
            switch (next.tick(tile)){
                case SUCCESS -> {}
                case BLOCKED -> {
                    this.isBlocking = true;
                }
                case DISCARD -> iterator.remove();
                case DONE -> {
                    next.whenDone(tile);
                    iterator.remove();
                }
            }
            if (isBlocking) break;
        }
    }

    public void refreshBlockingState(){
        if (isBlocking){
            this.isBlocking = false;
        }
    }

    public void addInstance(ProcessingInstance instance){
        this.instances.add(instance);
    }

    @Override
    public void updateFurnaceMode(FurnaceMode mode) {
        this.instances.clear();
        refreshBlockingState();
    }
}
