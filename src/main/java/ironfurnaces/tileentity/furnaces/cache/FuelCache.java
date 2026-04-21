//~ replace_INBTSerializable
package ironfurnaces.tileentity.furnaces.cache;

import ironfurnaces.adaptor.energy.FEnergyStorage;
import ironfurnaces.items.ItemHeater;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.pattern.IFurnaceStats;
import ironfurnaces.tileentity.furnaces.pattern.mode.AbstractFurnaceModeHandler;
import ironfurnaces.tileentity.furnaces.pattern.mode.FurnaceModeManager;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
//? 1.20.1 {

//? } else {
/*import net.minecraft.core.HolderLookup;
*///?}
import java.util.function.Consumer;
import java.util.function.Predicate;

@Accessors(fluent = true, chain = true)
public class FuelCache extends ResizableCache implements IEnergyStorage {
    private final static int[] cacheIndex = new int[0];
        @Getter
    private FEnergyStorage energy;
    @Setter
    private Predicate<ItemStack> burnableFunction;
    @Setter
    private Consumer<FuelCache> contentChangeCallback;

    public FuelCache(AbstractFurnaceModeHandler mode, FEnergyStorage energy) {
        super(mode);
        this.energy = energy;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return energy.receiveEnergy(maxReceive, simulate);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return energy.extractEnergy(maxExtract, simulate);
    }

    @Override
    public int getEnergyStored() {
        return energy.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored() {
        return energy.getMaxEnergyStored();
    }

    @Override
    public boolean canExtract() {
        return energy.canExtract();
    }

    @Override
    public boolean canReceive() {
        return energy.canReceive();
    }

    public boolean canReceive(int energy) {
        return (getEnergyStored() + energy) <= getMaxEnergyStored();
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return burnableFunction.test(stack) || stack.getItem() instanceof ItemHeater;
    }

    /*

    @Override
    public void updateFurnaceMode(FurnaceMode mode, FurnacePatternBlockEntity blockEntity) {
        if (mode.equals(FurnaceMode.FACTORY) && !this.stacks.isEmpty()){
            blockEntity.addLevelConsumer(level -> {
                blockEntity.returnOrDropStack(this.stacks, blockEntity.getOwner());

            });
        }
    }
*/
    @Override
    protected void onContentsChanged(int slot) {
        if (contentChangeCallback != null) {
            contentChangeCallback.accept(this);
        }
        recomputeFillStats();

    }

    @Override
    public void update(AbstractFurnaceModeHandler mode, IRecipeTypeHandler recipeTypeHandler, IFurnaceStats<?> stats, FurnacePatternBlockEntity blockEntity) {
        super.update(mode, recipeTypeHandler, stats, blockEntity);
        this.energy.setCapacity(stats.energyCapacity());
        this.energy.setMaxTransfer(stats.energyCapacity());
    }

    @Override
    protected int updateSlotAmount(AbstractFurnaceModeHandler mode, IRecipeTypeHandler recipeTypeHandler, IFurnaceStats<?> stats, FurnacePatternBlockEntity blockEntity) {
        return FurnaceModeManager.INSTANCE.getMaxFuelSlot(stats);
    }


    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();

        tag.put("Items", super.serializeNBT());
        Tag tag1 = energy.serializeNBT();
        tag.put("Energy", tag1);

        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains("Items", Tag.TAG_COMPOUND)) {
            super.deserializeNBT(nbt.getCompound("Items"));
        }
        if (nbt.contains("Energy")) {
            energy.deserializeNBT(nbt.get("Energy"));
        }

        recomputeFillStats();
    }

}
