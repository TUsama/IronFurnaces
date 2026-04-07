package ironfurnaces.tileentity.furnaces.pattern;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.With;
import net.minecraft.util.ExtraCodecs;

@With
public record EffectiveFurnaceStats(
        int smeltTick,
        int batchHandle,
        int energyCapacity,
        int energyGenerationPerTick,
        int energyConsumerPerTick,
        int inputSlotAmount
)  implements IFurnaceStats<EffectiveFurnaceStats>{

    public static final MapCodec<EffectiveFurnaceStats> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ExtraCodecs.POSITIVE_INT.fieldOf("smelt_tick").forGetter(EffectiveFurnaceStats::smeltTick),
            ExtraCodecs.POSITIVE_INT.fieldOf("batch_handle").forGetter(EffectiveFurnaceStats::batchHandle),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("energy_capacity").forGetter(EffectiveFurnaceStats::energyCapacity),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("energy_generation_per_tick").forGetter(EffectiveFurnaceStats::energyGenerationPerTick),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("energy_consumer_per_tick").forGetter(EffectiveFurnaceStats::energyConsumerPerTick),
            ExtraCodecs.POSITIVE_INT.fieldOf("input_slot_amount").forGetter(EffectiveFurnaceStats::inputSlotAmount)
    ).apply(inst, EffectiveFurnaceStats::new));

    public static EffectiveFurnaceStats fromBase(NormalFurnacePattern pattern) {
        return new EffectiveFurnaceStats(
                pattern.smeltTick(),
                pattern.batchHandle(),
                pattern.energyCapacity(),
                pattern.energyGenerationPerTick(),
                pattern.energyConsumerPerTick(),
                pattern.inputSlotAmount()
        );
    }

    public EffectiveFurnaceStats apply(RainbowBonus bonus) {
        return new EffectiveFurnaceStats(
                Math.max(1, smeltTick + bonus.smeltTickPerItemOffset()),
                1,
                Math.max(0, energyCapacity + bonus.energyCapacityOffset()),
                Math.max(0, energyGenerationPerTick + bonus.energyGenerationPerTickOffset()),
                Math.max(0, energyConsumerPerTick + bonus.energyConsumerPerTickOffset()),
                Math.max(1, inputSlotAmount + bonus.inputSlotAmountOffset())
        );
    }

    @Override
    public Type getType() {
        return Type.EFFECTIVE;
    }
}