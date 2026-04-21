package ironfurnaces.tileentity.furnaces.cache;

import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.pattern.mode.AbstractFurnaceModeHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public interface IRecipeTypeHandler extends INBTSerializable<CompoundTag> {
    RecipeType<?> getRecipeType();
    StringBuilder buildTextureName(StringBuilder stringBuilder);
    boolean testBurnable(ItemStack stack, FurnacePatternBlockEntity blockEntity);


    void provideInstance(FurnacePatternBlockEntity blockEntity);
    List<mezz.jei.api.recipe.RecipeType<?>> getShownRecipeTypes(AbstractFurnaceModeHandler mode);
    default Optional<? extends Recipe> getRecipe(FurnacePatternBlockEntity blockEntity, Function<RecipeType<?>, RecipeManager.CachedCheck<Container, ?>> quickCheck, List<ItemStack> stacks){
        //~ if >1.20.1 'SimpleContainer' -> 'SingleRecipeInput'
        return quickCheck.apply(blockEntity.getAugments().getCurrentRecipeType().getRecipeType()).getRecipeFor(new SimpleContainer(stacks.toArray(new ItemStack[0])), blockEntity.getLevel());
    }

    default boolean allowPlaceItem(FurnacePatternBlockEntity blockEntity, Function<RecipeType<?>, RecipeManager.CachedCheck<Container, ?>> quickCheck, List<ItemStack> stacks){
        return getRecipe(blockEntity, quickCheck, stacks).isPresent();
    }

    @Override
    default CompoundTag serializeNBT(){
        return new CompoundTag();
    }

    @Override
    default void deserializeNBT(CompoundTag nbt){

    }


}
