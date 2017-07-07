package id.co.ppu.collectionfast2.lkp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.co.ppu.collectionfast2.BuildConfig;
import id.co.ppu.collectionfast2.R;
import id.co.ppu.collectionfast2.component.BasicActivity;
import id.co.ppu.collectionfast2.listener.OnSuccessError;
import id.co.ppu.collectionfast2.location.Location;
import id.co.ppu.collectionfast2.pojo.sync.SyncFileUpload;
import id.co.ppu.collectionfast2.pojo.trn.TrnLDVDetails;
import id.co.ppu.collectionfast2.pojo.trn.TrnPhoto;
import id.co.ppu.collectionfast2.rest.APIClientBuilder;
import id.co.ppu.collectionfast2.util.NetUtil;
import id.co.ppu.collectionfast2.util.Storage;
import id.co.ppu.collectionfast2.util.Utility;
import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Cara yg lama, upload sync terpisah dari sinkronisasi utama.
 * ide baru adalah upload sync digabung dengan sinkronisasi utama, tp sepertinya akan memperlambat jd cara lama masih kupakai dulu
 *
 * Photo hanya tersedia 4 saja, yg masing2 dibuat id = picture4  utk menandakan foto ke-empat
 * each imageView has a tag: an URI
 */
public class ActivityUploadPictureGeo extends BasicActivity {
    public static final String PARAM_CONTRACT_NO = "customer.contractNo";
    public static final String PARAM_COLLECTOR_ID = "collector.id";
    public static final String PARAM_LDV_NO = "ldvNo";

    private static final String TAG = "upload";

    private String contractNo = null;
    private String collectorId = null;
    private String ldvNo = null;
    private String officeCode = null;

    private String mCurrentProfilePhotoPath;

    private ProgressDialog mProgressDialog = null;

    private final CharSequence[] menuItems = {
            "From Camera", "From Gallery", "Delete Photo"
    };

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    @BindView(R.id.ivUpload1)
    ImageView ivUpload1;

    @BindView(R.id.ivUploadCheck1)
    ImageView ivUploadCheck1;

    @BindView(R.id.ivUpload2)
    ImageView ivUpload2;

    @BindView(R.id.ivUploadCheck2)
    ImageView ivUploadCheck2;

    @BindView(R.id.ivUpload3)
    ImageView ivUpload3;

    @BindView(R.id.ivUploadCheck3)
    ImageView ivUploadCheck3;

    @BindView(R.id.ivUpload4)
    ImageView ivUpload4;

    @BindView(R.id.ivUploadCheck4)
    ImageView ivUploadCheck4;

    private void setupHttpClient() {
        String username = APIClientBuilder.SERVER_USERNAME;
        String password = APIClientBuilder.SERVER_PWD;
        String credentials = username + ":" + password;
        final String basic =
                "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                Request.Builder requestBuilder = original.newBuilder()
                        .header("Authorization", basic)
//                        .header("Accept", "application/json")
                        .method(original.method(), original.body());

                Request request = requestBuilder.build();
                okhttp3.Response resp = chain.proceed(request);

                return resp;
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_picture);

        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.contractNo = extras.getString(PARAM_CONTRACT_NO);
            this.collectorId = extras.getString(PARAM_COLLECTOR_ID);
            this.ldvNo = extras.getString(PARAM_LDV_NO);
        }

        if (this.collectorId == null || this.contractNo == null || this.ldvNo == null) {
            throw new RuntimeException("collectorId / ldvNo / contractNo cannot null");
        }

//        UserData userData = (UserData) Storage.getPreference(Storage.KEY_USER, UserData.class);

