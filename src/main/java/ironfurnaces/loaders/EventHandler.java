package ironfurnaces.loaders;


import ironfurnaces.registration.ModItems;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ExplosionEvent;

import java.util.Iterator;
import java.util.List;

@EventBusSubscriber(value = Dist.DEDICATED_SERVER)
public class EventHandler {

    @SubscribeEvent
    public static void explosionEvent(ExplosionEvent.Detonate event) {
        Level level = event.getLevel();

        if (level.isClientSide()) {
            return;
        }

        List<BlockPos> affectedBlocks = event.getAffectedBlocks();
        Iterator<BlockPos> iterator = affectedBlocks.iterator();

        while (iterator.hasNext()) {
            BlockPos pos = iterator.next();

            if (!(level.getBlockEntity(pos) instanceof FurnacePatternBlockEntity blockEntity && blockEntity.getPattern().isRainbow())) {
                continue;
            }

            // 从爆炸破坏列表中移除，避免爆炸后续再处理这个方块
            iterator.remove();

            // 先移除方块实体，再移除方块
            level.removeBlockEntity(pos);
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);

            level.addFreshEntity(new ItemEntity(
                    level,
                    pos.getX() + 0.5D,
                    pos.getY() + 6.0D,
                    pos.getZ() + 0.5D,
                    new ItemStack(ModItems.RAINBOW_COAL.get())
            ));
        }
    }

}
