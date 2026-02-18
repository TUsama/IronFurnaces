package ironfurnaces.mixin;

import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.util.duck.AdvancementBuilderDuck;
import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Consumer;

@Mixin(Advancement.Builder.class)
public abstract class AdvancementBuilderMixin implements AdvancementBuilderDuck {


    @Shadow
    public abstract Advancement build(ResourceLocation id);

    @Override
    public Advancement ironFurnaces$save(Consumer<Advancement> consumer, String id) {
        Advancement advancement = this.build(IronFurnaces.id(id));
        consumer.accept(advancement);
        return advancement;
    }
}
