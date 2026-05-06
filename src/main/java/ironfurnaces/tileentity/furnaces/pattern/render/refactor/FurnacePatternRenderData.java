package ironfurnaces.tileentity.furnaces.pattern.render.refactor;

import ironfurnaces.items.JovialState;
import ironfurnaces.tileentity.furnaces.cache.IRecipeTypeHandler;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;

public record FurnacePatternRenderData(
        Identifier patternId,
        boolean lit,
        Direction facing,
        JovialState jovial,
        IRecipeTypeHandler recipeType
) {
    public String patternPath() {
        return patternId.getPath();
    }
}