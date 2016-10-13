package id.co.ppu.collectionfast2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.co.ppu.collectionfast2.fragments.HomeFragment;
import id.co.ppu.collectionfast2.job.SyncJob;
import id.co.ppu.collectionfast2.listener.OnLKPListListener;
import id.co.ppu.collectionfast2.listener.OnPostRetrieveLKP;
import id.co.ppu.collectionfast2.listener.OnPostRetrieveServerInfo;
import id.co.ppu.collectionfast2.listener.OnSuccessError;
import id.co.ppu.collectionfast2.lkp.ActivityScrollingLKPDetails;
import id.co.ppu.collectionfast2.lkp.FragmentLKPList;
import id.co.ppu.collectionfast2.lkp.FragmentSummaryLKP;
import id.co.ppu.collectionfast2.login.LoginActivity;
import id.co.ppu.collectionfast2.payment.entry.ActivityPaymentEntri;
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
import id.co.ppu.collectionfast2.rest.response.ResponseSync;
import id.co.ppu.collectionfast2.settings.SettingsActivity;
import id.co.ppu.collectionfast2.sync.SyncActivity;
import id.co.ppu.collectionfast2.sync.SyncBastbj;
import id.co.ppu.collectionfast2.sync.SyncChangeAddr;
import id.co.ppu.collectionfast2.sync.SyncLdvComments;
import id.co.ppu.collectionfast2.sync.SyncLdvDetails;
import id.co.ppu.collectionfast2.sync.SyncLdvHeader;
import id.co.ppu.collectionfast2.sync.SyncRVColl;
import id.co.ppu.collectionfast2.sync.SyncRepo;
import id.co.ppu.collectionfast2.sync.SyncRvb;
import id.co.ppu.collectionfast2.util.DataUtil;
import id.co.ppu.collectionfast2.util.NetUtil;
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

// TODO: tombol sync multi fitur jd lbh simple, bisa utk tarik lkp ataupun sync. pembedanya bisa dr trnldvHeader
public class MainActivity extends SyncActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnLKPListListener {

    public static final String SELECTED_NAV_MENU_KEY = "selected_nav_menu_key";

    private final CharSequence[] menuItems = {
            "From Camera", "From Gallery", "Delete Photo"
    };

    private boolean viewIsAtHome;
    private Menu menu;
    private int mSelectedNavMenuIndex = 0;
    private PendingIntent pendingIntent;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.coordinatorLayout)
    View coordinatorLayout;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    private UserData currentUser;
    public String currentLDVNo = null;

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
        if (si == null) {
            try {
                DataUtil.retrieveServerInfo(this.realm, this, new OnPostRetrieveServerInfo() {
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

                                    userConfig.setDeviceId(Utility.getDeviceId(MainActivity.this));

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
                                    Snackbar.make(coordinatorLayout, "Server date changed", Snackbar.LENGTH_SHORT).show();
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

                        userConfig.setDeviceId(Utility.getDeviceId(MainActivity.this));

                    }

                    userConfig.setLastLogin(new Date());

                    realm.copyToRealmOrUpdate(userConfig);

                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    // check dates
                    UserConfig userConfig = realm.where(UserConfig.class).findFirst();

                    if (!Utility.isSameDay(userConfig.getLastLogin(), serverDate)) {
                        Snackbar.make(coordinatorLayout, "Server date changed", Snackbar.LENGTH_SHORT).show();
                    }

                    if (userConfig.getPhotoProfileUri() != null) {
                        Uri uri;
                        uri = Uri.parse(userConfig.getPhotoProfileUri());

                        imageView.setImageURI(uri);
                    }

                }
            });
        }

        stopJob();
        startJob();
