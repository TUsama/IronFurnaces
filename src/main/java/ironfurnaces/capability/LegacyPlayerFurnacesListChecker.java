package ironfurnaces.capability;

import ironfurnaces.tileentity.furnaces.BlockIronFurnaceTileBase;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import lombok.experimental.UtilityClass;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.HashSet;

@UtilityClass
public class LegacyPlayerFurnacesListChecker {
    public void validateFurnacesList(ServerPlayer player, ServerLevel level) {
        PlayerDataHandler.editFurnacesList(player, list -> {
            list.whenUpgradeFromLegacy(fList -> {
                if (!level.dimension().equals(Level.OVERWORLD)) return;
                for (GlobalPos globalPos : new HashSet<>(fList.get())) {
                    BlockPos pos = globalPos.pos();
                    level.getChunkAt(pos).setLoaded(true);
                    BlockEntity blockEntity = level.getBlockEntity(pos);
                    if (!(blockEntity instanceof BlockIronFurnaceTileBase || blockEntity instanceof FurnacePatternBlockEntity)) {
                        fList.remove(globalPos.dimension(), pos);
                    }
                }
                fList.resetUpgradeMark();
            });

        });
    }
}