//        this.officeCode = userData.getBranchId();
        this.officeCode = Storage.getPref(Storage.KEY_USER_BRANCH_ID, null);

        TrnLDVDetails dtl = this.realm.where(TrnLDVDetails.class)
                .equalTo("contractNo", contractNo)
                .findFirst();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_activity_upload_picture);
            getSupportActionBar().setSubtitle(dtl.getCustName());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        setupHttpClient();

        OkHttpClient client = httpClient.connectTimeout(Utility.NETWORK_TIMEOUT_MINUTES, TimeUnit.MINUTES)
                .readTimeout(Utility.NETWORK_TIMEOUT_MINUTES, TimeUnit.MINUTES)
                .build();

        long count = this.realm.where(TrnPhoto.class).count();

        loadPhoto(ivUpload1, ivUploadCheck1, "picture1");
        loadPhoto(ivUpload2, ivUploadCheck2, "picture2");
        loadPhoto(ivUpload3, ivUploadCheck3, "picture3");
        loadPhoto(ivUpload4, ivUploadCheck4, "picture4");

    }

    private void loadPhoto(ImageView view,ImageView checkBox, String photoId) {
        OkHttpClient client = httpClient.connectTimeout(Utility.NETWORK_TIMEOUT_MINUTES, TimeUnit.MINUTES)
                .readTimeout(Utility.NETWORK_TIMEOUT_MINUTES, TimeUnit.MINUTES)
                .build();

        TrnPhoto trnPhoto1 = this.realm.where(TrnPhoto.class)
                .equalTo("ldvNo", this.ldvNo)
                .equalTo("contractNo", this.contractNo)
                .equalTo("collCode", this.collectorId)
                .equalTo("photoId", photoId)
                .findFirst();

        if (trnPhoto1 != null) {
            Picasso pic = new Picasso.Builder(this)
                    .downloader(new OkHttp3Downloader(client))
                    .build();
            pic.setIndicatorsEnabled(true);
            if (trnPhoto1.getFilename().startsWith("content")
                    ) {
                Uri uri = Uri.parse(trnPhoto1.getFilename().toString());
                pic.load(uri).into(view);

                view.setTag(uri);

            }else if (trnPhoto1.getFilename().startsWith("/storage")) {
                Uri uri = Uri.parse(trnPhoto1.getFilename().toString());
                File file = new File(uri.getPath());

                pic.load(file).into(view);

                view.setTag(uri);

            } else {
                pic.load(convertPictureIDToUrl(trnPhoto1.getPhotoId()))
                        .into(view);

                // no need to set tag, because the picture came from server
            }

        }

        RealmResults<SyncFileUpload> uploads = this.realm.where(SyncFileUpload.class)
                .equalTo("contractNo", this.contractNo)
                .equalTo("collectorId", this.collectorId)
                .findAll();

        SyncFileUpload sync = this.realm.where(SyncFileUpload.class)
                .equalTo("contractNo", this.contractNo)
                .equalTo("collectorId", this.collectorId)
                .equalTo("pictureId", photoId)
                .isNotNull("syncedDate")
                .findFirst();
        if (sync != null) {
            checkBox.setVisibility(View.VISIBLE);
            view.setClickable(false);
            view.setFocusable(false);
            view.setFocusableInTouchMode(false);
        }

    }

    /**
     *
     * @param pictureId picture4
     * @return
     */
    private String convertPictureIDToUrl(String pictureId) {
        HttpUrl httpUrl = Utility.buildUrl(Storage.getPrefAsInt(Storage.KEY_SERVER_ID, 0));
        String fixUrl = httpUrl.toString();

        if (fixUrl.charAt(fixUrl.length() - 1) != '/') {
            fixUrl += '/';
        }

        String link = fixUrl + "fast/photo/" + this.collectorId + "/" + this.officeCode + "/" + this.ldvNo + "/" + this.contractNo + "/" + pictureId;

        return link;

//        return "https://www.google.com/images/branding/googlelogo/2x/googlelogo_color_272x92dp.png";
    }

    private void showDialog(final ImageView targetImage, final int returnCodeFromCamera, final int returnCodeFromGallery) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(menuItems, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    // from camera
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    // Ensure that there's a camera activity to handle the intent
                    if (takePicture.resolveActivity(getPackageManager()) != null) {

                        // Create the File where the photo should go
                        File photoFile = null;
                        try {
                            photoFile = File.createTempFile("JPEG_" + Utility.convertDateToString(new Date(), "yyyyMMdd_HHmmss"),  ".jpg", getExternalFilesDir(Environment.DIRECTORY_PICTURES) );

                        } catch (IOException ex) {
                            // Error occurred while creating the File
                            return;
                        }
                        // Continue only if the File was successfully created
                        if (photoFile != null) {
                            mCurrentProfilePhotoPath = photoFile.getAbsolutePath();

                            Uri photoURI = FileProvider.getUriForFile(ActivityUploadPictureGeo.this,
                                    BuildConfig.APPLICATION_ID + ".provider",
                                    photoFile);
                            takePicture.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                            // fix kitkat
                            List<ResolveInfo> resInfoList = ActivityUploadPictureGeo.this.getPackageManager().queryIntentActivities(takePicture, PackageManager.MATCH_DEFAULT_ONLY);
                            for (ResolveInfo resolveInfo : resInfoList) {
                                String packageName = resolveInfo.activityInfo.packageName;
                                ActivityUploadPictureGeo.this.grantUriPermission(packageName, photoURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            }

                            startActivityForResult(takePicture, returnCodeFromCamera);//zero can be replaced with any action code
                        }

                    }


                } else if (item == 1) {
                    // from gallery
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, returnCodeFromGallery);//one can be replaced with any action code
                } else if (item == 2) {
                    // delete
                    Drawable icon;
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        icon = VectorDrawableCompat.create(getResources(), R.drawable.ic_add_a_photo_black_24dp, getTheme());
                    } else {
                        icon = getResources().getDrawable(R.drawable.ic_add_a_photo_black_24dp, getTheme());
                    }

                    targetImage.setImageURI(null);
                    targetImage.setImageDrawable(icon);
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    @OnClick(R.id.ivUpload1)
    public void onClickUpload1() {
        showDialog(ivUpload1, 0, 1);
    }

    @OnClick(R.id.ivUpload2)
    public void onClickUpload2() {
        showDialog(ivUpload2, 2, 3);
    }

    @OnClick(R.id.ivUpload3)
    public void onClickUpload3() {
        showDialog(ivUpload3, 4, 5);
    }

    @OnClick(R.id.ivUpload4)
    public void onClickUpload4() {
        showDialog(ivUpload4, 6, 7);
    }

    private void uploadPicture(final String picId, ImageView targetImage, boolean skip, final OnSuccessError listener) {
        Object tag = targetImage.getTag();
        if (skip || tag == null) {
//            Toast.makeText(this, picId + " skipped", Toast.LENGTH_SHORT).show();
            if (listener != null) {
                listener.onSkip();
                return;
            }
        }

        TrnPhoto trnPhoto = null;
        String photoId = null;

        if (targetImage == ivUpload1) {
            photoId = "picture1";
        } else if (targetImage == ivUpload2) {
            photoId = "picture2";
        } else if (targetImage == ivUpload3) {
            photoId = "picture3";
        } else if (targetImage == ivUpload4) {
            photoId = "picture4";
        }

        if (photoId != null) {
            trnPhoto = this.realm.where(TrnPhoto.class)
                    .equalTo("ldvNo", ldvNo)
                    .equalTo("contractNo", contractNo)
                    .equalTo("collCode", collectorId)
                    .equalTo("photoId", photoId)
                    .findFirst()
            ;
        }

        if (trnPhoto == null) {
            if (listener != null) {
                listener.onSkip();
                return;
            }
        }

        Uri uri = Uri.parse(tag.toString());
        NetUtil.uploadPhoto(this, this.realm.copyFromRealm(trnPhoto), uri, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ResponseBody body1 = response.body();

                if (response.isSuccessful()) {
                    realm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {

                            SyncFileUpload sync = realm.where(SyncFileUpload.class)
                                    .equalTo("contractNo", contractNo)
                                    .equalTo("collectorId", collectorId)
                                    .equalTo("pictureId", picId)
                                    .findFirst();
                            if (sync == null) {
                                sync = new SyncFileUpload();
                                sync.setUid(UUID.randomUUID().toString());
                                sync.setContractNo(contractNo);
                                sync.setCollectorId(collectorId);
                                sync.setPictureId(picId);
                            }

                            sync.setSyncedDate(new Date());

                            realm.copyToRealmOrUpdate(sync);
                        }
                    }, new Realm.Transaction.OnSuccess() {
                        @Override
                        public void onSuccess() {
                            if (listener != null)
                                listener.onSuccess(null);
                        }
                    });

                    try {
                        if (body1 != null) {
                            String s = body1.string();

                            Log.d(TAG, s);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    try {
                        if (body1 != null) {
                            String s = body1.string();

                            Log.d(TAG, s);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(ActivityUploadPictureGeo.this, "upload " + picId + " failed", Toast.LENGTH_SHORT).show();
                    if (listener != null)
                        listener.onSkip();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ActivityUploadPictureGeo.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                t.printStackTrace();
                if (listener != null)
                    listener.onFailure(t);
            }
        });

    }

    private void uploadPicture1() {
        uploadPicture("picture1", ivUpload1, ivUploadCheck1.isShown(), new OnSuccessError() {
            @Override
            public void onSuccess(String msg) {
                ivUploadCheck1.setVisibility(View.VISIBLE);
                ivUpload1.setClickable(false);
                ivUpload1.setFocusable(false);
                ivUpload1.setFocusableInTouchMode(false);
                uploadPicture2();
            }

            @Override
            public void onFailure(Throwable throwable) {
                uploadPicture2();
            }

            @Override
            public void onSkip() {
                uploadPicture2();
            }
        });
    }

    private void uploadPicture2() {
        uploadPicture("picture2", ivUpload2, ivUploadCheck2.isShown(), new OnSuccessError() {
            @Override
            public void onSuccess(String msg) {
                ivUploadCheck2.setVisibility(View.VISIBLE);
                ivUpload2.setClickable(false);
                ivUpload2.setFocusable(false);
                ivUpload2.setFocusableInTouchMode(false);
                uploadPicture3();
            }

            @Override
            public void onFailure(Throwable throwable) {
                uploadPicture3();
            }

            @Override
            public void onSkip() {
                uploadPicture3();
            }
        });
    }

    private void uploadPicture3() {
        uploadPicture("picture3", ivUpload3, ivUploadCheck3.isShown(), new OnSuccessError() {
            @Override
            public void onSuccess(String msg) {
                ivUploadCheck3.setVisibility(View.VISIBLE);
                ivUpload3.setClickable(false);
                ivUpload3.setFocusable(false);
                ivUpload3.setFocusableInTouchMode(false);
                uploadPicture4();
            }

            @Override
            public void onFailure(Throwable throwable) {
                uploadPicture4();
            }

            @Override
            public void onSkip() {
                uploadPicture4();
            }
        });
    }

    private void uploadPicture4() {
        uploadPicture("picture4", ivUpload4, ivUploadCheck4.isShown(), new OnSuccessError() {
            @Override
            public void onSuccess(String msg) {
                ivUploadCheck4.setVisibility(View.VISIBLE);
                ivUpload4.setClickable(false);
                ivUpload4.setFocusable(false);
                ivUpload4.setFocusableInTouchMode(false);

                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
            }

            @Override
            public void onFailure(Throwable throwable) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
            }

            @Override
            public void onSkip() {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
            }
        });
    }

    @OnClick(R.id.btnUpload)
    public void onClickUpload() {

        if (!NetUtil.isConnected(this)) {
            Utility.showDialog(this, getString(R.string.title_no_connection), getString(R.string.error_online_required));
            return;
        }

        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
        }
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.show();

        uploadPicture1();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        if (resultCode != RESULT_OK) {
            return;
        }

        ImageView targetImage = null;
        Uri selectedImage = null;

        switch (requestCode) {
            case 0:
            case 1:
                targetImage = ivUpload1;
                break;
            case 2:
            case 3:
                targetImage = ivUpload2;
                break;
            case 4:
            case 5:
                targetImage = ivUpload3;
                break;
            case 6:
            case 7:
                targetImage = ivUpload4;
                break;
        }

        if (targetImage == null)
            return;

        // kalo genap brarti camera
        // kalo ganjil brarti gallery
        if (requestCode % 2 == 0) {
            selectedImage = Uri.parse(mCurrentProfilePhotoPath);

            File file = new File(selectedImage.getPath());

            Picasso.with(this)
                    .load(file)
                    .into(targetImage);
        } else {
            selectedImage = imageReturnedIntent.getData();

            Picasso.with(this)
                    .load(selectedImage)
                    .into(targetImage);

        }

