package ironfurnaces.tileentity.furnaces.menu.slot.compat;

import com.mojang.datafixers.util.Pair;
import ironfurnaces.tileentity.furnaces.cache.recipe_type_handlers.FarmerDelightCookingRecipeTypeHandler;
import ironfurnaces.tileentity.furnaces.menu.FurnacePatternMenu;
import ironfurnaces.tileentity.furnaces.menu.Partition;
import ironfurnaces.tileentity.furnaces.menu.slot.DynamicAccessSlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class LockedSlot extends DynamicAccessSlot {

    private Supplier<FurnacePatternMenu> menu;

    public LockedSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, Partition partition, Supplier<FurnacePatternMenu> menu) {
        super(itemHandler, index, xPosition, yPosition, partition);
        this.menu = menu;
    }

    @Nullable
    @Override
    public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
        /*if (menu.get().blockEntity.getAugments().getCurrentRecipeType() instanceof FarmerDelightCookingRecipeTypeHandler recipeTypeHandler){
            return Pair.of(InventoryMenu.BLOCK_ATLAS, recipeTypeHandler.showAvailableItem(this.getSlotIndex()));
        }*/
        return super.getNoItemIcon();
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        if (menu.get().blockEntity.getAugments().getCurrentRecipeType() instanceof FarmerDelightCookingRecipeTypeHandler recipeTypeHandler) {
            return recipeTypeHandler.canInsertToThisSlot(this.getSlotIndex(), stack, menu.get().blockEntity.getLevel()) && super.mayPlace(stack);
        }
        return false;

    }
}
