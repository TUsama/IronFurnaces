package ironfurnaces.items.augments;

import ironfurnaces.tileentity.furnaces.cache.IRecipeTypeHandler;

public abstract class ItemAugmentRed extends ItemAugment {


    public ItemAugmentRed(Properties properties) {
        super(properties);
    }

    public abstract IRecipeTypeHandler getRecipeTypeHandler();
    public abstract int getType();

}
