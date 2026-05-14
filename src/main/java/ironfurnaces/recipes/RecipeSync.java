package ironfurnaces.recipes;

import ironfurnaces.registration.ModCustomRecipe;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;

public final class RecipeSync {
    private RecipeSync() {}

    public static void register(IEventBus gameBus) {
        gameBus.addListener(RecipeSync::onDatapackSync);
    }

    private static void onDatapackSync(OnDatapackSyncEvent event) {
        event.sendRecipes(ModCustomRecipe.GENERATOR_RECIPE.get());
    }
}
