package ironfurnaces.tileentity.furnaces.pattern.mode.internal;

import ironfurnaces.tileentity.furnaces.pattern.mode.AbstractFurnaceModeHandler;

public sealed abstract class InternalFurnaceModeHandler extends AbstractFurnaceModeHandler permits FactoryModeHandler, GeneratorModeHandler, VanillaFurnaceModeHandler {
    @Override
    public final boolean isInternal() {
        return true;
    }

    @Override
    public boolean isFurnace() {
        return false;
    }

    @Override
    public boolean isFactory() {
        return false;
    }

    @Override
    public boolean isGenerator() {
        return false;
    }
}
