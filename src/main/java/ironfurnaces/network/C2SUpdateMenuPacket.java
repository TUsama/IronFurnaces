package ironfurnaces.network;

import com.clefal.nirvana_lib.network.newtoolchain.C2SModPacket;
import ironfurnaces.tileentity.furnaces.menu.FurnacePatternMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class C2SUpdateMenuPacket implements C2SModPacket<C2SUpdateMenuPacket> {
    public int index;

    public C2SUpdateMenuPacket() {
    }

    public C2SUpdateMenuPacket(int index) {
        this.index = index;
    }

    @Override
    public void handleServer(ServerPlayer serverPlayer, C2SUpdateMenuPacket c2SUpdateMenuPacket, boolean b) {
        if (serverPlayer.containerMenu instanceof FurnacePatternMenu furnacePatternMenu){
            switch (index){
                case 0 -> furnacePatternMenu.openSetting = !furnacePatternMenu.openSetting;
                case 1 -> furnacePatternMenu.openAugment = !furnacePatternMenu.openAugment;
                case 2 -> furnacePatternMenu.openRemaining = !furnacePatternMenu.openRemaining;
                case 3 -> {
                    furnacePatternMenu.getFactoryInput().nextPage();
                    furnacePatternMenu.getFactoryOutput().nextPage();
                }
                case 4 -> {
                    furnacePatternMenu.getFactoryInput().previousPage();
                    furnacePatternMenu.getFactoryOutput().previousPage();
                }
            }
        }
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeInt(index);
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) {
        this.index = friendlyByteBuf.readInt();
    }

    @Override
    public Class<C2SUpdateMenuPacket> getSelfClass() {
        return C2SUpdateMenuPacket.class;
    }
}
