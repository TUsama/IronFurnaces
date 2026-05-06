package ironfurnaces.extensions.net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

import lombok.experimental.UtilityClass;
import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jspecify.annotations.Nullable;

@Extension
public class ItemStacksResourceHandlerUtil {

    public static ItemStack getStack(@This ItemStacksResourceHandler handler, int index) {
        return ItemUtil.getStack(handler, index);
    }

    public static ItemStack insertItemReturnRemaining(
            @This ItemStacksResourceHandler handler,
            ItemStack stack,
            boolean simulate,
            @Nullable TransactionContext transaction){
        return ItemUtil.insertItemReturnRemaining(handler, stack, simulate, transaction);
    }

    public static ItemStack insertItemReturnRemaining(
            @This ItemStacksResourceHandler handler,
            int index,
            ItemStack stack,
            boolean simulate,
            @Nullable TransactionContext transaction){
        return ItemUtil.insertItemReturnRemaining(handler, index, stack, simulate, transaction);
    }

    public static int getSlotLimit(
            @This ItemStacksResourceHandler handler,
            int index){
        return handler.getCapacityAsInt(index, handler.getResource(index));
    }

    public static void setAsEmpty(
            @This ItemStacksResourceHandler handler,
            int index){
        handler.set(index, ItemResource.EMPTY, 0);
    }
}
