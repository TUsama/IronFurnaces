package ironfurnaces.items;

import ironfurnaces.gui.furnaces.BlockIronFurnaceScreenBase;
import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.registration.ModDataComponents;
import ironfurnaces.util.StringHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemHeater extends Item {


    public ItemHeater(Properties properties) {
        super(properties);
    }

    @Nullable
    public static BlockPos getBoundBlockPos(ItemStack stack){
        //? 1.20.1 {
        if (stack.hasTag()){
            return new BlockPos(stack.getTag().getInt("X"), stack.getTag().getInt("Y"), stack.getTag().getInt("Z"));
        }
        return null;
        //?} else {
        /*return stack.get(ModDataComponents.BOUND_BLOCK_POS.get());
        *///?}
    }

    public static void writeBoundBlockPos(ItemStack stack, BlockPos pos){
        //? 1.20.1 {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt("X", pos.getX());
        tag.putInt("Y", pos.getY());
        tag.putInt("Z", pos.getZ());
        //?} else {
        /*stack.set(ModDataComponents.BOUND_BLOCK_POS.get(), pos);
         *///?}
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {


        if (BlockIronFurnaceScreenBase.isShiftKeyDown())
        {
            BlockPos boundBlockPos = getBoundBlockPos(stack);
            if (boundBlockPos != null) {
                tooltip.add(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".heater").setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY))));
                tooltip.add(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".heaterX").setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY))).append(Component.literal("" + boundBlockPos.getX()).setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY)))));
                tooltip.add(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".heaterY").setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY))).append(Component.literal("" + boundBlockPos.getY()).setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY)))));
                tooltip.add(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".heaterZ").setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY))).append(Component.literal("" + boundBlockPos.getZ()).setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY)))));
            } else {
                tooltip.add(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".heater_not_bound").setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY))));
                tooltip.add(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".heater_tip").setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY))));
                tooltip.add(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".heater_tip1").setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY))));
            }
        }
        else
        {
            tooltip.add(StringHelper.getShiftInfoText());
        }
    }
}
