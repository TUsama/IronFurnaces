
package ironfurnaces.registration;

import com.mojang.serialization.Codec;
import dev.anvilcraft.lib.v2.registrum.util.entry.RegistryEntry;
import ironfurnaces.items.ItemFurnaceCopyV2;
import ironfurnaces.registration.data_component.FurnaceItemInfo;
import ironfurnaces.tileentity.furnaces.pattern.FurnacePattern;
import ironfurnaces.tileentity.furnaces.setting.FurnaceSettingsV2;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.network.codec.ByteBufCodecs;
//? 1.20.1 {

//? } else {
import net.minecraft.core.component.DataComponentType;
//?}

import java.util.Arrays;
import java.util.function.Supplier;

import static ironfurnaces.loaders.IronFurnaces.REGISTRATE;

public class ModDataComponents {
    //? >1.20.1 {
    public static final RegistryEntry<DataComponentType<?>, DataComponentType<BlockPos>> BOUND_BLOCK_POS = REGISTRATE.simple("bound_blockpos", Registries.DATA_COMPONENT_TYPE, () ->
            DataComponentType.<BlockPos>builder()
                    .persistent(BlockPos.CODEC)
                    .build());

    public static final RegistryEntry<DataComponentType<?>, DataComponentType<Identifier>> FURNACE_PATTERN_COMPONENT = REGISTRATE.simple("pattern_component",Registries.DATA_COMPONENT_TYPE, () ->
            DataComponentType.<Identifier>builder()
                    .persistent(Identifier.CODEC)
                    .build());

    public static final RegistryEntry<DataComponentType<?>, DataComponentType<FurnaceItemInfo>> FURNACE_ITEM_INFO = REGISTRATE.simple("pattern_component",Registries.DATA_COMPONENT_TYPE, () ->
            DataComponentType.<FurnaceItemInfo>builder()
                    .persistent(FurnaceItemInfo.CODEC)
                    .build());

    public static final RegistryEntry<DataComponentType<?>, DataComponentType<Identifier>> FURNACE_UPGRADE_RULE_COMPONENT = REGISTRATE.simple("upgrade_component",Registries.DATA_COMPONENT_TYPE, () ->
            DataComponentType.<Identifier>builder()
                    .persistent(Identifier.CODEC)
                    .build());

    public static final RegistryEntry<DataComponentType<?>, DataComponentType<Integer>> PERSISTENT_ENERGY = REGISTRATE.simple("persistent_energy",Registries.DATA_COMPONENT_TYPE, () ->
            DataComponentType.<Integer>builder()
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.INT)
                    .build());


    public static final RegistryEntry<DataComponentType<?>, DataComponentType<ItemFurnaceCopyV2.StreamSetting>> PERSISTENT_STREAM_SETTING = REGISTRATE.simple("persistent_stream_setting",Registries.DATA_COMPONENT_TYPE, () ->
            DataComponentType.<ItemFurnaceCopyV2.StreamSetting>builder()
                    .persistent(ItemFurnaceCopyV2.StreamSetting.CODEC)
                    .build());

    public static void register(){

    }
}
