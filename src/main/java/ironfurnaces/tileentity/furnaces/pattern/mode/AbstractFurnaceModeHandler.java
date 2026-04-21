package ironfurnaces.tileentity.furnaces.pattern.mode;

import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.handler.IFurnaceLitHandler;
import ironfurnaces.tileentity.furnaces.menu.FurnacePatternMenu;
import ironfurnaces.tileentity.furnaces.pattern.IFurnaceStats;
import net.minecraft.client.renderer.Rect2i;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public abstract class AbstractFurnaceModeHandler {
    public abstract String getId();

    public abstract boolean isInternal();

    public abstract int expectedInputSlots(IFurnaceStats<?> stats);

    public abstract int expectedOutputSlots(IFurnaceStats<?> stats);

    public abstract int expectedFuelSlots(IFurnaceStats<?> stats);
    @Nonnull
    public abstract Rect2i getArea(FurnacePatternMenu menu);

    public abstract boolean isRainbowActive(FurnacePatternBlockEntity blockEntity);

    public abstract Supplier<IFurnaceLitHandler> getLitHandler();

    public abstract boolean isFurnace();

    public abstract boolean isFactory();

    public abstract boolean isGenerator();

    public boolean needAutoFill(){
        return false;
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof AbstractFurnaceModeHandler handler){
            return handler.getId().equals(getId());
        }
        return false;
    }
}
