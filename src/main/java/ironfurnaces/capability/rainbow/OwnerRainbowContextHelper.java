package ironfurnaces.capability.rainbow;

import ironfurnaces.capability.ModCapabilities;
import ironfurnaces.capability.PlayerDataHandler;
import ironfurnaces.tileentity.furnaces.pattern.EffectiveFurnaceStats;
import ironfurnaces.tileentity.furnaces.pattern.IFurnaceStats;
import ironfurnaces.tileentity.furnaces.pattern.RainbowFurnacePattern;
import lombok.experimental.UtilityClass;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@UtilityClass
public final class OwnerRainbowContextHelper {


    public static void markDirty(@Nullable ServerPlayer player) {
        if (player == null) {
            return;
        }
        PlayerDataHandler.editRainbowContext(player, OwnerRainbowContext::markDirty);
    }

    @Nullable
    public static IFurnaceStats<?> getFurnaceStats(@Nullable Player player, RainbowFurnacePattern pattern) {
        if (player == null) {
            return null;
        }
        return PlayerDataHandler.readRainbowContext(player, x -> x.getResolvedStats(pattern));
    }

    @Nullable
    public static IFurnaceStats<?> getFurnaceStats(Level level, @Nullable UUID ownerUuid, RainbowFurnacePattern pattern) {
        if (ownerUuid == null) {
            return null;
        }

        Player player = level.getPlayerByUUID(ownerUuid);
        if (player == null) {
            return null;
        }

        return getFurnaceStats(player, pattern);
    }

    public static void markDirtyByOwnerUuid(ServerLevel level, @Nullable UUID ownerUuid) {
        if (ownerUuid == null) {
            return;
        }

        ServerPlayer player = level.getServer().getPlayerList().getPlayer(ownerUuid);
        if (player == null) {
            return;
        }

        markDirty(player);
    }
}