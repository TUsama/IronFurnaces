//~ replace_rl
package ironfurnaces.tileentity.furnaces.pattern;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

import java.util.Optional;
import java.util.OptionalInt;

public record FurnacePatternDefinition(
        int smeltTick,
        Optional<Integer> batchHandle,
        int energyCapacity,
        int energyGenerationPerTick,
        int energyConsumerPerTick,
        int inputSlotAmount,
        Optional<ResourceLocation> referenceBlock,
        Optional<RainbowFurnaceConfig> rainbow
) {
    public static final Codec<FurnacePatternDefinition> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ExtraCodecs.POSITIVE_INT.fieldOf("smelt_tick").forGetter(FurnacePatternDefinition::smeltTick),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("batch_handle").forGetter(FurnacePatternDefinition::batchHandle),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("energy_capacity").forGetter(FurnacePatternDefinition::energyCapacity),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("energy_generation_per_tick").forGetter(FurnacePatternDefinition::energyGenerationPerTick),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("energy_consumer_per_tick").forGetter(FurnacePatternDefinition::energyConsumerPerTick),
            ExtraCodecs.POSITIVE_INT.fieldOf("factory_input_slot_amount").forGetter(FurnacePatternDefinition::inputSlotAmount),
            ResourceLocation.CODEC.optionalFieldOf("reference_block").forGetter(FurnacePatternDefinition::referenceBlock),
            RainbowFurnaceConfig.CODEC.optionalFieldOf("rainbow").forGetter(FurnacePatternDefinition::rainbow)
    ).apply(inst, FurnacePatternDefinition::new));

    public FurnacePattern toRuntime(ResourceLocation id) {

        if (rainbow.isPresent()){
            return new RainbowFurnacePattern(
                    id,
                    smeltTick,
                    batchHandle.orElse(1),
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
                    smeltTick,
                    batchHandle.orElse(1),
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
                Optional.of(1),
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
                Optional.of(1),
                energyCapacity,
                energyGenerationPerTick,
                energyConsumerPerTick,
                inputSlotAmount,
                referenceBlock,
                Optional.of(config)
        );
    }

    public static FurnacePatternDefinition normal(
            int smeltTickPerItem,
            int batch,
            int energyCapacity,
            int energyGenerationPerTick,
            int energyConsumerPerTick,
            int inputSlotAmount,
            Optional<ResourceLocation> referenceBlock
    ) {
        return new FurnacePatternDefinition(
                smeltTickPerItem,
                Optional.of(batch),
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
            int batch,
            int energyCapacity,
            int energyGenerationPerTick,
            int energyConsumerPerTick,
            int inputSlotAmount,
            Optional<ResourceLocation> referenceBlock,
            RainbowFurnaceConfig config
    ) {
        return new FurnacePatternDefinition(
                smeltTickPerItem,
                Optional.of(batch),
                energyCapacity,
                energyGenerationPerTick,
                energyConsumerPerTick,
                inputSlotAmount,
                referenceBlock,
                Optional.of(config)
        );
    }
}