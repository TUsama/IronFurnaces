package ironfurnaces.tileentity.furnaces.menu.slot;

import net.minecraft.world.inventory.DataSlot;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class EnumDataSlot<E extends Enum<E>> extends DataSlot {

    private final Supplier<E> getter;
    private final Consumer<E> setter;
    private final E[] values;

    public EnumDataSlot(Class<E> enumClass, Supplier<E> getter, Consumer<E> setter) {
        this.getter = getter;
        this.setter = setter;
        this.values = enumClass.getEnumConstants();
    }

    @Override
    public int get() {
        return getter.get().ordinal();
    }

    @Override
    public void set(int value) {
        if (value >= 0 && value < values.length) {
            setter.accept(values[value]);
        }
    }

}
