package ironfurnaces.tileentity.furnaces;

import com.clefal.nirvana_lib.utils.ModUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import harmonised.pmmo.api.events.FurnaceBurnEvent;
import harmonised.pmmo.events.impl.FurnaceHandler;
import ironfurnaces.Config;
import ironfurnaces.adaptor.energy.EnergyWrapper;
import ironfurnaces.blocks.furnaces.BlockMillionFurnace;
import ironfurnaces.capability.ModCapabilities;
import ironfurnaces.adaptor.energy.FEnergyStorage;
import ironfurnaces.items.ItemHeater;
import ironfurnaces.items.augments.*;
import ironfurnaces.recipes.GeneratorRecipe;
import ironfurnaces.registration.ModBlockState;
import ironfurnaces.registration.ModItems;
import ironfurnaces.registration.ModCustomRecipe;
import ironfurnaces.tileentity.BlockWirelessEnergyHeaterTile;
import ironfurnaces.tileentity.TileEntityInventory;
import ironfurnaces.util.DirectionUtil;
import ironfurnaces.util.FurnaceSettings;
import ironfurnaces.util.LRUCache;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.AirItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiPredicate;

import static ironfurnaces.init.ModSetup.SMOKING_BURNS;
import static ironfurnaces.init.ModSetup.HAS_RECIPE;
import static ironfurnaces.init.ModSetup.HAS_RECIPE_SMOKING;
import static ironfurnaces.init.ModSetup.HAS_RECIPE_BLASTING;

public abstract class BlockIronFurnaceTileBase extends TileEntityInventory implements RecipeHolder, StackedContentsCompatible {

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

    public final Object2IntOpenHashMap<ResourceLocation> recipes = new Object2IntOpenHashMap<>();
    public RecipeType<? extends AbstractCookingRecipe> recipeType;
    public FurnaceSettings furnaceSettings;
    public LRUCache<Item, Optional<AbstractCookingRecipe>> cache = LRUCache.newInstance(Config.cache_capacity.get());
    public LRUCache<Item, Optional<AbstractCookingRecipe>> blasting_cache = LRUCache.newInstance(Config.cache_capacity.get());
    public LRUCache<Item, Optional<AbstractCookingRecipe>> smoking_cache = LRUCache.newInstance(Config.cache_capacity.get());
    public LRUCache<Item, Optional<GeneratorRecipe>> generator_cache = LRUCache.newInstance(Config.cache_capacity.get());
    public List<LRUCache<Item, Optional<AbstractCookingRecipe>>> factory_cache = Lists.newArrayList(
            LRUCache.newInstance(Config.cache_capacity.get()),
            LRUCache.newInstance(Config.cache_capacity.get()),
            LRUCache.newInstance(Config.cache_capacity.get()),
            LRUCache.newInstance(Config.cache_capacity.get()),
            LRUCache.newInstance(Config.cache_capacity.get()),
            LRUCache.newInstance(Config.cache_capacity.get()));

    public List<LRUCache<Item, Optional<AbstractCookingRecipe>>> factory_blasting_cache = Lists.newArrayList(
            LRUCache.newInstance(Config.cache_capacity.get()),
            LRUCache.newInstance(Config.cache_capacity.get()),
            LRUCache.newInstance(Config.cache_capacity.get()),
            LRUCache.newInstance(Config.cache_capacity.get()),
            LRUCache.newInstance(Config.cache_capacity.get()),
            LRUCache.newInstance(Config.cache_capacity.get()));

    public List<LRUCache<Item, Optional<AbstractCookingRecipe>>> factory_smoking_cache = Lists.newArrayList(
            LRUCache.newInstance(Config.cache_capacity.get()),
            LRUCache.newInstance(Config.cache_capacity.get()),
            LRUCache.newInstance(Config.cache_capacity.get()),
            LRUCache.newInstance(Config.cache_capacity.get()),
            LRUCache.newInstance(Config.cache_capacity.get()),
            LRUCache.newInstance(Config.cache_capacity.get()));


