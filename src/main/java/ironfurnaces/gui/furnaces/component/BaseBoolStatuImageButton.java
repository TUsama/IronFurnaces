package ironfurnaces.gui.furnaces.component;

import net.minecraft.resources.Identifier;

import java.util.function.BooleanSupplier;

public class BaseBoolStatuImageButton extends BaseImageButton {
    private BooleanSupplier bool;

    public BaseBoolStatuImageButton(int x, int y, int width, int height, String baseId, OnPress onPress, BooleanSupplier bool) {
        super(x, y, width, height, baseId, onPress);
        this.bool = bool;
    }

    public BaseBoolStatuImageButton(int x, int y, int width, int height, Identifier off, Identifier on, Identifier inactive, OnPress onPress, BooleanSupplier bool) {
        super(x, y, width, height, off, on, inactive, onPress);
        this.bool = bool;
    }

    @Override
    protected boolean shouldHighlight() {
        return super.shouldHighlight() || bool.getAsBoolean();
    }
}
