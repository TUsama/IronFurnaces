package ironfurnaces.capability;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface IPlayerFurnacesList {

    Set<GlobalPos> get();

    void add(ResourceKey<Level> key, BlockPos pos);

    void remove(ResourceKey<Level> key, BlockPos pos);

}
