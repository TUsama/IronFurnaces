package ironfurnaces.gui.furnaces;

import com.clefal.nirvana_lib.utils.NetworkUtils;
import com.clefal.nirvana_lib.utils.ResourceLocationUtils;
import ironfurnaces.gui.furnaces.component.*;
import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.network.C2SUpdateFurnaceSettingPacket;
import ironfurnaces.network.C2SUpdateMenuPacket;
import ironfurnaces.tileentity.furnaces.FurnaceMode;
import ironfurnaces.tileentity.furnaces.menu.FurnacePatternMenu;
import ironfurnaces.tileentity.furnaces.menu.MenuConstant;
import ironfurnaces.tileentity.furnaces.pattern.FurnacePattern;
import ironfurnaces.tileentity.furnaces.setting.FurnaceSettingsV2;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.function.Function;

public class FurnacePatternScreen extends AbstractContainerScreen<FurnacePatternMenu> {


    private static final ResourceLocation VANILLA = ResourceLocationUtils.make("minecraft", "textures/gui/container/furnace.png");
    public static final ResourceLocation WIDGET = IronFurnaces.gui("button_widget");

    private static final Function<FurnaceMode, ResourceLocation> DEFAULT_TEX = Util.memoize((mode) -> switch (mode){
        case FURNACE -> VANILLA;
        default -> IronFurnaces.gui(mode.toString().toLowerCase(Locale.ROOT) + "/default");
    });
    private FurnaceMode mode;
    protected ImageButton autoInputButton;
    protected ImageButton autoOutputButton;

    protected ImageButton redstoneModeButton;


    protected ImageButton autoFillButton;

    protected ImageButton subtractionValueDecButton;
    protected ImageButton subtractionValueIncButton;

    protected ImageButton settingsTabButton;
    protected ImageButton remainingCacheTabButton;
    protected ImageButton augmentCacheTabButton;
    private PageButton forwardButton;
    private PageButton backButton;

    protected WidgetGroup settingsPanelGroup;
    protected WidgetGroup remainingCachePanelGroup;
    protected WidgetGroup augmentCachePanelGroup;

    protected IOButton upIoButton;
    protected IOButton downIoButton;
    protected IOButton northIoButton;
    protected IOButton southIoButton;
    protected IOButton westIoButton;
    protected IOButton eastIoButton;

    protected WidgetGroup factoryGroup;
    protected WidgetGroup generatorGroup;
    private WidgetGroup pageGroup;
    // 左侧标签栏的基准位置：紧贴原版熔炉背景左边
    private static final int SIDE_TAB_OFFSET_X = -MenuConstant.SIDE_BUTTON_WIDTH;

    // panel 的 x 相对于 gui 左上角：向左展开 60 像素
    private static final int SIDE_PANEL_OFFSET_X = -MenuConstant.SIDE_PANEL_WIDTH;
    public final PositionContext positionContext = new PositionContext();


