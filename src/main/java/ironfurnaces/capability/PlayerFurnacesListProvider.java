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

    private PlayerFurnacesList furnaces = new PlayerFurnacesList();
    private LazyOptional<PlayerFurnacesList> lazyList = LazyOptional.of(() -> furnaces);

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == ModCapabilities.FURNACES_LIST ? lazyList.cast() : LazyOptional.empty();
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        return cap == ModCapabilities.FURNACES_LIST ? lazyList.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        PlayerFurnacesList.CODEC.encodeStart(NbtOps.INSTANCE, this.furnaces)
                .result()
                .ifPresent(nbt -> tag.put("furnace_data", nbt));
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        this.furnaces.clear();

        if (!tag.contains("furnace_data")) {
            deserializeLegacy(tag);
            rebuildLazyOptional();
            return;
        }

        DataResult<Pair<PlayerFurnacesList, Tag>> result =
                PlayerFurnacesList.CODEC.decode(NbtOps.INSTANCE, tag.get("furnace_data"));

        if (result.error().isPresent()) {
            IronFurnaces.LOGGER.warn(
                    "Failed to read player furnace data, fallback to old deserialization: {}",
                    result.error().get().message()
            );
            deserializeLegacy(tag);
            rebuildLazyOptional();
            return;
        }

        PlayerFurnacesList decoded = result.result().orElseThrow().getFirst();
        this.furnaces.copyFrom(decoded);
        rebuildLazyOptional();
    }

    private void deserializeLegacy(CompoundTag tag) {
        int size = tag.getInt("count");
        CompoundTag furnacesTag = tag.getCompound("furnaces");
        for (int i = 0; i < size; i++) {
            CompoundTag furnace = furnacesTag.getCompound("furnace" + i);
            BlockPos pos = new BlockPos(furnace.getInt("X"), furnace.getInt("Y"), furnace.getInt("Z"));
            furnaces.add(Level.OVERWORLD, pos);
        }
        this.furnaces.setAsLegacy();
    }

    private void rebuildLazyOptional() {
        lazyList.invalidate();
        lazyList = LazyOptional.of(() -> furnaces);
    }
}