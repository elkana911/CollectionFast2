package id.co.ppu.collectionfast2;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.co.ppu.collectionfast2.fragments.HomeFragment;
import id.co.ppu.collectionfast2.fragments.LKPListFragment;
import id.co.ppu.collectionfast2.login.LoginActivity;
import id.co.ppu.collectionfast2.pojo.TrxLDVDetails;
import id.co.ppu.collectionfast2.pojo.TrxLDVHeader;
import id.co.ppu.collectionfast2.pojo.User;
import id.co.ppu.collectionfast2.rest.ApiInterface;
import id.co.ppu.collectionfast2.rest.ServiceGenerator;
import id.co.ppu.collectionfast2.rest.request.RequestLKP;
import id.co.ppu.collectionfast2.rest.response.ResponseGetLKP;
import id.co.ppu.collectionfast2.settings.SettingsActivity;
import id.co.ppu.collectionfast2.util.Storage;
import id.co.ppu.collectionfast2.util.Utility;
import io.realm.Realm;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LKPListFragment.OnLKPSelectedListener {

    public static final String SELECTED_NAV_MENU_KEY = "selected_nav_menu_key";

    private boolean viewIsAtHome;

    private int mSelectedNavMenuIndex = 0;

    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    private User currentUsr;

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        this.realm = Realm.getDefaultInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        View v = navigationView.getHeaderView(0);

        currentUsr = (User) Storage.getObjPreference(getApplicationContext(), "user", User.class);

        TextView tvProfileName = ButterKnife.findById(v, R.id.tvProfileName);
        tvProfileName.setText(currentUsr.getFullName());

        TextView tvProfileEmail = ButterKnife.findById(v, R.id.tvProfileEmail);
        tvProfileEmail.setText(currentUsr.getEmailAddr());

        // is collector photo available ?
        boolean photoNotAvail = true;
        if (photoNotAvail) {
            ImageView iv = (ImageView) v.findViewById(R.id.imageView);
            Drawable drawable = AppCompatDrawableManager.get().getDrawable(this, R.drawable.ic_account_circle_black_24dp);
            iv.setImageDrawable(drawable);
        }

        if (savedInstanceState == null) {
            displayView(R.id.nav_home);
//            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new HomeFragment()).commit();
        } else {
            // recover state
            mSelectedNavMenuIndex = savedInstanceState.getInt(SELECTED_NAV_MENU_KEY);
            displayView(mSelectedNavMenuIndex);
        }

        loadLKP(currentUsr.getUserName());
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
        getMenuInflater().inflate(R.menu.main, menu);
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

        if (id == R.id.nav_logout) {
            logout();
        } else
            displayView(id);

        return true;
    }

    private void displayView(int viewId) {
        Fragment fragment = null;
        String title = null;
        viewIsAtHome = false;

        navigationView.setCheckedItem(viewId);

        if (viewId == R.id.nav_home) {
            fragment = new HomeFragment();

            title = getString(R.string.app_name);

            viewIsAtHome = true;

            fab.show();
        } else if (viewId == R.id.nav_loa) {
            fragment = new LKPListFragment();

            Bundle bundle = new Bundle();
            bundle.putString("collectorCode", currentUsr.getUserName());
            fragment.setArguments(bundle);

            title = "LKP List";
        }/* else if (viewId == R.id.nav_paymentEntry) {
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

    @OnClick(R.id.fab)
    public void onFabClick(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    private void loadLKP(String collectorCode) {
        if (currentUsr == null) {
            return;
        }

        // load cache
        long count = this.realm.where(TrxLDVDetails.class).count();
        if (count > 0) {
            return;
        }

        ApiInterface fastService =
                ServiceGenerator.createService(ApiInterface.class, Utility.buildUrl());

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Get LKP for " + collectorCode + "...");
        mProgressDialog.show();

        RequestLKP request = new RequestLKP();
        request.setCollectorCode(collectorCode);

        Call<ResponseGetLKP> call = fastService.getLKP(request);
        call.enqueue(new Callback<ResponseGetLKP>() {
            @Override
            public void onResponse(Call<ResponseGetLKP> call, Response<ResponseGetLKP> response) {

                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                if (response.isSuccessful()) {
                    final ResponseGetLKP respGetLKP = response.body();

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
                                    long count = bgRealm.where(TrxLDVHeader.class).count();
                                    if (count > 0) {
                                        bgRealm.delete(TrxLDVHeader.class);
                                    }
                                    bgRealm.copyToRealmOrUpdate(respGetLKP.getData().getHeader());

                                    // insert details
                                    count = bgRealm.where(TrxLDVDetails.class).count();
                                    if (count > 0) {
                                        bgRealm.delete(TrxLDVDetails.class);
                                    }
                                    bgRealm.copyToRealmOrUpdate(respGetLKP.getData().getDetails());

                                }
                            }, new Realm.Transaction.OnSuccess() {
                                @Override
                                public void onSuccess() {
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
                }

            }

            @Override
            public void onFailure(Call<ResponseGetLKP> call, Throwable t) {

                Log.e("eric.onFailure", t.getMessage(), t);

                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Utility.showDialog(MainActivity.this, "Server Problem", t.getMessage());
            }
        });

    }

    @Override
    public void onLKPSelected(int position) {
        Toast.makeText(MainActivity.this, "You select " + position, Toast.LENGTH_SHORT).show();
    }
}
