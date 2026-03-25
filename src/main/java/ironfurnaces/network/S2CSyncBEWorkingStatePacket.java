package ironfurnaces.network;

import com.clefal.nirvana_lib.network.newtoolchain.S2CModPacket;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.commons.compress.harmony.pack200.PackingOptions;

public class S2CSyncBEWorkingStatePacket implements S2CModPacket<S2CSyncBEWorkingStatePacket> {
    private boolean working;
    private BlockPos pos;
    public S2CSyncBEWorkingStatePacket() {
    }

    public S2CSyncBEWorkingStatePacket(boolean working, BlockPos pos) {
        this.working = working;
        this.pos = pos;
    }

    @Override
    public void handleClient() {
        BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(pos);
        if (blockEntity instanceof FurnacePatternBlockEntity be){
            be.setWorking(working);
        }
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeBoolean(working);
        friendlyByteBuf.writeBlockPos(pos);
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) {
        this.working = friendlyByteBuf.readBoolean();
        this.pos  = friendlyByteBuf.readBlockPos();
    }

    @Override
    public Class<S2CSyncBEWorkingStatePacket> getSelfClass() {
        return S2CSyncBEWorkingStatePacket.class;
    }
}
