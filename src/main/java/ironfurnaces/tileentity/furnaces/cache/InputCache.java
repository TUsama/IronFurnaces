package ironfurnaces.tileentity.furnaces.cache;

import ironfurnaces.tileentity.furnaces.FurnaceMode;
import ironfurnaces.tileentity.furnaces.process.Generate;
import ironfurnaces.tileentity.furnaces.process.ProcessingInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.lang.module.InvalidModuleDescriptorException;
import java.util.function.Predicate;

public class InputCache extends TieredCache {
    @Nullable
    private Predicate<InputCache> itemValidationCallBack;
    private NonNullList<SlotState> slotState;

    public InputCache(int size, FurnaceMode mode, ForgeConfigSpec.IntValue tier, @org.jetbrains.annotations.Nullable Predicate<InputCache> itemValidationCallBack) {
        super(size, mode, tier);
        this.itemValidationCallBack = itemValidationCallBack;
        this.slotState = NonNullList.withSize(size, SlotState.IDLE);

    }

    public InputCache(int size, FurnaceMode mode, ForgeConfigSpec.IntValue tier) {
        this(size, mode, tier, null);
    }

    @Override
    public int getSlots() {
        int slots = super.getSlots();
        for (int i = 0; i < slots; i++) {
            if (slotState.get(i).equals(SlotState.UNAVAILABLE)){
                slotState.set(i, SlotState.IDLE);
            }
        }
        return slots;
    }

    public void dropStacksInUnavailableSlots(Level level, BlockPos pos){
        for (int i = stacks.size(); i > getSlots(); i--) {
            ItemStack stackInSlot = getStackInSlot(i);
            if (!stackInSlot.isEmpty()){
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stackInSlot);
                slotState.set(i, SlotState.UNAVAILABLE);
            }
        }
    }

    public boolean isSlotIdle(int index){
        return slotState.get(index).equals(SlotState.IDLE);
    }


    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        if (itemValidationCallBack != null) return itemValidationCallBack.test(this);
        return super.isItemValid(slot, stack);
    }

    @Override
    public void updateFurnaceMode(FurnaceMode mode) {
        super.updateFurnaceMode(mode);
    }

    private enum SlotState {
        WORKING,
        UNAVAILABLE,
        IDLE;
    }

}
