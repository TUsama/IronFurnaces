package ironfurnaces.tileentity.furnaces.pattern;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

import java.util.Optional;

public record FurnacePatternDefinition(
        int smeltTickPerItem,
        int energyCapacity,
        int energyGenerationPerTick,
        int energyConsumerPerTick,
        int inputSlotAmount,
        Optional<ResourceLocation> referenceBlock,
        Optional<RainbowFurnaceConfig> rainbow
) {
    public static final Codec<FurnacePatternDefinition> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ExtraCodecs.POSITIVE_INT.fieldOf("smelt_tick_per_item").forGetter(FurnacePatternDefinition::smeltTickPerItem),
            ExtraCodecs.POSITIVE_INT.fieldOf("energy_capacity").forGetter(FurnacePatternDefinition::energyCapacity),
            ExtraCodecs.POSITIVE_INT.fieldOf("energy_generation_per_tick").forGetter(FurnacePatternDefinition::energyGenerationPerTick),
            ExtraCodecs.POSITIVE_INT.fieldOf("energy_consumer_per_tick").forGetter(FurnacePatternDefinition::energyConsumerPerTick),
            ExtraCodecs.POSITIVE_INT.fieldOf("input_slot_amount").forGetter(FurnacePatternDefinition::inputSlotAmount),
            ResourceLocation.CODEC.optionalFieldOf("reference_block").forGetter(FurnacePatternDefinition::referenceBlock),
            RainbowFurnaceConfig.CODEC.optionalFieldOf("rainbow").forGetter(FurnacePatternDefinition::rainbow)
    ).apply(inst, FurnacePatternDefinition::new));

    public FurnacePattern toRuntime(ResourceLocation id) {

        if (rainbow.isPresent()){
            return new RainbowFurnacePattern(
                    id,
                    smeltTickPerItem,
                    energyCapacity,
                    energyGenerationPerTick,
                    energyConsumerPerTick,
                    inputSlotAmount,
                    referenceBlock,
                    rainbow.get()
            );
        } else {
            return new NormalFurnacePattern(
                    id,
                    smeltTickPerItem,
                    energyCapacity,
                    energyGenerationPerTick,
                    energyConsumerPerTick,
                    inputSlotAmount,
                    referenceBlock
            );
        }

    }

    public static FurnacePatternDefinition normal(
            int smeltTickPerItem,
            int energyCapacity,
            int energyGenerationPerTick,
            int energyConsumerPerTick,
            int inputSlotAmount,
            Optional<ResourceLocation> referenceBlock
    ) {
        return new FurnacePatternDefinition(
                smeltTickPerItem,
                energyCapacity,
                energyGenerationPerTick,
                energyConsumerPerTick,
                inputSlotAmount,
                referenceBlock,
                Optional.empty()
        );
    }

    public static FurnacePatternDefinition rainbow(
            int smeltTickPerItem,
            int energyCapacity,
            int energyGenerationPerTick,
            int energyConsumerPerTick,
            int inputSlotAmount,
            Optional<ResourceLocation> referenceBlock,
            RainbowFurnaceConfig config
    ) {
        return new FurnacePatternDefinition(
                smeltTickPerItem,
                energyCapacity,
                energyGenerationPerTick,
                energyConsumerPerTick,
                inputSlotAmount,
                referenceBlock,
                Optional.of(config)
        );
    }
}