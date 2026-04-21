package ironfurnaces.items.augments;

import ironfurnaces.tileentity.furnaces.pattern.mode.AbstractFurnaceModeHandler;

public abstract class ItemAugmentBlue extends ItemAugment {


    public ItemAugmentBlue(Properties properties) {
        super(properties);
    }

    public abstract AbstractFurnaceModeHandler getModeHandler();


}
