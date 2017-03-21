package id.co.ppu.collectionfast2;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.OnLongClick;
import id.co.ppu.collectionfast2.chats.ChatActivity;
import id.co.ppu.collectionfast2.component.CustomTypefaceSpan;
import id.co.ppu.collectionfast2.exceptions.NoConnectionException;
import id.co.ppu.collectionfast2.fragments.FragmentChatActiveContacts;
import id.co.ppu.collectionfast2.fragments.FragmentChatWith;
import id.co.ppu.collectionfast2.fragments.HomeFragment;
import id.co.ppu.collectionfast2.job.SyncJob;
import id.co.ppu.collectionfast2.listener.OnPostRetrieveLKP;
import id.co.ppu.collectionfast2.listener.OnPostRetrieveServerInfo;
import id.co.ppu.collectionfast2.listener.OnSuccessError;
import id.co.ppu.collectionfast2.lkp.ActivityScrollingLKPDetails;
import id.co.ppu.collectionfast2.lkp.FragmentLKPList;
import id.co.ppu.collectionfast2.lkp.FragmentSummaryLKP;
import id.co.ppu.collectionfast2.login.LoginActivity;
import id.co.ppu.collectionfast2.payment.entry.ActivityPaymentEntri;
import id.co.ppu.collectionfast2.poa.ActivityPoA;
import id.co.ppu.collectionfast2.pojo.DisplayTrnContractBuckets;
import id.co.ppu.collectionfast2.pojo.DisplayTrnLDVDetails;
import id.co.ppu.collectionfast2.pojo.LKPData;
import id.co.ppu.collectionfast2.pojo.ServerInfo;
import id.co.ppu.collectionfast2.pojo.UserConfig;
import id.co.ppu.collectionfast2.pojo.UserData;
import id.co.ppu.collectionfast2.pojo.trn.TrnCollPos;
import id.co.ppu.collectionfast2.pojo.trn.TrnFlagTimestamp;
import id.co.ppu.collectionfast2.pojo.trn.TrnLDVDetails;
import id.co.ppu.collectionfast2.pojo.trn.TrnLDVHeader;
import id.co.ppu.collectionfast2.pojo.trn.TrnPhoto;
import id.co.ppu.collectionfast2.pojo.trn.TrnRVB;
import id.co.ppu.collectionfast2.pojo.trn.TrnRVColl;
import id.co.ppu.collectionfast2.rest.ApiInterface;
import id.co.ppu.collectionfast2.rest.ServiceGenerator;
import id.co.ppu.collectionfast2.rest.request.RequestLKPByDate;
import id.co.ppu.collectionfast2.rest.request.RequestSyncLKP;
import id.co.ppu.collectionfast2.rest.request.RequestSyncLocation;
import id.co.ppu.collectionfast2.rest.response.ResponseGetLKP;
import id.co.ppu.collectionfast2.rest.response.ResponseGetMobileConfig;
import id.co.ppu.collectionfast2.rest.response.ResponseSync;
import id.co.ppu.collectionfast2.settings.SettingsActivity;
import id.co.ppu.collectionfast2.sync.SyncBastbj;
import id.co.ppu.collectionfast2.sync.SyncChangeAddr;
import id.co.ppu.collectionfast2.sync.SyncLdvComments;
import id.co.ppu.collectionfast2.sync.SyncLdvDetails;
import id.co.ppu.collectionfast2.sync.SyncLdvHeader;
import id.co.ppu.collectionfast2.sync.SyncPhoto;
import id.co.ppu.collectionfast2.sync.SyncRVColl;
import id.co.ppu.collectionfast2.sync.SyncRepo;
import id.co.ppu.collectionfast2.sync.SyncRvb;
import id.co.ppu.collectionfast2.test.ActivityDeveloper;
import id.co.ppu.collectionfast2.util.DataUtil;
import id.co.ppu.collectionfast2.util.DemoUtil;
import id.co.ppu.collectionfast2.util.NetUtil;
import id.co.ppu.collectionfast2.util.PoAUtil;
import id.co.ppu.collectionfast2.util.RootUtil;
import id.co.ppu.collectionfast2.util.Storage;
import id.co.ppu.collectionfast2.util.UserUtil;
import id.co.ppu.collectionfast2.util.Utility;
import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.RealmObject;
import io.realm.RealmResults;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static id.co.ppu.collectionfast2.location.LocationFused.FASTEST_INTERVAL;
import static id.co.ppu.collectionfast2.location.LocationFused.UPDATE_INTERVAL;

