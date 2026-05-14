package ironfurnaces.tileentity.furnaces.process;

import com.clefal.nirvana_lib.relocated.io.vavr.API;
import com.clefal.nirvana_lib.relocated.io.vavr.collection.Map;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.pattern.IFurnaceStats;
//? fd{
/*import ironfurnaces.tileentity.furnaces.process.compat.Cooking;
import ironfurnaces.tileentity.furnaces.process.compat.MealTransfer;
*///?}
import lombok.AccessLevel;
import lombok.Getter;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;

@Getter(value = AccessLevel.PROTECTED)
public abstract class ProcessingInstance {
    private static final Map<String, MapCodec<? extends ProcessingInstance>> codecMap = API.Map(
            Burn.Smelting.TYPE, Burn.Smelting.CODEC,
            Burn.Blasting.TYPE, Burn.Blasting.CODEC,
            Burn.Smoking.TYPE, Burn.Smoking.CODEC,
            Generate.SmeltGenerate.TYPE, Generate.SmeltGenerate.CODEC,
            Generate.BlastGenerate.TYPE, Generate.BlastGenerate.CODEC,
            Generate.SmokingGenerate.TYPE, Generate.SmokingGenerate.CODEC
            //? fd{
            /*,Cooking.TYPE, Cooking.CODEC,
            MealTransfer.TYPE, MealTransfer.CODEC
            *///?}
    );

    public static final Codec<ProcessingInstance> DISPATCH_CODEC = Codec.STRING.dispatch(
            ProcessingInstance::getType,
            string -> codecMap.get(string).getOrElseThrow(() -> new IllegalArgumentException("can't find a Codec with type: " + string))
                    //? 1.20.1
                    //.codec()
    );

    private boolean handledStart = false;
    public final int fromIndex;

    public ProcessingInstance(int fromIndex) {
        this.fromIndex = fromIndex;
    }

    protected ProcessingInstance(int fromIndex, boolean handledStart) {
        this.fromIndex = fromIndex;
        this.handledStart = handledStart;
    }

    public abstract boolean needLit(FurnacePatternBlockEntity tile);

    public abstract String getType();

    public abstract void whenStart(FurnacePatternBlockEntity tile);
    public abstract float getDoneProgress();

    public void whenDone(FurnacePatternBlockEntity tile) {
        tile.setChanged();
    }
    public void whenChangeStats(IFurnaceStats stats){

    }


    public TickResult tick(FurnacePatternBlockEntity tile) {
        if (!handledStart) {
            whenStart(tile);
            handledStart = true;
        }
        return whenTick(tile);
    }

    public TickResult whenTick(FurnacePatternBlockEntity tile) {
        return TickResult.SUCCESS;
    }

    protected boolean validateSlot(ResourceHandler<ItemResource> handler, FurnacePatternBlockEntity tile){
        return fromIndex >= 0 && fromIndex < handler.size();
    }



    public enum TickResult {
        SUCCESS,
        DONE,
        BLOCKED,
        DISCARD;
    }
}
