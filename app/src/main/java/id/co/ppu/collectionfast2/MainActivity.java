package id.co.ppu.collectionfast2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.co.ppu.collectionfast2.fragments.HomeFragment;
import id.co.ppu.collectionfast2.job.SyncJob;
import id.co.ppu.collectionfast2.lkp.ActivityScrollingLKPDetails;
import id.co.ppu.collectionfast2.lkp.FragmentLKPList;
import id.co.ppu.collectionfast2.lkp.FragmentSummaryLKP;
import id.co.ppu.collectionfast2.location.LocationProvider;
import id.co.ppu.collectionfast2.location.LocationTracker;
import id.co.ppu.collectionfast2.login.LoginActivity;
import id.co.ppu.collectionfast2.payment.ActivityPaymentEntry;
import id.co.ppu.collectionfast2.pojo.DisplayTrnLDVDetails;
import id.co.ppu.collectionfast2.pojo.HistInstallments;
import id.co.ppu.collectionfast2.pojo.ServerInfo;
import id.co.ppu.collectionfast2.pojo.TrnBastbj;
import id.co.ppu.collectionfast2.pojo.TrnCollectAddr;
import id.co.ppu.collectionfast2.pojo.TrnContractBuckets;
import id.co.ppu.collectionfast2.pojo.TrnLDVComments;
import id.co.ppu.collectionfast2.pojo.TrnLDVDetails;
import id.co.ppu.collectionfast2.pojo.TrnLDVHeader;
import id.co.ppu.collectionfast2.pojo.TrnRVB;
import id.co.ppu.collectionfast2.pojo.TrnRepo;
import id.co.ppu.collectionfast2.pojo.TrnVehicleInfo;
import id.co.ppu.collectionfast2.pojo.UserData;
import id.co.ppu.collectionfast2.rest.ApiInterface;
import id.co.ppu.collectionfast2.rest.ServiceGenerator;
import id.co.ppu.collectionfast2.rest.request.RequestLKPByDate;
import id.co.ppu.collectionfast2.rest.response.ResponseGetLKP;
import id.co.ppu.collectionfast2.settings.SettingsActivity;
import id.co.ppu.collectionfast2.util.DataUtil;
import id.co.ppu.collectionfast2.util.Storage;
import id.co.ppu.collectionfast2.util.Utility;
import io.realm.Realm;
import io.realm.RealmObject;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FragmentLKPList.OnLKPListListener {

    public static final String SELECTED_NAV_MENU_KEY = "selected_nav_menu_key";

    private boolean viewIsAtHome;
    private Menu menu;
    private int mSelectedNavMenuIndex = 0;
    private PendingIntent pendingIntent;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    private UserData currentUser;

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        this.realm = Realm.getDefaultInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setBackgroundColor(); bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0000ff")));
//        toolbar.setBackgroundColor(Color.parseColor("#28166f"));
//        AppCompatDrawableManager
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        View v = navigationView.getHeaderView(0);

        currentUser = (UserData) Storage.getObjPreference(getApplicationContext(), Storage.KEY_USER, UserData.class);

        TextView tvProfileName = ButterKnife.findById(v, R.id.tvProfileName);
        tvProfileName.setText(currentUser.getSecUser().get(0).getFullName());

        TextView tvProfileEmail = ButterKnife.findById(v, R.id.tvProfileEmail);
        tvProfileEmail.setText(currentUser.getSecUser().get(0).getEmailAddr());

        // is collector photo available ?
        boolean photoNotAvail = true;
        if (photoNotAvail) {
            ImageView iv = (ImageView) v.findViewById(R.id.imageView);
            Drawable drawable = AppCompatDrawableManager.get().getDrawable(this, R.drawable.ic_account_circle_black_24dp);
            iv.setImageDrawable(drawable);
        }

        if (savedInstanceState == null) {
            displayView(R.id.nav_home);
        } else {
            // recover state
            mSelectedNavMenuIndex = savedInstanceState.getInt(SELECTED_NAV_MENU_KEY);
            displayView(mSelectedNavMenuIndex);
        }

        // re-get
        if (this.realm.where(ServerInfo.class).findFirst() == null) {
            try {
                DataUtil.retrieveServerInfo(this.realm, this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        startLocationTracker();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.realm.close();
        this.realm = null;
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
            resetData();
            return false;
        } else if (id == R.id.nav_logout) {
            logout();
            return false;
        } else if (id == R.id.nav_closeBatch) {
            closeBatch();
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
            bundle.putString(FragmentLKPList.ARG_PARAM1, currentUser.getSecUser().get(0).getUserName());
            fragment.setArguments(bundle);

            title = "LKP List";
        } else if (viewId == R.id.nav_summaryLKP) {
            fragment = new FragmentSummaryLKP();

            Bundle bundle = new Bundle();
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
            getSupportActionBar().setSubtitle(getString(R.string.title_mob_coll));
            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        drawer.closeDrawer(GravityCompat.START);

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
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialogBuilder.show();
    }

    private void closeBatch() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Close Batch");

//        Date serverDate = (Date) Storage.getObjPreference(getApplicationContext(), Storage.KEY_SERVER_DATE, Date.class);

        alertDialogBuilder.setMessage("This action will close All today's transactions. \nAre you sure?");
        //null should be your on click listener
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO: clear cookie
                syncTransaction();

                /*
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
                */
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

    private void syncTransaction() {
        // TODO: important !
        Utility.showDialog(this, "Close Batch", "Cannot close batch.\nPlease finish all transactions.");

        long ldvHeader = this.realm.where(TrnLDVHeader.class).equalTo("lastupdateBy", Utility.LAST_UPDATE_BY).count();
        long ldvDetails = this.realm.where(TrnLDVDetails.class).equalTo("lastupdateBy", Utility.LAST_UPDATE_BY).count();
        long trnRVB = this.realm.where(TrnRVB.class).equalTo("lastupdateBy", Utility.LAST_UPDATE_BY).count();
        long trnContractBuckets = this.realm.where(TrnContractBuckets.class).equalTo("lastupdateBy", Utility.LAST_UPDATE_BY).count();
        long trnLDVComments = this.realm.where(TrnLDVComments.class).equalTo("lastupdateBy", Utility.LAST_UPDATE_BY).count();
        long trnRepo = this.realm.where(TrnRepo.class).equalTo("lastupdateBy", Utility.LAST_UPDATE_BY).count();
        long trnBastbj = this.realm.where(TrnBastbj.class).equalTo("lastupdateBy", Utility.LAST_UPDATE_BY).count();
    }

    private void resetData() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Reset Data");
        alertDialogBuilder.setMessage("This will Logout Application.\nAre you sure?");
        //null should be your on click listener
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO: clear cookie
                if (realm != null) {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            realm.deleteAll();
                        }
                    });
                }

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
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialogBuilder.show();
    }

    @OnClick(R.id.fab)
    public void onFabClick(View view) {
        displayView(R.id.nav_loa);
    }



    public void loadLKPFromServer(String collectorCode, Date lkpDate, final OnPostRetrieveLKP listener) {
        if (currentUser == null || currentUser.getUser().size() != currentUser.getSecUser().size()) {
            return;
        }

        final String createdBy = "JOB" + Utility.convertDateToString(lkpDate, "yyyyMMdd");
        // load cache
        long count = this.realm.where(TrnLDVHeader.class).equalTo("collCode", collectorCode)
                .equalTo("createdBy", createdBy)
                .count();
        if (count > 0) {
            if (listener != null)
                listener.onLoadFromLocal();
            return;
        }

        // should check apakah ada data lkp yg masih kecantol di hari kemarin

        ApiInterface fastService =
                ServiceGenerator.createService(ApiInterface.class, Utility.buildUrl(Storage.getPreferenceAsInt(getApplicationContext(), Storage.KEY_SERVER_ID, 0)));

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Getting your LKP from server.\nPlease wait...");
        mProgressDialog.show();

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
                            realm.executeTransactionAsync(new Realm.Transaction() {
                                @Override
                                public void execute(Realm bgRealm) {
                                    // insert header
                                    // wipe existing tables?
                                    boolean d = bgRealm.where(TrnLDVHeader.class).equalTo(Utility.COLUMN_CREATED_BY, createdBy).findAll().deleteAllFromRealm();
                                    bgRealm.copyToRealmOrUpdate(respGetLKP.getData().getHeader());

                                    // insert address
                                    d = bgRealm.where(TrnCollectAddr.class).equalTo(Utility.COLUMN_CREATED_BY, createdBy).findAll().deleteAllFromRealm();
                                    bgRealm.copyToRealmOrUpdate(respGetLKP.getData().getAddress());

                                    // insert buckets
                                    d = bgRealm.where(TrnContractBuckets.class).equalTo(Utility.COLUMN_CREATED_BY, createdBy).findAll().deleteAllFromRealm();
                                    bgRealm.copyToRealmOrUpdate(respGetLKP.getData().getBuckets());

                                    // insert rvb
                                    d = bgRealm.where(TrnRVB.class).equalTo(Utility.COLUMN_CREATED_BY, createdBy).findAll().deleteAllFromRealm();
                                    bgRealm.copyToRealmOrUpdate(respGetLKP.getData().getRvb());

                                    // insert bastbj
                                    d = bgRealm.where(TrnBastbj.class).equalTo(Utility.COLUMN_CREATED_BY, createdBy).findAll().deleteAllFromRealm();
                                    bgRealm.copyToRealmOrUpdate(respGetLKP.getData().getBastbj());

                                    // insert vehicle info
                                    d = bgRealm.where(TrnVehicleInfo.class).equalTo(Utility.COLUMN_CREATED_BY, createdBy).findAll().deleteAllFromRealm();
                                    bgRealm.copyToRealmOrUpdate(respGetLKP.getData().getVehicleInfo());

                                    // insert history installments
                                    d = bgRealm.where(HistInstallments.class).equalTo(Utility.COLUMN_CREATED_BY, createdBy).findAll().deleteAllFromRealm();
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
//                                    bgRealm.copyToRealmOrUpdate(respGetLKP.getData().getDetails());


                                }
                            }, new Realm.Transaction.OnSuccess() {
                                @Override
                                public void onSuccess() {
                                    if (listener != null)
                                        listener.onSuccess();
//                                    startJob();   should not be here
                                }
                            }, new Realm.Transaction.OnError() {
                                @Override
                                public void onError(Throwable error) {
                                    // Transaction failed and was automatically canceled.
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

    @Override
    public void onLKPSelected(DisplayTrnLDVDetails detail) {

        if (detail instanceof RealmObject) {
            DisplayTrnLDVDetails dtl = this.realm.copyFromRealm(detail);

//            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new LKPDetailFragment()).commit();

            Intent i = new Intent(this, ActivityScrollingLKPDetails.class);
            i.putExtra(ActivityScrollingLKPDetails.PARAM_CONTRACT_NO, dtl.getContractNo());
            i.putExtra(ActivityScrollingLKPDetails.PARAM_LDV_NO, dtl.getLdvNo());
            i.putExtra(ActivityScrollingLKPDetails.PARAM_COLLECTOR_ID, dtl.getCollId());

            Date serverDate = this.realm.where(ServerInfo.class).findFirst().getServerDate();
            boolean isLKPInquiry = !detail.getCreatedBy().equals("JOB" + Utility.convertDateToString(serverDate, "yyyyMMdd"));

            i.putExtra(ActivityScrollingLKPDetails.PARAM_IS_LKP_INQUIRY, isLKPInquiry);

            startActivity(i);
        }
//        Toast.makeText(MainActivity.this, "You select " + detail.getCustName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLKPInquiry(final String collectorCode, final Date lkpDate) {
        final FragmentLKPList frag = (FragmentLKPList)
                getSupportFragmentManager().findFragmentById(R.id.content_frame);

        if (frag != null) {

            loadLKPFromServer(collectorCode, lkpDate, new OnPostRetrieveLKP() {
                @Override
                public void onLoadFromLocal() {
                    frag.loadLKP(collectorCode, lkpDate);
                }

                @Override
                public void onSuccess() {
                    frag.loadLKP(collectorCode, lkpDate);
                }

                @Override
                public void onFailure() {

                }

            });
        }

    }

    public void openPaymentEntry() {
        Intent i = new Intent(this, ActivityPaymentEntry.class);
        startActivity(i);
    }

    public void startJob() {

        if (true) {
            return;
        }

        Intent intentAlarm = new Intent(this, SyncJob.class);

        if (pendingIntent == null) {
            pendingIntent = PendingIntent.getBroadcast(this, 0, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        Calendar cal = Calendar.getInstance();
        // start 10 seconds from now
        cal.add(Calendar.SECOND, 10);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // repeat every 10 seconds
        alarmManager.setRepeating(AlarmManager.RTC, cal.getTimeInMillis(), 10000, pendingIntent);
        Log.i("fast.sync", "sync job started");

    }

    public void stopJob() {

        if (pendingIntent == null) {
            return;
        }

        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
        Log.i("fast.sync", "sync job stopped");

    }

    private void startLocationTracker() {
        // Configure the LocationTracker's broadcast receiver to run every 5 minutes.
        Intent intent = new Intent(this, LocationTracker.class);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(),
                LocationProvider.FIVE_MINUTES, pendingIntent);
    }

    public interface OnPostRetrieveLKP {

        void onLoadFromLocal();

        void onSuccess();

        void onFailure();
    }
}
