//? <1.21.11{
package ironfurnaces.tileentity.furnaces;

import ironfurnaces.container.furnaces.LegacyUnifiedMenu;
import ironfurnaces.registration.ModMenus;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ModConfigSpec;

public class UnifiedTileEntity extends BlockIronFurnaceTileBase{
    private ModConfigSpec.IntValue furnaceSpeed;
    @Getter
    private String identifier;
    private ModConfigSpec.IntValue tier;
    private ModConfigSpec.IntValue generationPerTick;

    public UnifiedTileEntity(BlockEntityType<?> tileentitytypeIn, BlockPos pos, BlockState state, ModConfigSpec.IntValue furnaceSpeed, ModConfigSpec.IntValue tier, ModConfigSpec.IntValue generationPerTick, String identifier) {
        super(tileentitytypeIn, pos, state);
        this.furnaceSpeed = furnaceSpeed;
        this.identifier = identifier;
        this.tier = tier;
        this.generationPerTick = generationPerTick;
    }


    @Override
    public ModConfigSpec.IntValue getCookTimeConfig() {
        return furnaceSpeed;
    }

    public int getGenerationPerTick(){
        return generationPerTick.get();
    }

    public int getMaxSmeltItemNumberOnSingleOp(){
        return 1;
    }

    public static boolean isRainbow(BlockEntity blockEntity){
        return blockEntity instanceof UnifiedTileEntity && ((UnifiedTileEntity) blockEntity).isRainbowFurnace();
    }

    @Override
    public String IgetName() {
        return "container.ironfurnaces." + identifier;
    }

    @Override
    public AbstractContainerMenu IcreateMenu(int i, Inventory playerInventory, Player playerEntity) {
        return new LegacyUnifiedMenu(ModMenus.UNIFIED_MENU.get(), i, playerEntity.level(), worldPosition, playerEntity.getInventory(), playerEntity);
    }

    @Override
    public int getTier() {
        return tier.get();
    }
}

//?}