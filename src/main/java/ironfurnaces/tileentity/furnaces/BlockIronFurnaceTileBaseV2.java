package ironfurnaces.tileentity.furnaces;

import ironfurnaces.Config;
import ironfurnaces.adaptor.energy.EnergyWrapper;
import ironfurnaces.recipes.GeneratorRecipe;
import ironfurnaces.tileentity.furnaces.cache.*;
import ironfurnaces.tileentity.furnaces.handler.IFurnaceLitHandler;
import ironfurnaces.tileentity.furnaces.process.Burn;
import ironfurnaces.tileentity.furnaces.process.Generate;
import ironfurnaces.tileentity.furnaces.process.ProcessingInstance;
import ironfurnaces.tileentity.furnaces.process.ProcessingInstanceManager;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeHooks;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

@Getter
public abstract class BlockIronFurnaceTileBaseV2 extends BaseContainerBlockEntity implements RecipeHolder, StackedContentsCompatible, WorldlyContainer {

    private final InputCache input;
    private final OutputCache output;
    private final FuelCache fuel;
    private final RemainingCache remaining;
    private final AugmentCache augments;
    private ProcessingInstanceManager instanceManager;
    @Setter
    private FurnaceMode mode;
    private IFurnaceLitHandler litHandler;
    private final Function<RecipeType<?>, RecipeManager.CachedCheck<Container, ?>> quickCheck;

    protected BlockIronFurnaceTileBaseV2(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        this.mode = FurnaceMode.FURNACE;
        this.input = new InputCache(6, this.mode, getTier(), null);
        this.output = new OutputCache(6, this.mode, getTier(), x -> {
            this.instanceManager.refreshBlockingState();
        });
        this.fuel = new FuelCache(new EnergyWrapper(Config.furnaceEnergyCapacityTier2.get()).withCallback(fEnergyStorage -> setChanged()), mode);
        this.remaining = new RemainingCache();
        this.augments = new AugmentCache(this::updateFurnaceMode);
        this.instanceManager = new ProcessingInstanceManager(new ArrayList<>(), this);
        this.litHandler = this.mode.litHandlerSelector.apply(this);
        this.quickCheck = Util.memoize(x -> RecipeManager.createCheck((RecipeType)x));
    }

    public BlockIronFurnaceTileBaseV2(BlockEntityType<?> type, BlockPos pos, BlockState blockState, FurnaceMode mode) {
        this(type, pos, blockState);
        this.mode = mode;
    }

    public abstract ForgeConfigSpec.IntValue getTier();
    public abstract String getId();

    private void updateFurnaceMode(FurnaceMode mode){
        this.mode = mode;
        input.updateFurnaceMode(mode);
        input.dropStacksInUnavailableSlots(this.level, this.getBlockPos());
        output.updateFurnaceMode(mode);
        instanceManager.updateFurnaceMode(mode);
        fuel.updateFurnaceMode(mode);
    }

    public abstract int getGenerationPerTick();

    @Override
    public @Nullable Recipe<?> getRecipeUsed() {
        return null;
    }

    @Override
    public void setRecipeUsed(@Nullable Recipe<?> recipe) {

    }

    @Override
    public void fillStackedContents(StackedContents contents) {
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, BlockIronFurnaceTileBaseV2 blockEntity) {
        blockEntity.selectLitHandler();
        blockEntity.tryProcessInput();
        blockEntity.litHandler.tick();
        if (blockEntity.litHandler.isLit()){
            blockEntity.getInstanceManager().manage();
        }
    }

    private void selectLitHandler(){
        this.litHandler = this.mode.litHandlerSelector.apply(this);
    }


    private void tryProcessInput(){
        for (int i = 0; i < input.getSlots(); i++) {
            if (!input.isSlotIdle(i)) continue;
            ItemStack stackInSlot = input.getStackInSlot(i);
            int finalI = i;
            quickCheck.apply(augments.getCurrentRecipeType()).getRecipeFor(new SimpleContainer(stackInSlot), level)
                    .ifPresent(x -> {
                        if (this.mode.equals(FurnaceMode.GENERATOR)){
                            int actualPerTickGeneration = augments.getCurrentModifiers().generateOutputModifier().applyAsInt(getGenerationPerTick());
                            if (x instanceof GeneratorRecipe generatorRecipe){
                                this.instanceManager.addInstance(new Generate.BlastGenerate(generatorRecipe.getEnergy(), generatorRecipe, stackInSlot, actualPerTickGeneration, augments));
                            } else {
                                this.instanceManager.addInstance(new Generate.SmeltGenerate(ForgeHooks.getBurnTime(stackInSlot, x.getType()) * getGenerationPerTick(), x, stackInSlot, actualPerTickGeneration, augments));
                            }
                        } else {
                            if (x instanceof AbstractCookingRecipe cookingRecipe){
                                this.instanceManager.addInstance(Burn.create(augments.getCurrentModifiers().normalWorkTimeModifier().applyAsInt(cookingRecipe.getCookingTime()), finalI, x, input, this.level, augments));
                            }
                        }
                    });
        }
    }

    public record FurnaceProperties(String id, int tier, int generationPerTick){}

}