// TODO: tombol sync multi fitur jd lbh simple, bisa utk tarik lkp ataupun sync. pembedanya bisa dr trnldvHeader
public class MainActivity extends ChatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, NavigationView.OnNavigationItemSelectedListener
        , FragmentLKPList.OnFragmentLKPListInteractionListener {

    public static final String SELECTED_NAV_MENU_KEY = "selected_nav_menu_key";
    private static final String TAG = "MainActivity";

    private final CharSequence[] menuItems = {
            "From Camera", "From Gallery", "Delete Photo"
    };

    private boolean viewIsAtHome;
    private Menu menu;

    private String mCurrentProfilePhotoPath;

    private PendingIntent pendingIntent;

//    @BindView(R.id.fab)
//    FloatingActionButton fab;

//    @BindView(R.id.coordinatorLayout)
//    public View coordinatorLayout;
//
//    @BindView(R.id.nav_view)
//    NavigationView navigationView;
//
//    @BindView(R.id.drawer_layout)
//    DrawerLayout drawer;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (DemoUtil.isDemo(this)) {
            promptSnackBar("WARNING! You're currently signed in as DEMO. Make sure you're OFFLINE");
        }

        if (!NetUtil.isConnected(this)
                && DemoUtil.isDemo(this)) {

        } else {
            // handle mobile setup
            final ProgressDialog mProgressDialog = Utility.createAndShowProgressDialog(this, getString(R.string.message_please_wait));

            ApiInterface fastService =
                    ServiceGenerator.createService(ApiInterface.class, Utility.buildUrl(Storage.getPreferenceAsInt(getApplicationContext(), Storage.KEY_SERVER_ID, 0)));

            Call<ResponseGetMobileConfig> call = fastService.getMobileConfig();
            call.enqueue(new Callback<ResponseGetMobileConfig>() {
                @Override
                public void onResponse(Call<ResponseGetMobileConfig> call, Response<ResponseGetMobileConfig> response) {
                    Utility.dismissDialog(mProgressDialog);

                    if (response.isSuccessful()) {
                        final ResponseGetMobileConfig respGetMobileCfg = response.body();

                        if (respGetMobileCfg == null) {
                            // silent
                            return;
                        }

                        if (respGetMobileCfg.getError() != null) {
                            // silent
                        } else {

                            if (respGetMobileCfg.getData() == null) {
                                // silent
                            } else {
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        realm.copyToRealmOrUpdate(respGetMobileCfg.getData());
                                    }
                                });

                                if (isGPSMandatory(realm)) {
                                    if (!id.co.ppu.collectionfast2.location.Location.isLocationDetected(MainActivity.this)) {
                                        showSnackBar(getString(R.string.message_no_gps));
                                        id.co.ppu.collectionfast2.location.Location.pleaseTurnOnGPS(MainActivity.this);
                                    }
                                }
                            }
                        }
                    } else {
                        // silent
                    }
                }

                @Override
                public void onFailure(Call<ResponseGetMobileConfig> call, Throwable t) {
                    Utility.dismissDialog(mProgressDialog);

                }
            });

        }

        String androidId = getAndroidToken();

    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), Utility.FONT_SAMSUNG_BOLD);
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setBackgroundColor(); bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0000ff")));
//        toolbar.setBackgroundColor(Color.parseColor("#28166f"));
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorRadanaBlue));
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        Menu mn = navigationView.getMenu();
        if (mn != null) {
            MenuItem miDeveloper = mn.findItem(R.id.nav_developer);
            if (miDeveloper != null) {
                miDeveloper.setVisible(Utility.DEVELOPER_MODE);
            }

            MenuItem miChats = mn.findItem(R.id.nav_chats);
            if (miChats != null) {
                miChats.setVisible(Utility.DEVELOPER_MODE);
            }

            MenuItem miReset = mn.findItem(R.id.nav_reset);
            if (miReset != null) {
//                miReset.setVisible(Utility.DEVELOPER_MODE);
            }

            MenuItem miSyncRVB = mn.findItem(R.id.nav_getRvb);
            if (miSyncRVB != null) {
                miSyncRVB.setVisible(Utility.DEVELOPER_MODE);
            }

            MenuItem miManualSync = mn.findItem(R.id.nav_manualSync);
            if (miManualSync != null) {
                miManualSync.setVisible(Utility.DEVELOPER_MODE);
            }
            // set custom font
            for (int i = 0; i < mn.size(); i++) {
                MenuItem mi = mn.getItem(i);

                //for aapplying a font to subMenu ...
                SubMenu subMenu = mi.getSubMenu();
                if (subMenu != null && subMenu.size() > 0) {
                    for (int j = 0; j < subMenu.size(); j++) {
                        MenuItem subMenuItem = subMenu.getItem(j);
                        applyFontToMenuItem(subMenuItem);
                    }
                }

                //the method we have create in activity
                applyFontToMenuItem(mi);
            }
        }

        View v = navigationView.getHeaderView(0);

        currentUser = (UserData) Storage.getObjPreference(getApplicationContext(), Storage.KEY_USER, UserData.class);

        TextView tvProfileName = ButterKnife.findById(v, R.id.tvProfileName);
        tvProfileName.setText(currentUser.getFullName());
        Typeface font = Typeface.createFromAsset(getAssets(), Utility.FONT_SAMSUNG);
        tvProfileName.setTypeface(font);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(currentUser.getFullName());
        }

        TextView tvProfileEmail = ButterKnife.findById(v, R.id.tvProfileEmail);
        tvProfileEmail.setText(currentUser.getEmailAddr());

        final ImageView imageView = ButterKnife.findById(v, R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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
                                    photoFile = UserUtil.createTempFileForProfilePic(MainActivity.this);  // /storage/emulated/0/Android/data/com.example.eric.cameranougat/files/Pictures/JPEG_20170318_113520_474001886.jpg
                                } catch (IOException ex) {
                                    // Error occurred while creating the File
                                    return;
                                }
                                // Continue only if the File was successfully created
                                if (photoFile != null) {

                                    mCurrentProfilePhotoPath = photoFile.getAbsolutePath();

                                    Uri photoURI = FileProvider.getUriForFile(MainActivity.this,
                                            BuildConfig.APPLICATION_ID + ".provider",
                                            photoFile);
                                    takePicture.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                    startActivityForResult(takePicture, 44);
                                }

                            }

                        } else if (item == 1) {
                            // from gallery
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(pickPhoto, 55);//one can be replaced with any action code
                        } else if (item == 2) {
                            // delete
                            Drawable icon;
                            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                                icon = VectorDrawableCompat.create(getResources(), R.drawable.ic_add_a_photo_black_24dp, getTheme());
                            } else {
                                icon = getResources().getDrawable(R.drawable.ic_add_a_photo_black_24dp, getTheme());
                            }

                            Drawable drawable = AppCompatDrawableManager.get().getDrawable(MainActivity.this, R.drawable.ic_account_circle_black_24dp);
                            imageView.setImageDrawable(drawable);
                        }
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();

            }
        });

        // is collector photo available ?
        boolean photoNotAvail = true;
        if (photoNotAvail) {
            Drawable drawable = AppCompatDrawableManager.get().getDrawable(this, R.drawable.ic_account_circle_black_24dp);
            imageView.setImageDrawable(drawable);
        }

        if (savedInstanceState == null) {
            displayView(R.id.nav_home);
        } else {
            // recover state
            mSelectedNavMenuIndex = savedInstanceState.getInt(SELECTED_NAV_MENU_KEY);
            displayView(mSelectedNavMenuIndex);
        }

        boolean b = DataUtil.isMasterDataDownloaded(this, this.realm);

        // re-get
        ServerInfo si = this.realm.where(ServerInfo.class).findFirst();
        if (si == null || si.getServerDate() == null) {
            try {
                DataUtil.retrieveServerInfo(currentUser.getUserId(), this.realm, this, new OnPostRetrieveServerInfo() {
                    @Override
                    public void onSuccess(final ServerInfo serverInfo) {
                        realm.executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                // create config
                                UserConfig userConfig = realm.where(UserConfig.class).findFirst();
                                if (userConfig == null) {
                                    userConfig = new UserConfig();
                                    userConfig.setUid(Utility.generateUUID());
                                    userConfig.setKodeTarikRunningNumber(0L);
                                    userConfig.setKodeRVCollRunningNumber(0L);

                                    userConfig.setServerDate(serverInfo.getServerDate());

                                    try {
                                        userConfig.setDeviceId(Utility.getDeviceId(MainActivity.this));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                } else {
                                    userConfig.setServerDate(serverInfo.getServerDate());
                                }

                                userConfig.setLastLogin(new Date());

                                realm.copyToRealmOrUpdate(userConfig);

                            }
                        }, new Realm.Transaction.OnSuccess() {
                            @Override
                            public void onSuccess() {
                                // check dates
                                UserConfig userConfig = realm.where(UserConfig.class).findFirst();

                                if (!Utility.isSameDay(userConfig.getLastLogin(), serverInfo.getServerDate())) {
                                    showSnackBar(getString(R.string.warning_close_batch));
                                }

                                if (userConfig.getPhotoProfileUri() != null) {
                                    Uri uri;
                                    uri = Uri.parse(userConfig.getPhotoProfileUri());

                                    imageView.setImageURI(uri);
                                }

                            }
                        });

                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Utility.showDialog(MainActivity.this, "Error", t.getMessage());
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            final Date serverDate = si.getServerDate();

            this.realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    // create config
                    UserConfig userConfig = realm.where(UserConfig.class).findFirst();
                    if (userConfig == null) {
                        userConfig = new UserConfig();
                        userConfig.setUid(Utility.generateUUID());
                        userConfig.setKodeTarikRunningNumber(0L);
                        userConfig.setKodeRVCollRunningNumber(0L);

                        userConfig.setServerDate(serverDate);

                        try {
                            userConfig.setDeviceId(Utility.getDeviceId(MainActivity.this));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    userConfig.setLastLogin(new Date());

                    // fix serverDate should not before lastLogin
                    if (serverDate.before(userConfig.getLastLogin())) {
                        userConfig.setServerDate(new Date());
                    }

                    realm.copyToRealmOrUpdate(userConfig);

                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    // check dates
                    UserConfig userConfig = realm.where(UserConfig.class).findFirst();

                    if (DataUtil.isLDVHeaderValid(realm, currentUser.getUserId()) != null) {
                        showSnackBar(getString(R.string.warning_close_batch));
                    }

//                    if (!Utility.isSameDay(userConfig.getLastLogin(), serverDate)) {
//                        Snackbar.make(coordinatorLayout, "Server date changed. Please Close Batch.", Snackbar.LENGTH_SHORT).show();
//                    }

                    if (userConfig.getPhotoProfileUri() != null) {
                        Uri uri;
                        uri = Uri.parse(userConfig.getPhotoProfileUri());

                        imageView.setImageURI(uri);
                    }

                }
            });
        }
// Create the location client to start receiving updates
        try {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Storage.savePreference(getApplicationContext(), Storage.KEY_LOGIN_DATE, new Date().toString());
    }

    @Override
    protected void onStart() {
        super.onStart();
        stopJob();
        startJob();

        try {
            mGoogleApiClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onStop() {

        // Disconnecting the client invalidates it.
        try {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // only stop if it's connected, otherwise we crash
        if (mGoogleApiClient != null) {
            try {
                mGoogleApiClient.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        stopJob();

        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_NAV_MENU_KEY, mSelectedNavMenuIndex);

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
//            super.onBackPressed();

            int x = getSupportFragmentManager().getBackStackEntryCount();

            if (!viewIsAtHome) {
                if (x > 0) {
                    getSupportFragmentManager().popBackStackImmediate();
                } else
                    displayView(R.id.nav_home);
            } else {
                //display logout dialog
                logout();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here

                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();

                return true;

            }

            @Override
            public boolean onQueryTextChange(String newText) {

                FragmentLKPList frag = (FragmentLKPList)
                        getSupportFragmentManager().findFragmentById(R.id.content_frame);

                if (frag != null) {
                    frag.search(newText);
                }

                return true;
            }
        });

        Drawable drawableTaskLog = menu.findItem(R.id.action_clear_chats).getIcon();
        drawableTaskLog = DrawableCompat.wrap(drawableTaskLog);
        DrawableCompat.setTint(drawableTaskLog, ContextCompat.getColor(this, android.R.color.white));
        menu.findItem(R.id.action_clear_chats).setIcon(drawableTaskLog);

        return true;
    }

    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {

        Fragment frag = getSupportFragmentManager().findFragmentById(R.id.content_frame);

        MenuItem chatClearItem = menu.findItem(R.id.action_clear_chats);
        chatClearItem.setVisible(frag instanceof FragmentChatWith);

        return super.onPrepareOptionsPanel(view, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivityForResult(new Intent(this, SettingsActivity.class), 999);
            return true;
        } else if (id == R.id.action_clear_chats) {
            Fragment frag = getSupportFragmentManager().findFragmentById(R.id.content_frame);
            if (frag instanceof FragmentChatWith) {
                ((FragmentChatWith) frag).clearChat();
            }

        } else if (id == R.id.action_reset) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Reset Data");
            alertDialogBuilder.setMessage("This will Logout Application.\nAre you sure?");
            //null should be your on click listener
            alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    View promptsView = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_pwd, null);
                    final EditText input = ButterKnife.findById(promptsView, R.id.password);

                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Type Your Password")
                            .setView(promptsView)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String value = input.getText().toString();

                                    if (!value.equals(currentUser.getUserPwd())) {
                                        Snackbar.make(coordinatorLayout, "Invalid password !", Snackbar.LENGTH_LONG).show();
                                        return;
                                    }

                                    resetData();
                                    backToLoginScreen();

                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .show()
                    ;

                }
            });

            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            alertDialogBuilder.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_paymentEntry) {
            openPaymentEntry();

            return false;
        } else if (id == R.id.nav_developer) {

            Intent i = new Intent(this, ActivityDeveloper.class);
            startActivity(i);

            return false;
        } else if (id == R.id.nav_getRvb) {

            try {
                NetUtil.refreshRVBFromServer(this);
            } catch (Exception e) {
                e.printStackTrace();

                if (currentUser != null)
                    NetUtil.syncLogError(MainActivity.this, realm, currentUser.getUserId(), "refreshRVBFromServer", e.getMessage(), null);
            }

            return false;
        }/* else if (id == R.id.nav_chats) {

            Intent i = new Intent(this, ActivityChats.class);
            startActivity(i);

            return false;
        }*/ else if (id == R.id.nav_reset) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Reset Data");
            alertDialogBuilder.setMessage("This will Logout Application.\nAre you sure?");
            //null should be your on click listener
            alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    resetData();
                    backToLoginScreen();

                }
            });

            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            alertDialogBuilder.show();


            return false;
        } else if (id == R.id.nav_logout) {
            logout();

            return false;
        } else if (id == R.id.nav_closeBatch) {
            drawer.closeDrawers();

            try {
                attemptCloseBatch();
            } catch (Exception e) {
                e.printStackTrace();

                if (currentUser != null)
                    NetUtil.syncLogError(MainActivity.this, realm, currentUser.getUserId(), "CloseBatch", e.getMessage(), null);
            }

            return false;
        } else if (id == R.id.nav_manualSync) {

            drawer.closeDrawers();

            showSnackBar("Nothing to do");

//            testuploadPoAOneByOne();
                    /*
            syncTransaction(true, new OnSuccessError() {
                @Override
                public void onSuccess(String msg) {
                    Fragment frag = getSupportFragmentManager().findFragmentById(R.id.content_frame);

                    if (frag != null && frag instanceof FragmentLKPList) {
                        ((FragmentLKPList) frag).loadCurrentLKP(); // masih terkadang ga mau update
//                    ((FragmentLKPList) frag).refresh(); ga mau update
//                    frag.refresh();
                    }
                }

                @Override
                public void onFailure(Throwable throwable) {

                }

                @Override
                public void onSkip() {

                }
            });
*/
            return false;
        } else if (id == R.id.nav_clearSyncTables) {
            clearSyncTables();

            return false;
        } else
            displayView(id);

        return true;
    }

    @Override
    protected void displayView(int viewId) {

        if (mSelectedNavMenuIndex == viewId) {
            return;
        }

        Fragment fragment = null;
        String title = null;
        viewIsAtHome = false;

        if (this.menu != null) {
            // action_search is now disabled due to the limitation of realmsearchview. filterkey cant search by custName if data sorted by seqNo
//            MenuItem item = this.menu.findItem(R.id.action_search);
//            item.setVisible(viewId == R.id.nav_loa);
        }

        navigationView.setCheckedItem(viewId);

        title = getString(R.string.app_name);


        fab.setImageDrawable(AppCompatDrawableManager.get().getDrawable(MainActivity.this, R.drawable.ic_assignment_black_24dp));

        if (viewId == R.id.nav_home) {

            fragment = new HomeFragment();

            viewIsAtHome = true;

//            fab.show();
        } else if (viewId == R.id.nav_loa) {
            fab.setImageDrawable(AppCompatDrawableManager.get().getDrawable(MainActivity.this, R.drawable.ic_sync_black_24dp));

            fragment = new FragmentLKPList();

            Bundle bundle = new Bundle();
            bundle.putString(FragmentLKPList.ARG_PARAM1, currentUser.getUserId());
            fragment.setArguments(bundle);

            title = "LKP List";
        } else if (viewId == R.id.nav_summaryLKP) {
            fragment = new FragmentSummaryLKP();

            Bundle bundle = new Bundle();
            bundle.putString(FragmentSummaryLKP.ARG_PARAM1, currentUser.getUserId());
//            bundle.putString(FragmentLKPList.ARG_PARAM1, currentUser.getSecUser().get(0).getUserName());
            fragment.setArguments(bundle);

            title = "Summary LKP";
        } else if (viewId == R.id.nav_chats) {
            fab.setImageDrawable(AppCompatDrawableManager.get().getDrawable(MainActivity.this, R.drawable.ic_send_black_24dp));

            fragment = new FragmentChatActiveContacts();

            Bundle bundle = new Bundle();
            bundle.putString(FragmentChatActiveContacts.PARAM_USERCODE, currentUser.getUserId());
//            bundle.putString(FragmentLKPList.ARG_PARAM1, currentUser.getSecUser().get(0).getUserName());
            fragment.setArguments(bundle);

            title = "Chats";

        }

        /* else if (viewId == R.id.nav_paymentEntry) {
            title = "Payment Entry";

        } else if (viewId == R.id.nav_customerProgram) {
            title = "Customer Program";

        } else if (viewId == R.id.nav_marketingPromo) {
            title = "Marketing Promo";
        } else if (viewId == R.id.nav_nearlyPayment) {
            title = "Nearly Payment";
        } else if (viewId == R.id.nav_radanaOffice) {
            title = "Radana Office";
        }*/

        if (viewId != R.id.nav_home) {
//            fab.hide();
        }

        mSelectedNavMenuIndex = viewId;

        if (fragment != null) {

            int x = getSupportFragmentManager().getBackStackEntryCount();

            if (x > 0)
                getSupportFragmentManager().popBackStackImmediate();

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//            ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            ft.replace(R.id.content_frame, fragment);

//            if (viewId != R.id.nav_home)
//                ft.addToBackStack(title);

            ft.commit();
        }


        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
            getSupportActionBar().setSubtitle(currentUser.getFullName());
            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        drawer.closeDrawer(GravityCompat.START);

    }

    private void backToLoginScreen() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStackImmediate();
        }

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.putExtra("finish", true); // if you are checking for this in your other Activities
        if (Build.VERSION.SDK_INT >= 11) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }

        // flag as clean logout
        Storage.savePreference(getApplicationContext(), Storage.KEY_LOGIN_DATE, null);
        Storage.savePreference(getApplicationContext(), Storage.KEY_LOGOUT_DATE, new Date().toString());

        NetUtil.chatLogOff(this, getCurrentUserId(), null);

        startActivity(intent);
