package ironfurnaces.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.util.duck.AdvancementBuilderDuck;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.advancements.Advancement;
//? 1.20.1 {

//? } else {
import net.minecraft.advancements.AdvancementHolder;
//?}

import java.util.function.Consumer;

@Mixin(Advancement.Builder.class)
@MixinEnvironment
public abstract class AdvancementBuilderMixin implements AdvancementBuilderDuck {

    //~ if >1.20.1 'Advancement' -> 'AdvancementHolder' {
    @Shadow
    public abstract AdvancementHolder build(Identifier id);

    @Override
    public AdvancementHolder ironFurnaces$save(Consumer<AdvancementHolder> consumer, String id) {
        AdvancementHolder advancement = this.build(IronFurnaces.id(id));
        consumer.accept(advancement);
        return advancement;
    }
    //~}
}
