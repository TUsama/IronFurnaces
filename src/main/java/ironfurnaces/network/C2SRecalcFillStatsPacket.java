package ironfurnaces.network;

import com.clefal.nirvana_lib.network.newtoolchain.C2SModPacket;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class C2SRecalcFillStatsPacket implements C2SModPacket<C2SRecalcFillStatsPacket> {
    private BlockPos pos;

    public C2SRecalcFillStatsPacket() {
    }

    public C2SRecalcFillStatsPacket(BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public void handleServer(ServerPlayer serverPlayer, C2SRecalcFillStatsPacket c2SRecalcFillStatsPacket, boolean b) {
        if (serverPlayer.level().getBlockEntity(pos) instanceof FurnacePatternBlockEntity blockEntity) {
            blockEntity.recomputeFillStat();
        }
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeBlockPos(pos);
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) {
        this.pos = friendlyByteBuf.readBlockPos();
    }

    @Override
    public Class<C2SRecalcFillStatsPacket> getSelfClass() {
        return C2SRecalcFillStatsPacket.class;
    }
}
