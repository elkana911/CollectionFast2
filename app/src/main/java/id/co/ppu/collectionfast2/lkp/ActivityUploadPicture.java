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
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.co.ppu.collectionfast2.R;
import id.co.ppu.collectionfast2.component.BasicActivity;
import id.co.ppu.collectionfast2.listener.OnSuccessError;
import id.co.ppu.collectionfast2.pojo.UploadPicture;
import id.co.ppu.collectionfast2.pojo.UserData;
import id.co.ppu.collectionfast2.pojo.sync.SyncFileUpload;
import id.co.ppu.collectionfast2.pojo.trn.TrnLDVDetails;
import id.co.ppu.collectionfast2.util.NetUtil;
import id.co.ppu.collectionfast2.util.Storage;
import id.co.ppu.collectionfast2.util.Utility;
import io.realm.Realm;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityUploadPicture extends BasicActivity {
    public static final String PARAM_CONTRACT_NO = "customer.contractNo";
    public static final String PARAM_COLLECTOR_ID = "collector.id";
    public static final String PARAM_LDV_NO = "ldvNo";

    private static final String TAG = "upload";

    private String contractNo = null;
    private String collectorId = null;
    private String ldvNo = null;

    private ProgressDialog mProgressDialog = null;

    private final CharSequence[] menuItems = {
            "From Camera", "From Gallery", "Delete Photo"
    };

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

        TrnLDVDetails dtl = this.realm.where(TrnLDVDetails.class).equalTo("contractNo", contractNo).findFirst();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_activity_upload_picture);
            getSupportActionBar().setSubtitle(dtl.getCustName());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        long count = this.realm.where(UploadPicture.class).count();

        UploadPicture uploadPicture = this.realm.where(UploadPicture.class)
                .equalTo("contractNo", this.contractNo)
                .findFirst();

        if (uploadPicture == null)
            return;

        if (uploadPicture.getPicture1() != null) {
            Uri uri = Uri.parse(uploadPicture.getPicture1());
            Picasso.with(this)
                    .load(uri)
                    .into(ivUpload1);
            ivUpload1.setTag(uri);
        }
        if (uploadPicture.getPicture2() != null) {
            Uri uri = Uri.parse(uploadPicture.getPicture2());
            Picasso.with(this)
                    .load(uri)
                    .into(ivUpload2);
            ivUpload2.setTag(uri);
        }
        if (uploadPicture.getPicture3() != null) {
            Uri uri = Uri.parse(uploadPicture.getPicture3());
            Picasso.with(this)
                    .load(uri)
                    .into(ivUpload3);
            ivUpload3.setTag(uri);
        }
        if (uploadPicture.getPicture4() != null) {
            Uri uri = Uri.parse(uploadPicture.getPicture4());
            Picasso.with(this)
                    .load(uri)
                    .into(ivUpload4);
            ivUpload4.setTag(uri);
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
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, returnCodeFromGallery);//one can be replaced with any action code
                } else if (item == 2) {
                    // delete
                    Drawable icon;
                    if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
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
            Toast.makeText(this, picId + " skipped", Toast.LENGTH_SHORT).show();
            if (listener != null) {
                listener.onSkip();
                return;
            }
        }

        UserData userData = (UserData) Storage.getObjPreference(getApplicationContext(), Storage.KEY_USER, UserData.class);

        String officeCode = userData.getUser().get(0).getBranchId();

        UploadPicture uploadPicture = this.realm.where(UploadPicture.class)
                .equalTo("contractNo", contractNo)
                .equalTo("collectorId", collectorId)
                .findFirst();

        String latitude = "";
        String longitude = "";

        if (targetImage == ivUpload1) {
            latitude = uploadPicture.getLat1();
            longitude = uploadPicture.getLong1();
        } else if (targetImage == ivUpload2) {
            latitude = uploadPicture.getLat2();
            longitude = uploadPicture.getLong2();

        } else if (targetImage == ivUpload3) {
            latitude = uploadPicture.getLat3();
            longitude = uploadPicture.getLong3();

        } else if (targetImage == ivUpload4) {
            latitude = uploadPicture.getLat4();
            longitude = uploadPicture.getLong4();
        }

        Uri uri = Uri.parse(tag.toString());
        NetUtil.uploadPicture(this, officeCode, this.collectorId, this.ldvNo, this.contractNo, picId, latitude, longitude, uri, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
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

                    ResponseBody body1 = response.body();

                    try {
                        String s = body1.string();

                        Log.d(TAG, s);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(ActivityUploadPicture.this, "upload " + picId + " failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ActivityUploadPicture.this, t.getMessage(), Toast.LENGTH_SHORT).show();
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

        this.realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                UploadPicture uploadPicture = realm.where(UploadPicture.class)
                        .equalTo("contractNo", contractNo)
                        .equalTo("collectorId", collectorId)
                        .findFirst();
                if (uploadPicture == null) {
                    uploadPicture = new UploadPicture();
                    uploadPicture.setContractNo(contractNo);
                    uploadPicture.setCollectorId(collectorId);
                }

                if (finalTargetImage == ivUpload1) {
                    uploadPicture.setPicture1(finalTargetImage.getTag().toString());
                    uploadPicture.setLat1("0");
                    uploadPicture.setLong1("0");
                } else if (finalTargetImage == ivUpload2) {
                    uploadPicture.setPicture2(finalTargetImage.getTag().toString());
                    uploadPicture.setLat2("0");
                    uploadPicture.setLong2("0");
                } else if (finalTargetImage == ivUpload3) {
                    uploadPicture.setPicture3(finalTargetImage.getTag().toString());
                    uploadPicture.setLat3("0");
                    uploadPicture.setLong3("0");
                } else if (finalTargetImage == ivUpload4) {
                    uploadPicture.setPicture4(finalTargetImage.getTag().toString());
                    uploadPicture.setLat4("0");
                    uploadPicture.setLong4("0");
                }

                realm.copyToRealmOrUpdate(uploadPicture);
            }
        });
    }


}
