package ironfurnaces.tileentity.furnaces.pattern;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public interface IFurnaceStats<T extends IFurnaceStats<?>> {
    Codec<IFurnaceStats> CODEC =
            Type.CODEC.dispatch(
                    "stats_type",
                    IFurnaceStats::getType,
                    //~ if 1.20.1 '.CODEC' -> '.CODEC.codec()'{
                    type -> switch (type) {
                        case EFFECTIVE -> EffectiveFurnaceStats.CODEC.codec();
                    }
                    //~ }

            );

    int smeltTick();
    int batchHandle();
    int energyCapacity();
    int energyGenerationPerTick();
    int energyConsumerPerTick();
    int inputSlotAmount();
    T withSmeltTick(int value);
    T withBatchHandle(int value);
    T withEnergyCapacity(int value);
    T withEnergyGenerationPerTick(int value);
    T withEnergyConsumerPerTick(int value);
    T withInputSlotAmount(int value);
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
