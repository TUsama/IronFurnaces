package ironfurnaces.gui.furnaces.component;

import com.mojang.blaze3d.systems.RenderSystem;
import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.tileentity.furnaces.setting.FurnaceSettingsV2;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
//? 1.20.1 {

//? } else {
/*import net.minecraft.client.gui.components.WidgetSprites;
*///?}
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

public class RedstoneModeButton extends BaseImageButton {
    private Supplier<FurnaceSettingsV2> settingsV2;
    private final Map<FurnaceSettingsV2.RedStoneMode, WidgetSprites> spritesMap = new HashMap<>();
    private WidgetGroup substractionGroup;
    private final OnPress rightClick;


    public RedstoneModeButton(int x, int y, int width, int height, OnPress onPress, OnPress rightClick, WidgetGroup substractionGroup, Supplier<FurnaceSettingsV2> settingsV2) {
        super(x, y, width, height, "redstone_mode_ignore", onPress);
        this.rightClick = rightClick;
        this.substractionGroup = substractionGroup;
        this.settingsV2 = settingsV2;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.active && this.visible && button == 1) {
            boolean flag = this.clicked(mouseX, mouseY);
            if (flag) {
                this.playDownSound(Minecraft.getInstance().getSoundManager());
                this.rightClick.onPress(this);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    @Override
    public @Nullable Tooltip getTooltip() {
        return Tooltip.create(Component.translatable("ironfurnaces.furnace_setting.redstone_mode", Component.translatable(settingsV2.get().redStoneMode().translationKey)));
    }


    //? 1.20.1 {
    
    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.setTooltip(getTooltip());

        ResourceLocation resourcelocation = spritesMap.computeIfAbsent(this.settingsV2.get().redStoneMode(), x -> {
            var baseId = "redstone_mode_" + x.toString().toLowerCase(Locale.ROOT);
            return new WidgetSprites(IronFurnaces.sprite(baseId + "_off"), IronFurnaces.sprite(baseId + "_inactive"), IronFurnaces.sprite(baseId + "_on"), IronFurnaces.sprite(baseId + "_inactive"));
        }).get(this.isActive(), this.shouldHighlight());

        if (settingsV2.get().redStoneMode().equals(FurnaceSettingsV2.RedStoneMode.COMPARATOR_SUBTRACTION)){
            substractionGroup.activeAll();
        } else {
            substractionGroup.deactivateAll();
        }
        guiGraphics.blit(resourcelocation, this.getX(), this.getY(), 0, 0, this.width, this.height, getTextureWidth(), getTextureHeight());
    }
    //? } else {

    /*@Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (settingsV2.get().redStoneMode().equals(FurnaceSettingsV2.RedStoneMode.COMPARATOR_SUBTRACTION)){
            substractionGroup.activeAll();
        } else {
            substractionGroup.deactivateAll();
        }
        ResourceLocation resourcelocation = spritesMap.computeIfAbsent(this.settingsV2.get().redStoneMode(), x -> {
            var baseId = x.toString().toLowerCase(Locale.ROOT);
            return new WidgetSprites(IronFurnaces.sprite(baseId + "_off"), IronFurnaces.sprite(baseId + "_inactive"), IronFurnaces.sprite(baseId + "_on"), IronFurnaces.sprite(baseId + "_inactive"));
        }).get(this.isActive(), this.shouldHighlight());
        guiGraphics.blitSprite(resourcelocation, this.getX(), this.getY(), this.width, this.height);
    }

    *///?}

}
