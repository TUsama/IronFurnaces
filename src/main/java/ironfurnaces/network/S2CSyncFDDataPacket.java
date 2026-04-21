package ironfurnaces.network;

import com.clefal.nirvana_lib.network.newtoolchain.S2CModPacket;
import ironfurnaces.gui.furnaces.FurnacePatternScreen;
import ironfurnaces.tileentity.furnaces.cache.recipe_type_handlers.FarmerDelightCookingRecipeTypeHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;

import java.util.Optional;

public class S2CSyncFDDataPacket implements S2CModPacket<S2CSyncFDDataPacket> {
    private ResourceLocation lockedRecipe;

    public S2CSyncFDDataPacket() {
    }

    public S2CSyncFDDataPacket(ResourceLocation lockedRecipe) {
        this.lockedRecipe = lockedRecipe;
    }

    @Override
    public void handleClient() {
        if (Minecraft.getInstance().screen instanceof FurnacePatternScreen patternScreen){
            if (patternScreen.getMenu().blockEntity.getAugments().getCurrentRecipeType() instanceof FarmerDelightCookingRecipeTypeHandler handler) {
                Optional<? extends Recipe<?>> recipe = Minecraft.getInstance().level.getRecipeManager().byKey(lockedRecipe);
                if (recipe.isPresent() && recipe.get() instanceof CookingPotRecipe cookingPotRecipe){
                    handler.setLastRecipe(cookingPotRecipe);
                    handler.setLockedToLastRecipe();
                }

            }
        }
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeResourceLocation(lockedRecipe);
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) {
        this.lockedRecipe = friendlyByteBuf.readResourceLocation();
    }

    @Override
    public Class<S2CSyncFDDataPacket> getSelfClass() {
        return S2CSyncFDDataPacket.class;
    }
}
