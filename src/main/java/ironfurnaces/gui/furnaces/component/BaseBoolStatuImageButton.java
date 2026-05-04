package ironfurnaces.gui.furnaces.component;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

//? 1.20.1 {

//? } else {
import net.minecraft.client.gui.components.WidgetSprites;
//?}

import java.util.function.BooleanSupplier;

public class BaseBoolStatuImageButton extends BaseImageButton {
    private BooleanSupplier bool;

    public BaseBoolStatuImageButton(int x, int y, int width, int height, String baseId, OnPress onPress, BooleanSupplier bool) {
        super(x, y, width, height, baseId, onPress);
        this.bool = bool;
    }

    public BaseBoolStatuImageButton(int x, int y, int width, int height, ResourceLocation off, ResourceLocation on, ResourceLocation inactive, OnPress onPress, BooleanSupplier bool) {
        super(x, y, width, height, off, on, inactive, onPress);
        this.bool = bool;
    }

    @Override
    protected boolean shouldHighlight() {
        return super.shouldHighlight() || bool.getAsBoolean();
    }
}
