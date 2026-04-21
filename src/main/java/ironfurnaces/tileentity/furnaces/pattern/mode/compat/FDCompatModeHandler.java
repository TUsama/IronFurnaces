package ironfurnaces.tileentity.furnaces.pattern.mode.compat;

import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.cache.IRecipeTypeHandler;
import ironfurnaces.tileentity.furnaces.cache.recipe_type_handlers.FarmerDelightCookingRecipeTypeHandler;
import ironfurnaces.tileentity.furnaces.handler.EnergyLitHandler;
import ironfurnaces.tileentity.furnaces.handler.IFurnaceLitHandler;
import ironfurnaces.tileentity.furnaces.handler.ItemFuelLitHandler;
import ironfurnaces.tileentity.furnaces.menu.FurnacePatternMenu;
import ironfurnaces.tileentity.furnaces.menu.handler.AbstractCompatMenuHandler;
import ironfurnaces.tileentity.furnaces.menu.handler.FDMenuHandler;
import ironfurnaces.tileentity.furnaces.pattern.IFurnaceStats;
import net.minecraft.client.renderer.Rect2i;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Supplier;

public class FDCompatModeHandler extends CompatModeHandler {
    public static final FDCompatModeHandler INSTANCE = new FDCompatModeHandler();

    public static final String ID = "farmer_delight_cooking_pot";

    @Override
    public String getId() {
        return ID;
    }


    @Override
    public int expectedInputSlots(IFurnaceStats<?> stats) {
        return 7;
    }

    @Override
    public int expectedOutputSlots(IFurnaceStats<?> stats) {
        return 1;
    }

    @Override
    public int expectedFuelSlots(IFurnaceStats<?> stats) {
        return 1;
    }

    @NotNull
    @Override
    public Rect2i getArea(FurnacePatternMenu menu) {
        return new Rect2i(79, 35, 23, 16);
    }

    @Override
    public boolean isRainbowActive(FurnacePatternBlockEntity blockEntity) {
        return blockEntity.getInstanceManager().hasInstances();
    }

    @Override
    public Supplier<IFurnaceLitHandler> getLitHandler() {
        return EnergyLitHandler::new;
    }

    @Nullable
    @Override
    public IRecipeTypeHandler getAttachRecipeTypeHandler() {
        return FarmerDelightCookingRecipeTypeHandler.getInstance();
    }

    @Override
    @Nullable
    public Function<FurnacePatternMenu, AbstractCompatMenuHandler> getMenuHandler() {
        return FDMenuHandler::new;
    }
}
