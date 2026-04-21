package ironfurnaces.tileentity.furnaces.pattern.mode.internal;

import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.handler.IFurnaceLitHandler;
import ironfurnaces.tileentity.furnaces.handler.ItemFuelLitHandler;
import ironfurnaces.tileentity.furnaces.menu.FurnacePatternMenu;
import ironfurnaces.tileentity.furnaces.pattern.IFurnaceStats;
import net.minecraft.client.renderer.Rect2i;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public final class VanillaFurnaceModeHandler extends InternalFurnaceModeHandler {
    public static final VanillaFurnaceModeHandler INSTANCE = new VanillaFurnaceModeHandler();

    public static final String ID = "furnace";
    @Override
    public String getId() {
        return ID;
    }

    @Override
    public int expectedInputSlots(IFurnaceStats<?> stats) {
        return 1;
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
        return ItemFuelLitHandler::new;
    }

    @Override
    public boolean isFurnace() {
        return true;
    }
}
