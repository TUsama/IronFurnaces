package ironfurnaces.event;

import com.clefal.nirvana_lib.relocated.net.neoforged.bus.api.Event;
import ironfurnaces.tileentity.furnaces.pattern.mode.AbstractFurnaceModeHandler;

import java.util.HashMap;
import java.util.Map;

public class RegisterModeHandlerEvent extends Event {
    Map<String, AbstractFurnaceModeHandler> map = new HashMap<>();

    public RegisterModeHandlerEvent() {
    }

}
