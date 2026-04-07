package ironfurnaces.mixin;

import com.google.common.collect.Lists;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import ironfurnaces.items.ItemRainbowCoal;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Optional;

@Mixin(RecipeManager.class)
@MixinEnvironment
public class FixInfiniteRainbowCoalMixin {

    @Inject(
            method = "getRemainingItemsFor",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Optional;isPresent()Z"),
            locals = LocalCapture.CAPTURE_FAILSOFT,
            cancellable = true
    )
    //? 1.20.1 {
    
    private <C extends Container, T extends Recipe<C>> void ironfurnace$fixInfiniteCoal(RecipeType<T> recipeType, C inventory, Level level, CallbackInfoReturnable<NonNullList<ItemStack>> cir, Optional optional) {
        if (optional.isPresent() && optional.get() instanceof RepairItemRecipe){
            List<ItemStack> list = Lists.newArrayList();
            int containerSize = inventory.getContainerSize();
            for(int i = 0; i < containerSize; ++i) {
                ItemStack itemstack = inventory.getItem(i);
                if (!itemstack.isEmpty() && itemstack.getItem() instanceof ItemRainbowCoal) {
                    list.add(itemstack);
                    if (list.size() > 1) {
                        cir.setReturnValue(NonNullList.withSize(containerSize, ItemStack.EMPTY));
                    }
                }
            }
        }
    }
    //? } else {
    /*private <I extends RecipeInput, T extends Recipe<I>> void ironfurnace$fixInfiniteCoal(RecipeType<T> recipeType, RecipeInput input, Level lvel, CallbackInfoReturnable<NonNullList<ItemStack>> cir, Optional<RecipeHolder<T>> optional) {
        if (optional.isPresent() && optional.get().value() instanceof RepairItemRecipe){
            List<ItemStack> list = Lists.newArrayList();
            int containerSize = input.size();
            for(int i = 0; i < containerSize; ++i) {
                ItemStack itemstack = input.getItem(i);
                if (!itemstack.isEmpty() && itemstack.getItem() instanceof ItemRainbowCoal) {
                    list.add(itemstack);
                    if (list.size() > 1) {
                        cir.setReturnValue(NonNullList.withSize(containerSize, ItemStack.EMPTY));
                    }
                }
            }
        }
    }
    *///?}


}