    public FurnacePatternScreen(FurnacePatternMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.mode = menu.blockEntity.getMode();
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
        this.autoInputButton = new ImageButton(buttonStartX, topPos + 12, squareLength, squareLength, uStart.applyAsInt(0), 0, WIDGET, button -> NetworkUtils.sendToServer(new C2SUpdateFurnaceSettingPacket(menu.getSettingsV2().withAutoInput(!menu.getSettingsV2().autoInput()), menu.bePos))){
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

        this.autoOutputButton = new ImageButton(buttonStartX + squareLength + 2, topPos + 12, squareLength, squareLength, uStart.applyAsInt(1), 0, WIDGET, button -> NetworkUtils.sendToServer(new C2SUpdateFurnaceSettingPacket(menu.getSettingsV2().withAutoOutput(!menu.getSettingsV2().autoOutput()), menu.bePos))){
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

        this.autoFillButton = new ImageButton(0, 0, squareLength, squareLength, uStart.applyAsInt(7), 0, WIDGET, button -> NetworkUtils.sendToServer(new C2SUpdateFurnaceSettingPacket(menu.getSettingsV2().withAutoFill(!menu.getSettingsV2().autoFill()), menu.bePos))){
            @Override
            public @Nullable Tooltip getTooltip() {
                return Tooltip.create(Component.translatable("ironfurnaces.furnace_setting.auto_fill", menu.getSettingsV2().autoFill()));
            }

            @Override
            public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
                super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
                this.setTooltip(getTooltip());
            }
        };
        this.autoFillButton.setPosition(leftPos + 9, topPos + 56);

        this.subtractionValueIncButton = new ImageButton(buttonStartX + squareLength + 2, i, squareLength, squareLength, uStart.applyAsInt(8), 0, WIDGET, button -> NetworkUtils.sendToServer(new C2SUpdateFurnaceSettingPacket(menu.getSettingsV2().withSubtractionNumber(Math.min(menu.getSettingsV2().subtractionNumber() + 1, 15)), menu.bePos))){
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


        this.subtractionValueDecButton = new ImageButton(buttonStartX + (squareLength * 2) + 4, i, squareLength, squareLength, uStart.applyAsInt(9), 0, WIDGET, button -> {
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
        this.redstoneModeButton = new RedstoneModeButton(buttonStartX, i, squareLength, squareLength, uStart.applyAsInt(2), 0, WIDGET, button -> NetworkUtils.sendToServer(new C2SUpdateFurnaceSettingPacket(menu.getSettingsV2().withRedStoneMode(menu.getSettingsV2().redStoneMode().next()), menu.bePos)), button -> NetworkUtils.sendToServer(new C2SUpdateFurnaceSettingPacket(menu.getSettingsV2().withRedStoneMode(menu.getSettingsV2().redStoneMode().previous()), menu.bePos)), widgetGroup, menu::getSettingsV2){
            @Override
            public @Nullable Tooltip getTooltip() {
                return Tooltip.create(Component.translatable("ironfurnaces.furnace_setting.redstone_mode", Component.translatable(menu.getSettingsV2().redStoneMode().translationKey)));
            }

            @Override
            public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
                super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
                this.setTooltip(getTooltip());
            }
        };
        widgetGroup.deactivateAll();

        int ioButtonSize = 10;

        Function<Direction, IOButton> ioButtonMaker = direction -> {
            FurnaceSettingsV2.IOMode oldSetting = menu.getSettingsV2().IOSetting().get(direction);

            return new IOButton(0, 0, ioButtonSize, ioButtonSize, 0, 175, 10, 10, WIDGET, button -> {
                FurnaceSettingsV2.IOMode next = menu.getSettingsV2().IOSetting().get(direction).next();
                NetworkUtils.sendToServer(new C2SUpdateFurnaceSettingPacket(menu.getSettingsV2().withDirectionChanged(direction, next), menu.bePos));
            }, oldSetting, direction, menu, button -> {
                FurnaceSettingsV2.IOMode previous = menu.getSettingsV2().IOSetting().get(direction).previous();
                NetworkUtils.sendToServer(new C2SUpdateFurnaceSettingPacket(menu.getSettingsV2().withDirectionChanged(direction, previous), menu.bePos));
            });
        };

        int upButtonX = buttonStartX + ioButtonSize * 2 - 5;
        int upButtonY = topPos + 45;
        int interval = 13;

        this.upIoButton = ioButtonMaker.apply(Direction.UP);
        this.upIoButton.setPosition(upButtonX, upButtonY);

        this.downIoButton = ioButtonMaker.apply(Direction.DOWN);
        this.downIoButton.setPosition(upButtonX + interval, upButtonY + interval);

        this.northIoButton = ioButtonMaker.apply(Direction.NORTH);
        this.northIoButton.setPosition(upButtonX, upButtonY - interval);

        this.southIoButton = ioButtonMaker.apply(Direction.SOUTH);
        this.southIoButton.setPosition(upButtonX, upButtonY + interval);

        this.westIoButton = ioButtonMaker.apply(Direction.WEST);
        this.westIoButton.setPosition(upButtonX - interval, upButtonY);

        this.eastIoButton = ioButtonMaker.apply(Direction.EAST);
        this.eastIoButton.setPosition(upButtonX + interval, upButtonY);

        this.settingsPanelGroup = new WidgetGroup(
                this.upIoButton,
                this.downIoButton,
                this.northIoButton,
                this.southIoButton,
                this.westIoButton,
                this.eastIoButton,

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
                MenuConstant.SIDE_BUTTON_WIDTH * 0,      // u
                42,      // v
                0,      // hoveredVOffset
                WIDGET,
                btn -> {NetworkUtils.sendToServer(new C2SUpdateMenuPacket(0));},
                positionContext,
                new SideTabPanel(this.settingsPanelGroup, positionContext, 0),
                this.menu::isOpenSetting

        ));
        
        this.settingsTabButton.setTooltip(Tooltip.create(Component.translatable("screen.ironfurnaces.side_tab.setting")));
        int y = topPos + 70;
        int i1 = leftPos + 70;
        this.forwardButton = new PageButton(i1 + 38, y, true, (button) -> NetworkUtils.sendToServer(new C2SUpdateMenuPacket(3)), false);
        this.backButton = new PageButton(i1 - 8, y, false, (button) -> NetworkUtils.sendToServer(new C2SUpdateMenuPacket(4)), false);


        this.pageGroup = new WidgetGroup(forwardButton, backButton);
        this.addRenderableWidget(autoInputButton);
        this.addRenderableWidget(autoOutputButton);
        this.addRenderableWidget(redstoneModeButton);
        this.addRenderableWidget(autoFillButton);
        this.addRenderableWidget(subtractionValueIncButton);
        this.addRenderableWidget(subtractionValueDecButton);
        this.addRenderableWidget(upIoButton);
        this.addRenderableWidget(downIoButton);
        this.addRenderableWidget(southIoButton);
        this.addRenderableWidget(westIoButton);
        this.addRenderableWidget(eastIoButton);
        this.addRenderableWidget(northIoButton);
        this.addRenderableWidget(forwardButton);
        this.addRenderableWidget(backButton);


        // =========================
        // remaining
        // =========================
        this.remainingCacheTabButton = this.addRenderableWidget(new SideTab(
                0,
                0,
                MenuConstant.SIDE_BUTTON_WIDTH,
                MenuConstant.SIDE_BUTTON_HEIGHT,
                MenuConstant.SIDE_BUTTON_WIDTH * 1,     // u
                42,      // v
                0,
                WIDGET,
                btn -> {
                    NetworkUtils.sendToServer(new C2SUpdateMenuPacket(2));
                },
                positionContext,
                new SideTabPanel(this.remainingCachePanelGroup, positionContext, 1),
                this.menu::isOpenRemaining
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
                MenuConstant.SIDE_BUTTON_WIDTH * 2,     // u
                42,      // v
                0,
                WIDGET,
                btn -> {
                    NetworkUtils.sendToServer(new C2SUpdateMenuPacket(1));
                },
                positionContext,
                new SideTabPanel(this.augmentCachePanelGroup, positionContext, 2),
                this.menu::isOpenAugment
        ));

        this.augmentCacheTabButton.setTooltip(Tooltip.create(Component.translatable("screen.ironfurnaces.side_tab.augment")));

        this.augmentCachePanelGroup = new WidgetGroup();




        // 初始全部隐藏 panel
        this.settingsPanelGroup.deactivateAll();


        this.energyArea = new AbstractWidget(0, 0, 14, 42, Component.empty()) {
            @Override
            protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
                this.setTooltip(Tooltip.create(
                        Component.translatable("screen.ironfurnaces.energy_slot", menu.getEnergyStored(), menu.getMaxEnergy())
                ));
            }

            @Override
            protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

            }

        };

        this.addRenderableWidget(energyArea);

        this.factoryGroup = new WidgetGroup(energyArea, autoFillButton);
        this.generatorGroup = new WidgetGroup(energyArea);


    }
    private AbstractWidget energyArea;

