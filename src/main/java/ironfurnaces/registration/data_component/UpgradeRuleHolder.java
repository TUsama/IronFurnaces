package ironfurnaces.registration.data_component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.tileentity.furnaces.pattern.FurnacePattern;
import ironfurnaces.tileentity.furnaces.pattern.upgrade.PatternUpgradeRule;
import ironfurnaces.tileentity.furnaces.pattern.upgrade.PatternUpgradeRuleManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

import java.util.Optional;
import java.util.function.Consumer;

public record UpgradeRuleHolder(Optional<Identifier> upgradeRule) implements TooltipProvider {

    public static final UpgradeRuleHolder BROKEN = new UpgradeRuleHolder(Optional.empty());

    public static final Codec<UpgradeRuleHolder> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Identifier.CODEC
                            .optionalFieldOf("upgrade_rule")
                            .forGetter(UpgradeRuleHolder::upgradeRule)
            ).apply(instance, UpgradeRuleHolder::new)
    );

    public static UpgradeRuleHolder of(PatternUpgradeRule rule) {
        return rule == null ? BROKEN : new UpgradeRuleHolder(Optional.of(rule.id()));
    }

    @Override
    public void addToTooltip(
            Item.TooltipContext context,
            Consumer<Component> tooltip,
            TooltipFlag flag,
            DataComponentGetter components
    ) {
        tooltip.accept(Component.literal(""));

        if (upgradeRule.isPresent() && PatternUpgradeRuleManager.isValidRuleId(upgradeRule.get())) {
            PatternUpgradeRule rule = PatternUpgradeRuleManager.get(upgradeRule.get());

            Component fromName = FurnacePattern.toDisplayName(rule.from())
                    .copy()
                    .withStyle(ChatFormatting.YELLOW);

            Component toName = FurnacePattern.toDisplayName(rule.to())
                    .copy()
                    .withStyle(ChatFormatting.GREEN);

            tooltip.accept(Component.translatable(
                    "item.ironfurnaces.upgrade_tool.upgrade_rule",
                    fromName,
                    toName
            ));

            tooltip.accept(Component.literal(""));

            tooltip.accept(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".upgrade_right_click")
                    .setStyle(Style.EMPTY.applyFormat(ChatFormatting.GRAY)));
        } else {
            tooltip.accept(Component.translatable("item.ironfurnaces.upgrade_tool.broken_upgrade_tool")
                    .withStyle(style -> style.withBold(true).withColor(ChatFormatting.RED)));
        }
    }
}
