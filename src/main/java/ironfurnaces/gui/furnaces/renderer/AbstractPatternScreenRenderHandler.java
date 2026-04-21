package ironfurnaces.gui.furnaces.renderer;

import com.clefal.nirvana_lib.utils.ResourceLocationUtils;
import ironfurnaces.gui.furnaces.FurnacePatternScreen;
import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.tileentity.furnaces.pattern.FurnacePattern;
import ironfurnaces.tileentity.furnaces.pattern.mode.AbstractFurnaceModeHandler;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.List;
import java.util.Locale;
import java.util.function.Function;

public abstract class AbstractPatternScreenRenderHandler {
    protected FurnacePatternScreen screen;
    protected static final ResourceLocation VANILLA = ResourceLocationUtils.make("minecraft", "textures/gui/container/furnace.png");
    protected static final Function<AbstractFurnaceModeHandler, ResourceLocation> DEFAULT_TEX = Util.memoize((mode) -> {
        if (mode.isFurnace()) return VANILLA;
        return IronFurnaces.gui(mode.getId().toLowerCase(Locale.ROOT) + "/default");
    });

    public AbstractPatternScreenRenderHandler(FurnacePatternScreen screen) {
        this.screen = screen;
    }

    private ResourceLocation getCurrentTexture() {
        FurnacePattern pattern = screen.getMenu().blockEntity.getPattern();
        return IronFurnaces.gui(screen.getMenu().getMode().getId().toLowerCase(Locale.ROOT) + "/" + pattern.id().getPath());
    }

    protected ResourceLocation pickTexture() {
        ResourceLocation currentTexture = getCurrentTexture();
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        return resourceManager.getResource(currentTexture).isPresent() ? currentTexture : DEFAULT_TEX.apply(screen.getMenu().getMode());
    }

    public List<AbstractWidget> getRenderables(){
        return List.of();
    }

    public List<AbstractWidget> getRenderableWidget(){
        return List.of();
    }

    public List<AbstractWidget> getWidget(){
        return List.of();
    }

    public boolean needRenderCustomTooltip(GuiGraphics guiGraphics, int x, int y){
        return false;
    }

    public void renderCustomTooltip(GuiGraphics guiGraphics, int x, int y){

    }

    public abstract void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY);
}
