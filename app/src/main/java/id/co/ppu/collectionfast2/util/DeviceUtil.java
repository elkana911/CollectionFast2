package id.co.ppu.collectionfast2.util;

import android.Manifest;
import android.app.Activity;
import android.support.v4.app.ActivityCompat;

/**
 * Created by Eric on 04-Feb-17.
 */

public class DeviceUtil {

//    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 344;
//    public static final int MY_PERMISSIONS_REQUEST_PHONE = 346;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA_AND_CONTACTS_AND_LOCATION = 411;
    public static final int MY_PERMISSIONS_REQUEST_PHONE_AND_STORAGE = 412;

/*
    public static boolean isCameraPermitted(Context ctx) {
        int PERMISSION = ContextCompat.checkSelfPermission(ctx,
                Manifest.permission.CAMERA);
        return PERMISSION == 0;
    }
    public static boolean isContactsPermitted(Context ctx) {
        int PERMISSION = ContextCompat.checkSelfPermission(ctx,
                Manifest.permission.READ_CONTACTS);
        return PERMISSION == 0;
    }
    public static boolean isLocationPermitted(Context ctx) {
        int PERMISSION = ContextCompat.checkSelfPermission(ctx,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return PERMISSION == 0;
    }
    public static boolean isPhonePermitted(Context ctx) {
        int PERMISSION = ContextCompat.checkSelfPermission(ctx,
                Manifest.permission.READ_PHONE_STATE);
        return PERMISSION == 0;
    }
    public static boolean isStoragePermitted(Context ctx) {
        int PERMISSION = ContextCompat.checkSelfPermission(ctx,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return PERMISSION == 0;
    }
*/
    public static void askCameraContactsAndLocationPermission(Activity act) {
        ActivityCompat.requestPermissions(act, new String[]{Manifest.permission.CAMERA
                        , Manifest.permission.READ_CONTACTS
                        , Manifest.permission.ACCESS_FINE_LOCATION},
                MY_PERMISSIONS_REQUEST_CAMERA_AND_CONTACTS_AND_LOCATION);

    }

    /*
    public static void askCameraPermission(Activity act) {
        ActivityCompat.requestPermissions(act, new String[]{Manifest.permission.CAMERA},
                MY_PERMISSIONS_REQUEST_CAMERA);
    }

    public static void askPhonePermission(Activity act) {
        ActivityCompat.requestPermissions(act, new String[]{Manifest.permission.READ_PHONE_STATE},
                MY_PERMISSIONS_REQUEST_PHONE);
    }
    */

    public static void askPhoneAndStoragePermission(Activity act) {
        ActivityCompat.requestPermissions(act, new String[]{Manifest.permission.READ_PHONE_STATE
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE},
                MY_PERMISSIONS_REQUEST_PHONE_AND_STORAGE);

    }


}
