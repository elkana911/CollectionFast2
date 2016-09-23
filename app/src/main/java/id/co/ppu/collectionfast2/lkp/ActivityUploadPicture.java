package id.co.ppu.collectionfast2.lkp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.co.ppu.collectionfast2.R;
import id.co.ppu.collectionfast2.component.BasicActivity;
import id.co.ppu.collectionfast2.pojo.TrnLDVDetails;
import id.co.ppu.collectionfast2.pojo.UploadPicture;
import io.realm.Realm;

public class ActivityUploadPicture extends BasicActivity {
    public static final String PARAM_CONTRACT_NO = "customer.contractNo";

    private String contractNo = null;

    private final CharSequence[] menuItems = {
            "From Camera", "From Gallery", "Delete Photo"
    };

    @BindView(R.id.ivUpload1)
    ImageView ivUpload1;

    @BindView(R.id.ivUpload2)
    ImageView ivUpload2;

    @BindView(R.id.ivUpload3)
    ImageView ivUpload3;

    @BindView(R.id.ivUpload4)
    ImageView ivUpload4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_picture);

        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            contractNo = extras.getString(PARAM_CONTRACT_NO);
        }

        TrnLDVDetails dtl = this.realm.where(TrnLDVDetails.class).equalTo("contractNo", contractNo).findFirst();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_activity_upload_picture);
            getSupportActionBar().setSubtitle(dtl.getCustName());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        long count = this.realm.where(UploadPicture.class).count();
        UploadPicture uploadPicture = this.realm.where(UploadPicture.class).equalTo("contractNo", this.contractNo).findFirst();

        if (uploadPicture != null && uploadPicture.getPicture4() != null) {
            Uri uri = Uri.parse(uploadPicture.getPicture4());
            Picasso.with(this)
                    .load(uri)
                    .into(ivUpload4);
            ivUpload4.setTag(uri);
        }
    }

    @OnClick(R.id.ivUpload1)
    public void onClickUpload1() {
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
                    startActivityForResult(pickPhoto , 1);//one can be replaced with any action code
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
    }

    @OnClick(R.id.ivUpload2)
    public void onClickUpload2() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(menuItems, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    // from camera
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 2);//zero can be replaced with any action code
                } else if (item == 1) {
                    // from gallery
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , 3);//one can be replaced with any action code
                } else if (item == 2) {
                    // delete
                    Drawable icon;
                    if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        icon = VectorDrawableCompat.create(getResources(), R.drawable.ic_add_a_photo_black_24dp, getTheme());
                    } else {
                        icon = getResources().getDrawable(R.drawable.ic_add_a_photo_black_24dp, getTheme());
                    }
                    ivUpload2.setImageURI(null);
                    ivUpload2.setImageDrawable(icon);
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @OnClick(R.id.ivUpload3)
    public void onClickUpload3() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(menuItems, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    // from camera
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 4);//zero can be replaced with any action code
                } else if (item == 1) {
                    // from gallery
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , 5);//one can be replaced with any action code
                } else if (item == 2) {
                    // delete
                    Drawable icon;
                    if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        icon = VectorDrawableCompat.create(getResources(), R.drawable.ic_add_a_photo_black_24dp, getTheme());
                    } else {
                        icon = getResources().getDrawable(R.drawable.ic_add_a_photo_black_24dp, getTheme());
                    }
                    ivUpload3.setImageURI(null);
                    ivUpload3.setImageDrawable(icon);
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @OnClick(R.id.ivUpload4)
    public void onClickUpload4() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(menuItems, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    // from camera
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 6);//zero can be replaced with any action code
                } else if (item == 1) {
                    // from gallery
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , 7);//one can be replaced with any action code
                } else if (item == 2) {
                    // delete
                    Drawable icon;
                    if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        icon = VectorDrawableCompat.create(getResources(), R.drawable.ic_add_a_photo_black_24dp, getTheme());
                    } else {
                        icon = getResources().getDrawable(R.drawable.ic_add_a_photo_black_24dp, getTheme());
                    }
                    ivUpload4.setImageURI(null);
                    ivUpload4.setImageDrawable(icon);
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @OnClick(R.id.btnUpload)
    public void onClickUpload() {

        if (TextUtils.isEmpty(this.contractNo))
            return;


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 0:
            case 1:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    ivUpload1.setImageURI(selectedImage);
                }
                break;
            case 2:
            case 3:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    ivUpload2.setImageURI(selectedImage);
                }
                break;
            case 4:
            case 5:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    ivUpload3.setImageURI(selectedImage);
                }
                break;
            case 6:
            case 7:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
//                    ivUpload4.setImageURI(selectedImage);
                    Picasso.with(this)
                            .load(selectedImage)
                            .into(ivUpload4);
                    ivUpload4.setTag(selectedImage);

                    this.realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            UploadPicture uploadPic4 = new UploadPicture();
                            uploadPic4.setContractNo(contractNo);
                            uploadPic4.setPicture4(ivUpload4.getTag().toString());
                            realm.copyToRealmOrUpdate(uploadPic4);
                        }
                    });
                }
                break;
        }
    }
}
