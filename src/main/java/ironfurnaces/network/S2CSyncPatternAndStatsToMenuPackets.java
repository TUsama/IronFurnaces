package ironfurnaces.network;

import com.clefal.nirvana_lib.network.newtoolchain.S2CModPacket;
import ironfurnaces.gui.furnaces.FurnacePatternScreen;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.cache.AugmentCache;
import ironfurnaces.tileentity.furnaces.menu.FurnacePatternMenu;
import ironfurnaces.tileentity.furnaces.pattern.EffectiveFurnaceStats;
import ironfurnaces.tileentity.furnaces.pattern.FurnacePattern;
import ironfurnaces.tileentity.furnaces.pattern.IFurnaceStats;
import ironfurnaces.tileentity.furnaces.pattern.mode.AbstractFurnaceModeHandler;
import ironfurnaces.tileentity.furnaces.pattern.mode.FurnaceModeManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;

public class S2CSyncPatternAndStatsToMenuPackets implements S2CModPacket<S2CSyncPatternAndStatsToMenuPackets> {
    private FurnacePattern pattern;
    private IFurnaceStats stats;

    public S2CSyncPatternAndStatsToMenuPackets(FurnacePattern pattern, String modeId, IFurnaceStats stats) {
        this.pattern = pattern;
        this.stats = stats;
    }

    public S2CSyncPatternAndStatsToMenuPackets() {
    }

    @Override
    public void handleClient() {
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.containerMenu instanceof FurnacePatternMenu furnacePatternMenu) {
            FurnacePatternBlockEntity blockEntity = furnacePatternMenu.blockEntity;
            FurnacePattern furnacePattern = blockEntity.getPattern();
            AugmentCache augments = blockEntity.getAugments();

            if (furnacePattern != pattern){
                blockEntity.updatePattern(pattern);
            }

            if (!blockEntity.usedStats.equals(stats)){
                blockEntity.updateFurnaceStats(stats);
            }

            augments.refreshState();
            if (Minecraft.getInstance().screen instanceof FurnacePatternScreen screen) {
                screen.updateRenderHandler(blockEntity.getMode().getId());
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
        this.pattern = friendlyByteBuf.readLenientJsonWithCodec(FurnacePattern.REF_CODEC);
        this.stats = friendlyByteBuf.readLenientJsonWithCodec(IFurnaceStats.CODEC);
    }

    @Override
    public Class<S2CSyncPatternAndStatsToMenuPackets> getSelfClass() {
        return S2CSyncPatternAndStatsToMenuPackets.class;
    }
}
