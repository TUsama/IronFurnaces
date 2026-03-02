package ironfurnaces.tileentity.furnaces.tier;

import ironfurnaces.loaders.IronFurnaces;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

public final class FurnacePatternDatagen extends CodecJsonProvider<FurnacePattern> {
    public FurnacePatternDatagen(PackOutput packOutput) {
        super(packOutput, "furnace_tiers", FurnacePattern.CODEC);
    }

    @Override
    protected void buildEntries() {
        final int CONSUME = 20;
        final int INPUT_SLOTS = 1;

        register("copper_furnace",    180,  80_000,     40,   CONSUME, INPUT_SLOTS);
        register("iron_furnace",      160,  80_000,     40,   CONSUME, INPUT_SLOTS);

        register("silver_furnace",    140, 200_000,    100,   CONSUME, INPUT_SLOTS);
        register("gold_furnace",      120, 200_000,    160,   CONSUME, INPUT_SLOTS);

        register("diamond_furnace",    80, 1_000_000,  240,   CONSUME, INPUT_SLOTS);
        register("emerald_furnace",    40, 1_000_000,  320,   CONSUME, INPUT_SLOTS);
        register("crystal_furnace",    40, 1_000_000,  360,   CONSUME, INPUT_SLOTS);
        register("obsidian_furnace",   20, 1_000_000,  500,   CONSUME, INPUT_SLOTS);
        register("netherite_furnace",   5, 1_000_000, 1000,   CONSUME, INPUT_SLOTS);

        //register("rainbow_furnace",    20, 1_000_000, 2000,   CONSUME, INPUT_SLOTS);
    }

    private void register(
            String name,
            int smeltTickPerItem,
            int energyCapacity,
            int energyGenerationPerTick,
            int energyConsumerPerTick,
            int inputSlotAmount
    ) {
        ResourceLocation id = new ResourceLocation(IronFurnaces.MOD_ID, name);
        entries.put(id, new FurnacePattern(
                id,
                smeltTickPerItem,
                energyCapacity,
                energyGenerationPerTick,
                energyConsumerPerTick,
                inputSlotAmount
        ));
    }

    @Override
    public String getName() {
        return "Furnace Tiers";
    }
}
