package ironfurnaces.util.duck;

import net.minecraft.advancements.Advancement;

import java.util.function.Consumer;

public interface AdvancementBuilderDuck {
    Advancement ironFurnaces$save(Consumer<Advancement> consumer, String id);
}
