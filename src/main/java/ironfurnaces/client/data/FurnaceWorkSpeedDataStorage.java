package ironfurnaces.client.data;

import ironfurnaces.blocks.furnaces.BlockIronFurnaceBase;
import net.minecraft.network.FriendlyByteBuf;

import java.util.HashMap;
import java.util.Map;

public class FurnaceWorkSpeedDataStorage {
    private static final FurnaceWorkSpeedDataStorage INSTANCE = new FurnaceWorkSpeedDataStorage();
    private final Map<String, Integer> speedMap = new HashMap<>();
    public static FurnaceWorkSpeedDataStorage getInstance() {
        return INSTANCE;
    }

    public void updateFromMap(Map<String, Integer> map){
        speedMap.clear();
        speedMap.putAll(map);
    }

    public int getSpeed(BlockIronFurnaceBase furnaceBase){
        return speedMap.getOrDefault(furnaceBase.getId(), 0);
    }

}
