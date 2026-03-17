package ironfurnaces.tileentity.furnaces.process;

import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Function6;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.cache.FuelCache;
import ironfurnaces.tileentity.furnaces.pattern.FurnacePattern;
import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.function.BiFunction;
@Getter(AccessLevel.PRIVATE)
public abstract class Generate extends ProcessingInstance {
    protected final int expectedTotalOutput;
    protected int eachTickOutPut;
    protected int currentOutput;
    protected float partialProgress;


    public Generate(int index, int expectedTotalOutput, int eachTickOutPut) {
        this(index, false, expectedTotalOutput, eachTickOutPut, 0, 0.0f);
    }

    protected Generate(int fromIndex, boolean handledStart, int expectedTotalOutput, int eachTickOutPut, int currentOutput, float partialProgress) {
        super(fromIndex, handledStart);
        this.expectedTotalOutput = expectedTotalOutput;
        this.eachTickOutPut = eachTickOutPut;
        this.currentOutput = currentOutput;
        this.partialProgress = partialProgress;
    }

    protected static <T extends Generate> MapCodec<T> simpleGenerateCodec(Function6<Integer, Boolean, Integer, Integer,Integer,Float,T> factory) {
        return RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        ExtraCodecs.POSITIVE_INT.fieldOf("fromIndex").forGetter(Generate::getFromIndex),
                        Codec.BOOL.fieldOf("handledStart").forGetter(Generate::isHandledStart),
                        ExtraCodecs.POSITIVE_INT.fieldOf("expectedTick").forGetter(Generate::getExpectedTotalOutput),
                        ExtraCodecs.POSITIVE_INT.fieldOf("currentTick").forGetter(Generate::getEachTickOutPut),
                        ExtraCodecs.POSITIVE_INT.fieldOf("partialProgress").forGetter(Generate::getCurrentOutput),
                        ExtraCodecs.POSITIVE_FLOAT.fieldOf("currentTick").forGetter(Generate::getPartialProgress)
                ).apply(instance, factory));
    }

    @Override
    public void whenStart(FurnacePatternBlockEntity tile) {
        ItemStack copy = tile.getFuel().getStackInSlot(fromIndex).copy();
        tile.getFuel().getStackInSlot(fromIndex).shrink(1);
        if (copy.hasCraftingRemainingItem()){
            ItemStack copy1 = copy.getCraftingRemainingItem().copy();
            ItemStack itemStack = ItemHandlerHelper.insertItem(tile.getRemaining(), copy1, false);
            Level level = tile.getLevel();
            BlockPos blockPos = tile.getBlockPos();
            if (level != null && !level.isClientSide){
                Containers.dropItemStack(level, blockPos.getX(), blockPos.getY(), blockPos.getZ(), itemStack);
            }
        }
    }


    @Override
    public TickResult whenTick(FurnacePatternBlockEntity tile) {
        FuelCache fuel = tile.getFuel();
        partialProgress += tile.getAugments().getCurrentModifiers().generateOutputModifier().get(eachTickOutPut);
        int actualGeneration = (int) partialProgress;
        int min = Math.min(actualGeneration, expectedTotalOutput - currentOutput);
        partialProgress -= actualGeneration;
        if (fuel.canReceive(min)) {
            fuel.receiveEnergy(min, false);
            currentOutput += min;
            if (currentOutput >= expectedTotalOutput) {
                return TickResult.DONE;
            }
            return TickResult.SUCCESS;
        } else {
            return TickResult.BLOCKED;
        }
    }


    @Override
    public void whenChangePattern(FurnacePattern pattern) {
        this.eachTickOutPut = pattern.energyGenerationPerTick();
    }

    @Override
    public float getDoneProgress() {
        return currentOutput / (expectedTotalOutput * 1.0f);
    }

    public static class SmeltGenerate extends Generate {
        public static final MapCodec<SmeltGenerate> CODEC = simpleGenerateCodec(SmeltGenerate::new);
        public static final String TYPE = "generator_smelt";

        public SmeltGenerate(int index, int expectedTotalOutput, int eachTickOutPut) {
            super(index, expectedTotalOutput, eachTickOutPut);
        }

        private SmeltGenerate(int fromIndex, boolean handledStart, int expectedTotalOutput, int eachTickOutPut, int currentOutput, float partialProgress) {
            super(fromIndex, handledStart, expectedTotalOutput, eachTickOutPut, currentOutput, partialProgress);
        }

        @Override
        public String getType() {
            return TYPE;
        }
    }

    public static class BlastGenerate extends Generate {
        public static final MapCodec<BlastGenerate> CODEC = simpleGenerateCodec(BlastGenerate::new);
        public static final String TYPE = "generator_blast";

        public BlastGenerate(int index, int expectedTotalOutput, int eachTickOutPut) {
            super(index, expectedTotalOutput, eachTickOutPut);
        }

        private BlastGenerate(int fromIndex, boolean handledStart, int expectedTotalOutput, int eachTickOutPut, int currentOutput, float partialProgress) {
            super(fromIndex, handledStart, expectedTotalOutput, eachTickOutPut, currentOutput, partialProgress);
        }

        @Override
        public String getType() {
            return TYPE;
        }
    }
}
