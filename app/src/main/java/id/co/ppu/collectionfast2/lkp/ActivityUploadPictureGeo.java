package id.co.ppu.collectionfast2.lkp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.co.ppu.collectionfast2.R;
import id.co.ppu.collectionfast2.component.BasicActivity;
import id.co.ppu.collectionfast2.listener.OnSuccessError;
import id.co.ppu.collectionfast2.location.Location;
import id.co.ppu.collectionfast2.pojo.UserData;
import id.co.ppu.collectionfast2.pojo.sync.SyncFileUpload;
import id.co.ppu.collectionfast2.pojo.trn.TrnLDVDetails;
import id.co.ppu.collectionfast2.pojo.trn.TrnPhoto;
import id.co.ppu.collectionfast2.rest.ServiceGenerator;
import id.co.ppu.collectionfast2.util.NetUtil;
import id.co.ppu.collectionfast2.util.Storage;
import id.co.ppu.collectionfast2.util.Utility;
import io.realm.Realm;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityUploadPictureGeo extends BasicActivity {
    public static final String PARAM_CONTRACT_NO = "customer.contractNo";
    public static final String PARAM_COLLECTOR_ID = "collector.id";
    public static final String PARAM_LDV_NO = "ldvNo";

    private static final String TAG = "upload";

    private String contractNo = null;
    private String collectorId = null;
    private String ldvNo = null;
    private String officeCode = null;

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
        String username = ServiceGenerator.SERVER_USERNAME;
        String password = ServiceGenerator.SERVER_PWD;
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

        UserData userData = (UserData) Storage.getObjPreference(getApplicationContext(), Storage.KEY_USER, UserData.class);

        this.officeCode = userData.getBranchId();

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

        OkHttpClient client = httpClient.connectTimeout(4, TimeUnit.MINUTES)
                .readTimeout(4, TimeUnit.MINUTES)
                .build();

        long count = this.realm.where(TrnPhoto.class).count();

        TrnPhoto trnPhoto1 = this.realm.where(TrnPhoto.class)
                .equalTo("ldvNo", this.ldvNo)
                .equalTo("contractNo", this.contractNo)
                .equalTo("collCode", this.collectorId)
                .equalTo("photoId", "picture1")
                .findFirst();

        if (trnPhoto1 != null) {
            Picasso pic = new Picasso.Builder(this)
                    .downloader(new OkHttp3Downloader(client))
                    .build();
            pic.setIndicatorsEnabled(true);
            pic.load(convertPictureIDToUrl(trnPhoto1.getPhotoId()))
                    .into(ivUpload1);

        }

        TrnPhoto trnPhoto2 = this.realm.where(TrnPhoto.class)
                .equalTo("ldvNo", this.ldvNo)
                .equalTo("contractNo", this.contractNo)
                .equalTo("collCode", this.collectorId)
                .equalTo("photoId", "picture2")
                .findFirst();

        if (trnPhoto2 != null) {

            Picasso pic = new Picasso.Builder(this)
                    .downloader(new OkHttp3Downloader(client))
                    .build();
            pic.setIndicatorsEnabled(true);
            pic.load(convertPictureIDToUrl(trnPhoto2.getPhotoId()))
                    .into(ivUpload2);
        }

        TrnPhoto trnPhoto3 = this.realm.where(TrnPhoto.class)
                .equalTo("ldvNo", this.ldvNo)
                .equalTo("contractNo", this.contractNo)
                .equalTo("collCode", this.collectorId)
                .equalTo("photoId", "picture3")
                .findFirst();

        if (trnPhoto3 != null) {
            Picasso pic = new Picasso.Builder(this)
                    .downloader(new OkHttp3Downloader(client))
                    .build();
            pic.setIndicatorsEnabled(true);
            pic.load(convertPictureIDToUrl(trnPhoto3.getPhotoId()))
                    .into(ivUpload3);

        }

        TrnPhoto trnPhoto4 = this.realm.where(TrnPhoto.class)
                .equalTo("ldvNo", this.ldvNo)
                .equalTo("contractNo", this.contractNo)
                .equalTo("collCode", this.collectorId)
                .equalTo("photoId", "picture4")
                .findFirst();

        if (trnPhoto4 != null) {
            Picasso pic = new Picasso.Builder(this)
                    .downloader(new OkHttp3Downloader(client))
                    .build();
            pic.setIndicatorsEnabled(true);
            pic.load(convertPictureIDToUrl(trnPhoto4.getPhotoId()))
                    .into(ivUpload4);

        }


        SyncFileUpload first = this.realm.where(SyncFileUpload.class)
                .equalTo("contractNo", this.contractNo)
                .equalTo("collectorId", this.collectorId)
                .equalTo("pictureId", "picture1")
                .isNotNull("syncedDate")
                .findFirst();
        if (first != null) {
            ivUploadCheck1.setVisibility(View.VISIBLE);
            ivUpload1.setClickable(false);
            ivUpload1.setFocusable(false);
            ivUpload1.setFocusableInTouchMode(false);
        }
        SyncFileUpload second = this.realm.where(SyncFileUpload.class)
                .equalTo("contractNo", this.contractNo)
                .equalTo("collectorId", this.collectorId)
                .equalTo("pictureId", "picture2")
                .isNotNull("syncedDate")
                .findFirst();
        if (second != null) {
            ivUploadCheck2.setVisibility(View.VISIBLE);
            ivUpload2.setClickable(false);
            ivUpload2.setFocusable(false);
            ivUpload2.setFocusableInTouchMode(false);
        }
        SyncFileUpload third = this.realm.where(SyncFileUpload.class)
                .equalTo("contractNo", this.contractNo)
                .equalTo("collectorId", this.collectorId)
                .equalTo("pictureId", "picture3")
                .isNotNull("syncedDate")
                .findFirst();
        if (third != null) {
            ivUploadCheck3.setVisibility(View.VISIBLE);
            ivUpload3.setClickable(false);
            ivUpload3.setFocusable(false);
            ivUpload3.setFocusableInTouchMode(false);
        }
        SyncFileUpload fourth = this.realm.where(SyncFileUpload.class)
                .equalTo("contractNo", this.contractNo)
                .equalTo("collectorId", this.collectorId)
                .equalTo("pictureId", "picture4")
                .isNotNull("syncedDate")
                .findFirst();
        if (fourth != null) {
            ivUploadCheck4.setVisibility(View.VISIBLE);
            ivUpload4.setClickable(false);
            ivUpload4.setFocusable(false);
            ivUpload4.setFocusableInTouchMode(false);
        }
    }

    private String convertPictureIDToUrl(String pictureId) {
        HttpUrl httpUrl = Utility.buildUrl(Storage.getPreferenceAsInt(this, Storage.KEY_SERVER_ID, 0));
        String fixUrl = httpUrl.toString();

        if(fixUrl.charAt(fixUrl.length()-1)!= '/'){
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
                    startActivityForResult(takePicture, returnCodeFromCamera);//zero can be replaced with any action code
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
        /*
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(menuItems, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    // from camera
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);//zero can be replaced with any action code
                } else if (item == 1) {
                    // from gallery
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, 1);//one can be replaced with any action code
                } else if (item == 2) {
                    // delete
                    Drawable icon;
                    if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        icon = VectorDrawableCompat.create(getResources(), R.drawable.ic_add_a_photo_black_24dp, getTheme());
                    } else {
                        icon = getResources().getDrawable(R.drawable.ic_add_a_photo_black_24dp, getTheme());
                    }
                    ivUpload1.setImageURI(null);
                    ivUpload1.setImageDrawable(icon);
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
        */
    }

    @OnClick(R.id.ivUpload2)
    public void onClickUpload2() {
        showDialog(ivUpload2, 2, 3);
    }

    @OnClick(R.id.ivUpload3)
    public void onClickUpload3() {
        showDialog(ivUpload2, 4, 5);
    }

    @OnClick(R.id.ivUpload4)
    public void onClickUpload4() {
        showDialog(ivUpload2, 6, 7);
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

        String latitude = "";
        String longitude = "";

        TrnPhoto trnPhoto = null;

        if (targetImage == ivUpload1) {
            trnPhoto = this.realm.where(TrnPhoto.class)
                    .equalTo("ldvNo", ldvNo)
                    .equalTo("contractNo", contractNo)
                    .equalTo("collCode", collectorId)
                    .equalTo("photoId", "picture1")
                    .findFirst()
                    ;

            latitude = trnPhoto.getLatitude();
            longitude = trnPhoto.getLongitude();
        } else if (targetImage == ivUpload2) {
            trnPhoto = this.realm.where(TrnPhoto.class)
                    .equalTo("ldvNo", ldvNo)
                    .equalTo("contractNo", contractNo)
                    .equalTo("collCode", collectorId)
                    .equalTo("photoId", "picture2")
                    .findFirst()
                    ;

            latitude = trnPhoto.getLatitude();
            longitude = trnPhoto.getLongitude();

        } else if (targetImage == ivUpload3) {
            trnPhoto = this.realm.where(TrnPhoto.class)
                    .equalTo("ldvNo", ldvNo)
                    .equalTo("contractNo", contractNo)
                    .equalTo("collCode", collectorId)
                    .equalTo("photoId", "picture3")
                    .findFirst()
                    ;

            latitude = trnPhoto.getLatitude();
            longitude = trnPhoto.getLongitude();

        } else if (targetImage == ivUpload4) {
            trnPhoto = this.realm.where(TrnPhoto.class)
                    .equalTo("ldvNo", ldvNo)
                    .equalTo("contractNo", contractNo)
                    .equalTo("collCode", collectorId)
                    .equalTo("photoId", "picture4")
                    .findFirst()
                    ;

            latitude = trnPhoto.getLatitude();
            longitude = trnPhoto.getLongitude();
        }

        if (trnPhoto == null){
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

        Uri selectedImage = imageReturnedIntent.getData();

        ImageView targetImage = null;
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

        Picasso.with(this)
                .load(selectedImage)
                .into(targetImage);

        targetImage.setTag(selectedImage);

        final ImageView finalTargetImage = targetImage;

        double[] gps = Location.getGPS(this);
        final String latitude = String.valueOf(gps[0]);
        final String longitude = String.valueOf(gps[1]);

        this.realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                if (finalTargetImage == ivUpload1) {
                    TrnPhoto trnPhoto1 = realm.where(TrnPhoto.class)
                            .equalTo("ldvNo", ldvNo)
                            .equalTo("contractNo", contractNo)
                            .equalTo("collCode", collectorId)
                            .equalTo("photoId", "picture1")
                            .findFirst();
                    if (trnPhoto1 == null) {
                        trnPhoto1 = new TrnPhoto();
                        trnPhoto1.setUid(UUID.randomUUID().toString());
                        trnPhoto1.setFilename(finalTargetImage.getTag().toString());
                        trnPhoto1.setCreatedBy(Utility.LAST_UPDATE_BY);
                        trnPhoto1.setCreatedTimestamp(new Date());
                    }
                    trnPhoto1.setOfficeCode(officeCode);
                    trnPhoto1.setContractNo(contractNo);
                    trnPhoto1.setCollCode(collectorId);
                    trnPhoto1.setLdvNo(ldvNo);
                    trnPhoto1.setLatitude(latitude);
                    trnPhoto1.setLongitude(longitude);
                    trnPhoto1.setPhotoId("picture1");
                    trnPhoto1.setLastupdateBy(Utility.LAST_UPDATE_BY);
                    trnPhoto1.setLastupdateTimestamp(new Date());
                    realm.copyToRealmOrUpdate(trnPhoto1);

                } else if (finalTargetImage == ivUpload2) {
                    TrnPhoto trnPhoto2 = realm.where(TrnPhoto.class)
                            .equalTo("ldvNo", ldvNo)
                            .equalTo("contractNo", contractNo)
                            .equalTo("collCode", collectorId)
                            .equalTo("photoId", "picture2")
                            .findFirst();
                    if (trnPhoto2 == null) {
                        trnPhoto2 = new TrnPhoto();
                        trnPhoto2.setUid(UUID.randomUUID().toString());
                        trnPhoto2.setFilename(finalTargetImage.getTag().toString());
                        trnPhoto2.setCreatedBy(Utility.LAST_UPDATE_BY);
                        trnPhoto2.setCreatedTimestamp(new Date());
                    }
                    trnPhoto2.setOfficeCode(officeCode);
                    trnPhoto2.setContractNo(contractNo);
                    trnPhoto2.setCollCode(collectorId);
                    trnPhoto2.setLdvNo(ldvNo);
                    trnPhoto2.setLatitude(latitude);
                    trnPhoto2.setLongitude(longitude);
                    trnPhoto2.setPhotoId("picture2");
                    trnPhoto2.setLastupdateBy(Utility.LAST_UPDATE_BY);
                    trnPhoto2.setLastupdateTimestamp(new Date());
                    realm.copyToRealmOrUpdate(trnPhoto2);

                } else if (finalTargetImage == ivUpload3) {
                    TrnPhoto trnPhoto3 = realm.where(TrnPhoto.class)
                            .equalTo("ldvNo", ldvNo)
                            .equalTo("contractNo", contractNo)
                            .equalTo("collCode", collectorId)
                            .equalTo("photoId", "picture3")
                            .findFirst();
                    if (trnPhoto3 == null) {
                        trnPhoto3 = new TrnPhoto();
                        trnPhoto3.setUid(UUID.randomUUID().toString());
                        trnPhoto3.setFilename(finalTargetImage.getTag().toString());
                        trnPhoto3.setCreatedBy(Utility.LAST_UPDATE_BY);
                        trnPhoto3.setCreatedTimestamp(new Date());
                    }
                    trnPhoto3.setOfficeCode(officeCode);
                    trnPhoto3.setContractNo(contractNo);
                    trnPhoto3.setCollCode(collectorId);
                    trnPhoto3.setLdvNo(ldvNo);
                    trnPhoto3.setLatitude(latitude);
                    trnPhoto3.setLongitude(longitude);
                    trnPhoto3.setPhotoId("picture3");
                    trnPhoto3.setLastupdateBy(Utility.LAST_UPDATE_BY);
                    trnPhoto3.setLastupdateTimestamp(new Date());
                    realm.copyToRealmOrUpdate(trnPhoto3);

                } else if (finalTargetImage == ivUpload4) {
                    TrnPhoto trnPhoto4 = realm.where(TrnPhoto.class)
                            .equalTo("ldvNo", ldvNo)
                            .equalTo("contractNo", contractNo)
                            .equalTo("collCode", collectorId)
                            .equalTo("photoId", "picture4")
                            .findFirst();
                    if (trnPhoto4 == null) {
                        trnPhoto4 = new TrnPhoto();
                        trnPhoto4.setUid(UUID.randomUUID().toString());
                        trnPhoto4.setFilename(finalTargetImage.getTag().toString());
                        trnPhoto4.setCreatedBy(Utility.LAST_UPDATE_BY);
                        trnPhoto4.setCreatedTimestamp(new Date());
                    }
                    trnPhoto4.setOfficeCode(officeCode);
                    trnPhoto4.setContractNo(contractNo);
                    trnPhoto4.setCollCode(collectorId);
                    trnPhoto4.setLdvNo(ldvNo);
                    trnPhoto4.setLatitude(latitude);
                    trnPhoto4.setLongitude(longitude);
                    trnPhoto4.setPhotoId("picture4");
                    trnPhoto4.setLastupdateBy(Utility.LAST_UPDATE_BY);
                    trnPhoto4.setLastupdateTimestamp(new Date());
                    realm.copyToRealmOrUpdate(trnPhoto4);

                }

            }
        });
    }


}
