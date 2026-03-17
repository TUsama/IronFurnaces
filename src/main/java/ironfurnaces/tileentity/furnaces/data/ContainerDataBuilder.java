package ironfurnaces.tileentity.furnaces.data;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.world.inventory.ContainerData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class ContainerDataBuilder {
    private final List<ContainerDataField> fields = new ArrayList<>();

    private ContainerDataBuilder() {}

    public static ContainerDataBuilder create() {
        return new ContainerDataBuilder();
    }

    public ContainerDataBuilder intValue(IntSupplier getter, IntConsumer setter) {
        fields.add(PrimitiveContainerDataFields.ofInt(getter, setter));
        return this;
    }

    public ContainerDataBuilder boolValue(BooleanSupplier getter, BooleanConsumer setter) {
        fields.add(PrimitiveContainerDataFields.ofBoolean(getter, setter));
        return this;
    }

    public <E extends Enum<E>> ContainerDataBuilder enumValue(
            Class<E> enumClass,
            Supplier<E> getter,
            Consumer<E> setter
    ) {
        fields.add(PrimitiveContainerDataFields.ofEnum(enumClass, getter, setter));
        return this;
    }

    public ContainerDataBuilder addField(ContainerDataField field) {
        fields.add(field);
        return this;
    }

    public ContainerDataBuilder addFields(Iterable<? extends ContainerDataField> moreFields) {
        for (ContainerDataField field : moreFields) {
            fields.add(field);
        }
        return this;
    }

    public List<ContainerDataField> fields() {
        return Collections.unmodifiableList(fields);
    }

    public ContainerData build() {
        List<ContainerDataField> snapshot = List.copyOf(fields);

        return new ContainerData() {
            @Override
            public int get(int index) {
                if (index < 0 || index >= snapshot.size()) {
                    return 0;
                }
                return snapshot.get(index).get();
            }

            @Override
            public void set(int index, int value) {
                if (index < 0 || index >= snapshot.size()) {
                    return;
                }
                snapshot.get(index).set(value);
            }

            @Override
            public int getCount() {
                return snapshot.size();
            }
        };
    }
}
