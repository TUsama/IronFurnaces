package ironfurnaces.tileentity.furnaces.process;

import ironfurnaces.tileentity.furnaces.BlockIronFurnaceTileBaseV2;
import ironfurnaces.tileentity.furnaces.FurnaceMode;
import ironfurnaces.tileentity.furnaces.cache.IModeSensitive;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ProcessingInstanceManager implements IModeSensitive {
    private final List<ProcessingInstance> instances;
    @Getter
    private boolean isBlocking;
    private final BlockIronFurnaceTileBaseV2 tile;

    public ProcessingInstanceManager(List<ProcessingInstance> instances, BlockIronFurnaceTileBaseV2 tile) {
        this.instances = instances;
        this.tile = tile;
    }

    public void manage(){
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
        Iterator<ProcessingInstance> iterator = this.instances.iterator();
        while (iterator.hasNext()){
            ProcessingInstance next = iterator.next();
            if (!next.getRecipeType().equals(tile.getAugments().getCurrentRecipeType())) iterator.remove();
        }
    }
}
