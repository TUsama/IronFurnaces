package ironfurnaces.tileentity.furnaces.pattern.mode.compat;

import ironfurnaces.tileentity.furnaces.cache.IRecipeTypeHandler;
import ironfurnaces.tileentity.furnaces.menu.FurnacePatternMenu;
import ironfurnaces.tileentity.furnaces.menu.handler.AbstractCompatMenuHandler;
import ironfurnaces.tileentity.furnaces.menu.handler.IMenuHandler;
import ironfurnaces.tileentity.furnaces.pattern.mode.AbstractFurnaceModeHandler;

import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class CompatModeHandler extends AbstractFurnaceModeHandler {
    @Override
    public final boolean isInternal() {
        return false;
    }

    @Override
    public final boolean isFurnace() {
        return false;
    }

    @Override
    public final boolean isFactory() {
        return false;
    }

    @Override
    public final boolean isGenerator() {
        return false;
    }

    @Nullable
    public abstract IRecipeTypeHandler getAttachRecipeTypeHandler();

    @Nullable
    public abstract Function<FurnacePatternMenu, AbstractCompatMenuHandler> getMenuHandler();

}
