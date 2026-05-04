
package ironfurnaces.tileentity.furnaces.pattern;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;

import java.util.Optional;
@Getter
@Accessors(fluent = true)
public final class RainbowFurnacePattern extends FurnacePattern {

    public static final MapCodec<RainbowFurnacePattern> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Identifier.CODEC.fieldOf("id").forGetter(RainbowFurnacePattern::id),
            ExtraCodecs.POSITIVE_INT.fieldOf("smelt_tick").forGetter(RainbowFurnacePattern::baseSmeltTickPerItem),
            ExtraCodecs.POSITIVE_INT.fieldOf("batch_handle").forGetter(RainbowFurnacePattern::baseBatchHandle),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("energy_capacity").forGetter(RainbowFurnacePattern::baseEnergyCapacity),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("energy_generation_per_tick").forGetter(RainbowFurnacePattern::baseEnergyGenerationPerTick),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("energy_consumer_per_tick").forGetter(RainbowFurnacePattern::baseEnergyConsumerPerTick),
            ExtraCodecs.POSITIVE_INT.fieldOf("input_slot_amount").forGetter(RainbowFurnacePattern::baseInputSlotAmount),
            Identifier.CODEC.optionalFieldOf("reference_block").forGetter(RainbowFurnacePattern::referenceBlock),
            RainbowFurnaceConfig.CODEC.fieldOf("rainbow_config").forGetter(RainbowFurnacePattern::config)
    ).apply(inst, RainbowFurnacePattern::new));

    private final Identifier id;
    private final int baseSmeltTickPerItem;
    private final int baseBatchHandle;
    private final int baseEnergyCapacity;
    private final int baseEnergyGenerationPerTick;
    private final int baseEnergyConsumerPerTick;
    private final int baseInputSlotAmount;
    private final Optional<Identifier> referenceBlock;
    private final RainbowFurnaceConfig config;

    public RainbowFurnacePattern(
            Identifier id,
            int baseSmeltTickPerItem,
            int baseBatchHandle,
            int baseEnergyCapacity,
            int baseEnergyGenerationPerTick,
            int baseEnergyConsumerPerTick,
            int baseInputSlotAmount,
            Optional<Identifier> referenceBlock,
            RainbowFurnaceConfig config
    ) {
        this.id = id;
        this.baseSmeltTickPerItem = baseSmeltTickPerItem;
        this.baseBatchHandle = baseBatchHandle;
        this.baseEnergyCapacity = baseEnergyCapacity;
        this.baseEnergyGenerationPerTick = baseEnergyGenerationPerTick;
        this.baseEnergyConsumerPerTick = baseEnergyConsumerPerTick;
        this.baseInputSlotAmount = baseInputSlotAmount;
        this.referenceBlock = referenceBlock == null ? Optional.empty() : referenceBlock;
        this.config = config;
    }

    @Override
    public Identifier id() {
        return id;
    }

    @Override
    public Optional<Identifier> referenceBlock() {
        return referenceBlock;
    }

    @Override
    public PatternKind kind() {
        return PatternKind.RAINBOW;
    }

    @Override
    public Optional<RainbowFurnaceConfig> rainbowConfig() {
        return Optional.of(config);
    }



    public RainbowFurnaceConfig config() {
        return config;
    }
}