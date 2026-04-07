package ironfurnaces.items.upgrades.furnace_pattern;

import ironfurnaces.tileentity.furnaces.pattern.FurnacePattern;
import ironfurnaces.tileentity.furnaces.pattern.FurnacePatternManager;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

//? 1.20.1 {

//? } else {
/*import net.minecraft.world.item.component.CustomData;
import net.minecraft.core.component.DataComponents;
*///?}
import javax.annotation.Nullable;

public interface IPatternAccessor {
    //todo potential bug: server added new pattern without sync to client, resulting this method return null.
    @Nullable
    static FurnacePattern getFurnacePatternFromTag(ItemStack stack){
        //? 1.20.1 {
        CompoundTag tag = stack.getTagElement(BlockItem.BLOCK_ENTITY_TAG);
        if (tag != null && tag.contains(FurnacePattern.NBT_KEY)){
            ResourceLocation resourceLocation = ResourceLocation.tryParse(tag.getString(FurnacePattern.NBT_KEY));
            if (resourceLocation != null){
                return FurnacePatternManager.get(resourceLocation);
            }

        }
        //? } else {
        /*var a = stack.get(DataComponents.CUSTOM_DATA);
        if (a != null && a.contains(FurnacePattern.NBT_KEY)) {
            ResourceLocation resourceLocation = ResourceLocation.tryParse(a.copyTag().getString(FurnacePattern.NBT_KEY));
            if (resourceLocation != null){
                return FurnacePatternManager.get(resourceLocation);
            }
        }
        *///?}

        return null;
    }

    static void writePatternToItemStack(ItemStack stack, FurnacePattern pattern){
        writePatternToItemStack(stack, pattern.id());
    }

    static void writePatternToItemStack(ItemStack stack, ResourceLocation pattern){
        //? 1.20.1 {
        CompoundTag blockEntityTag = stack.getOrCreateTagElement(BlockItem.BLOCK_ENTITY_TAG);
        blockEntityTag.putString(FurnacePattern.NBT_KEY, pattern.toString());
        //? } else {
        /*CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
            tag.putString(FurnacePattern.NBT_KEY, pattern.toString());
        });
        *///?}
    }
}
