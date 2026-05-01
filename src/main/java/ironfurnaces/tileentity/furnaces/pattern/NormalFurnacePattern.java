//~ replace_rl
package ironfurnaces.tileentity.furnaces.pattern;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

import java.util.Optional;
import java.util.OptionalInt;

@Getter
@Accessors(fluent = true)
public final class NormalFurnacePattern extends FurnacePattern {

    public static final MapCodec<NormalFurnacePattern> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(NormalFurnacePattern::id),
            ExtraCodecs.POSITIVE_INT.fieldOf("smelt_tick").forGetter(NormalFurnacePattern::smeltTick),
            ExtraCodecs.POSITIVE_INT.fieldOf("batch_handle").forGetter(NormalFurnacePattern::batchHandle),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("energy_capacity").forGetter(NormalFurnacePattern::energyCapacity),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("energy_generation_per_tick").forGetter(NormalFurnacePattern::energyGenerationPerTick),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("energy_consumer_per_tick").forGetter(NormalFurnacePattern::energyConsumerPerTick),
            ExtraCodecs.POSITIVE_INT.fieldOf("input_slot_amount").forGetter(NormalFurnacePattern::inputSlotAmount),
            ResourceLocation.CODEC.optionalFieldOf("reference_block").forGetter(NormalFurnacePattern::referenceBlock)
    ).apply(inst, NormalFurnacePattern::new));

    private final ResourceLocation id;
    private final int smeltTick;
    private final int batchHandle;
    private final int energyCapacity;
    private final int energyGenerationPerTick;
    private final int energyConsumerPerTick;
    private final int inputSlotAmount;
    private final Optional<ResourceLocation> referenceBlock;

    public NormalFurnacePattern(
            ResourceLocation id,
            int smeltTick,
            int batchHandle,
            int energyCapacity,
            int energyGenerationPerTick,
            int energyConsumerPerTick,
            int inputSlotAmount,
            Optional<ResourceLocation> referenceBlock
    ) {
        this.id = id;
        this.smeltTick = smeltTick;
        this.batchHandle = batchHandle;
        this.energyCapacity = energyCapacity;
        this.energyGenerationPerTick = energyGenerationPerTick;
        this.energyConsumerPerTick = energyConsumerPerTick;
        this.inputSlotAmount = inputSlotAmount;
        this.referenceBlock = referenceBlock == null ? Optional.empty() : referenceBlock;
    }

    @Override
    public ResourceLocation id() {
        return id;
    }

    @Override
    public Optional<ResourceLocation> referenceBlock() {
        return referenceBlock;
    }

    @Override
    public PatternKind kind() {
        return PatternKind.NORMAL;
    }

    public EffectiveFurnaceStats toEffectiveFurnaceStats(){
        return EffectiveFurnaceStats.fromBase(this);
    }
}
