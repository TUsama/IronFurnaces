package ironfurnaces.items.augments;

import ironfurnaces.tileentity.furnaces.cache.AugmentCache;
import net.minecraft.world.item.crafting.RecipeType;

public abstract class ItemAugmentRed extends ItemAugment {


    public ItemAugmentRed(Properties properties) {
        super(properties);
    }

    public abstract AugmentCache.HandlingRecipeType getRecipeType();
    public abstract int getType();

}