//                moveTaskToBack(true);
        finish();

    }

    private void logout() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Log Out");
        alertDialogBuilder.setMessage(getString(R.string.prompt_quit));
        //null should be your on click listener
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO: clear cookie
                Storage.savePreferenceAsBoolean(getApplicationContext(), "isLKPInquiry", false);

                backToLoginScreen();
            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialogBuilder.show();
    }

    @OnLongClick(R.id.fab)
    public boolean onFabLongClick(View view) {
        Fragment frag = getSupportFragmentManager().findFragmentById(R.id.content_frame);

        if (frag != null && frag instanceof FragmentLKPList) {
            if (!anyDataToSync()) {

                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Force refresh")
                        .setMessage("Are you sure ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                clearLKPTables();
                                clearSyncTables();

                                attemptGetLKP(currentUser.getUserId(), getServerDateFromDB(realm), false, null);
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        }

        return true;
    }


    private void getLKPFromServer(final String collectorCode, Date lkpDate, final String createdBy, final OnPostRetrieveLKP listener) {

        // tarik LKP
        RequestLKPByDate requestLKP = new RequestLKPByDate();
        requestLKP.setCollectorCode(collectorCode);
        requestLKP.setYyyyMMdd(Utility.convertDateToString(lkpDate, "yyyyMMdd"));

        fillRequest(Utility.ACTION_GET_LKP, requestLKP);

        if (!NetUtil.isConnected(this)
                && DemoUtil.isDemo(this)) {

            final LKPData lkpData = DemoUtil.buildLKP(new Date(), currentUser.getUserId(), currentUser.getBranchId(), createdBy);
            currentLDVNo = lkpData.getHeader().getLdvNo();

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm bgRealm) {
                    DataUtil.saveLKPToDB(bgRealm, collectorCode, createdBy, lkpData);
                }
            });

            if (listener != null)
                listener.onSuccess();

            return;
        }

        final ProgressDialog mProgressDialog = Utility.createAndShowProgressDialog(this, "Getting your LKP from server.\nPlease wait...");

        ApiInterface fastService =
                ServiceGenerator.createService(ApiInterface.class, Utility.buildUrl(Storage.getPreferenceAsInt(getApplicationContext(), Storage.KEY_SERVER_ID, 0)));

        Call<ResponseGetLKP> call = fastService.getLKPByDate(requestLKP);
        call.enqueue(new Callback<ResponseGetLKP>() {
            @Override
            public void onResponse(Call<ResponseGetLKP> call, Response<ResponseGetLKP> response) {

                Utility.dismissDialog(mProgressDialog);

                if (response.isSuccessful()) {
                    final ResponseGetLKP respGetLKP = response.body();

                    if (respGetLKP == null) {
                        Utility.showDialog(MainActivity.this, "No LKP found", getString(R.string.error_lkp_empty));
                        return;
                    }

                    if (respGetLKP.getError() != null) {
                        Utility.showDialog(MainActivity.this, "Error (" + respGetLKP.getError().getErrorCode() + ")", respGetLKP.getError().getErrorDesc());
                    } else {

                        if (respGetLKP.getData().getDetails() == null) {

                        } else {
                            // save db here
                            RealmAsyncTask realmAsyncTask = realm.executeTransactionAsync(new Realm.Transaction() {
                                @Override
                                public void execute(Realm bgRealm) {

                                    currentLDVNo = respGetLKP.getData().getHeader().getLdvNo();

                                    DataUtil.saveLKPToDB(bgRealm, collectorCode, createdBy, respGetLKP.getData());

                                }
                            }, new Realm.Transaction.OnSuccess() {
                                @Override
                                public void onSuccess() {
                                    if (listener != null)
                                        listener.onSuccess();

                                }
                            }, new Realm.Transaction.OnError() {
                                @Override
                                public void onError(Throwable error) {
                                    // Transaction failed and was automatically canceled.
                                    Toast.makeText(MainActivity.this, "Error while getting LKP", Toast.LENGTH_LONG).show();
                                    error.printStackTrace();

                                    if (listener != null)
                                        listener.onFailure();
                                }
                            });

                        }
                    }
                } else {
                    int statusCode = response.code();

                    // handle request errors yourself
                    ResponseBody errorBody = response.errorBody();

                    try {
                        Utility.showDialog(MainActivity.this, "Server Problem (" + statusCode + ")", errorBody.string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (listener != null)
                        listener.onFailure();

                }
            }

            @Override
            public void onFailure(Call<ResponseGetLKP> call, Throwable t) {

                Log.e("eric.onFailure", t.getMessage(), t);

                if (listener != null)
                    listener.onFailure();

                Utility.dismissDialog(mProgressDialog);

                Utility.showDialog(MainActivity.this, "Server Problem", t.getMessage());
            }
        });


    }

    public void attemptGetLKP(final String collectorCode, final Date lkpDate, boolean useCache, final OnPostRetrieveLKP listener) {
        if (currentUser == null) {
            return;
        }

        Date serverDate = getServerDateFromDB(this.realm);

        final String createdBy = "JOB" + Utility.convertDateToString(lkpDate, "yyyyMMdd");
        // load cache
        long count = this.realm.where(TrnLDVHeader.class).equalTo("collCode", collectorCode)
                .equalTo("createdBy", createdBy)
                .count();

        if (useCache) {
            if (count > 0 && lkpDate.before(serverDate)) {
                if (listener != null)
                    listener.onLoadFromLocal();
                return;
            }
        }

        // should check apakah ada data lkp yg masih kecantol di hari kemarin
        // must sync first
        syncTransaction(false, new OnSuccessError() {
            @Override
            public void onSuccess(String msg) {
                getLKPFromServer(collectorCode, lkpDate, createdBy, listener);
            }

            @Override
            public void onFailure(Throwable throwable) {
            }

            @Override
            public void onSkip() {
                getLKPFromServer(collectorCode, lkpDate, createdBy, listener);
            }
        });

    }

    @Override
    public void onLKPSelected(DisplayTrnLDVDetails detail) {

        if (DataUtil.isLDVHeaderValid(realm, currentUser.getUserId()) != null) {
            // di method ini terkadang penggunaan getString bisa ga ketemu lho, mungkin krn listener bisa beda context
            showSnackBar(getString(R.string.warning_close_batch));
            return;
        }
/*
        // cek lagi apa headernya ternyata closed
        if (DataUtil.isLDVHeaderClosed(realm, currentUser.getUserId(), detail.getLkpDate())) {
            showSnackBar("Sorry, this LKP already closed.");
            return;
        }
*/
        if (detail instanceof RealmObject) {
            DisplayTrnLDVDetails dtl = this.realm.copyFromRealm(detail);

            // always call POA if not synced / edited
            if (dtl.getWorkStatus().equals("A")) {
                Intent i = new Intent((this), ActivityPoA.class);

                String json = new Gson().toJson(dtl);

                i.putExtra(ActivityPoA.PARAM_LKP_DETAIL, json);
                i.putExtra(ActivityPoA.PARAM_COLLECTOR_ID, dtl.getCollId());
                i.putExtra(ActivityPoA.PARAM_CONTRACT_NO, dtl.getContractNo());
                i.putExtra(ActivityPoA.PARAM_CUSTOMER_NAME, dtl.getCustName());

                String alamat = null;
                if (detail.getAddress() != null) {
                    alamat = "Kel/Kec: " +detail.getAddress().getCollKel() + "/" + detail
                    .getAddress().getCollKec();
                }

                i.putExtra(ActivityPoA.PARAM_CUSTOMER_ADDR, alamat);
                i.putExtra(ActivityPoA.PARAM_LDV_NO, dtl.getLdvNo());

                startActivityForResult(i, 66);
            } else {
                showLKPDetail(dtl);
            }


//            showLKPDetail(dtl);
            /*
            Intent i = new Intent(this, ActivityScrollingLKPDetails.class);
            i.putExtra(ActivityScrollingLKPDetails.PARAM_CONTRACT_NO, dtl.getContractNo());
            i.putExtra(ActivityScrollingLKPDetails.PARAM_LDV_NO, dtl.getLdvNo());
            i.putExtra(ActivityScrollingLKPDetails.PARAM_COLLECTOR_ID, dtl.getCollId());
            i.putExtra(ActivityScrollingLKPDetails.PARAM_LKP_DATE, dtl.getLkpDate().getTime());
            i.putExtra(ActivityScrollingLKPDetails.PARAM_WORKSTATUS, dtl.getWorkStatus());

            Date serverDate = this.realm.where(ServerInfo.class).findFirst().getServerDate();
            boolean isLKPInquiry = !detail.getCreatedBy().equals("JOB" + Utility.convertDateToString(serverDate, "yyyyMMdd"));

            i.putExtra(ActivityScrollingLKPDetails.PARAM_IS_LKP_INQUIRY, isLKPInquiry);

            startActivity(i);
            */
        }
    }

    @Override
    public void onLKPCancelSync(DisplayTrnLDVDetails detail) {

        if (DataUtil.isLDVHeaderValid(realm, currentUser.getUserId()) != null) {
            showSnackBar(getString(R.string.warning_close_batch));
            return;
        }

        if (detail instanceof RealmObject) {
            final DisplayTrnLDVDetails dtl = this.realm.copyFromRealm(detail);

            StringBuffer sb = new StringBuffer("Are you sure to cancel ");
            sb.append(dtl.getCustName()).append(" ?\n");

            if (dtl.getAddress() != null)
                sb.append("[").append(dtl.getAddress().getCollKel())
                        .append("/").append(dtl.getAddress().getCollKec())
                        .append("]");

            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Cancel Sync")
                    .setMessage(sb.toString())
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cancelSync(realm, dtl.getLdvNo(), dtl.getContractNo());

                            Fragment frag = getSupportFragmentManager().findFragmentById(R.id.content_frame);

                            if (frag != null && frag instanceof FragmentLKPList) {
                                ((FragmentLKPList) frag).refresh();
                            }

                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    public boolean cancelSync(Realm realm, final String ldvNo, final String contractNo) {

        // hanya cancel yg statusnya V
        TrnLDVDetails ada = realm.where(TrnLDVDetails.class)
                .equalTo("workStatus", "V")
                .equalTo("pk.ldvNo", ldvNo)
                .equalTo("contractNo", contractNo)
                .findFirst();

        if (ada == null) {
            return false;
        }


        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // hapus VISIT RESULT
                boolean b = getLDVComments(realm, ldvNo, contractNo)
                        .findAll().deleteAllFromRealm();

                // hapus PAYMENT
                // sebelum hapus rvcoll, restore dulu rvb
                TrnRVColl trnRVColl = getRVColl(realm, contractNo)
                        .findFirst();

                if (trnRVColl != null) {
                    String lastRvbNo = trnRVColl.getPk().getRbvNo();

                    // reopen voucher
                    TrnRVB trnRVB = realm.where(TrnRVB.class)
                            .equalTo("rvbNo", lastRvbNo)
                            .findFirst();
                    trnRVB.setRvbStatus("OP");
                    trnRVB.setLastupdateBy(Utility.LAST_UPDATE_BY);
                    trnRVB.setLastupdateTimestamp(new Date());
                    realm.copyToRealmOrUpdate(trnRVB);

                    trnRVColl.deleteFromRealm();
                }

                // hapus REPO
                b = getRepo(realm, contractNo)
                        .findAll().deleteAllFromRealm();

                // update/restore ldv details
                RealmResults<TrnLDVDetails> trnLDVDetailses = realm.where(TrnLDVDetails.class)
                        .equalTo("pk.ldvNo", ldvNo)
                        .equalTo("contractNo", contractNo)
                        .equalTo("lastupdateBy", Utility.LAST_UPDATE_BY)
//                        .equalTo("createdBy", createdBy)
                        .findAll();

                if (trnLDVDetailses.size() > 1) {
                    throw new RuntimeException("Duplicate data LDVDetail found");
                }
                TrnLDVDetails trnLDVDetails = realm.copyFromRealm(trnLDVDetailses.get(0));
                b = trnLDVDetailses.deleteAllFromRealm();

                trnLDVDetails.setLdvFlag("NEW");
                trnLDVDetails.setWorkStatus("A");
                trnLDVDetails.setFlagToEmrafin("N");
                trnLDVDetails.setLastupdateBy(Utility.LAST_UPDATE_BY);
                trnLDVDetails.setLastupdateTimestamp(new Date());
                realm.copyToRealm(trnLDVDetails);

                // update the display
                DisplayTrnLDVDetails displayTrnLDVDetails = realm.where(DisplayTrnLDVDetails.class)
                        .equalTo("ldvNo", ldvNo)
                        .equalTo("contractNo", contractNo)
                        .findFirst();
                displayTrnLDVDetails.setWorkStatus("A");
                realm.copyToRealm(displayTrnLDVDetails);


                // TODO: harusnya hapus foto juga, sementara di lokal dulu. photo yg terlanjur upload ke server biarin aja
                b = realm.where(TrnPhoto.class)
                        .equalTo("collCode", currentUser.getUserId())
                        .equalTo("contractNo", contractNo)
                        .equalTo("lastupdateBy", Utility.LAST_UPDATE_BY)
//                        .equalTo("createdBy", createdBy)
                        .findAll().deleteAllFromRealm();


            }
        });

        PoAUtil.cancel(realm, currentUser.getUserId(), ldvNo, contractNo);


        // must sync (not async) to avoid process sequence
        /*
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // hapus VISIT RESULT
                boolean b = getLDVComments(realm, ldvNo, contractNo)
                        .findAll().deleteAllFromRealm();

                // hapus PAYMENT
                // sebelum hapus rvcoll, restore dulu rvb
                TrnRVColl trnRVColl = getRVColl(realm, contractNo)
                        .findFirst();

                if (trnRVColl != null) {
                    String lastRvbNo = trnRVColl.getPk().getRbvNo();

                    // reopen voucher
                    TrnRVB trnRVB = realm.where(TrnRVB.class)
                            .equalTo("rvbNo", lastRvbNo)
                            .findFirst();
                    trnRVB.setRvbStatus("OP");
                    trnRVB.setLastupdateBy(Utility.LAST_UPDATE_BY);
                    trnRVB.setLastupdateTimestamp(new Date());
                    realm.copyToRealmOrUpdate(trnRVB);

                    trnRVColl.deleteFromRealm();
                }

                // hapus REPO
                b = getRepo(realm, contractNo)
                        .findAll().deleteAllFromRealm();

                // update/restore ldv details
                RealmResults<TrnLDVDetails> trnLDVDetailses = realm.where(TrnLDVDetails.class)
                        .equalTo("pk.ldvNo", ldvNo)
                        .equalTo("contractNo", contractNo)
                        .equalTo("lastupdateBy", Utility.LAST_UPDATE_BY)
//                        .equalTo("createdBy", createdBy)
                        .findAll();

                if (trnLDVDetailses.size() > 1) {
                    throw new RuntimeException("Duplicate data LDVDetail found");
                }
                TrnLDVDetails trnLDVDetails = realm.copyFromRealm(trnLDVDetailses.get(0));
                b = trnLDVDetailses.deleteAllFromRealm();

                trnLDVDetails.setLdvFlag("NEW");
                trnLDVDetails.setWorkStatus("A");
                trnLDVDetails.setFlagToEmrafin("N");
                trnLDVDetails.setLastupdateBy(Utility.LAST_UPDATE_BY);
                trnLDVDetails.setLastupdateTimestamp(new Date());
                realm.copyToRealm(trnLDVDetails);

                // update the display
                DisplayTrnLDVDetails displayTrnLDVDetails = realm.where(DisplayTrnLDVDetails.class)
                        .equalTo("ldvNo", ldvNo)
                        .equalTo("contractNo", contractNo)
                        .findFirst();
                displayTrnLDVDetails.setWorkStatus("A");
                realm.copyToRealm(displayTrnLDVDetails);


                // TODO: harusnya hapus foto juga, sementara di lokal dulu. photo yg terlanjur upload ke server biarin aja
                b = realm.where(TrnPhoto.class)
                        .equalTo("collCode", currentUser.getUserId())
                        .equalTo("contractNo", contractNo)
                        .equalTo("lastupdateBy", Utility.LAST_UPDATE_BY)
//                        .equalTo("createdBy", createdBy)
                        .findAll().deleteAllFromRealm();
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Fragment frag = getSupportFragmentManager().findFragmentById(R.id.content_frame);

                if (frag != null && frag instanceof FragmentLKPList) {
                    ((FragmentLKPList) frag).refresh();
                }

            }
        });
        */

        return true;
    }

    @Override
    public void onLKPInquiry(final String collectorCode, final Date lkpDate) {

        final Fragment frag = getSupportFragmentManager().findFragmentById(R.id.content_frame);

        if (frag != null && frag instanceof FragmentLKPList) {
            attemptGetLKP(collectorCode, lkpDate, false, new OnPostRetrieveLKP() {
                @Override
                public void onLoadFromLocal() {
                    currentLDVNo = ((FragmentLKPList) frag).loadLKP(collectorCode, lkpDate);
                }

                @Override
                public void onSuccess() {
                    ((FragmentLKPList) frag).loadLKP(collectorCode, lkpDate);
                }

                @Override
                public void onFailure() {

                }

            });
        }

    }

    @Override
    public boolean isAnyDataToSync() {
        return anyDataToSync();
    }

    public void openPaymentEntry() {

        if (DataUtil.isLDVHeaderValid(realm, currentUser.getUserId()) != null) {
            showSnackBar(getString(R.string.warning_close_batch));
            return;
        }

        String collectorId = currentUser.getUserId();

        Date serverDate = null;
        try {
            serverDate = this.realm.where(ServerInfo.class).findFirst().getServerDate();
        } catch (Exception e) {
            e.printStackTrace();
            serverDate = new Date();
        }
        String createdBy = "JOB" + Utility.convertDateToString(serverDate, "yyyyMMdd");

        TrnLDVHeader trnLDVHeader = realm.where(TrnLDVHeader.class)
                .equalTo("collCode", collectorId)
                .equalTo("createdBy", createdBy)
                .findFirst();

        if (trnLDVHeader == null) {
            Utility.showDialog(MainActivity.this, "No LKP Data", "Please Get LKP first.");
            return;
        }

        long count = realm.where(DisplayTrnContractBuckets.class).count();

        if (count < 1) {
            Utility.showDialog(MainActivity.this, "No Data", "No Contracts found !");
            return;
        }

        if (isGPSMandatory(this.realm)) {
            if (!id.co.ppu.collectionfast2.location.Location.isLocationDetected(this)) {
                showSnackBar(getString(R.string.message_no_gps));
                id.co.ppu.collectionfast2.location.Location.pleaseTurnOnGPS(this);
            }
        }

        /* propose cancel, dipindah di activitypaymententri saat user pilih kontrak
        Intent i = new Intent((this), ActivityPoA.class);

        i.putExtra(ActivityPoA.PARAM_COLLECTOR_ID, collectorId);
        i.putExtra(ActivityPoA.PARAM_LDV_NO, trnLDVHeader.getLdvNo());

        startActivityForResult(i, 67);
        */

        Intent i = new Intent(this, ActivityPaymentEntri.class);
        // DO NOT SEND ANY PARAMs !
        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case 999:
                /*
                if (data != null && data.getExtras().containsKey("command")) {
                    String value = data.getStringExtra("command");

                    if (!TextUtils.isEmpty(value)) {
                        if (value.equalsIgnoreCase("logout")) {
                            backToLoginScreen();
                        }
                    }
                }
                */
                break;
            case 44:
                // Show the thumbnail on ImageView
                final Uri imageUri = Uri.parse(mCurrentProfilePhotoPath); // /storage/emulated/0/Android/data/com.example.eric.cameranougat/files/Pictures/JPEG_20170318_201833_1993305013.jpg
                File file = new File(imageUri.getPath());   // /storage/emulated/0/Android/data/com.example.eric.cameranougat/files/Pictures/JPEG_20170318_201833_1993305013.jpg
                try {
                    InputStream ims = new FileInputStream(file);

                    View v = navigationView.getHeaderView(0);
                    final ImageView imageView = ButterKnife.findById(v, R.id.imageView);

                    imageView.setImageBitmap(BitmapFactory.decodeStream(ims));
                } catch (FileNotFoundException e) {
                    return;
                }

                this.realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        UserConfig userConfig = realm.where(UserConfig.class).findFirst();
                        if (userConfig != null) {
                            userConfig.setPhotoProfileUri(imageUri.toString());
                        }

                        realm.copyToRealmOrUpdate(userConfig);
                    }
                });

                break;
            case 55:
                View v = navigationView.getHeaderView(0);
                final ImageView imageView = ButterKnife.findById(v, R.id.imageView);

                final Uri selectedImage = data.getData();
                imageView.setImageURI(selectedImage);

                this.realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        UserConfig userConfig = realm.where(UserConfig.class).findFirst();
                        if (userConfig != null) {
                            userConfig.setPhotoProfileUri(selectedImage.toString());
                        }

                        realm.copyToRealmOrUpdate(userConfig);
                    }
                });

                break;
            case 66:
                if (data != null) {

                    String json = data.getStringExtra(ActivityPoA.PARAM_LKP_DETAIL);
                    DisplayTrnLDVDetails dtl = new Gson().fromJson(json, DisplayTrnLDVDetails.class);
                    showLKPDetail(dtl);
                }
                break;
            case 67:
                Intent i = new Intent(this, ActivityPaymentEntri.class);
