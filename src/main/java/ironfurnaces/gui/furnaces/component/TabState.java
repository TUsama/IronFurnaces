package ironfurnaces.gui.furnaces.component;

import java.util.function.Consumer;

public enum TabState {
    OPEN(WidgetGroup::activeAll),
    CLOSE(WidgetGroup::deactivateAll);

    TabState(Consumer<WidgetGroup> groupHandler) {
        this.groupHandler = groupHandler;
    }

    public final Consumer<WidgetGroup> groupHandler;

    public TabState next() {
        return values()[(this.ordinal() + 1) % values().length];
    }

    public TabState previous() {
        return values()[Math.floorMod(this.ordinal() - 1, values().length)];
    }
}
