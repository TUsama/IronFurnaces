package ironfurnaces.items.augments;

import ironfurnaces.tileentity.furnaces.cache.AugmentCache;

import java.util.function.IntUnaryOperator;

public abstract class ItemAugmentGreen extends ItemAugment {


    public ItemAugmentGreen(Properties properties) {
        super(properties);
    }
    public abstract AugmentCache.GreenAugmentModifier getModifier();

}
