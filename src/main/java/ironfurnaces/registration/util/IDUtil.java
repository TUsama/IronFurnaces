
package ironfurnaces.registration.util;

import ironfurnaces.loaders.IronFurnaces;
import lombok.experimental.UtilityClass;
import net.minecraft.resources.Identifier;

@UtilityClass
public class IDUtil {

    public static String makeNewFurnaceID(String furnaceName) {
        return IronFurnaces.id("new_furnaces/" + furnaceName).toString();
    }

    public static String newUpgrade(String path) {
        return IronFurnaces.id("new_upgrades/" + path).toString();
    }


    public static String makeID(String name) {
        return IronFurnaces.id(name).toString();
    }



}
