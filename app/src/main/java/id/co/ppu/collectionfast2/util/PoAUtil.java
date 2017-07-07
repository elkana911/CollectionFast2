package id.co.ppu.collectionfast2.util;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import id.co.ppu.collectionfast2.BuildConfig;
import id.co.ppu.collectionfast2.component.BasicActivity;
import id.co.ppu.collectionfast2.location.Location;
import id.co.ppu.collectionfast2.pojo.trn.TrnFlagTimestamp;
import id.co.ppu.collectionfast2.pojo.trn.TrnFlagTimestampPK;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * PoA = Photo On Arrival
 * Taken when a collector arrived at destination / customer's house
 * <p>
 * Created by Eric on 14-Feb-17.
 */
public class PoAUtil {

    public static final String POA_PREFIX = "poa";
    public static final String POA_PAYMENT_RECEIVE_PREFIX = POA_PREFIX + "PymtRcv";
    public static final String POA_PAYMENT_ENTRI_PREFIX = POA_PREFIX + "PymtEntri";
    public static final String POA_VISIT_RESULT_PREFIX = POA_PREFIX + "VstRslt";
    public static final String POA_VISIT_RESULT_RPC_PREFIX = POA_PREFIX + "VstRsltRPC";
    public static final String POA_REPO_ENTRI_PREFIX = POA_PREFIX + "RepoEntri";
    public static final String POA_DEFAULT_PREFIX = POA_PREFIX + "Default";

    public static final int POA_JPG_QUALITY = 10;
//    public static final int POA_JPG_QUALITY = 20;

    private static String concatAsJpgFilename(String prefix, String collectorId, String contractNo) {
        return prefix + "_" + collectorId + "_" + contractNo + ".jpg";
    }


    private static String getPoAPath() {
//        File dir = getCacheDir();   // hindari penggunaan cache default, krn bisa dibersihkan by job oleh utility
        String storagePath = Environment.getExternalStorageDirectory().toString() + "/RadanaCache/";

        return storagePath;
    }

    /**
     * sub directory named cache
     */
    private static String getPoACachePath() {
//        File dir = getCacheDir();   // hindari penggunaan cache, krn bisa dibersihkan by job oleh utility
        String storagePath = getPoAPath() + "cache/";

        return storagePath;
    }

    /*
    // will move from cache to outside
    public static boolean commitPhotoOnArrival(String jpgFileName) {
        File from = new File(getPhotoOnArrivalCachePath() + jpgFileName);
        File to = new File(getPhotoOnArrivalPath() + jpgFileName);

        return from.renameTo(to);
    }
    */

    private static String getJpg(BasicActivity activity, String collCode, String contractNo) {
        String jpgFileName = null;
/*
        if (activity instanceof ActivityPaymentReceive)
            jpgFileName = concatAsJpgFilename(POA_PAYMENT_RECEIVE_PREFIX, collCode, contractNo);
        else if (activity instanceof ActivityPaymentEntri)
            jpgFileName = concatAsJpgFilename(POA_PAYMENT_ENTRI_PREFIX, collCode, contractNo);
        else if (activity instanceof ActivityRepoEntry)
            jpgFileName = concatAsJpgFilename(POA_REPO_ENTRI_PREFIX, collCode, contractNo);
        else if (activity instanceof ActivityVisitResult)
            jpgFileName = concatAsJpgFilename(POA_VISIT_RESULT_PREFIX, collCode, contractNo);
        else if (activity instanceof ActivityVisitResultRPC)
            jpgFileName = concatAsJpgFilename(POA_VISIT_RESULT_RPC_PREFIX, collCode, contractNo);
        else if (activity instanceof ActivityPoA)
        */
        jpgFileName = concatAsJpgFilename(POA_DEFAULT_PREFIX, collCode, contractNo);

        return jpgFileName;
    }

    public static void hidePoAFiles() {
        if (!Utility.DEVELOPER_MODE) {
            Storage.putNoMediaInto(new File(getPoAPath()));
            Storage.putNoMediaInto(new File(getPoACachePath()));
        }

    }

    /**
     * Check the file exists or not
     *
     * @return
     * @see #commit(BasicActivity, String, String, String)
     */
    public static File getPoAFile(BasicActivity activity, String collCode, String contractNo) {
//        File dir = getCacheDir();   // hindari penggunaan cache, krn bisa dibersihkan by job oleh utility

        return new File(getPoAPath() + getJpg(activity, collCode, contractNo));
    }

