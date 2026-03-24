package ironfurnaces.network;

import com.clefal.nirvana_lib.network.newtoolchain.S2CModPacket;
import ironfurnaces.tileentity.furnaces.menu.FurnacePatternMenu;
import ironfurnaces.tileentity.furnaces.pattern.EffectiveFurnaceStats;
import ironfurnaces.tileentity.furnaces.pattern.FurnacePattern;
import ironfurnaces.tileentity.furnaces.pattern.IFurnaceStats;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;

public class S2CSyncPatternAndStatsToMenuPackets implements S2CModPacket<S2CSyncPatternAndStatsToMenuPackets> {
    private FurnacePattern pattern;
    private IFurnaceStats stats;

    public S2CSyncPatternAndStatsToMenuPackets(FurnacePattern pattern, IFurnaceStats stats) {
        this.pattern = pattern;
        this.stats = stats;
    }

    public S2CSyncPatternAndStatsToMenuPackets() {
    }

    @Override
    public void handleClient() {
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.containerMenu instanceof FurnacePatternMenu furnacePatternMenu) {
            FurnacePattern furnacePattern = furnacePatternMenu.blockEntity.getPattern();
            if (furnacePattern != pattern){
                furnacePatternMenu.blockEntity.updatePattern(pattern);
            }

            if (!furnacePatternMenu.blockEntity.usedStats.equals(stats)){
                furnacePatternMenu.blockEntity.updateFurnaceStats(stats, false);
            }


        }
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeJsonWithCodec(FurnacePattern.REF_CODEC, pattern);
        friendlyByteBuf.writeJsonWithCodec(IFurnaceStats.CODEC, stats);
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) {
        this.pattern = friendlyByteBuf.readJsonWithCodec(FurnacePattern.REF_CODEC);
        this.stats = friendlyByteBuf.readJsonWithCodec(IFurnaceStats.CODEC);
    }

    @Override
    public Class<S2CSyncPatternAndStatsToMenuPackets> getSelfClass() {
        return S2CSyncPatternAndStatsToMenuPackets.class;
    }
}
