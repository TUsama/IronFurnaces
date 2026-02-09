package ironfurnaces.loaders;

import com.clefal.nirvana_lib.utils.NetworkUtils;
import com.clefal.nirvana_lib.utils.SideUtils;
import ironfurnaces.network.C2SSettingsButtonPacket;
import ironfurnaces.network.C2SShowConfigButtonPacket;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PacketInit {
    public void initPackets(){
        initServerPacket();
        if (SideUtils.isClient()){

        }
    }

    private void initServerPacket(){
        NetworkUtils.registerPacket(C2SSettingsButtonPacket::new);
        NetworkUtils.registerPacket(C2SShowConfigButtonPacket::new);
    }
}
