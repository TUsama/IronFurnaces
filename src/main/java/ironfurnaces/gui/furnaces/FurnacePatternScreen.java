package ironfurnaces.gui.furnaces;

import com.clefal.nirvana_lib.utils.NetworkUtils;
import ironfurnaces.gui.furnaces.component.*;
import ironfurnaces.gui.furnaces.renderer.AbstractPatternScreenRenderHandler;
import ironfurnaces.gui.furnaces.renderer.PatternScreenRenderHandlerManager;
import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.network.C2SUpdateFurnaceSettingPacket;
import ironfurnaces.network.C2SUpdateMenuPacket;
import ironfurnaces.tileentity.furnaces.cache.recipe_type_handlers.FarmerDelightCookingRecipeTypeHandler;
import ironfurnaces.tileentity.furnaces.menu.FurnacePatternMenu;
import ironfurnaces.tileentity.furnaces.menu.MenuConstant;
import ironfurnaces.tileentity.furnaces.setting.FurnaceSettingsV2;
import ironfurnaces.tileentity.furnaces.setting.RelativeFaceHelper;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class FurnacePatternScreen extends AbstractContainerScreen<FurnacePatternMenu> {



    public static final ResourceLocation WIDGET = IronFurnaces.gui("button_widget");
    @Getter
    private AbstractPatternScreenRenderHandler renderHandler;

    protected ImageButton autoInputButton;
    protected ImageButton autoOutputButton;

    protected ImageButton redstoneModeButton;


    protected ImageButton subtractionValueDecButton;
    protected ImageButton subtractionValueIncButton;

    protected ImageButton settingsTabButton;
    protected ImageButton remainingCacheTabButton;
    protected ImageButton augmentCacheTabButton;

    protected WidgetGroup settingsPanelGroup;
    protected WidgetGroup remainingCachePanelGroup;
    protected WidgetGroup augmentCachePanelGroup;

    protected IOButton upIoButton;
    protected IOButton downIoButton;
    protected IOButton faceIoButton;
    protected IOButton backIoButton;
    protected IOButton leftIoButton;
    protected IOButton rightIoButton;

    // 左侧标签栏的基准位置：紧贴原版熔炉背景左边
    private static final int SIDE_TAB_OFFSET_X = -MenuConstant.SIDE_BUTTON_WIDTH;

    // panel 的 x 相对于 gui 左上角：向左展开 60 像素
    private static final int SIDE_PANEL_OFFSET_X = -MenuConstant.SIDE_PANEL_WIDTH;
    public final PositionContext positionContext = new PositionContext();


    public FurnacePatternScreen(FurnacePatternMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Override
    protected void init() {
        super.init();

        int sideButtonBaseX = this.leftPos + SIDE_TAB_OFFSET_X;
        int sideBaseY = this.topPos;
        int sidePanelBaseX = this.leftPos + SIDE_PANEL_OFFSET_X;
        this.positionContext.setBase(sideButtonBaseX, sideBaseY, sidePanelBaseX);
        // =========================
        // settings
        // =========================

        int squareLength = 14;


        Function<FurnaceSettingsV2, Button.OnPress> makeOnPress = settingsV2 -> {
            return button -> {
                NetworkUtils.sendToServer(new C2SUpdateFurnaceSettingPacket(settingsV2, menu.bePos));
            };
        };

        Int2IntFunction uStart = index -> index * 14;

        int buttonStartX = sidePanelBaseX + 10;
        this.autoInputButton = new BaseBoolStatuImageButton(buttonStartX, topPos + 12, squareLength, squareLength, "auto_input", button -> NetworkUtils.sendToServer(new C2SUpdateFurnaceSettingPacket(menu.getSettingsV2().withAutoInput(!menu.getSettingsV2().autoInput()), menu.bePos)), () -> menu.getSettingsV2().autoInput()){
            @Override
            public @Nullable Tooltip getTooltip() {
                return Tooltip.create(Component.translatable("ironfurnaces.furnace_setting.auto_input", menu.getSettingsV2().autoInput()));
            }

            @Override
            public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
                super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
                this.setTooltip(getTooltip());
            }
        };

        this.autoOutputButton = new BaseBoolStatuImageButton(buttonStartX + squareLength + 2, topPos + 12, squareLength, squareLength, "auto_output", button -> NetworkUtils.sendToServer(new C2SUpdateFurnaceSettingPacket(menu.getSettingsV2().withAutoOutput(!menu.getSettingsV2().autoOutput()), menu.bePos)), () -> menu.getSettingsV2().autoOutput()){
            @Override
            public @Nullable Tooltip getTooltip() {
                return Tooltip.create(Component.translatable("ironfurnaces.furnace_setting.auto_output", menu.getSettingsV2().autoOutput()));
            }

            @Override
            public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
                super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
                this.setTooltip(getTooltip());
            }
        };

        int i = topPos + MenuConstant.SIDE_PANEL_HEIGHT - 25;


        this.subtractionValueIncButton = new BaseImageButton(buttonStartX + squareLength + 2, i, squareLength, squareLength, "redstone_mode_comparator_subtraction_plus", button -> NetworkUtils.sendToServer(new C2SUpdateFurnaceSettingPacket(menu.getSettingsV2().withSubtractionNumber(Math.min(menu.getSettingsV2().subtractionNumber() + 1, 15)), menu.bePos))){
            @Override
            public @Nullable Tooltip getTooltip() {
                return Tooltip.create(Component.translatable("ironfurnaces.furnace_setting.redstone_value", menu.getSettingsV2().subtractionNumber()));
            }

            @Override
            public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
                super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
                this.setTooltip(getTooltip());
            }
        };


        this.subtractionValueDecButton = new BaseImageButton(buttonStartX + (squareLength * 2) + 4, i, squareLength, squareLength, "redstone_mode_comparator_subtraction_subtract", button -> {
            NetworkUtils.sendToServer(new C2SUpdateFurnaceSettingPacket(menu.getSettingsV2().withSubtractionNumber(Math.max(menu.getSettingsV2().subtractionNumber() - 1, 0)), menu.bePos));
        }){
            @Override
            public @Nullable Tooltip getTooltip() {
                return Tooltip.create(Component.translatable("ironfurnaces.furnace_setting.redstone_value", menu.getSettingsV2().subtractionNumber()));
            }

            @Override
            public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
                super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
                this.setTooltip(getTooltip());
            }
        };

        WidgetGroup widgetGroup = new WidgetGroup(subtractionValueIncButton, subtractionValueDecButton);
        this.redstoneModeButton = new RedstoneModeButton(buttonStartX, i, squareLength, squareLength, button -> NetworkUtils.sendToServer(new C2SUpdateFurnaceSettingPacket(menu.getSettingsV2().withRedStoneMode(menu.getSettingsV2().redStoneMode().next()), menu.bePos)), button -> NetworkUtils.sendToServer(new C2SUpdateFurnaceSettingPacket(menu.getSettingsV2().withRedStoneMode(menu.getSettingsV2().redStoneMode().previous()), menu.bePos)), widgetGroup, menu::getSettingsV2);
        widgetGroup.deactivateAll();

        int ioButtonSize = 10;
        var facing = Minecraft.getInstance().level.getBlockState(this.getMenu().bePos).getValue(BlockStateProperties.HORIZONTAL_FACING);

        Function<FurnaceSettingsV2.RelativeFace, IOButton> ioButtonMaker = relativeFace -> {
            Direction worldFacing = RelativeFaceHelper.toWorld(facing, relativeFace);
            return new IOButton(0, 0, ioButtonSize, ioButtonSize, button -> {
                FurnaceSettingsV2.IOMode next = menu.getSettingsV2().IOSetting().get(worldFacing).next();
                NetworkUtils.sendToServer(new C2SUpdateFurnaceSettingPacket(menu.getSettingsV2().withDirectionChanged(worldFacing, next), menu.bePos));
            }, relativeFace, button -> {
                FurnaceSettingsV2.IOMode previous = menu.getSettingsV2().IOSetting().get(worldFacing).previous();
                NetworkUtils.sendToServer(new C2SUpdateFurnaceSettingPacket(menu.getSettingsV2().withDirectionChanged(worldFacing, previous), menu.bePos));
            }, () -> menu.getSettingsV2().IOSetting().get(RelativeFaceHelper.toWorld(Minecraft.getInstance().level.getBlockState(menu.bePos).getValue(BlockStateProperties.HORIZONTAL_FACING), relativeFace)));
        };

        int upButtonX = buttonStartX + ioButtonSize * 2 - 5;
        int upButtonY = topPos + 45;
        int interval = 13;

        this.upIoButton = ioButtonMaker.apply(FurnaceSettingsV2.RelativeFace.UP);
        this.upIoButton.setPosition(upButtonX, upButtonY - interval);

        this.downIoButton = ioButtonMaker.apply(FurnaceSettingsV2.RelativeFace.DOWN);
        this.downIoButton.setPosition(upButtonX + interval, upButtonY + interval);

        this.faceIoButton = ioButtonMaker.apply(FurnaceSettingsV2.RelativeFace.FRONT);
        this.faceIoButton.setPosition(upButtonX, upButtonY);

        this.backIoButton = ioButtonMaker.apply(FurnaceSettingsV2.RelativeFace.BACK);
        this.backIoButton.setPosition(upButtonX, upButtonY + interval);

        this.leftIoButton = ioButtonMaker.apply(FurnaceSettingsV2.RelativeFace.LEFT);
        this.leftIoButton.setPosition(upButtonX - interval, upButtonY);

        this.rightIoButton = ioButtonMaker.apply(FurnaceSettingsV2.RelativeFace.RIGHT);
        this.rightIoButton.setPosition(upButtonX + interval, upButtonY);

        this.settingsPanelGroup = new WidgetGroup(
                this.upIoButton,
                this.downIoButton,
                this.faceIoButton,
                this.backIoButton,
                this.leftIoButton,
                this.rightIoButton,

                /*this.subtractionValueDecButton,
                this.subtractionValueIncButton,*/

                this.redstoneModeButton,
                this.autoInputButton,
                this.autoOutputButton
        );

        this.settingsTabButton = this.addRenderableWidget(new SideTab(
                0,
                0,
                MenuConstant.SIDE_BUTTON_WIDTH,
                MenuConstant.SIDE_BUTTON_HEIGHT,
                "side_button_setting",
                btn -> {NetworkUtils.sendToServer(new C2SUpdateMenuPacket(0));},
                new SideTabPanel(this.settingsPanelGroup, positionContext, 0),
                this.menu::isOpenSetting,
                positionContext
        ));
        
        this.settingsTabButton.setTooltip(Tooltip.create(Component.translatable("screen.ironfurnaces.side_tab.setting")));




        this.addRenderableWidget(autoInputButton);
        this.addRenderableWidget(autoOutputButton);
        this.addRenderableWidget(redstoneModeButton);
        this.addRenderableWidget(subtractionValueIncButton);
        this.addRenderableWidget(subtractionValueDecButton);
        this.addRenderableWidget(upIoButton);
        this.addRenderableWidget(downIoButton);
        this.addRenderableWidget(backIoButton);
        this.addRenderableWidget(leftIoButton);
        this.addRenderableWidget(rightIoButton);
        this.addRenderableWidget(faceIoButton);
        updateRenderHandler(menu.getMode().getId());

        // =========================
        // remaining
        // =========================
        this.remainingCacheTabButton = this.addRenderableWidget(new SideTab(
                0,
                0,
                MenuConstant.SIDE_BUTTON_WIDTH,
                MenuConstant.SIDE_BUTTON_HEIGHT,
                "side_button_remaining",
                btn -> {
                    NetworkUtils.sendToServer(new C2SUpdateMenuPacket(2));
                },
                new SideTabPanel(this.remainingCachePanelGroup, positionContext, 1),
                this.menu::isOpenRemaining,
                positionContext
        ));
        
        this.remainingCacheTabButton.setTooltip(Tooltip.create(Component.translatable("screen.ironfurnaces.side_tab.remaining_items")));

        this.remainingCachePanelGroup = new WidgetGroup();


        // =========================
        // augment
        // =========================
        this.augmentCacheTabButton = this.addRenderableWidget(new SideTab(
                0,
                0,
                MenuConstant.SIDE_BUTTON_WIDTH,
                MenuConstant.SIDE_BUTTON_HEIGHT,
               "side_button_augment",
                btn -> {
                    NetworkUtils.sendToServer(new C2SUpdateMenuPacket(1));
                },
                new SideTabPanel(this.augmentCachePanelGroup, positionContext, 2),
                this.menu::isOpenAugment,
                positionContext

        ));

        this.augmentCacheTabButton.setTooltip(Tooltip.create(Component.translatable("screen.ironfurnaces.side_tab.augment")));

        this.augmentCachePanelGroup = new WidgetGroup();

        // 初始全部隐藏 panel
        this.settingsPanelGroup.deactivateAll();

    }

    public void updateRenderHandler(String id){
        if (this.renderHandler != null){
            for (AbstractWidget renderable : this.renderHandler.getRenderables()) {
                this.removeWidget(renderable);
            }
            for (AbstractWidget abstractWidget : this.renderHandler.getRenderableWidget()) {
                this.removeWidget(abstractWidget);
            }
            for (AbstractWidget abstractWidget : this.renderHandler.getWidget()) {
                this.removeWidget(abstractWidget);
            }
        }

        this.renderHandler = PatternScreenRenderHandlerManager.INSTANCE.get(id, this);

        for (AbstractWidget renderable : this.renderHandler.getRenderables()) {
            this.addRenderableOnly(renderable);
        }
        for (AbstractWidget abstractWidget : this.renderHandler.getRenderableWidget()) {
            this.addRenderableWidget(abstractWidget);
        }
        for (AbstractWidget abstractWidget : this.renderHandler.getWidget()) {
            this.addWidget(abstractWidget);
        }
    }


    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        renderHandler.renderBg(guiGraphics, partialTick, mouseX, mouseY);
    }


    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.positionContext.zero();
        this.menu.updateMode();
        //~ if >1.20.1 'guiGraphics' -> 'guiGraphics, mouseX, mouseY, partialTick'
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        if (renderHandler.needRenderCustomTooltip(guiGraphics, mouseX, mouseY)) {
            renderHandler.renderCustomTooltip(guiGraphics, mouseX, mouseY);
        } else {
            super.renderTooltip(guiGraphics, mouseX, mouseY);
        }
    }
}
