package ironfurnaces.registration.util;




import net.minecraft.advancements.Criterion;


//? <1.21.11 {
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
//?} else {
/*import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
*///?}
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
//~ if >1.20.1 'InventoryChangeTrigger.TriggerInstance' -> 'Criterion<InventoryChangeTrigger.TriggerInstance>' {
public class CriterionUtil {

    public static InventoryChangeTrigger.TriggerInstance has(ItemLike item, RegistrateRecipeProvider provider) {
        //? <1.21.11 {
        return RegistrateRecipeProvider.has(item);
        //?} else {
        /*return provider.has(item);
        *///?}

    }

    public static InventoryChangeTrigger.TriggerInstance has(TagKey<Item> tag, RegistrateRecipeProvider provider) {
        //? <1.21.11 {
        return RegistrateRecipeProvider.has(tag);
        //?} else {
        /*return provider.has(tag);
        *///?}
    }
}
//~}
