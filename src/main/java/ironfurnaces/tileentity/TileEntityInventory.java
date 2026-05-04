//? <1.21.11{
//~ replace_block_entity
//~ replace_all_recipe
package ironfurnaces.tileentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemHandlerHelper;
//? 1.20.1 {

//? } else {
import net.minecraft.core.HolderLookup;
        //?}
import javax.annotation.Nullable;

public abstract class TileEntityInventory extends BlockEntity implements ITileInventory, WorldlyContainer, MenuProvider, Nameable {

    public NonNullList<ItemStack> inventory;
    protected Component name;

    public TileEntityInventory(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state, int sizeInventory) {
        super(tileEntityTypeIn, pos, state);
        inventory = NonNullList.withSize(sizeInventory, ItemStack.EMPTY);
    }



    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        setChanged();
        return ClientboundBlockEntityDataPacket.create(this);
    }
    /*
        @Override
        public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
            CompoundTag tag = pkt.getTag();
            load(tag);
            setChanged();
            level.markAndNotifyBlock(worldPosition, level.getChunkAt(worldPosition), level.getBlockState(worldPosition).getBlock().defaultBlockState(), level.getBlockState(worldPosition), 2, 3);

        }
    */
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        //? 1.20.1 {
        /*CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        *///? } else {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries);
        //?}
        return tag;
    }


    public void setCustomName(Component name) {
        this.name = name;
    }

    @Override
    public Component getName() {
        return (this.name != null ? this.name : Component.translatable(IgetName()));
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return this.IgetSlotsForFace(side);
    }

    @Override
    public boolean canPlaceItem(int i, ItemStack itemStack) {
        return IisItemValidForSlot(i, itemStack);
    }

    @Override
    public boolean canPlaceItemThroughFace(int i, ItemStack itemStack, @Nullable Direction direction) {
        return IisItemValidForSlot(i, itemStack);
    }

    @Override
    public boolean canTakeItemThroughFace(int i, ItemStack itemStack, Direction direction) {
        return IcanExtractItem(i, itemStack, direction);
    }

    @Override
    public int getContainerSize() {
        return this.inventory.size();
    }

    @Override
    public boolean isEmpty() {
        for(ItemStack itemstack : this.inventory) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.inventory.get(slot);
    }

    @Override
    public ItemStack removeItem(int i, int i1) {
        return ContainerHelper.removeItem(this.inventory, i, i1);
    }

    @Override
    public ItemStack removeItemNoUpdate(int i) {
        return ContainerHelper.takeItem(this.inventory, i);
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        ItemStack itemstack = this.inventory.get(index);
        boolean flag = !stack.isEmpty() && ItemStack.isSameItemSameComponents(itemstack, stack);
        this.inventory.set(index, stack);
        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }
    }




    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.inventory = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.inventory, registries);

        if (tag.contains("CustomName", 8)) {
            //~ if >1.20.1 'tag.getString("CustomName")' -> 'tag.getString("CustomName"), registries'
            this.name = Component.Serializer.fromJson(tag.getString("CustomName"), registries);
        }
    }



    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (this.name != null) {
            //~ if >1.20.1 'this.name' -> 'this.name, registries'
            tag.putString("CustomName", Component.Serializer.toJson(this.name, registries));
        }
        ContainerHelper.saveAllItems(tag, this.inventory, registries);
    }

    @Override
    public boolean stillValid(Player playerEntity) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return !(playerEntity.distanceToSqr((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D) > 64.0D);
        }
    }

    @Override
    public boolean hasCustomName() {
        return this.name != null;
    }

    @Nullable
    @Override
    public Component getCustomName() {
        return this.name;
    }

    @Override
    public Component getDisplayName() {
        return this.getName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return IcreateMenu(i, inventory, player);
    }

    @Override
    public void clearContent() {
        this.inventory.clear();

    }
}

//?}