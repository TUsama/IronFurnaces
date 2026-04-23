package ironfurnaces.network;

import com.clefal.nirvana_lib.network.newtoolchain.C2SModPacket;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.cache.recipe_type_handlers.FarmerDelightCookingRecipeTypeHandler;
import ironfurnaces.tileentity.furnaces.menu.FurnacePatternMenu;
import ironfurnaces.tileentity.furnaces.menu.handler.FDMenuHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;

public class C2SLockedRecipePacket implements C2SModPacket<C2SLockedRecipePacket> {

    private BlockPos blockPos;

    public C2SLockedRecipePacket() {
    }

    public C2SLockedRecipePacket(BlockPos blockPos) {
        this.blockPos = blockPos;
    }

    @Override
    public void handleServer(ServerPlayer serverPlayer, C2SLockedRecipePacket c2SLockedRecipePacket, boolean b) {
        BlockEntity blockEntity = serverPlayer.level().getBlockEntity(blockPos);
        if (blockEntity instanceof FurnacePatternBlockEntity patternBlockEntity){
            var recipeType = patternBlockEntity.getAugments().getCurrentRecipeType();

            if (recipeType instanceof FarmerDelightCookingRecipeTypeHandler handler){
                if (handler.isLocking) {
                    handler.setLockedRecipe(null);
                } else {
                    handler.setLockedToLastRecipe();
                    handler.sortByCurrentLockedRecipe(patternBlockEntity.getInput());
                }

            }
        }

    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeBlockPos(blockPos);
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) {
        this.blockPos = friendlyByteBuf.readBlockPos();
    }

    @Override
    public Class<C2SLockedRecipePacket> getSelfClass() {
        return C2SLockedRecipePacket.class;
    }
}
