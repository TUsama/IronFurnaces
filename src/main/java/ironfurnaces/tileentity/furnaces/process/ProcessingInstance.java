package ironfurnaces.tileentity.furnaces.process;

import com.clefal.nirvana_lib.relocated.io.vavr.API;
import com.clefal.nirvana_lib.relocated.io.vavr.collection.Map;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import ironfurnaces.tileentity.furnaces.BlockIronFurnaceTileBaseV2;

public abstract class ProcessingInstance {
    private static final Map<String, MapCodec<? extends ProcessingInstance>> codecMap = API.Map(
            Burn.Smelting.TYPE, Burn.Smelting.CODEC,
            Burn.Blasting.TYPE, Burn.Blasting.CODEC,
            Burn.Smoking.TYPE, Burn.Smoking.CODEC,
            Generate.SmeltGenerate.TYPE, Generate.SmeltGenerate.CODEC,
            Generate.BlastGenerate.TYPE, Generate.BlastGenerate.CODEC
    );

    public static final Codec<ProcessingInstance> DISPATCH_CODEC = Codec.STRING.dispatch(
            ProcessingInstance::getType,
            string -> codecMap.get(string).getOrElseThrow(() -> new IllegalArgumentException("can't find a Codec with type: " + string))
                    //? 1.20.1
                    .codec()
    );

    private boolean handledStart = false;

    public ProcessingInstance() {
    }

    public abstract String getType();

    public abstract void whenStart(BlockIronFurnaceTileBaseV2 tile);

    public void whenDone(BlockIronFurnaceTileBaseV2 tile) {
        tile.setChanged();
    }


    public TickResult tick(BlockIronFurnaceTileBaseV2 tile) {
        if (!handledStart) {
            whenStart(tile);
            handledStart = true;
        }
        return whenTick(tile);
    }

    public TickResult whenTick(BlockIronFurnaceTileBaseV2 tile) {
        return TickResult.SUCCESS;
    }


    public enum TickResult {
        SUCCESS,
        DONE,
        BLOCKED,
        DISCARD;
    }
}
