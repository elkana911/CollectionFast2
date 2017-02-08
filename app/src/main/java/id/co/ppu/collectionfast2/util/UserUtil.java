package id.co.ppu.collectionfast2.util;

/**
 * Created by Eric on 07-Feb-17.
 */

public class UserUtil {

    public static boolean userIsAdmin(String username, String password) {
        return username.equals("admin") && password.equals("admin");
    }

    public static boolean userIsDemo(String username, String password) {
        return username.equals("demo") && password.equals("demo");
    }
}
