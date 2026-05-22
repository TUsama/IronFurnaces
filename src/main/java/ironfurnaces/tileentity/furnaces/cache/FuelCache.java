//~ replace_INBTSerializable
package ironfurnaces.tileentity.furnaces.cache;

import ironfurnaces.adaptor.energy.FEnergyStorage;
import ironfurnaces.items.ItemHeater;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.pattern.IFurnaceStats;
import ironfurnaces.tileentity.furnaces.pattern.mode.AbstractFurnaceModeHandler;
import ironfurnaces.tileentity.furnaces.pattern.mode.FurnaceModeManager;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.function.Consumer;
import java.util.function.Predicate;

@Accessors(fluent = true, chain = true)
public class FuelCache extends ResizableCache implements EnergyHandler {
    @Getter
    private FEnergyStorage energy;
    @Setter
    private Predicate<ItemStack> burnableFunction;
    @Setter
    private Consumer<FuelCache> contentChangeCallback;

    public FuelCache(AbstractFurnaceModeHandler mode, FEnergyStorage energy) {
        super(mode);
        this.energy = energy;
    }

    @Override
    public boolean isValid(int index, ItemResource resource) {
        ItemStack stack = resource.toStack();
        return burnableFunction.test(stack) || stack.getItem() instanceof ItemHeater;
    }

    @Override
    protected void onContentsChanged(int index, ItemStack previousContents) {
        if (contentChangeCallback != null) {
            contentChangeCallback.accept(this);
        }
        recomputeFillStats();
    }

    @Override
    public void update(AbstractFurnaceModeHandler mode, IRecipeTypeHandler recipeTypeHandler, IFurnaceStats<?> stats, FurnacePatternBlockEntity blockEntity) {
        super.update(mode, recipeTypeHandler, stats, blockEntity);
        this.energy.setCapacity(stats.energyCapacity());
        this.energy.setMaxTransfer(stats.energyCapacity());
    }

    @Override
    protected int updateSlotAmount(AbstractFurnaceModeHandler mode, IRecipeTypeHandler recipeTypeHandler, IFurnaceStats<?> stats, FurnacePatternBlockEntity blockEntity) {
        return FurnaceModeManager.INSTANCE.getMaxFuelSlot(stats);
    }

    @Override
    public void serialize(ValueOutput output) {
        super.serialize(output);
        energy.serialize(output);
    }

    @Override
    public void deserialize(ValueInput input) {
        super.deserialize(input);
        energy.deserialize(input);
    }


    @Override
    public long getAmountAsLong() {
        return energy.getAmountAsLong();
    }

    @Override
    public long getCapacityAsLong() {
        return energy.getCapacityAsLong();
    }

    @Override
    public int insert(int amount, TransactionContext transaction) {
        return energy.insert(amount, transaction);
    }

    @Override
    public int extract(int amount, TransactionContext transaction) {
        return energy.extract(amount, transaction);
    }
}
