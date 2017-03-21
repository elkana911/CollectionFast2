package id.co.ppu.collectionfast2.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    public static File createTempFileForProfilePic(Context ctx) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = ctx.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }
}
