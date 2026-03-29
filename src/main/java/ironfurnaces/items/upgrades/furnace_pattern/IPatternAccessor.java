package ironfurnaces.items.upgrades.furnace_pattern;

import ironfurnaces.tileentity.furnaces.pattern.FurnacePattern;
import ironfurnaces.tileentity.furnaces.pattern.FurnacePatternManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public interface IPatternAccessor {
    //todo potential bug: server added new pattern without sync to client, resulting this method return null.
    @Nullable
    static FurnacePattern getFurnacePatternFromTag(ItemStack stack){
        CompoundTag tag = stack.getTagElement(BlockItem.BLOCK_ENTITY_TAG);
        if (tag != null && tag.contains(FurnacePattern.NBT_KEY)){
            ResourceLocation resourceLocation = ResourceLocation.tryParse(tag.getString(FurnacePattern.NBT_KEY));
            if (resourceLocation != null){
                return FurnacePatternManager.get(resourceLocation);
            }

        }
        return null;
    }

    static void writePatternToItemStack(ItemStack stack, FurnacePattern pattern){
        CompoundTag blockEntityTag = stack.getOrCreateTagElement(BlockItem.BLOCK_ENTITY_TAG);
        blockEntityTag.putString(FurnacePattern.NBT_KEY, pattern.id().toString());
    }

    static void writePatternToItemStack(ItemStack stack, ResourceLocation pattern){
        CompoundTag blockEntityTag = stack.getOrCreateTagElement(BlockItem.BLOCK_ENTITY_TAG);
        blockEntityTag.putString(FurnacePattern.NBT_KEY, pattern.toString());
    }
}
