package ironfurnaces.gui.furnaces.component;

import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.tileentity.furnaces.setting.FurnaceSettingsV2;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

public class IOButton extends ReversibleImageButton {
    private final FurnaceSettingsV2.RelativeFace relativeFace;
    private final Map<FurnaceSettingsV2.IOMode, WidgetSprites> spritesMap = new HashMap<>();
    protected Supplier<FurnaceSettingsV2.IOMode> currentState;

    public IOButton(int x, int y, int width, int height, OnPress onPress, FurnaceSettingsV2.RelativeFace relativeFace, OnPress rightClick, Supplier<FurnaceSettingsV2.IOMode> currentState) {
        super(x, y, width, height, "iomode_none", onPress, rightClick);
        this.relativeFace = relativeFace;
        this.currentState = currentState;
    }




    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        graphics.setTooltipForNextFrame(Component.translatable("ironfurnaces.furnace_setting.relative_face." + relativeFace.getSerializedName(), Component.translatable(currentState.get().translationKey)), mouseX, mouseY);
        super.extractContents(graphics, mouseX, mouseY, a);
        Identifier resourcelocation = spritesMap.computeIfAbsent(currentState.get(), x -> {
            var baseId = "iomode_" + x.toString().toLowerCase(Locale.ROOT);
            return new WidgetSprites(IronFurnaces.sprite(baseId + "_off"), IronFurnaces.sprite(baseId + "_inactive"), IronFurnaces.sprite(baseId + "_on"), IronFurnaces.sprite(baseId + "_inactive"));
        }).get(this.isActive(), this.shouldHighlight());
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, resourcelocation, this.getX(), this.getY(), this.width, this.height);
    }


}