    public static File getPoACacheFile(BasicActivity activity, String collCode, String contractNo) {
//        File dir = getCacheDir();   // hindari penggunaan cache, krn bisa dibersihkan by job oleh utility

        return new File(getPoACachePath() + getJpg(activity, collCode, contractNo));
    }

    /**
     * Following the action taken:
     * <ul>
     * <li>if succeed will insert data into table {@link TrnFlagTimestamp}</li>
     * <li>will also moving the cache picture into parent folder</li>
     * </ul>
     * <p>WARNING ! may return false on second commit
     *
     * @param activity
     * @param collCode
     * @param contractNo
     * @return
     */
    public static boolean commit(final BasicActivity activity, final String collCode, final String ldvNo, final String contractNo) {

        String jpgFileName = getJpg(activity, collCode, contractNo);

        File from = new File(getPoACachePath() + jpgFileName);  // /storage/emulated/0/RadanaCache/cache/poaDefault_demo_71000000069115.jpg
        final File to = new File(getPoAPath() + jpgFileName);   // /storage/emulated/0/RadanaCache/poaDefault_demo_71000000069115.jpg

        boolean ok = from.renameTo(to);

        if (!ok)
            return false;

        Realm realm = Realm.getDefaultInstance();
        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    TrnFlagTimestamp obj = (TrnFlagTimestamp) Storage.getPrefAsJson(Storage.KEY_POA_DATA_TEMPORARY, TrnFlagTimestamp.class, null);

                    if (obj == null) {
                        throw new NullPointerException("No Temporary PoA found !");
                    }
                    if (!obj.getFileName().equals(to.getName())) {
                        throw new RuntimeException("Temporary PoA not match(" + obj.getFileName() + "<>" + to.getName());
                    }

                    realm.copyToRealm(obj);
                }
            });

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (realm != null)
                realm.close();
        }


        return false;
    }

    public static void cleanPoACache() {
        File cacheDir = new File(getPoACachePath());
        Storage.deleteDir(cacheDir);

    }

    public static void cleanPoA() {

        cleanPoACache();

        // delete root
        File dir = new File(getPoAPath());
        File[] toBeDeleted = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return (file.getName().startsWith(POA_PREFIX) && file.getName().endsWith(".jpg"));
            }
        });

        if (toBeDeleted == null)
            return;

        for (File f : toBeDeleted) {
            Log.e("RADANAReset", "Delete " + f.getName() + (f.delete() ? " success" : " failed"));
        }

    }

    /**
     * Only get files that was committed
     *
     * @return
     * @see #commit(BasicActivity, String, String, String)
     */
    public static File[] getPoAFiles() {
        File dir = new File(getPoAPath());

        File[] selectedFiles = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return (file.getName().startsWith(POA_PREFIX) && file.getName().endsWith(".jpg"));
            }
        });

        return selectedFiles;
    }


    /*
    private void deletePoA() {
        // delete pra* files, dgn asumsi data baru. jgn hapus file yg sudah dientri
        File dir = new File(PoAUtil.getPhotoOnArrivalCachePath());
        File[] toBeDeleted = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return (file.getName().startsWith(POA_PREFIX) && file.getName().endsWith(".jpg"));
//                        return (file.getName().startsWith("dailyReport_08") && pathname.getName().endsWith(".txt"));
            }
        });

        for (File f : toBeDeleted) {
            Log.e("RADANA-PRcv", "Delete " + f.getName() + (f.delete() ? " success" : " failed"));
        }
    }
    */

    /**
     * Only check within database. Not the file.
     *
     * @param realm
     * @param collCode
     * @param ldvNo
     * @param contractNo
     * @return
     */
    public static TrnFlagTimestamp isPoADataExists(Realm realm, String collCode, String ldvNo, String contractNo) {
        TrnFlagTimestamp first = realm.where(TrnFlagTimestamp.class)
                .equalTo("pk.contractNo", contractNo)
                .equalTo("pk.ldvNo", ldvNo)
                .equalTo("pk.collCode", collCode)
                .findFirst();

        return first;
    }

    /**
     * @param realm
     * @param collCode
     * @param contractNo
     * @return
     */
    public static void cancel(Realm realm, String collCode, String ldvNo, String contractNo) {
        RealmResults<TrnFlagTimestamp> all = realm.where(TrnFlagTimestamp.class)
                .equalTo("pk.contractNo", contractNo)
                .equalTo("pk.ldvNo", ldvNo)
                .equalTo("pk.collCode", collCode)
                .findAll();

        for (TrnFlagTimestamp trn :
                all) {
            File file = new File(getPoAPath() + trn.getFileName());

            if (file.exists())
                file.delete();

            // getPoAFile(null, collCode, contractNo)
        }

        realm.beginTransaction();
        boolean b = all.deleteAllFromRealm();
        realm.commitTransaction();


    }

    /**
     * You must set onActivityResult to create the file using Uri, resultCode is 1
     *
     * @param activity
     * @param collCode
     * @param contractNo
     * @see #postCameraIntoCache(BasicActivity, String, String)
     * @see #commit(BasicActivity, String, String, String)
     */
    public static void callCameraIntent(BasicActivity activity, String collCode, String ldvNo, String contractNo) throws Exception {

        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        hidePoAFiles();

        // prepare the Uri
        File cacheFile = getPoACacheFile(activity, collCode, contractNo);   ///storage/emulated/0/RadanaCache/cache/poaDefault_demo_71000000008115.jpg

        // clean up the last one
        if (cacheFile.exists())
            cacheFile.delete();

        // must create file first
        cacheFile.getParentFile().mkdirs();
        cacheFile.createNewFile();

        Uri uriSavedImage = null; // content://id.co.ppu.collectionfast2.provider/external_files/RadanaCache/cache/poaDefault_demo_71000000008115.jpg
        uriSavedImage = FileProvider.getUriForFile(activity,
                BuildConfig.APPLICATION_ID + ".provider",
                cacheFile);

        takePicture.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);

        List<ResolveInfo> resInfoList = activity.getPackageManager().queryIntentActivities(takePicture, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            activity.grantUriPermission(packageName, uriSavedImage, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        activity.startActivityForResult(takePicture, 1);//zero can be replaced with any action code

        // create temporary data
        TrnFlagTimestamp obj = new TrnFlagTimestamp();

        double[] gps = Location.getGPS(activity);
        final String latitude = String.valueOf(gps[0]);
        final String longitude = String.valueOf(gps[1]);

        obj.setLatitude(latitude);
        obj.setLongitude(longitude);

        obj.setFileName(getPoAFile(activity, collCode, contractNo).getName());
        obj.setCreatedBy(Utility.LAST_UPDATE_BY);
        obj.setCreatedTimestamp(new Date());


        TrnFlagTimestampPK pk = new TrnFlagTimestampPK();
        pk.setContractNo(contractNo);
        pk.setCollCode(collCode);
        pk.setLdvNo(ldvNo);
        obj.setPk(pk);

        Storage.savePrefAsJson(Storage.KEY_POA_DATA_TEMPORARY, obj);
    }

    /**
     * Dont forget to call commit anytime you set
     *
     * @param activity
     * @param collCode
     * @param contractNo
     * @see #callCameraIntent(BasicActivity, String, String, String)
     * @see #commit(BasicActivity, String, String, String)
     */
    public static File postCameraIntoCache(BasicActivity activity, String collCode, String contractNo) throws Exception {
        File cacheFile = PoAUtil.getPoACacheFile(activity, collCode, contractNo); // /storage/emulated/0/RadanaCache/cache/poaVstRslt_demo_71000000008115.jpg

        //compressing
        InputStream in = new FileInputStream(cacheFile);
        Bitmap bm2 = BitmapFactory.decodeStream(in);

        // flush the compressed as temp file
        File outputDir = new File(getPoACachePath());   //  /storage/emulated/0/RadanaCache/cache
        File outputFile = File.createTempFile(POA_PREFIX + "-", ".jpg", outputDir);// /storage/emulated/0/RadanaCache/cache/poa-300549737.jpg

        OutputStream stream = new FileOutputStream(outputFile);
//                bm2.compress(Bitmap.CompressFormat.JPEG, 10, stream); // agak rusak gambarnya
        bm2.compress(Bitmap.CompressFormat.JPEG, POA_JPG_QUALITY, stream);
        stream.close();
        in.close();

        // delete original if any
        if (cacheFile.exists())
            cacheFile.delete();

        // rename
        outputFile.renameTo(cacheFile);

        return cacheFile;

        //utk tes bisa ga uploadnya. kalo udah tlg dihapus. commit harusnya dilakukan setelah save data success
//                boolean ok = PoAUtil.commitPhotoOnArrival(constructPoAFilename());

    }


    public static void cleanDB(Realm realm) {
        realm.beginTransaction();
        realm.where(TrnFlagTimestamp.class)
                .findAll().deleteAllFromRealm();
        realm.commitTransaction();

    }
}
