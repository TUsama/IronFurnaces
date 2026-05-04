package ironfurnaces.util.duck;



//? 1.20.1 {
/*import net.minecraft.advancements.Advancement;
*///? } else {
import net.minecraft.advancements.AdvancementHolder;
//?}
import java.util.function.Consumer;

public interface AdvancementBuilderDuck {
    //~ if >1.20.1 'Advancement' -> 'AdvancementHolder'
    AdvancementHolder ironFurnaces$save(Consumer<AdvancementHolder> consumer, String id);
}
