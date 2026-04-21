package ironfurnaces.tileentity.furnaces.handler;

import com.mojang.serialization.MapCodec;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.process.Generate;
import ironfurnaces.tileentity.furnaces.process.ProcessingInstanceManager;

import java.util.List;

public class GeneratorLitHandler implements IFurnaceLitHandler{
    public static final GeneratorLitHandler INSTANCE = new GeneratorLitHandler();
    public static final MapCodec<GeneratorLitHandler> CODEC = MapCodec.unit(INSTANCE);
    private int litTime;
    private int litDuration;
    private boolean isLit = false;

    public static final String TYPE = "generator";


    @Override
    public void tick(FurnacePatternBlockEntity tile) {
        List<Generate> allGenerateInstances = tile.getInstanceManager().getAllGenerateInstances();
        if (allGenerateInstances.isEmpty()){
            litDuration = 0;
            litTime = 0;
            isLit = false;
        } else {
            float i = 0;
            for (Generate allGenerateInstance : allGenerateInstances) {
                i += allGenerateInstance.getDoneProgress();
            }
            //pure placeholder
            //don't care the real number actually
            //only for rendering the bar
            litDuration = 200;
            litTime = (int)Math.max(litDuration * 1.0f - (litDuration * (i / allGenerateInstances.size())), 0);
            isLit = true;
        }
        ensureLitState(tile);
    }

    @Override
    public void refresh(FurnacePatternBlockEntity tile) {

    }

    @Override
    public boolean isLit(FurnacePatternBlockEntity tile) {
        ProcessingInstanceManager instanceManager = tile.getInstanceManager();
        return isLit && !instanceManager.isAllBlocking();
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public int getLitDuration() {
        return litDuration;
    }

    @Override
    public int getLitTime() {
        return litTime;
    }

}
