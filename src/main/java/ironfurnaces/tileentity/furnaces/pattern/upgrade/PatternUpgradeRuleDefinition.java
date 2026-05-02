
package ironfurnaces.tileentity.furnaces.pattern.upgrade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public record PatternUpgradeRuleDefinition(
        ResourceLocation from,
        ResourceLocation to
) {
    public static final Codec<PatternUpgradeRuleDefinition> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ResourceLocation.CODEC.fieldOf("from").forGetter(PatternUpgradeRuleDefinition::from),
            ResourceLocation.CODEC.fieldOf("to").forGetter(PatternUpgradeRuleDefinition::to)
    ).apply(inst, PatternUpgradeRuleDefinition::new));

    public PatternUpgradeRule toRuntime(ResourceLocation id) {
        return new PatternUpgradeRule(id, from, to);
    }
}
