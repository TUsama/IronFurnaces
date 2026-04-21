package ironfurnaces.tileentity.furnaces.menu.handler;

import ironfurnaces.tileentity.furnaces.menu.FurnacePatternMenu;
import lombok.AccessLevel;
import lombok.Getter;

public abstract class AbstractCompatMenuHandler implements IMenuHandler {
    @Getter(AccessLevel.PROTECTED)
    private FurnacePatternMenu menu;

    public AbstractCompatMenuHandler(FurnacePatternMenu menu) {
        this.menu = menu;
    }

}
