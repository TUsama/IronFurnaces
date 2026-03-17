package ironfurnaces.tileentity.furnaces.menu.slot;

import net.minecraft.world.inventory.DataSlot;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

public class IntDataSlot extends DataSlot {

    private final IntSupplier getter;
    private final IntConsumer setter;

    public IntDataSlot(IntSupplier getter, IntConsumer setter) {
        this.getter = getter;
        this.setter = setter;
    }

    @Override
    public int get() {
        return getter.getAsInt();
    }

    @Override
    public void set(int value) {
        setter.accept(value);
    }
}
