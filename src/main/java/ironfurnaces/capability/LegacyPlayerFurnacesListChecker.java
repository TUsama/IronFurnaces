package ironfurnaces.capability;

import ironfurnaces.tileentity.furnaces.BlockIronFurnaceTileBase;
import ironfurnaces.tileentity.furnaces.BlockIronFurnaceTileBaseV2;
import lombok.experimental.UtilityClass;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.HashSet;

@UtilityClass
public class LegacyPlayerFurnacesListChecker {
    public void validateFurnacesList(ServerPlayer player, ServerLevel level) {
        player.getCapability(CapabilityPlayerFurnacesList.FURNACES_LIST).ifPresent(list -> {
            list.whenUpgradeFromLegacy(fList -> {
                if (level.dimension().equals(Level.OVERWORLD)) return;
                for (GlobalPos globalPos : new HashSet<>(fList.get())) {
                    BlockEntity blockEntity = level.getBlockEntity(globalPos.pos());
                    if (!(blockEntity instanceof BlockIronFurnaceTileBase || blockEntity instanceof BlockIronFurnaceTileBaseV2)){
                        fList.remove(globalPos.dimension(), globalPos.pos());
                    }
                }
                fList.resetUpgradeMark();
            });

        });
    }
}
