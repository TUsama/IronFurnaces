package ironfurnaces.tileentity.furnaces.handler;

import ironfurnaces.tileentity.furnaces.BlockIronFurnaceTileBaseV2;
import ironfurnaces.tileentity.furnaces.FurnaceMode;
import ironfurnaces.tileentity.furnaces.process.Burn;
import ironfurnaces.tileentity.furnaces.process.ProcessingInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.ForgeHooks;

public class ItemFuelLitHandler implements IFurnaceLitHandler{
    private final BlockIronFurnaceTileBaseV2 tile;
    private int litTime;
    private int litDuration;

    public ItemFuelLitHandler(BlockIronFurnaceTileBaseV2 tile) {
        this.tile = tile;
    }

    @Override
    public void tick() {
        Level level = tile.getLevel();
        BlockPos blockPos = tile.getBlockPos();
        if (litTime > 0) {
            if (!level.getBlockState(blockPos).getValue(BlockStateProperties.LIT)) {
                level.setBlock(blockPos, level.getBlockState(blockPos).setValue(BlockStateProperties.LIT, true), 3);
            }

            litTime--;
        }

        if (litTime == 0){
            if (!tile.getInstanceManager().isBlocking()){
                ItemStack stackInSlot = tile.getFuel().getStackInSlot(0);
                int burnTime = tile.getAugments().getCurrentModifiers().normalBurnTimeModifier().applyAsInt(ForgeHooks.getBurnTime(stackInSlot, tile.getAugments().getCurrentRecipeType()));

                if (burnTime > 0) {
                    litTime = burnTime;
                    litDuration = burnTime;
                    stackInSlot.shrink(1);
                }
            }
        }


         if (litTime == 0 && level.getBlockState(blockPos).getValue(BlockStateProperties.LIT)) {
            level.setBlock(blockPos, level.getBlockState(blockPos).setValue(BlockStateProperties.LIT, false), 3);
        }
    }

    @Override
    public void refresh() {

    }

    @Override
    public boolean isLit() {
        return litTime > 0;
    }


}
