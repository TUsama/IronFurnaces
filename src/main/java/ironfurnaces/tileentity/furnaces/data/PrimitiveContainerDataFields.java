package ironfurnaces.tileentity.furnaces.data;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;

import java.util.function.BooleanSupplier;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

public final class PrimitiveContainerDataFields {
    private PrimitiveContainerDataFields() {}

    public static ContainerDataField ofInt(IntSupplier getter, IntConsumer setter) {
        return new ContainerDataField() {
            @Override
            public int get() {
                return getter.getAsInt();
            }

            @Override
            public void set(int value) {
                setter.accept(value);
            }
        };
    }

    public static ContainerDataField ofBoolean(BooleanSupplier getter, BooleanConsumer setter) {
        return new ContainerDataField() {
            @Override
            public int get() {
                return getter.getAsBoolean() ? 1 : 0;
            }

            @Override
            public void set(int value) {
                setter.accept(value != 0);
            }
        };
    }

    public static <E extends Enum<E>> ContainerDataField ofEnum(
            Class<E> enumClass,
            java.util.function.Supplier<E> getter,
            java.util.function.Consumer<E> setter
    ) {
        E[] values = enumClass.getEnumConstants();
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("Enum class has no constants: " + enumClass.getName());
        }

        return new ContainerDataField() {
            @Override
            public int get() {
                E value = getter.get();
                return value == null ? 0 : value.ordinal();
            }

            @Override
            public void set(int value) {
                if (value < 0 || value >= values.length) {
                    return;
                }
                setter.accept(values[value]);
            }
        };
    }
}