package ironfurnaces.network;

import com.clefal.nirvana_lib.network.newtoolchain.C2SModPacket;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.setting.FurnaceSettingsV2;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;

public class C2SUpdateFurnaceSettingPacket implements C2SModPacket<C2SUpdateFurnaceSettingPacket> {
    private FurnaceSettingsV2 settings;
    private BlockPos pos;

    public C2SUpdateFurnaceSettingPacket() {
    }

    public C2SUpdateFurnaceSettingPacket(FurnaceSettingsV2 settings, BlockPos pos) {
        this.settings = settings;
        this.pos = pos;
    }

    @Override
    public void handleServer(ServerPlayer serverPlayer, C2SUpdateFurnaceSettingPacket c2SUpdateFurnaceSettingPacket, boolean b) {
        BlockEntity blockEntity = serverPlayer.level().getBlockEntity(pos);
        if (blockEntity instanceof FurnacePatternBlockEntity furnacePatternBlockEntity){
            furnacePatternBlockEntity.setWholeSettingV2(settings);
        }
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeJsonWithCodec(FurnaceSettingsV2.CODEC, settings);
        friendlyByteBuf.writeBlockPos(pos);
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) {
        this.settings = friendlyByteBuf.readLenientJsonWithCodec(FurnaceSettingsV2.CODEC);
        this.pos = friendlyByteBuf.readBlockPos();
    }

    @Override
    public Class<C2SUpdateFurnaceSettingPacket> getSelfClass() {
        return C2SUpdateFurnaceSettingPacket.class;
    }
}
