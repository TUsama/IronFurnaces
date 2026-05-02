
package ironfurnaces.tileentity.furnaces.pattern;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;
import java.util.Set;

public record RainbowFurnaceConfig(
        Map<ResourceLocation, RainbowBonus> bonuses,
        RainbowBonus allKindsBonus,
        Set<ResourceLocation> ignorePatterns
) {
    public static final Codec<RainbowFurnaceConfig> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.unboundedMap(ResourceLocation.CODEC, RainbowBonus.CODEC)
                    .optionalFieldOf("bonuses", Map.of())
                    .forGetter(RainbowFurnaceConfig::bonuses),
            RainbowBonus.CODEC.fieldOf("all_kinds_bonus").forGetter(RainbowFurnaceConfig::allKindsBonus),
            ResourceLocation.CODEC.listOf()
                    .xmap(Set::copyOf, List::copyOf)
                    .optionalFieldOf("ignore_patterns", Set.of())
                    .forGetter(RainbowFurnaceConfig::ignorePatterns)
    ).apply(inst, RainbowFurnaceConfig::new));

    public RainbowBonus bonusFor(ResourceLocation patternId) {
        if (patternId == null || ignorePatterns.contains(patternId)) {
            return RainbowBonus.ZERO;
        }
        return bonuses.getOrDefault(patternId, RainbowBonus.ZERO);
    }

    public boolean isAllKindsActivated(Set<ResourceLocation> kinds){
        return bonuses.keySet().containsAll(kinds);
    }

    public boolean isEmpty() {
        return bonuses.isEmpty();
    }
}
