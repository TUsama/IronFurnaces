package ironfurnaces.tileentity.furnaces.handler;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ironfurnaces.container.BlockWirelessEnergyHeaterContainer;
import ironfurnaces.items.ItemHeater;
import ironfurnaces.tileentity.BlockWirelessEnergyHeaterTile;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.util.FuelBurnTimeUtil;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.items.ItemHandlerHelper;

@Getter
public class ItemFuelLitHandler implements IFurnaceLitHandler {
    public static final MapCodec<ItemFuelLitHandler> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.INT.fieldOf("litTime").forGetter(ItemFuelLitHandler::getLitTime),
                    Codec.INT.fieldOf("litDuration").forGetter(ItemFuelLitHandler::getLitDuration)
            ).apply(instance, ItemFuelLitHandler::new));
    public static final String TYPE = "item";
    private int litTime;
    private int litDuration;

    public ItemFuelLitHandler() {
        this(0, 0);
    }

    private ItemFuelLitHandler(int litTime, int litDuration) {
        this.litTime = litTime;
        this.litDuration = litDuration;
    }

    @Override
    public void tick(FurnacePatternBlockEntity tile) {
        Level level = tile.getLevel();
        BlockPos blockPos = tile.getBlockPos();
        if (litTime > 0) {
            litTime--;
        }

        if (litTime == 0 && tile.getInstanceManager().needLit(tile)) {

            ItemStack stackInSlot = tile.getFuel().getStackInSlot(0);
            int burnTime = tile.getAugments().getCurrentModifiers().normalBurnTimeModifier().applyAsInt(FuelBurnTimeUtil.getBurnTime(stackInSlot, tile.getAugments().getCurrentRecipeType().getRecipeType()));

            if (burnTime > 0) {
                litTime = burnTime;
                litDuration = burnTime;
                ItemStack copy1 = stackInSlot.copy();
                if (stackInSlot.isDamageableItem()){
                    stackInSlot.setDamageValue(stackInSlot.getDamageValue() + 1);
                } else {
                    stackInSlot.shrink(1);
                }

                if (copy1.hasCraftingRemainingItem()) {
                    ItemStack copy = copy1.getCraftingRemainingItem().copy();
                    ItemStack itemStack = ItemHandlerHelper.insertItem(tile.getRemaining(), copy, false);
                    if (level != null && !level.isClientSide) {
                        Containers.dropItemStack(level, blockPos.getX(), blockPos.getY(), blockPos.getZ(), itemStack);
                    }
                }
            } else if (stackInSlot.getItem() instanceof ItemHeater) {
                BlockPos boundBlockPos = ItemHeater.getBoundBlockPos(stackInSlot);
                if (boundBlockPos != null){
                    if (level.getBlockEntity(boundBlockPos) instanceof BlockWirelessEnergyHeaterTile heaterTile && heaterTile.getWrapper().getEnergyStored() >= 20) {
                        heaterTile.getWrapper().extractEnergy(20, false);
                        litTime = 5;
                        litDuration = 5;
                    }
                }


            }
        }
        ensureLitState(tile);
    }

    @Override
    public void refresh(FurnacePatternBlockEntity tile) {

    }

    @Override
    public boolean isLit(FurnacePatternBlockEntity tile) {
        return litTime > 0;
    }

    @Override
    public String getType() {
        return TYPE;
    }

}
