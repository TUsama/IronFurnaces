package ironfurnaces.gui.furnaces.renderer.compat;

import com.clefal.nirvana_lib.utils.NetworkUtils;
import com.clefal.nirvana_lib.utils.ResourceLocationUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import ironfurnaces.gui.furnaces.FurnacePatternScreen;
import ironfurnaces.gui.furnaces.component.BaseImageButton;
import ironfurnaces.gui.furnaces.renderer.AbstractPatternScreenRenderHandler;
import ironfurnaces.gui.furnaces.renderer.FactoryRenderHandler;
import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.network.C2SLockedRecipePacket;
import ironfurnaces.tileentity.furnaces.cache.recipe_type_handlers.FarmerDelightCookingRecipeTypeHandler;
import ironfurnaces.tileentity.furnaces.menu.FurnacePatternMenu;
import ironfurnaces.tileentity.furnaces.menu.handler.FDMenuHandler;
import ironfurnaces.tileentity.furnaces.pattern.mode.compat.FDCompatModeHandler;
import ironfurnaces.tileentity.furnaces.pattern.mode.internal.FactoryModeHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.LockIconButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.lwjgl.opengl.GL11;
import vectorwing.farmersdelight.common.block.entity.container.CookingPotMenu;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;
import vectorwing.farmersdelight.common.utility.TextUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FDRenderHandler extends AbstractPatternScreenRenderHandler {
    private static final ResourceLocation BACKGROUND_TEXTURE = ResourceLocationUtils.make("farmersdelight", "textures/gui/cooking_pot.png");
    private static final ResourceLocation ENERGY_BAR = IronFurnaces.gui("energy_bar");
    private static final Rectangle HEAT_ICON = new Rectangle(47, 55, 17, 15);
    private static final Rectangle PROGRESS_ARROW = new Rectangle(89, 25, 0, 17);
    private final LockIconButton lockRecipeButton;
    private final AbstractWidget energyArea = FactoryRenderHandler.energyAreaGetter.apply(screen);

    public FDRenderHandler(FurnacePatternScreen screen) {
        super(screen);
        this.lockRecipeButton = new LockIconButton(0, 0, button -> {
            System.out.println("press");
            NetworkUtils.sendToServer(new C2SLockedRecipePacket(screen.getMenu().bePos));
        }){
            @Override
            public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
                super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
                if (this.isHovered() && screen.getMenu().blockEntity.getAugments().getCurrentRecipeType() instanceof FarmerDelightCookingRecipeTypeHandler handler){
                    List<Component> components = handler.buildTooltips(Minecraft.getInstance().level);
                    guiGraphics.renderTooltip(Minecraft.getInstance().font, components, Optional.empty(), mouseX, mouseY);
                }

            }
        };
    }

    @Override
    public List<AbstractWidget> getRenderableWidget() {
        return List.of(lockRecipeButton, energyArea);
    }

    public void setLockedButtonActive(boolean active){
        this.lockRecipeButton.active = active;
    }

    @Override
    public void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        if (screen.getMenu().blockEntity.getAugments().getCurrentRecipeType() instanceof FarmerDelightCookingRecipeTypeHandler handler){
            this.lockRecipeButton.setLocked(handler.isLocking);
        } else {
            this.lockRecipeButton.setLocked(false);
        }


        int guiLeft = screen.getGuiLeft();
        int guiTop = screen.getGuiTop();

        FurnacePatternMenu menu = screen.getMenu();

        guiGraphics.blit(BACKGROUND_TEXTURE, guiLeft, guiTop, 0, 0, screen.getXSize(), screen.getYSize());
        if (menu.getLitProgress() > 0) {
            guiGraphics.blit(BACKGROUND_TEXTURE, guiLeft + HEAT_ICON.x, guiTop + HEAT_ICON.y, 176, 0, HEAT_ICON.width, HEAT_ICON.height);
        }

        int l = menu.getLitProgress();
        guiGraphics.blit(BACKGROUND_TEXTURE, guiLeft + PROGRESS_ARROW.x, guiTop + PROGRESS_ARROW.y, 176, 15, l + 1, PROGRESS_ARROW.height);


        int barHeight = 42;
        int barX = guiLeft + 9;
        int barY = guiTop + 10;
        lockRecipeButton.setPosition(barX + 140, barY + 15);
        int k = menu.getMaxEnergy() > 0
                ? menu.getEnergyStored() * barHeight / menu.getMaxEnergy()
                : 0;

        energyArea.setPosition(barX, barY);
        guiGraphics.blit(ENERGY_BAR, barX, barY, 0, 0, 14, 42, 28, 42);
        if (k > 0) {
            guiGraphics.blit(ENERGY_BAR, barX, barY + (barHeight - k), 14, k, 14, (barHeight - k), 14, k, 28, 42);
        }
        if (screen.getMenu().blockEntity.getAugments().getCurrentRecipeType() instanceof FarmerDelightCookingRecipeTypeHandler handler && handler.isLocking){

            for (int i = 0; i < 6; i++) {
                ResourceLocation resourceLocation = handler.showAvailableItem(i, Minecraft.getInstance().level.getGameTime());
                if (resourceLocation != null){
                    Item value = ForgeRegistries.ITEMS.getValue(resourceLocation);
                    if (value != null){
                        int x = guiLeft + 30 + (i % 3) * 18;
                        int y = guiTop + 17 + (i / 3) * 18;

                        guiGraphics.renderItem(value.getDefaultInstance(), x, y);
                        guiGraphics.fill(x, y, x + 16, y + 16, 0x80FFFFFF);
                    }
                }
            }

        }
    }


    @Override
    public boolean needRenderCustomTooltip(GuiGraphics guiGraphics, int x, int y) {
        return true;
    }

    @Override
    public void renderCustomTooltip(GuiGraphics guiGraphics, int x, int y) {
        Slot slotUnderMouse = screen.getSlotUnderMouse();
        FurnacePatternMenu menu = screen.getMenu();
        if (menu.getCarried().isEmpty() && slotUnderMouse != null && slotUnderMouse.hasItem()) {

            if (slotUnderMouse instanceof SlotItemHandler slotItemHandler && slotItemHandler.getItemHandler() == menu.blockEntity.getViewOnly()) {
                List<Component> tooltip = new ArrayList<>();

                ItemStack mealStack = slotUnderMouse.getItem();
                tooltip.add(((MutableComponent) mealStack.getItem().getDescription()).withStyle(mealStack.getRarity().color));

                ItemStack containerStack = slotUnderMouse.getItem().getCraftingRemainingItem();
                if (!containerStack.isEmpty()) {
                    String container = !containerStack.isEmpty() ? containerStack.getItem().getDescription().getString() : "";
                    tooltip.add(TextUtils.getTranslation("container.cooking_pot.served_on", container).withStyle(ChatFormatting.GRAY));
                }

                guiGraphics.renderComponentTooltip(Minecraft.getInstance().font, tooltip, x, y);
            } else {
                guiGraphics.renderTooltip(Minecraft.getInstance().font, slotUnderMouse.getItem(), x, y);
            }
        }


    }
}
