package ironfurnaces.init;

import ironfurnaces.blocks.BlockWirelessEnergyHeater;
import ironfurnaces.items.*;
import ironfurnaces.items.upgrades.*;
import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.recipes.GeneratorRecipe;
import ironfurnaces.recipes.SimpleGeneratorRecipe;
import ironfurnaces.tileentity.BlockWirelessEnergyHeaterTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static ironfurnaces.loaders.IronFurnaces.MOD_ID;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class Registration {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MOD_ID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);
    public static final String GENERATOR_ID = "generator_blasting";
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    /*
    public static final RegistryObject<BlockWirelessEnergyHeater> HEATER = BLOCKS.register(BlockWirelessEnergyHeater.HEATER, () -> new BlockWirelessEnergyHeater(Block.Properties.copy(Blocks.IRON_BLOCK)));*/
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);




    //public static final RegistryObject<Item> IRON_FURNACE_ITEM = ITEMS.register(BlockIronFurnace.IRON_FURNACE, () -> new ItemFurnace(LegacyFurnaceBlocks.IRON_FURNACE.get(), new Item.Properties(), Config.ironFurnaceSpeed.get()));
    //public static final RegistryObject<BlockEntityType<BlockIronFurnaceTile>> IRON_FURNACE_TILE = TILES.register(BlockIronFurnace.IRON_FURNACE, () -> BlockEntityType.Builder.of(BlockIronFurnaceTile::new, LegacyFurnaceBlocks.IRON_FURNACE.get()).build(null));

    /*public static final RegistryObject<MenuType<BlockIronFurnaceContainer>> IRON_FURNACE_CONTAINER = CONTAINERS.register(BlockIronFurnace.IRON_FURNACE, () -> IForgeMenuType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        Level world = inv.player.getCommandSenderWorld();
        return new BlockIronFurnaceContainer(windowId, world, pos, inv, inv.player);
    }));*/


    //public static final RegistryObject<Item> GOLD_FURNACE_ITEM = ITEMS.register(BlockGoldFurnace.GOLD_FURNACE, () -> new ItemFurnace(LegacyFurnaceBlocks.GOLD_FURNACE.get(), new Item.Properties(), Config.goldFurnaceSpeed.get()));
    /*public static final RegistryObject<BlockEntityType<BlockGoldFurnaceTile>> GOLD_FURNACE_TILE = TILES.register(BlockGoldFurnace.ID, () -> BlockEntityType.Builder.of(BlockGoldFurnaceTile::new, LegacyFurnaceBlocks.GOLD_FURNACE.get()).build(null));

    public static final RegistryObject<MenuType<BlockGoldFurnaceContainer>> GOLD_FURNACE_CONTAINER = CONTAINERS.register(BlockGoldFurnace.ID, () -> IForgeMenuType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        Level world = inv.player.getCommandSenderWorld();
        return new BlockGoldFurnaceContainer(windowId, world, pos, inv, inv.player);
    }));*/

    //public static final RegistryObject<Item> DIAMOND_FURNACE_ITEM = ITEMS.register(BlockDiamondFurnace.DIAMOND_FURNACE, () -> new ItemFurnace(LegacyFurnaceBlocks.DIAMOND_FURNACE.get(), new Item.Properties(), Config.diamondFurnaceSpeed.get()));
    /*public static final RegistryObject<BlockEntityType<BlockDiamondFurnaceTile>> DIAMOND_FURNACE_TILE = TILES.register(BlockDiamondFurnace.ID, () -> BlockEntityType.Builder.of(BlockDiamondFurnaceTile::new, LegacyFurnaceBlocks.DIAMOND_FURNACE.get()).build(null));

    public static final RegistryObject<MenuType<BlockDiamondFurnaceContainer>> DIAMOND_FURNACE_CONTAINER = CONTAINERS.register(BlockDiamondFurnace.ID, () -> IForgeMenuType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        Level world = inv.player.getCommandSenderWorld();
        return new BlockDiamondFurnaceContainer(windowId, world, pos, inv, inv.player);
    }));*/

    //public static final RegistryObject<Item> EMERALD_FURNACE_ITEM = ITEMS.register(BlockEmeraldFurnace.EMERALD_FURNACE, () -> new ItemFurnace(LegacyFurnaceBlocks.EMERALD_FURNACE.get(), new Item.Properties(), Config.emeraldFurnaceSpeed.get()));
    /*public static final RegistryObject<BlockEntityType<BlockEmeraldFurnaceTile>> EMERALD_FURNACE_TILE = TILES.register(BlockEmeraldFurnace.ID, () -> BlockEntityType.Builder.of(BlockEmeraldFurnaceTile::new, LegacyFurnaceBlocks.EMERALD_FURNACE.get()).build(null));

    public static final RegistryObject<MenuType<BlockEmeraldFurnaceContainer>> EMERALD_FURNACE_CONTAINER = CONTAINERS.register(BlockEmeraldFurnace.ID, () -> IForgeMenuType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        Level world = inv.player.getCommandSenderWorld();
        return new BlockEmeraldFurnaceContainer(windowId, world, pos, inv, inv.player);
    }));*/

    //public static final RegistryObject<Item> OBSIDIAN_FURNACE_ITEM = ITEMS.register(BlockObsidianFurnace.OBSIDIAN_FURNACE, () -> new ItemFurnace(LegacyFurnaceBlocks.OBSIDIAN_FURNACE.get(), new Item.Properties(), Config.obsidianFurnaceSpeed.get()));
    /*public static final RegistryObject<BlockEntityType<BlockObsidianFurnaceTile>> OBSIDIAN_FURNACE_TILE = TILES.register(BlockObsidianFurnace.ID, () -> BlockEntityType.Builder.of(BlockObsidianFurnaceTile::new, LegacyFurnaceBlocks.OBSIDIAN_FURNACE.get()).build(null));

    public static final RegistryObject<MenuType<BlockObsidianFurnaceContainer>> OBSIDIAN_FURNACE_CONTAINER = CONTAINERS.register(BlockObsidianFurnace.ID, () -> IForgeMenuType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        Level world = inv.player.getCommandSenderWorld();
        return new BlockObsidianFurnaceContainer(windowId, world, pos, inv, inv.player);
    }));*/

    //public static final RegistryObject<Item> CRYSTAL_FURNACE_ITEM = ITEMS.register(BlockCrystalFurnace.CRYSTAL_FURNACE, () -> new ItemFurnace(LegacyFurnaceBlocks.CRYSTAL_FURNACE.get(), new Item.Properties(), Config.crystalFurnaceSpeed.get()));
    /*public static final RegistryObject<BlockEntityType<BlockCrystalFurnaceTile>> CRYSTAL_FURNACE_TILE = TILES.register(BlockCrystalFurnace.ID, () -> BlockEntityType.Builder.of(BlockCrystalFurnaceTile::new, LegacyFurnaceBlocks.CRYSTAL_FURNACE.get()).build(null));

    public static final RegistryObject<MenuType<BlockCrystalFurnaceContainer>> CRYSTAL_FURNACE_CONTAINER = CONTAINERS.register(BlockCrystalFurnace.ID, () -> IForgeMenuType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        Level world = inv.player.getCommandSenderWorld();
        return new BlockCrystalFurnaceContainer(windowId, world, pos, inv, inv.player);
    }));*/


    //public static final RegistryObject<Item> NETHERITE_FURNACE_ITEM = ITEMS.register(BlockNetheriteFurnace.NETHERITE_FURNACE, () -> new ItemFurnace(LegacyFurnaceBlocks.NETHERITE_FURNACE.get(), new Item.Properties(), Config.netheriteFurnaceSpeed.get()));
    /*public static final RegistryObject<BlockEntityType<BlockNetheriteFurnaceTile>> NETHERITE_FURNACE_TILE = TILES.register(BlockNetheriteFurnace.ID, () -> BlockEntityType.Builder.of(BlockNetheriteFurnaceTile::new, LegacyFurnaceBlocks.NETHERITE_FURNACE.get()).build(null));

    public static final RegistryObject<MenuType<BlockNetheriteFurnaceContainer>> NETHERITE_FURNACE_CONTAINER = CONTAINERS.register(BlockNetheriteFurnace.ID, () -> IForgeMenuType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        Level world = inv.player.getCommandSenderWorld();
        return new BlockNetheriteFurnaceContainer(windowId, world, pos, inv, inv.player);
    }));*/

    //public static final RegistryObject<Item> COPPER_FURNACE_ITEM = ITEMS.register(BlockCopperFurnace.COPPER_FURNACE, () -> new ItemFurnace(LegacyFurnaceBlocks.COPPER_FURNACE.get(), new Item.Properties(), Config.copperFurnaceSpeed.get()));
    /*public static final RegistryObject<BlockEntityType<BlockCopperFurnaceTile>> COPPER_FURNACE_TILE = TILES.register(BlockCopperFurnace.ID, () -> BlockEntityType.Builder.of(BlockCopperFurnaceTile::new, LegacyFurnaceBlocks.COPPER_FURNACE.get()).build(null));

    public static final RegistryObject<MenuType<BlockCopperFurnaceContainer>> COPPER_FURNACE_CONTAINER = CONTAINERS.register(BlockCopperFurnace.ID, () -> IForgeMenuType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        Level world = inv.player.getCommandSenderWorld();
        return new BlockCopperFurnaceContainer(windowId, world, pos, inv, inv.player);
    }));*/

    //public static final RegistryObject<Item> SILVER_FURNACE_ITEM = ITEMS.register(BlockSilverFurnace.SILVER_FURNACE, () -> new ItemFurnace(LegacyFurnaceBlocks.SILVER_FURNACE.get(), new Item.Properties(), Config.silverFurnaceSpeed.get()));
    /*public static final RegistryObject<BlockEntityType<BlockSilverFurnaceTile>> SILVER_FURNACE_TILE = TILES.register(BlockSilverFurnace.ID, () -> BlockEntityType.Builder.of(BlockSilverFurnaceTile::new, LegacyFurnaceBlocks.SILVER_FURNACE.get()).build(null));

    public static final RegistryObject<MenuType<BlockSilverFurnaceContainer>> SILVER_FURNACE_CONTAINER = CONTAINERS.register(BlockSilverFurnace.ID, () -> IForgeMenuType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        Level world = inv.player.getCommandSenderWorld();
        return new BlockSilverFurnaceContainer(windowId, world, pos, inv, inv.player);
    }));*/


    //public static final RegistryObject<Item> ALLTHEMODIUM_FURNACE_ITEM = ITEMS.register(BlockAllthemodiumFurnace.ALLTHEMODIUM_FURNACE, () -> new ItemFurnace(LegacyFurnaceBlocks.ALLTHEMODIUM_FURNACE.get(), ModList.get().isLoaded("allthemodium") ? new Item.Properties() : new Item.Properties(), Config.allthemodiumFurnaceSpeed.get()));
    /*public static final RegistryObject<BlockEntityType<BlockAllthemodiumFurnaceTile>> ALLTHEMODIUM_FURNACE_TILE = TILES.register(BlockAllthemodiumFurnace.ID, () -> BlockEntityType.Builder.of(BlockAllthemodiumFurnaceTile::new, LegacyFurnaceBlocks.ALLTHEMODIUM_FURNACE.get()).build(null));

    public static final RegistryObject<MenuType<BlockAllthemodiumFurnaceContainer>> ALLTHEMODIUM_FURNACE_CONTAINER = CONTAINERS.register(BlockAllthemodiumFurnace.ID, () -> IForgeMenuType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        Level world = inv.player.getCommandSenderWorld();
        return new BlockAllthemodiumFurnaceContainer(windowId, world, pos, inv, inv.player);
    }));*/

    //public static final RegistryObject<Item> VIBRANIUM_FURNACE_ITEM = ITEMS.register(BlockVibraniumFurnace.VIBRANIUM_FURNACE, () -> new ItemFurnace(LegacyFurnaceBlocks.VIBRANIUM_FURNACE.get(), ModList.get().isLoaded("allthemodium") ? new Item.Properties() : new Item.Properties(), Config.vibraniumFurnaceSpeed.get()));
    /*public static final RegistryObject<BlockEntityType<BlockVibraniumFurnaceTile>> VIBRANIUM_FURNACE_TILE = TILES.register(BlockVibraniumFurnace.ID, () -> BlockEntityType.Builder.of(BlockVibraniumFurnaceTile::new, LegacyFurnaceBlocks.VIBRANIUM_FURNACE.get()).build(null));

    public static final RegistryObject<MenuType<BlockVibraniumFurnaceContainer>> VIBRANIUM_FURNACE_CONTAINER = CONTAINERS.register(BlockVibraniumFurnace.ID, () -> IForgeMenuType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        Level world = inv.player.getCommandSenderWorld();
        return new BlockVibraniumFurnaceContainer(windowId, world, pos, inv, inv.player);
    }));*/

    //public static final RegistryObject<Item> UNOBTAINIUM_FURNACE_ITEM = ITEMS.register(BlockUnobtainiumFurnace.UNOBTAINIUM_FURNACE, () -> new ItemFurnace(LegacyFurnaceBlocks.UNOBTAINIUM_FURNACE.get(), ModList.get().isLoaded("allthemodium") ? new Item.Properties() : new Item.Properties(), Config.unobtainiumFurnaceSpeed.get()));
    /*public static final RegistryObject<BlockEntityType<BlockUnobtainiumFurnaceTile>> UNOBTAINIUM_FURNACE_TILE = TILES.register(BlockUnobtainiumFurnace.ID, () -> BlockEntityType.Builder.of(BlockUnobtainiumFurnaceTile::new, LegacyFurnaceBlocks.UNOBTAINIUM_FURNACE.get()).build(null));

    public static final RegistryObject<MenuType<BlockUnobtainiumFurnaceContainer>> UNOBTAINIUM_FURNACE_CONTAINER = CONTAINERS.register(BlockUnobtainiumFurnace.ID, () -> IForgeMenuType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        Level world = inv.player.getCommandSenderWorld();
        return new BlockUnobtainiumFurnaceContainer(windowId, world, pos, inv, inv.player);
    }));*/
    private static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MOD_ID);



    private static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MOD_ID);
