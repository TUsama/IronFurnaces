package ironfurnaces.tileentity.furnaces.pattern.render;

import net.minecraft.resources.Identifier;

public record CubeTextures(
        Identifier front,
        Identifier back,
        Identifier left,
        Identifier right,
        Identifier top,
        Identifier bottom
) {
    public static CubeTextures frontSideTopBottom(
            Identifier front,
            Identifier side,
            Identifier top,
            Identifier bottom
    ) {
        return new CubeTextures(front, side, side, side, top, bottom);
    }
}
