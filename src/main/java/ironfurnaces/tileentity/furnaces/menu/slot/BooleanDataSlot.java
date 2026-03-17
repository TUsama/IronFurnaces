package ironfurnaces.tileentity.furnaces.menu.slot;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.world.inventory.DataSlot;

import java.util.function.BooleanSupplier;

public class BooleanDataSlot extends DataSlot {

    private final BooleanSupplier getter;
    private final BooleanConsumer setter;

    public BooleanDataSlot(BooleanSupplier getter, BooleanConsumer setter) {
        this.getter = getter;
        this.setter = setter;
    }

    @Override
    public int get() {
        return getter.getAsBoolean() ? 1 : 0;
    }

    @Override
    public void set(int value) {
        setter.accept(value != 0);
    }
}
