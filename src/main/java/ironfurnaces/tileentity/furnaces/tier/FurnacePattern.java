package ironfurnaces.tileentity.furnaces.tier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ironfurnaces.loaders.IronFurnaces;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import org.jetbrains.annotations.NotNull;

public record FurnacePattern(
        ResourceLocation id,
        int smeltTickPerItem,
        int energyCapacity,
        int energyGenerationPerTick,
        int energyConsumerPerTick,
        int inputSlotAmount
) implements Comparable<FurnacePattern>{
    public static final Codec<FurnacePattern> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(FurnacePattern::id),
            ExtraCodecs.POSITIVE_INT.fieldOf("smelt_tick_per_item").forGetter(FurnacePattern::smeltTickPerItem),
            ExtraCodecs.POSITIVE_INT.fieldOf("energy_capacity").forGetter(FurnacePattern::energyCapacity),
            ExtraCodecs.POSITIVE_INT.fieldOf("energy_generation_per_tick").forGetter(FurnacePattern::energyGenerationPerTick),
            ExtraCodecs.POSITIVE_INT.fieldOf("energy_consumer_per_tick").forGetter(FurnacePattern::energyConsumerPerTick),
            ExtraCodecs.POSITIVE_INT.fieldOf("input_slot_amount").forGetter(FurnacePattern::inputSlotAmount)
    ).apply(inst, FurnacePattern::new));

    public static final FurnacePattern FALLBACK = new FurnacePattern(IronFurnaces.id("fallback_pattern"), 1000, 20000, 40, 20, 1);
    public static final String NBT_ID = "ir_current_pattern";

    @Override
    public int compareTo(@NotNull FurnacePattern o) {
        return FurnacePatternManager.compare(this, o);
    }
}
