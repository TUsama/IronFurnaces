package ironfurnaces.items.upgrades.furnace_pattern;

import ironfurnaces.tileentity.furnaces.pattern.FurnacePattern;
import ironfurnaces.tileentity.furnaces.pattern.FurnacePatternManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

//? >1.20.1 {
/*import net.minecraft.world.item.component.CustomData;
import net.minecraft.core.component.DataComponents;
import ironfurnaces.registration.ModDataComponents;
*///?}

import javax.annotation.Nullable;

public interface IPatternAccessor {
    // todo potential bug: server added new pattern without sync to client, resulting this method return null.

    @Nullable
    static FurnacePattern getFurnacePatternFromTag(ItemStack stack) {
        //? 1.20.1 {
        CompoundTag tag = stack.getTagElement(BlockItem.BLOCK_ENTITY_TAG);
        if (tag != null && tag.contains(FurnacePattern.NBT_KEY)) {
            ResourceLocation resourceLocation = ResourceLocation.tryParse(tag.getString(FurnacePattern.NBT_KEY));
            if (resourceLocation != null) {
                return FurnacePatternManager.get(resourceLocation);
            }
        }
        //?} else {
            
        /*ResourceLocation componentPattern = getComponentPatternId(stack);
        ResourceLocation legacyPattern = getLegacyPatternId(stack);

        // 新组件存在时，新组件是权威数据。
        // 如果旧数据也存在，不管是否相同，都清理掉，避免后续代码读到脏数据。
        if (componentPattern != null) {
            if (legacyPattern != null) {
                removeLegacyPatternData(stack);
            }

            return FurnacePatternManager.get(componentPattern);
        }

        // 新组件不存在，但旧数据存在，执行迁移。
        if (legacyPattern != null) {
            stack.set(ModDataComponents.FURNACE_PATTERN_COMPONENT.get(), legacyPattern);
            removeLegacyPatternData(stack);

            return FurnacePatternManager.get(legacyPattern);
        }
            
        *///?}

        return null;
    }

    static void writePatternToItemStack(ItemStack stack, FurnacePattern pattern) {
        writePatternToItemStack(stack, pattern.id());
    }

    static void writePatternToItemStack(ItemStack stack, ResourceLocation pattern) {
        //? 1.20.1 {
        CompoundTag blockEntityTag = stack.getOrCreateTagElement(BlockItem.BLOCK_ENTITY_TAG);
        blockEntityTag.putString(FurnacePattern.NBT_KEY, pattern.toString());
        //? } else {
        
        /*// 先做一次兼容迁移/清理，覆盖以下情况：
        // 1. 只有旧数据：先迁移到组件，再按本次入参更新。
        // 2. 新旧数据都有：以新组件为准，并清理旧数据。
        // 3. 只有新组件：不动旧数据，因为没有旧数据。
        migrateLegacyPatternDataIfNeeded(stack);

        // writePatternToItemStack 的语义是“把当前 stack 的 pattern 更新为入参 pattern”。
        // 所以最终值必须以入参为准，而不是以旧数据为准。
        stack.set(ModDataComponents.FURNACE_PATTERN_COMPONENT.get(), pattern);

        // 双保险：避免调用链上先写了旧 CustomData，或者 migrate 后又残留旧 key。
        removeLegacyPatternData(stack);
        
        *///?}
    }
    //? >1.20.1 {
    /*@Nullable
    private static ResourceLocation getComponentPatternId(ItemStack stack) {
        return stack.get(ModDataComponents.FURNACE_PATTERN_COMPONENT.get());
    }

    @Nullable
    private static ResourceLocation getLegacyPatternId(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null && customData.contains(FurnacePattern.NBT_KEY)) {
            return ResourceLocation.tryParse(customData.copyTag().getString(FurnacePattern.NBT_KEY));
        }

        return null;
    }

    private static void migrateLegacyPatternDataIfNeeded(ItemStack stack) {
        ResourceLocation componentPattern = getComponentPatternId(stack);
        ResourceLocation legacyPattern = getLegacyPatternId(stack);

        if (legacyPattern == null) {
            return;
        }

        if (componentPattern == null) {
            stack.set(ModDataComponents.FURNACE_PATTERN_COMPONENT.get(), legacyPattern);
        }

        removeLegacyPatternData(stack);
    }

    private static void removeLegacyPatternData(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null || !customData.contains(FurnacePattern.NBT_KEY)) {
            return;
        }

        CompoundTag tag = customData.copyTag();
        tag.remove(FurnacePattern.NBT_KEY);

        if (tag.isEmpty()) {
            stack.remove(DataComponents.CUSTOM_DATA);
        } else {
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }

    }
    *///?}
}