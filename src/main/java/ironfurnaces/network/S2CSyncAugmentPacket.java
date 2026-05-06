package ironfurnaces.network;

import com.clefal.nirvana_lib.network.newtoolchain.S2CModPacket;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class S2CSyncAugmentPacket implements S2CModPacket<S2CSyncAugmentPacket> {
    private CompoundTag tag;
    private BlockPos pos;

    public S2CSyncAugmentPacket() {
    }

    public S2CSyncAugmentPacket(CompoundTag tag, BlockPos pos) {
        this.tag = tag;
        this.pos = pos;
    }

    @Override
    public void handleClient() {
        if (Minecraft.getInstance().level.getBlockEntity(pos) instanceof FurnacePatternBlockEntity patternBlockEntity) {
            /*patternBlockEntity.getAugments().deserializeNBT(tag);
            patternBlockEntity.getAugments().refreshState();
            System.out.println(patternBlockEntity.getAugments().getCurrentRecipeType().getRecipeType().toString());*/
        }
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf
                .writeNbt(tag)
                .writeBlockPos(pos);
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) {
        this.tag = friendlyByteBuf.readNbt();
        this.pos = friendlyByteBuf.readBlockPos();
    }

    @Override
    public Class<S2CSyncAugmentPacket> getSelfClass() {
        return S2CSyncAugmentPacket.class;
    }
}
