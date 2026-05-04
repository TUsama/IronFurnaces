
package ironfurnaces.tileentity.furnaces.pattern.upgrade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

public record PatternUpgradeRuleDefinition(
        Identifier from,
        Identifier to
) {
    public static final Codec<PatternUpgradeRuleDefinition> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Identifier.CODEC.fieldOf("from").forGetter(PatternUpgradeRuleDefinition::from),
            Identifier.CODEC.fieldOf("to").forGetter(PatternUpgradeRuleDefinition::to)
    ).apply(inst, PatternUpgradeRuleDefinition::new));

    public PatternUpgradeRule toRuntime(Identifier id) {
        return new PatternUpgradeRule(id, from, to);
    }
}
