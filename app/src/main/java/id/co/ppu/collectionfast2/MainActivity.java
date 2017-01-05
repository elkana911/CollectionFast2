package id.co.ppu.collectionfast2;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.OnLongClick;
import id.co.ppu.collectionfast2.chats.ChatActivity;
import id.co.ppu.collectionfast2.exceptions.ExpiredException;
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
import id.co.ppu.collectionfast2.pojo.DisplayTrnContractBuckets;
import id.co.ppu.collectionfast2.pojo.DisplayTrnLDVDetails;
import id.co.ppu.collectionfast2.pojo.ServerInfo;
import id.co.ppu.collectionfast2.pojo.UserConfig;
import id.co.ppu.collectionfast2.pojo.UserData;
import id.co.ppu.collectionfast2.pojo.sync.SyncFileUpload;
import id.co.ppu.collectionfast2.pojo.sync.SyncTrnBastbj;
import id.co.ppu.collectionfast2.pojo.sync.SyncTrnLDVComments;
import id.co.ppu.collectionfast2.pojo.sync.SyncTrnLDVDetails;
import id.co.ppu.collectionfast2.pojo.sync.SyncTrnLDVHeader;
import id.co.ppu.collectionfast2.pojo.sync.SyncTrnRVB;
import id.co.ppu.collectionfast2.pojo.sync.SyncTrnRVColl;
import id.co.ppu.collectionfast2.pojo.sync.SyncTrnRepo;
import id.co.ppu.collectionfast2.pojo.trn.HistInstallments;
import id.co.ppu.collectionfast2.pojo.trn.TrnBastbj;
import id.co.ppu.collectionfast2.pojo.trn.TrnCollPos;
import id.co.ppu.collectionfast2.pojo.trn.TrnCollectAddr;
import id.co.ppu.collectionfast2.pojo.trn.TrnContractBuckets;
import id.co.ppu.collectionfast2.pojo.trn.TrnLDVComments;
import id.co.ppu.collectionfast2.pojo.trn.TrnLDVDetails;
import id.co.ppu.collectionfast2.pojo.trn.TrnLDVHeader;
import id.co.ppu.collectionfast2.pojo.trn.TrnPhoto;
import id.co.ppu.collectionfast2.pojo.trn.TrnRVB;
import id.co.ppu.collectionfast2.pojo.trn.TrnRVColl;
import id.co.ppu.collectionfast2.pojo.trn.TrnRepo;
import id.co.ppu.collectionfast2.pojo.trn.TrnVehicleInfo;
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
import id.co.ppu.collectionfast2.util.NetUtil;
import id.co.ppu.collectionfast2.util.RootUtil;
import id.co.ppu.collectionfast2.util.Storage;
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
        , FragmentLKPList.OnFragmentLKPListInteractionListener
        {

    public static final String SELECTED_NAV_MENU_KEY = "selected_nav_menu_key";
    private static final String TAG = "MainActivity";

    private final CharSequence[] menuItems = {
            "From Camera", "From Gallery", "Delete Photo"
    };

    private boolean viewIsAtHome;
    private Menu menu;

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

        // handle mobile setup
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(true); // WARNING: rarely true
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.show();

        ApiInterface fastService =
                ServiceGenerator.createService(ApiInterface.class, Utility.buildUrl(Storage.getPreferenceAsInt(getApplicationContext(), Storage.KEY_SERVER_ID, 0)));

        Call<ResponseGetMobileConfig> call = fastService.getMobileConfig();
        call.enqueue(new Callback<ResponseGetMobileConfig>() {
            @Override
            public void onResponse(Call<ResponseGetMobileConfig> call, Response<ResponseGetMobileConfig> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

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
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

            }
        });

        String androidId = getAndroidToken();

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
                miDeveloper.setVisible(Utility.developerMode);
            }

            MenuItem miChats = mn.findItem(R.id.nav_chats);
            if (miChats != null) {
//                miChats.setVisible(Utility.developerMode);
            }

            MenuItem miReset = mn.findItem(R.id.nav_reset);
            if (miReset != null) {
//                miReset.setVisible(Utility.developerMode);
            }

            MenuItem miSyncRVB = mn.findItem(R.id.nav_getRvb);
            if (miSyncRVB != null) {
                miSyncRVB.setVisible(Utility.developerMode);
            }
        }

        View v = navigationView.getHeaderView(0);

        currentUser = (UserData) Storage.getObjPreference(getApplicationContext(), Storage.KEY_USER, UserData.class);

        TextView tvProfileName = ButterKnife.findById(v, R.id.tvProfileName);
        tvProfileName.setText(currentUser.getFullName());

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
                            startActivityForResult(takePicture, 44);//zero can be replaced with any action code
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
                ((FragmentChatWith)frag).clearChat();
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
//            ((FragmentLKPList) frag).performClickSync();
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

                                attemptGetLKP(currentUser.getUserId(), getServerDate(realm), false, null);
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        }

        return true;
    }


    private void getLKPFromServer(final ProgressDialog mProgressDialog, final String collectorCode, Date lkpDate, final String createdBy, final OnPostRetrieveLKP listener) {
        ApiInterface fastService =
                ServiceGenerator.createService(ApiInterface.class, Utility.buildUrl(Storage.getPreferenceAsInt(getApplicationContext(), Storage.KEY_SERVER_ID, 0)));

        // tarik LKP
        RequestLKPByDate requestLKP = new RequestLKPByDate();
        requestLKP.setCollectorCode(collectorCode);
        requestLKP.setYyyyMMdd(Utility.convertDateToString(lkpDate, "yyyyMMdd"));

        fillRequest(Utility.ACTION_GET_LKP, requestLKP);

        Call<ResponseGetLKP> call = fastService.getLKPByDate(requestLKP);
        call.enqueue(new Callback<ResponseGetLKP>() {
            @Override
            public void onResponse(Call<ResponseGetLKP> call, Response<ResponseGetLKP> response) {

                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                if (response.isSuccessful()) {
                    final ResponseGetLKP respGetLKP = response.body();

                    if (respGetLKP == null) {
                        Utility.showDialog(MainActivity.this, "No LKP found", "You have empty List");
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
                                    // insert header
                                    // wipe existing tables?
                                    boolean d = bgRealm.where(TrnLDVHeader.class)
                                            .equalTo("collCode", collectorCode)
                                            .equalTo(Utility.COLUMN_CREATED_BY, createdBy)
                                            .findAll()
                                            .deleteAllFromRealm();

                                    bgRealm.copyToRealm(respGetLKP.getData().getHeader());

                                    currentLDVNo = respGetLKP.getData().getHeader().getLdvNo();

                                    // refresh synctable
                                    TrnLDVHeader _header = respGetLKP.getData().getHeader();
                                    if (_header.getFlagDone() != null && _header.getFlagDone().equalsIgnoreCase("Y")) {
                                        SyncTrnLDVHeader sync = bgRealm.where(SyncTrnLDVHeader.class)
                                                .equalTo("ldvNo", _header.getLdvNo()).findFirst();

                                        if (sync == null) {
                                            sync = new SyncTrnLDVHeader();
                                        }
                                        sync.setLdvNo(_header.getLdvNo());
                                        sync.setLastUpdateBy(_header.getLastupdateBy());
                                        sync.setCreatedBy(_header.getCreatedBy());

                                        sync.setSyncedDate(_header.getDateDone());

                                        bgRealm.copyToRealm(sync);
                                    }

                                    // insert address
                                    d = bgRealm.where(TrnCollectAddr.class).equalTo(Utility.COLUMN_CREATED_BY, createdBy).findAll().deleteAllFromRealm();
                                    bgRealm.copyToRealm(respGetLKP.getData().getAddress());

                                    // insert rvb
                                    d = bgRealm.where(TrnRVB.class).equalTo(Utility.COLUMN_CREATED_BY, createdBy).findAll().deleteAllFromRealm();
                                    bgRealm.copyToRealm(respGetLKP.getData().getRvb());

                                    for (TrnRVB _rvb : respGetLKP.getData().getRvb()) {
                                        if (_rvb == null)
                                            continue;

                                        if (_rvb.getFlagDone() != null && _rvb.getFlagDone().equalsIgnoreCase("Y")) {
                                            SyncTrnRVB s = bgRealm.where(SyncTrnRVB.class)
                                                    .equalTo("rvbNo", _rvb.getRvbNo()).findFirst();

                                            if (s == null) {
                                                s = new SyncTrnRVB();
                                            }
                                            s.setRvbNo(_rvb.getRvbNo());
                                            s.setLastUpdateBy(_rvb.getLastupdateBy());
                                            s.setCreatedBy(_rvb.getCreatedBy());
                                            s.setSyncedDate(_rvb.getDateDone());

                                            bgRealm.copyToRealm(s);
                                        }
                                    }


                                    // insert bastbj
                                    d = bgRealm.where(TrnBastbj.class).equalTo(Utility.COLUMN_CREATED_BY, createdBy).findAll().deleteAllFromRealm();
                                    bgRealm.copyToRealm(respGetLKP.getData().getBastbj());

                                    for (TrnBastbj _bastbj : respGetLKP.getData().getBastbj()) {

                                        if (_bastbj == null)
                                            continue;

                                        if (_bastbj.getFlagDone() != null && _bastbj.getFlagDone().equalsIgnoreCase("Y")) {
                                            SyncTrnBastbj sync = bgRealm.where(SyncTrnBastbj.class)
                                                    .equalTo("bastbjNo", _bastbj.getBastbjNo()).findFirst();

                                            if (sync == null) {
                                                sync = new SyncTrnBastbj();
                                            }
                                            sync.setBastbjNo(_bastbj.getBastbjNo());
                                            sync.setLastUpdateBy(_bastbj.getLastupdateBy());
                                            sync.setCreatedBy(_bastbj.getCreatedBy());
                                            sync.setSyncedDate(_bastbj.getDateDone());

                                            bgRealm.copyToRealm(sync);
                                        }
                                    }

                                    // insert vehicle info
                                    d = bgRealm.where(TrnVehicleInfo.class).equalTo(Utility.COLUMN_CREATED_BY, createdBy).findAll().deleteAllFromRealm();
                                    bgRealm.copyToRealmOrUpdate(respGetLKP.getData().getVehicleInfo());

                                    // insert history installments
                                    d = bgRealm.where(HistInstallments.class)./*equalTo(Utility.COLUMN_CREATED_BY, createdBy).*/findAll().deleteAllFromRealm();
                                    bgRealm.copyToRealmOrUpdate(respGetLKP.getData().getHistoryInstallments());

                                    // insert buckets
                                    d = bgRealm.where(TrnContractBuckets.class).equalTo(Utility.COLUMN_CREATED_BY, createdBy).findAll().deleteAllFromRealm();
                                    bgRealm.copyToRealm(respGetLKP.getData().getBuckets());

                                    // insert details
                                    d = bgRealm.where(TrnLDVDetails.class).equalTo(Utility.COLUMN_CREATED_BY, createdBy).findAll().deleteAllFromRealm();

                                    // link trxCollectAddr
                                    for (TrnLDVDetails ldvDetails : respGetLKP.getData().getDetails()) {

                                        TrnCollectAddr _address = null;
                                        for (TrnCollectAddr addr : respGetLKP.getData().getAddress()) {
                                            if (addr.getPk().getContractNo().equalsIgnoreCase(ldvDetails.getContractNo())) {
                                                _address = addr;
                                                break;
                                            }
                                        }
                                        ldvDetails.setAddress(_address);
                                        bgRealm.copyToRealm(ldvDetails);
                                    }

                                    for (TrnLDVDetails _ldvDtl : respGetLKP.getData().getDetails()) {
                                        if (_ldvDtl == null)
                                            continue;

                                        if (_ldvDtl.getFlagDone() != null && _ldvDtl.getFlagDone().equalsIgnoreCase("Y")) {
                                            SyncTrnLDVDetails sync = bgRealm.where(SyncTrnLDVDetails.class)
                                                    .equalTo("ldvNo", _ldvDtl.getPk().getLdvNo())
                                                    .equalTo("seqNo", _ldvDtl.getPk().getSeqNo())
                                                    .equalTo("contractNo", _ldvDtl.getContractNo())
                                                    .findFirst();

                                            if (sync == null) {
                                                sync = new SyncTrnLDVDetails();
                                            }
                                            sync.setLdvNo(_ldvDtl.getPk().getLdvNo());
                                            sync.setSeqNo(_ldvDtl.getPk().getSeqNo());
                                            sync.setContractNo(_ldvDtl.getContractNo());
                                            sync.setLastUpdateBy(_ldvDtl.getLastupdateBy());
                                            sync.setCreatedBy(_ldvDtl.getCreatedBy());
                                            sync.setSyncedDate(_ldvDtl.getDateDone());

                                            bgRealm.copyToRealm(sync);
                                        }
                                    }

                                    // for faster show, but must be load after ldvdetails
                                    bgRealm.delete(DisplayTrnContractBuckets.class);
                                    for (TrnContractBuckets obj : respGetLKP.getData().getBuckets()) {
                                        /*
                                        batal difilter karena pak yoce bilang utk kasus lkp yg sudah bayar bisa dientri di payment entri utk cicilan di bulan berikutnya
                                        boolean exist = false;
                                        for (TrnLDVDetails _dtl : respGetLKP.getData().getDetails()) {
                                            if (_dtl.getContractNo().equalsIgnoreCase(obj.getPk().getContractNo())) {
                                                exist = true;
                                                break;
                                            }
                                        }

                                        if (exist)
                                            continue;
                                            */

                                        DisplayTrnContractBuckets displayTrnContractBuckets = bgRealm.createObject(DisplayTrnContractBuckets.class);

                                        displayTrnContractBuckets.setContractNo(obj.getPk().getContractNo());
                                        displayTrnContractBuckets.setCreatedBy(obj.getCreatedBy());
                                        displayTrnContractBuckets.setCustName(obj.getCustName());

                                        bgRealm.copyToRealm(displayTrnContractBuckets);
                                    }

                                    //repo inserted by MOBCOL
                                    d = bgRealm.where(TrnRepo.class).equalTo(Utility.COLUMN_CREATED_BY, Utility.LAST_UPDATE_BY).findAll().deleteAllFromRealm();
                                    bgRealm.copyToRealmOrUpdate(respGetLKP.getData().getRepo());

                                    for (TrnRepo _obj : respGetLKP.getData().getRepo()) {
                                        if (_obj == null)
                                            continue;

                                        if (_obj.getFlagDone() != null && _obj.getFlagDone().equalsIgnoreCase("Y")) {
                                            SyncTrnRepo sync = bgRealm.where(SyncTrnRepo.class)
                                                    .equalTo("repoNo", _obj.getRepoNo())
                                                    .equalTo("contractNo", _obj.getContractNo())
                                                    .findFirst();

                                            if (sync == null) {
                                                sync = new SyncTrnRepo();
                                            }
                                            sync.setRepoNo(_obj.getRepoNo());
                                            sync.setContractNo(_obj.getContractNo());
                                            sync.setLastUpdateBy(_obj.getLastupdateBy());
                                            sync.setCreatedBy(_obj.getCreatedBy());
                                            sync.setSyncedDate(_obj.getDateDone());


                                            bgRealm.copyToRealm(sync);
                                        }
                                    }
                                    //changeaddr ?

                                    //ldvcomment ?
                                    d = bgRealm.where(TrnLDVComments.class).equalTo(Utility.COLUMN_CREATED_BY, Utility.LAST_UPDATE_BY).findAll().deleteAllFromRealm();
                                    bgRealm.copyToRealmOrUpdate(respGetLKP.getData().getLdvComments());

                                    for (TrnLDVComments _obj : respGetLKP.getData().getLdvComments()) {
                                        if (_obj == null)
                                            continue;

                                        if (_obj.getFlagDone() != null && _obj.getFlagDone().equalsIgnoreCase("Y")) {
                                            SyncTrnLDVComments sync = bgRealm.where(SyncTrnLDVComments.class)
                                                    .equalTo("ldvNo", _obj.getPk().getLdvNo())
                                                    .equalTo("seqNo", _obj.getPk().getSeqNo())
                                                    .equalTo("contractNo", _obj.getPk().getContractNo())
                                                    .findFirst();

                                            if (sync == null) {
                                                sync = new SyncTrnLDVComments();
                                            }
                                            sync.setLdvNo(_obj.getPk().getLdvNo());
                                            sync.setSeqNo(_obj.getPk().getSeqNo());
                                            sync.setContractNo(_obj.getPk().getContractNo());
                                            sync.setLastUpdateBy(_obj.getLastupdateBy());
                                            sync.setCreatedBy(_obj.getCreatedBy());
                                            sync.setSyncedDate(_obj.getDateDone());


                                            bgRealm.copyToRealm(sync);
                                        }
                                    }

                                    //rvcoll
                                    d = bgRealm.where(TrnRVColl.class).equalTo(Utility.COLUMN_CREATED_BY, Utility.LAST_UPDATE_BY).findAll().deleteAllFromRealm();
                                    bgRealm.copyToRealmOrUpdate(respGetLKP.getData().getRvColl());

                                    for (TrnRVColl _obj : respGetLKP.getData().getRvColl()) {
                                        if (_obj == null)
                                            continue;

                                        if (_obj.getFlagDone() != null && _obj.getFlagDone().equalsIgnoreCase("Y")) {
                                            SyncTrnRVColl sync = bgRealm.where(SyncTrnRVColl.class)
                                                    .equalTo("ldvNo", _obj.getLdvNo())
                                                    .equalTo("contractNo", _obj.getContractNo())
                                                    .findFirst();

                                            if (sync == null) {
                                                sync = new SyncTrnRVColl();
                                            }
                                            sync.setLdvNo(_obj.getLdvNo());
                                            sync.setContractNo(_obj.getContractNo());
                                            sync.setLastUpdateBy(_obj.getLastupdateBy());
                                            sync.setCreatedBy(_obj.getCreatedBy());
                                            sync.setSyncedDate(_obj.getDateDone());


                                            bgRealm.copyToRealm(sync);
                                        }
                                    }

                                    // photo
                                    d = bgRealm.where(TrnPhoto.class).findAll().deleteAllFromRealm();
                                    bgRealm.copyToRealmOrUpdate(respGetLKP.getData().getPhoto());

                                    for (TrnPhoto _obj : respGetLKP.getData().getPhoto()) {
                                        if (_obj == null)
                                            continue;

                                        if (_obj.getCreatedTimestamp() != null) {
                                            SyncFileUpload sync = bgRealm.where(SyncFileUpload.class)
                                                    .equalTo("contractNo", _obj.getContractNo())
                                                    .equalTo("collectorId", _obj.getCollCode())
                                                    .equalTo("pictureId", _obj.getPhotoId())
                                                    .findFirst();

                                            if (sync == null) {
                                                sync = new SyncFileUpload();
                                                sync.setUid(java.util.UUID.randomUUID().toString());
                                            }
//                                            sync.setLdvNo(_obj.getLdvNo());
                                            sync.setContractNo(_obj.getContractNo());
                                            sync.setCollectorId(_obj.getCollCode());
                                            sync.setPictureId(_obj.getPhotoId());
                                            sync.setSyncedDate(_obj.getCreatedTimestamp());


                                            bgRealm.copyToRealmOrUpdate(sync);
                                        }
                                    }


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

                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Utility.showDialog(MainActivity.this, "Server Problem", t.getMessage());
            }
        });


    }

    public void attemptGetLKP(final String collectorCode, final Date lkpDate, boolean useCache, final OnPostRetrieveLKP listener) {
        if (currentUser == null) {
            return;
        }

        Date serverDate = getServerDate(this.realm);

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

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Getting your LKP from server.\nPlease wait...");
        mProgressDialog.show();

        // must sync first
        syncTransaction(false, new OnSuccessError() {
            @Override
            public void onSuccess(String msg) {
                getLKPFromServer(mProgressDialog, collectorCode, lkpDate, createdBy, listener);
            }

            @Override
            public void onFailure(Throwable throwable) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
            }

            @Override
            public void onSkip() {
                getLKPFromServer(mProgressDialog, collectorCode, lkpDate, createdBy, listener);
            }
        });
/*
        syncTransaction(false, false, new OnSuccessError() {
            @Override
            public void onSuccess(String msg) {
                getLKPFromServer(mProgressDialog, collectorCode, lkpDate, createdBy, listener);
            }

            @Override
            public void onFailure(Throwable throwable) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
            }

            @Override
            public void onSkip() {
                getLKPFromServer(mProgressDialog, collectorCode, lkpDate, createdBy, listener);
            }

        });
*/
    }

    @Override
    public void onLKPSelected(DisplayTrnLDVDetails detail) {

        if (DataUtil.isLDVHeaderValid(realm, currentUser.getUserId()) != null) {
            // di method ini terkadang penggunaan getString bisa ga ketemu lho, mungkin krn listener bisa beda context
            showSnackBar(getString(R.string.warning_close_batch));
//            showSnackBar("Server date changed. Please Close Batch.");
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

//            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new LKPDetailFragment()).commit();

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
        }
//        Toast.makeText(MainActivity.this, "You select " + detail.getCustName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLKPCancelSync(DisplayTrnLDVDetails detail) {

        if (DataUtil.isLDVHeaderValid(realm, currentUser.getUserId()) != null) {
            showSnackBar(getString(R.string.warning_close_batch));
            return;
        }

        if (detail instanceof RealmObject) {
            final DisplayTrnLDVDetails dtl = this.realm.copyFromRealm(detail);

            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Cancel Sync")
                    .setMessage("Are you sure to cancel " + dtl.getCustName() + " ?\n[" + dtl.getAddress().getCollKel() + "/" + dtl.getAddress().getCollKec() + "]")
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
//        UserData userData = (UserData) Storage.getObjPreference(getApplicationContext(), Storage.KEY_USER, UserData.class);

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

        Intent i = new Intent(this, ActivityPaymentEntri.class);
        // DO NOT SEND ANY PARAMs !
        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

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
            case 55:
                if (resultCode == RESULT_OK) {
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

                }

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

                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }

                if (response.isSuccessful()) {

                    if (listener != null) {
                        listener.onSuccess(null);
                    }

                } else {

                    if (listener != null) {
                        try {
                            listener.onFailure(new RuntimeException(response.errorBody().string()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }

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

        if (TextUtils.isEmpty(currentLDVNo)) {
            Utility.showDialog(MainActivity.this, "Invalid LKP", "Please try to Get LKP");
            return;
        }


        this.realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                TrnLDVHeader trnLDVHeader = realm.where(TrnLDVHeader.class)
                        .equalTo("ldvNo", currentLDVNo)
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

            final ProgressDialog mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage(getString(R.string.message_please_wait));
            mProgressDialog.show();

            final RequestSyncLKP req = new RequestSyncLKP();
            req.setLdvHeader(syncLdvHeader.getDataToSync());

            fillRequest(Utility.ACTION_CLOSEBATCH, req);

            ApiInterface fastService =
                    ServiceGenerator.createService(ApiInterface.class, Utility.buildUrl(Storage.getPreferenceAsInt(getApplicationContext(), Storage.KEY_SERVER_ID, 0)));

            Call<ResponseSync> call = fastService.syncLKP(req);
            call.enqueue(new Callback<ResponseSync>() {
                @Override
                public void onResponse(Call<ResponseSync> call, Response<ResponseSync> response) {

                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();

                    if (!response.isSuccessful()) {

                        ResponseBody errorBody = response.errorBody();

                        String msg = "";
                        try {
//                        Utility.showDialog(MainActivity.this, "Server Problem (" + statusCode + ")", errorBody.string());
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

                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();

                    Log.e("eric.onFailure", t.getMessage(), t);
                    if (t instanceof ConnectException) {
                        if (Utility.developerMode)
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

                        attemptGetLKP(currentUser.getUserId(), getServerDate(realm), false, null);
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
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();

                    if (!response.isSuccessful()) {

                        ResponseBody errorBody = response.errorBody();

                        String msg = "";
                        try {
//                        Utility.showDialog(MainActivity.this, "Server Problem (" + statusCode + ")", errorBody.string());
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

                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();

                    Log.e("eric.onFailure", t.getMessage(), t);
                    if (t instanceof ConnectException) {
                        if (Utility.developerMode)
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

                        attemptGetLKP(currentUser.getUserId(), getServerDate(realm), false, null);
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
            final ProgressDialog mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage(getString(R.string.message_please_wait));
            mProgressDialog.show();

            // memastikan pengecekan closebatch menggunakan tanggal di server
            DataUtil.retrieveServerInfo(currentUser.getUserId(), this.realm, this, new OnPostRetrieveServerInfo() {
                @Override
                public void onSuccess(ServerInfo serverInfo) {
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }

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
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }

                    if (throwable == null) {
                        return;
                    }

                    if (throwable instanceof ExpiredException)
                        Utility.showDialog(MainActivity.this, "Version Changed", throwable.getMessage());
                    else if (throwable instanceof UnknownHostException)
                        Utility.showDialog(MainActivity.this, getString(R.string.error_server_not_found), "Please try another server.\n" + throwable.getMessage());
                    else if (throwable instanceof SocketTimeoutException)
                        Utility.showDialog(MainActivity.this, getString(R.string.error_server_timeout), "Please check your network.\n" + throwable.getMessage());
                    else if (throwable instanceof ConnectException) {
                        Utility.showDialog(MainActivity.this, getString(R.string.error_server_down), "Please contact administrator.\n" + throwable.getMessage());
                    } else {

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
            Utility.showDialog(MainActivity.this, "No Data", "Please Get LKP first");
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
                                        Utility.showDialog(MainActivity.this, "Close Batch", "Close Batch Success");

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
    }

    // wiwan: maunya dipanggil real time, tp kalo sudah sukses terkirim hapus yg dilokal spy ga penuh
    protected void syncLocation(final boolean showDialog, final OnSuccessError listener) {
        if (!NetUtil.isConnected(this)) {
            Snackbar.make(coordinatorLayout, getString(R.string.error_online_required), Snackbar.LENGTH_LONG).show();
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
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }

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

                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
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

        ApiInterface fastService =
                ServiceGenerator.createService(ApiInterface.class, Utility.buildUrl(Storage.getPreferenceAsInt(getApplicationContext(), Storage.KEY_SERVER_ID, 0)));

        RequestLKPByDate requestLKP = new RequestLKPByDate();
        requestLKP.setCollectorCode(collCode);
        requestLKP.setYyyyMMdd(Utility.convertDateToString(lkpDate, "yyyyMMdd"));

        fillRequest(Utility.ACTION_CHECK_PAID_LKP, requestLKP);

        Call<ResponseGetLKP> call = fastService.getLKPPaidByDate(requestLKP);
        call.enqueue(new Callback<ResponseGetLKP>() {
            @Override
            public void onResponse(Call<ResponseGetLKP> call, Response<ResponseGetLKP> response) {

                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

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

                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                if (listener != null)
                    listener.onFailure(t);

                Utility.showDialog(MainActivity.this, "Server Problem", t.getMessage());

            }
        });

    }

    public void syncTransaction(final boolean showDialog, final OnSuccessError listener) {
        if (!NetUtil.isConnected(this)) {
            Snackbar.make(coordinatorLayout, getString(R.string.error_online_required), Snackbar.LENGTH_LONG).show();
            return;
        }

        if (currentUser == null) {
            Snackbar.make(coordinatorLayout, "Please relogin", Snackbar.LENGTH_LONG).show();
            return;
        }

        if (RootUtil.isDeviceRooted()) {
            Toast.makeText(this, "Sorry, your device is rooted. Unable to open application.", Toast.LENGTH_SHORT).show();
            resetData();
            backToLoginScreen();
            return;
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

                Snackbar.make(coordinatorLayout, "Sync started", Snackbar.LENGTH_SHORT).show();

                mProgressDialog.setMessage("Sync Data In Progress.\nPlease wait...");

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
//                        Utility.showDialog(MainActivity.this, "Server Problem (" + statusCode + ")", errorBody.string());
                                msg = response.message() + "(" + response.code() + ") " + errorBody.string();
                                Snackbar.make(coordinatorLayout, msg, Snackbar.LENGTH_LONG).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if (listener != null)
                                listener.onFailure(new RuntimeException(msg));

                            if (showDialog) {
                                if (mProgressDialog.isShowing())
                                    mProgressDialog.dismiss();
                            }

                            return;
                        }

                        // successful here
                        final ResponseSync respSync = response.body();

                        if (respSync == null || respSync.getError() != null) {
                            if (listener != null)
                                listener.onFailure(new RuntimeException("Sync Failed due to Server Error"));

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

                            if (req.getLdvDetails() != null && req.getLdvDetails().size() > 0)
                                syncLdvDetails.syncData();

                            if (req.getLdvComments() != null && req.getLdvComments().size() > 0)
                                syncLdvComments.syncData();

                            if (req.getRepo() != null && req.getRepo().size() > 0)
                                syncRepo.syncData();

                            if (req.getRvb() != null && req.getRvb().size() > 0)
                                syncRvb.syncData();

                            if (req.getRvColl() != null && req.getRvColl().size() > 0)
                                syncRVColl.syncData();

                            if (req.getChangeAddr() != null && req.getChangeAddr().size() > 0)
                                syncChangeAddr.syncData();

                            if (req.getBastbj() != null && req.getBastbj().size() > 0)
                                syncBastbj.syncData();

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
                            if (mProgressDialog.isShowing())
                                mProgressDialog.dismiss();
                        }

                        Snackbar.make(coordinatorLayout, "Sync success", Snackbar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<ResponseSync> call, Throwable t) {
                        Log.e("eric.onFailure", t.getMessage(), t);

                        if (showDialog) {
                            if (mProgressDialog.isShowing())
                                mProgressDialog.dismiss();
                        }

                        if (t instanceof ConnectException) {
                            if (Utility.developerMode)
                                Utility.showDialog(MainActivity.this, "No Connection", t.getMessage());
                            else
                                Utility.showDialog(MainActivity.this, "No Connection", getString(R.string.error_contact_admin));
                        }

                        if (listener != null)
                            listener.onFailure(t);

                        Snackbar.make(coordinatorLayout, "Sync Failed\n" + t.getMessage(), Snackbar.LENGTH_LONG).show();

                        // TODO: utk mencegah data di server udah sync, coba get lkp lagi
                        if (t.getMessage() == null) {
                            // should add delay to gave server a breath
                            try {
                                TimeUnit.SECONDS.sleep(10);
                            } catch (InterruptedException e) {
                                //Handle exception
                            }

                            resetData();
                            attemptGetLKP(currentUser.getUserId(), getServerDate(realm), false, null);
                        }
                    }
                });

            }

            @Override
            public void onFailure(Throwable throwable) {

            }

            @Override
            public void onSkip() {

            }
        });

    }

    public void syncTransactionOld(final boolean closeBatch, final boolean showDialog, final OnSuccessError listener) {

        if (!NetUtil.isConnected(this)) {
            Snackbar.make(coordinatorLayout, getString(R.string.error_online_required), Snackbar.LENGTH_LONG).show();
            return;
        }

        if (currentUser == null) {
            Snackbar.make(coordinatorLayout, "Please relogin", Snackbar.LENGTH_LONG).show();
            return;
        }

        if (RootUtil.isDeviceRooted()) {
            Toast.makeText(this, "This device is rooted. Unable to open application.", Toast.LENGTH_SHORT).show();
            resetData();
            backToLoginScreen();
            return;
        }

        if (closeBatch) {
            closeBatch();
            /*
            // bug, krn serverDate msh ada kemungkinan tidak update krn isi serverdate hanya diupdate wkt data reset/closebatch sukses
            Date serverDate = getServerDate(this.realm);
            final String createdBy = "JOB" + Utility.convertDateToString(serverDate, "yyyyMMdd");

            TrnLDVHeader trnLDVHeaderToday = this.realm.where(TrnLDVHeader.class)
                    .equalTo("collCode", currentUser.getUserId())
                    .equalTo("createdBy", createdBy)
                    .findFirst();

            if (trnLDVHeaderToday == null) {

                closeBatchYesterday();

                return;
            }

            closeBatchToday();
            */
        }

        final SyncLdvHeader syncLdvHeader = new SyncLdvHeader(this.realm);
        final SyncLdvDetails syncLdvDetails = new SyncLdvDetails(this.realm);
        final SyncLdvComments syncLdvComments = new SyncLdvComments(this.realm);
        final SyncRvb syncRvb = new SyncRvb(this.realm);
        final SyncRVColl syncRVColl = new SyncRVColl(this.realm);
        final SyncBastbj syncBastbj = new SyncBastbj(this.realm);
        final SyncRepo syncRepo = new SyncRepo(this.realm);
        final SyncChangeAddr syncChangeAddr = new SyncChangeAddr(this.realm);

        boolean anyDataToSync =
                (closeBatch && syncLdvHeader.anyDataToSync())
                        || syncLdvDetails.anyDataToSync()
                        || syncLdvComments.anyDataToSync()
                        || syncRvb.anyDataToSync()
                        || syncRVColl.anyDataToSync()
                        || syncBastbj.anyDataToSync()
                        || syncRepo.anyDataToSync()
                        || syncChangeAddr.anyDataToSync();

        if (!anyDataToSync) {

            if (listener != null)
                listener.onSkip();

//            Toast.makeText(this, "No Data to sync", Toast.LENGTH_SHORT).show();
            return;
        } else {
        }

        final RequestSyncLKP req = new RequestSyncLKP();

        double[] gps = id.co.ppu.collectionfast2.location.Location.getGPS(this);
        String latitude = String.valueOf(gps[0]);
        String longitude = String.valueOf(gps[1]);
        req.setLatitude(latitude);
        req.setLongitude(longitude);

        if (closeBatch) {
            req.setLdvHeader(syncLdvHeader.getDataToSync());
        }

        // TODO: you may test each of modules which data to sync. But dont forget to enable all on production
        req.setRvb(syncRvb.getDataToSync());
        req.setRvColl(syncRVColl.getDataToSync());
        req.setLdvDetails(syncLdvDetails.getDataToSync());
        req.setLdvComments(syncLdvComments.getDataToSync());
        req.setBastbj(syncBastbj.getDataToSync());
        req.setRepo(syncRepo.getDataToSync());
        req.setChangeAddr(syncChangeAddr.getDataToSync());

        Snackbar.make(coordinatorLayout, "Sync started", Snackbar.LENGTH_SHORT).show();

        final ProgressDialog mProgressDialog = new ProgressDialog(this);

        if (showDialog) {

            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Sync Data In Progress.\nPlease wait...");
            mProgressDialog.show();

        }

        // upload photo first
        final SyncPhoto syncPhoto = new SyncPhoto(this.realm);
        if (syncPhoto.anyDataToSync()) {
            NetUtil.uploadPhotos(this, this.realm, new OnSuccessError() {
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
//                        Utility.showDialog(MainActivity.this, "Server Problem (" + statusCode + ")", errorBody.string());
                        msg = response.message() + "(" + response.code() + ") " + errorBody.string();
                        Snackbar.make(coordinatorLayout, msg, Snackbar.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (listener != null)
                        listener.onFailure(new RuntimeException(msg));

                    if (showDialog) {
                        if (mProgressDialog.isShowing())
                            mProgressDialog.dismiss();
                    }

                    return;
                }

                // successful here
                final ResponseSync respSync = response.body();

                if (respSync == null || respSync.getError() != null) {
                    if (listener != null)
                        listener.onFailure(new RuntimeException("Sync Failed due to Server Error"));

                    if (respSync == null) {
                        // Not found(404) berarti ada yg salah di json
                        Snackbar.make(coordinatorLayout, response.message() + "(" + response.code() + ") ", Snackbar.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Data Error (" + respSync.getError() + ")\n" + respSync.getError().getErrorDesc(), Toast.LENGTH_SHORT).show();
                    }

                } else if (respSync.getData() != 1) {

                    if (listener != null)
                        listener.onSkip();

                    if (showDialog) {
                        if (mProgressDialog.isShowing())
                            mProgressDialog.dismiss();
                    }
                } else {

                    if (closeBatch) {
                        if (req.getLdvHeader() != null && req.getLdvHeader().size() > 0) {
                            syncLdvHeader.syncData();
                        }
                    }

                    if (req.getLdvDetails() != null && req.getLdvDetails().size() > 0)
                        syncLdvDetails.syncData();

                    if (req.getLdvComments() != null && req.getLdvComments().size() > 0)
                        syncLdvComments.syncData();

                    if (req.getRepo() != null && req.getRepo().size() > 0)
                        syncRepo.syncData();

                    if (req.getRvb() != null && req.getRvb().size() > 0)
                        syncRvb.syncData();

                    if (req.getRvColl() != null && req.getRvColl().size() > 0)
                        syncRVColl.syncData();

                    if (req.getChangeAddr() != null && req.getChangeAddr().size() > 0)
                        syncChangeAddr.syncData();

                    if (req.getBastbj() != null && req.getBastbj().size() > 0)
                        syncBastbj.syncData();

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
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                }

                Snackbar.make(coordinatorLayout, "Sync success", Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponseSync> call, Throwable t) {
                Log.e("eric.onFailure", t.getMessage(), t);

                if (showDialog) {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                }

//                Toast.makeText(MainActivity.this, "Sync Failed\n" + t.getMessage(), Toast.LENGTH_LONG).show();

                if (t instanceof ConnectException) {
                    if (Utility.developerMode)
                        Utility.showDialog(MainActivity.this, "No Connection", t.getMessage());
                    else
                        Utility.showDialog(MainActivity.this, "No Connection", getString(R.string.error_contact_admin));
                }

                if (listener != null)
                    listener.onFailure(t);

                Snackbar.make(coordinatorLayout, "Sync Failed\n" + t.getMessage(), Snackbar.LENGTH_LONG).show();

                // TODO: utk mencegah data di server udah sync, coba get lkp lagi
                if (t.getMessage() == null) {
                    // should add delay to gave server a breath
                    try {
                        TimeUnit.SECONDS.sleep(10);
                    } catch (InterruptedException e) {
                        //Handle exception
                    }

                    resetData();
                    attemptGetLKP(currentUser.getUserId(), getServerDate(realm), false, null);
                }
            }
        });
    }

    public void showSnackBar(String message) {
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT).show();
    }

}
