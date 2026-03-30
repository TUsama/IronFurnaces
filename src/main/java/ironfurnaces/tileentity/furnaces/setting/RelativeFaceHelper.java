package ironfurnaces.tileentity.furnaces.setting;

import lombok.experimental.UtilityClass;
import net.minecraft.core.Direction;

@UtilityClass
public final class RelativeFaceHelper {


    public static FurnaceSettingsV2.RelativeFace toRelative(Direction blockFacing, Direction worldSide) {
        if (worldSide == Direction.UP) return FurnaceSettingsV2.RelativeFace.UP;
        if (worldSide == Direction.DOWN) return FurnaceSettingsV2.RelativeFace.DOWN;

        if (worldSide == blockFacing) return FurnaceSettingsV2.RelativeFace.FRONT;
        if (worldSide == blockFacing.getOpposite()) return FurnaceSettingsV2.RelativeFace.BACK;
        if (worldSide == blockFacing.getClockWise()) return FurnaceSettingsV2.RelativeFace.RIGHT;
        if (worldSide == blockFacing.getCounterClockWise()) return FurnaceSettingsV2.RelativeFace.LEFT;

        throw new IllegalStateException("Unexpected side: " + worldSide + ", facing: " + blockFacing);
    }

    public static Direction toWorld(Direction blockFacing, FurnaceSettingsV2.RelativeFace relativeFace) {
        return switch (relativeFace) {
            case FRONT -> blockFacing;
            case BACK -> blockFacing.getOpposite();
            case LEFT -> blockFacing.getClockWise();
            case RIGHT -> blockFacing.getCounterClockWise();
            case UP -> Direction.UP;
            case DOWN -> Direction.DOWN;
        };
    }
}