//~ replace_serialization
package ironfurnaces.tileentity.heater;

import ironfurnaces.adaptor.energy.EnergyWrapper;
import ironfurnaces.adaptor.energy.IEnergyWrapperHolder;
import ironfurnaces.config.GameplayConfig;
import ironfurnaces.items.ItemHeater;
import ironfurnaces.registration.ModBlocks;
import ironfurnaces.tileentity.TileEntityInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
//? forge {
/*import net.neoforged.neoforge.common.capabilities.ForgeCapabilities;
import net.neoforged.neoforge.common.util.LazyOptional;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;

import javax.annotation.Nullable;
*///?} else {
import net.minecraft.core.HolderLookup;
//?}

//~ if > 1.21.11 'TileEntityInventory' -> 'BlockEntity'
public class BlockWirelessEnergyHeaterTile extends TileEntityInventory implements IEnergyWrapperHolder {

    private final EnergyWrapper energy;

    public BlockWirelessEnergyHeaterTile(BlockPos pos, BlockState state) {
        this(ModBlocks.asGenericBlockEntityType(ModBlocks.HEATER), pos, state);
    }

    public BlockWirelessEnergyHeaterTile(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state, 1);
        this.energy = new EnergyWrapper(GameplayConfig.config.heater_capacity.get(), GameplayConfig.config.heater_capacity.get(), 0).withCallback(fEnergyStorage -> setChanged());
    }




    public static void tick(Level level, BlockPos worldPosition, BlockState blockState, BlockWirelessEnergyHeaterTile e) {
        ItemStack stack = e.getItem(0);
        if (!stack.isEmpty() && stack.getItem() instanceof ItemHeater) {
            ItemHeater.writeBoundBlockPos(stack, worldPosition);
        }

    }



    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        //? <1.21.11 {
        this.getWrapper().receiveEnergy(tag.getInt("Energy"), false);
        //?} else {
        /*tag.getInt("Energy").ifPresent(x -> this.getWrapper().receiveEnergy(x, false));
        *///?}

    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("Energy", getWrapper().getEnergyStored());
    }

    @Override
    public int[] IgetSlotsForFace(Direction side) {
        return new int[0];
    }

    @Override
    public boolean IcanExtractItem(int index, ItemStack stack, Direction direction) {
        return true;
    }

    @Override
    public String IgetName() {
        return "container.ironfurnaces.wireless_energy_heater";
    }

    @Override
    public boolean IisItemValidForSlot(int index, ItemStack stack) {
        return stack.getItem() instanceof ItemHeater;
    }

    @Override
    public AbstractContainerMenu IcreateMenu(int i, Inventory playerInventory, Player playerEntity) {
        return null;
    }

    //? forge {

    /*LazyOptional<? extends IItemHandler>[] handlers = SidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);

    @Override
    public <T> LazyOptional<T> getCapability(net.neoforged.neoforge.common.capabilities.Capability<T> capability, @Nullable Direction facing) {
        //world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 2);
        if (!this.isRemoved() && facing != null && capability == ForgeCapabilities.ITEM_HANDLER) {
            if (facing == Direction.UP)
                return handlers[0].cast();
            else if (facing == Direction.DOWN)
                return handlers[1].cast();
            else
                return handlers[2].cast();
        }
        if (!this.isRemoved() && capability == ForgeCapabilities.ENERGY) {
            return energy.getStorage().cast();
        }
        return super.getCapability(capability, facing);
    }
*///?}



    @Override
    public void setRemoved() {
        //? 1.20.1
        //energy.invalidate();
        super.setRemoved();

    }

    @Override
    public EnergyWrapper getWrapper() {
        return energy;
    }
}
