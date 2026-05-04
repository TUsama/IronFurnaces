
package ironfurnaces.registration.util;

import ironfurnaces.loaders.IronFurnaces;
import lombok.experimental.UtilityClass;
import net.minecraft.resources.Identifier;

@UtilityClass
public class IDUtil {

    //? if >1.21.11 {
    public static String makeNewFurnaceID(String furnaceName) {
        return makeID("new_furnaces/" + furnaceName);
    }
    //?} else {
    /*public static Identifier makeNewFurnaceID(String furnaceName) {
        return makeID("new_furnaces/" + furnaceName);
    }
    *///?}



    //? if >1.21.11 {
    public static String makeID(String name) {
        return name;
    }

    //?} else {
    /*public static Identifier makeID(String path) {
        return IronFurnaces.id(path);
    }
    *///?}

    //? if >1.21.11 {
    public static String newUpgrade(String path) {
        return makeID("new_upgrades/" + path);
    }
    //?} else {
    /*public static Identifier newUpgrade(String path) {
        return makeID("new_upgrades/" + path);
    }
    *///?}

}
