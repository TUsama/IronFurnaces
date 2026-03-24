package ironfurnaces.capability.rainbow;

import ironfurnaces.capability.ModCapabilities;
import lombok.experimental.UtilityClass;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@UtilityClass
public final class OwnerRainbowContextHelper {


    public static void markDirty(@Nullable ServerPlayer player) {
        if (player == null) {
            return;
        }

        player.getCapability(ModCapabilities.PLAYER_RAINBOW_CONTEXT).ifPresent(OwnerRainbowContext::markDirty);
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