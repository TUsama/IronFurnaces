//? forge{
/*package ironfurnaces.capability;

import ironfurnaces.adaptor.energy.FEnergyStorage;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.common.capabilities.ForgeCapabilities;
import net.neoforged.neoforge.common.capabilities.ICapabilitySerializable;
import net.neoforged.neoforge.common.util.LazyOptional;
import net.neoforged.neoforge.energy.IEnergyStorage;

import javax.annotation.Nullable;

public class ItemEnergyCapabilityProvider implements ICapabilitySerializable<CompoundTag> {
    private final FEnergyStorage energyStorage;
    private final LazyOptional<IEnergyStorage> energyOptional;

    public ItemEnergyCapabilityProvider(int capacity, int maxReceive, int maxExtract) {
        this.energyStorage = new FEnergyStorage(capacity, maxReceive, maxExtract);
        this.energyOptional = LazyOptional.of(() -> this.energyStorage);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return this.energyOptional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("Energy", this.energyStorage.getEnergyStored());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        if (tag == null) {
            return;
        }

        if (tag.contains("Energy", Tag.TAG_INT)) {
            this.energyStorage.setEnergy(tag.getInt("Energy"));
        }
    }
}

*///?}