/*
    public static RegistryObject<RecipeType<GeneratorRecipe>> GENERATOR_RECIPE_TYPE = RECIPE_TYPES.register(GENERATOR_ID, () -> new RecipeType<GeneratorRecipe>() {
        @Override
        public String toString() {
            return GENERATOR_ID;
        }
    });*/
/*
    public static RegistryObject<RecipeSerializer<GeneratorRecipe>> GENERATOR_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register(GENERATOR_ID, GeneratorRecipe.Serializer::new);*/

    public static void init() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        TILES.register(modEventBus);
        CONTAINERS.register(modEventBus);
        RECIPE_SERIALIZERS.register(modEventBus);
        RECIPE_TYPES.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        //ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
        //DIMENSIONS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    private static Boolean isntSolid(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, EntityType<?> entityType) {
        return (Boolean) false;
    }

    private static boolean isntSolid(BlockState p_50806_, BlockGetter p_50807_, BlockPos p_50808_) {
        return false;
    }

    public static final class RecipeTypes {

        public static mezz.jei.api.recipe.RecipeType<GeneratorRecipe> GENERATOR_BLASTING = mezz.jei.api.recipe.RecipeType.create(IronFurnaces.MOD_ID, "generator_blasting", GeneratorRecipe.class);
        public static mezz.jei.api.recipe.RecipeType<SimpleGeneratorRecipe> GENERATOR_SMOKING = mezz.jei.api.recipe.RecipeType.create(IronFurnaces.MOD_ID, "generator_smoking", SimpleGeneratorRecipe.class);
        public static mezz.jei.api.recipe.RecipeType<SimpleGeneratorRecipe> GENERATOR_REGULAR = mezz.jei.api.recipe.RecipeType.create(IronFurnaces.MOD_ID, "generator_regular", SimpleGeneratorRecipe.class);
    }



    //public static final RegistryObject<Item> MILLION_FURNACE_ITEM = ITEMS.register(BlockMillionFurnace.MILLION_FURNACE, () -> new ItemMillionFurnace(LegacyFurnaceBlocks.MILLION_FURNACE.get(), new Item.Properties()));

    /*public static final RegistryObject<BlockEntityType<BlockMillionFurnaceTile>> MILLION_FURNACE_TILE = TILES.register(BlockMillionFurnace.ID, () -> BlockEntityType.Builder.of(BlockMillionFurnaceTile::new, LegacyFurnaceBlocks.MILLION_FURNACE.get()).build(null));

    public static final RegistryObject<MenuType<BlockMillionFurnaceContainer>> MILLION_FURNACE_CONTAINER = CONTAINERS.register(BlockMillionFurnace.ID, () -> IForgeMenuType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        Level world = inv.player.getCommandSenderWorld();
        return new BlockMillionFurnaceContainer(windowId, world, pos, inv, inv.player);
    }));*/


}
