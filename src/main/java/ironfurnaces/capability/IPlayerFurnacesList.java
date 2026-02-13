package ironfurnaces.capability;

import net.minecraft.core.BlockPos;

import java.util.List;

public interface IPlayerFurnacesList {

    List<BlockPos> get();

    void add(BlockPos pos);

    void remove(BlockPos pos);

}
