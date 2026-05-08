package ironfurnaces.gui.furnaces;

import com.clefal.nirvana_lib.utils.NetworkUtils;
import ironfurnaces.gui.furnaces.component.BaseBoolStatuImageButton;
import ironfurnaces.gui.furnaces.component.BaseImageButton;
import ironfurnaces.gui.furnaces.component.IOButton;
import ironfurnaces.gui.furnaces.component.PositionContext;
import ironfurnaces.gui.furnaces.component.RedstoneModeButton;
import ironfurnaces.gui.furnaces.component.SideTab;
import ironfurnaces.gui.furnaces.component.SideTabPanel;
import ironfurnaces.gui.furnaces.component.WidgetGroup;
import ironfurnaces.gui.furnaces.renderer.AbstractPatternScreenRenderHandler;
import ironfurnaces.gui.furnaces.renderer.PatternScreenRenderHandlerManager;
import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.network.C2SUpdateFurnaceSettingPacket;
import ironfurnaces.network.C2SUpdateMenuPacket;
import ironfurnaces.tileentity.furnaces.menu.FurnacePatternMenu;
import ironfurnaces.tileentity.furnaces.menu.MenuConstant;
import ironfurnaces.tileentity.furnaces.setting.FurnaceSettingsV2;
import ironfurnaces.tileentity.furnaces.setting.RelativeFaceHelper;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class FurnacePatternScreen extends AbstractContainerScreen<FurnacePatternMenu> {
    public static final Identifier WIDGET = IronFurnaces.gui("button_widget");

    private static final int SIDE_TAB_OFFSET_X = -MenuConstant.SIDE_BUTTON_WIDTH;
    private static final int SIDE_PANEL_OFFSET_X = -MenuConstant.SIDE_PANEL_WIDTH;

    private static final int SETTINGS_BUTTON_SIZE = 14;
    private static final int IO_BUTTON_SIZE = 10;
    private static final int IO_BUTTON_INTERVAL = 13;

    private static final int SETTINGS_PANEL_PADDING_X = 10;
    private static final int AUTO_BUTTON_Y_OFFSET = 12;
    private static final int IO_BUTTON_Y_OFFSET = 45;
    private static final int REDSTONE_BUTTON_BOTTOM_OFFSET = 25;

    public final PositionContext positionContext = new PositionContext();

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

    @Getter
    private AbstractPatternScreenRenderHandler renderHandler;

    public FurnacePatternScreen(FurnacePatternMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Override
    protected void init() {
        super.init();

        updatePositionContext();
        createPanelGroups();
        createSettingsPanelWidgets();
        createSideTabs();
        registerSettingsWidgets();

        updateRenderHandler(menu.getMode().getId());

        this.settingsPanelGroup.deactivateAll();
        this.remainingCachePanelGroup.deactivateAll();
        this.augmentCachePanelGroup.deactivateAll();
    }

    private void updatePositionContext() {
        int sideButtonBaseX = this.leftPos + SIDE_TAB_OFFSET_X;
        int sideBaseY = this.topPos;
        int sidePanelBaseX = this.leftPos + SIDE_PANEL_OFFSET_X;

        this.positionContext.setBase(sideButtonBaseX, sideBaseY, sidePanelBaseX);
    }

    private void createPanelGroups() {
        this.remainingCachePanelGroup = new WidgetGroup();
        this.augmentCachePanelGroup = new WidgetGroup();
    }

    private void createSettingsPanelWidgets() {
        int buttonStartX = settingsButtonStartX();

        createAutoButtons(buttonStartX);
        createRedstoneButtons(buttonStartX);
        createIoButtons(buttonStartX);

        this.settingsPanelGroup = new WidgetGroup(
                this.upIoButton,
                this.downIoButton,
                this.faceIoButton,
                this.backIoButton,
                this.leftIoButton,
                this.rightIoButton,
                this.redstoneModeButton,
                this.autoInputButton,
                this.autoOutputButton
        );
    }

    private void createAutoButtons(int buttonStartX) {
        this.autoInputButton = new BaseBoolStatuImageButton(
                buttonStartX,
                this.topPos + AUTO_BUTTON_Y_OFFSET,
                SETTINGS_BUTTON_SIZE,
                SETTINGS_BUTTON_SIZE,
                "auto_input",
                button -> sendSettings(menu.getSettingsV2().withAutoInput(!menu.getSettingsV2().autoInput())),
                () -> menu.getSettingsV2().autoInput()
        ) {

            @Override
            public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
                super.extractContents(graphics, mouseX, mouseY, a);
                graphics.setTooltipForNextFrame(Component.translatable("ironfurnaces.furnace_setting.auto_input", menu.getSettingsV2().autoInput()), mouseX, mouseY);
            }

        };

        this.autoOutputButton = new BaseBoolStatuImageButton(
                buttonStartX + SETTINGS_BUTTON_SIZE + 2,
                this.topPos + AUTO_BUTTON_Y_OFFSET,
                SETTINGS_BUTTON_SIZE,
                SETTINGS_BUTTON_SIZE,
                "auto_output",
                button -> sendSettings(menu.getSettingsV2().withAutoOutput(!menu.getSettingsV2().autoOutput())),
                () -> menu.getSettingsV2().autoOutput()
        ) {
            @Override
            public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
                super.extractContents(graphics, mouseX, mouseY, a);
                graphics.setTooltipForNextFrame(Component.translatable("ironfurnaces.furnace_setting.auto_output", menu.getSettingsV2().autoOutput()), mouseX, mouseY);
            }

        };
    }

    private void createRedstoneButtons(int buttonStartX) {
        int y = this.topPos + MenuConstant.SIDE_PANEL_HEIGHT - REDSTONE_BUTTON_BOTTOM_OFFSET;

        this.subtractionValueIncButton = createDynamicTooltipButton(
                buttonStartX + SETTINGS_BUTTON_SIZE + 2,
                y,
                "redstone_mode_comparator_subtraction_plus",
                button -> sendSettings(menu.getSettingsV2().withSubtractionNumber(
                        Math.min(menu.getSettingsV2().subtractionNumber() + 1, 15)
                )),
                () -> Component.translatable(
                        "ironfurnaces.furnace_setting.redstone_value",
                        menu.getSettingsV2().subtractionNumber()
                )
        );

        this.subtractionValueDecButton = createDynamicTooltipButton(
                buttonStartX + SETTINGS_BUTTON_SIZE * 2 + 4,
                y,
                "redstone_mode_comparator_subtraction_subtract",
                button -> sendSettings(menu.getSettingsV2().withSubtractionNumber(
                        Math.max(menu.getSettingsV2().subtractionNumber() - 1, 0)
                )),
                () -> Component.translatable(
                        "ironfurnaces.furnace_setting.redstone_value",
                        menu.getSettingsV2().subtractionNumber()
                )
        );

        WidgetGroup subtractionButtons = new WidgetGroup(
                this.subtractionValueIncButton,
                this.subtractionValueDecButton
        );

        this.redstoneModeButton = new RedstoneModeButton(
                buttonStartX,
                y,
                SETTINGS_BUTTON_SIZE,
                SETTINGS_BUTTON_SIZE,
                button -> sendSettings(menu.getSettingsV2().withRedStoneMode(menu.getSettingsV2().redStoneMode().next())),
                button -> sendSettings(menu.getSettingsV2().withRedStoneMode(menu.getSettingsV2().redStoneMode().previous())),
                subtractionButtons,
                menu::getSettingsV2
        );

        subtractionButtons.deactivateAll();
    }

    private BaseImageButton createDynamicTooltipButton(
            int x,
            int y,
            String textureName,
            Button.OnPress onPress,
            Supplier<Component> tooltipSupplier
    ) {
        return new BaseImageButton(
                x,
                y,
                SETTINGS_BUTTON_SIZE,
                SETTINGS_BUTTON_SIZE,
                textureName,
                onPress
        ) {
            @Override
            public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
                super.extractContents(graphics, mouseX, mouseY, a);
                graphics.setTooltipForNextFrame(tooltipSupplier.get(), mouseX, mouseY);
            }

        };
    }

    private void createIoButtons(int buttonStartX) {
        int centerX = buttonStartX + IO_BUTTON_SIZE * 2 - 5;
        int centerY = this.topPos + IO_BUTTON_Y_OFFSET;

        this.upIoButton = createIoButton(FurnaceSettingsV2.RelativeFace.UP);
        this.upIoButton.setPosition(centerX, centerY - IO_BUTTON_INTERVAL);

        this.downIoButton = createIoButton(FurnaceSettingsV2.RelativeFace.DOWN);
        this.downIoButton.setPosition(centerX + IO_BUTTON_INTERVAL, centerY + IO_BUTTON_INTERVAL);

        this.faceIoButton = createIoButton(FurnaceSettingsV2.RelativeFace.FRONT);
        this.faceIoButton.setPosition(centerX, centerY);

        this.backIoButton = createIoButton(FurnaceSettingsV2.RelativeFace.BACK);
        this.backIoButton.setPosition(centerX, centerY + IO_BUTTON_INTERVAL);

        this.leftIoButton = createIoButton(FurnaceSettingsV2.RelativeFace.LEFT);
        this.leftIoButton.setPosition(centerX - IO_BUTTON_INTERVAL, centerY);

        this.rightIoButton = createIoButton(FurnaceSettingsV2.RelativeFace.RIGHT);
        this.rightIoButton.setPosition(centerX + IO_BUTTON_INTERVAL, centerY);
    }

    private IOButton createIoButton(FurnaceSettingsV2.RelativeFace relativeFace) {
        return new IOButton(
                0,
                0,
                IO_BUTTON_SIZE,
                IO_BUTTON_SIZE,
                button -> cycleIoMode(relativeFace, true),
                relativeFace,
                button -> cycleIoMode(relativeFace, false),
                () -> getIoMode(relativeFace)
        );
    }

    private void cycleIoMode(FurnaceSettingsV2.RelativeFace relativeFace, boolean forward) {
        Direction worldSide = getWorldSide(relativeFace);
        FurnaceSettingsV2.IOMode current = menu.getSettingsV2().IOSetting().get(worldSide);
        FurnaceSettingsV2.IOMode next = forward ? current.next() : current.previous();

        sendSettings(menu.getSettingsV2().withDirectionChanged(worldSide, next));
    }

    private FurnaceSettingsV2.IOMode getIoMode(FurnaceSettingsV2.RelativeFace relativeFace) {
        return menu.getSettingsV2().IOSetting().get(getWorldSide(relativeFace));
    }

    private Direction getWorldSide(FurnaceSettingsV2.RelativeFace relativeFace) {
        return RelativeFaceHelper.toWorld(getBlockFacing(), relativeFace);
    }

    private Direction getBlockFacing() {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.level == null) {
            return Direction.NORTH;
        }

        BlockState state = minecraft.level.getBlockState(this.menu.bePos);

        if (!state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            return Direction.NORTH;
        }

        return state.getValue(BlockStateProperties.HORIZONTAL_FACING);
    }

    private void createSideTabs() {
        this.settingsTabButton = createSideTab(
                "side_button_setting",
                "screen.ironfurnaces.side_tab.setting",
                0,
                new SideTabPanel(this.settingsPanelGroup, positionContext, 0),
                this.menu::isOpenSetting
        );

        this.remainingCacheTabButton = createSideTab(
                "side_button_remaining",
                "screen.ironfurnaces.side_tab.remaining_items",
                2,
                new SideTabPanel(this.remainingCachePanelGroup, positionContext, 1),
                this.menu::isOpenRemaining
        );

        this.augmentCacheTabButton = createSideTab(
                "side_button_augment",
                "screen.ironfurnaces.side_tab.augment",
                1,
                new SideTabPanel(this.augmentCachePanelGroup, positionContext, 2),
                this.menu::isOpenAugment
        );
    }

    private ImageButton createSideTab(
            String textureName,
            String tooltipKey,
            int menuIndex,
            SideTabPanel panel,
            BooleanSupplier openState
    ) {
        ImageButton button = this.addRenderableWidget(new SideTab(
                0,
                0,
                MenuConstant.SIDE_BUTTON_WIDTH,
                MenuConstant.SIDE_BUTTON_HEIGHT,
                textureName,
                btn -> NetworkUtils.sendToServer(new C2SUpdateMenuPacket(menuIndex)),
                panel,
                openState,
                positionContext
        ));

        button.setTooltip(Tooltip.create(Component.translatable(tooltipKey)));
        return button;
    }

    private void registerSettingsWidgets() {
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
    }

    private int settingsButtonStartX() {
        return this.leftPos + SIDE_PANEL_OFFSET_X + SETTINGS_PANEL_PADDING_X;
    }

    private void sendSettings(FurnaceSettingsV2 settings) {
        NetworkUtils.sendToServer(new C2SUpdateFurnaceSettingPacket(settings, menu.bePos));
    }

    private static Tooltip tooltip(String key, Object... args) {
        return Tooltip.create(Component.translatable(key, args));
    }

    public void updateRenderHandler(String id) {
        removeCurrentRenderHandlerWidgets();

        this.renderHandler = PatternScreenRenderHandlerManager.INSTANCE.get(id, this);

        addRenderHandlerWidgets(this.renderHandler);
    }

    private void removeCurrentRenderHandlerWidgets() {
        if (this.renderHandler == null) {
            return;
        }

        for (AbstractWidget widget : this.renderHandler.getRenderables()) {
            this.removeWidget(widget);
        }

        for (AbstractWidget widget : this.renderHandler.getRenderableWidget()) {
            this.removeWidget(widget);
        }

        for (AbstractWidget widget : this.renderHandler.getWidget()) {
            this.removeWidget(widget);
        }
    }

    private void addRenderHandlerWidgets(AbstractPatternScreenRenderHandler handler) {
        for (AbstractWidget widget : handler.getRenderables()) {
            this.addRenderableOnly(widget);
        }

        for (AbstractWidget widget : handler.getRenderableWidget()) {
            this.addRenderableWidget(widget);
        }

        for (AbstractWidget widget : handler.getWidget()) {
            this.addWidget(widget);
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        this.positionContext.zero();
        this.menu.updateMode();

        if (this.renderHandler == null) {
            updateRenderHandler(this.menu.getMode().getId());
        }

        this.renderHandler.extractBackground(graphics, partialTick, mouseX, mouseY);

        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if (this.renderHandler != null && this.renderHandler.needRenderCustomTooltip(graphics, mouseX, mouseY)) {
            this.renderHandler.renderCustomTooltip(graphics, mouseX, mouseY);
            return;
        }

        super.extractTooltip(graphics, mouseX, mouseY);
    }
}