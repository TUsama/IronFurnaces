package ironfurnaces.tileentity.furnaces.pattern;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record RainbowBonus(
        int smeltTickPerItemOffset,
        int batchHandleOffset,
        int energyCapacityOffset,
        int energyGenerationPerTickOffset,
        int energyConsumerPerTickOffset,
        int inputSlotAmountOffset
) {
    public static final RainbowBonus ZERO = new RainbowBonus(0, 0, 0, 0, 0, 0);

    public static final Codec<RainbowBonus> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.INT.optionalFieldOf("smelt_tick_offset", 0).forGetter(RainbowBonus::smeltTickPerItemOffset),
            Codec.INT.optionalFieldOf("batch_handle_offset", 0).forGetter(RainbowBonus::batchHandleOffset),
            Codec.INT.optionalFieldOf("energy_capacity_offset", 0).forGetter(RainbowBonus::energyCapacityOffset),
            Codec.INT.optionalFieldOf("energy_generation_per_tick_offset", 0).forGetter(RainbowBonus::energyGenerationPerTickOffset),
            Codec.INT.optionalFieldOf("energy_consumer_per_tick_offset", 0).forGetter(RainbowBonus::energyConsumerPerTickOffset),
            Codec.INT.optionalFieldOf("input_slot_amount_offset", 0).forGetter(RainbowBonus::inputSlotAmountOffset)
    ).apply(inst, RainbowBonus::new));

    public RainbowBonus add(RainbowBonus other) {
        return new RainbowBonus(
                this.smeltTickPerItemOffset + other.smeltTickPerItemOffset,
                this.batchHandleOffset + other.batchHandleOffset,
                this.energyCapacityOffset + other.energyCapacityOffset,
                this.energyGenerationPerTickOffset + other.energyGenerationPerTickOffset,
                this.energyConsumerPerTickOffset + other.energyConsumerPerTickOffset,
                this.inputSlotAmountOffset + other.inputSlotAmountOffset
        );
    }
}