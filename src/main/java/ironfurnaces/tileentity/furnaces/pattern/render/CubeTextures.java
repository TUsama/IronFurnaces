package ironfurnaces.tileentity.furnaces.pattern.render;

import net.minecraft.resources.ResourceLocation;

public record CubeTextures(
        ResourceLocation front,
        ResourceLocation back,
        ResourceLocation left,
        ResourceLocation right,
        ResourceLocation top,
        ResourceLocation bottom
) {
    public static CubeTextures frontSideTopBottom(
            ResourceLocation front,
            ResourceLocation side,
            ResourceLocation top,
            ResourceLocation bottom
    ) {
        return new CubeTextures(front, side, side, side, top, bottom);
    }
}
