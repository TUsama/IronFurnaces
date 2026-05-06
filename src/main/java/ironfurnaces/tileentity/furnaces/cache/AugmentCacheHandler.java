package ironfurnaces.tileentity.furnaces.cache;

import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemUtil;

import java.util.function.BiPredicate;
import java.util.function.IntConsumer;

@Accessors(chain = true, fluent = true)
public class AugmentCacheHandler extends ItemStacksResourceHandler {
    @Setter
    private BiPredicate<Integer, ItemStack> validator;
    @Setter
    private IntConsumer onChange;


    public AugmentCacheHandler() {
        super(1);
    }

    @Override
    protected void onContentsChanged(int index, ItemStack previousContents) {
        if (onChange != null) onChange.accept(index);
    }

    @Override
    public boolean isValid(int index, ItemResource resource) {
        var stack = ItemUtil.getStack(this, index);
        if (resource.matches(stack)) {
            return false;
        }

        if (validator != null && !validator.test(index, stack)) {
            return false;
        }

        return super.isValid(index, resource);
    }

}