//        startLocationTracker();
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

            if (!viewIsAtHome) {
                int x = getSupportFragmentManager().getBackStackEntryCount();

                if (x > 1) {
                    getSupportFragmentManager().popBackStackImmediate();
                } else
                    displayView(R.id.nav_home);
            } else {
                //display logout dialog
//                moveTaskToBack(true);
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


//        MenuItem item = menu.findItem(R.id.action_search);

//        Fragment f = getSupportFragmentManager().findFragmentById(R.id.content_frame);

//        item.setVisible(mSelectedNavMenuIndex == R.id.nav_loa);
//        item.setVisible(f instanceof FragmentLKPList);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
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
        } else if (id == R.id.nav_reset) {
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

            closeBatch();

            return false;
        } else if (id == R.id.nav_manualSync) {

            drawer.closeDrawers();

            syncTransaction(false, true, new OnSuccessError() {
                @Override
                public void onSuccess(String msg) {

                    Fragment frag = getSupportFragmentManager().findFragmentById(R.id.content_frame);

                    if (frag != null && frag instanceof FragmentLKPList) {
                        ((FragmentLKPList)frag).refresh();
//                    frag.refresh();
                    }

                    Date serverDate = realm.where(ServerInfo.class).findFirst().getServerDate();
//                    retrieveLKPFromServer(currentUser.getUser().get(0).getUserId(), serverDate, true, null);

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

    private void displayView(int viewId) {
        Fragment fragment = null;
        String title = null;
        viewIsAtHome = false;

        if (this.menu != null) {
            MenuItem item = this.menu.findItem(R.id.action_search);
            item.setVisible(viewId == R.id.nav_loa);
        }

        navigationView.setCheckedItem(viewId);

        if (viewId == R.id.nav_home) {
            fragment = new HomeFragment();

            title = getString(R.string.app_name);

            viewIsAtHome = true;

            fab.show();
        } else if (viewId == R.id.nav_loa) {
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
            fab.hide();
        }

        mSelectedNavMenuIndex = viewId;

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
//            getSupportActionBar().setSubtitle(getString(R.string.title_mob_coll));
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
        startActivity(intent);
//                moveTaskToBack(true);
        finish();

    }

    private void logout() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Log Out");
        alertDialogBuilder.setMessage("Are you sure?");
        //null should be your on click listener
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO: clear cookie
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

    private void resetData() {
        // TODO: clear cookie
        if (realm != null) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.deleteAll();
                }
            });
        }

    }

    @OnClick(R.id.fab)
    public void onFabClick(View view) {
        displayView(R.id.nav_loa);
    }


    private void getLKP(final ProgressDialog mProgressDialog, final String collectorCode, Date lkpDate, final String createdBy, final OnPostRetrieveLKP listener) {
        ApiInterface fastService =
                ServiceGenerator.createService(ApiInterface.class, Utility.buildUrl(Storage.getPreferenceAsInt(getApplicationContext(), Storage.KEY_SERVER_ID, 0)));

        // tarik LKP
        RequestLKPByDate requestLKP = new RequestLKPByDate();
        requestLKP.setCollectorCode(collectorCode);
        requestLKP.setYyyyMMdd(Utility.convertDateToString(lkpDate, "yyyyMMdd"));

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


                                    // insert buckets
                                    d = bgRealm.where(TrnContractBuckets.class).equalTo(Utility.COLUMN_CREATED_BY, createdBy).findAll().deleteAllFromRealm();
                                    bgRealm.copyToRealm(respGetLKP.getData().getBuckets());

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
                                                    .equalTo("contractNo", _obj.getContractNo())
                                                    .findFirst();

                                            if (sync == null) {
                                                sync = new SyncTrnLDVComments();
                                            }
                                            sync.setLdvNo(_obj.getPk().getLdvNo());
                                            sync.setSeqNo(_obj.getPk().getSeqNo());
                                            sync.setContractNo(_obj.getContractNo());
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
                                            SyncFileUpload  sync = bgRealm.where(SyncFileUpload.class)
//                                                    .equalTo("ldvNo", _obj.getLdvNo())
                                                    .equalTo("contractNo", _obj.getContractNo())
                                                    .equalTo("collectorId", _obj.getCollCode())
                                                    .equalTo("pictureId", "picture1")
                                                    .findFirst();

                                            if (sync == null) {
                                                sync = new SyncFileUpload();
                                            }
//                                            sync.setLdvNo(_obj.getLdvNo());
                                            sync.setContractNo(_obj.getContractNo());
                                            sync.setCollectorId(_obj.getLastupdateBy());
                                            sync.setPictureId(_obj.getLastupdateBy());
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

    public void retrieveLKPFromServer(final String collectorCode, final Date lkpDate, boolean useCache, final OnPostRetrieveLKP listener) {
        if (currentUser == null) {
            return;
        }

        Date serverDate = this.realm.where(ServerInfo.class).findFirst().getServerDate();

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
        syncTransaction(false, false, new OnSuccessError() {
            @Override
            public void onSuccess(String msg) {
                getLKP(mProgressDialog, collectorCode, lkpDate, createdBy, listener);
            }

            @Override
            public void onFailure(Throwable throwable) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
            }

            @Override
            public void onSkip() {
                getLKP(mProgressDialog, collectorCode, lkpDate, createdBy, listener);
            }

        });

    }

    @Override
    public void onLKPSelected(DisplayTrnLDVDetails detail) {

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
        if (detail instanceof RealmObject) {
            final DisplayTrnLDVDetails dtl = this.realm.copyFromRealm(detail);

            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Cancel Sync")
                    .setMessage("Are you sure to cancel " + dtl.getCustName() + " ?\n[" + dtl.getAddress().getCollKel() + "/" + dtl.getAddress().getCollKec() + "]")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cancelSync(dtl.getLdvNo(), dtl.getContractNo());
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    private void cancelSync(final String ldvNo, final String contractNo) {

        this.realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                boolean b = realm.where(TrnLDVComments.class)
                        .equalTo("pk.ldvNo", ldvNo)
                        .equalTo("contractNo", contractNo)
                        .equalTo("createdBy", Utility.LAST_UPDATE_BY)
                        .findAll().deleteAllFromRealm();

                // sebelum hapus rvcoll, restore dulu rvb
                TrnRVColl trnRVColl = realm.where(TrnRVColl.class)
                        .equalTo("contractNo", contractNo)
                        .equalTo("createdBy", Utility.LAST_UPDATE_BY)
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

                b = realm.where(TrnRepo.class)
                        .equalTo("contractNo", contractNo)
                        .equalTo("createdBy", Utility.LAST_UPDATE_BY)
                        .findAll().deleteAllFromRealm();

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

                // TODO: confirm pak yoce mengenai NEW
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

            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Fragment frag = getSupportFragmentManager().findFragmentById(R.id.content_frame);

                if (frag != null && frag instanceof FragmentLKPList) {
                    ((FragmentLKPList)frag).refresh();
                }

            }
        });
    }

    @Override
    public void onLKPInquiry(final String collectorCode, final Date lkpDate) {

        final Fragment frag = getSupportFragmentManager().findFragmentById(R.id.content_frame);

        if (frag != null && frag instanceof FragmentLKPList) {
            retrieveLKPFromServer(collectorCode, lkpDate, false, new OnPostRetrieveLKP() {
                @Override
                public void onLoadFromLocal() {
                    currentLDVNo = ((FragmentLKPList)frag).loadLKP(collectorCode, lkpDate);
                }

                @Override
                public void onSuccess() {
                    ((FragmentLKPList)frag).loadLKP(collectorCode, lkpDate);
                }

                @Override
                public void onFailure() {

                }

            });
        }

    }

    public void openPaymentEntry() {
        Intent i = new Intent(this, ActivityPaymentEntri.class);
        // DO NOT SEND ANY PARAMs !
        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        stopJob();

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

        /*
    private void startLocationTracker() {
        Intent intent = new Intent(this, LocationTracker.class);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(),
                LocationProvider.FIVE_MINUTES, pendingIntent);
        // Configure the LocationTracker's broadcast receiver to run every 5 minutes.
    }
        */

    protected void closeBatch() {

        if (!NetUtil.isConnected(this)) {
            Snackbar.make(coordinatorLayout, getString(R.string.error_online_required), Snackbar.LENGTH_LONG).show();
            return;
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Close Batch");

//        Date serverDate = (Date) Storage.getObjPreference(getApplicationContext(), Storage.KEY_SERVER_DATE, Date.class);

        alertDialogBuilder.setMessage("Are you sure?");
        //null should be your on click listener
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO: clear cookie
                syncTransaction(true, true, new OnSuccessError() {
                    @Override
                    public void onSuccess(String msg) {
                        Utility.showDialog(MainActivity.this, "Close Batch", "Close Batch successful");

                        clearLKPTables();
                        clearSyncTables();

                        Fragment frag = getSupportFragmentManager().findFragmentById(R.id.content_frame);

                        if (frag != null && frag instanceof FragmentLKPList) {
                            ((FragmentLKPList)frag).clearTodayList();
                        }

                    }

                    @Override
                    public void onFailure(Throwable throwable) {

                    }

                    @Override
                    public void onSkip() {

                    }
                });

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

        final ProgressDialog mProgressDialog = new ProgressDialog(this);

        if (showDialog) {

            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Please wait...");
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

    protected void syncTransaction(final boolean closeBatch, final boolean showDialog, final OnSuccessError listener) {

        if (!NetUtil.isConnected(this)) {
            Snackbar.make(coordinatorLayout, getString(R.string.error_online_required), Snackbar.LENGTH_LONG).show();
            return;
        }

        ApiInterface fastService =
                ServiceGenerator.createService(ApiInterface.class, Utility.buildUrl(Storage.getPreferenceAsInt(getApplicationContext(), Storage.KEY_SERVER_ID, 0)));

        if (closeBatch) {

            if (TextUtils.isEmpty(currentLDVNo)) {

                // coba close LKP yg kmrn
                final ProgressDialog mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setIndeterminate(true);
                mProgressDialog.setCancelable(false);
                mProgressDialog.setMessage("Please wait...");
                mProgressDialog.show();

                Call<ResponseBody> cb = fastService.closeBatchYesterday(currentUser.getUserId());
                cb.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        if (mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                        }
                        if (response.isSuccessful()) {

                            resetData();


                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                            alertDialogBuilder.setTitle("");
                            alertDialogBuilder.setMessage("Close Batch success. Please relogin.");
                            alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    backToLoginScreen();
                                }
                            });

                            alertDialogBuilder.show();

                        } else {

                            try {
                                Utility.showDialog(MainActivity.this, "Close Batch", response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        if (mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                        }
                        Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


//                Utility.showDialog(this, "Error Close Batch", "LKP not loaded");
                return;
            }

            this.realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    TrnLDVHeader trnLDVHeader = realm.where(TrnLDVHeader.class)
                            .equalTo("ldvNo", currentLDVNo)
                            .equalTo("collCode", currentUser.getUserId())
//                    .equalTo("createdBy", createdBy)
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
            mProgressDialog.setMessage("Please wait...");
            mProgressDialog.show();

        }
        Call<ResponseSync> call = fastService.syncLKP(req);
        call.enqueue(new Callback<ResponseSync>() {
            @Override
            public void onResponse(Call<ResponseSync> call, Response<ResponseSync> response) {
                final ResponseSync respSync = response.body();

                if (showDialog) {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                }

                if (respSync.getError() != null) {
                    if (listener != null)
                        listener.onSkip();

                    Toast.makeText(MainActivity.this, "Data Error (" + respSync.getError() + ")\n" + respSync.getError().getErrorDesc(), Toast.LENGTH_SHORT).show();
                    return;
                }

                // TODO: tackle successful sync result here
                if (respSync.getData() != 1) {
                    if (listener != null)
                        listener.onSkip();

                    return;
                }

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
//                final FragmentLKPList frag = (FragmentLKPList)
//                        getSupportFragmentManager().findFragmentById(R.id.content_frame);

                if (frag != null && frag instanceof FragmentLKPList) {
                    ((FragmentLKPList)frag).loadCurrentLKP();
//                    frag.refresh();
                }

                if (listener != null)
                    listener.onSuccess(null);

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

                if (listener != null)
                    listener.onFailure(t);
                Snackbar.make(coordinatorLayout, "Sync Failed\n" + t.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

}
