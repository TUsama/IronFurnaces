package ironfurnaces.recipes;

import ironfurnaces.registration.ModCustomRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;

import java.util.List;

public final class ClientRecipeCache {
    private static List<GeneratorRecipe> generatorRecipes = List.of();

    private ClientRecipeCache() {}

    public static void register(IEventBus gameBus) {
        gameBus.addListener(ClientRecipeCache::onRecipesReceived);
        gameBus.addListener(ClientRecipeCache::onClientLogout);
    }

    private static void onRecipesReceived(RecipesReceivedEvent event) {
        generatorRecipes = event.getRecipeMap()
                .byType(ModCustomRecipe.GENERATOR_RECIPE.get())
                .stream()
                .map(RecipeHolder::value)
                .toList();
    }

    private static void onClientLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        generatorRecipes = List.of();
    }

    public static List<GeneratorRecipe> generatorRecipes() {
        return generatorRecipes;
    }
}
