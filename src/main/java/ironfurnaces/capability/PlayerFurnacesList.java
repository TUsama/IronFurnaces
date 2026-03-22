package ironfurnaces.capability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;

public class PlayerFurnacesList implements IPlayerFurnacesList {
    public static final Codec<PlayerFurnacesList> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            GlobalPos.CODEC.listOf()
                    .xmap(LinkedHashSet::new, ArrayList::new)
                    .fieldOf("furnaces")
                    .forGetter(x -> x.posLinkedHashSet)
    ).apply(instance, PlayerFurnacesList::new));

    private LinkedHashSet<GlobalPos> posLinkedHashSet;
    private boolean upgradeFromLegacy = false;
    private boolean updated = false;

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
        this.updated = true;
    }

    @Override
    public void remove(ResourceKey<Level> key, BlockPos pos) {
        this.posLinkedHashSet.remove(GlobalPos.of(key, pos));
        this.updated = true;
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

    @Override
    public boolean isUpdated() {
        return updated;
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
}