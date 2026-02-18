package ironfurnaces.registration;

import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.providers.ProviderType;
import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.util.duck.AdvancementBuilderDuck;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.resources.ResourceLocation;

import static ironfurnaces.loaders.IronFurnaces.REGISTRATE;

public class ModAdvancements {
    public static Registrate RAINBOW_COAL = REGISTRATE.addDataGenerator(ProviderType.ADVANCEMENT, x -> {
        ((AdvancementBuilderDuck) Advancement.Builder.advancement()
                .display(
                        ModItems.RAINBOW_COAL.get(),
                        x.title(IronFurnaces.MOD_ID, "rainbow_coal", "Taste the Rainbow!").withStyle(ChatFormatting.LIGHT_PURPLE),
                        x.desc(IronFurnaces.MOD_ID, "rainbow_coal", "Obtain the Rainbow Coal"),
                        new ResourceLocation("minecraft", "textures/gui/advancements/backgrounds/stone.png"),
                        FrameType.TASK,
                        false,
                        false,
                        false
                )
                .addCriterion("rainbow_coal",
                        InventoryChangeTrigger.TriggerInstance.hasItems(
                                ItemPredicate.Builder.item()
                                        .of(ModItems.RAINBOW_COAL.get())
                                        .withCount(MinMaxBounds.Ints.between(1, 1))
                                        .build()
                        )
                ))
                .ironFurnaces$save(x, "rainbow_coal");

    });

    public static void register(){

    }
}
