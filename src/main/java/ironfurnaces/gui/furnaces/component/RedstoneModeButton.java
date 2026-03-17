package ironfurnaces.gui.furnaces.component;

import ironfurnaces.tileentity.furnaces.setting.FurnaceSettingsV2;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class RedstoneModeButton extends ImageButton {
    private Supplier<FurnaceSettingsV2> settingsV2;
    private WidgetGroup substractionGroup;

    public RedstoneModeButton(int x, int y, int width, int height, int xTexStart, int yTexStart, ResourceLocation resourceLocation, OnPress onPress, Supplier<FurnaceSettingsV2> settingsV2, WidgetGroup substractionGroup) {
        super(x, y, width, height, xTexStart, yTexStart, resourceLocation, onPress);
        this.settingsV2 = settingsV2;
        this.substractionGroup = substractionGroup;
    }

    public RedstoneModeButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffTex, ResourceLocation resourceLocation, OnPress onPress, Supplier<FurnaceSettingsV2> settingsV2, WidgetGroup substractionGroup) {
        super(x, y, width, height, xTexStart, yTexStart, yDiffTex, resourceLocation, onPress);
        this.settingsV2 = settingsV2;
        this.substractionGroup = substractionGroup;
    }

    public RedstoneModeButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffTex, ResourceLocation resourceLocation, int textureWidth, int textureHeight, OnPress onPress, Supplier<FurnaceSettingsV2> settingsV2, WidgetGroup substractionGroup) {
        super(x, y, width, height, xTexStart, yTexStart, yDiffTex, resourceLocation, textureWidth, textureHeight, onPress);
        this.settingsV2 = settingsV2;
        this.substractionGroup = substractionGroup;
    }

    public RedstoneModeButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffTex, ResourceLocation resourceLocation, int textureWidth, int textureHeight, OnPress onPress, Component message, Supplier<FurnaceSettingsV2> settingsV2, WidgetGroup substractionGroup) {
        super(x, y, width, height, xTexStart, yTexStart, yDiffTex, resourceLocation, textureWidth, textureHeight, onPress, message);
        this.settingsV2 = settingsV2;
        this.substractionGroup = substractionGroup;
    }


    @Override
    public void onPress() {
        super.onPress();
        if (settingsV2.get().redStoneMode().next().equals(FurnaceSettingsV2.RedStoneMode.COMPARATOR_SUBTRACTION)){
            substractionGroup.activeAll();
        } else {
            substractionGroup.deactivateAll();
        }
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderTexture(guiGraphics, this.resourceLocation, this.getX(), this.getY(), this.xTexStart + settingsV2.get().redStoneMode().ordinal() * 14, this.yTexStart, this.yDiffTex, this.width, this.height, this.textureWidth, this.textureHeight);
    }
}
