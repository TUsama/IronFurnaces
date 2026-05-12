//~ replace_serialization
package ironfurnaces.tileentity.heater;

import ironfurnaces.adaptor.energy.FEnergyStorage;
import ironfurnaces.config.GameplayConfig;
import ironfurnaces.items.ItemHeater;
import ironfurnaces.registration.ModBlocks;
import ironfurnaces.registration.ModItems;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

public class BlockWirelessEnergyHeaterTile extends BaseContainerBlockEntity {
    @Getter
    private final FEnergyStorage energy;
    private final ItemStacksResourceHandler items;

    public BlockWirelessEnergyHeaterTile(BlockPos pos, BlockState state) {
        this(ModBlocks.asGenericBlockEntityType(ModBlocks.HEATER), pos, state);
    }

    public BlockWirelessEnergyHeaterTile(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
        this.energy = new FEnergyStorage(GameplayConfig.config.heater_capacity.get(), GameplayConfig.config.heater_capacity.get(), 0).callback(fEnergyStorage -> setChanged());
        this.items = new ItemStacksResourceHandler(1) {
            @Override
            public boolean isValid(int index, ItemResource resource) {
                return resource.is(ModItems.ITEM_HEATER.asItem());
            }
        };
    }

    public static void tick(Level level, BlockPos worldPosition, BlockState blockState, BlockWirelessEnergyHeaterTile e) {
        ItemStack stack = e.getItem(0);
        if (!stack.isEmpty() && stack.getItem() instanceof ItemHeater) {
            ItemHeater.writeBoundBlockPos(stack, worldPosition);
        }

    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.ironfurnaces.wireless_energy_heater");
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.items.copyToList();
    }

    @Override
    protected void setItems(NonNullList<ItemStack> nonNullList) {
        for (int i = 0; i < this.items.size(); i++) {
            if (nonNullList.size() - 1 > i) this.items.setAsEmpty(i);
            this.items.set(i, ItemResource.of(nonNullList.get(i)), nonNullList.get(i).getCount());
        }
    }

    @Override
    protected AbstractContainerMenu createMenu(int i, Inventory inventory) {
        return null;
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        energy.deserialize(input);
        this.items.deserialize(input);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        energy.serialize(output);
        this.items.serialize(output);
    }


    @Override
    public int getContainerSize() {
        return this.items.size();
    }
}
