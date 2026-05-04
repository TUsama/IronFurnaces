//? <1.21.11{
/*//~ replace_tile
//~ replace_block_entity
package ironfurnaces.tileentity.furnaces;

import com.clefal.nirvana_lib.utils.ModUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import harmonised.pmmo.api.events.FurnaceBurnEvent;
import harmonised.pmmo.events.impl.FurnaceHandler;
import ironfurnaces.Config;
import ironfurnaces.adaptor.energy.EnergyWrapper;
import ironfurnaces.blocks.furnaces.BlockMillionFurnace;
import ironfurnaces.adaptor.energy.FEnergyStorage;
import ironfurnaces.capability.PlayerFurnacesList;
import ironfurnaces.capability.VanillaCapabilityHandler;
import ironfurnaces.capability.PlayerDataHandler;
import ironfurnaces.items.ItemHeater;
import ironfurnaces.items.augments.*;
import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.recipes.GeneratorRecipe;
import ironfurnaces.registration.ModBlockState;
import ironfurnaces.registration.ModItems;
import ironfurnaces.registration.ModCustomRecipe;
import ironfurnaces.tileentity.heater.BlockWirelessEnergyHeaterTile;
import ironfurnaces.tileentity.TileEntityInventory;
import ironfurnaces.util.DirectionUtil;
import ironfurnaces.util.FuelBurnTimeUtil;
import ironfurnaces.util.FurnaceSettings;
import ironfurnaces.util.LRUCache;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;


import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.AirItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.items.IItemHandler;
//? forge {
/^import net.minecraft.world.inventory.RecipeHolder;
import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.common.capabilities.ForgeCapabilities;
import net.neoforged.neoforge.common.util.LazyOptional;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import net.neoforged.neoforge.registries.ForgeRegistries;
import net.minecraft.world.SimpleContainer;
^///?} else {
import net.minecraft.world.inventory.RecipeCraftingHolder;
//?}
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiPredicate;

import static ironfurnaces.init.ModSetup.SMOKING_BURNS;
import static ironfurnaces.init.ModSetup.HAS_RECIPE;
import static ironfurnaces.init.ModSetup.HAS_RECIPE_SMOKING;
import static ironfurnaces.init.ModSetup.HAS_RECIPE_BLASTING;

public abstract class BlockIronFurnaceTileBase extends TileEntityInventory implements
        //? >1.20.1 {
        RecipeCraftingHolder,
//? } else {
        /^RecipeHolder,
         ^///? }
        StackedContentsCompatible {

    public static final int INPUT = 0;
    public static final int FUEL = 1;
    public static final int OUTPUT = 2;
    public static final int AUGMENT_RED = 3;
    public static final int AUGMENT_GREEN = 4;
    public static final int AUGMENT_BLUE = 5;
    public static final int GENERATOR_FUEL = 6;
    public static final int[] FACTORY_INPUT = new int[]{7, 8, 9, 10, 11, 12};
    //public Player savedPlayer;

    public final int[] provides = new int[Direction.values().length];
    protected final int[] lastProvides = new int[this.provides.length];
    public int jovial;
    public int[] currentAugment = new int[3];
    public int[] factoryCookTime = new int[6];
    public int[] factoryTotalCookTime = new int[6];
    public double[] usedRF = new double[6];
    public double generatorBurn;
    public int generatorRecentRecipeRF;
    public double gottenRF;
    public int furnaceBurnTime;
    public int cookTime;
    public int totalCookTime;
    public int recipesUsed;

    public long lastGameTickEnergyUpdated;

    public UUID owner;

    public boolean rainbowGenerating;


    public RecipeType<? extends AbstractCookingRecipe> recipeType;
    public FurnaceSettings furnaceSettings;
    //~ if >1.20.1 '(Config.cache_capacity.get())' -> '(10)' {
    public LRUCache<Item, Optional<RecipeHolder<AbstractCookingRecipe>>> cache = LRUCache.newInstance(10);
    public LRUCache<Item, Optional<RecipeHolder<AbstractCookingRecipe>>> blasting_cache = LRUCache.newInstance(10);
    public LRUCache<Item, Optional<RecipeHolder<AbstractCookingRecipe>>> smoking_cache = LRUCache.newInstance(10);
    public LRUCache<Item, Optional<GeneratorRecipe>> generator_cache = LRUCache.newInstance(10);
    public List<LRUCache<Item, Optional<RecipeHolder<AbstractCookingRecipe>>>> factory_cache = Lists.newArrayList(
            LRUCache.newInstance(10),
            LRUCache.newInstance(10),
            LRUCache.newInstance(10),
            LRUCache.newInstance(10),
            LRUCache.newInstance(10),
            LRUCache.newInstance(10));

    public List<LRUCache<Item, Optional<RecipeHolder<AbstractCookingRecipe>>>> factory_blasting_cache = Lists.newArrayList(
            LRUCache.newInstance(10),
            LRUCache.newInstance(10),
            LRUCache.newInstance(10),
            LRUCache.newInstance(10),
            LRUCache.newInstance(10),
            LRUCache.newInstance(10));

    public List<LRUCache<Item, Optional<RecipeHolder<AbstractCookingRecipe>>>> factory_smoking_cache = Lists.newArrayList(
            LRUCache.newInstance(10),
            LRUCache.newInstance(10),
            LRUCache.newInstance(10),
            LRUCache.newInstance(10),
            LRUCache.newInstance(10),
            LRUCache.newInstance(10));

    //~}
    public EnergyWrapper energyStorage = new EnergyWrapper(Config.furnaceEnergyCapacityTier2.get()).withCallback(fEnergyStorage -> {
        if (level != null && level.getBlockEntity(getBlockPos()) != null) {
            if (lastGameTickEnergyUpdated <= 0) {
                setChanged();
                lastGameTickEnergyUpdated = level.getGameTime();
            } else if (level.getGameTime() - lastGameTickEnergyUpdated >= 20) {
                setChanged();
                lastGameTickEnergyUpdated = level.getGameTime();
            }
        }
    });


    public BlockIronFurnaceTileBase(BlockEntityType<?> tileentitytypeIn, BlockPos pos, BlockState state) {
        super(tileentitytypeIn, pos, state, 19);
        recipeType = RecipeType.SMELTING;
        furnaceSettings = new FurnaceSettings() {
            @Override
            public void onChanged() {
                setChanged();
            }
        };


    }


    public int getEnergy() {
        return energyStorage.getMaxEnergyStored();
    }

    public int getCapacity() {
        return energyStorage.getMaxEnergyStored();
    }


    public void setMaxEnergy(int energy) {
        energyStorage.setCapacity(energy);
    }

    public void removeEnergy(int energy) {
        energyStorage.extractEnergy(energy, false);
    }

    public boolean hasRecipe(ItemStack stack) {

        Item item = stack.getItem();
        if (recipeType == RecipeType.SMOKING) {
            return HAS_RECIPE_SMOKING.computeIfAbsent(BuiltInRegistries.ITEM.getHolderOrThrow(item.builtInRegistryHolder().getKey()), (value) -> this.level.getRecipeManager().getRecipeFor((RecipeType<AbstractCookingRecipe>) recipeType, new SingleRecipeInput(stack), this.level).isPresent());
        } else if (recipeType == RecipeType.BLASTING) {
            return HAS_RECIPE_BLASTING.computeIfAbsent(BuiltInRegistries.ITEM.getHolderOrThrow(item.builtInRegistryHolder().getKey()), (value) -> this.level.getRecipeManager().getRecipeFor((RecipeType<AbstractCookingRecipe>) recipeType, new SingleRecipeInput(stack), this.level).isPresent());

        }
        return HAS_RECIPE.computeIfAbsent(BuiltInRegistries.ITEM.getHolderOrThrow(item.builtInRegistryHolder().getKey()), (value) -> this.level.getRecipeManager().getRecipeFor((RecipeType<AbstractCookingRecipe>) recipeType, new SingleRecipeInput(stack), this.level).isPresent());


    }


    public boolean hasGeneratorBlastingRecipe(ItemStack stack) {
        return getRecipeGeneratorBlasting(stack).isPresent();
    }

    protected Optional<RecipeHolder<AbstractCookingRecipe>> getRecipe(ItemStack stack) {
        Optional<RecipeHolder<AbstractCookingRecipe>> recipe = getCache().computeIfAbsent(stack.getItem(), (item) -> (stack.getItem() instanceof AirItem)
                ? Optional.empty()
                : Optional.ofNullable(this.level.getRecipeManager().getRecipeFor((RecipeType<AbstractCookingRecipe>) recipeType, new SingleRecipeInput(stack), this.level).orElse(null)));
        return recipe;
    }

    protected Optional<RecipeHolder<AbstractCookingRecipe>> getRecipeFactory(int slot, ItemStack stack) {
        Optional<RecipeHolder<AbstractCookingRecipe>> recipe = getFactoryCache().get(slot - FACTORY_INPUT[0]).computeIfAbsent(stack.getItem(), (item) -> (stack.getItem() instanceof AirItem)
                ? Optional.empty()
                : Optional.ofNullable(this.level.getRecipeManager().getRecipeFor((RecipeType<AbstractCookingRecipe>) recipeType, new SingleRecipeInput(stack), this.level).orElse(null)));
        return recipe;
    }

    protected Optional<RecipeHolder<AbstractCookingRecipe>> getRecipeNonCached(ItemStack stack) {
        return stack.getItem() instanceof AirItem
                ? Optional.empty()
                : Optional.ofNullable(this.level.getRecipeManager().getRecipeFor((RecipeType<AbstractCookingRecipe>) recipeType, new SingleRecipeInput(stack), this.level).orElse(null));
    }
    //~ if >1.20.1 'recipe.getResultItem' -> 'recipe.value().getResultItem'
    protected Optional<GeneratorRecipe> getRecipeGeneratorBlasting(ItemStack item) {
        return (item.getItem() instanceof AirItem)
                ? Optional.empty()
                : Optional.ofNullable(this.level.getRecipeManager().getRecipeFor(ModCustomRecipe.GENERATOR_RECIPE.get(), new SingleRecipeInput(item), this.level)
                //? >1.20.1
                .map(RecipeHolder::value)
                .orElse(null));
    }

    protected void checkRecipeType() {
        ItemStack stack = this.getItem(AUGMENT_RED);
        if (stack.getItem() instanceof ItemAugmentBlasting) {
            if (recipeType != RecipeType.BLASTING) {
                recipeType = RecipeType.BLASTING;
            }
        }
        if (stack.getItem() instanceof ItemAugmentSmoking) {
            if (recipeType != RecipeType.SMOKING) {
                recipeType = RecipeType.SMOKING;
            }
        }
        if (!(stack.getItem() instanceof ItemAugmentSmoking) && !(stack.getItem() instanceof ItemAugmentBlasting)) {
            if (recipeType != RecipeType.SMELTING) {
                recipeType = RecipeType.SMELTING;
            }
        }
    }

    protected LRUCache<Item, Optional<RecipeHolder<AbstractCookingRecipe>>> getCache() {
        checkRecipeType();
        if (recipeType == RecipeType.BLASTING) {
            return blasting_cache;
        }
        if (recipeType == RecipeType.SMOKING) {
            return smoking_cache;
        }
        return cache;
    }

    protected List<LRUCache<Item, Optional<RecipeHolder<AbstractCookingRecipe>>>> getFactoryCache() {
        checkRecipeType();
        if (recipeType == RecipeType.BLASTING) {
            return factory_blasting_cache;
        }
        if (recipeType == RecipeType.SMOKING) {
            return factory_smoking_cache;
        }
        return factory_cache;
    }

    public int getCookTime() {
        ItemStack stack = this.getItem(AUGMENT_GREEN);
        if (this.getItem(INPUT).getItem() == Items.AIR) {
            return totalCookTime;
        }
        int speed = getSpeed();
        if (!stack.isEmpty()) {
            if (stack.getItem() instanceof ItemAugmentSpeed) {
                speed = Math.max(1, (speed / 2));
            }
            if (stack.getItem() instanceof ItemAugmentFuel) {
                speed = Math.max(1, (int) (Math.ceil(speed * 1.25)));
            }
        }
        return Math.max(1, speed);
    }

    protected int getSpeed() {
        int regular = getCookTimeConfig().get();
        Optional<RecipeHolder<AbstractCookingRecipe>> recipe = getRecipeNonCached(this.getItem(INPUT));
        if (recipe.isPresent()) {
            //~ if >1.20.1 '.get()' -> '.get().value()'
            AbstractCookingRecipe abstractCookingRecipe = recipe.get().value();
            int recipe_cooktime = abstractCookingRecipe.getCookingTime();
            double div = 200.0 / recipe_cooktime;
            double i = regular / div;
            return (int) Math.max(1, i);
        } else {
            return 0;
        }

    }

    protected int getFactoryCookTime(int slot) {
        ItemStack stack = this.getItem(AUGMENT_GREEN);
        if (this.getItem(slot).getItem() == Items.AIR) {
            return factoryTotalCookTime[slot - FACTORY_INPUT[0]];
        }
        int speed = getFactorySpeed(slot);
        if (!stack.isEmpty()) {
            if (stack.getItem() instanceof ItemAugmentSpeed) {
                speed = Math.max(1, (speed / 2));
            }
            if (stack.getItem() instanceof ItemAugmentFuel) {
                speed = Math.max(1, (int) (Math.ceil(speed * 1.25)));
            }
        }
        return Math.max(1, speed);
    }

    protected int getFactorySpeed(int slot) {
        int regular = getCookTimeConfig().get();
        Optional<RecipeHolder<AbstractCookingRecipe>> recipe = getRecipeNonCached(this.getItem(slot));
        if (recipe.isPresent()) {
            //~ if >1.20.1 'recipe.get()' -> 'recipe.get().value()'
            AbstractCookingRecipe abstractCookingRecipe = recipe.get().value();
            int recipe_cooktime = abstractCookingRecipe.getCookingTime();
            double div = 200.0 / recipe_cooktime;
            double i = regular / div;
            return (int) Math.max(1, i);
        } else {
            return 0;
        }
    }

    public abstract ModConfigSpec.IntValue getCookTimeConfig();

    public UnifiedTileEntity self() {
        return ((UnifiedTileEntity) this);
    }


    protected int getAugment(ItemStack stack) {
        if (stack.getItem() instanceof ItemAugmentBlasting) {
            return 1;
        } else if (stack.getItem() instanceof ItemAugmentSmoking) {
            return 2;
        } else if (stack.getItem() instanceof ItemAugmentSpeed) {
            return 1;
        } else if (stack.getItem() instanceof ItemAugmentFuel) {
            return 2;
        } else if (stack.getItem() instanceof ItemAugmentFactory) {
            return 1;
        } else if (stack.getItem() instanceof ItemAugmentGenerator) {
            return 2;
        }
        return 0;
    }

    public void forceUpdateAllStates() {
        BlockState state = level.getBlockState(worldPosition);
        if (state.getValue(BlockStateProperties.LIT) != this.isBurning()) {
            level.setBlock(worldPosition, state.setValue(BlockStateProperties.LIT, this.isBurning()), 3);
        }
        if (state.getValue(ModBlockState.TYPE) != this.getStateType()) {
            level.setBlock(worldPosition, state.setValue(ModBlockState.TYPE, this.getStateType()), 3);
        }
        if (state.getValue(ModBlockState.JOVIAL) != jovial) {
            level.setBlock(worldPosition, state.setValue(ModBlockState.JOVIAL, jovial), 3);
        }
    }

    public void dropContents() {
        for (int i = 0; i <= 18; i++) {
            if (i < 3 || i > 5) {
                ItemStack stack = getItem(i);
                Containers.dropItemStack(level, (double) worldPosition.getX(), (double) worldPosition.getY(), (double) worldPosition.getZ(), stack);
            }
        }
    }

    public int getGeneration() {
        int rf = this.self().getGenerationPerTick();

        return getItem(AUGMENT_GREEN).getItem() instanceof ItemAugmentSpeed ? rf * 2 : getItem(AUGMENT_GREEN).getItem() instanceof ItemAugmentFuel ? (int) (rf * 0.75) : rf;
    }

    public static int getSmokingBurn(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        } else {
            Item item = stack.getItem();
            return SMOKING_BURNS.getOrDefault(BuiltInRegistries.ITEM.getHolderOrThrow(item.builtInRegistryHolder().getKey()), addSmokingBurn(stack));
        }
    }

    public static int addSmokingBurn(ItemStack stack) {
        int burnTime = getSmokingBurnTime(stack);
        Item item = stack.getItem();
        SMOKING_BURNS.put(BuiltInRegistries.ITEM.getHolderOrThrow(item.builtInRegistryHolder().getKey()), burnTime);
        return 0;
    }

    public static int getSmokingBurnTime(ItemStack stack) {
        if (!stack.isEmpty()) {
            FoodProperties foodProperties = stack.getItem().getFoodProperties(
                    //? >= 1.21.1
                    stack, null
            );
            if (foodProperties != null) {
                int i = foodProperties
                        //$ if 1.20.1 '.nutrition();' else '.nutrition();'
                        .nutrition();
                if (i > 0) {
                    return i * 800;
                }
            }
        }
        return 0;
    }

    public int getGeneratorBurn() {
        int burn = 0;
        if (getItem(AUGMENT_RED).getItem() instanceof ItemAugmentSmoking) {
            burn = getSmokingBurn(getItem(GENERATOR_FUEL));
        } else if (getItem(AUGMENT_RED).getItem() instanceof ItemAugmentBlasting) {
            if (!getItem(GENERATOR_FUEL).isEmpty()) {
                int energy = generator_cache.computeIfAbsent(getItem(GENERATOR_FUEL).getItem(), (item) -> getRecipeGeneratorBlasting(new ItemStack(item))).map(GeneratorRecipe::getEnergy).orElse(0);
                burn = energy / 20;
            }
        } else {
            burn = getBurnTime(getItem(GENERATOR_FUEL), RecipeType.SMELTING);
        }
        if (getItem(AUGMENT_GREEN).getItem() instanceof ItemAugmentSpeed) {
            burn /= 2;
        } else if (getItem(AUGMENT_GREEN).getItem() instanceof ItemAugmentFuel) {
            burn *= 2;
        }
        return burn;
    }


    public boolean isFactoryCooking() {
        for (int i = 0; i < factoryCookTime.length; i++) {
            if (factoryCookTime[i] > 0) {
                return true;
            }
        }
        return false;
    }

    // ???????????????? what does this even do
    public Map<Integer, Integer> getSplitCounts(int[] slot, int[] input) {
        if (slot.length != input.length) {
            return null;
        }
        Map<Integer, Integer> output = Maps.newHashMap();
        double sum = 0;
        for (int i = 0; i < input.length; i++) {
            sum += input[i];
        }
        double splitted = sum / (double) input.length;
        if (sum % input.length != 0) {
            if (Math.floor(splitted) < splitted) {
                double lowest = Math.floor(sum / input.length) * input.length;
                int itemsLeftOver = (int) sum - (int) lowest;
                for (int i = 0; i < input.length; i++) {
                    if (itemsLeftOver > 0) {
                        input[i] = (int) Math.ceil(splitted);
                        itemsLeftOver--;
                    } else {
                        input[i] = (int) splitted;
                    }

                }
            }

        } else {
            for (int i = 0; i < input.length; i++) {
                input[i] = (int) splitted;
            }
        }
        for (int i = 0; i < input.length; i++) {
            output.put(slot[i], input[i]);
        }
        return output;
    }

    // lmao
    public void fillEmptySlots(int start, int size) {
        int amount = 0;
        for (int i = start; i < size; i++) {
            if (getItem(FACTORY_INPUT[i]).isEmpty()) {
                amount++;
            }
        }
        if (amount == 0) {
            return;
        }
        ItemStack stack = ItemStack.EMPTY;
        for (int j = start; j < size; j++) {
            if (!getItem(FACTORY_INPUT[j]).isEmpty()) {
                if (getItem(FACTORY_INPUT[j]).getCount() > 1 && amount > 0) {
                    if (amount >= getItem(FACTORY_INPUT[j]).getCount()) {
                        amount = getItem(FACTORY_INPUT[j]).getCount() - 1;
                    }
                    stack = getItem(FACTORY_INPUT[j]).copy();
                    getItem(FACTORY_INPUT[j]).shrink(amount);
                    for (int i = start; i < size; i++) {
                        if (getItem(FACTORY_INPUT[i]).isEmpty() && amount > 0) {
                            setItem(FACTORY_INPUT[i], stack.copyWithCount(1));
                            amount--;
                            setChanged();
                        }
                    }
                    setChanged();
                    break;
                }

            }
        }
    }


    // whaaat
    public void split(boolean fullCheck, int start, int size) {
        ItemStack itemToCheck = ItemStack.EMPTY;
        int fullCheckCount = 0;
        if (!fullCheck) {
            for (int i = start; i < size; i++) {
                if (getItem(FACTORY_INPUT[i]).isEmpty()) {
                    fullCheckCount++;
                }
            }
            if (fullCheckCount == 0) {
                return;
            }
        }
        for (int i = start; i < size; i++) {
            if (!getItem(FACTORY_INPUT[i]).isEmpty()) {
                itemToCheck = getItem(FACTORY_INPUT[i]).copy();
            }
        }
        if (!itemToCheck.isEmpty()) {
            fillEmptySlots(start, size);
        } else {
            return;
        }

        Map<Integer, Integer> items = Maps.newHashMap();
        Map<Integer, Integer> setCounts = Maps.newHashMap();

        for (int i = start; i < size; i++) {
            if (!getItem(FACTORY_INPUT[i]).isEmpty() && getItem(FACTORY_INPUT[i]).getItem() == itemToCheck.getItem()) {
                items.put(FACTORY_INPUT[i], getItem(FACTORY_INPUT[i]).getCount());
            }

        }
        if (items.isEmpty()) {
            return;
        }
        int[] slot = new int[items.size()];
        int[] input = new int[items.size()];
        int j = 0;
        for (Map.Entry<Integer, Integer> itemEntry : items.entrySet()) {
            slot[j] = itemEntry.getKey();
            input[j] = itemEntry.getValue();
            j++;
        }
        setCounts = getSplitCounts(slot, input);
        int check = 0;
        for (Map.Entry<Integer, Integer> countsEntry : setCounts.entrySet()) {
            int count = getItem(countsEntry.getKey()).getCount();
            if (count == countsEntry.getValue()) {
                check++;
            }

        }
        if (check == setCounts.size()) {
            return;
        }
        for (Map.Entry<Integer, Integer> countsEntry : setCounts.entrySet()) {
            ItemStack newStack = getItem(countsEntry.getKey()).copy();
            newStack.setCount(countsEntry.getValue());
            setItem(countsEntry.getKey(), newStack);
            setChanged();
        }


    }


    boolean rainbowCheckFurnaceTiers(List<BlockIronFurnaceTileBase> list) {
        if (list.isEmpty()) {
            return false;
        }
        int check = 0;
        for (BlockIronFurnaceTileBase furnace : list) {
            if (furnace.generatorBurn > 0 && furnace.getEnergy() < furnace.getCapacity()) {
                check++;
            }
        }
        if (check == 0) {
            return false;
        }
        return true;
    }

    public static void tick(Level level, BlockPos worldPosition, BlockState blockState, BlockIronFurnaceTileBase furnaceTile) {
        if (!furnaceTile.level.isClientSide) {
            if (furnaceTile.isGenerator()) {
                boolean flag3 = false;

                if (furnaceTile.isRainbowFurnace()) {
                    List<BlockIronFurnaceTileBase> rainbow = new ArrayList<>();
                    List<BlockIronFurnaceTileBase> nonRainbow = new ArrayList<>();
                    if (furnaceTile.owner != null) {
                        flag3 = true;
                        Player playerByUUID = level.getPlayerByUUID(furnaceTile.owner);
                        if (playerByUUID != null) {
                            Set<GlobalPos> furnacesPos = PlayerDataHandler.readFurnacesList(playerByUUID, PlayerFurnacesList::get);
                            if (!furnacesPos.isEmpty()) {
                                for (GlobalPos furnacesPo : furnacesPos) {
                                    BlockPos pos = furnacesPo.pos();
                                    ResourceKey<Level> dimension = furnacesPo.dimension();
                                    ServerLevel targetLevel = level.getServer().getLevel(dimension);
                                    if (targetLevel != null) {
                                        targetLevel.getChunkAt(pos).setLoaded(true);
                                        BlockEntity be = targetLevel.getBlockEntity(pos);
                                        if (be instanceof UnifiedTileEntity unifiedTileEntity) {
                                            if (unifiedTileEntity.isRainbowFurnace()) {
                                                rainbow.add(unifiedTileEntity);
                                            } else {
                                                nonRainbow.add(unifiedTileEntity);
                                            }
                                        }
                                    }
                                }

                            }
                        }
                    }
                    if (rainbow.size() > 1) {
                        int rainbowGens = 0;
                        for (int i = 0; i < rainbow.size(); i++) {
                            if (rainbow.get(i).isGenerator()) {
                                rainbowGens++;
                            }
                        }
                        if (rainbowGens > 1) {
                            flag3 = false;
                        }
                    }
                    if (flag3
                            && furnaceTile.rainbowCheckFurnaceTiers(nonRainbow)
                    ) {
                        furnaceTile.rainbowGenerating = flag3;
                        BlockState state = level.getBlockState(worldPosition);
                        if (BlockMillionFurnace.isGenerating(state) != furnaceTile.rainbowGenerating) {
                            level.setBlock(worldPosition, BlockMillionFurnace.modifyGenerating(state, furnaceTile.rainbowGenerating), 3);
                        }
                        furnaceTile.rainbowEnergyOut();
                    } else {
                        furnaceTile.rainbowGenerating = false;
                        BlockState state = level.getBlockState(worldPosition);
                        if (BlockMillionFurnace.isGenerating(state) != furnaceTile.rainbowGenerating) {
                            level.setBlock(worldPosition, BlockMillionFurnace.modifyGenerating(state, furnaceTile.rainbowGenerating), 3);
                        }
                    }
                }
            }
        }


        boolean flag1 = false;
        boolean wasBurning = furnaceTile.isBurning();
        if (furnaceTile.furnaceSettings.size() <= 0) {
            furnaceTile.furnaceSettings = new FurnaceSettings() {
                @Override
                public void onChanged() {
                    furnaceTile.setChanged();
                }
            };
        }
        for (int i = 3; i <= 5; i++) {
            if (furnaceTile.currentAugment[i - 3] != furnaceTile.getAugment(furnaceTile.getItem(i))) {
                furnaceTile.currentAugment[i - 3] = furnaceTile.getAugment(furnaceTile.getItem(i));
                furnaceTile.furnaceBurnTime = 0;
                furnaceTile.generatorBurn = 0;
                if (i - 3 == 2 || (furnaceTile.isGenerator() && i - 3 == 0)) {
                    furnaceTile.dropContents();
                }

            }
        }
        if (!furnaceTile.level.isClientSide) {

            if (furnaceTile.getCapacity() != furnaceTile.getCapacityFromTier()) {
                furnaceTile.setMaxEnergy(furnaceTile.getCapacityFromTier());
            }
            if (furnaceTile.totalCookTime != furnaceTile.getCookTime()) {
                furnaceTile.totalCookTime = furnaceTile.getCookTime();
            }
            int mode = furnaceTile.getRedstoneSetting();
            if (mode != 0) {
                if (mode == 2) {
                    int i = 0;
                    for (Direction side : Direction.values()) {
                        if (level.getSignal(worldPosition.offset(side.getNormal()), side) > 0) {
                            i++;
                        }
                    }
                    if (i != 0) {
                        furnaceTile.cookTime = 0;
                        furnaceTile.furnaceBurnTime = 0;
                        furnaceTile.forceUpdateAllStates();
                        return;
                    }
                }
                if (mode == 1) {
                    boolean flag = false;
                    for (Direction side : Direction.values()) {

                        if (level.getSignal(worldPosition.offset(side.getNormal()), side) > 0) {
                            flag = true;
                        }
                    }
                    if (!flag) {
                        furnaceTile.cookTime = 0;
                        furnaceTile.furnaceBurnTime = 0;
                        furnaceTile.forceUpdateAllStates();
                        return;
                    }
                }
                for (int i = 0; i < Direction.values().length; i++)
                    furnaceTile.provides[i] = furnaceTile.getBlockState().getDirectSignal(furnaceTile.level, worldPosition, DirectionUtil.fromId(i));

            } else {
                for (int i = 0; i < Direction.values().length; i++)
                    furnaceTile.provides[i] = 0;
            }
            if (furnaceTile.doesNeedUpdateSend()) {
                furnaceTile.onUpdateSent();
            }


        }


        if (furnaceTile.isFactory()) {
            if (!furnaceTile.level.isClientSide) {
                VanillaCapabilityHandler.withBlockEnergyStorage(furnaceTile, null, iEnergyStorage -> {
                    if (!iEnergyStorage.canReceive()) {
                        ((EnergyWrapper) iEnergyStorage).setMaxReceive(iEnergyStorage.getMaxEnergyStored());
                    }
                    if (iEnergyStorage.canExtract()) {
                        ((EnergyWrapper) iEnergyStorage).setMaxExtract(0);
                    }
                });

                furnaceTile.checkRecipeType();
                int start = furnaceTile.getTier() == 0 ? 2 : furnaceTile.getTier() == 1 ? 1 : 0;
                int size = furnaceTile.getTier() == 0 ? 4 : furnaceTile.getTier() == 1 ? 5 : 6;
                if (furnaceTile.isAutoSplit()) {
                    furnaceTile.split(false, start, size);
                }
                for (int i = start; i < size; i++) {
                    int slot = FACTORY_INPUT[i];
                    if (furnaceTile.factoryTotalCookTime[i] != furnaceTile.getFactoryCookTime(slot)) {
                        furnaceTile.factoryTotalCookTime[i] = furnaceTile.getFactoryCookTime(slot);
                    }
                    if (!furnaceTile.getItem(slot).isEmpty()) {
                        Optional<RecipeHolder<AbstractCookingRecipe>> irecipe = furnaceTile.getRecipeFactory(slot, furnaceTile.getItem(slot));

                        boolean valid = furnaceTile.canFactorySmelt(irecipe.orElse(null), slot);
                        if (valid) {
                            //~ if >1.20.1 '.get()' -> '.get().value()'
                            int energyRecipe = irecipe.get().value().getCookingTime() * 20;
                            int energy = furnaceTile.getItem(AUGMENT_GREEN).getItem() instanceof ItemAugmentSpeed ?
                                    energyRecipe * 2 : furnaceTile.getItem(AUGMENT_GREEN).getItem() instanceof ItemAugmentFuel ?
                                    energyRecipe / 2 : energyRecipe;
                            if (furnaceTile.getEnergy() >= energy || furnaceTile.factoryCookTime[i] > 0) {
                                furnaceTile.factoryCookTime[i]++;
                                furnaceTile.usedRF[i] += (double) (energy / furnaceTile.factoryTotalCookTime[i]);
                                furnaceTile.energyStorage.extractEnergy(energy / furnaceTile.factoryTotalCookTime[i], false);
                                if (level.getBlockState(furnaceTile.getBlockPos()).getValue(BlockStateProperties.LIT) != furnaceTile.isFactoryCooking()) {
                                    level.setBlock(worldPosition, level.getBlockState(worldPosition).setValue(BlockStateProperties.LIT, furnaceTile.isFactoryCooking()), 3);
                                }
                                if (furnaceTile.factoryCookTime[i] >= furnaceTile.factoryTotalCookTime[i]) {
                                    furnaceTile.factoryCookTime[i] = 0;
                                    if (furnaceTile.usedRF[i] < energy) {
                                        double diff = energy - furnaceTile.usedRF[i];
                                        furnaceTile.removeEnergy((int) diff);
                                    }
                                    furnaceTile.usedRF[i] = 0;
                                    furnaceTile.factoryTotalCookTime[i] = furnaceTile.getFactoryCookTime(slot);
                                    if (furnaceTile.isAutoSplit()) {
                                        furnaceTile.split(true, start, size);
                                    }

                                    furnaceTile.factorySmelt(irecipe.orElse(null), slot);
                                    furnaceTile.autoFactoryIO();
                                    furnaceTile.setChanged();
                                }
                            }
                        }
                    } else {
                        furnaceTile.factoryCookTime[i] = 0;
                        if (level.getBlockState(furnaceTile.getBlockPos()).getValue(BlockStateProperties.LIT) != furnaceTile.isFactoryCooking()) {
                            level.setBlock(worldPosition, level.getBlockState(worldPosition).setValue(BlockStateProperties.LIT, furnaceTile.isFactoryCooking()), 3);
                        }
                    }

                }
                if (furnaceTile.level.getGameTime() % 24 == 0) {
                    BlockState state = level.getBlockState(worldPosition);
                    if (state.getValue(ModBlockState.TYPE) != furnaceTile.getStateType()) {
                        level.setBlock(worldPosition, state.setValue(ModBlockState.TYPE, furnaceTile.getStateType()), 3);
                    }
                    if (state.getValue(ModBlockState.JOVIAL) != furnaceTile.jovial) {
                        level.setBlock(worldPosition, state.setValue(ModBlockState.JOVIAL, furnaceTile.jovial), 3);
                    }
                    for (int i = 0; i < furnaceTile.factoryCookTime.length; i++) {
                        if (furnaceTile.factoryCookTime[i] <= 0) {
                            for (int j = 0; j < FACTORY_INPUT.length; j++) {
                                if (furnaceTile.getItem(FACTORY_INPUT[j]).isEmpty()) {
                                    furnaceTile.autoFactoryIO();
                                    furnaceTile.setChanged();
                                } else if (furnaceTile.getItem(FACTORY_INPUT[j]).getCount() < furnaceTile.getItem(FACTORY_INPUT[j]).getMaxStackSize()) {
                                    furnaceTile.autoFactoryIO();
                                    furnaceTile.setChanged();
                                }
                            }
                            for (int j = 0; j < FACTORY_INPUT.length; j++) {
                                int outputSlot = FACTORY_INPUT[j] + 6;
                                if (!furnaceTile.getItem(outputSlot).isEmpty() && furnaceTile.getItem(outputSlot).getCount() >= 64) {
                                    furnaceTile.autoFactoryIO();
                                }
                            }

                        }
                    }
                }
            }
        } else if (furnaceTile.isGenerator()) {
            if (!level.isClientSide) {
                VanillaCapabilityHandler.withBlockEnergyStorage(furnaceTile, null, iEnergyStorage -> {
                    if (iEnergyStorage.canReceive()) {
                        ((EnergyWrapper) iEnergyStorage).setMaxReceive(0);
                    }
                    if (!iEnergyStorage.canExtract()) {
                        ((EnergyWrapper) iEnergyStorage).setMaxExtract(iEnergyStorage.getMaxEnergyStored());
                    }
                });

                if (furnaceTile.getEnergy() < furnaceTile.getCapacity()) {
                    if (!furnaceTile.getItem(GENERATOR_FUEL).isEmpty() && furnaceTile.generatorBurn <= 0) {
                        furnaceTile.generatorBurn = furnaceTile.getGeneratorBurn();
                        furnaceTile.generatorRecentRecipeRF = (int) furnaceTile.generatorBurn;
                        if (furnaceTile.getItem(GENERATOR_FUEL).hasCraftingRemainingItem())
                            furnaceTile.setItem(GENERATOR_FUEL, furnaceTile.getItem(GENERATOR_FUEL).getCraftingRemainingItem());
                        else if (!furnaceTile.getItem(GENERATOR_FUEL).isEmpty()) {
                            furnaceTile.getItem(GENERATOR_FUEL).shrink(1);
                            if (furnaceTile.getItem(GENERATOR_FUEL).isEmpty()) {
                                furnaceTile.setItem(GENERATOR_FUEL, furnaceTile.getItem(GENERATOR_FUEL).getCraftingRemainingItem());
                            }
                        }
                        furnaceTile.setChanged();
                    }
                    if (furnaceTile.isGenerator()) {
                        if (level.getBlockState(furnaceTile.getBlockPos()).getValue(BlockStateProperties.LIT) != furnaceTile.generatorBurn > 0) {
                            level.setBlock(worldPosition, level.getBlockState(worldPosition).setValue(BlockStateProperties.LIT, furnaceTile.generatorBurn > 0), 3);
                        }
                    }
                    if (furnaceTile.generatorBurn > 0) {
                        double max = furnaceTile.generatorRecentRecipeRF * 20;
                        furnaceTile.gottenRF += furnaceTile.getGeneration();

                        furnaceTile.energyStorage.receiveEnergy(furnaceTile.getGeneration(), false);
                        if (furnaceTile.generatorBurn - (double) ((double) furnaceTile.getGeneration() / 20) <= 0) {
                            if (furnaceTile.gottenRF + furnaceTile.getGeneration() > max && furnaceTile.gottenRF + furnaceTile.getGeneration() < furnaceTile.getCapacity()) {
                                int diff = (int) (furnaceTile.gottenRF + furnaceTile.getGeneration() - max);
                                furnaceTile.energyStorage.receiveEnergy(furnaceTile.getGeneration(), false);
                                furnaceTile.removeEnergy(diff);
                            }
                            if (furnaceTile.gottenRF + furnaceTile.getGeneration() < max) {
                                int diff = (int) (max - furnaceTile.gottenRF + furnaceTile.getGeneration());
                                furnaceTile.energyStorage.receiveEnergy(furnaceTile.getGeneration(), false);
                                furnaceTile.energyStorage.receiveEnergy(diff, false);
                            }
                            furnaceTile.gottenRF = 0;
                        }
                        furnaceTile.generatorBurn -= (double) ((double) furnaceTile.getGeneration() / 20);
                        if (furnaceTile.generatorBurn <= 0) {
                            furnaceTile.autoIOGenerator();
                            furnaceTile.generatorBurn = 0;
                        }
                    }
                }
                if (furnaceTile.generatorBurn <= 0) {
                    furnaceTile.generatorBurn = 0;
                }


                furnaceTile.energyOut();
                if (furnaceTile.level.getGameTime() % 24 == 0) {

                    if (furnaceTile.generatorBurn <= 0) {
                        if (furnaceTile.getItem(GENERATOR_FUEL).isEmpty()) {
                            furnaceTile.autoIOGenerator();
                            furnaceTile.setChanged();
                        } else if (furnaceTile.getItem(GENERATOR_FUEL).getCount() < furnaceTile.getItem(GENERATOR_FUEL).getMaxStackSize()) {
                            furnaceTile.autoIOGenerator();
                            furnaceTile.setChanged();
                        }
                    }


                }


            }
            if (furnaceTile.level.getGameTime() % 24 == 0) {
                BlockState state = level.getBlockState(worldPosition);
                if (state.getValue(ModBlockState.TYPE) != furnaceTile.getStateType()) {
                    level.setBlock(worldPosition, state.setValue(ModBlockState.TYPE, furnaceTile.getStateType()), 3);
                }
                if (state.getValue(ModBlockState.JOVIAL) != furnaceTile.jovial) {
                    level.setBlock(worldPosition, state.setValue(ModBlockState.JOVIAL, furnaceTile.jovial), 3);
                }
            }


        } else if (furnaceTile.isFurnace()) {
            VanillaCapabilityHandler.withBlockEnergyStorage(furnaceTile, null, iEnergyStorage -> {
                if (iEnergyStorage.canReceive()) {
                    ((EnergyWrapper) iEnergyStorage).setMaxReceive(0);
                }
                if (iEnergyStorage.canExtract()) {
                    ((EnergyWrapper) iEnergyStorage).setMaxExtract(0);
                }
            });


            if (!furnaceTile.level.isClientSide) {
                if (furnaceTile.isBurning()) {
                    --furnaceTile.furnaceBurnTime;
                }
                furnaceTile.checkRecipeType();

                ItemStack itemstack = furnaceTile.getItem(FUEL);
                if (furnaceTile.isBurning() || !itemstack.isEmpty() && !furnaceTile.getItem(INPUT).isEmpty()) {
                    Optional<RecipeHolder<AbstractCookingRecipe>> irecipe = Optional.empty();
                    if (!furnaceTile.getItem(INPUT).isEmpty()) {
                        irecipe = furnaceTile.getRecipe(furnaceTile.getItem(INPUT));
                    }

                    boolean valid = furnaceTile.canSmelt(irecipe.orElse(null));
                    if (!furnaceTile.isBurning() && valid) {
                        if (itemstack.getItem() instanceof ItemHeater) {
                            BlockPos boundBlockPos = ItemHeater.getBoundBlockPos(itemstack);
                            if (boundBlockPos != null) {
                                BlockEntity te = level.getBlockEntity(boundBlockPos);
                                if (te instanceof BlockWirelessEnergyHeaterTile heaterTile) {
                                    int energy = heaterTile.getWrapper().getEnergyStored();
                                    if (energy >= 2000) {
                                        if (!furnaceTile.getItem(AUGMENT_GREEN).isEmpty() && furnaceTile.getItem(AUGMENT_GREEN).getItem() instanceof ItemAugmentFuel) {
                                            furnaceTile.furnaceBurnTime = 400 * furnaceTile.getCookTime() / 200;
                                        } else if (!furnaceTile.getItem(AUGMENT_GREEN).isEmpty() && furnaceTile.getItem(AUGMENT_GREEN).getItem() instanceof ItemAugmentSpeed) {
                                            if (energy >= 4000) {
                                                furnaceTile.furnaceBurnTime = 100 * furnaceTile.getCookTime() / 200;
                                            }
                                        } else {
                                            furnaceTile.furnaceBurnTime = 200 * furnaceTile.getCookTime() / 200;
                                        }
                                        if (furnaceTile.furnaceBurnTime > 0)
                                            ((BlockWirelessEnergyHeaterTile) te).getWrapper().extractEnergy(2000, false);

                                        furnaceTile.recipesUsed = furnaceTile.furnaceBurnTime;
                                    }
                                }
                            }
                        } else {
                            if (!furnaceTile.getItem(AUGMENT_GREEN).isEmpty()) {
                                if (furnaceTile.getItem(AUGMENT_GREEN).getItem() instanceof ItemAugmentFuel) {
                                    furnaceTile.furnaceBurnTime = (getBurnTime(itemstack, furnaceTile.recipeType) * furnaceTile.getCookTime() / 200) * 2;
                                } else if (furnaceTile.getItem(AUGMENT_GREEN).getItem() instanceof ItemAugmentSpeed) {
                                    furnaceTile.furnaceBurnTime = (getBurnTime(itemstack, furnaceTile.recipeType) * furnaceTile.getCookTime() / 200) / 2;

                                }
                            } else {
                                furnaceTile.furnaceBurnTime = getBurnTime(itemstack, furnaceTile.recipeType) * furnaceTile.getCookTime() / 200;
                            }
                            furnaceTile.recipesUsed = furnaceTile.furnaceBurnTime;
                        }
                        if (furnaceTile.isBurning()) {
                            flag1 = true;
                            if (!(itemstack.getItem() instanceof ItemHeater)) {
                                if (itemstack.hasCraftingRemainingItem())
                                    furnaceTile.setItem(FUEL, itemstack.getCraftingRemainingItem());
                                else if (!itemstack.isEmpty()) {
                                    itemstack.shrink(1);
                                    if (itemstack.isEmpty()) {
                                        furnaceTile.setItem(FUEL, itemstack.getCraftingRemainingItem());
                                    }
                                }
                            }
                        }
                    }
                    if (furnaceTile.isBurning() && valid) {
                        ++furnaceTile.cookTime;
                        if (furnaceTile.cookTime >= furnaceTile.totalCookTime) {
                            furnaceTile.cookTime = 0;
                            furnaceTile.totalCookTime = furnaceTile.getCookTime();
                            furnaceTile.smelt(irecipe.orElse(null));
                            furnaceTile.autoIO();
                            flag1 = true;
                        }
                    } else {
                        furnaceTile.cookTime = 0;
                    }
                } else if (!furnaceTile.isBurning() && furnaceTile.cookTime > 0) {
                    furnaceTile.cookTime = clamp(furnaceTile.cookTime - 2, 0, furnaceTile.totalCookTime);
                }
                if (furnaceTile.level.getGameTime() % 24 == 0) {

                    if (furnaceTile.cookTime <= 0) {

                        if (furnaceTile.getItem(INPUT).isEmpty()) {
                            furnaceTile.autoIO();
                            flag1 = true;
                        } else if (furnaceTile.getItem(INPUT).getCount() < furnaceTile.getItem(INPUT).getMaxStackSize()) {
                            furnaceTile.autoIO();
                            flag1 = true;
                        }
                        if (furnaceTile.getItem(FUEL).isEmpty()) {
                            furnaceTile.autoIO();
                            flag1 = true;
                        } else if (furnaceTile.getItem(FUEL).getCount() < furnaceTile.getItem(FUEL).getMaxStackSize()) {
                            furnaceTile.autoIO();
                            flag1 = true;
                        }

                        if (!furnaceTile.getItem(OUTPUT).isEmpty() && furnaceTile.getItem(OUTPUT).getCount() >= 64) {
                            furnaceTile.autoIO();
                        }
                    }


                }
            }
            if (wasBurning != furnaceTile.isBurning()) {
                level.setBlock(worldPosition, level.getBlockState(furnaceTile.worldPosition).setValue(BlockStateProperties.LIT, furnaceTile.isBurning()), 3);
            }
            if (furnaceTile.level.getGameTime() % 24 == 0) {
                BlockState state = level.getBlockState(worldPosition);
                if (state.getValue(ModBlockState.TYPE) != furnaceTile.getStateType()) {
                    level.setBlock(worldPosition, state.setValue(ModBlockState.TYPE, furnaceTile.getStateType()), 3);
                }
                if (state.getValue(ModBlockState.JOVIAL) != furnaceTile.jovial) {
                    level.setBlock(worldPosition, state.setValue(ModBlockState.JOVIAL, furnaceTile.jovial), 3);
                }
            }

            if (flag1) {
                furnaceTile.setChanged();
            }


        }
    }

    public static int clamp(int p_76125_0_, int p_76125_1_, int p_76125_2_) {
        if (p_76125_0_ < p_76125_1_) {
            return p_76125_1_;
        } else {
            return p_76125_0_ > p_76125_2_ ? p_76125_2_ : p_76125_0_;
        }
    }

    protected int getCapacityFromTier() {
        return switch (getTier()) {
            case 1 -> Config.furnaceEnergyCapacityTier1.get();
            case 2 -> Config.furnaceEnergyCapacityTier2.get();
            default -> Config.furnaceEnergyCapacityTier0.get();
        };
    }

    protected void rainbowEnergyOut() {
        Map<BlockEntity, Direction> tiles = Maps.newHashMap();
        for (Direction dir : Direction.values()) {
            BlockEntity tile = level.getBlockEntity(worldPosition.offset(dir.getNormal()));
            if (tile == null) {
                continue;
            }
            if (furnaceSettings.get(dir.ordinal()) == 2 || furnaceSettings.get(dir.ordinal()) == 3) {
                VanillaCapabilityHandler.withBlockEnergyStorage(tile, dir.getOpposite(), other -> {
                    if (other.canReceive() && other.getEnergyStored() < other.getMaxEnergyStored()) {
                        tiles.put(tile, dir.getOpposite());
                    }
                });
            }
        }
        for (Map.Entry<BlockEntity, Direction> entry : tiles.entrySet()) {
            int energy = Config.millionFurnacePowerToGenerate.get() / tiles.size();
            VanillaCapabilityHandler.withBlockEnergyStorage(entry.getKey(), entry.getValue(), iEnergyStorage -> {
                iEnergyStorage.receiveEnergy(energy, false);
            });
        }
    }

    protected void energyOut() {
        Map<BlockEntity, Direction> tiles = Maps.newHashMap();
        for (Direction dir : Direction.values()) {
            BlockEntity tile = level.getBlockEntity(worldPosition.offset(dir.getNormal()));
            if (tile == null) {
                continue;
            }
            if (furnaceSettings.get(dir.ordinal()) == 2 || furnaceSettings.get(dir.ordinal()) == 3) {
                VanillaCapabilityHandler.withBlockEnergyStorage(tile, dir.getOpposite(), other -> {
                    if (other.canReceive() && other.getEnergyStored() < other.getMaxEnergyStored()) {
                        tiles.put(tile, dir.getOpposite());
                    }
                });

            }
        }

        for (Map.Entry<BlockEntity, Direction> entry : tiles.entrySet()) {
            VanillaCapabilityHandler.withBlockEnergyStorage(entry.getKey(), entry.getValue(), iEnergyStorage -> {
                int i = energyStorage.getEnergyStored() / tiles.size();
                removeEnergy(iEnergyStorage.receiveEnergy(i, false));
            });
        }
    }

    protected void autoIO() {
        for (Direction dir : Direction.values()) {
            BlockEntity tile = level.getBlockEntity(worldPosition.offset(dir.getNormal()));
            if (tile == null) {
                continue;
            }
            if (furnaceSettings.get(dir.ordinal()) == 1 || furnaceSettings.get(dir.ordinal()) == 2 || furnaceSettings.get(dir.ordinal()) == 3 || furnaceSettings.get(dir.ordinal()) == 4) {
                if (tile != null) {

                    IItemHandler other = VanillaCapabilityHandler.getBlockItemHandler(tile, dir.getOpposite());

                    if (other == null) {
                        continue;
                    }
                    if (other != null) {
                        if (this.getAutoInput() != 0 || this.getAutoOutput() != 0) {
                            if (this.getAutoInput() == 1) {
                                if (furnaceSettings.get(dir.ordinal()) == 1 || furnaceSettings.get(dir.ordinal()) == 3) {
                                    if (this.getItem(INPUT).getCount() >= this.getItem(INPUT).getMaxStackSize()) {
                                        continue;
                                    }
                                    for (int i = 0; i < other.getSlots(); i++) {
                                        if (other.getStackInSlot(i).isEmpty()) {
                                            continue;
                                        }
                                        ItemStack stack = other.extractItem(i, other.getStackInSlot(i).getMaxStackSize(), true);
                                        if (hasRecipe(stack) && getItem(INPUT).isEmpty() || ItemStack.isSameItemSameComponents(getItem(INPUT), stack)) {
                                            insertItemInternal(INPUT, other.extractItem(i, other.getStackInSlot(i).getMaxStackSize() - this.getItem(INPUT).getCount(), false), false);
                                        }
                                    }
                                }
                                if (furnaceSettings.get(dir.ordinal()) == 4) {
                                    if (this.getItem(FUEL).getCount() >= this.getItem(FUEL).getMaxStackSize()) {
                                        continue;
                                    }
                                    for (int i = 0; i < other.getSlots(); i++) {
                                        if (other.getStackInSlot(i).isEmpty()) {
                                            continue;
                                        }
                                        if (!isItemFuel(other.getStackInSlot(i), recipeType)) {
                                            continue;
                                        }
                                        ItemStack stack = other.extractItem(i, other.getStackInSlot(i).getMaxStackSize(), true);
                                        if (isItemFuel(stack, recipeType) && getItem(FUEL).isEmpty() || ItemStack.isSameItemSameComponents(getItem(FUEL), stack)) {
                                            insertItemInternal(FUEL, other.extractItem(i, other.getStackInSlot(i).getMaxStackSize() - this.getItem(FUEL).getCount(), false), false);
                                        }
                                    }
                                }
                            }
                            if (this.getAutoOutput() == 1) {
                                if (furnaceSettings.get(dir.ordinal()) == 4) {
                                    if (this.getItem(FUEL).isEmpty()) {
                                        continue;
                                    }
                                    if (isItemFuel(this.getItem(FUEL), recipeType)) {
                                        continue;
                                    }
                                    for (int i = 0; i < other.getSlots(); i++) {
                                        ItemStack stack = extractItemInternal(FUEL, other.getSlotLimit(i) - other.getStackInSlot(i).getCount(), true);
                                        if (other.isItemValid(i, stack) && (other.getStackInSlot(i).isEmpty() || (ItemStack.isSameItemSameComponents(other.getStackInSlot(i), stack) && other.getStackInSlot(i).getCount() + stack.getCount() <= other.getSlotLimit(i)))) {
                                            boolean check = other.insertItem(i, extractItemInternal(FUEL, stack.getCount(), true), true).isEmpty();
                                            if (check)
                                                other.insertItem(i, extractItemInternal(FUEL, stack.getCount(), false), false);
                                        }
                                    }
                                }

                                if (furnaceSettings.get(dir.ordinal()) == 2 || furnaceSettings.get(dir.ordinal()) == 3) {
                                    if (this.getItem(OUTPUT).isEmpty()) {
                                        continue;
                                    }
                                    for (int i = 0; i < other.getSlots(); i++) {
                                        ItemStack stack = extractItemInternal(OUTPUT, other.getSlotLimit(i) - other.getStackInSlot(i).getCount(), true);
                                        if (other.isItemValid(i, stack) && (other.getStackInSlot(i).isEmpty() || (ItemStack.isSameItemSameComponents(other.getStackInSlot(i), stack) && other.getStackInSlot(i).getCount() + stack.getCount() <= other.getSlotLimit(i)))) {
                                            boolean check = other.insertItem(i, extractItemInternal(OUTPUT, stack.getCount(), true), true).isEmpty();
                                            if (check)
                                                other.insertItem(i, extractItemInternal(OUTPUT, stack.getCount(), false), false);
                                        }
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }
    }

    protected void autoIOGenerator() {
        for (Direction dir : Direction.values()) {
            BlockEntity tile = level.getBlockEntity(worldPosition.offset(dir.getNormal()));
            if (tile == null) {
                continue;
            }
            if (furnaceSettings.get(dir.ordinal()) == 4) {
                if (tile != null) {
                    IItemHandler other = VanillaCapabilityHandler.getBlockItemHandler(tile, dir.getOpposite());

                    if (other == null) {
                        continue;
                    }
                    if (other != null) {
                        if (this.getAutoInput() != 0) {
                            if (furnaceSettings.get(dir.ordinal()) == 4) {
                                if (this.getItem(GENERATOR_FUEL).getCount() >= this.getItem(GENERATOR_FUEL).getMaxStackSize()) {
                                    continue;
                                }
                                for (int i = 0; i < other.getSlots(); i++) {
                                    if (other.getStackInSlot(i).isEmpty()) {
                                        continue;
                                    }
                                    if (other.getStackInSlot(i).getItem() == Items.BUCKET) {
                                        continue;
                                    }
                                    ItemStack stack = other.extractItem(i, other.getStackInSlot(i).getMaxStackSize(), true);
                                    if (stack.getItem() instanceof ItemHeater) {
                                        continue;
                                    }
                                    if (isItemFuel(stack, recipeType) && getItem(GENERATOR_FUEL).isEmpty() || ItemStack.isSameItemSameComponents(getItem(GENERATOR_FUEL), stack)) {
                                        insertItemInternal(GENERATOR_FUEL, other.extractItem(i, other.getStackInSlot(i).getMaxStackSize() - this.getItem(GENERATOR_FUEL).getCount(), false), false);
                                    }
                                }
                            }
                        }
                        if (this.getAutoOutput() != 0) {
                            if (furnaceSettings.get(dir.ordinal()) == 4) {
                                if (this.getItem(GENERATOR_FUEL).isEmpty()) {
                                    continue;
                                }
                                if (!isItemFuel(getItem(GENERATOR_FUEL), recipeType)) {
                                    for (int i = 0; i < other.getSlots(); i++) {
                                        ItemStack stack = extractItemInternal(GENERATOR_FUEL, this.getItem(GENERATOR_FUEL).getMaxStackSize() - other.getStackInSlot(i).getCount(), true);
                                        if (other.isItemValid(i, stack) && (other.getStackInSlot(i).isEmpty() || (ItemStack.isSameItemSameComponents(other.getStackInSlot(i), stack) && other.getStackInSlot(i).getCount() + stack.getCount() <= other.getSlotLimit(i)))) {
                                            boolean check = other.insertItem(i, extractItemInternal(GENERATOR_FUEL, stack.getCount(), true), true).isEmpty();
                                            if (check)
                                                other.insertItem(i, extractItemInternal(GENERATOR_FUEL, stack.getCount(), false), false);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    protected void autoFactoryIO() {
        for (Direction dir : Direction.values()) {
            BlockEntity tile = level.getBlockEntity(worldPosition.offset(dir.getNormal()));
            if (tile == null) {
                continue;
            }
            if (furnaceSettings.get(dir.ordinal()) == 1 || furnaceSettings.get(dir.ordinal()) == 2 || furnaceSettings.get(dir.ordinal()) == 3) {
                if (tile != null) {
                    IItemHandler other = VanillaCapabilityHandler.getBlockItemHandler(tile, dir.getOpposite());

                    if (other == null) {
                        continue;
                    }
                    if (other != null) {
                        if (this.getAutoInput() != 0 || this.getAutoOutput() != 0) {
                            if (this.getAutoInput() == 1) {
                                if (furnaceSettings.get(dir.ordinal()) == 1 || furnaceSettings.get(dir.ordinal()) == 3) {
                                    int start = getTier() == 0 ? 2 : getTier() == 1 ? 1 : 0;
                                    int size = getTier() == 0 ? 4 : getTier() == 1 ? 5 : 6;

                                    for (int j = start; j < size; j++) {
                                        if (this.getItem(FACTORY_INPUT[j]).getCount() >= this.getItem(FACTORY_INPUT[j]).getMaxStackSize()) {
                                            continue;
                                        }
                                        for (int i = 0; i < other.getSlots(); i++) {
                                            if (other.getStackInSlot(i).isEmpty()) {
                                                continue;
                                            }
                                            ItemStack stack = other.extractItem(i, other.getStackInSlot(i).getMaxStackSize(), true);
                                            if (hasRecipe(stack) && getItem(FACTORY_INPUT[j]).isEmpty() || canItemStacksStack(getItem(FACTORY_INPUT[j]), stack)) {
                                                insertItemInternal(FACTORY_INPUT[j], other.extractItem(i, other.getStackInSlot(i).getMaxStackSize() - this.getItem(FACTORY_INPUT[j]).getCount(), false), false);
                                            }
                                        }
                                    }

                                }
                            }
                            if (this.getAutoOutput() == 1) {

                                if (furnaceSettings.get(dir.ordinal()) == 2 || furnaceSettings.get(dir.ordinal()) == 3) {
                                    int start = getTier() == 0 ? 2 : getTier() == 1 ? 1 : 0;
                                    int size = getTier() == 0 ? 4 : getTier() == 1 ? 5 : 6;
                                    for (int j = start; j < size; j++) {
                                        if (this.getItem(FACTORY_INPUT[j] + 6).isEmpty()) {
                                            continue;
                                        }

                                        for (int i = 0; i < other.getSlots(); i++) {
                                            ItemStack stack = extractItemInternal(FACTORY_INPUT[j] + 6, other.getSlotLimit(i) - other.getStackInSlot(i).getCount(), true);
                                            if (other.isItemValid(i, stack) && (other.getStackInSlot(i).isEmpty() || (canItemStacksStack(other.getStackInSlot(i), stack) && other.getStackInSlot(i).getCount() + stack.getCount() <= other.getSlotLimit(i)))) {
                                                boolean check = other.insertItem(i, extractItemInternal(FACTORY_INPUT[j] + 6, stack.getCount(), true), true).isEmpty();
                                                if (check)
                                                    other.insertItem(i, extractItemInternal(FACTORY_INPUT[j] + 6, stack.getCount(), false), false);
                                            }
                                        }
                                    }


                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static boolean canItemStacksStack(@NotNull ItemStack a, @NotNull ItemStack b) {
        /^
        if (a.isEmpty() || !ItemStack.isSameItem(a, b) || a.hasTag() != b.hasTag())
            return false;

        return (!a.hasTag() || a.getTag().equals(b.getTag()));
        ^/
        return ItemStack.isSameItemSameComponents(a, b);
    }

    @Nonnull
    public ItemStack insertItemInternal(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (stack.isEmpty())
            return ItemStack.EMPTY;

        if (!canPlaceItemThroughFace(slot, stack, null))
            return stack;

        ItemStack existing = this.getItem(slot);

        int limit = stack.getMaxStackSize();

        if (!existing.isEmpty()) {
            if (!ItemStack.isSameItemSameComponents(stack, existing))
                return stack;

            limit -= existing.getCount();
        }

        if (limit <= 0)
            return stack;

        boolean reachedLimit = stack.getCount() > limit;

        if (!simulate) {
            if (existing.isEmpty()) {
                this.setItem(slot, reachedLimit ? stack.copyWithCount(limit) : stack);
            } else {
                existing.grow(reachedLimit ? limit : stack.getCount());
            }
            this.setChanged();
        }

        return reachedLimit ? stack.copyWithCount(stack.getCount() - limit) : ItemStack.EMPTY;
    }

    @Nonnull
    private ItemStack extractItemInternal(int slot, int amount, boolean simulate) {
        if (amount == 0)
            return ItemStack.EMPTY;

        ItemStack existing = this.getItem(slot);

        if (existing.isEmpty())
            return ItemStack.EMPTY;

        int toExtract = Math.min(amount, existing.getMaxStackSize());

        if (existing.getCount() <= toExtract) {
            if (!simulate) {
                this.setItem(slot, ItemStack.EMPTY);
                this.setChanged();
                return existing;
            } else {
                return existing.copy();
            }
        } else {
            if (!simulate) {
                this.setItem(slot, existing.copyWithCount(existing.getCount() - toExtract));
                this.setChanged();
            }

            return existing.copyWithCount(toExtract);
        }
    }

    //CLIENT SYNC

    public boolean isAutoSplit() {
        return furnaceSettings.autoSplit == 1;
    }

    public int getSettingBottom() {
        return furnaceSettings.get(0);
    }

    public int getSettingTop() {
        return furnaceSettings.get(1);
    }

    public int getSettingFront() {
        int i = DirectionUtil.getId(this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING));
        return furnaceSettings.get(i);
    }

    public int getSettingBack() {
        int i = DirectionUtil.getId(this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite());
        return furnaceSettings.get(i);
    }

    public int getSettingLeft() {
        Direction facing = this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        if (facing == Direction.NORTH) {
            return furnaceSettings.get(DirectionUtil.getId(Direction.EAST));
        } else if (facing == Direction.WEST) {
            return furnaceSettings.get(DirectionUtil.getId(Direction.NORTH));
        } else if (facing == Direction.SOUTH) {
            return furnaceSettings.get(DirectionUtil.getId(Direction.WEST));
        } else {
            return furnaceSettings.get(DirectionUtil.getId(Direction.SOUTH));
        }
    }

    public int getSettingRight() {
        Direction facing = this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        if (facing == Direction.NORTH) {
            return furnaceSettings.get(DirectionUtil.getId(Direction.WEST));
        } else if (facing == Direction.WEST) {
            return furnaceSettings.get(DirectionUtil.getId(Direction.SOUTH));
        } else if (facing == Direction.SOUTH) {
            return furnaceSettings.get(DirectionUtil.getId(Direction.EAST));
        } else {
            return furnaceSettings.get(DirectionUtil.getId(Direction.NORTH));
        }
    }

    public int getIndexFront() {
        int i = this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING).ordinal();
        return i;
    }

    public int getIndexBack() {
        int i = this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite().ordinal();
        return i;
    }

    public int getIndexLeft() {
        Direction facing = this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        if (facing == Direction.NORTH) {
            return Direction.EAST.ordinal();
        } else if (facing == Direction.WEST) {
            return Direction.NORTH.ordinal();
        } else if (facing == Direction.SOUTH) {
            return Direction.WEST.ordinal();
        } else {
            return Direction.SOUTH.ordinal();
        }
    }

    public int getIndexRight() {
        Direction facing = this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        if (facing == Direction.NORTH) {
            return Direction.WEST.ordinal();
        } else if (facing == Direction.WEST) {
            return Direction.SOUTH.ordinal();
        } else if (facing == Direction.SOUTH) {
            return Direction.EAST.ordinal();
        } else {
            return Direction.NORTH.ordinal();
        }
    }

    public int getAutoInput() {
        return furnaceSettings.get(6);
    }

    public int getAugmentGUI() {
        return furnaceSettings.get(10);
    }

    public int getAutoOutput() {
        return furnaceSettings.get(7);
    }

    public int getRedstoneSetting() {
        return furnaceSettings.get(8);
    }

    public int getRedstoneComSub() {
        return furnaceSettings.get(9);
    }


    protected int getStateType() {
        if (this.getItem(3).getItem() == ModItems.SMOKING_AUGMENT.get()) {
            return 1;
        } else if (this.getItem(3).getItem() == ModItems.BLASTING_AUGMENT.get()) {
            return 2;
        } else {
            return 0;
        }
    }

    public boolean isBurning() {
        return furnaceBurnTime > 0;
    }

    public boolean isRainbowFurnace() {
        return this.self().getIdentifier().equals(BlockMillionFurnace.ID);
    }

    protected void smelt(@Nullable RecipeHolder<?> recipe) {
        smeltItem(recipe, this.self().getMaxSmeltItemNumberOnSingleOp());
    }

    protected void factorySmelt(@Nullable RecipeHolder<?> recipe, int slot) {
        smeltFactoryItem(recipe, slot, this.self().getMaxSmeltItemNumberOnSingleOp());
    }

    protected boolean canSmelt(@Nullable RecipeHolder<?> recipe) {
        return canSmeltInternal(recipe, 0, OUTPUT, true);
    }


    protected boolean canFactorySmelt(@Nullable RecipeHolder<?> recipe, int slot) {
        return canSmeltInternal(recipe, slot, OUTPUT, false);
    }

    protected boolean canSmeltInternal(
            @Nullable RecipeHolder<?> recipe,
            int inputSlot,
            int outputSlot,
            boolean limitTo64
    ) {
        ItemStack input = this.getItem(inputSlot);
        if (input.isEmpty() || recipe == null) {
            return false;
        }
        //~ if >1.20.1 'recipe.getResultItem' -> 'recipe.value().getResultItem'
        ItemStack recipeOutput = recipe.value().getResultItem(RegistryAccess.EMPTY);
        if (recipeOutput.isEmpty()) {
            return false;
        }

        ItemStack output = this.getItem(outputSlot);

        if (output.isEmpty()) {
            return true;
        }

        if (!ItemStack.isSameItemSameComponents(output, recipeOutput)) {
            return false;
        }

        int maxStack = limitTo64
                ? Math.min(output.getMaxStackSize(), 64)
                : output.getMaxStackSize();

        return output.getCount() + recipeOutput.getCount() <= maxStack;
    }


    protected void smeltItem(@Nullable RecipeHolder<?> recipe, int maxOperations) {
        smeltInternal(recipe, INPUT, OUTPUT, maxOperations,
                (r, slot) -> this.canSmelt(r));
    }

    protected void smeltFactoryItem(@Nullable RecipeHolder<?> recipe,
                                    int slot,
                                    int maxOperations) {
        smeltInternal(recipe, slot, slot + 6, maxOperations,
                this::canFactorySmelt);
    }

    private void smeltInternal(
            @Nullable RecipeHolder<?> recipe,
            int inputSlot,
            int outputSlot,
            int maxOperations,
            BiPredicate<RecipeHolder<?>, Integer> canSmeltCheck
    ) {
        if (recipe == null || !canSmeltCheck.test(recipe, inputSlot)) {
            return;
        }

        ItemStack input = this.getItem(inputSlot);
        //~ if >1.20.1 'recipe.getResultItem' -> 'recipe.value().getResultItem'
        ItemStack result = recipe.value().getResultItem(RegistryAccess.EMPTY);
        ItemStack output = this.getItem(outputSlot);

        int maxByOutput =
                (64 - output.getCount()) / result.getCount();

        int operations =
                Math.min(Math.min(maxOperations, maxByOutput), input.getCount());

        if (operations <= 0) {
            return;
        }

        int totalOutput = result.getCount() * operations;

        if (output.isEmpty()) {
            this.setItem(outputSlot, result.copyWithCount(totalOutput));
        } else if (output.getItem() == result.getItem()) {
            output.grow(totalOutput);
        }

        if (!this.level.isClientSide) {
            for (int i = 0; i < operations; i++) {
                this.setRecipeUsed(recipe);
            }
        }

        if (input.getItem() == Blocks.WET_SPONGE.asItem()
                && inputSlot == INPUT
                && !this.getItem(FUEL).isEmpty()
                && this.getItem(FUEL).getItem() == Items.BUCKET) {
            this.setItem(FUEL, new ItemStack(Items.WATER_BUCKET));
        }

        if (ModUtils.isModLoaded("pmmo")) {
            //~ if >1.20.1 'input' -> 'input, result.copyWithCount(totalOutput)'
            FurnaceHandler.handle(new FurnaceBurnEvent(input, result.copyWithCount(totalOutput), level, worldPosition));
        }

        input.shrink(operations);
    }




    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {

        if (tag.get("Owner") != null) {
            owner = tag.getUUID("Owner");
        }

        tag.getBoolean("RainbowGen");

        for (int i = 0; i < factoryCookTime.length; i++) {
            int[] tagArr = tag.getIntArray("FactoryCookTime");
            if (tagArr.length == factoryCookTime.length) {
                factoryCookTime[i] = tagArr[i];
            }
        }
        for (int i = 0; i < factoryTotalCookTime.length; i++) {
            int[] tagArr = tag.getIntArray("FactoryTotalCookTime");
            if (tagArr.length == factoryTotalCookTime.length) {
                factoryTotalCookTime[i] = tagArr[i];
            }
        }
        for (int i = 0; i < usedRF.length; i++) {
            double tagRF = tag.getDouble("UsedRF" + i);
            usedRF[i] = tagRF;
        }

        generatorBurn = tag.getDouble("GeneratorBurn");
        generatorRecentRecipeRF = tag.getInt("GeneratorRecent");
        gottenRF = tag.getDouble("GottenRF");

        furnaceBurnTime = tag.getInt("BurnTime");
        cookTime = tag.getInt("CookTime");
        totalCookTime = tag.getInt("CookTimeTotal");
        currentAugment = tag.getIntArray("Augment");
        jovial = tag.getInt("Jovial");
        recipesUsed = this.getBurnTime(this.getItem(1), recipeType);
        CompoundTag compoundnbt = tag.getCompound("RecipesUsed");

        for (String s : compoundnbt.getAllKeys()) {
            recipes.put(IronFurnaces.parse(s), compoundnbt.getInt(s));
        }
        furnaceSettings.read(tag);

        energyStorage.receiveEnergy(tag.getInt("Energy"), false);
        lastGameTickEnergyUpdated = 0;

        super.loadAdditional(tag, registries);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (owner != null) {
            tag.putUUID("Owner", owner);
        }
        tag.putBoolean("RainbowGen", rainbowGenerating);
        tag.putIntArray("FactoryCookTime", factoryCookTime);
        tag.putIntArray("FactoryTotalCookTime", factoryTotalCookTime);
        for (int i = 0; i < usedRF.length; i++) {
            tag.putDouble("UsedRF" + i, usedRF[i]);
        }

        tag.putDouble("GeneratorBurn", generatorBurn);
        tag.putInt("GeneratorRecent", generatorRecentRecipeRF);
        tag.putDouble("GottenRF", gottenRF);

        tag.putInt("BurnTime", furnaceBurnTime);
        tag.putInt("CookTime", cookTime);
        tag.putInt("CookTimeTotal", totalCookTime);
        tag.putIntArray("Augment", currentAugment);
        tag.putInt("Jovial", jovial);
        furnaceSettings.write(tag);


        tag.putInt("Energy", getEnergy());


        CompoundTag compoundnbt = new CompoundTag();
        recipes.forEach((recipeId, craftedAmount) -> {
            compoundnbt.putInt(recipeId.toString(), craftedAmount);
        });
        tag.put("RecipesUsed", compoundnbt);


        //tag.putString("SavedPlayer", savedPlayer.getStringUUID());
    }

    public static int getBurnTime(ItemStack stack, RecipeType recipeType) {
        return FuelBurnTimeUtil.getBurnTime(stack, recipeType);
    }


    public static boolean isItemFuel(ItemStack stack, RecipeType recipeType) {
        return getBurnTime(stack, recipeType) > 0 || stack.getItem() instanceof ItemHeater;
    }

    public static boolean isItemAugment(ItemStack stack, int type) {
        if (type == 0) {
            return stack.getItem() instanceof ItemAugmentRed;
        }
        if (type == 1) {
            return stack.getItem() instanceof ItemAugmentGreen;
        }
        if (type == 2) {
            return stack.getItem() instanceof ItemAugmentBlue;
        }
        return stack.getItem() instanceof ItemAugment;
    }
    //? forge {

    /^LazyOptional<? extends IItemHandler>[] invHandlers =
            SidedInvWrapper.create(this, Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {

        if (!this.isRemoved() && facing != null && capability == ForgeCapabilities.ITEM_HANDLER) {
            if (facing == Direction.DOWN)
                return invHandlers[0].cast();
            else if (facing == Direction.UP)
                return invHandlers[1].cast();
            else if (facing == Direction.NORTH)
                return invHandlers[2].cast();
            else if (facing == Direction.SOUTH)
                return invHandlers[3].cast();
            else if (facing == Direction.WEST)
                return invHandlers[4].cast();
            else
                return invHandlers[5].cast();
        }
        if (!this.isRemoved() && capability == ForgeCapabilities.ENERGY && (isGenerator() || isFactory())) {
            return energyStorage.getStorage().cast();
        }
        return super.getCapability(capability, facing);
    }
    ^///? } else {

    //?}


    @Override
    public int[] IgetSlotsForFace(Direction side) {
        if (isFurnace()) {
            if (furnaceSettings.get(DirectionUtil.getId(side)) == 0) {
                return new int[]{};
            } else if (furnaceSettings.get(DirectionUtil.getId(side)) == 1) {
                return new int[]{0, 1};
            } else if (furnaceSettings.get(DirectionUtil.getId(side)) == 2) {
                return new int[]{2};
            } else if (furnaceSettings.get(DirectionUtil.getId(side)) == 3) {
                return new int[]{0, 1, 2};
            } else if (furnaceSettings.get(DirectionUtil.getId(side)) == 4) {
                return new int[]{1};
            }
        } else if (isGenerator()) {
            if (furnaceSettings.get(DirectionUtil.getId(side)) == 4) {
                return new int[]{6};
            }
        } else if (isFactory()) {
            if (furnaceSettings.get(DirectionUtil.getId(side)) == 0) {
                return new int[]{};
            } else if (furnaceSettings.get(DirectionUtil.getId(side)) == 1) {
                return FACTORY_INPUT;
            } else if (furnaceSettings.get(DirectionUtil.getId(side)) == 2) {
                return new int[]{13, 14, 15, 16, 17, 18};
            } else if (furnaceSettings.get(DirectionUtil.getId(side)) == 3) {
                return new int[]{7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18};
            }
        }

        return new int[]{};
    }

    @Override
    public boolean IcanExtractItem(int index, ItemStack stack, Direction direction) {
        if (isFurnace()) {
            if (furnaceSettings.get(DirectionUtil.getId(direction)) == 0) {
                return false;
            } else if (furnaceSettings.get(DirectionUtil.getId(direction)) == 1) {
                return false;
            } else if (furnaceSettings.get(DirectionUtil.getId(direction)) == 2) {
                return index == 2;
            } else if (furnaceSettings.get(DirectionUtil.getId(direction)) == 3) {
                return index == 2;
            } else if (furnaceSettings.get(DirectionUtil.getId(direction)) == 4 && stack.getItem() != Items.BUCKET) {
                return false;
            } else if (furnaceSettings.get(DirectionUtil.getId(direction)) == 4 && stack.getItem() == Items.BUCKET) {
                return true;
            }
        } else if (isGenerator()) {
            if (furnaceSettings.get(DirectionUtil.getId(direction)) == 4 && stack.getItem() == Items.BUCKET) {
                return true;
            }
        } else if (isFactory()) {
            if (furnaceSettings.get(DirectionUtil.getId(direction)) == 2) {
                return index >= 13 && index <= 18;
            } else if (furnaceSettings.get(DirectionUtil.getId(direction)) == 3) {
                return index >= 13 && index <= 18;
            }
        }
        return false;
    }

    @Override
    public boolean IisItemValidForSlot(int index, ItemStack stack) {
        if (isFurnace()) {
            if (index == OUTPUT || index == 3 || index == 4 || index == 5) {
                return false;
            }
            if (index == INPUT) {
                if (stack.isEmpty()) {
                    return false;
                }

                return hasRecipe(stack);
            }
            if (index == FUEL) {
                ItemStack itemstack = this.getItem(FUEL);
                return getBurnTime(stack, recipeType) > 0 || (stack.getItem() == Items.BUCKET && itemstack.getItem() != Items.BUCKET) || stack.getItem() instanceof ItemHeater;
            }
        } else if (isGenerator()) {
            if (index == GENERATOR_FUEL) {
                if (getItem(AUGMENT_RED).getItem() instanceof ItemAugmentSmoking && getSmokingBurn(stack) > 0) {
                    return true;
                }
                if (getItem(AUGMENT_RED).getItem() instanceof ItemAugmentBlasting && hasGeneratorBlastingRecipe(stack)) {
                    return true;
                }
                if (getItem(AUGMENT_RED).isEmpty() && getBurnTime(stack, recipeType) > 0) {
                    return true;
                }
                if (stack.getItem() instanceof ItemHeater) {
                    return false;
                }
            }
        } else if (isFactory()) {
            if ((index >= 13 && index <= 18) || index == 3 || index == 4 || index == 5) {
                return false;
            }
            if (index >= 7 && index <= 12) {
                if (stack.isEmpty()) {
                    return false;
                }
                if (getTier() == 0) {
                    if (index >= 9 && index <= 10) {
                        return hasRecipe(stack);
                    } else {
                        return false;
                    }
                } else if (getTier() == 1) {
                    if (index >= 8 && index <= 11) {
                        return hasRecipe(stack);
                    } else {
                        return false;
                    }
                }
                return hasRecipe(stack);
            }
        }
        return false;
    }

    public void setJovial(int value) {
        jovial = value;
    }

    public int getXpNeededForNextLevel(int experienceLevel) {
        if (experienceLevel >= 30) {
            return 112 + (experienceLevel - 30) * 9;
        } else {
            return experienceLevel >= 15 ? 37 + (experienceLevel - 15) * 5 : 7 + experienceLevel * 2;
        }
    }


    public int getXpNeededForLevel(int level) {
        int xp = 0;
        for (int i = 0; i < level; i++) {
            xp += getXpNeededForNextLevel(i);
        }
        return xp + 1;
    }

    //? forge {

    /^@Override
    public void setRecipeUsed(@Nullable RecipeHolder<?> recipe) {

        if (recipe != null) {
            Identifier resourcelocation = recipe.getId();
            if (recipe instanceof AbstractCookingRecipe cookingRecipe) {
                float xpRecipe = cookingRecipe.getExperience();
                if ( ((recipes.getInt(resourcelocation) + 1) * xpRecipe) <= getXpNeededForLevel(Config.recipeMaxXPLevel.get()) + 1)
                {
                    recipes.addTo(resourcelocation, 1);
                }
            }
        }
    }
    @Nullable
    @Override
    public RecipeHolder<?> getRecipeUsed() {
        return null;
    }

    ^///? } else {
    @Override
    public void setRecipeUsed(@Nullable RecipeHolder<?> recipe) {
        if (recipe != null) {
            Identifier resourcelocation = recipe.id();
            //~ if >1.20.1 'recipe instanceof' -> 'recipe.value() instanceof'
            if (recipe.value() instanceof AbstractCookingRecipe cookingRecipe) {
                float xpRecipe = cookingRecipe.getExperience();
                if (((recipes.getInt(resourcelocation) + 1) * xpRecipe) <= getXpNeededForLevel(Config.recipeMaxXPLevel.get()) + 1) {
                    recipes.addTo(resourcelocation, 1);
                }
            }
        }
    }

    @Nullable
    @Override
    public RecipeHolder<?> getRecipeUsed() {
        return null;
    }
//?}

    //~ if !forge 'Recipe<?>' -> 'RecipeHolder<?>' {
    public final Object2IntOpenHashMap<Identifier> recipes = new Object2IntOpenHashMap<>();

    public void unlockRecipes(ServerPlayer player) {
        List<RecipeHolder<?>> list = this.grantStoredRecipeExperience(player.serverLevel(), player.position());
        player.awardRecipes(list);
        recipes.clear();
    }

    public List<RecipeHolder<?>> grantStoredRecipeExperience(ServerLevel level, Vec3 worldPosition) {
        List<RecipeHolder<?>> list = Lists.newArrayList();

        for (Object2IntMap.Entry<Identifier> entry : recipes.object2IntEntrySet()) {
            level.getRecipeManager().byKey(entry.getKey()).ifPresent((h) -> {
                list.add(h);
                splitAndSpawnExperience(level, worldPosition, entry.getIntValue(),
                        //~ if !forge '((AbstractCookingRecipe) h)' -> '((AbstractCookingRecipe) h.value())'
                        ((AbstractCookingRecipe) h.value())
                                .getExperience());
            });
        }


        return list;
    }
    //~}
    private static void splitAndSpawnExperience(ServerLevel level, Vec3 worldPosition, int craftedAmount, float experience) {
        int i = Mth.floor((float) craftedAmount * experience);
        float f = Mth.frac((float) craftedAmount * experience);
        if (f != 0.0F && Math.random() < (double) f) {
            ++i;
        }
        ExperienceOrb.award(level, worldPosition, i);

    }

    @Override
    public void fillStackedContents(StackedContents helper) {
        for (ItemStack itemstack : this.inventory) {
            helper.accountStack(itemstack);
        }

    }

    protected boolean doesNeedUpdateSend() {
        return !Arrays.equals(this.provides, this.lastProvides);
    }

    public void onUpdateSent() {
        System.arraycopy(this.provides, 0, this.lastProvides, 0, this.provides.length);
        this.level.updateNeighborsAt(this.worldPosition, getBlockState().getBlock());
    }


    public void placeConfig() {

        if (furnaceSettings != null) {
            furnaceSettings.set(0, 2);
            furnaceSettings.set(1, 1);
            for (Direction dir : Direction.values()) {
                if (dir != Direction.DOWN && dir != Direction.UP) {
                    furnaceSettings.set(dir.ordinal(), 4);
                }
            }
            level.markAndNotifyBlock(worldPosition, level.getChunkAt(worldPosition), level.getBlockState(worldPosition).getBlock().defaultBlockState(), level.getBlockState(worldPosition), 3, 3);
        }

    }

    public boolean isGenerator() {
        return currentAugment[2] == 2;
    }

    public boolean isFactory() {
        return currentAugment[2] == 1;
    }

    public boolean isFurnace() {
        return currentAugment[2] == 0;
    }

    @Override
    public void setRemoved() {
        //? 1.20.1
        //energyStorage.invalidate();
        super.setRemoved();

    }

    public int getTier() {
        return 0;
    }
}

*///?}