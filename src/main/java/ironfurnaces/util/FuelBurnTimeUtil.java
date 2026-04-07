package ironfurnaces.util;

import lombok.experimental.UtilityClass;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public final class FuelBurnTimeUtil {


    /**
     * Returns the burn time for the given stack in the given recipe type.
     *
     * Semantics:
     * - 0 means not a valid fuel
     * - positive value means burn time in ticks
     */
    public static int getBurnTime(ItemStack stack, @Nullable RecipeType<?> recipeType) {
        if (stack.isEmpty()) {
            return 0;
        }

        //? if <=1.20.1 {
        return net.minecraftforge.common.ForgeHooks.getBurnTime(stack, recipeType);
         //?} else {
        /*return stack.getBurnTime(recipeType);
        *///?}
    }

    public static boolean isFuel(ItemStack stack, @Nullable RecipeType<?> recipeType) {
        return getBurnTime(stack, recipeType) > 0;
    }
}