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

public class S2CSyncInstancesToMenuPackets implements S2CModPacket<S2CSyncInstancesToMenuPackets> {
    private List<Tuple2<Integer, Float>> instances;

    public S2CSyncInstancesToMenuPackets(List<ProcessingInstance> instances) {
        this.instances = new ArrayList<>();
        for (ProcessingInstance instance : instances) {
            this.instances.add(Tuple.of(instance.fromIndex, instance.getDoneProgress()));
        }

    }

    public S2CSyncInstancesToMenuPackets() {
    }

    @Override
    public void handleClient() {
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.containerMenu instanceof FurnacePatternMenu furnacePatternMenu) {
            Int2FloatLinkedOpenHashMap int2FloatLinkedOpenHashMap = new Int2FloatLinkedOpenHashMap();
            for (Tuple2<Integer, Float> instance : this.instances) {
                int2FloatLinkedOpenHashMap.put(instance._1.intValue(), instance._2.floatValue());
            }
            furnacePatternMenu.instances = int2FloatLinkedOpenHashMap;

        }
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeCollection(instances, (buf, instance) -> {
            buf.writeInt(instance._1());
            buf.writeFloat(instance._2());
        });

    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) {
        ArrayList<Tuple2<Integer, Float>> tuple2s = friendlyByteBuf.readCollection(ArrayList::new, buf -> {
            return Tuple.of(buf.readInt(), buf.readFloat());
        });
        this.instances = tuple2s;
    }

    @Override
    public Class<S2CSyncInstancesToMenuPackets> getSelfClass() {
        return S2CSyncInstancesToMenuPackets.class;
    }
}
