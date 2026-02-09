package ironfurnaces.network;

import com.clefal.nirvana_lib.network.newtoolchain.C2SModPacket;
import ironfurnaces.capability.CapabilityPlayerShowConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class C2SShowConfigButtonPacket implements C2SModPacket<C2SShowConfigButtonPacket> {

	private int set;

	public C2SShowConfigButtonPacket() {
	}


	public C2SShowConfigButtonPacket(int set) {
		this.set = set;
	}


    @Override
    public void handleServer(ServerPlayer serverPlayer, C2SShowConfigButtonPacket c2SShowConfigButtonPacket, boolean b) {
        serverPlayer.getCapability(CapabilityPlayerShowConfig.CONFIG).ifPresent(h -> h.set(set));
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeInt(set);
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) {
        set = friendlyByteBuf.readInt();
    }

    @Override
    public Class<C2SShowConfigButtonPacket> getSelfClass() {
        return C2SShowConfigButtonPacket.class;
    }
}
