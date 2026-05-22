
package ironfurnaces.items.upgrades.furnace_pattern;

import ironfurnaces.registration.ModDataComponents;
import ironfurnaces.registration.data_component.PatternHolderInfo;
import ironfurnaces.tileentity.furnaces.pattern.FurnacePattern;

import ironfurnaces.tileentity.furnaces.pattern.FurnacePatternManager;
import ironfurnaces.tileentity.furnaces.setting.FurnaceSettingsV2;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

//? 1.20.1 {

//? } else {
import net.minecraft.world.item.component.CustomData;
import net.minecraft.core.component.DataComponents;
//?}
import javax.annotation.Nullable;

public interface IPatternAccessor {
    //todo potential bug: server added new pattern without sync to client, resulting this method return null.
    @Nullable
    static FurnacePattern getFurnacePatternFromTag(ItemStack stack){
        var a = stack.get(DataComponents.CUSTOM_DATA);
        if (a != null && a.contains(FurnacePattern.NBT_KEY)) {
            a.copyTag().getString(FurnacePattern.NBT_KEY).ifPresent(x -> {
                Identifier resourceLocation = Identifier.tryParse(x);
                if (resourceLocation != null) {
                    FurnacePattern furnacePattern = FurnacePatternManager.get(resourceLocation);
                    stack.set(ModDataComponents.PATTERN_HOLDER_INFO, new PatternHolderInfo(furnacePattern, FurnaceSettingsV2.DEFAULT));
                    a.update(tag -> tag.remove(FurnacePattern.NBT_KEY));
                }
            });

        }

        PatternHolderInfo patternHolderInfo = stack.get(ModDataComponents.PATTERN_HOLDER_INFO);
        if (patternHolderInfo != null) {
            return patternHolderInfo.pattern();
        }

        return null;
    }

    static void writePatternToItemStack(ItemStack stack, FurnacePattern pattern){
        PatternHolderInfo old = stack.get(ModDataComponents.PATTERN_HOLDER_INFO);
        if (old != null){
            stack.set(ModDataComponents.PATTERN_HOLDER_INFO, PatternHolderInfo.of(pattern, old.settingsV2()));
        } else {
            stack.set(ModDataComponents.PATTERN_HOLDER_INFO, PatternHolderInfo.of(pattern, null));
        }

    }

    static void writePatternToItemStack(ItemStack stack, Identifier pattern){
        writePatternToItemStack(stack, FurnacePatternManager.get(pattern));
    }
}
