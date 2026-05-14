//? fd{
/*package ironfurnaces.tileentity.furnaces.menu.handler;

import com.mojang.datafixers.util.Pair;
import ironfurnaces.gui.furnaces.FurnacePatternScreen;
import ironfurnaces.gui.furnaces.renderer.compat.FDRenderHandler;
import ironfurnaces.network.S2CSyncFDDataPacket;
import ironfurnaces.tileentity.furnaces.cache.recipe_type_handlers.FarmerDelightCookingRecipeTypeHandler;
import ironfurnaces.tileentity.furnaces.menu.FurnacePatternMenu;
import ironfurnaces.tileentity.furnaces.menu.partition.GridPartition;
import ironfurnaces.tileentity.furnaces.menu.Partition;
import ironfurnaces.tileentity.furnaces.menu.QuickMoveRuleBuilder;
import ironfurnaces.tileentity.furnaces.menu.partition.TrackSlotPartition;
import ironfurnaces.tileentity.furnaces.menu.slot.BooleanDataSlot;
import ironfurnaces.tileentity.furnaces.menu.slot.DynamicAccessSlot;
import ironfurnaces.tileentity.furnaces.menu.slot.PartitionAccessSlot;
import ironfurnaces.tileentity.furnaces.menu.slot.compat.LockedSlot;
import ironfurnaces.tileentity.furnaces.pattern.mode.compat.FDCompatModeHandler;
import ironfurnaces.tileentity.furnaces.process.compat.Cooking;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import vectorwing.farmersdelight.common.block.entity.container.CookingPotMenu;

import java.util.List;

public class FDMenuHandler extends AbstractCompatMenuHandler{
    private Partition ingredients;
    private Partition foodContainer;
    private Partition meal;
    private TrackSlotPartition preMeal;
    private BooleanDataSlot lockedRecipeChangeDataSlot;
    private BooleanDataSlot lastRecipeChangeDataSlot;
    private ContainerListener dataSlotListener;
    @Getter
    private boolean shouldActive = false;
    @Setter
    @NotNull
    public ItemStack container = ItemStack.EMPTY;
    public FDMenuHandler(FurnacePatternMenu menu) {
        super(menu);
        this.ingredients = new GridPartition(6, new Vector2i(30, 17), () -> getMenu().getMode().getId().equals(FDCompatModeHandler.ID), getMenu().blockEntity.getInput(), 0, 3).setCreator((itemHandler, index, xPosition, yPosition, partition) -> new LockedSlot(itemHandler, index, xPosition, yPosition, partition, this::getMenu));

        this.foodContainer = new GridPartition(1, new Vector2i(92, 55), () -> getMenu().getMode().getId().equals(FDCompatModeHandler.ID), getMenu().blockEntity.getInput(), 6, 1).setCreator((itemHandler, index, xPosition, yPosition, partition) -> new PartitionAccessSlot(itemHandler, index, xPosition, yPosition, partition){
            public Pair<Identifier, Identifier> getNoItemIcon() {
                return Pair.of(InventoryMenu.BLOCK_ATLAS, CookingPotMenu.EMPTY_CONTAINER_SLOT_BOWL);
            }

        });

        this.preMeal = new TrackSlotPartition(1, new Vector2i(124, 26), () -> getMenu().getMode().getId().equals(FDCompatModeHandler.ID), getMenu().blockEntity.getViewOnly(), 0, 1).setCreator((itemHandler, index, xPosition, yPosition, partition) -> new DynamicAccessSlot(itemHandler, index, xPosition, yPosition, partition).appendMayPlaceCallback(() -> false).appendMayPickupCallback(() -> false));

        this.lockedRecipeChangeDataSlot = new BooleanDataSlot(() -> {
            if (menu.blockEntity.getAugments().getCurrentRecipeType() instanceof FarmerDelightCookingRecipeTypeHandler handler){
                return handler.getLockedRecipe() == null;
            }
            return false;
        }, x -> {

        });

        this.lastRecipeChangeDataSlot = new BooleanDataSlot(() -> {
            if (menu.blockEntity.getAugments().getCurrentRecipeType() instanceof FarmerDelightCookingRecipeTypeHandler handler){
                return handler.getLastRecipe() == null;
            }
            return false;
        }, x -> {

        });

        this.dataSlotListener = new ContainerListener() {
            @Override
            public void slotChanged(AbstractContainerMenu containerToSend, int dataSlotIndex, ItemStack stack) {

            }

            @Override
            public void dataChanged(AbstractContainerMenu containerMenu, int dataSlotIndex, int value) {
                if (containerMenu instanceof FurnacePatternMenu menu){
                    if ((dataSlotIndex == menu.getDataSlotIndex(lockedRecipeChangeDataSlot) || dataSlotIndex == menu.getDataSlotIndex(lastRecipeChangeDataSlot)) && menu.blockEntity.getAugments().getCurrentRecipeType() instanceof FarmerDelightCookingRecipeTypeHandler handler){
                        //~ if >1.20.1 '.getId()' -> '.id()' {
                        Identifier locked = handler.getLockedRecipe() != null ? handler.getLockedRecipe().id() : null;
                        Identifier last = handler.getLastRecipe() != null ? handler.getLastRecipe().id() : null;
                        //~}
                        if (menu.blockEntity.hasLevel() && !menu.blockEntity.getLevel().isClientSide && menu.player.containerMenu instanceof FurnacePatternMenu) {
                            menu.blockEntity.syncToViewer(new S2CSyncFDDataPacket(locked, last));
                        }
                    }
                }

            }
        };
        this.meal = new GridPartition(1, new Vector2i(124, 55), () -> getMenu().getMode().getId().equals(FDCompatModeHandler.ID), getMenu().blockEntity.getOutput(), 0, 1);


    }

    @Override
    public List<Partition> getPartitions() {
        return List.of(ingredients, foodContainer, meal, preMeal);
    }

    @Override
    public List<Partition> getInput() {
        return List.of(ingredients, foodContainer);
    }


    public List<Partition> getOutput() {
        return List.of(meal, preMeal);
    }

    @Override
    public List<DataSlot> getDataSlots() {
        return List.of(lockedRecipeChangeDataSlot, lastRecipeChangeDataSlot);
    }

    @Override
    public List<ContainerListener> getDataContainerListener() {
        return List.of(dataSlotListener);
    }

    @Override
    public QuickMoveRuleBuilder buildRule(QuickMoveRuleBuilder builder) {
        builder
                .rule()
                .when(x -> !getMenu().blockEntity.getViewOnly().getStackInSlot(Cooking.PREMEAL).isEmpty() && getMenu().blockEntity.getInput().isItemValid(Cooking.CONTAINER, x.stack()) && getMenu().blockEntity.getInput().insertItem(Cooking.CONTAINER, x.stack(), true).isEmpty())
                .bidirectional(getMenu().getPlayerInv(), foodContainer);
        return builder;
    }
}

*///?}