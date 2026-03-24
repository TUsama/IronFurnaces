package ironfurnaces.tileentity.furnaces;

import ironfurnaces.capability.ModCapabilities;
import ironfurnaces.tileentity.furnaces.pattern.FurnacePattern;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicInteger;

public final class RainbowLimitHelper {

    private RainbowLimitHelper() {
    }

    public static int countOwnedRainbowFurnaces(@Nullable Player player) {
        if (player == null || player.level().isClientSide) {
            return 0;
        }

        AtomicInteger count = new AtomicInteger(0);

        player.getCapability(ModCapabilities.FURNACES_LIST).ifPresent(list -> {
            for (GlobalPos globalPos : list.get()) {
                if (globalPos == null) continue;
                if (player.level().getServer() == null) continue;

                ServerLevel targetLevel = player.level().getServer().getLevel(globalPos.dimension());
                if (targetLevel == null) continue;
                if (!targetLevel.isLoaded(globalPos.pos())) continue;

                if (!(targetLevel.getBlockEntity(globalPos.pos()) instanceof FurnacePatternBlockEntity be)) continue;

                FurnacePattern pattern = be.getPattern();
                if (pattern != null && pattern.isRainbow()) {
                    count.incrementAndGet();
                }
            }
        });

        return count.get();
    }


    public static boolean canApplyRainbowPattern(
            @Nullable Player player,
            Level level,
            BlockPos pos,
            @Nullable FurnacePatternBlockEntity currentBe,
            int maxAllowed
    ) {
        if (player == null) return false;
        if (level.isClientSide) return true;
        if (maxAllowed < 0) return true;

        int owned = countOwnedRainbowFurnaces(player);

        // 如果当前这个 BE 本身已经是彩虹熔炉，那么这次只是“重复应用/刷新”，不应该再占一个名额
        if (currentBe != null) {
            FurnacePattern currentPattern = currentBe.getPattern();
            if (currentPattern != null && currentPattern.isRainbow()) {
                return owned <= maxAllowed;
            }
        }

        return owned < maxAllowed;
    }
}
