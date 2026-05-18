package ironfurnaces.util;

import lombok.experimental.UtilityClass;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.FuelValues;
import net.neoforged.neoforge.registries.datamaps.builtin.FurnaceFuel;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

@UtilityClass
public final class FuelBurnTimeUtil {


    public static int getBurnTime(ItemStack stack, @Nullable RecipeType<?> recipeType, Level level) {
        if (stack.isEmpty()) {
            return 0;
        }
        FuelValues fuelValues = level.fuelValues();
        return stack.getBurnTime(recipeType, fuelValues);

    }

}