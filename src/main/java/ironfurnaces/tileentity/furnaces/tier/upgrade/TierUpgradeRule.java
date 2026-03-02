package ironfurnaces.tileentity.furnaces.tier.upgrade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ironfurnaces.loaders.IronFurnaces;
import net.minecraft.resources.ResourceLocation;

public record TierUpgradeRule(
        ResourceLocation id,
        ResourceLocation from,
        ResourceLocation to
) {
    public static final String KEY = "ir_upgrade";
    public static final Codec<TierUpgradeRule> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(TierUpgradeRule::id),
            ResourceLocation.CODEC.fieldOf("from").forGetter(TierUpgradeRule::from),
            ResourceLocation.CODEC.fieldOf("to").forGetter(TierUpgradeRule::to)
    ).apply(inst, TierUpgradeRule::new));
    private static final ResourceLocation placeholder = IronFurnaces.id("placeholder");

    public static final TierUpgradeRule backup = new TierUpgradeRule(IronFurnaces.id("backup_upgrade"), placeholder, IronFurnaces.id("iron_furnace"));
}
