package ironfurnaces.capability;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import ironfurnaces.loaders.IronFurnaces;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerFurnacesListProvider implements ICapabilityProvider, ICapabilitySerializable<CompoundTag> {

    public PlayerFurnacesList furnaces = new PlayerFurnacesList();
    private LazyOptional<PlayerFurnacesList> lazyList = LazyOptional.of(() -> furnaces);

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == CapabilityPlayerFurnacesList.FURNACES_LIST ? lazyList.cast() : LazyOptional.empty();
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        return cap == CapabilityPlayerFurnacesList.FURNACES_LIST ? lazyList.cast() : LazyOptional.empty();
    }


    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.put("furnace_data", PlayerFurnacesList.CODEC.encode(this.furnaces, NbtOps.INSTANCE, tag)
                .result()
                .get());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        DataResult<Pair<PlayerFurnacesList, Tag>> result = PlayerFurnacesList.CODEC.decode(NbtOps.INSTANCE, tag.get("furnace_data"));

        if (result.error().isPresent()) {
            IronFurnaces.LOGGER.warn(
                    "Failed to read player furnace data, fallback to old deserialization: {}",
                    result.error().get().message()
            );

            int size = tag.getInt("count");
            CompoundTag furances = tag.getCompound("furnaces");
            for (int i = 0; i < size; i++)
            {
                CompoundTag furance = furances.getCompound("furnace" + i);
                BlockPos pos = new BlockPos(furance.getInt("X"), furance.getInt("Y"), furance.getInt("Z"));
                furnaces.add(Level.OVERWORLD, pos);
            }
        } else {
            this.furnaces = result.result().get().getFirst();
        }
    }
}
