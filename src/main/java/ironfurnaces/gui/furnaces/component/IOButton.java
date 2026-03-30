package ironfurnaces.gui.furnaces.component;

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

public class IOButton extends MutiStateImageButton<FurnaceSettingsV2.IOMode> {
    private final FurnaceSettingsV2.RelativeFace relativeFace;
    private final FurnacePatternMenu menu;
    private final OnPress rightClick;

    public IOButton(int x, int y, int width, int height, int xTexStart, int yTexStart, ResourceLocation resourceLocation, OnPress onPress, FurnaceSettingsV2.IOMode initState, FurnaceSettingsV2.RelativeFace relativeFace, FurnacePatternMenu menu, OnPress rightClick) {
        super(x, y, width, height, xTexStart, yTexStart, resourceLocation, onPress, initState);
        this.relativeFace = relativeFace;
        this.menu = menu;
        this.rightClick = rightClick;
    }

    public IOButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int xDiffTex, int yDiffTex, ResourceLocation resourceLocation, OnPress onPress, FurnaceSettingsV2.IOMode initState, FurnaceSettingsV2.RelativeFace relativeFace, FurnacePatternMenu menu, OnPress rightClick) {
        super(x, y, width, height, xTexStart, yTexStart, xDiffTex, yDiffTex, resourceLocation, onPress, initState);
        this.relativeFace = relativeFace;
        this.menu = menu;
        this.rightClick = rightClick;
    }

    public IOButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int xDiffTex, int yDiffTex, ResourceLocation resourceLocation, int textureWidth, int textureHeight, OnPress onPress, FurnaceSettingsV2.IOMode initState, FurnaceSettingsV2.RelativeFace relativeFace, FurnacePatternMenu menu, OnPress rightClick) {
        super(x, y, width, height, xTexStart, yTexStart, xDiffTex, yDiffTex, resourceLocation, textureWidth, textureHeight, onPress, initState);
        this.relativeFace = relativeFace;
        this.menu = menu;
        this.rightClick = rightClick;
    }

    public IOButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int xDiffTex, int yDiffTex, ResourceLocation resourceLocation, int textureWidth, int textureHeight, OnPress onPress, Component message, FurnaceSettingsV2.IOMode initState, FurnaceSettingsV2.RelativeFace relativeFace, FurnacePatternMenu menu, OnPress rightClick) {
        super(x, y, width, height, xTexStart, yTexStart, xDiffTex, yDiffTex, resourceLocation, textureWidth, textureHeight, onPress, message, initState);
        this.relativeFace = relativeFace;
        this.menu = menu;
        this.rightClick = rightClick;
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
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Direction world = RelativeFaceHelper.toWorld(Minecraft.getInstance().level.getBlockState(menu.bePos).getValue(BlockStateProperties.HORIZONTAL_FACING), relativeFace);
        if (!this.getCurrentState().equals(menu.getSettingsV2().IOSetting().get(world))) {
            this.setCurrentState(menu.getSettingsV2().IOSetting().get(world));
        }
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        this.setTooltip(Tooltip.create(Component.translatable("ironfurnaces.furnace_setting.relative_face." + relativeFace.getSerializedName(), Component.translatable(currentState.translationKey))));
    }

}
