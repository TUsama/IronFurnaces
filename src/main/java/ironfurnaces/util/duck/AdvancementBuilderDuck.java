package ironfurnaces.util.duck;

import net.minecraft.advancements.AdvancementHolder;

import java.util.function.Consumer;

public interface AdvancementBuilderDuck {

    AdvancementHolder ironFurnaces$save(Consumer<AdvancementHolder> consumer, String id);
}
