package ironfurnaces.network;

import com.clefal.nirvana_lib.network.newtoolchain.S2CModPacket;
import ironfurnaces.gui.furnaces.FurnacePatternScreen;
import ironfurnaces.tileentity.furnaces.cache.recipe_type_handlers.FarmerDelightCookingRecipeTypeHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;
//? 1.20.1 {

//?} else {

/*import net.minecraft.world.item.crafting.RecipeHolder;

*///?}
import java.util.Optional;

public class S2CSyncFDDataPacket implements S2CModPacket<S2CSyncFDDataPacket> {
    private ResourceLocation lockedRecipe;
    private ResourceLocation lastRecipe;

    public S2CSyncFDDataPacket() {
    }

    public S2CSyncFDDataPacket(ResourceLocation lockedRecipe, ResourceLocation lastRecipe) {
        this.lockedRecipe = lockedRecipe;
        this.lastRecipe = lastRecipe;
    }

    @Override
    public void handleClient() {
        if (Minecraft.getInstance().screen instanceof FurnacePatternScreen patternScreen){
            if (patternScreen.getMenu().blockEntity.getAugments().getCurrentRecipeType() instanceof FarmerDelightCookingRecipeTypeHandler handler) {
                if (lockedRecipe != null) {
                    var recipe = Minecraft.getInstance().level.getRecipeManager().byKey(lockedRecipe);
                    //~ if >1.20.1 'recipe.get()' -> 'recipe.get().value()'
                    if (recipe.isPresent() && recipe.get() instanceof CookingPotRecipe cookingPotRecipe){
                        //~ if >1.20.1 'cookingPotRecipe' -> '((RecipeHolder<CookingPotRecipe>) recipe.get())'
                        handler.setLockedRecipe(cookingPotRecipe);
                    }
                    handler.isLocking = true;
                } else {
                    handler.setLockedRecipe(null);
                    handler.isLocking = false;
                }
                if (lastRecipe != null) {
                    var recipe = Minecraft.getInstance().level.getRecipeManager().byKey(lastRecipe);
                    //~ if >1.20.1 'recipe.get()' -> 'recipe.get().value()'
                    if (recipe.isPresent() && recipe.get() instanceof CookingPotRecipe cookingPotRecipe){
                        //~ if >1.20.1 'cookingPotRecipe' -> '((RecipeHolder<CookingPotRecipe>) recipe.get())'
                        handler.setLastRecipe(cookingPotRecipe);

                    }
                } else {
                    handler.setLastRecipe(null);
                }


            }
        }
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        if (lockedRecipe != null){
            friendlyByteBuf.writeResourceLocation(lockedRecipe);
        } else {
            friendlyByteBuf.writeUtf("null");
        }

        if (lastRecipe != null){
            friendlyByteBuf.writeResourceLocation(lastRecipe);
        } else {
            friendlyByteBuf.writeUtf("null");
        }

    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) {
        String s = friendlyByteBuf.readUtf();
        if (s.equals("null")){
            this.lockedRecipe = null;
        } else {
            this.lockedRecipe = ResourceLocation.tryParse(s);
        }

        String s2 = friendlyByteBuf.readUtf();
        if (s2.equals("null")){
            this.lastRecipe = null;
        } else {
            this.lastRecipe = ResourceLocation.tryParse(s2);
        }

    }

    @Override
    public Class<S2CSyncFDDataPacket> getSelfClass() {
        return S2CSyncFDDataPacket.class;
    }
}