//        Uri selectedImage = imageReturnedIntent.getData();
//        selectedImage = Uri.parse(mCurrentProfilePhotoPath);

//        File file = new File(selectedImage.getPath());
//
//        Picasso.with(this)
//                .load(file)
//                .into(targetImage);

        targetImage.setTag(selectedImage);

        final ImageView finalTargetImage = targetImage;

        double[] gps = Location.getGPS(this);
        final String latitude = String.valueOf(gps[0]);
        final String longitude = String.valueOf(gps[1]);

        this.realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                String photoId = null;
                if (finalTargetImage == ivUpload1) {
                    photoId = "picture1";
                } else if (finalTargetImage == ivUpload2) {
                    photoId = "picture2";
                } else if (finalTargetImage == ivUpload3) {
                    photoId = "picture3";
                } else if (finalTargetImage == ivUpload4) {
                    photoId = "picture4";
                }

                if (photoId != null) {

                    TrnPhoto trnPhoto = realm.where(TrnPhoto.class)
                            .equalTo("ldvNo", ldvNo)
                            .equalTo("contractNo", contractNo)
                            .equalTo("collCode", collectorId)
                            .equalTo("photoId", photoId)
                            .findFirst();
                    if (trnPhoto == null) {
                        trnPhoto = new TrnPhoto();
                        trnPhoto.setUid(UUID.randomUUID().toString());
                        trnPhoto.setFilename(finalTargetImage.getTag().toString());
                        trnPhoto.setCreatedBy(Utility.LAST_UPDATE_BY);
                        trnPhoto.setCreatedTimestamp(new Date());
                    }
                    trnPhoto.setOfficeCode(officeCode);
                    trnPhoto.setContractNo(contractNo);
                    trnPhoto.setCollCode(collectorId);
                    trnPhoto.setLdvNo(ldvNo);
                    trnPhoto.setLatitude(latitude);
                    trnPhoto.setLongitude(longitude);
                    trnPhoto.setPhotoId(photoId);
                    trnPhoto.setLastupdateBy(Utility.LAST_UPDATE_BY);
                    trnPhoto.setLastupdateTimestamp(new Date());

                    realm.copyToRealmOrUpdate(trnPhoto);
                }


            }
        });
    }


}