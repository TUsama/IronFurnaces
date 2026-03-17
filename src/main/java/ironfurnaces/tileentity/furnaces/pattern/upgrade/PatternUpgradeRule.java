package ironfurnaces.tileentity.furnaces.pattern.upgrade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ironfurnaces.loaders.IronFurnaces;
import net.minecraft.resources.ResourceLocation;

public record PatternUpgradeRule(
        ResourceLocation id,
        ResourceLocation from,
        ResourceLocation to
) {
    public static final String KEY = "ir_upgrade";
    public static final Codec<PatternUpgradeRule> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(PatternUpgradeRule::id),
            ResourceLocation.CODEC.fieldOf("from").forGetter(PatternUpgradeRule::from),
            ResourceLocation.CODEC.fieldOf("to").forGetter(PatternUpgradeRule::to)
    ).apply(inst, PatternUpgradeRule::new));
    private static final ResourceLocation placeholder = IronFurnaces.id("placeholder");

    public static final PatternUpgradeRule backup = new PatternUpgradeRule(IronFurnaces.id("backup_upgrade"), placeholder, IronFurnaces.id("iron_furnace"));
}
