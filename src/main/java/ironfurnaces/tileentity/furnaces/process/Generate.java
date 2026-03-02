package ironfurnaces.tileentity.furnaces.process;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ironfurnaces.tileentity.furnaces.BlockIronFurnaceTileBaseV2;
import ironfurnaces.tileentity.furnaces.cache.AugmentCache;
import ironfurnaces.tileentity.furnaces.cache.FuelCache;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

public abstract class Generate extends ProcessingInstance {
    protected final int expectedTotalOutput;
    protected final int eachTickOutPut;
    protected int currentOutput;


    public Generate(int expectedTotalOutput, int eachTickOutPut) {
        super();
        this.expectedTotalOutput = expectedTotalOutput;
        this.eachTickOutPut = eachTickOutPut;
    }



    @Override
    public void whenStart(BlockIronFurnaceTileBaseV2 tile) {
        tile.getFuel().getStackInSlot(0).shrink(1);
    }


    @Override
    public TickResult whenTick(BlockIronFurnaceTileBaseV2 tile) {
        FuelCache fuel = tile.getFuel();
        int min = Math.min(eachTickOutPut, expectedTotalOutput - currentOutput);
        if (fuel.canReceive(min)) {
            fuel.receiveEnergy(min, false);
            currentOutput += min;
            if (currentOutput >= expectedTotalOutput) {
                return TickResult.DISCARD;
            }
            return TickResult.SUCCESS;
        } else {
            return TickResult.BLOCKED;
        }
    }


    public static class SmeltGenerate extends Generate {
        public static final MapCodec<SmeltGenerate> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Codec.INT.fieldOf("expectedTotalOutput").forGetter(x -> x.expectedTotalOutput),
                        Codec.INT.fieldOf("eachTickOutPut").forGetter(x -> x.eachTickOutPut),
                        Codec.INT.fieldOf("currentOutput").forGetter(x -> x.currentOutput)
                ).apply(instance, (a, b, c) -> {
                    SmeltGenerate smeltGenerate = new SmeltGenerate(a, b);
                    smeltGenerate.currentOutput = c;
                    return smeltGenerate;
                }));
        public static final String TYPE = "generator_smelt";

        public SmeltGenerate(int expectedTotalOutput, int eachTickOutPut) {
            super(expectedTotalOutput, eachTickOutPut);
        }

        @Override
        public String getType() {
            return TYPE;
        }
    }

    public static class BlastGenerate extends Generate {
        public static final MapCodec<BlastGenerate> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Codec.INT.fieldOf("expectedTotalOutput").forGetter(x -> x.expectedTotalOutput),
                        Codec.INT.fieldOf("eachTickOutPut").forGetter(x -> x.eachTickOutPut),
                        Codec.INT.fieldOf("currentOutput").forGetter(x -> x.currentOutput)
                ).apply(instance, (a, b, c) -> {
                    BlastGenerate blastGenerate = new BlastGenerate(a, b);
                    blastGenerate.currentOutput = c;
                    return blastGenerate;
                }));
        public static final String TYPE = "generator_blast";

        public BlastGenerate(int expectedTotalOutput, int eachTickOutPut) {
            super(expectedTotalOutput,eachTickOutPut);
        }

        @Override
        public String getType() {
            return TYPE;
        }
    }
}
