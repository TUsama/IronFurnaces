//? forge {

/*package ironfurnaces.capability.rainbow;

import ironfurnaces.capability.ModCapabilities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.common.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.common.capabilities.ICapabilitySerializable;
import net.neoforged.neoforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerRainbowContextCapability implements ICapabilityProvider, ICapabilitySerializable<CompoundTag> {

    private final OwnerRainbowContext context = new OwnerRainbowContext();
    private LazyOptional<OwnerRainbowContext> lazyList = LazyOptional.of(() -> context);

    public OwnerRainbowContext context() {
        return context;
    }

    public void tick(ServerPlayer player) {
        context.tick(player);
    }

    public CompoundTag save() {
        return context.saveToTag();
    }

    public void load(CompoundTag tag) {
        context.loadFromTag(tag);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == ModCapabilities.PLAYER_RAINBOW_CONTEXT ? lazyList.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return save();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        load(nbt);
    }
}
*///?}