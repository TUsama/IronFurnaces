package ironfurnaces.tileentity.furnaces.cache;

import ironfurnaces.adaptor.energy.FEnergyStorage;
import ironfurnaces.items.ItemHeater;
import ironfurnaces.tileentity.furnaces.BlockIronFurnaceTileBaseV2;
import ironfurnaces.tileentity.furnaces.FurnaceMode;
import lombok.Getter;
import lombok.Setter;
import lombok.With;
import lombok.experimental.Accessors;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@Accessors(fluent = true, chain = true)
public class FuelCache extends ItemStackHandler implements IEnergyStorage, IModeSensitive, ICacheIndex {
    private final static int[] cacheIndex = new int[0];
    private final BlockIronFurnaceTileBaseV2 tile;
    @Setter
    protected Function<ItemStack, Optional<? extends Recipe>> grabRecipeCallback;
    @Getter
    private FEnergyStorage energy;
    private FurnaceMode mode;
    @Setter
    private Consumer<FuelCache> contentChangeCallback;

    public FuelCache(BlockIronFurnaceTileBaseV2 tile, FEnergyStorage energy, FurnaceMode mode) {
        this.tile = tile;
        this.energy = energy;
        this.mode = mode;
    }

    public FuelCache(BlockIronFurnaceTileBaseV2 tile, FEnergyStorage energy, FurnaceMode mode, Function<ItemStack, Optional<? extends Recipe>> grabRecipeCallback) {
        this(tile, energy, mode);
        this.grabRecipeCallback = grabRecipeCallback;
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
        return grabRecipeCallback.apply(stack).isPresent() || stack.getItem() instanceof ItemHeater;
    }

    @Override
    public void updateFurnaceMode(FurnaceMode mode) {
        this.mode = mode;
    }

    @Override
    protected void onContentsChanged(int slot) {
        if (contentChangeCallback != null) {
            contentChangeCallback.accept(this);
        }
    }

    @Override
    public int[] getCacheIndex() {
        return cacheIndex;
    }
}
