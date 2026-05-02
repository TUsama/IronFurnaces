//? <1.21.11{
package ironfurnaces.items;

import ironfurnaces.registration.ModDataComponents;
import ironfurnaces.registration.ModLangs;
import ironfurnaces.tileentity.furnaces.BlockIronFurnaceTileBase;
import ironfurnaces.util.DirectionUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;



import javax.annotation.Nullable;
import java.util.List;

public class ItemFurnaceCopy extends Item {


    public ItemFurnaceCopy(Properties properties) {
        super(properties);
    }


    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        //? 1.20.1 {
        if (stack.hasTag()) {
            if (stack.getTag().getIntArray("settings").length >= 10)
            {

                tooltip.add(Component.translatable("ironfurnaces.furnace_setting.direction.down", stack.getTag().getIntArray("settings")[0]).setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY))));
                tooltip.add(Component.translatable("ironfurnaces.furnace_setting.direction.up", stack.getTag().getIntArray("settings")[1]).setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY))));
                tooltip.add(Component.translatable("ironfurnaces.furnace_setting.direction.north", stack.getTag().getIntArray("settings")[2]).setStyle(Style.EMPTY.applyFormat(ChatFormatting.GRAY)));
                tooltip.add(Component.translatable("ironfurnaces.furnace_setting.direction.south", stack.getTag().getIntArray("settings")[3]).setStyle(Style.EMPTY.applyFormat(ChatFormatting.GRAY)));
                tooltip.add(Component.translatable("ironfurnaces.furnace_setting.direction.west", stack.getTag().getIntArray("settings")[4]).setStyle(Style.EMPTY.applyFormat(ChatFormatting.GRAY)));
                tooltip.add(Component.translatable("ironfurnaces.furnace_setting.direction.east", stack.getTag().getIntArray("settings")[5]).setStyle(Style.EMPTY.applyFormat(ChatFormatting.GRAY)));
                tooltip.add(Component.translatable("ironfurnaces.furnace_setting.auto_input", stack.getTag().getIntArray("settings")[6]).setStyle(Style.EMPTY.applyFormat(ChatFormatting.GRAY)));
                tooltip.add(Component.translatable("ironfurnaces.furnace_setting.auto_output", stack.getTag().getIntArray("settings")[7]).setStyle(Style.EMPTY.applyFormat(ChatFormatting.GRAY)));
                tooltip.add(Component.translatable("ironfurnaces.furnace_setting.redstone_mode", stack.getTag().getIntArray("settings")[8]).setStyle(Style.EMPTY.applyFormat(ChatFormatting.GRAY)));
                tooltip.add(Component.translatable("ironfurnaces.furnace_setting.redstone_value", stack.getTag().getIntArray("settings")[9]).setStyle(Style.EMPTY.applyFormat(ChatFormatting.GRAY)));
                tooltip.add(Component.translatable("ironfurnaces.furnace_setting.faced_direction", DirectionUtil.fromId(stack.getTag().getInt("direction"))).setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY))));
            }
        }
        //? } else {
        /*if (stack.has(ModDataComponents.PERSISTENT_LEGACY_SETTING)){
            var a = stack.get(ModDataComponents.PERSISTENT_LEGACY_SETTING);
            tooltip.add(Component.translatable("ironfurnaces.furnace_setting.direction.down", a[0]).setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY))));
            tooltip.add(Component.translatable("ironfurnaces.furnace_setting.direction.up", a[1]).setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY))));
            tooltip.add(Component.translatable("ironfurnaces.furnace_setting.direction.north", a[2]).setStyle(Style.EMPTY.applyFormat(ChatFormatting.GRAY)));
            tooltip.add(Component.translatable("ironfurnaces.furnace_setting.direction.south", a[3]).setStyle(Style.EMPTY.applyFormat(ChatFormatting.GRAY)));
            tooltip.add(Component.translatable("ironfurnaces.furnace_setting.direction.west", a[4]).setStyle(Style.EMPTY.applyFormat(ChatFormatting.GRAY)));
            tooltip.add(Component.translatable("ironfurnaces.furnace_setting.direction.east", a[5]).setStyle(Style.EMPTY.applyFormat(ChatFormatting.GRAY)));
            tooltip.add(Component.translatable("ironfurnaces.furnace_setting.auto_input", a[6]).setStyle(Style.EMPTY.applyFormat(ChatFormatting.GRAY)));
            tooltip.add(Component.translatable("ironfurnaces.furnace_setting.auto_output", a[7]).setStyle(Style.EMPTY.applyFormat(ChatFormatting.GRAY)));
            tooltip.add(Component.translatable("ironfurnaces.furnace_setting.redstone_mode", a[8]).setStyle(Style.EMPTY.applyFormat(ChatFormatting.GRAY)));
            tooltip.add(Component.translatable("ironfurnaces.furnace_setting.redstone_value", a[9]).setStyle(Style.EMPTY.applyFormat(ChatFormatting.GRAY)));

        }

        if (stack.has(ModDataComponents.PERSISTENT_DIRECTION)){
            tooltip.add(Component.translatable("ironfurnaces.furnace_setting.faced_direction", stack.get(ModDataComponents.PERSISTENT_DIRECTION)));
        }
        *///?}

        tooltip.add(Component.translatable("ironfurnaces.item.item_copy.usage.1").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("ironfurnaces.item.item_copy.usage.2").withStyle(ChatFormatting.GRAY));
    }




    @Override
    public InteractionResult useOn(UseOnContext ctx) {

        Level world = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        if (!ctx.getPlayer().isCrouching())
        {
            return super.useOn(ctx);
        }
        if (!world.isClientSide) {
            BlockEntity te = world.getBlockEntity(pos);

            if (!(te instanceof BlockIronFurnaceTileBase)) {
                return super.useOn(ctx);
            }

            ItemStack stack = ctx.getItemInHand();
            //? 1.20.1 {
            if (stack.hasTag())
            {
                CompoundTag tag = stack.getTag();
                if (tag.getIntArray("settings") != null && tag.getIntArray("settings").length > 0)
                {
                    int[] settings = tag.getIntArray("settings");
                    for (int i = 0; i < settings.length; i++)
                    {
                        ((BlockIronFurnaceTileBase) te).furnaceSettings.set(i, settings[i]);
                    }
                }

                Direction dir = DirectionUtil.fromId(tag.getInt("direction"));
                if (dir != Direction.UP && dir != Direction.DOWN)
                {
                    if (te.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING) != dir) {
                        te.getLevel().setBlock(te.getBlockPos(), te.getBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, dir), 3);
                    }
                }
            }
            //? } else {
            /*if (stack.has(ModDataComponents.PERSISTENT_LEGACY_SETTING)){
                var a = stack.get(ModDataComponents.PERSISTENT_LEGACY_SETTING);
                for (int i = 0; i < a.length; i++)
                {
                    ((BlockIronFurnaceTileBase) te).furnaceSettings.set(i, a[i]);
                }

            }

            if (stack.has(ModDataComponents.PERSISTENT_DIRECTION)){
                Direction dir = DirectionUtil.fromId(stack.get(ModDataComponents.PERSISTENT_DIRECTION));
                if (dir != Direction.UP && dir != Direction.DOWN)
                {
                    if (te.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING) != dir) {
                        te.getLevel().setBlock(te.getBlockPos(), te.getBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, dir), 3);
                    }
                }
            }
            *///?}

            world.markAndNotifyBlock(pos, world.getChunkAt(pos), world.getBlockState(pos).getBlock().defaultBlockState(), world.getBlockState(pos), 3, 3);
            ctx.getPlayer().sendSystemMessage(Component.translatable("ironfurnaces.item.item_copy.tip.setting_applied"));
        }

        return super.useOn(ctx);
    }
}

//?}