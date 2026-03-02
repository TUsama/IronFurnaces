package ironfurnaces.items.upgrades.furnace_upgrade;

import com.mojang.serialization.DataResult;
import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.tileentity.furnaces.tier.upgrade.TierUpgradeRule;
import ironfurnaces.tileentity.furnaces.tier.upgrade.TierUpgradeRuleManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class ItemUpgradeTool extends Item {
    public ItemUpgradeTool(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".upgrade_right_click").setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY))));
    }


    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (!context.getLevel().isClientSide){

        }
        return super.useOn(context);
    }

}
