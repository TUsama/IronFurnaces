package ironfurnaces.loaders;

import com.clefal.nirvana_lib.utils.NetworkUtils;
import ironfurnaces.network.*;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PacketInit {
    public void initPackets(){
        NetworkUtils.registerPacket(S2CSyncFurnaceSpeedPacket::new);
        NetworkUtils.registerPacket(S2CSyncInstancesToMenuPackets::new);
        NetworkUtils.registerPacket(S2CSyncPatternAndStatsToMenuPackets::new);
        NetworkUtils.registerPacket(S2CSyncFDDataPacket::new);
        NetworkUtils.registerPacket(S2CSyncAugmentPacket::new);




        NetworkUtils.registerPacket(C2SSettingsButtonPacket::new);
        NetworkUtils.registerPacket(C2SUpdateMenuPacket::new);
        NetworkUtils.registerPacket(C2SUpdateFurnaceSettingPacket::new);
        NetworkUtils.registerPacket(C2SLockedRecipePacket::new);
    }

}
