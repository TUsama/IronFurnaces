package ironfurnaces.items;


import ironfurnaces.registration.ModDataComponents;
import ironfurnaces.registration.data_component.HeaterItemInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ItemHeater extends Item {


    public ItemHeater(Properties properties) {
        super(properties);
    }

    @Nullable
    public static BlockPos getBoundBlockPos(ItemStack stack) {
        return stack.get(ModDataComponents.HEATER_ITEM_INFO.get()).getPosOrNull();

    }

    public static void writeBoundBlockPos(ItemStack stack, BlockPos pos) {
        stack.set(ModDataComponents.HEATER_ITEM_INFO.get(), new HeaterItemInfo(pos));
    }


}
