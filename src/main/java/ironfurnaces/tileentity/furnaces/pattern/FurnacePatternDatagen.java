
package ironfurnaces.tileentity.furnaces.pattern;

import com.clefal.nirvana_lib.utils.ResourceLocationUtils;
import ironfurnaces.loaders.IronFurnaces;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class FurnacePatternDatagen extends CodecJsonProvider<FurnacePatternDefinition> {

    public FurnacePatternDatagen(PackOutput packOutput) {
        super(packOutput, FurnacePatternManager.DIRECTORY, FurnacePatternDefinition.CODEC);
    }


    private static FurnacePatternDefinition definition(
            int smeltTickPerItem,
            int energyCapacity,
            int energyGenerationPerTick,
            int inputSlot,
            ResourceLocation referenceBlock
    ) {
        final int CONSUME = 20;
        return FurnacePatternDefinition.normal(
                smeltTickPerItem,
                energyCapacity,
                energyGenerationPerTick,
                CONSUME,
                inputSlot,
                Optional.of(referenceBlock)
        );
    }

    private static FurnacePatternDefinition definition(
            int smeltTickPerItem,
            int batch,
            int energyCapacity,
            int energyGenerationPerTick,
            int inputSlot,
            ResourceLocation referenceBlock
    ) {
        final int CONSUME = 20;
        return FurnacePatternDefinition.normal(
                smeltTickPerItem,
                batch,
                energyCapacity,
                energyGenerationPerTick,
                CONSUME,
                inputSlot,
                Optional.of(referenceBlock)
        );
    }

    public static final Map<ResourceLocation, FurnacePatternDefinition> DUMMY_FURNACE_PATTERN_DEFINITIONS =
            Stream.of(
                    Map.entry(IronFurnaces.id("copper_furnace"),
                            definition(180, 80_000, 40, 1, ResourceLocationUtils.make("minecraft", "copper_block"))),

                    Map.entry(IronFurnaces.id("iron_furnace"),
                            definition(160, 80_000, 80, 2, ResourceLocationUtils.make("minecraft", "iron_block"))),

                    Map.entry(IronFurnaces.id("silver_furnace"),
                            definition(140, 200_000, 120, 3, ResourceLocationUtils.make("minecraft", "iron_block"))),

                    Map.entry(IronFurnaces.id("gold_furnace"),
                            definition(120, 200_000, 160, 4, ResourceLocationUtils.make("minecraft", "gold_block"))),

                    Map.entry(IronFurnaces.id("diamond_furnace"),
                            definition(80, 1_000_000, 240, 5, ResourceLocationUtils.make("minecraft", "diamond_block"))),

                    Map.entry(IronFurnaces.id("emerald_furnace"),
                            definition(40, 1_000_000, 320, 6, ResourceLocationUtils.make("minecraft", "emerald_block"))),

                    Map.entry(IronFurnaces.id("crystal_furnace"),
                            definition(40, 1_000_000, 360, 7, ResourceLocationUtils.make("minecraft", "glass"))),

                    Map.entry(IronFurnaces.id("obsidian_furnace"),
                            definition(20, 1_000_000, 500, 8, ResourceLocationUtils.make("minecraft", "obsidian"))),

                    Map.entry(IronFurnaces.id("netherite_furnace"),
                            definition(5, 1_000_000, 1000, 9, ResourceLocationUtils.make("minecraft", "netherite_block"))),

                    Map.entry(IronFurnaces.id("allthemodium_furnace"),
                            definition(20, 16, 2_000_000, 2000, 10, ResourceLocationUtils.make("allthemodium", "allthemodium_block"))),


                    Map.entry(IronFurnaces.id("vibranium_furnace"),
                            definition(20, 32, 3_000_000, 3000, 11, ResourceLocationUtils.make("allthemodium", "vibranium_block"))),

                    Map.entry(IronFurnaces.id("unobtainium_furnace"),
                            definition(20, 64, 5_000_000, 5000, 12, ResourceLocationUtils.make("allthemodium", "unobtainium_block")))

            ).collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));

    @Override
    protected void buildEntries() {
        entries.putAll(DUMMY_FURNACE_PATTERN_DEFINITIONS);

        registerRainbow();
    }

    private void registerRainbow() {
        RainbowFurnaceConfig config = new RainbowFurnaceConfig(
                Map.of(
                        IronFurnaces.id("copper_furnace"), new RainbowBonus(-1, 0,10_000, 10, 0, 0),
                        IronFurnaces.id("iron_furnace"), new RainbowBonus(-2, 0,20_000, 40, 0, 0),
                        IronFurnaces.id("gold_furnace"), new RainbowBonus(-3, 0,40_000, 80, 0, 1),
                        IronFurnaces.id("diamond_furnace"), new RainbowBonus(-4, 0,80_000, 120, 0, 3),
                        IronFurnaces.id("netherite_furnace"), new RainbowBonus(-5, 5,500_000, 500, 0, 5)
                ),
                new RainbowBonus(0, 0,0, 500_000, 0, 0),
                java.util.Set.of(IronFurnaces.id("rainbow_furnace"))
        );

        entries.put(
                IronFurnaces.id("rainbow_furnace"),
                FurnacePatternDefinition.rainbow(
                        20,
                        100_000,
                        10000,
                        20,
                        1,
                        Optional.empty(),
                        config
                )
        );
    }

    @Override
    public String getName() {
        return "Furnace Pattern Definitions";
    }
}