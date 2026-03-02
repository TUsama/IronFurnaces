package ironfurnaces.items.upgrades.furnace_pattern;

import ironfurnaces.tileentity.furnaces.tier.FurnacePattern;
import ironfurnaces.tileentity.furnaces.tier.FurnacePatternManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public interface IPatternAccessor {
    @Nullable
    static FurnacePattern getFurnacePatternFromTag(ItemStack stack){
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(FurnacePattern.NBT_ID)){
            ResourceLocation resourceLocation = ResourceLocation.tryParse(tag.getString(FurnacePattern.NBT_ID));
            if (resourceLocation != null){
                return FurnacePatternManager.get(resourceLocation);
            }

        }
        return null;
    }

    static void writePatternToItemStack(ItemStack stack, FurnacePattern pattern){
        CompoundTag tag = stack.getOrCreateTag();
        String key = FurnacePattern.NBT_ID;
        if (tag.contains(key)) {
            tag.remove(key);

        }
        tag.putString(key, pattern.id().toString());
    }
}
