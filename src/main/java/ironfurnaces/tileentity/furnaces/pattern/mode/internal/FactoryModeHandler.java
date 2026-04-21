package ironfurnaces.tileentity.furnaces.pattern.mode.internal;

import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.handler.EnergyLitHandler;
import ironfurnaces.tileentity.furnaces.handler.IFurnaceLitHandler;
import ironfurnaces.tileentity.furnaces.menu.FurnacePatternMenu;
import ironfurnaces.tileentity.furnaces.pattern.IFurnaceStats;
import net.minecraft.client.renderer.Rect2i;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public final class FactoryModeHandler extends InternalFurnaceModeHandler {
    public static final FactoryModeHandler INSTANCE = new FactoryModeHandler();

    public static final String ID = "factory";
    @Override
    public String getId() {
        return ID;
    }

    @Override
    public int expectedInputSlots(IFurnaceStats<?> stats) {
        return stats.inputSlotAmount();
    }

    @Override
    public int expectedOutputSlots(IFurnaceStats<?> stats) {
        return stats.inputSlotAmount();
    }

    @Override
    public int expectedFuelSlots(IFurnaceStats<?> stats) {
        return 0;
    }

    @NotNull
    @Override
    public Rect2i getArea(FurnacePatternMenu menu) {
        return new Rect2i(89, 38, 15, 16);
    }

    @Override
    public boolean isRainbowActive(FurnacePatternBlockEntity blockEntity) {
        return blockEntity.getInstanceManager().hasInstances();
    }

    @Override
    public Supplier<IFurnaceLitHandler> getLitHandler() {
        return EnergyLitHandler::new;
    }

    @Override
    public boolean isFactory() {
        return true;
    }

    @Override
    public boolean needAutoFill() {
        return true;
    }
}
