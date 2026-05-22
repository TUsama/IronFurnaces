package ironfurnaces.registration.data_component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ironfurnaces.registration.ModDataComponents;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.pattern.FurnacePattern;
import ironfurnaces.tileentity.furnaces.pattern.NormalFurnacePattern;
import ironfurnaces.tileentity.furnaces.pattern.RainbowFurnacePattern;
import ironfurnaces.tileentity.furnaces.setting.FurnaceSettingsV2;
import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.function.Consumer;

import static net.minecraft.network.chat.Component.translatable;

public record PatternHolderInfo(@Nullable FurnacePattern pattern, FurnaceSettingsV2 settingsV2) implements TooltipProvider {
    public static final Codec<PatternHolderInfo> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    FurnacePattern.REF_CODEC.fieldOf("pattern").forGetter(PatternHolderInfo::pattern),
                    FurnaceSettingsV2.CODEC.fieldOf("setting").forGetter(PatternHolderInfo::settingsV2)
            ).apply(instance, PatternHolderInfo::new));


    public static PatternHolderInfo of(@Nullable FurnacePattern pattern, @Nullable FurnaceSettingsV2 settingsV2){
        return new PatternHolderInfo(pattern == null ? FurnacePattern.FALLBACK : pattern, settingsV2 == null ? FurnaceSettingsV2.DEFAULT : settingsV2);
    }

    public static void writeTo(ItemStack stack, PatternHolderInfo info) {
        if (stack.isEmpty()) {
            return;
        }

        stack.set(ModDataComponents.PATTERN_HOLDER_INFO.get(), info);
    }

    public static void writeTo(
            ItemStack stack,
            FurnacePattern pattern,
            FurnaceSettingsV2 settingsV2
    ) {
        writeTo(stack, new PatternHolderInfo(pattern, settingsV2));
    }

    @Override
    public void addToTooltip(Item.TooltipContext tooltipContext, Consumer<Component> consumer, TooltipFlag tooltipFlag, DataComponentGetter dataComponentGetter) {
        if (pattern != null) {
            int s = 0;
            if (pattern instanceof NormalFurnacePattern pattern1) {
                s = pattern1.smeltTick();
            } else if (pattern instanceof RainbowFurnacePattern pattern) {
                s = pattern.baseSmeltTickPerItem();
            }
            if (s != 0) {
                consumer.accept(translatable("ironfurnaces.block.furnace.work_speed", Component.literal(String.valueOf(s)).withStyle(ChatFormatting.GREEN)).withStyle(ChatFormatting.GRAY));
            }

        } else {
            consumer.accept(translatable("block.ironfurnaces.furnace_pattern_holder.without_pattern")
                    .withStyle(ChatFormatting.RED));
        }
        if (settingsV2 != null) {
            settingsV2.toTooltips().forEach(consumer);
        }

        consumer.accept(translatable("item.ironfurnaces.furnace_pattern_holder.reset_setting_hint"));
    }

    public void writeTo(ItemStack stack) {
        writeTo(stack, this);
    }

    public void writeToBlockEntity(FurnacePatternBlockEntity blockEntity) {
        blockEntity.updatePattern(pattern);
        pattern.referenceBlock().ifPresent(x -> {
            var blockReference = BuiltInRegistries.BLOCK.getValue(x);
            BlockState refState = blockReference.defaultBlockState();
            SoundType soundType = refState.getSoundType(blockEntity.getLevel(), blockEntity.getBlockPos(), null);

            blockEntity.getLevel().playSound(
                    null,
                    blockEntity.getBlockPos(),
                    soundType.getPlaceSound(),
                    SoundSource.BLOCKS,
                    (soundType.getVolume() + 1.0F) / 2.0F,
                    soundType.getPitch() * 0.8F
            );
        });
        blockEntity.setWholeSettingV2(settingsV2);

        blockEntity.setChanged();
    }
}