    private ResourceLocation pickTexture(ResourceLocation wanted) {
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        return resourceManager.getResource(wanted).isPresent() ? wanted : DEFAULT_TEX.apply(mode);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        this.mode = menu.getMode();
        switch (this.mode){
            case FURNACE -> renderFurnaceBg(guiGraphics, partialTick, mouseX, mouseY);
            case FACTORY -> renderFactoryBg(guiGraphics, partialTick, mouseX, mouseY);
            case GENERATOR -> renderGeneratorBg(guiGraphics, partialTick, mouseX, mouseY);
        }
    }



    private void renderFurnaceBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int i = this.leftPos;
        int j = this.topPos;
        guiGraphics.blit(VANILLA, i, j, 0, 0, this.imageWidth, this.imageHeight);
        if (this.menu.isLit()) {
            int k = this.menu.getLitProgress();
            guiGraphics.blit(VANILLA, i + 56, j + 36 + 12 - k, 176, 12 - k, 14, k + 1);
        }

        int l = this.menu.getBurnProgress(0);
        guiGraphics.blit(VANILLA, i + 79, j + 34, 176, 14, l + 1, 16);
    }

    private void renderFactoryBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        ResourceLocation texture = pickTexture(getCurrentTexture());
        int i = this.leftPos;
        int j = this.topPos;
        guiGraphics.blit(texture, i, j, 0, 0, this.imageWidth, this.imageHeight);

        int columns = 3;
        int visibleRows = 3;
        int pageSize = columns * visibleRows;

        int currentPage = this.menu.factoryInput.getCurrentPage();
        int startIndex = currentPage * pageSize;
        int endIndex = Math.min(startIndex + pageSize, this.menu.factoryInput.size);

        for (int index = startIndex; index < endIndex; index++) {
            int indexInPage = index % pageSize;
            int col = indexInPage % columns;
            int row = indexInPage / columns;

            guiGraphics.blit(texture, i + 35 + col * 18, j + 16 + row * 18, 176, 55, 18, 18);
            guiGraphics.blit(texture, i + 105 + col * 18, j + 16 + row * 18, 176, 55, 18, 18);
        }

        if (this.menu.isLit()) {
            int k = this.menu.getLitProgress();
            guiGraphics.blit(texture, i + 89, j + 36 - k + 15, 176, 12 - k, 14, k + 1);
        }

        int barHeight = 42;
        int barX = i + 9;
        int barY = j + 7;

        int l = this.menu.getMaxEnergy() > 0
                ? this.menu.getEnergyStored() * barHeight / this.menu.getMaxEnergy()
                : 0;

        energyArea.setPosition(barX, barY);

        if (l > 0) {
            guiGraphics.blit(texture, barX, barY + (barHeight - l), 176, 14 + (barHeight - l), 14, l);
        }
    }

    private void renderGeneratorBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        ResourceLocation texture = pickTexture(getCurrentTexture());
        int i = this.leftPos;
        int j = this.topPos;

        guiGraphics.blit(texture, i, j, 0, 0, this.imageWidth, this.imageHeight);

        if (this.menu.isLit()) {
            int k = this.menu.getLitProgress();
            guiGraphics.blit(texture, i + 57, j + 36 - k, 176, 12 - k, 14, k + 1);
        }

        int barHeight = 42;
        int barX = i + 109;
        int barY = j + 22;

        int l = this.menu.getMaxEnergy() > 0
                ? this.menu.getEnergyStored() * barHeight / this.menu.getMaxEnergy()
                : 0;

        energyArea.setPosition(barX, barY);

        if (l > 0) {
            guiGraphics.blit(
                    texture,
                    barX,
                    barY + (barHeight - l),   // 目标 y 下移，保证从底部开始长
                    176,
                    14 + (barHeight - l),     // 纹理 v 也同步下移
                    14,
                    l
            );
        }
    }

    private ResourceLocation getCurrentTexture() {
        FurnacePattern pattern = this.menu.blockEntity.getPattern();

        return IronFurnaces.gui(mode.toString().toLowerCase(Locale.ROOT) + "/" + pattern.id().getPath());
    }



    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.positionContext.zero();
        this.mode = menu.getMode();
        switch (this.mode){
            case FURNACE -> {
                factoryGroup.deactivateAll();
                generatorGroup.deactivateAll();
            }
            case FACTORY -> {
                generatorGroup.deactivateAll();
                factoryGroup.activeAll();
            }
            case GENERATOR -> {
                factoryGroup.deactivateAll();
                generatorGroup.activeAll();
            }
        }
        if (this.menu.factoryInput.getPageCount() > 1 && this.mode.equals(FurnaceMode.FACTORY)) {
            this.pageGroup.activeAll();
        } else {
            this.pageGroup.deactivateAll();
        }
        this.menu.updateMode();
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);

    }
}
