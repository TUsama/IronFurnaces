package ironfurnaces.items.augments;

import net.minecraft.world.item.crafting.RecipeType;

public abstract class ItemAugmentRed extends ItemAugment {


    public ItemAugmentRed(Properties properties) {
        super(properties);
    }

    public abstract RecipeType<?> getRecipeType();

}
