package ironfurnaces.registration;

import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.util.entry.BlockEntry;
import ironfurnaces.blocks.BlockWirelessEnergyHeater;
import ironfurnaces.blocks.furnaces.BlockItemHeater;
import ironfurnaces.tileentity.BlockWirelessEnergyHeaterTile;
import ironfurnaces.tileentity.furnaces.BlockIronFurnaceTileBase;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ForgeRegistries;

import static ironfurnaces.loaders.IronFurnaces.REGISTRATE;

public class ModBlocks {

    public static final BlockEntry<BlockWirelessEnergyHeater> HEATER = REGISTRATE
            .block(BlockWirelessEnergyHeater.HEATER, BlockWirelessEnergyHeater::new)
            .initialProperties(() -> Blocks.IRON_BLOCK)
            .lang("Wireless Heat Transmitter")
            .item(BlockItemHeater::new)
            .build()
            .<BlockWirelessEnergyHeaterTile>blockEntity(BlockWirelessEnergyHeaterTile::new)
            .build()
            .addMiscData(ProviderType.LANG, x -> x.add("container.ironfurnaces.wireless_energy_heater", "Wireless Heater"))
            .register();

    public static void register() {

    }

    public static BlockEntityType<? extends BlockIronFurnaceTileBase> asBlockEntityType(BlockEntry<?> entry) {
        return (BlockEntityType<? extends BlockIronFurnaceTileBase>) entry.getSibling(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES).get();
    }

    public static <T extends BlockEntity> BlockEntityType<T> asGenericBlockEntityType(BlockEntry<?> entry) {
        return (BlockEntityType<T>) entry.getSibling(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES).get();
    }
}
