package ironfurnaces.network;

import com.clefal.nirvana_lib.network.newtoolchain.C2SModPacket;
import ironfurnaces.tileentity.furnaces.BlockIronFurnaceTileBase;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class C2SSettingsButtonPacket implements C2SModPacket<C2SSettingsButtonPacket> {

	private int x;
	private int y;
	private int z;
	private int index;
	private int set;

    public C2SSettingsButtonPacket() {
    }


	public C2SSettingsButtonPacket(BlockPos pos, int index, int set) {
		this.x = pos.getX();
		this.y = pos.getY();
		this.z = pos.getZ();
		this.index = index;
		this.set = set;
	}


    @Override
    public void handleServer(ServerPlayer serverPlayer, C2SSettingsButtonPacket c2SSettingsButtonPacket, boolean b) {
        BlockPos pos = new BlockPos(x, y, z);
        BlockIronFurnaceTileBase te = (BlockIronFurnaceTileBase) serverPlayer.level().getBlockEntity(pos);
        if (serverPlayer.level().isLoaded(pos)) {
            te.furnaceSettings.set(index, set);
            te.getLevel().markAndNotifyBlock(pos, serverPlayer.level().getChunkAt(pos), te.getLevel().getBlockState(pos).getBlock().defaultBlockState(), te.getLevel().getBlockState(pos), 2, 0);
            te.setChanged();
        }
    }


    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeInt(x);
        friendlyByteBuf.writeInt(y);
        friendlyByteBuf.writeInt(z);
        friendlyByteBuf.writeInt(index);
        friendlyByteBuf.writeInt(set);
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) {
        x = friendlyByteBuf.readInt();
        y = friendlyByteBuf.readInt();
        z = friendlyByteBuf.readInt();
        index = friendlyByteBuf.readInt();
        set = friendlyByteBuf.readInt();
    }

    @Override
    public Class<C2SSettingsButtonPacket> getSelfClass() {
        return C2SSettingsButtonPacket.class;
    }
}
