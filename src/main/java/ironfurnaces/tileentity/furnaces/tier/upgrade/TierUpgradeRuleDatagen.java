package ironfurnaces.tileentity.furnaces.tier.upgrade;

import com.clefal.nirvana_lib.utils.ResourceLocationUtils;
import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.tileentity.furnaces.tier.CodecJsonProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

public final class TierUpgradeRuleDatagen extends CodecJsonProvider<TierUpgradeRule> {

    public TierUpgradeRuleDatagen(PackOutput packOutput) {
        super(packOutput, "furnace_tier_upgrades", TierUpgradeRule.CODEC);
    }

    @Override
    protected void buildEntries() {
        final boolean CONSUME = true;
        registerVanillaFurnace("upgrade_copper", "copper_furnace", CONSUME);
        registerVanillaFurnace("upgrade_iron", "iron_furnace", CONSUME);
        register("upgrade_iron2",     "copper_furnace",   "iron_furnace",    CONSUME);
        register("upgrade_silver",    "copper_furnace",   "silver_furnace",  CONSUME);
        register("upgrade_gold",      "iron_furnace",     "gold_furnace",    CONSUME);
        register("upgrade_silver2",   "iron_furnace",     "silver_furnace",  CONSUME);
        register("upgrade_gold2",     "silver_furnace",   "gold_furnace",    CONSUME);
        register("upgrade_diamond",   "gold_furnace",     "diamond_furnace", CONSUME);
        register("upgrade_emerald",   "diamond_furnace",  "emerald_furnace", CONSUME);
        register("upgrade_crystal",   "diamond_furnace",  "crystal_furnace", CONSUME);
        register("upgrade_obsidian",  "emerald_furnace",  "obsidian_furnace",CONSUME);
        register("upgrade_obsidian2", "crystal_furnace",  "obsidian_furnace",CONSUME);
        register("upgrade_netherite", "obsidian_furnace", "netherite_furnace", CONSUME);
    }

    private void register(
            String rulePath,
            String fromTierPath,
            String toTierPath,
            boolean consume
    ) {
        ResourceLocation ruleId = IronFurnaces.id(rulePath);
        ResourceLocation from = IronFurnaces.id(fromTierPath);
        ResourceLocation to = IronFurnaces.id(toTierPath);

        entries.put(ruleId, new TierUpgradeRule(ruleId, from, to));
    }

    private void registerVanillaFurnace(
            String rulePath,
            String toTierPath,
            boolean consume
    ) {
        ResourceLocation ruleId = IronFurnaces.id(rulePath);
        ResourceLocation from = ResourceLocationUtils.make("minecraft", "furnace");
        ResourceLocation to = IronFurnaces.id(toTierPath);

        entries.put(ruleId, new TierUpgradeRule(ruleId, from, to));
    }

    @Override
    public String getName() {
        return "Furnace Tier Upgrade Rules";
    }
}
