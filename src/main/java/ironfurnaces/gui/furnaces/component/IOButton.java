package ironfurnaces.gui.furnaces.component;

import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.tileentity.furnaces.menu.FurnacePatternMenu;
import ironfurnaces.tileentity.furnaces.setting.FurnaceSettingsV2;
import ironfurnaces.tileentity.furnaces.setting.RelativeFaceHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;
//? 1.20.1 {

//? } else {
/*import net.minecraft.client.gui.components.WidgetSprites;
*///?}
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

public class IOButton extends BaseImageButton {
    private final FurnaceSettingsV2.RelativeFace relativeFace;
    private final OnPress rightClick;
    protected Supplier<FurnaceSettingsV2.IOMode> currentState;
    private final Map<FurnaceSettingsV2.IOMode, WidgetSprites> spritesMap = new HashMap<>();

    public IOButton(int x, int y, int width, int height, OnPress onPress, FurnaceSettingsV2.RelativeFace relativeFace, OnPress rightClick, Supplier<FurnaceSettingsV2.IOMode> currentState) {
        super(x, y, width, height, "iomode_none", onPress);
        this.relativeFace = relativeFace;
        this.rightClick = rightClick;
        this.currentState = currentState;
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
        return Tooltip.create(Component.translatable("ironfurnaces.furnace_setting.relative_face." + relativeFace.getSerializedName(), Component.translatable(currentState.get().translationKey)));
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.setTooltip(getTooltip());
        ResourceLocation resourcelocation = spritesMap.computeIfAbsent(currentState.get(), x -> {
            var baseId = "iomode_" + x.toString().toLowerCase(Locale.ROOT);
            return new WidgetSprites(IronFurnaces.sprite(baseId + "_off"), IronFurnaces.sprite(baseId + "_inactive"), IronFurnaces.sprite(baseId + "_on"), IronFurnaces.sprite(baseId + "_inactive"));
        }).get(this.isActive(), this.shouldHighlight());
        //? 1.20.1 {
        

        guiGraphics.blit(resourcelocation, this.getX(), this.getY(), 0, 0, this.width, this.height, getTextureWidth(), getTextureHeight());
        //? } else {
        /*guiGraphics.blitSprite(resourcelocation, this.getX(), this.getY(), this.width, this.height);
        *///?}


    }
}
