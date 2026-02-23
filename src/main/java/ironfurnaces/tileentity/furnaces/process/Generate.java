package ironfurnaces.tileentity.furnaces.process;

import ironfurnaces.tileentity.furnaces.BlockIronFurnaceTileBaseV2;
import ironfurnaces.tileentity.furnaces.cache.AugmentCache;
import ironfurnaces.tileentity.furnaces.cache.FuelCache;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

public class Generate extends ProcessingInstance{
    private final int expectedTotalOutput;
    private int currentOutput;
    private final ItemStack stack;
    private final int eachTickOutPut;
    private final AugmentCache augment;


    public Generate(int expectedTotalOutput, Recipe<?> recipe, ItemStack stack, int eachTickOutPut, AugmentCache augment) {
        super(recipe);
        this.expectedTotalOutput = expectedTotalOutput;
        this.stack = stack;
        this.eachTickOutPut = eachTickOutPut;
        this.augment = augment;
    }

    @Override
    public boolean validate() {
        return true;
    }

    @Override
    public void whenStart(BlockIronFurnaceTileBaseV2 tile) {
        stack.shrink(1);
    }



    @Override
    public TickResult whenTick(BlockIronFurnaceTileBaseV2 tile) {
        FuelCache fuel = tile.getFuel();
        if (fuel.canReceive()){
            int min = Math.min(eachTickOutPut, expectedTotalOutput - currentOutput);
            fuel.receiveEnergy(min, false);
            currentOutput += min;
        }
        if (currentOutput >= expectedTotalOutput) {
            return TickResult.DISCARD;
        }
        return TickResult.SUCCESS;
    }



    public static class SmeltGenerate extends Generate{


        public SmeltGenerate(int expectedTotalOutput, Recipe<?> recipe, ItemStack stack, int eachTickOutPut, AugmentCache augment) {
            super(expectedTotalOutput, recipe, stack, eachTickOutPut, augment);
        }
    }

    public static class BlastGenerate extends Generate{

        public BlastGenerate(int expectedTotalOutput, Recipe<?> recipe, ItemStack stack, int eachTickOutPut, AugmentCache augment) {
            super(expectedTotalOutput, recipe, stack, eachTickOutPut, augment);
        }
    }
}
