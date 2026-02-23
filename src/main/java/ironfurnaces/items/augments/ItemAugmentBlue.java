package ironfurnaces.items.augments;

import ironfurnaces.tileentity.furnaces.FurnaceMode;

public abstract class ItemAugmentBlue extends ItemAugment {


    public ItemAugmentBlue(Properties properties) {
        super(properties);
    }

    public abstract FurnaceMode getMode();


}
