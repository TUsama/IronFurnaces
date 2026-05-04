//? >1.21.11{
/*package ironfurnaces.tileentity.furnaces.pattern.render.refactor;

import ironfurnaces.items.JovialState;
import ironfurnaces.tileentity.furnaces.cache.IRecipeTypeHandler;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
// 如果你当前分支仍然是 ResourceLocation，就把 ResourceLocation 替换为 ResourceLocation。

public record FurnacePatternRenderData(
        ResourceLocation patternId,
        boolean lit,
        Direction facing,
        JovialState jovial,
        IRecipeTypeHandler recipeType
) {
    public String patternPath() {
        return patternId.getPath();
    }
}

*///?}