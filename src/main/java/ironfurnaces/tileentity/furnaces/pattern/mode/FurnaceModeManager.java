package ironfurnaces.tileentity.furnaces.pattern.mode;

import com.clefal.nirvana_lib.relocated.io.vavr.Tuple;
import com.clefal.nirvana_lib.relocated.io.vavr.Tuple2;
import ironfurnaces.tileentity.furnaces.menu.FurnacePatternMenu;
import ironfurnaces.tileentity.furnaces.menu.handler.AbstractCompatMenuHandler;
import ironfurnaces.tileentity.furnaces.menu.handler.IMenuHandler;
import ironfurnaces.tileentity.furnaces.pattern.IFurnaceStats;
import ironfurnaces.tileentity.furnaces.pattern.mode.compat.CompatModeHandler;
import ironfurnaces.tileentity.furnaces.pattern.mode.internal.FactoryModeHandler;
import ironfurnaces.tileentity.furnaces.pattern.mode.internal.GeneratorModeHandler;
import ironfurnaces.tileentity.furnaces.pattern.mode.internal.VanillaFurnaceModeHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FurnaceModeManager {
    public static final FurnaceModeManager INSTANCE = new FurnaceModeManager();

    private final Map<String, AbstractFurnaceModeHandler> map = new HashMap<>();


    public FurnaceModeManager() {
        map.put(VanillaFurnaceModeHandler.ID, VanillaFurnaceModeHandler.INSTANCE);
        map.put(FactoryModeHandler.ID, FactoryModeHandler.INSTANCE);
        map.put(GeneratorModeHandler.ID, GeneratorModeHandler.INSTANCE);
    }

    public void register(String id, AbstractFurnaceModeHandler handler) {
        map.put(id, handler);
    }

    public AbstractFurnaceModeHandler get(String id) {
        return map.getOrDefault(id, VanillaFurnaceModeHandler.INSTANCE);
    }

    public int getMaxInputSlot(IFurnaceStats<?> stats){
        return map.values().stream()
                .map(handler -> handler.expectedInputSlots(stats))
                .max(Integer::compareTo)
                .orElse(0);
    }

    public int getMaxOutputSlot(IFurnaceStats<?> stats){
        return map.values().stream()
                .map(handler -> handler.expectedOutputSlots(stats))
                .max(Integer::compareTo)
                .orElse(0);
    }

    public int getMaxFuelSlot(IFurnaceStats<?> stats){
        return map.values().stream()
                .map(handler -> handler.expectedFuelSlots(stats))
                .max(Integer::compareTo)
                .orElse(0);
    }

    public Map<String, AbstractCompatMenuHandler> getAllMenuHandlers(FurnacePatternMenu menu){
        return this.map.entrySet().stream()
                .filter(x -> x.getValue() instanceof CompatModeHandler)
                .map(x -> Tuple.of(x.getKey(), ((CompatModeHandler) x.getValue()).getMenuHandler()))
                .filter(x -> x._2 != null)
                .map(x -> Tuple.of(x._1, x._2.apply(menu)))
                .collect(HashMap::new,
                        (map, tuple2) -> map.put(tuple2._1, tuple2._2),
                        HashMap::putAll);
    }
}
