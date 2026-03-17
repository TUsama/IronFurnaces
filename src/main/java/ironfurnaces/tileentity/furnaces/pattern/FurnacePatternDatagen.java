package ironfurnaces.tileentity.furnaces.pattern;

import ironfurnaces.loaders.IronFurnaces;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class FurnacePatternDatagen extends CodecJsonProvider<FurnacePattern> {
    public FurnacePatternDatagen(PackOutput packOutput) {
        super(packOutput, FurnacePatternManager.DIRECTORY, FurnacePattern.CODEC);
    }

    private static FurnacePattern pattern(
            String name,
            int smeltTickPerItem,
            int energyCapacity,
            int energyGenerationPerTick,
            int inputSlot
    ) {
        final int CONSUME = 20;
        ResourceLocation id = new ResourceLocation(IronFurnaces.MOD_ID, name);
        return new FurnacePattern(id, smeltTickPerItem, energyCapacity, energyGenerationPerTick, CONSUME, inputSlot);
    }

    public static final Map<ResourceLocation, FurnacePattern> DUMMY_FURNACE_PATTERNS =
            Stream.of(
                    pattern("copper_furnace", 180, 80_000, 40, 1),
                    pattern("iron_furnace", 160, 80_000, 40,2),
                    pattern("silver_furnace", 140, 200_000, 100,3),
                    pattern("gold_furnace", 120, 200_000, 160,4),
                    pattern("diamond_furnace", 80, 1_000_000, 240,5),
                    pattern("emerald_furnace", 40, 1_000_000, 320,6),
                    pattern("crystal_furnace", 40, 1_000_000, 360,7),
                    pattern("obsidian_furnace", 20, 1_000_000, 500,8),
                    pattern("netherite_furnace", 5, 1_000_000, 1000,9)
            ).collect(Collectors.toUnmodifiableMap(FurnacePattern::id, Function.identity()));

    @Override
    protected void buildEntries() {
        entries.putAll(DUMMY_FURNACE_PATTERNS);

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
