package ironfurnaces.registration;

import ironfurnaces.items.JovialState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class ModBlockState {

    public static final EnumProperty<JovialState> JOVIAL_STATE = EnumProperty.create("jovial", JovialState.class);


    //? <1.21.11{
    // Legacy
    public static final IntegerProperty TYPE = IntegerProperty.create("type", 0, 2);
    public static final IntegerProperty JOVIAL = IntegerProperty.create("jovial", 0, 2);
    //?}

}
