package ironfurnaces.registration.util;

import net.minecraft.advancements.Criterion;
import dev.anvilcraft.lib.v2.registrum.providers.generators.RegistrumRecipeProvider;

//? <1.21.11 {
/*import net.minecraft.advancements.criterion.InventoryChangeTrigger;
*///?} else {
import net.minecraft.advancements.criterion.InventoryChangeTrigger;

//?}
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
//~ if >1.20.1 'InventoryChangeTrigger.TriggerInstance' -> 'Criterion<InventoryChangeTrigger.TriggerInstance>' {
//~ if >1.20.1 'RegistrateRecipeProvider' -> 'RegistrumRecipeProvider' {
public class CriterionUtil {

    public static Criterion<InventoryChangeTrigger.TriggerInstance> has(ItemLike item, RegistrumRecipeProvider provider) {
        //? <1.21.11 {
        /*return RegistrumRecipeProvider.has(item);
        *///?} else {
        return provider.has(item);
        //?}

    }

    public static Criterion<InventoryChangeTrigger.TriggerInstance> has(TagKey<Item> tag, RegistrumRecipeProvider provider) {
        //? <1.21.11 {
        /*return RegistrumRecipeProvider.has(tag);
        *///?} else {
        return provider.has(tag);
        //?}
    }
}
//~}
//~}
