//~ replace_Registrate
package ironfurnaces.registration;

import dev.anvilcraft.lib.v2.registrum.Registrum;
import dev.anvilcraft.lib.v2.registrum.providers.ProviderType;
import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.util.duck.AdvancementBuilderDuck;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
//? 1.20.1 {
/*import net.minecraft.advancements.FrameType;
*///? } else {
import net.minecraft.advancements.AdvancementType;
//?}

import static ironfurnaces.loaders.IronFurnaces.REGISTRATE;

public class ModAdvancements {
    public static Registrum RAINBOW_COAL = REGISTRATE.addDataGenerator(ProviderType.ADVANCEMENT, x -> {
        ((AdvancementBuilderDuck) Advancement.Builder.advancement()
                .display(
                        ModItems.RAINBOW_COAL.get(),
                        x.title(IronFurnaces.MOD_ID, "rainbow_coal", "Taste the Rainbow!").withStyle(ChatFormatting.LIGHT_PURPLE),
                        x.desc(IronFurnaces.MOD_ID, "rainbow_coal", "Obtain the Rainbow Coal"),
                        IronFurnaces.parse("textures/gui/advancements/backgrounds/stone.png"),
                        AdvancementType.TASK,
                        false,
                        false,
                        false
                )
                .addCriterion("rainbow_coal",
                        InventoryChangeTrigger.TriggerInstance.hasItems(
                                ModItems.RAINBOW_COAL.get()
                        )
                ))
                .ironFurnaces$save(x, "rainbow_coal");

    });

    public static void register(){

    }
}