    public EnergyWrapper energyStorage = new EnergyWrapper(Config.furnaceEnergyCapacityTier2.get()).withCallback(fEnergyStorage -> {
        if (level != null && level.getBlockEntity(getBlockPos()) != null)
        {
            if (lastGameTickEnergyUpdated <= 0)
            {
                setChanged();
                lastGameTickEnergyUpdated = level.getGameTime();
            }
            else if (level.getGameTime() - lastGameTickEnergyUpdated >= 20)
            {
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
        return energyStorage.getEnergy();
    }

    public int getCapacity() {
        return energyStorage.getEnergyCapacity();
    }

    public void setEnergy(int energy) {
        energyStorage.setEnergy(energy);
    }

    public void setMaxEnergy(int energy) {
        energyStorage.setEnergy(energy);
    }

    public void removeEnergy(int energy) {
        energyStorage.removeEnergy(energy);
    }

    public boolean hasRecipe(ItemStack stack) {

        Item item = stack.getItem();
        if (recipeType == RecipeType.SMOKING)
        {
            return HAS_RECIPE_SMOKING.computeIfAbsent(ForgeRegistries.ITEMS.getDelegateOrThrow(item), (value) -> this.level.getRecipeManager().getRecipeFor((RecipeType<AbstractCookingRecipe>) recipeType, new SimpleContainer(stack), this.level).isPresent());
        }
        else if (recipeType == RecipeType.BLASTING)
        {
            return HAS_RECIPE_BLASTING.computeIfAbsent(ForgeRegistries.ITEMS.getDelegateOrThrow(item), (value) -> this.level.getRecipeManager().getRecipeFor((RecipeType<AbstractCookingRecipe>) recipeType, new SimpleContainer(stack), this.level).isPresent());

        }
        return HAS_RECIPE.computeIfAbsent(ForgeRegistries.ITEMS.getDelegateOrThrow(item), (value) -> this.level.getRecipeManager().getRecipeFor((RecipeType<AbstractCookingRecipe>) recipeType, new SimpleContainer(stack), this.level).isPresent());


    }


    public boolean hasGeneratorBlastingRecipe(ItemStack stack) {
        return getRecipeGeneratorBlasting(stack).isPresent();
    }

    protected Optional<AbstractCookingRecipe> getRecipe(ItemStack stack) {
        Optional<AbstractCookingRecipe> recipe = getCache().computeIfAbsent(stack.getItem(), (item) -> (stack.getItem() instanceof AirItem)
                ? Optional.empty()
                : Optional.ofNullable(this.level.getRecipeManager().getRecipeFor((RecipeType<AbstractCookingRecipe>) recipeType, new SimpleContainer(stack), this.level).orElse(null)));
        return recipe;
    }

    protected Optional<AbstractCookingRecipe> getRecipeFactory(int slot, ItemStack stack) {
        Optional<AbstractCookingRecipe> recipe = getFactoryCache().get(slot - FACTORY_INPUT[0]).computeIfAbsent(stack.getItem(), (item) -> (stack.getItem() instanceof AirItem)
                ? Optional.empty()
                : Optional.ofNullable(this.level.getRecipeManager().getRecipeFor((RecipeType<AbstractCookingRecipe>) recipeType, new SimpleContainer(stack), this.level).orElse(null)));
        return recipe;
    }

    protected Optional<AbstractCookingRecipe> getRecipeNonCached(ItemStack stack) {
        return stack.getItem() instanceof AirItem
                ? Optional.empty()
                : Optional.ofNullable(this.level.getRecipeManager().getRecipeFor((RecipeType<AbstractCookingRecipe>) recipeType, new SimpleContainer(stack), this.level).orElse(null));
    }

    protected Optional<GeneratorRecipe> getRecipeGeneratorBlasting(ItemStack item) {
        return (item.getItem() instanceof AirItem)
                ? Optional.empty()
                : Optional.ofNullable(this.level.getRecipeManager().getRecipeFor(ModCustomRecipe.GENERATOR_RECIPE.get(), new SimpleContainer(item), this.level).orElse(null));
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

    protected LRUCache<Item, Optional<AbstractCookingRecipe>> getCache() {
        checkRecipeType();
        if (recipeType == RecipeType.BLASTING) {
            return blasting_cache;
        }
        if (recipeType == RecipeType.SMOKING) {
            return smoking_cache;
        }
        return cache;
    }

    protected List<LRUCache<Item, Optional<AbstractCookingRecipe>>> getFactoryCache() {
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
        Optional<AbstractCookingRecipe> recipe = getRecipeNonCached(this.getItem(INPUT));
        if (recipe.isPresent()) {
            AbstractCookingRecipe abstractCookingRecipe = recipe.get();
            int recipe_cooktime = abstractCookingRecipe.getCookingTime();
            double div = 200.0 / recipe_cooktime;
            double i = regular / div;
            return (int)Math.max(1, i);
        }
        else
        {
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
        Optional<AbstractCookingRecipe> recipe = getRecipeNonCached(this.getItem(slot));
        if (recipe.isPresent()) {
            AbstractCookingRecipe abstractCookingRecipe = recipe.get();
            int recipe_cooktime = abstractCookingRecipe.getCookingTime();
            double div = 200.0 / recipe_cooktime;
            double i = regular / div;
            return (int)Math.max(1, i);
        }
        else
        {
            return 0;
        }
    }

    public abstract ForgeConfigSpec.IntValue getCookTimeConfig();

    public UnifiedTileEntity self(){
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

    public static int getSmokingBurn(ItemStack stack)
    {
        if (stack.isEmpty())
        {
            return 0;
        }
        else
        {
            Item item = stack.getItem();
            return SMOKING_BURNS.getOrDefault(ForgeRegistries.ITEMS.getDelegateOrThrow(item), addSmokingBurn(stack));
        }
    }

    public static int addSmokingBurn(ItemStack stack) {
        int burnTime = getSmokingBurnTime(stack);
        Item item = stack.getItem();
        SMOKING_BURNS.put(ForgeRegistries.ITEMS.getDelegateOrThrow(item), burnTime);
        return 0;
    }
    public static int getSmokingBurnTime(ItemStack stack) {
        if (!stack.isEmpty()) {
            if (stack.getItem().getFoodProperties() != null) {
                if (stack.getItem().getFoodProperties().getNutrition() > 0) {
                    return stack.getItem().getFoodProperties().getNutrition() * 800;
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
                    CompoundTag stackTag = getItem(FACTORY_INPUT[j]).getTag();
                    stack = new ItemStack(getItem(FACTORY_INPUT[j]).getItem());
                    stack.setTag(stackTag);
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
            CompoundTag newTag = getItem(countsEntry.getKey()).getTag();
            ItemStack newStack = new ItemStack(getItem(countsEntry.getKey()).getItem(), countsEntry.getValue());
            newStack.setTag(newTag);
            setItem(countsEntry.getKey(), newStack);
            setChanged();
        }


    }


    boolean rainbowCheckFurnaceTiers(List<BlockIronFurnaceTileBase> list)
    {
        if (list.isEmpty())
        {
            return false;
        }
        int check = 0;
        for (BlockIronFurnaceTileBase furnace : list) {
            if (furnace.generatorBurn > 0 && furnace.getEnergy() < furnace.getCapacity()) {
                check++;
            }
        }
        if (check == 0)
        {
            return false;
        }
        return true;
    }

    public static void tick(Level level, BlockPos worldPosition, BlockState blockState, BlockIronFurnaceTileBase furnaceTile) {
            if (!furnaceTile.level.isClientSide) {
                if (furnaceTile.isGenerator())
                {
                    boolean flag3 = false;

                    if (furnaceTile.isRainbowFurnace()) {
                        List<BlockIronFurnaceTileBase> rainbow = new ArrayList<>();
                        List<BlockIronFurnaceTileBase> nonRainbow = new ArrayList<>();
                        if (furnaceTile.owner != null)
                        {
                            flag3 = true;
                            if (level.getPlayerByUUID(furnaceTile.owner) != null)
                            {

                                Set<GlobalPos> furnacesPos = level.getPlayerByUUID(furnaceTile.owner).getCapability(ModCapabilities.FURNACES_LIST).map(h -> h.get()).orElse(new LinkedHashSet<>());
                                if (!furnacesPos.isEmpty())
                                {
                                    for (GlobalPos furnacesPo : furnacesPos) {
                                        BlockPos pos = furnacesPo.pos();
                                        ResourceKey<Level> dimension = furnacesPo.dimension();
                                        ServerLevel targetLevel = level.getServer().getLevel(dimension);
                                        if (targetLevel != null){
                                            targetLevel.getChunkAt(pos).setLoaded(true);
                                            BlockEntity be = targetLevel.getBlockEntity(pos);
                                            if (be instanceof UnifiedTileEntity unifiedTileEntity){
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
                        if (rainbow.size() > 1)
                        {
                            int rainbowGens = 0;
                            for (int i = 0; i < rainbow.size(); i++)
                            {
                                if (rainbow.get(i).isGenerator())
                                {
                                    rainbowGens++;
                                }
                            }
                            if (rainbowGens > 1)
                            {
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
                        }
                        else
                        {
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
                furnaceTile.getCapability(ForgeCapabilities.ENERGY).ifPresent(h -> {
                    if (!h.canReceive()) {
                        ((FEnergyStorage) h).setMaxReceive(h.getMaxEnergyStored());
                    }
                    if (h.canExtract()) {
                        ((FEnergyStorage) h).setMaxExtract(0);
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
                        Optional<AbstractCookingRecipe> irecipe = furnaceTile.getRecipeFactory(slot, furnaceTile.getItem(slot));

                        boolean valid = furnaceTile.canFactorySmelt(irecipe.orElse(null), slot);
                        if (valid) {
                            int energyRecipe = irecipe.get().getCookingTime() * 20;
                            int energy = furnaceTile.getItem(AUGMENT_GREEN).getItem() instanceof ItemAugmentSpeed ?
                                    energyRecipe * 2 : furnaceTile.getItem(AUGMENT_GREEN).getItem() instanceof ItemAugmentFuel ?
                                    energyRecipe / 2 : energyRecipe;
                            if (furnaceTile.getEnergy() >= energy || furnaceTile.factoryCookTime[i] > 0) {
                                furnaceTile.factoryCookTime[i]++;
                                furnaceTile.usedRF[i] += (double) (energy / furnaceTile.factoryTotalCookTime[i]);
                                furnaceTile.setEnergy((int) (furnaceTile.getEnergy() - (double) (energy / furnaceTile.factoryTotalCookTime[i])));
                                if (level.getBlockState(furnaceTile.getBlockPos()).getValue(BlockStateProperties.LIT) != furnaceTile.isFactoryCooking()) {
                                    level.setBlock(worldPosition, level.getBlockState(worldPosition).setValue(BlockStateProperties.LIT, furnaceTile.isFactoryCooking()), 3);
                                }
                                if (furnaceTile.factoryCookTime[i] >= furnaceTile.factoryTotalCookTime[i]) {
                                    furnaceTile.factoryCookTime[i] = 0;
                                    if (furnaceTile.usedRF[i] < energy) {
                                        double diff = energy - furnaceTile.usedRF[i];
                                        furnaceTile.setEnergy((int) (furnaceTile.getEnergy() - diff));
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
                            for (int j = 0; j < FACTORY_INPUT.length; j++)
                            {
                                int outputSlot = FACTORY_INPUT[j] + 6;
                                if (!furnaceTile.getItem(outputSlot).isEmpty() && furnaceTile.getItem(outputSlot).getCount() >= 64)
                                {
                                    furnaceTile.autoFactoryIO();
                                }
                            }

                        }
                    }
                }
            }
        } else if (furnaceTile.isGenerator()) {
            if (!level.isClientSide) {
                furnaceTile.getCapability(ForgeCapabilities.ENERGY).ifPresent(h -> {
                    if (h.canReceive()) {
                        ((FEnergyStorage) h).setMaxReceive(0);
                    }
                    if (!h.canExtract()) {
                        ((FEnergyStorage) h).setMaxExtract(h.getMaxEnergyStored());
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

                        furnaceTile.setEnergy(furnaceTile.getEnergy() + furnaceTile.getGeneration());
                        if (furnaceTile.generatorBurn - (double) ((double) furnaceTile.getGeneration() / 20) <= 0) {
                            if (furnaceTile.gottenRF + furnaceTile.getGeneration() > max && furnaceTile.gottenRF + furnaceTile.getGeneration() < furnaceTile.getCapacity()) {
                                int diff = (int) (furnaceTile.gottenRF + furnaceTile.getGeneration() - max);
                                furnaceTile.setEnergy(furnaceTile.getEnergy() + furnaceTile.getGeneration());
                                furnaceTile.removeEnergy(diff);
                            }
                            if (furnaceTile.gottenRF + furnaceTile.getGeneration() < max) {
                                int diff = (int) (max - furnaceTile.gottenRF + furnaceTile.getGeneration());
                                furnaceTile.setEnergy(furnaceTile.getEnergy() + furnaceTile.getGeneration());
                                furnaceTile.setEnergy(furnaceTile.getEnergy() + diff);
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
            furnaceTile.getCapability(ForgeCapabilities.ENERGY).ifPresent(h -> {
                if (h.canReceive()) {
                    ((FEnergyStorage) h).setMaxReceive(0);
                }
                if (h.canExtract()) {
                    ((FEnergyStorage) h).setMaxExtract(0);
                }
            });

            if (!furnaceTile.level.isClientSide) {
                if (furnaceTile.isBurning()) {
                    --furnaceTile.furnaceBurnTime;
                }
                furnaceTile.checkRecipeType();

                ItemStack itemstack = furnaceTile.getItem(FUEL);
                if (furnaceTile.isBurning() || !itemstack.isEmpty() && !furnaceTile.getItem(INPUT).isEmpty()) {
                    Optional<AbstractCookingRecipe> irecipe = Optional.empty();
                    if (!furnaceTile.getItem(INPUT).isEmpty()) {
                        irecipe = furnaceTile.getRecipe(furnaceTile.getItem(INPUT));
                    }

                    boolean valid = furnaceTile.canSmelt(irecipe.orElse(null));
                    if (!furnaceTile.isBurning() && valid) {
                        if (itemstack.getItem() instanceof ItemHeater) {
                            if (itemstack.hasTag()) {
                                int x = itemstack.getTag().getInt("X");
                                int y = itemstack.getTag().getInt("Y");
                                int z = itemstack.getTag().getInt("Z");
                                BlockEntity te = level.getBlockEntity(new BlockPos(x, y, z));
                                if (te instanceof BlockWirelessEnergyHeaterTile heaterTile) {
                                    int energy = heaterTile.getWrapper().getEnergy();
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
                                            ((BlockWirelessEnergyHeaterTile) te).getWrapper().removeEnergy(2000);

                                        furnaceTile.recipesUsed = furnaceTile.furnaceBurnTime;
                                    }
                                }
                            }
                        } else {
                            if (!furnaceTile.getItem(AUGMENT_GREEN).isEmpty())
                            {
                                if (furnaceTile.getItem(AUGMENT_GREEN).getItem() instanceof ItemAugmentFuel)
                                {
                                    furnaceTile.furnaceBurnTime = (getBurnTime(itemstack, furnaceTile.recipeType) * furnaceTile.getCookTime() / 200) * 2;
                                }
                                else if (furnaceTile.getItem(AUGMENT_GREEN).getItem() instanceof ItemAugmentSpeed)
                                {
                                    furnaceTile.furnaceBurnTime = (getBurnTime(itemstack, furnaceTile.recipeType) * furnaceTile.getCookTime() / 200) / 2;

                                }
                            }
                            else
                            {
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

                        if (!furnaceTile.getItem(OUTPUT).isEmpty() && furnaceTile.getItem(OUTPUT).getCount() >= 64)
                        {
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

    protected void rainbowEnergyOut()
    {
        Map<BlockEntity, Direction> tiles = Maps.newHashMap();
        for (Direction dir : Direction.values()) {
            BlockEntity tile = level.getBlockEntity(worldPosition.offset(dir.getNormal()));
            if (tile == null) {
                continue;
            }
            if (furnaceSettings.get(dir.ordinal()) == 2 || furnaceSettings.get(dir.ordinal()) == 3) {
                IEnergyStorage other = tile.getCapability(ForgeCapabilities.ENERGY, dir.getOpposite()).map(other1 -> other1).orElse(null);
                if (other == null) {
                    continue;
                }
                if (other.canReceive() && other.getEnergyStored() < other.getMaxEnergyStored()) {
                    tiles.put(tile, dir.getOpposite());
                }
            }
        }
        for (Map.Entry<BlockEntity, Direction> entry : tiles.entrySet()) {
            int energy = Config.millionFurnacePowerToGenerate.get() / tiles.size();
            entry.getKey().getCapability(ForgeCapabilities.ENERGY, entry.getValue()).ifPresent(h -> h.receiveEnergy(energy, false));
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
                IEnergyStorage other = tile.getCapability(ForgeCapabilities.ENERGY, dir.getOpposite()).map(other1 -> other1).orElse(null);
                if (other == null) {
                    continue;
                }
                if (other.canReceive() && other.getEnergyStored() < other.getMaxEnergyStored()) {
                    tiles.put(tile, dir.getOpposite());
                }
            }
        }
        for (Map.Entry<BlockEntity, Direction> entry : tiles.entrySet()) {
            int energy = Math.min(getCapability(ForgeCapabilities.ENERGY).map(h -> ((FEnergyStorage) h).getMaxEnergyStored()).orElse(0), getEnergy()) / tiles.size();
            entry.getKey().getCapability(ForgeCapabilities.ENERGY, entry.getValue()).ifPresent(
                    h -> {
                        removeEnergy(h.receiveEnergy(energy, false));
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
                    IItemHandler other = tile.getCapability(ForgeCapabilities.ITEM_HANDLER, dir.getOpposite()).orElse(null);

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
                                        if (hasRecipe(stack) && getItem(INPUT).isEmpty() || ItemHandlerHelper.canItemStacksStack(getItem(INPUT), stack)) {
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
                                        if (isItemFuel(stack, recipeType) && getItem(FUEL).isEmpty() || ItemHandlerHelper.canItemStacksStack(getItem(FUEL), stack)) {
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
                                        if (other.isItemValid(i, stack) && (other.getStackInSlot(i).isEmpty() || (ItemHandlerHelper.canItemStacksStack(other.getStackInSlot(i), stack) && other.getStackInSlot(i).getCount() + stack.getCount() <= other.getSlotLimit(i)))) {
                                            boolean check = other.insertItem(i, extractItemInternal(FUEL, stack.getCount(), true), true).isEmpty();
                                            if (check) other.insertItem(i, extractItemInternal(FUEL, stack.getCount(), false), false);
                                        }
                                    }
                                }

                                if (furnaceSettings.get(dir.ordinal()) == 2 || furnaceSettings.get(dir.ordinal()) == 3) {
                                    if (this.getItem(OUTPUT).isEmpty()) {
                                        continue;
                                    }
                                    for (int i = 0; i < other.getSlots(); i++) {
                                        ItemStack stack = extractItemInternal(OUTPUT, other.getSlotLimit(i) - other.getStackInSlot(i).getCount(), true);
                                        if (other.isItemValid(i, stack) && (other.getStackInSlot(i).isEmpty() || (ItemHandlerHelper.canItemStacksStack(other.getStackInSlot(i), stack) && other.getStackInSlot(i).getCount() + stack.getCount() <= other.getSlotLimit(i)))) {
                                            boolean check = other.insertItem(i, extractItemInternal(OUTPUT, stack.getCount(), true), true).isEmpty();
                                            if (check) other.insertItem(i, extractItemInternal(OUTPUT, stack.getCount(), false), false);
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
                    IItemHandler other = tile.getCapability(ForgeCapabilities.ITEM_HANDLER, dir.getOpposite()).map(other1 -> other1).orElse(null);

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
                                    if (other.getStackInSlot(i).getItem() == Items.BUCKET)
                                    {
                                        continue;
                                    }
                                    ItemStack stack = other.extractItem(i, other.getStackInSlot(i).getMaxStackSize(), true);
                                    if (stack.getItem() instanceof ItemHeater)
                                    {
                                        continue;
                                    }
                                    if (isItemFuel(stack, recipeType) && getItem(GENERATOR_FUEL).isEmpty() || ItemHandlerHelper.canItemStacksStack(getItem(GENERATOR_FUEL), stack)) {
                                        insertItemInternal(GENERATOR_FUEL, other.extractItem(i, other.getStackInSlot(i).getMaxStackSize() - this.getItem(GENERATOR_FUEL).getCount(), false), false);
                                    }
                                }
                            }
                        }
                        if (this.getAutoOutput() != 0)
                        {
                            if (furnaceSettings.get(dir.ordinal()) == 4)
                            {
                                if (this.getItem(GENERATOR_FUEL).isEmpty()) {
                                    continue;
                                }
                                if (!isItemFuel(getItem(GENERATOR_FUEL), recipeType)) {
                                    for (int i = 0; i < other.getSlots(); i++) {
                                        ItemStack stack = extractItemInternal(GENERATOR_FUEL, this.getItem(GENERATOR_FUEL).getMaxStackSize() - other.getStackInSlot(i).getCount(), true);
                                        if (other.isItemValid(i, stack) && (other.getStackInSlot(i).isEmpty() || (ItemHandlerHelper.canItemStacksStack(other.getStackInSlot(i), stack) && other.getStackInSlot(i).getCount() + stack.getCount() <= other.getSlotLimit(i)))) {
                                            boolean check = other.insertItem(i, extractItemInternal(GENERATOR_FUEL, stack.getCount(), true), true).isEmpty();
                                            if (check) other.insertItem(i, extractItemInternal(GENERATOR_FUEL, stack.getCount(), false), false);
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
                    IItemHandler other = tile.getCapability(ForgeCapabilities.ITEM_HANDLER, dir.getOpposite()).map(other1 -> other1).orElse(null);

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
                                                if (check) other.insertItem(i, extractItemInternal(FACTORY_INPUT[j] + 6, stack.getCount(), false), false);
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

    public static boolean canItemStacksStack(@NotNull ItemStack a, @NotNull ItemStack b)
    {
        /*
        if (a.isEmpty() || !ItemStack.isSameItem(a, b) || a.hasTag() != b.hasTag())
            return false;

        return (!a.hasTag() || a.getTag().equals(b.getTag()));
        */
        return ItemHandlerHelper.canItemStacksStack(a, b);
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
            if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
                return stack;

            limit -= existing.getCount();
        }

        if (limit <= 0)
            return stack;

        boolean reachedLimit = stack.getCount() > limit;

        if (!simulate) {
            if (existing.isEmpty()) {
                this.setItem(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
            } else {
                existing.grow(reachedLimit ? limit : stack.getCount());
            }
            this.setChanged();
        }

        return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
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
                this.setItem(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
                this.setChanged();
            }

            return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
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

    protected void smelt(@Nullable Recipe<?> recipe) {
        smeltItem(recipe, this.self().getMaxSmeltItemNumberOnSingleOp());
    }

    protected void factorySmelt(@Nullable Recipe<?> recipe, int slot) {
        smeltFactoryItem(recipe, slot, this.self().getMaxSmeltItemNumberOnSingleOp());
    }

    protected boolean canSmelt(@Nullable Recipe<?> recipe) {
        return canSmeltInternal(recipe, 0, OUTPUT, true);
    }


    protected boolean canFactorySmelt(@Nullable Recipe<?> recipe, int slot) {
        return canSmeltInternal(recipe, slot, OUTPUT, false);
    }

    protected boolean canSmeltInternal(
            @Nullable Recipe<?> recipe,
            int inputSlot,
            int outputSlot,
            boolean limitTo64
    ) {
        ItemStack input = this.getItem(inputSlot);
        if (input.isEmpty() || recipe == null) {
            return false;
        }

        ItemStack recipeOutput = recipe.getResultItem(RegistryAccess.EMPTY);
        if (recipeOutput.isEmpty()) {
            return false;
        }

        ItemStack output = this.getItem(outputSlot);

        if (output.isEmpty()) {
            return true;
        }

        if (!ItemStack.isSameItemSameTags(output, recipeOutput)) {
            return false;
        }

        int maxStack = limitTo64
                ? Math.min(output.getMaxStackSize(), 64)
                : output.getMaxStackSize();

        return output.getCount() + recipeOutput.getCount() <= maxStack;
    }


    protected void smeltItem(@Nullable Recipe<?> recipe, int maxOperations) {
        smeltInternal(recipe, INPUT, OUTPUT, maxOperations,
                (r, slot) -> this.canSmelt(r));
    }

    protected void smeltFactoryItem(@Nullable Recipe<?> recipe,
                                    int slot,
                                    int maxOperations) {
        smeltInternal(recipe, slot, slot + 6, maxOperations,
                this::canFactorySmelt);
    }

    private void smeltInternal(
            @Nullable Recipe<?> recipe,
            int inputSlot,
            int outputSlot,
            int maxOperations,
            BiPredicate<Recipe<?>, Integer> canSmeltCheck
    ) {
        if (recipe == null || !canSmeltCheck.test(recipe, inputSlot)) {
            return;
        }

        ItemStack input = this.getItem(inputSlot);
        ItemStack result = recipe.getResultItem(RegistryAccess.EMPTY);
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
            handleSmeltedPMMO(input, level, worldPosition);
        }

        input.shrink(operations);
    }


    private void handleSmeltedPMMO(ItemStack stack, Level level, BlockPos pos) {
        FurnaceHandler.handle(new FurnaceBurnEvent(stack, level, pos));
    }


    @Override
    public void load(CompoundTag tag) {

        if (tag.get("Owner") != null)
        {
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
            recipes.put(new ResourceLocation(s), compoundnbt.getInt(s));
        }
        furnaceSettings.read(tag);

        setEnergy(tag.getInt("Energy"));
        lastGameTickEnergyUpdated = 0;

        super.load(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (owner != null)
        {
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
        return ForgeHooks.getBurnTime(stack, recipeType);
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

    LazyOptional<? extends IItemHandler>[] invHandlers =
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
                if (stack.getItem() instanceof ItemHeater)
                {
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
                if (getTier() == 0)
                {
                    if (index >= 9 && index <= 10)
                    {
                        return hasRecipe(stack);
                    }
                    else
                    {
                        return false;
                    }
                }
                else if (getTier() == 1)
                {
                    if (index >= 8 && index <= 11)
                    {
                        return hasRecipe(stack);
                    }
                    else
                    {
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


    public int getXpNeededForLevel(int level)
    {
        int xp = 0;
        for (int i = 0; i < level; i++)
        {
            xp += getXpNeededForNextLevel(i);
        }
        return xp + 1;
    }

    @Override
    public void setRecipeUsed(@Nullable Recipe<?> recipe) {

        if (recipe != null) {
            ResourceLocation resourcelocation = recipe.getId();
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
    public Recipe<?> getRecipeUsed() {
        return null;
    }

    public void unlockRecipes(ServerPlayer player) {
        List<Recipe<?>> list = this.grantStoredRecipeExperience(player.serverLevel(), player.position());
        player.awardRecipes(list);
        recipes.clear();
    }

    public List<Recipe<?>> grantStoredRecipeExperience(ServerLevel level, Vec3 worldPosition) {
        List<Recipe<?>> list = Lists.newArrayList();

        for (Object2IntMap.Entry<ResourceLocation> entry : recipes.object2IntEntrySet()) {
            level.getRecipeManager().byKey(entry.getKey()).ifPresent((h) -> {
                list.add(h);
                splitAndSpawnExperience(level, worldPosition, entry.getIntValue(), ((AbstractCookingRecipe) h).getExperience());
            });
        }


        return list;
    }

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
        energyStorage.invalidate();
        super.setRemoved();

    }

    public int getTier() {
        return 0;
    }
}
