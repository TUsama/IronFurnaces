package ironfurnaces.tileentity.furnaces.handler;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ironfurnaces.tileentity.furnaces.BlockIronFurnaceTileBaseV2;
import ironfurnaces.tileentity.furnaces.FurnaceMode;
import ironfurnaces.tileentity.furnaces.process.Burn;
import ironfurnaces.tileentity.furnaces.process.ProcessingInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.ForgeHooks;
import org.checkerframework.checker.units.qual.C;

public class ItemFuelLitHandler implements IFurnaceLitHandler{
    public static final MapCodec<ItemFuelLitHandler> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.INT.fieldOf("litTime").forGetter(x -> x.litTime),
                    Codec.INT.fieldOf("litDuration").forGetter(x -> x.litDuration)
            ).apply(instance, ItemFuelLitHandler::new));
    public static final String TYPE = "item";
    private int litTime;
    private int litDuration;

    public ItemFuelLitHandler(BlockIronFurnaceTileBaseV2 tile) {
    }

    private ItemFuelLitHandler(int litTime, int litDuration) {
        this.litTime = litTime;
        this.litDuration = litDuration;
    }

    @Override
    public void tick(BlockIronFurnaceTileBaseV2 tile) {
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
    public void refresh(BlockIronFurnaceTileBaseV2 tile) {

    }

    @Override
    public boolean isLit(BlockIronFurnaceTileBaseV2 tile) {
        return litTime > 0;
    }

    @Override
    public String getType() {
        return TYPE;
    }

}
