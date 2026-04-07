//~ replace_INBTSerializable
package ironfurnaces.capability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;
//? 1.20.1 {

//? } else {
/*import net.minecraft.core.HolderLookup;
*///?}
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;

public class PlayerFurnacesList implements IPlayerFurnacesList, INBTSerializable<CompoundTag> {
    public static final Codec<PlayerFurnacesList> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            GlobalPos.CODEC.listOf().xmap(LinkedHashSet::new, ArrayList::new).fieldOf("furnaces").forGetter(PlayerFurnacesList::getPosLinkedHashSet)

    ).apply(instance, PlayerFurnacesList::new));
    @Getter(AccessLevel.PRIVATE)
    private LinkedHashSet<GlobalPos> posLinkedHashSet;
    private boolean upgradeFromLegacy = false;

    public PlayerFurnacesList() {
        this.posLinkedHashSet = new LinkedHashSet<>();
    }

    private PlayerFurnacesList(LinkedHashSet<GlobalPos> posLinkedHashSet) {
        this.posLinkedHashSet = new LinkedHashSet<>(posLinkedHashSet);
    }

    @Override
    public Set<GlobalPos> get() {
        return Collections.unmodifiableSet(posLinkedHashSet);
    }

    @Override
    public void add(ResourceKey<Level> key, BlockPos pos) {
        this.posLinkedHashSet.add(GlobalPos.of(key, pos));
    }

    @Override
    public void remove(ResourceKey<Level> key, BlockPos pos) {
        this.posLinkedHashSet.remove(GlobalPos.of(key, pos));
    }

    @Override
    public void whenUpgradeFromLegacy(Consumer<IPlayerFurnacesList> handler) {
        if (upgradeFromLegacy) {
            handler.accept(this);
        }
    }

    @Override
    public void resetUpgradeMark() {
        this.upgradeFromLegacy = false;
    }


    public boolean isEmpty() {
        return this.posLinkedHashSet.isEmpty();
    }

    public void setAsLegacy() {
        this.upgradeFromLegacy = true;
    }

    public void clear() {
        this.posLinkedHashSet.clear();
        this.upgradeFromLegacy = false;
    }

    public void copyFrom(PlayerFurnacesList other) {
        this.posLinkedHashSet.clear();
        this.posLinkedHashSet.addAll(other.posLinkedHashSet);
        this.upgradeFromLegacy = other.upgradeFromLegacy;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compoundTag = new CompoundTag();
        CODEC.encodeStart(NbtOps.INSTANCE, this).result().ifPresent(x -> compoundTag.put("Data", x));
        return compoundTag;
    }

    @Override
    public void deserializeNBT(CompoundTag compoundTag) {
        if (compoundTag.contains("Data")){
            CODEC.decode(NbtOps.INSTANCE, compoundTag).result().ifPresent(x -> this.posLinkedHashSet.addAll(x.getFirst().posLinkedHashSet));
        }
    }
}