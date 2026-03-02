package ironfurnaces.blocks.furnaces.new_furnace;

import com.google.common.collect.Lists;
import ironfurnaces.tileentity.furnaces.FurnaceMode;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import java.util.Collection;

public class FurnaceModeProperty extends EnumProperty<FurnaceMode> {
    protected FurnaceModeProperty(String name, Class<FurnaceMode> clazz, Collection<FurnaceMode> values) {
        super(name, clazz, values);
    }

    public static FurnaceModeProperty create() {
        return new FurnaceModeProperty("mode", FurnaceMode.class, Lists.newArrayList(FurnaceMode.values()));
    }
}
