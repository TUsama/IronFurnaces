package ironfurnaces.network;

import com.clefal.nirvana_lib.network.newtoolchain.S2CModPacket;
import com.clefal.nirvana_lib.relocated.io.vavr.Tuple;
import com.clefal.nirvana_lib.relocated.io.vavr.Tuple2;
import ironfurnaces.tileentity.furnaces.menu.FurnacePatternMenu;
import ironfurnaces.tileentity.furnaces.pattern.FurnacePattern;
import ironfurnaces.tileentity.furnaces.process.ProcessingInstance;
import it.unimi.dsi.fastutil.ints.Int2FloatLinkedOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.List;

public class S2CSyncPatternToMenuPackets implements S2CModPacket<S2CSyncPatternToMenuPackets> {
    private FurnacePattern pattern;

    public S2CSyncPatternToMenuPackets(FurnacePattern pattern) {
        this.pattern = pattern;
    }

    public S2CSyncPatternToMenuPackets() {
    }

    @Override
    public void handleClient() {
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.containerMenu instanceof FurnacePatternMenu furnacePatternMenu) {
            FurnacePattern furnacePattern = furnacePatternMenu.blockEntity.getPattern();
            if (furnacePattern != pattern){
                furnacePatternMenu.blockEntity.updatePattern(pattern);
            }


        }
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeJsonWithCodec(FurnacePattern.DIRECT_CODEC, pattern);
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) {
        this.pattern = friendlyByteBuf.readJsonWithCodec(FurnacePattern.DIRECT_CODEC);
    }

    @Override
    public Class<S2CSyncPatternToMenuPackets> getSelfClass() {
        return S2CSyncPatternToMenuPackets.class;
    }
}
