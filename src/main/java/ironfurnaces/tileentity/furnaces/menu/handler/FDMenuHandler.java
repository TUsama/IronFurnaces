package ironfurnaces.tileentity.furnaces.menu.handler;

import com.clefal.nirvana_lib.utils.NetworkUtils;
import com.mojang.datafixers.util.Pair;
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
import lombok.Getter;
import lombok.Setter;
import net.minecraft.resources.ResourceLocation;
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
    private BooleanDataSlot shouldActiveDataSlot;
    private ContainerListener shouldActiveDataSlotListener;
    @Getter
    private boolean shouldActive = false;
    @Setter
    @NotNull
    public ItemStack container = ItemStack.EMPTY;
    public FDMenuHandler(FurnacePatternMenu menu) {
        super(menu);
        this.ingredients = new GridPartition(6, new Vector2i(30, 17), () -> getMenu().getMode().getId().equals(FDCompatModeHandler.ID), getMenu().blockEntity.getInput(), 0, 3).setCreator((itemHandler, index, xPosition, yPosition, partition) -> new LockedSlot(itemHandler, index, xPosition, yPosition, partition, this::getMenu));

        this.foodContainer = new GridPartition(1, new Vector2i(92, 55), () -> getMenu().getMode().getId().equals(FDCompatModeHandler.ID), getMenu().blockEntity.getInput(), 6, 1).setCreator((itemHandler, index, xPosition, yPosition, partition) -> new PartitionAccessSlot(itemHandler, index, xPosition, yPosition, partition){
            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                return Pair.of(InventoryMenu.BLOCK_ATLAS, CookingPotMenu.EMPTY_CONTAINER_SLOT_BOWL);
            }

        });

        this.preMeal = new TrackSlotPartition(1, new Vector2i(124, 26), () -> getMenu().getMode().getId().equals(FDCompatModeHandler.ID), getMenu().blockEntity.getViewOnly(), 0, 1).setCreator((itemHandler, index, xPosition, yPosition, partition) -> new DynamicAccessSlot(itemHandler, index, xPosition, yPosition, partition).appendMayPlaceCallback(() -> false).appendMayPickupCallback(() -> false));
        this.shouldActiveDataSlot = new BooleanDataSlot(() -> {
            if (menu.blockEntity.getAugments().getCurrentRecipeType() instanceof FarmerDelightCookingRecipeTypeHandler handler){
                return handler.isLocking;
            }
            return false;
        }, x -> {
            if (menu.blockEntity.getAugments().getCurrentRecipeType() instanceof FarmerDelightCookingRecipeTypeHandler handler){
                handler.isLocking = x;
            }
        });
        this.shouldActiveDataSlotListener = new ContainerListener() {
            @Override
            public void slotChanged(AbstractContainerMenu containerToSend, int dataSlotIndex, ItemStack stack) {

            }

            @Override
            public void dataChanged(AbstractContainerMenu containerMenu, int dataSlotIndex, int value) {
                var menu = (FurnacePatternMenu) containerMenu;
                if (dataSlotIndex == menu.getDataSlotIndex(shouldActiveDataSlot) && menu.blockEntity.getAugments().getCurrentRecipeType() instanceof FarmerDelightCookingRecipeTypeHandler handler){
                    menu.blockEntity.syncToViewer(new S2CSyncFDDataPacket(handler.getLockedRecipe().getId()));

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
        return List.of(shouldActiveDataSlot);
    }

    @Override
    public List<ContainerListener> getDataContainerListener() {
        return List.of();
    }

    @Override
    public QuickMoveRuleBuilder buildRule(QuickMoveRuleBuilder builder) {
        return builder;
    }
}
