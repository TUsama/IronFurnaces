package ironfurnaces.gui.furnaces.component;

import net.minecraft.client.gui.components.AbstractWidget;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class WidgetGroup {
    public List<? extends AbstractWidget> widgets;

    public WidgetGroup(AbstractWidget... widgets) {
        this.widgets = Arrays.stream(widgets).toList();

    }

    public void deactivateAll() {
        for (AbstractWidget widget : widgets) {
            widget.visible = false;
            widget.active = false;
        }
    }

    public void activeAll() {
        for (AbstractWidget widget : widgets) {
            widget.visible = true;
            widget.active = true;
        }
    }

    public void hideAll() {
        for (AbstractWidget widget : widgets) {
            widget.visible = false;
        }
    }

    public void showAll() {
        for (AbstractWidget widget : widgets) {
            widget.visible = true;
        }
    }
}
