package ironfurnaces.tileentity.furnaces.pattern;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

import java.util.Optional;
@Getter
@Accessors(fluent = true)
public final class NormalFurnacePattern extends FurnacePattern {

    public static final Codec<NormalFurnacePattern> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(NormalFurnacePattern::id),
            ExtraCodecs.POSITIVE_INT.fieldOf("smelt_tick_per_item").forGetter(NormalFurnacePattern::smeltTickPerItem),
            ExtraCodecs.POSITIVE_INT.fieldOf("energy_capacity").forGetter(NormalFurnacePattern::energyCapacity),
            ExtraCodecs.POSITIVE_INT.fieldOf("energy_generation_per_tick").forGetter(NormalFurnacePattern::energyGenerationPerTick),
            ExtraCodecs.POSITIVE_INT.fieldOf("energy_consumer_per_tick").forGetter(NormalFurnacePattern::energyConsumerPerTick),
            ExtraCodecs.POSITIVE_INT.fieldOf("input_slot_amount").forGetter(NormalFurnacePattern::inputSlotAmount),
            ResourceLocation.CODEC.optionalFieldOf("reference_block").forGetter(NormalFurnacePattern::referenceBlock)
    ).apply(inst, NormalFurnacePattern::new));

    private final ResourceLocation id;
    private final int smeltTickPerItem;
    private final int energyCapacity;
    private final int energyGenerationPerTick;
    private final int energyConsumerPerTick;
    private final int inputSlotAmount;
    private final Optional<ResourceLocation> referenceBlock;

    public NormalFurnacePattern(
            ResourceLocation id,
            int smeltTickPerItem,
            int energyCapacity,
            int energyGenerationPerTick,
            int energyConsumerPerTick,
            int inputSlotAmount,
            Optional<ResourceLocation> referenceBlock
    ) {
        this.id = id;
        this.smeltTickPerItem = smeltTickPerItem;
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
