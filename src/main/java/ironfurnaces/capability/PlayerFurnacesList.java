package ironfurnaces.capability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class PlayerFurnacesList implements IPlayerFurnacesList {
    public static final Codec<PlayerFurnacesList> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    GlobalPos.CODEC.listOf().xmap(LinkedHashSet::new, ArrayList::new).fieldOf("furnaces").forGetter(x -> x.posLinkedHashSet)
            ).apply(instance, PlayerFurnacesList::new)
    );

    private LinkedHashSet<GlobalPos> posLinkedHashSet;
    private boolean upgradeFromLegacy = false;

    public PlayerFurnacesList() {
        posLinkedHashSet = new LinkedHashSet<>();
    }

    private PlayerFurnacesList(LinkedHashSet<GlobalPos> posLinkedHashSet) {
        this.posLinkedHashSet = posLinkedHashSet;
    }

    @Override
    public Set<GlobalPos> get() {
        return posLinkedHashSet;
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
        if (upgradeFromLegacy){
            handler.accept(this);
        }
    }

    @Override
    public void resetUpgradeMark() {
        this.upgradeFromLegacy = false;
    }

    public boolean isEmpty(){
        return this.posLinkedHashSet.isEmpty();
    }

    public void setAsLegacy(){
        this.upgradeFromLegacy = true;
    }

}
