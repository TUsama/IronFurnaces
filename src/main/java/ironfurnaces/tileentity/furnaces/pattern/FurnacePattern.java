package ironfurnaces.tileentity.furnaces.pattern;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ironfurnaces.loaders.IronFurnaces;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public record FurnacePattern(
        ResourceLocation id,
        int smeltTickPerItem,
        int energyCapacity,
        int energyGenerationPerTick,
        int energyConsumerPerTick,
        int inputSlotAmount,
        Optional<RainbowFurnaceConfig> rainbow
) {

    public static final Codec<FurnacePattern> DIRECT_CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(FurnacePattern::id),
            Codec.INT.fieldOf("smelt_tick_per_item").forGetter(FurnacePattern::smeltTickPerItem),
            Codec.INT.fieldOf("energy_capacity").forGetter(FurnacePattern::energyCapacity),
            Codec.INT.fieldOf("energy_generation_per_tick").forGetter(FurnacePattern::energyGenerationPerTick),
            Codec.INT.fieldOf("energy_consumer_per_tick").forGetter(FurnacePattern::energyConsumerPerTick),
            Codec.INT.fieldOf("input_slot_amount").forGetter(FurnacePattern::inputSlotAmount),
            RainbowFurnaceConfig.CODEC.optionalFieldOf("rainbow").forGetter(FurnacePattern::rainbow)
    ).apply(inst, FurnacePattern::new));
    public static final FurnacePattern FALLBACK = new FurnacePattern(
            IronFurnaces.id("fallback_pattern"),
            1000,
            20_000,
            40,
            20,
            1,
            Optional.empty()
    );
    /**
     * 推荐用于 BlockEntity / capability / packet 中的“引用式”序列化。
     * 只存 id，加载时去 manager 查。
     */
    public static final Codec<FurnacePattern> REF_CODEC = ResourceLocation.CODEC.xmap(
            FurnacePatternManager::getOrFallback,
            FurnacePattern::id
    );
    public static final String NBT_KEY = "ir_current_pattern";

    public boolean isRainbow() {
        return rainbow.isPresent() && !rainbow.get().isEmpty();
    }

    public RainbowFurnaceConfig rainbowConfigOrEmpty() {
        return rainbow.orElseGet(() -> new RainbowFurnaceConfig(java.util.Map.of(), 20, false, java.util.Set.of()));
    }


    public FurnacePattern withId(ResourceLocation newId) {
        return new FurnacePattern(
                newId,
                smeltTickPerItem,
                energyCapacity,
                energyGenerationPerTick,
                energyConsumerPerTick,
                inputSlotAmount,
                rainbow
        );
    }
}