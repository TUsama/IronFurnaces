package ironfurnaces.network;

import com.clefal.nirvana_lib.network.newtoolchain.S2CModPacket;
import ironfurnaces.client.data.FurnaceWorkSpeedDataStorage;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Map;

public class S2CSyncFurnaceSpeedPacket implements S2CModPacket<S2CSyncFurnaceSpeedPacket> {

    public S2CSyncFurnaceSpeedPacket() {
    }

    private Map<String, Integer> speedMap;

    public S2CSyncFurnaceSpeedPacket(Map<String, Integer> speedMap) {
        this.speedMap = speedMap;
    }

    @Override
    public void handleClient() {

    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeMap(speedMap, FriendlyByteBuf::writeUtf, FriendlyByteBuf::writeInt);
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) {
        this.speedMap = friendlyByteBuf.readMap(FriendlyByteBuf::readUtf, FriendlyByteBuf::readInt);
    }

    @Override
    public Class<S2CSyncFurnaceSpeedPacket> getSelfClass() {
        return S2CSyncFurnaceSpeedPacket.class;
    }
}
