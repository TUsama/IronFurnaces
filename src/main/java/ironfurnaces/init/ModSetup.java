package ironfurnaces.init;

import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;


import java.util.HashMap;
import java.util.Map;

public class ModSetup {

    public static final Map<Holder.Reference<Item>, Integer> SMOKING_BURNS = new HashMap<>();
    public static final Map<Holder.Reference<Item>, Boolean> HAS_RECIPE = new HashMap<>();
    public static final Map<Holder.Reference<Item>, Boolean> HAS_RECIPE_SMOKING = new HashMap<>();
    public static final Map<Holder.Reference<Item>, Boolean> HAS_RECIPE_BLASTING = new HashMap<>();


}
