package ironfurnaces.util;

import lombok.experimental.UtilityClass;

import ironfurnaces.config.FurnaceConfig;
import lombok.experimental.UtilityClass;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.IntConsumer;

@UtilityClass
public class FoodUtil {

    /**
     * 26.1.2 食物属性已经走 DataComponent。
     *
     * @return 不是食物时返回 null
     */
    @Nullable
    public FoodProperties getFoodProperties(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return null;
        }

        return stack.get(DataComponents.FOOD);
    }

    /**
     * 只判断这个 ItemStack 是否带有 FOOD 组件。
     * 注意：nutrition 为 0 的物品，只要有 FOOD 组件，仍然会被视为 food。
     */
    public boolean isFood(ItemStack stack) {
        return getFoodProperties(stack) != null;
    }

    public boolean isBurnableFood(ItemStack stack) {
        FoodProperties foodProperties = getFoodProperties(stack);
        return foodProperties != null && foodProperties.nutrition() > 0;
    }

    public boolean whenIsBurnableFood(ItemStack stack, IntConsumer energy) {
        if (isBurnableFood(stack)){
            energy.accept(getEnergyFromFood(stack));
        }
    }

    /**
     * 单个物品可产出的能量。
     */
    public int getEnergyFromFood(ItemStack stack) {
        FoodProperties foodProperties = getFoodProperties(stack);
        return getEnergyFromFood(foodProperties);
    }

    /**
     * 根据 FoodProperties 计算单个物品可产出的能量。
     */
    public int getEnergyFromFood(@Nullable FoodProperties foodProperties) {
        if (foodProperties == null) {
            return 0;
        }

        int nutrition = foodProperties.nutrition();
        if (nutrition <= 0) {
            return 0;
        }

        int factor = FurnaceConfig.config.nutrition_to_energy_factor;
        if (factor <= 0) {
            return 0;
        }

        long result = (long) nutrition * factor;
        return result > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) result;
    }

    /**
     * 整个 ItemStack 可产出的总能量。
     */
    public int getEnergyFromFoodStack(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return 0;
        }

        int energyPerItem = getEnergyFromFood(stack);
        if (energyPerItem <= 0) {
            return 0;
        }

        long result = (long) energyPerItem * stack.getCount();
        return result > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) result;
    }
}
