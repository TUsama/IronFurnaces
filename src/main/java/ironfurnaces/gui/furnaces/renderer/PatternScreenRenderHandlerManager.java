package ironfurnaces.gui.furnaces.renderer;

import ironfurnaces.gui.furnaces.FurnacePatternScreen;
import ironfurnaces.tileentity.furnaces.pattern.mode.internal.FactoryModeHandler;
import ironfurnaces.tileentity.furnaces.pattern.mode.internal.GeneratorModeHandler;
import ironfurnaces.tileentity.furnaces.pattern.mode.internal.VanillaFurnaceModeHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class PatternScreenRenderHandlerManager {

    public static final PatternScreenRenderHandlerManager INSTANCE = new PatternScreenRenderHandlerManager();
    private final Map<String, Function<FurnacePatternScreen, AbstractPatternScreenRenderHandler>> map = new HashMap<>();

    public PatternScreenRenderHandlerManager() {
        map.put(VanillaFurnaceModeHandler.ID, VanillaFurnaceRenderHandler::new);
        map.put(FactoryModeHandler.ID, FactoryRenderHandler::new);
        map.put(GeneratorModeHandler.ID, GeneratorRenderHandler::new);
    }

    public void register(String id, Function<FurnacePatternScreen, AbstractPatternScreenRenderHandler> getter){
        map.put(id, getter);
    }

    public AbstractPatternScreenRenderHandler get(String id, FurnacePatternScreen screen){
        return map.getOrDefault(id, VanillaFurnaceRenderHandler::new).apply(screen);
    }
}
