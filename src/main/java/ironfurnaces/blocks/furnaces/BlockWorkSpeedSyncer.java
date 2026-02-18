package ironfurnaces.blocks.furnaces;

import com.clefal.nirvana_lib.utils.NetworkUtils;
import ironfurnaces.Config;
import ironfurnaces.blocks.furnaces.other.BlockAllthemodiumFurnace;
import ironfurnaces.blocks.furnaces.other.BlockUnobtainiumFurnace;
import ironfurnaces.blocks.furnaces.other.BlockVibraniumFurnace;
import ironfurnaces.client.data.FurnaceWorkSpeedDataStorage;
import ironfurnaces.network.S2CSyncFurnaceSpeedPacket;
import lombok.experimental.UtilityClass;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;

@UtilityClass
public class BlockWorkSpeedSyncer {
    
    public void syncWhenPlayerJoin(ServerPlayer player) {
        System.out.println("sync!!!");
        HashMap<String, Integer> stringIntegerHashMap = new HashMap<>();
        stringIntegerHashMap.put(BlockCopperFurnace.ID, Config.copperFurnaceSpeed.get());
        stringIntegerHashMap.put(BlockCrystalFurnace.ID, Config.crystalFurnaceSpeed.get());
        stringIntegerHashMap.put(BlockDiamondFurnace.ID, Config.diamondFurnaceSpeed.get());
        stringIntegerHashMap.put(BlockEmeraldFurnace.ID, Config.emeraldFurnaceSpeed.get());
        stringIntegerHashMap.put(BlockGoldFurnace.ID, Config.goldFurnaceSpeed.get());
        stringIntegerHashMap.put(BlockIronFurnace.ID, Config.ironFurnaceSpeed.get());
        stringIntegerHashMap.put(BlockMillionFurnace.ID, Config.millionFurnaceSpeed.get());
        stringIntegerHashMap.put(BlockNetheriteFurnace.ID, Config.netheriteFurnaceSpeed.get());
        stringIntegerHashMap.put(BlockObsidianFurnace.ID, Config.obsidianFurnaceSpeed.get());
        stringIntegerHashMap.put(BlockSilverFurnace.ID, Config.silverFurnaceSpeed.get());
        stringIntegerHashMap.put(BlockAllthemodiumFurnace.ID, Config.allthemodiumFurnaceSpeed.get());
        stringIntegerHashMap.put(BlockVibraniumFurnace.ID, Config.vibraniumFurnaceSpeed.get());
        stringIntegerHashMap.put(BlockUnobtainiumFurnace.ID, Config.unobtainiumFurnaceSpeed.get());
        if (player.connection.connection.isConnecting()) {
            NetworkUtils.sendToClient(new S2CSyncFurnaceSpeedPacket(stringIntegerHashMap), player);
        } else {
            FurnaceWorkSpeedDataStorage.getInstance().updateFromMap(stringIntegerHashMap);
        }



    }


}
