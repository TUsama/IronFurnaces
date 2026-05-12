package ironfurnaces.gui.furnaces.component;

import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.tileentity.furnaces.setting.FurnaceSettingsV2;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

public class RedstoneModeButton extends ReversibleImageButton {
    private final Map<FurnaceSettingsV2.RedStoneMode, WidgetSprites> spritesMap = new HashMap<>();
    private Supplier<FurnaceSettingsV2> settingsV2;
    private WidgetGroup substractionGroup;


    public RedstoneModeButton(int x, int y, int width, int height, OnPress onPress, OnPress rightClick, WidgetGroup substractionGroup, Supplier<FurnaceSettingsV2> settingsV2) {
        super(x, y, width, height, "redstone_mode_ignore", onPress, rightClick);
        this.substractionGroup = substractionGroup;
        this.settingsV2 = settingsV2;
    }


    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        this.setTooltip(Tooltip.create(Component.translatable("ironfurnaces.furnace_setting.redstone_mode", Component.translatable(settingsV2.get().redStoneMode().translationKey))));
        if (settingsV2.get().redStoneMode().equals(FurnaceSettingsV2.RedStoneMode.COMPARATOR_SUBTRACTION)) {
            substractionGroup.activeAll();
        } else {
            substractionGroup.deactivateAll();
        }
        Identifier resourcelocation = spritesMap.computeIfAbsent(this.settingsV2.get().redStoneMode(), x -> {
            var baseId = "redstone_mode_" + x.toString().toLowerCase(Locale.ROOT);
            return new WidgetSprites(IronFurnaces.sprite(baseId + "_off"), IronFurnaces.sprite(baseId + "_inactive"), IronFurnaces.sprite(baseId + "_on"), IronFurnaces.sprite(baseId + "_inactive"));
        }).get(this.isActive(), this.shouldHighlight());
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, resourcelocation, this.getX(), this.getY(), this.width, this.height);
    }


}
