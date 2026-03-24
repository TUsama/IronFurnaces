package ironfurnaces.tileentity.furnaces.pattern;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.With;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

@With
public record EffectiveFurnaceStats(
        int smeltTickPerItem,
        int energyCapacity,
        int energyGenerationPerTick,
        int energyConsumerPerTick,
        int inputSlotAmount
)  implements IFurnaceStats{

    public static final Codec<EffectiveFurnaceStats> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ExtraCodecs.POSITIVE_INT.fieldOf("smelt_tick_per_item").forGetter(EffectiveFurnaceStats::smeltTickPerItem),
            ExtraCodecs.POSITIVE_INT.fieldOf("energy_capacity").forGetter(EffectiveFurnaceStats::energyCapacity),
            ExtraCodecs.POSITIVE_INT.fieldOf("energy_generation_per_tick").forGetter(EffectiveFurnaceStats::energyGenerationPerTick),
            ExtraCodecs.POSITIVE_INT.fieldOf("energy_consumer_per_tick").forGetter(EffectiveFurnaceStats::energyConsumerPerTick),
            ExtraCodecs.POSITIVE_INT.fieldOf("input_slot_amount").forGetter(EffectiveFurnaceStats::inputSlotAmount)
    ).apply(inst, EffectiveFurnaceStats::new));

    public static EffectiveFurnaceStats fromBase(NormalFurnacePattern pattern) {
        return new EffectiveFurnaceStats(
                pattern.smeltTickPerItem(),
                pattern.energyCapacity(),
                pattern.energyGenerationPerTick(),
                pattern.energyConsumerPerTick(),
                pattern.inputSlotAmount()
        );
    }

    public EffectiveFurnaceStats apply(RainbowBonus bonus) {
        return new EffectiveFurnaceStats(
                Math.max(1, smeltTickPerItem + bonus.smeltTickPerItemOffset()),
                Math.max(1, energyCapacity + bonus.energyCapacityOffset()),
                Math.max(1, energyGenerationPerTick + bonus.energyGenerationPerTickOffset()),
                Math.max(1, energyConsumerPerTick + bonus.energyConsumerPerTickOffset()),
                Math.max(1, inputSlotAmount + bonus.inputSlotAmountOffset())
        );
    }

    @Override
    public Type getType() {
        return Type.EFFECTIVE;
    }
}