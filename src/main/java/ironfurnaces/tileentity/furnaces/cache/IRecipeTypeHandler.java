//~ replace_INBTSerializable
//~ replace_all_recipe
package ironfurnaces.tileentity.furnaces.cache;

import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.pattern.mode.AbstractFurnaceModeHandler;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;

import java.util.List;
import java.util.Optional;

public interface IRecipeTypeHandler extends ValueIOSerializable {
    RecipeType<?> getRecipeType();

    StringBuilder buildTextureName(StringBuilder stringBuilder);

    boolean testBurnable(ItemStack stack, FurnacePatternBlockEntity blockEntity);


    void provideInstance(FurnacePatternBlockEntity blockEntity);

    List<mezz.jei.api.recipe.RecipeType<?>> getShownRecipeTypes(AbstractFurnaceModeHandler mode);

    default Optional<? extends RecipeHolder> getRecipe(FurnacePatternBlockEntity blockEntity, List<ItemStack> stacks) {
        //~ if >1.20.1 'SimpleContainer(stacks.toArray(new ItemStack[0]))' -> 'SingleRecipeInput(stacks.get(0))'
        return blockEntity.getQuickCheck().apply(blockEntity.getAugments().getCurrentRecipeType().getRecipeType()).getRecipeFor(new SingleRecipeInput(stacks.get(0)), blockEntity.getLevel());
    }

    default boolean allowPlaceItem(FurnacePatternBlockEntity blockEntity, List<ItemStack> stacks) {
        return getRecipe(blockEntity, stacks).isPresent();
    }

    @Override
    default void serialize(ValueOutput output) {

    }

    @Override
    default void deserialize(ValueInput input) {

    }
}
