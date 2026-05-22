//~ replace_INBTSerializable
//~ replace_all_recipe
package ironfurnaces.tileentity.furnaces.cache;

import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.pattern.mode.AbstractFurnaceModeHandler;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.server.level.ServerLevel;
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

    List<IRecipeType<?>> getShownRecipeTypes(AbstractFurnaceModeHandler mode);

    default Optional<? extends RecipeHolder> getRecipe(FurnacePatternBlockEntity blockEntity, List<ItemStack> stacks) {
        if (!blockEntity.hasLevel() || blockEntity.getLevel().isClientSide()) {
            return Optional.empty();
        } else {
            Optional<? extends RecipeHolder<?>> recipeFor = blockEntity.getQuickCheck().apply(blockEntity.getAugments().getCurrentRecipeType().getRecipeType()).getRecipeFor(new SingleRecipeInput(stacks.get(0)), ((ServerLevel) blockEntity.getLevel()));

            return recipeFor;
        }

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