//                         DO NOT SEND ANY PARAMs !
                startActivity(i);

                break;

        }
    }

    /**
     * @see #stopJob()
     */
    public void startJob() {

        Intent intentAlarm = new Intent(this, SyncJob.class);

        if (pendingIntent == null) {
            pendingIntent = PendingIntent.getBroadcast(this, 0, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        Calendar cal = Calendar.getInstance();
        // start 10 seconds from now
        cal.add(Calendar.SECOND, 10);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // repeat every 15 minutes
        alarmManager.setRepeating(AlarmManager.RTC, cal.getTimeInMillis(), 15 * 60 * 1000, pendingIntent);
        Log.i("fast.sync", "sync job started");

    }

    /**
     * @see #startJob()
     */
    public void stopJob() {

        if (pendingIntent == null) {
            return;
        }

        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
        Log.i("fast.sync", "sync job stopped");

    }

    private void closeBatchYesterday(boolean showDialog, final OnSuccessError listener) {
        final ProgressDialog mProgressDialog = new ProgressDialog(this);

        if (showDialog) {
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Closing Yesterday transactions.\nPlease wait...");
            mProgressDialog.show();
        }

        ApiInterface fastService =
                ServiceGenerator.createService(ApiInterface.class, Utility.buildUrl(Storage.getPreferenceAsInt(getApplicationContext(), Storage.KEY_SERVER_ID, 0)));

        Call<ResponseBody> cb = fastService.closeBatchYesterday(currentUser.getUserId());
        cb.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                Utility.dismissDialog(mProgressDialog);

                if (response.isSuccessful()) {

                    if (listener != null)
                        listener.onSuccess(null);

                } else {

                    if (listener != null)
                        try {
                            listener.onFailure(new RuntimeException(response.errorBody().string()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Utility.dismissDialog(mProgressDialog);

                if (listener != null) {
                    listener.onFailure(new RuntimeException(t != null ? t.getMessage() : "Unknown error"));
                }
            }
        });

    }


    private void closeBatchToday() {

        if (currentUser == null) {
            Utility.showDialog(MainActivity.this, "Login", "Please logout and login again.");
            return;
        }

        if (this.realm.where(TrnLDVHeader.class).count() < 1) {
            Utility.showDialog(MainActivity.this, "Invalid LKP", "Please try to Get LKP");
            return;
        }


        this.realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                TrnLDVHeader trnLDVHeader = realm.where(TrnLDVHeader.class)
//                        .equalTo("ldvNo", currentLDVNo)   // dangerous, currentLDVNo maybe null on first time activity
                        .equalTo("collCode", currentUser.getUserId())
                        .findFirst();

                if (trnLDVHeader != null) {
                    trnLDVHeader.setWorkFlag("C");
                    trnLDVHeader.setCloseBatch("Y");
                    trnLDVHeader.setLastupdateBy(Utility.LAST_UPDATE_BY);
                    trnLDVHeader.setLastupdateTimestamp(new Date());
                    realm.copyToRealmOrUpdate(trnLDVHeader); // hanya di update waktu close batch
                }

            }
        });

        final SyncLdvHeader syncLdvHeader = new SyncLdvHeader(this.realm);

        if (syncLdvHeader.anyDataToSync()) {

            final ProgressDialog mProgressDialog = Utility.createAndShowProgressDialog(this, getString(R.string.message_please_wait));

            final RequestSyncLKP req = new RequestSyncLKP();
            req.setLdvHeader(syncLdvHeader.getDataToSync());

            fillRequest(Utility.ACTION_CLOSEBATCH, req);

            // override demo user
            if (!NetUtil.isConnected(MainActivity.this)
                    && DemoUtil.isDemo(MainActivity.this)) {
                Utility.dismissDialog(mProgressDialog);

                if (req.getLdvHeader() != null && req.getLdvHeader().size() > 0) {
                    syncLdvHeader.syncData();

                    resetData();

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                    alertDialogBuilder.setTitle("");
                    alertDialogBuilder.setMessage("Close Batch success.\nPlease relogin.");
                    alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            backToLoginScreen();
                        }
                    });

                    alertDialogBuilder.show();
                }

                return;
            }

            ApiInterface fastService =
                    ServiceGenerator.createService(ApiInterface.class, Utility.buildUrl(Storage.getPreferenceAsInt(getApplicationContext(), Storage.KEY_SERVER_ID, 0)));

            Call<ResponseSync> call = fastService.syncLKP(req);
            call.enqueue(new Callback<ResponseSync>() {
                @Override
                public void onResponse(Call<ResponseSync> call, Response<ResponseSync> response) {

                    Utility.dismissDialog(mProgressDialog);

                    if (!response.isSuccessful()) {

                        ResponseBody errorBody = response.errorBody();

                        String msg = "";
                        try {
//                        Utility.createAndShowProgressDialog(MainActivity.this, "Server Problem (" + statusCode + ")", errorBody.string());
                            msg = response.message() + "(" + response.code() + ") " + errorBody.string();
                            Snackbar.make(coordinatorLayout, msg, Snackbar.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        return;
                    }

                    final ResponseSync respSync = response.body();

                    if (respSync == null || respSync.getError() != null) {
                        if (respSync == null) {
                            // Not found(404) berarti ada yg salah di json
                            Snackbar.make(coordinatorLayout, response.message() + "(" + response.code() + ") ", Snackbar.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Data Error (" + respSync.getError() + ")\n" + respSync.getError().getErrorDesc(), Toast.LENGTH_SHORT).show();
                        }

                    } else if (respSync.getData() != 1) {

                    } else {
                        if (req.getLdvHeader() != null && req.getLdvHeader().size() > 0) {
                            syncLdvHeader.syncData();

                            resetData();

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                            alertDialogBuilder.setTitle("");
                            alertDialogBuilder.setMessage("Close Batch success.\nPlease relogin.");
                            alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    backToLoginScreen();
                                }
                            });

                            alertDialogBuilder.show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseSync> call, Throwable t) {

                    Utility.dismissDialog(mProgressDialog);

                    Log.e("eric.onFailure", t.getMessage(), t);
                    if (t instanceof ConnectException) {
                        if (Utility.DEVELOPER_MODE)
                            Utility.showDialog(MainActivity.this, "No Connection", t.getMessage());
                        else
                            Utility.showDialog(MainActivity.this, "No Connection", getString(R.string.error_contact_admin));
                    }

                    Snackbar.make(coordinatorLayout, "CloseBatch Failed\n" + t.getMessage(), Snackbar.LENGTH_LONG).show();
                    // TODO: utk mencegah data di server udah sync, coba get lkp lagi
                    if (t.getMessage() == null) {
                        // should add delay to gave server a breath
                        try {
                            TimeUnit.SECONDS.sleep(10);
                        } catch (InterruptedException e) {
                            //Handle exception
                        }

                        clearLKPTables();
                        clearSyncTables();

                        attemptGetLKP(currentUser.getUserId(), getServerDateFromDB(realm), false, null);
                    }
                }
            });
        }

    }

    private void closeBatchOldData(boolean showDialog) {
        if (currentUser == null) {
            Utility.showDialog(MainActivity.this, "Login", "Please logout and login again.");
            return;
        }

        this.realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                TrnLDVHeader trnLDVHeader = realm.where(TrnLDVHeader.class)
                        .equalTo("collCode", currentUser.getUserId())
                        .notEqualTo("closeBatch", "Y")
                        .or()
                        .isEmpty("closeBatch")
                        .findFirst();

                if (trnLDVHeader != null) {
                    trnLDVHeader.setWorkFlag("C");
                    trnLDVHeader.setCloseBatch("Y");
                    trnLDVHeader.setLastupdateBy(Utility.LAST_UPDATE_BY);
                    trnLDVHeader.setLastupdateTimestamp(new Date());
                    realm.copyToRealmOrUpdate(trnLDVHeader); // hanya di update waktu close batch
                }

            }
        });

        final SyncLdvHeader syncLdvHeader = new SyncLdvHeader(this.realm);
        if (syncLdvHeader.anyDataToSync()) {

            final ProgressDialog mProgressDialog = new ProgressDialog(this);
            if (showDialog) {
                mProgressDialog.setIndeterminate(true);
                mProgressDialog.setCancelable(false);
                mProgressDialog.setMessage(getString(R.string.message_please_wait));
                mProgressDialog.show();
            }

            final RequestSyncLKP req = new RequestSyncLKP();
            req.setLdvHeader(syncLdvHeader.getDataToSync());

            fillRequest(Utility.ACTION_CLOSEBATCH_PREVIOUS, req);

            ApiInterface fastService =
                    ServiceGenerator.createService(ApiInterface.class, Utility.buildUrl(Storage.getPreferenceAsInt(getApplicationContext(), Storage.KEY_SERVER_ID, 0)));

            Call<ResponseSync> call = fastService.syncLKP(req);
            call.enqueue(new Callback<ResponseSync>() {
                @Override
                public void onResponse(Call<ResponseSync> call, Response<ResponseSync> response) {
                    Utility.dismissDialog(mProgressDialog);

                    if (!response.isSuccessful()) {

                        ResponseBody errorBody = response.errorBody();

                        String msg = "";
                        try {
//                        Utility.createAndShowProgressDialog(MainActivity.this, "Server Problem (" + statusCode + ")", errorBody.string());
                            msg = response.message() + "(" + response.code() + ") " + errorBody.string();
                            Snackbar.make(coordinatorLayout, msg, Snackbar.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        return;
                    }

                    final ResponseSync respSync = response.body();

                    if (respSync == null || respSync.getError() != null) {
                        if (respSync == null) {
                            // Not found(404) berarti ada yg salah di json
                            Snackbar.make(coordinatorLayout, response.message() + "(" + response.code() + ") ", Snackbar.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Data Error (" + respSync.getError() + ")\n" + respSync.getError().getErrorDesc(), Toast.LENGTH_SHORT).show();
                        }

                    } else if (respSync.getData() != 1) {

                    } else {
                        if (req.getLdvHeader() != null && req.getLdvHeader().size() > 0) {
                            syncLdvHeader.syncData();

                            resetData();

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                            alertDialogBuilder.setTitle("");
                            alertDialogBuilder.setMessage("Close Batch success.\nPlease relogin.");
                            alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    backToLoginScreen();
                                }
                            });

                            alertDialogBuilder.show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseSync> call, Throwable t) {

                    Utility.dismissDialog(mProgressDialog);

                    Log.e("eric.onFailure", t.getMessage(), t);
                    if (t instanceof ConnectException) {
                        if (Utility.DEVELOPER_MODE)
                            Utility.showDialog(MainActivity.this, "No Connection", t.getMessage());
                        else
                            Utility.showDialog(MainActivity.this, "No Connection", getString(R.string.error_contact_admin));
                    }

                    Snackbar.make(coordinatorLayout, "CloseBatch Failed\n" + t.getMessage(), Snackbar.LENGTH_LONG).show();
                    // TODO: utk mencegah data di server udah sync, coba get lkp lagi
                    if (t.getMessage() == null) {
                        // should add delay to gave server a breath
                        try {
                            TimeUnit.SECONDS.sleep(10);
                        } catch (InterruptedException e) {
                            //Handle exception
                        }

                        clearLKPTables();
                        clearSyncTables();

                        attemptGetLKP(currentUser.getUserId(), getServerDateFromDB(realm), false, null);
                    }
                }
            });
        }

    }

    /**
     * WARNING ! make sure data transaction is synced before call this method
     */
    protected void closeBatch() {

        try {
            final ProgressDialog mProgressDialog = Utility.createAndShowProgressDialog(this, getString(R.string.message_please_wait));

            // memastikan pengecekan closebatch menggunakan tanggal di server
            DataUtil.retrieveServerInfo(currentUser.getUserId(), this.realm, this, new OnPostRetrieveServerInfo() {
                @Override
                public void onSuccess(ServerInfo serverInfo) {
                    Utility.dismissDialog(mProgressDialog);

                    final String createdBy = "JOB" + Utility.convertDateToString(serverInfo.getServerDate(), "yyyyMMdd");

                    TrnLDVHeader trnLDVHeaderToday = realm.where(TrnLDVHeader.class)
                            .equalTo("collCode", currentUser.getUserId())
                            .equalTo("createdBy", createdBy)
                            .findFirst();

                    if (trnLDVHeaderToday == null) {

                        // sapa tau ada data nyantol berhari2 (dengan syarat sudah tersync data transaksi)
                        if (DataUtil.isLDVHeaderValid(realm, currentUser.getUserId()) != null) {
                            closeBatchOldData(true);
                        } else {

                            closeBatchYesterday(true, new OnSuccessError() {
                                @Override
                                public void onSuccess(String msg) {
                                    resetData();


                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                                    alertDialogBuilder.setTitle("");
                                    alertDialogBuilder.setMessage("Close Batch success.\nPlease relogin.");
                                    alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            backToLoginScreen();
                                        }
                                    });

                                    alertDialogBuilder.show();

                                }

                                @Override
                                public void onFailure(Throwable t) {
                                    Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    t.printStackTrace();

                                    if (t != null && currentUser != null && !TextUtils.isEmpty(t.getMessage()))
                                        NetUtil.syncLogError(MainActivity.this, realm, currentUser.getUserId(), "CloseBatch", t.getMessage(), null);

                                    Log.e("eric.onFailure", t.getMessage(), t);
                                }

                                @Override
                                public void onSkip() {
                                    Toast.makeText(MainActivity.this, "Close Batch skipped", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    } else {
                        closeBatchToday();
                    }

                }

                @Override
                public void onFailure(Throwable throwable) {
                    Utility.dismissDialog(mProgressDialog);

                    if (!Utility.throwableHandler(MainActivity.this, throwable, true)) {
                        if (currentUser != null)
                            NetUtil.syncLogError(MainActivity.this, realm, currentUser.getUserId(), "RetrieveServerInfo.CloseBatch", throwable.getMessage(), null);

                        Toast.makeText(MainActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    Log.e(TAG, throwable.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    protected void attemptCloseBatch() {

        // header memang bisa > 1 dan bisa kosong pertama kali utk menutup closebatch terakhir
        /*
        TrnLDVHeader trnLDVHeader = realm.where(TrnLDVHeader.class)
                .equalTo("collCode", currentUser.getUserId())
//                .equalTo("createdBy", createdBy) logikanya 1 collector 1 ldvheader
                .findFirst();

        if (trnLDVHeader == null) {
            Utility.createAndShowProgressDialog(MainActivity.this, "No Data", "Please Get LKP first");
            return;
        }
*/
        // konfirm
        // header should not checked

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Close Batch");

        alertDialogBuilder.setMessage("Are you sure?");
        //null should be your on click listener
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                View promptsView = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_pwd, null);
                final EditText input = ButterKnife.findById(promptsView, R.id.password);

                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Type Your Password")
                        .setView(promptsView)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                Utility.hideKeyboard(input);

                                String value = input.getText().toString();

                                if (!value.equals(currentUser.getUserPwd())) {
                                    Snackbar.make(coordinatorLayout, "Invalid password !", Snackbar.LENGTH_LONG).show();
                                    return;
                                }

                                boolean anyDataToSync = anyDataToSync();

                                if (anyDataToSync) {
                                    syncTransaction(false, new OnSuccessError() {
                                        @Override
                                        public void onSuccess(String msg) {
                                            // close batch here
                                            closeBatch();

                                        }

                                        @Override
                                        public void onFailure(Throwable throwable) {
                                        }

                                        @Override
                                        public void onSkip() {
                                        }
                                    });
                                    return;
                                }

                                // brarti ijo semua, close
                                closeBatch();
/*
                                // TODO: clear cookie
                                syncTransaction(true, true, new OnSuccessError() {
                                    @Override
                                    public void onSuccess(String msg) {
                                        Utility.createAndShowProgressDialog(MainActivity.this, "Close Batch", "Close Batch Success");

                                        clearLKPTables();
                                        clearSyncTables();

                                        Fragment frag = getSupportFragmentManager().findFragmentById(R.id.content_frame);

                                        if (frag != null && frag instanceof FragmentLKPList) {
                                            ((FragmentLKPList) frag).clearTodayList();
                                        }

                                    }

                                    @Override
                                    public void onFailure(Throwable throwable) {

                                    }

                                    @Override
                                    public void onSkip() {

                                    }
                                });
                                */

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Utility.hideKeyboard(input);
                            }
                        })
                        .show();

                alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        Utility.hideKeyboard(input);
                    }
                });

                Utility.showKeyboard(input);

            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialogBuilder.show();
//        input.requestFocus();
    }

    // wiwan: maunya dipanggil real time, tp kalo sudah sukses terkirim hapus yg dilokal spy ga penuh
    protected void syncLocation(final boolean showDialog, final OnSuccessError listener) {
        if (!NetUtil.isConnected(this)) {
            showSnackBar(getString(R.string.error_online_required));
            return;
        }

        String collCode = currentUser.getUserId();

        List<TrnCollPos> list = this.realm.copyFromRealm(this.realm.where(TrnCollPos.class).equalTo("collectorId", collCode).findAll());

        RequestSyncLocation req = new RequestSyncLocation();
        req.setList(list);

        fillRequest(Utility.ACTION_SYNC_LOCATION, req);

        final ProgressDialog mProgressDialog = new ProgressDialog(this);

        if (showDialog) {

            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage(getString(R.string.message_please_wait));
            mProgressDialog.show();

        }

        ApiInterface fastService =
                ServiceGenerator.createService(ApiInterface.class, Utility.buildUrl(Storage.getPreferenceAsInt(getApplicationContext(), Storage.KEY_SERVER_ID, 0)));
        Call<ResponseBody> call = fastService.syncLocation(req);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Utility.dismissDialog(mProgressDialog);

                if (response.isSuccessful()) {

                    realm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {

                            boolean b = realm.where(TrnCollPos.class).findAll().deleteAllFromRealm();

                        }
                    }, new Realm.Transaction.OnSuccess() {
                        @Override
                        public void onSuccess() {
                            if (listener != null)
                                listener.onSuccess(null);
                        }
                    }, new Realm.Transaction.OnError() {
                        @Override
                        public void onError(Throwable error) {
                            if (listener != null)
                                listener.onFailure(error);
                        }
                    });

                } else {
                    int statusCode = response.code();

                    // handle request errors yourself
                    ResponseBody errorBody = response.errorBody();

                    try {
                        Utility.showDialog(MainActivity.this, "Server Problem (" + statusCode + ")", errorBody.string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (listener != null)
                        listener.onFailure(null);

                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (listener != null)
                    listener.onFailure(t);

                Utility.dismissDialog(mProgressDialog);
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // Get last known recent location.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        // Note that this can be NULL if last location isn't already known.
        if (mCurrentLocation != null) {
            // Print current location if not null
            Log.d("DEBUG", "current location: " + mCurrentLocation.toString());
            LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        }
        // Begin polling for new location updates.
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    // Trigger new location updates at interval, with battery check
    protected void startLocationUpdates() {
        // Create the location request
        LocationRequest mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
//                .setPriority(LocationRequest.PRIORITY_LOW_POWER)
//                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        // Request location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {

        if (this.currentUser == null)
            return;

        // jangan dibatasi by jam kerja, karena bisa malem2
//        if (!Utility.isWorkingHours())
//            return;

        // New location has now been determined
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
//        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        // You can now create a LatLng Object for use with maps
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        double[] gps = new double[2];
        gps[0] = latLng.latitude;
        gps[1] = latLng.longitude;

        NetUtil.syncLocation(this, gps, true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 108:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //continueYourTask
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Hanya jalan bila sebelumnya udah get lkp
     *
     * @param showDialog
     * @param listener
     */
    public void checkPaidLKP(final boolean showDialog, final OnSuccessError listener) {

        // kata wiwan jgn dulu
        if (listener != null)
            listener.onSuccess(null);

        if (true)
            return;

        if (!NetUtil.isConnected(this)) {
            Snackbar.make(coordinatorLayout, getString(R.string.error_online_required), Snackbar.LENGTH_LONG).show();
            return;
        }

        final String collCode = currentUser.getUserId();
        final Date lkpDate = new Date();
        final String createdBy = "JOB" + Utility.convertDateToString(lkpDate, "yyyyMMdd");

        TrnLDVHeader header = realm.where(TrnLDVHeader.class)
                .equalTo("collCode", collCode)
                .equalTo(Utility.COLUMN_CREATED_BY, createdBy)
                .findFirst();

        if (header != null) {
            long count = realm.where(TrnLDVDetails.class)
                    .equalTo("pk.ldvNo", header.getLdvNo())
                    .equalTo(Utility.COLUMN_CREATED_BY, createdBy)
                    .count();
            if (count < 1) {
                if (listener != null)
                    listener.onSuccess("No LKP found for " + createdBy);
                return;
            }
        } else {
            if (listener != null)
                listener.onSuccess("No Header found for " + createdBy);
            return;
        }

        // TODO: ambil kontrak mana saja yg udah bayar
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(getString(R.string.message_please_wait));

        if (showDialog) {
            mProgressDialog.show();
        }

        RequestLKPByDate requestLKP = new RequestLKPByDate();
        requestLKP.setCollectorCode(collCode);
        requestLKP.setYyyyMMdd(Utility.convertDateToString(lkpDate, "yyyyMMdd"));

        fillRequest(Utility.ACTION_CHECK_PAID_LKP, requestLKP);

        ApiInterface fastService =
                ServiceGenerator.createService(ApiInterface.class, Utility.buildUrl(Storage.getPreferenceAsInt(getApplicationContext(), Storage.KEY_SERVER_ID, 0)));

        Call<ResponseGetLKP> call = fastService.getLKPPaidByDate(requestLKP);
        call.enqueue(new Callback<ResponseGetLKP>() {
            @Override
            public void onResponse(Call<ResponseGetLKP> call, Response<ResponseGetLKP> response) {

                Utility.dismissDialog(mProgressDialog);

                if (!response.isSuccessful()) {
                    int statusCode = response.code();

                    // handle request errors yourself
                    ResponseBody errorBody = response.errorBody();

                    try {
                        Utility.showDialog(MainActivity.this, "Server Problem (" + statusCode + ")", errorBody.string());

                        if (listener != null)
                            listener.onFailure(new RuntimeException("Server Problem (" + statusCode + ") " + errorBody.string()));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return;
                }

                final ResponseGetLKP respGetLKP = response.body();


                if (respGetLKP == null || respGetLKP.getData().getDetails() == null) {
                    if (listener != null)
                        listener.onSkip();
                    return;
                }

                if (respGetLKP.getError() != null) {
                    Utility.showDialog(MainActivity.this, "Error (" + respGetLKP.getError().getErrorCode() + ")", respGetLKP.getError().getErrorDesc());
                    NetUtil.syncLogError(MainActivity.this, realm, currentUser.getUserId(), "getLKPPaidByDate", respGetLKP.getError().getErrorCode(), respGetLKP.getError().getErrorDesc());

                    if (listener != null)
                        listener.onSkip();

                    return;
                }

                if (respGetLKP.getData().getDetails() != null && respGetLKP.getData().getDetails().size() > 0) {
                    // cancel first to avoid realm clash
                    for (TrnLDVDetails ldvDetails : respGetLKP.getData().getDetails()) {
                        // kata wiwan jangan krn menghindari collector terima duit 2x
                        //cancelSync(realm, ldvDetails.getPk().getLdvNo(), ldvDetails.getContractNo());
                    }


                    // do here
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {

                            // 1. update ldvdetails tertentu saja
                            for (TrnLDVDetails ldvDetails : respGetLKP.getData().getDetails()) {

                                // hanya tg statusnya A bisa dioverwrite ke W
                                TrnLDVDetails first = realm.where(TrnLDVDetails.class)
                                        .equalTo("contractNo", ldvDetails.getContractNo())
                                        .equalTo(Utility.COLUMN_CREATED_BY, ldvDetails.getCreatedBy())
                                        .equalTo("workStatus", "A")
//                                        .notEqualTo("workStatus", "W")
                                        .findFirst();

                                if (first == null)
                                    continue;

                                first.deleteFromRealm();

                                realm.copyToRealm(ldvDetails);

                                // 2. ga perlu modify DisplayTrnLDVDetails krn akan direfresh via loadCurrentLKP
                                /*
                                realm.where(DisplayTrnLDVDetails.class)
                                        .equalTo("contractNo", ldvDetails.getContractNo())
                                        .equalTo(Utility.COLUMN_CREATED_BY, ldvDetails.getCreatedBy())
                                        .notEqualTo("workStatus", "W")
                                        .findFirst();
                                        */

                                // 3. cancel syncdata
                            }

                            // 4. refresh lkp list
                            Fragment frag = getSupportFragmentManager().findFragmentById(R.id.content_frame);

                            if (frag != null && frag instanceof FragmentLKPList) {
                                ((FragmentLKPList) frag).loadCurrentLKP(); // kayanya masih terkadang ga mau update
                            }

                        }
                    });
                }

                if (listener != null)
                    listener.onSuccess(null);

            }

            @Override
            public void onFailure(Call<ResponseGetLKP> call, Throwable t) {
                Log.e("eric.onFailure", t.getMessage(), t);

                Utility.dismissDialog(mProgressDialog);

                if (listener != null)
                    listener.onFailure(t);

                Utility.showDialog(MainActivity.this, "Server Problem", t.getMessage());

            }
        });
    }

    /**
     * @param showDialog
     * @param listener
     * @see #onPostSyncTransactionSuccess(RequestSyncLKP, SyncLdvDetails, SyncLdvComments, SyncRvb, SyncRVColl, SyncBastbj, SyncRepo, SyncChangeAddr)
     */
    public void syncTransaction(final boolean showDialog, final OnSuccessError listener) {
        if (currentUser == null) {
            Snackbar.make(coordinatorLayout, "Please relogin", Snackbar.LENGTH_LONG).show();
            return; // be careful
        }

        if (RootUtil.isDeviceRooted()) {
            Toast.makeText(this, getString(R.string.error_rooted), Toast.LENGTH_SHORT).show();
            resetData();
            backToLoginScreen();
            return; // be careful
        }

        // check dulu udah dibayar apa belum, kalo udah yg status kuning di cancel saja
        checkPaidLKP(showDialog, new OnSuccessError() {
            @Override
            public void onSuccess(String msg) {
                final ProgressDialog mProgressDialog = new ProgressDialog(MainActivity.this);
                mProgressDialog.setIndeterminate(true);
                mProgressDialog.setCancelable(false);
                mProgressDialog.setMessage(getString(R.string.message_please_wait));

                if (showDialog) {
                    mProgressDialog.show();
                }

                final SyncLdvDetails syncLdvDetails = new SyncLdvDetails(realm);
                final SyncLdvComments syncLdvComments = new SyncLdvComments(realm);
                final SyncRvb syncRvb = new SyncRvb(realm);
                final SyncRVColl syncRVColl = new SyncRVColl(realm);
                final SyncBastbj syncBastbj = new SyncBastbj(realm);
                final SyncRepo syncRepo = new SyncRepo(realm);
                final SyncChangeAddr syncChangeAddr = new SyncChangeAddr(realm);

                boolean anyDataToSync =
                        syncLdvDetails.anyDataToSync()
                                || syncLdvComments.anyDataToSync()
                                || syncRvb.anyDataToSync()
                                || syncRVColl.anyDataToSync()
                                || syncBastbj.anyDataToSync()
                                || syncRepo.anyDataToSync()
                                || syncChangeAddr.anyDataToSync();

                if (!anyDataToSync) {

                    if (listener != null)
                        listener.onSkip();
                    return;
                } else {
                }

                final RequestSyncLKP req = new RequestSyncLKP();

                // TODO: you may test each of modules which data to sync. But dont forget to enable all on production
                req.setRvb(syncRvb.getDataToSync());
                req.setRvColl(syncRVColl.getDataToSync());
                req.setLdvDetails(syncLdvDetails.getDataToSync());
                req.setLdvComments(syncLdvComments.getDataToSync());
                req.setBastbj(syncBastbj.getDataToSync());
                req.setRepo(syncRepo.getDataToSync());
                req.setChangeAddr(syncChangeAddr.getDataToSync());

                fillRequest(Utility.ACTION_SYNC_LKP, req);

                showSnackBar(getString(R.string.message_sync_start));

                mProgressDialog.setMessage(getString(R.string.message_sync_photo_wait));

                if (showDialog) {
                    mProgressDialog.show();
                }

                // upload photo first
                final SyncPhoto syncPhoto = new SyncPhoto(realm);
                if (syncPhoto.anyDataToSync()) {
                    NetUtil.uploadPhotos(MainActivity.this, realm, new OnSuccessError() {
                        @Override
                        public void onSuccess(String msg) {

                        }

                        @Override
                        public void onFailure(Throwable throwable) {

                        }

                        @Override
                        public void onSkip() {

                        }
                    });
                }

                // do here for POA (Photo-On-Arrival)
                // mending kirim semua file spy tau ada yg gagal apa tidak
                uploadPoAOneByOne(new OnSuccessError() {
                    @Override
                    public void onSuccess(String msg) {
                        mProgressDialog.setMessage(getString(R.string.message_sync_data_wait));

                        PoAUtil.cleanPoACache();

                        PoAUtil.cleanDB(realm);

                        // override demo user
                        if (!NetUtil.isConnected(MainActivity.this)
                                && DemoUtil.isDemo(MainActivity.this)) {

                            // assume success
                            onPostSyncTransactionSuccess(req, syncLdvDetails, syncLdvComments, syncRvb, syncRVColl, syncBastbj, syncRepo, syncChangeAddr);

                            Fragment frag = getSupportFragmentManager().findFragmentById(R.id.content_frame);

                            if (frag != null && frag instanceof FragmentLKPList) {
                                ((FragmentLKPList) frag).loadCurrentLKP(); // masih terkadang ga mau update
//                    ((FragmentLKPList) frag).refresh(); ga mau update
//                    frag.refresh();
                            }

                            if (listener != null) {
                                listener.onSuccess(null);
                            }

                            if (showDialog) {
                                Utility.dismissDialog(mProgressDialog);
                            }

                            showSnackBar(getString(R.string.message_sync_finish));

                            return;
                        }

                        ApiInterface fastService =
                                ServiceGenerator.createService(ApiInterface.class, Utility.buildUrl(Storage.getPreferenceAsInt(getApplicationContext(), Storage.KEY_SERVER_ID, 0)));

                        Call<ResponseSync> call = fastService.syncLKP(req);
                        call.enqueue(new Callback<ResponseSync>() {
                            @Override
                            public void onResponse(Call<ResponseSync> call, Response<ResponseSync> response) {

                                if (!response.isSuccessful()) {

                                    ResponseBody errorBody = response.errorBody();

                                    String msg = "";
                                    try {
                                        msg = response.message() + "(" + response.code() + ") " + errorBody.string();
                                        Snackbar.make(coordinatorLayout, msg, Snackbar.LENGTH_LONG).show();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    if (listener != null)
                                        listener.onFailure(new RuntimeException(msg));

                                    if (showDialog) {
                                        Utility.dismissDialog(mProgressDialog);
                                    }

                                    return;
                                }

                                // successful here
                                final ResponseSync respSync = response.body();

                                if (respSync == null || respSync.getError() != null) {
                                    if (listener != null)
                                        listener.onFailure(new RuntimeException(getString(R.string.error_sync_failed_with_msg, "Server Error")));

                                    if (respSync == null) {
                                        // Not found(404) berarti ada yg salah di json
                                        Snackbar.make(coordinatorLayout, response.message() + "(" + response.code() + ") ", Snackbar.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(MainActivity.this, "Data Error (" + respSync.getError() + ")\n" + respSync.getError().getErrorDesc(), Toast.LENGTH_SHORT).show();
                                    }

                                } else if (respSync.getData() != 1) {

                                    if (listener != null)
                                        listener.onSkip();

                                } else {

                                    onPostSyncTransactionSuccess(req, syncLdvDetails, syncLdvComments, syncRvb, syncRVColl, syncBastbj, syncRepo, syncChangeAddr);

                                    Fragment frag = getSupportFragmentManager().findFragmentById(R.id.content_frame);

                                    if (frag != null && frag instanceof FragmentLKPList) {
                                        ((FragmentLKPList) frag).loadCurrentLKP(); // masih terkadang ga mau update
//                    ((FragmentLKPList) frag).refresh(); ga mau update
//                    frag.refresh();
                                    }

                                    if (listener != null)
                                        listener.onSuccess(null);

                                }

                                if (showDialog) {
                                    Utility.dismissDialog(mProgressDialog);
                                }

                                showSnackBar(getString(R.string.message_sync_finish));
                            }

                            @Override
                            public void onFailure(Call<ResponseSync> call, Throwable t) {
                                Log.e("eric.onFailure", t.getMessage(), t);

                                if (showDialog) {
                                    Utility.dismissDialog(mProgressDialog);
                                }

                                if (t instanceof ConnectException) {
                                    Utility.showDialog(MainActivity.this, "No Connection", Utility.DEVELOPER_MODE ? t.getMessage() : getString(R.string.error_contact_admin));
                                }

                                if (listener != null)
                                    listener.onFailure(t);

                                showSnackBar(getString(R.string.error_sync_failed_with_msg, t.getMessage()), Snackbar.LENGTH_LONG);

                                // TODO: utk mencegah data di server udah sync, coba get lkp lagi
                                if (t.getMessage() == null) {
                                    // should add delay to gave server a breath
                                    try {
                                        TimeUnit.SECONDS.sleep(10);
                                    } catch (InterruptedException e) {
                                        //Handle exception
                                    }

                                    resetData();
                                    attemptGetLKP(currentUser.getUserId(), getServerDateFromDB(realm), false, null);
                                }
                            }
                        });

                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        if (showDialog) {
                            Utility.dismissDialog(mProgressDialog);
                        }

                        if (throwable != null)
                            showSnackBar(getString(R.string.error_sync_failed_with_msg, throwable.getMessage()), Snackbar.LENGTH_LONG);
                    }

                    @Override
                    public void onSkip() {
                        if (showDialog) {
                            Utility.dismissDialog(mProgressDialog);
                        }

                    }
                });

            }

            @Override
            public void onFailure(Throwable throwable) {
                // 1ignore
            }

            @Override
            public void onSkip() {
                // ignore
            }
        });

    }

    public void showSnackBar(String message) {
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    public void showSnackBar(String message, int duration) {
        Snackbar.make(coordinatorLayout, message, duration).show();
    }

    public void promptSnackBar(String message) {
        final Snackbar snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });

        View snackbarView = snackbar.getView();
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setMaxLines(5);

        snackbar.show();
    }

    /*
    // abandoned due too consume my dev time, so meanwhile i'll choose one-bu-one method
    private void testUploadPoAMultiple() {

        File[] poaFiles = PoAUtil.getPoAFiles();
        if (poaFiles == null || poaFiles.length < 1) {
            return;
        }

        List<TrnFlagTimestamp> list = new ArrayList<>();
        Realm r = Realm.getDefaultInstance();
        try {
            list = r.copyFromRealm(r.where(TrnFlagTimestamp.class).findAll());
        } finally {
            if (r != null)
                r.close();
        }

        if (list.size() != poaFiles.length) {
            // send error to server
            NetUtil.syncLogError(getBaseContext(), realm, getCurrentUserId(), "UploadPOA", "Unmatched size TrnFlagTimestamp <> poaFiles"
                    , TrnFlagTimestamp.class.getName() +"=" +list.size()
                            + "," + "PoaFiles=" + poaFiles.length);
            showSnackBar("Cannot sync PoA");
        }

        // surely, data is more important
        if (list.size() < 1) {
            return;
        }

        try {
            NetUtil.uploadPoA(MainActivity.this, list, poaFiles, new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        showSnackBar("success");
                    } else {
                        int statusCode = response.code();

                        // handle request errors yourself
                        ResponseBody errorBody = response.errorBody();

                        try {
                            Utility.showDialog(MainActivity.this, "Server Problem (" + statusCode + ")", errorBody.string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Utility.throwableHandler(MainActivity.this, t, false);
                }
            });
        } catch (NoConnectionException e) {
            e.printStackTrace();
            showSnackBar(getString(R.string.error_online_required));
        }

    }
*/

    private void showLKPDetail(DisplayTrnLDVDetails dtl) {
        Intent i = new Intent(this, ActivityScrollingLKPDetails.class);
        i.putExtra(ActivityScrollingLKPDetails.PARAM_CONTRACT_NO, dtl.getContractNo());
        i.putExtra(ActivityScrollingLKPDetails.PARAM_LDV_NO, dtl.getLdvNo());
        i.putExtra(ActivityScrollingLKPDetails.PARAM_COLLECTOR_ID, dtl.getCollId());
        i.putExtra(ActivityScrollingLKPDetails.PARAM_LKP_DATE, dtl.getLkpDate().getTime());
        i.putExtra(ActivityScrollingLKPDetails.PARAM_WORKSTATUS, dtl.getWorkStatus());

        Date serverDate = this.realm.where(ServerInfo.class).findFirst().getServerDate();
        boolean isLKPInquiry = !dtl.getCreatedBy().equals("JOB" + Utility.convertDateToString(serverDate, "yyyyMMdd"));

        i.putExtra(ActivityScrollingLKPDetails.PARAM_IS_LKP_INQUIRY, isLKPInquiry);

        startActivity(i);

    }

    private void testuploadPoAOneByOne() {

        File[] poaFiles = PoAUtil.getPoAFiles();
        if (poaFiles == null || poaFiles.length < 1) {
            return;
        }

        List<TrnFlagTimestamp> list = new ArrayList<>();
        Realm r = Realm.getDefaultInstance();
        try {
            list = r.copyFromRealm(r.where(TrnFlagTimestamp.class).findAll());
        } finally {
            if (r != null)
                r.close();
        }

        for (TrnFlagTimestamp trn : list) {

            File matchedFile = null;

            for (File file : poaFiles) {
                if (file.getName().equals(trn.getFileName())) {
                    matchedFile = file;
                    break;
                }
            }

            if (matchedFile == null)
                continue;

            try {
                NetUtil.uploadPoA(MainActivity.this, trn, matchedFile, new OnSuccessError() {
                    @Override
                    public void onSuccess(String msg) {
//                        donot show any dialog here to avoid confusion showing message repeatedly
                        Log.e("eric.uploadPoAOneByOne", "success with message=" + msg);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        if (throwable != null)
                            Utility.throwableHandler(MainActivity.this, throwable, false);
                    }

                    @Override
                    public void onSkip() {

                    }
                });

            } catch (NoConnectionException e) {
                e.printStackTrace();
                showSnackBar(getString(R.string.error_online_required));
            }
        }

    }

    /**
     * @return < 0 means nothing uploaded
     */
    private int uploadPoAOneByOne(OnSuccessError listener) {

        List<TrnFlagTimestamp> list = new ArrayList<>();
        Realm r = Realm.getDefaultInstance();
        try {
            list = r.copyFromRealm(r.where(TrnFlagTimestamp.class).findAll());
        } finally {
            if (r != null)
                r.close();
        }

        // validate
        String errorMsg = "Cannot send PoA to Server";

        // surely, data is more important
        if (list.size() < 1) {
            if (listener != null)
                listener.onSuccess("Nothing PoA sent");

            return 0;
        }

        /*
        for (int i = 0; i < poaFiles.length; i++) {
            // analisa file
            if (!poaFiles[i].canRead()) {
                errorMsg = "Some of the Photo cannot be upload";
                valid = false;
                break;
            }
        }
        */

        /*
        if (list.size() != poaFiles.length) {
            // send error to server
            errorMsg = "Unmatched size TrnFlagTimestamp <> poaFiles";
            NetUtil.syncLogError(getBaseContext(), realm, getCurrentUserId(), "UploadPOA", errorMsg
                    , TrnFlagTimestamp.class.getName() + "=" + list.size()
                            + "," + "PoaFiles=" + poaFiles.length);
//            showSnackBar("Cannot sync PoA");

            valid = false;
        } else
        */
        File[] poaFilesBuffer = PoAUtil.getPoAFiles();
        if (poaFilesBuffer == null || poaFilesBuffer.length < 1) {
            if (listener != null)
                listener.onFailure(new RuntimeException("Photo On Arrival missing!"));

            return 0;
        }

        boolean valid = true;

        File[] poaFiles = new File[list.size()];
        for (int i = 0; i < list.size(); i++) {
            File ada = null;

            TrnFlagTimestamp trn = list.get(i);

            for (File file : poaFilesBuffer) {
                if (file.getName().equals(trn.getFileName())) {
                    ada = file ;
                    break;
                }
            }

            if (ada == null) {
                errorMsg = "Missing Photo On Arrival for contract " + trn.getPk().getContractNo();
                valid = false;
                break;
            } else {
                poaFiles[i] = ada;
            }
        }
/*
        for (TrnFlagTimestamp trn : list) {

            boolean gaAda = true;

            for (File file : poaFiles) {
                if (file.getName().equals(trn.getFileName())) {
                    gaAda = false;
                    break;
                } else {
                    poaFiles
                }
            }

            if (gaAda) {
                valid = false;
                break;
            }

        }
*/
        if (!valid) {
            if (listener != null)
                listener.onFailure(new RuntimeException(errorMsg));
            return 0;
        }

        // harusnya hanya kirim based on list, jadi bisa buat cek foto manual jika ada apa2
        // jadi keberadaan list lebih prioritas
        NetUtil.uploadPoAs(MainActivity.this, list, poaFiles, listener);

        return list.size();

    }
}
