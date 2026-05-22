
package ironfurnaces.registration;

import com.mojang.serialization.Codec;
import dev.anvilcraft.lib.v2.registrum.util.entry.RegistryEntry;
import ironfurnaces.items.ItemFurnaceCopyV2;
import ironfurnaces.registration.data_component.*;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.network.codec.ByteBufCodecs;


import static ironfurnaces.loaders.IronFurnaces.REGISTRATE;

public class ModDataComponents {



    public static final RegistryEntry<DataComponentType<?>, DataComponentType<HeaterItemInfo>> HEATER_ITEM_INFO = REGISTRATE.simple("heater_item_info", Registries.DATA_COMPONENT_TYPE, () ->
            DataComponentType.<HeaterItemInfo>builder()
                    .persistent(HeaterItemInfo.CODEC)
                    .build());


    public static final RegistryEntry<DataComponentType<?>, DataComponentType<Identifier>> FURNACE_PATTERN_COMPONENT = REGISTRATE.simple("pattern_component",Registries.DATA_COMPONENT_TYPE, () ->
            DataComponentType.<Identifier>builder()
                    .persistent(Identifier.CODEC)
                    .build());

    public static final RegistryEntry<DataComponentType<?>, DataComponentType<PatternHolderInfo>> PATTERN_HOLDER_INFO = REGISTRATE.simple("furnace_item_info",Registries.DATA_COMPONENT_TYPE, () ->
            DataComponentType.<PatternHolderInfo>builder()
                    .persistent(PatternHolderInfo.CODEC)
                    .build());

    public static final RegistryEntry<DataComponentType<?>, DataComponentType<UpgradeRuleHolder>> UPGRADE_RULE_HOLDER = REGISTRATE.simple("upgrade_rule_holder",Registries.DATA_COMPONENT_TYPE, () ->
            DataComponentType.<UpgradeRuleHolder>builder()
                    .persistent(UpgradeRuleHolder.CODEC)
                    .build());


    public static final RegistryEntry<DataComponentType<?>, DataComponentType<Integer>> PERSISTENT_ENERGY = REGISTRATE.simple("persistent_energy",Registries.DATA_COMPONENT_TYPE, () ->
            DataComponentType.<Integer>builder()
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.INT)
                    .build());


    public static final RegistryEntry<DataComponentType<?>, DataComponentType<ItemFurnaceCopyV2.PackedSetting>> PERSISTENT_STREAM_SETTING = REGISTRATE.simple("persistent_stream_setting",Registries.DATA_COMPONENT_TYPE, () ->
            DataComponentType.<ItemFurnaceCopyV2.PackedSetting>builder()
                    .persistent(ItemFurnaceCopyV2.PackedSetting.CODEC)
                    .build());

    public static void register(){

    }
}
