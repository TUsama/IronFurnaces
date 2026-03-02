package ironfurnaces.registration;

import ironfurnaces.items.JovialState;
import ironfurnaces.tileentity.furnaces.FurnaceMode;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class ModBlockState {

    public static final EnumProperty<JovialState> JOVIAL_STATE = EnumProperty.create("jovial", JovialState.class);
    public static final EnumProperty<FurnaceMode> FURNACE_MODE = EnumProperty.create("mode", FurnaceMode.class);

    // Legacy
    public static final IntegerProperty TYPE = IntegerProperty.create("type", 0, 2);
    public static final IntegerProperty JOVIAL = IntegerProperty.create("jovial", 0, 2);
}
