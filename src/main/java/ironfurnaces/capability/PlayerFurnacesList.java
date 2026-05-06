//~ replace_INBTSerializable
package ironfurnaces.capability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.common.util.ValueIOSerializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;

public class PlayerFurnacesList implements IPlayerFurnacesList, ValueIOSerializable {
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

    private static final String KEY_DATA = "Data";

    @Override
    public void serialize(ValueOutput output) {
        output.store(KEY_DATA, CODEC, this);
    }

    @Override
    public void deserialize(ValueInput input) {
        input.read(KEY_DATA, CODEC)
                .ifPresent(decoded -> {
                    this.posLinkedHashSet.clear();
                    this.posLinkedHashSet.addAll(decoded.posLinkedHashSet);
                });
    }
}