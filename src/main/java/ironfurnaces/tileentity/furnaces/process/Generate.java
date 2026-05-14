package ironfurnaces.tileentity.furnaces.process;

import com.mojang.datafixers.util.Function5;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.cache.AugmentCache;
import ironfurnaces.tileentity.furnaces.cache.FuelCache;
import ironfurnaces.tileentity.furnaces.pattern.IFurnaceStats;
import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.transfer.transaction.Transaction;

@Getter(AccessLevel.PRIVATE)
public abstract class Generate extends ProcessingInstance {
    protected final float expectedTotalOutput;
    protected float eachTickOutPut;
    protected float currentOutput;


    public Generate(int index, int expectedTotalOutput, int eachTickOutPut) {
        this(index, false, expectedTotalOutput, eachTickOutPut, 0);
    }

    protected Generate(int fromIndex, boolean handledStart, float expectedTotalOutput, float eachTickOutPut, float currentOutput) {
        super(fromIndex, handledStart);
        this.expectedTotalOutput = expectedTotalOutput;
        this.eachTickOutPut = eachTickOutPut;
        this.currentOutput = currentOutput;

    }

    protected static <T extends Generate> MapCodec<T> simpleGenerateCodec(Function5<Integer, Boolean, Float, Float, Float, T> factory) {
        return RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        ExtraCodecs.POSITIVE_INT.fieldOf("fromIndex").forGetter(Generate::getFromIndex),
                        Codec.BOOL.fieldOf("handledStart").forGetter(Generate::isHandledStart),
                        ExtraCodecs.POSITIVE_FLOAT.fieldOf("expectedTick").forGetter(Generate::getExpectedTotalOutput),
                        ExtraCodecs.POSITIVE_FLOAT.fieldOf("currentTick").forGetter(Generate::getEachTickOutPut),
                        ExtraCodecs.POSITIVE_FLOAT.fieldOf("partialProgress").forGetter(Generate::getCurrentOutput)
                ).apply(instance, factory));
    }

    @Override
    public void whenStart(FurnacePatternBlockEntity tile) {
        ItemStack copy = tile.getFuel().getStackInSlot(fromIndex).copy();
        tile.getFuel().getStackInSlot(fromIndex).shrink(1);
        ItemStackTemplate remainderTemplate = copy.getItem().getCraftingRemainder(copy);
        if (remainderTemplate != null) {
            ItemStack remainder = remainderTemplate.create();

            if (!remainder.isEmpty()) {
                ItemStack notInserted = tile.getRemaining().insertItemReturnRemaining(remainder, false, null);

                if (!notInserted.isEmpty()) {
                    Level level = tile.getLevel();
                    BlockPos blockPos = tile.getBlockPos();

                    if (level != null && !level.isClientSide()) {
                        Containers.dropItemStack(
                                level,
                                blockPos.getX() + 0.5D,
                                blockPos.getY() + 0.5D,
                                blockPos.getZ() + 0.5D,
                                notInserted
                        );
                    }
                }
            }
        }
    }

    @Override
    public boolean needLit(FurnacePatternBlockEntity tile) {
        return true;
    }

    @Override
    public TickResult whenTick(FurnacePatternBlockEntity tile) {
        FuelCache fuel = tile.getFuel();
        if (!validateSlot(fuel, tile)) return TickResult.DISCARD;

        AugmentCache.GreenAugmentModifier.Modifiers currentModifiers = tile.getAugments().getCurrentModifiers();

        float actualGeneration = currentModifiers.generatePerTickOutputModifier().get(eachTickOutPut);
        int min = (int) Math.ceil(Math.min(actualGeneration, expectedTotalOutput - currentOutput));
        try (Transaction transaction = Transaction.openRoot()) {
            int insert = fuel.insert(min, transaction);
            if (insert == min) {
                transaction.commit();
                currentOutput += currentModifiers.generateCurrentOutputModifier().get(min);

                if (currentOutput >= expectedTotalOutput) {
                    return TickResult.DONE;
                }
                return TickResult.SUCCESS;
            } else {
                return TickResult.BLOCKED;
            }
        }

    }


    @Override
    public void whenChangeStats(IFurnaceStats stats) {
        this.eachTickOutPut = stats.energyGenerationPerTick();
    }

    @Override
    public float getDoneProgress() {
        return currentOutput / (expectedTotalOutput);
    }


    public static class SmeltGenerate extends Generate {
        public static final MapCodec<SmeltGenerate> CODEC = simpleGenerateCodec(SmeltGenerate::new);
        public static final String TYPE = "generator_smelt";

        public SmeltGenerate(int index, int expectedTotalOutput, int eachTickOutPut) {
            super(index, expectedTotalOutput, eachTickOutPut);
        }

        private SmeltGenerate(int fromIndex, boolean handledStart, float expectedTotalOutput, float eachTickOutPut, float currentOutput) {
            super(fromIndex, handledStart, expectedTotalOutput, eachTickOutPut, currentOutput);
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

        private BlastGenerate(int fromIndex, boolean handledStart, float expectedTotalOutput, float eachTickOutPut, float currentOutput) {
            super(fromIndex, handledStart, expectedTotalOutput, eachTickOutPut, currentOutput);
        }

        @Override
        public String getType() {
            return TYPE;
        }
    }


    public static class SmokingGenerate extends Generate {
        public static final MapCodec<SmokingGenerate> CODEC = simpleGenerateCodec(SmokingGenerate::new);
        public static final String TYPE = "generator_smoking";

        private SmokingGenerate(int fromIndex, boolean handledStart, float expectedTotalOutput, float eachTickOutPut, float currentOutput) {
            super(fromIndex, handledStart, expectedTotalOutput, eachTickOutPut, currentOutput);
        }

        public SmokingGenerate(int index, int expectedTotalOutput, int eachTickOutPut) {
            super(index, expectedTotalOutput, eachTickOutPut);
        }

        @Override
        public String getType() {
            return TYPE;
        }
    }
}
