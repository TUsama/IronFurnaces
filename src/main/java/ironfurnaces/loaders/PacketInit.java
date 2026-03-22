package ironfurnaces.loaders;

import com.clefal.nirvana_lib.utils.NetworkUtils;
import ironfurnaces.network.*;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PacketInit {
    public void initPackets(){
        NetworkUtils.registerPacket(C2SSettingsButtonPacket::new);
        NetworkUtils.registerPacket(S2CSyncFurnaceSpeedPacket::new);
        NetworkUtils.registerPacket(S2CSyncInstancesToMenuPackets::new);
        NetworkUtils.registerPacket(S2CSyncPatternToMenuPackets::new);
        NetworkUtils.registerPacket(C2SUpdateMenuPacket::new);
        NetworkUtils.registerPacket(C2SUpdateFurnaceSettingPacket::new);
    }

}
