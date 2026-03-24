package ironfurnaces.tileentity.furnaces.pattern;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public interface IFurnaceStats {
    Codec<IFurnaceStats> CODEC =
            Type.CODEC.dispatch(
                    "stats_type",
                    IFurnaceStats::getType,
                    type -> switch (type) {
                        case EFFECTIVE -> EffectiveFurnaceStats.CODEC;
                    }
            );

    int smeltTickPerItem();
    int energyCapacity();
    int energyGenerationPerTick();
    int energyConsumerPerTick();
    int inputSlotAmount();
    Type getType();

    enum Type implements StringRepresentable {
        EFFECTIVE("effective_stats");

        public static final EnumCodec<Type> CODEC =
                StringRepresentable.fromEnum(Type::values);

        String name;

        Type(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
