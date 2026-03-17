package ironfurnaces.tileentity.furnaces.cache;

import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiPredicate;
import java.util.function.IntConsumer;

@Accessors(chain = true, fluent = true)
public class AugmentCacheHandler extends ItemStackHandler {
    @Setter
    private BiPredicate<Integer, ItemStack> validator;
    @Setter
    private IntConsumer onChange;


    public AugmentCacheHandler() {
        super(1);
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        return super.insertItem(slot, stack, simulate);
    }

    @Override
    protected void onContentsChanged(int slot) {
        if (onChange != null) onChange.accept(slot);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        if (ItemStack.isSameItemSameTags(stack, this.getStackInSlot(slot))) {
            return false;
        }

        if (validator != null && !validator.test(slot, stack)) {
            return false;
        }
        return super.isItemValid(slot, stack);
    }
}
