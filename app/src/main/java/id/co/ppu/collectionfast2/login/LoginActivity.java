package id.co.ppu.collectionfast2.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.co.ppu.collectionfast2.BuildConfig;
import id.co.ppu.collectionfast2.MainActivity;
import id.co.ppu.collectionfast2.R;
import id.co.ppu.collectionfast2.component.BasicActivity;
import id.co.ppu.collectionfast2.exceptions.ExpiredException;
import id.co.ppu.collectionfast2.listener.OnPostRetrieveServerInfo;
import id.co.ppu.collectionfast2.listener.OnSuccessError;
import id.co.ppu.collectionfast2.pojo.ServerInfo;
import id.co.ppu.collectionfast2.pojo.UserData;
import id.co.ppu.collectionfast2.pojo.master.MstSecUser;
import id.co.ppu.collectionfast2.pojo.master.MstUser;
import id.co.ppu.collectionfast2.pojo.trn.TrnLDVHeader;
import id.co.ppu.collectionfast2.rest.ApiInterface;
import id.co.ppu.collectionfast2.rest.ServiceGenerator;
import id.co.ppu.collectionfast2.rest.request.RequestLogin;
import id.co.ppu.collectionfast2.rest.response.ResponseLogin;
import id.co.ppu.collectionfast2.rest.response.ResponseServerInfo;
import id.co.ppu.collectionfast2.rest.response.ResponseUserPwd;
import id.co.ppu.collectionfast2.util.DataUtil;
import id.co.ppu.collectionfast2.util.NetUtil;
import id.co.ppu.collectionfast2.util.RootUtil;
import id.co.ppu.collectionfast2.util.Storage;
import id.co.ppu.collectionfast2.util.Utility;
import io.realm.Realm;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BasicActivity {

    private static final String TAG = "login";


    // UI references.
    @BindView(R.id.tilUsername)
    View tilUsername;

    @BindView(R.id.tilPassword)
    View tilPassword;

    @BindView(R.id.username)
    AutoCompleteTextView mUserNameView;

    @BindView(R.id.cbRememberPwd)
    CheckBox cbRememberPwd;

    @BindView(R.id.password)
    EditText mPasswordView;

    @BindView(R.id.spServers)
    Spinner spServers;

    @BindView(R.id.imageLogo)
    ImageView imageLogo;

    @BindView(R.id.btnGetLKPUser)
    Button btnGetLKPUser;

    @BindView(R.id.tvVersion)
    TextView tvVersion;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    public ApiInterface getAPIService() {
        return
                ServiceGenerator.createService(ApiInterface.class, Utility.buildUrl(Utility.getServerID(spServers.getSelectedItem().toString())));
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        String loginDate = Storage.getPreference(getApplicationContext(), Storage.KEY_LOGIN_DATE);

        if (!TextUtils.isEmpty(loginDate)) {
            UserData prevUserData = (UserData) Storage.getObjPreference(getApplicationContext(), Storage.KEY_USER, UserData.class);

            loginOffline(prevUserData.getUserId(), prevUserData.getUserPwd());
        } else {
            boolean b = DataUtil.isMasterDataDownloaded(this, this.realm);
        }

        Animation animZoomIn = AnimationUtils.loadAnimation(this, R.anim.zoom_in);
        imageLogo.startAnimation(animZoomIn);

        tilUsername.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_left));
        tilPassword.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_right));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;

        tvVersion.setText("v" + versionName);


        if (getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE) {
            imageLogo.setVisibility(View.GONE);
        }

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    try {

                        isLatestVersion(new OnSuccessError() {
                            @Override
                            public void onSuccess(String msg) {
                                try {
                                    attemptLogin();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(Throwable throwable) {
                                if (throwable == null)
                                    return;

                                if (throwable instanceof ExpiredException)
                                    Utility.showDialog(LoginActivity.this, "Version Changed", throwable.getMessage());
                                else
                                    Toast.makeText(LoginActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onSkip() {
                                try {
                                    attemptLogin();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }
                return false;
            }
        });

        //  Declare a new thread to do a preference check
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //  Initialize SharedPreferences
                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                //  Create a new boolean and preference and set it to true
                boolean isFirstStart = false;// getPrefs.getBoolean("firstStart", true);

                //  If the activity has never started before...
                if (isFirstStart) {

                    //  Launch app intro
                    Intent i = new Intent(LoginActivity.this, WelcomeActivity.class);
                    startActivity(i);

                    //  Make a new preferences editor
                    SharedPreferences.Editor e = getPrefs.edit();

                    //  Edit preference to make it false because we don't want this to run again
//                    e.putBoolean("firstStart", false);

                    //  Apply changes
                    e.apply();
                }
            }
        });
        // Start the thread
        t.start();

        UserData prevUserData = (UserData) Storage.getObjPreference(getApplicationContext(), Storage.KEY_USER, UserData.class);

//        String lastUsername = Storage.getPreference(getApplicationContext(), Storage.KEY_USER_NAME_LAST);
        if (prevUserData != null)
            mUserNameView.setText(prevUserData.getUserId());

        String password_rem = Storage.getPreference(getApplicationContext(), Storage.KEY_PASSWORD_REMEMBER);
        if (!TextUtils.isEmpty(password_rem)) {
            if (password_rem.equalsIgnoreCase("true")) {
                cbRememberPwd.setChecked(true);

                // load user & password
                String lastPwd = Storage.getPreference(getApplicationContext(), Storage.KEY_PASSWORD_LAST);

                String username = mUserNameView.getText().toString();
                if (prevUserData != null && username.equalsIgnoreCase(prevUserData.getUserId())) {
                    mPasswordView.setText(lastPwd);
                } else
                    mPasswordView.setText(null);
            }
        }

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        List<String> servers = new ArrayList<>();
        for (int i = 0; i < Utility.servers.length; i++) {

            if (!Utility.developerMode) {
                if (Utility.servers[i][0].startsWith("local")
                        || Utility.servers[i][0].startsWith("dev-")
                        )
                    continue;
            }

            servers.add(Utility.servers[i][0]);
        }

        ServerAdapter arrayAdapter = new ServerAdapter(this, android.R.layout.simple_spinner_item, servers);
        spServers.setAdapter(arrayAdapter);

        int x = Storage.getPreferenceAsInt(getApplicationContext(), Storage.KEY_SERVER_ID, 0);
        Utility.setSpinnerAsString(spServers, Utility.getServerName(x));

        if (Utility.developerMode) {
            btnGetLKPUser.setVisibility(View.VISIBLE);

        }
    }

    @OnClick(R.id.sign_in_button)
    public void onSignInClick() {

        if (RootUtil.isDeviceRooted()) {
            Utility.showDialog(this, "Rooted", "This device is rooted. Unable to open application.");
            resetData();
            return;
        }

        try {
            /*
            if (!NetUtil.isConnected(this)) {
                Utility.showDialog(this, getString(R.string.title_no_connection), getString(R.string.error_online_required));
                return;
            }
            */

            final ProgressDialog mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Checking version...");
            mProgressDialog.show();

            isLatestVersion(new OnSuccessError() {
                @Override
                public void onSuccess(String msg) {

                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();

                    try {
                        attemptLogin();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Throwable throwable) {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();

                    if (throwable == null)
                        return;

                    if (throwable instanceof ExpiredException)
                        Utility.showDialog(LoginActivity.this, "Version Changed", throwable.getMessage());
                    else if (throwable instanceof UnknownHostException)
                        Utility.showDialog(LoginActivity.this, getString(R.string.error_server_not_found), "Please try another server.\n" + throwable.getMessage());
                    else if (throwable instanceof SocketTimeoutException)
                        Utility.showDialog(LoginActivity.this, getString(R.string.error_server_timeout), "Please check your network.\n" + throwable.getMessage());
                    else if (throwable instanceof ConnectException)
                        Utility.showDialog(LoginActivity.this, getString(R.string.error_server_down), "Please contact administrator.\n" + throwable.getMessage());
                    else
                        Toast.makeText(LoginActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSkip() {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();

                    try {
                        attemptLogin();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
//        startActivity(new Intent(this, MainActivity.class));

    }

    @OnClick(R.id.sign_up_button)
    public void onSignUpClick() {
//        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }

    private void attemptLogin() throws Exception {
/*
        Date sysDate = new Date();
        if (sysDate.after(Utility.convertStringToDate(Utility.DATE_EXPIRED_YYYYMMDD, "yyyyMMdd"))) {
//            Utility.showDialog(this, "Expired App", "This application version is expired. Please update from the latest");
//            return;
        }
*/
        // Reset errors.
        mUserNameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        final String username = mUserNameView.getText().toString();
        final String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            mUserNameView.setError(getString(R.string.error_field_required));
            focusView = mUserNameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
            return;
        }

        Storage.savePreferenceAsInt(getApplicationContext(), Storage.KEY_SERVER_ID, Utility.getServerID(spServers.getSelectedItem().toString()));

//        String lastUsername = Storage.getPreference(getApplicationContext(), Storage.KEY_USER_NAME_LAST);

        UserData prevUserData = (UserData) Storage.getObjPreference(getApplicationContext(), Storage.KEY_USER, UserData.class);

//        RealmResults<MstSecUser> all = this.realm.where(MstSecUser.class).findAll();

        if (prevUserData == null) {
            boolean isOnline = NetUtil.isConnected(this);

            if (isOnline) {
                loginOnline(username, password);
            } else {
                Utility.showDialog(this, "Offline", getString(R.string.error_offline));
            }
        } else {
            if (!username.equalsIgnoreCase(prevUserData.getUserId())) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Reset Data");
                alertDialogBuilder.setMessage("You are using different account, previous data will be reset.\nDo you want to login as " + username + " ?");
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (realm != null) {

                            realm.executeTransactionAsync(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    realm.deleteAll();
                                }
                            }, new Realm.Transaction.OnSuccess() {
                                @Override
                                public void onSuccess() {
                                    loginOnline(username, password);

                                }
                            });

                        }

                    }
                });

                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alertDialogBuilder.show();

            } else {
                Date lastMorning = (Date) Storage.getObjPreference(getApplicationContext(), Storage.KEY_USER_LAST_DAY, Date.class);
                if (lastMorning == null) {
                    loginOffline(username, password);
                } else if (Utility.isSameDay(lastMorning, new Date())) {
                    loginOffline(username, password);
                } else
                    loginOnline(username, password);

            }
        }

    }

    /*
    ga wajib online
     */
    private void isLatestVersion(final OnSuccessError listener) {
        if (!NetUtil.isConnected(this)) {
            Toast.makeText(this, getString(R.string.error_online_required), Toast.LENGTH_LONG).show();

            if (listener != null) {
                listener.onSkip();
            }

            return;
        }

        int versionCode = BuildConfig.VERSION_CODE;
        final String versionName = BuildConfig.VERSION_NAME;

        ApiInterface fastService = getAPIService();

        Call<ResponseBody> call = fastService.getAppVersion(versionName);

//        if return same version then return true
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ResponseBody body1 = response.body();

                if (response.isSuccessful()) {

                    try {
                        if (body1 != null) {
                            String s = body1.string();

                            Log.d(TAG, s);

                            if (s.equals(versionName)) {
                                if (listener != null) {
                                    listener.onSuccess(s);
                                }
                            } else {

                                // krn update aplikasi bisa mengakibatkan data transaksi hilang, maka dianggap sukses jika ada transaksi
                                TrnLDVHeader ldvHeader = realm.where(TrnLDVHeader.class)
                                        .equalTo("closeBatch", "N")
                                        .or()
                                        .isNull("closeBatch")
                                        .findFirst();
                                if (ldvHeader != null) {

                                    if (listener != null) {
                                        listener.onSkip();
                                    }

                                } else {

                                    if (listener != null) {
                                        listener.onFailure(new ExpiredException("Please update app to latest version"));
                                    }

                                    Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse(s));
                                    startActivity(browser);
                                }

                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    try {
                        if (body1 != null) {
                            String s = body1.string();

                            if (listener != null) {
                                listener.onFailure(new RuntimeException(s));
                            }

                            Log.d(TAG, s);
                        } else {
                            if (listener != null) {
                                listener.onFailure(new RuntimeException("Get Version failed"));
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        if (listener != null) {
                            listener.onFailure(e);
                        }
                    }

                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                t.printStackTrace();

                if (listener != null) {
                    listener.onFailure(t);
                }
            }
        });

    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin2() throws Exception {

        Date sysDate = new Date();
        if (sysDate.after(Utility.convertStringToDate(Utility.DATE_EXPIRED_YYYYMMDD, "yyyyMMdd"))) {
            Utility.showDialog(this, "Expired App", "This application version is expired. Please update from the latest");
            return;
        }


        // Reset errors.
        mUserNameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        final String username = mUserNameView.getText().toString();
        final String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            mUserNameView.setError(getString(R.string.error_field_required));
            focusView = mUserNameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
            return;
        }

        Storage.savePreferenceAsInt(getApplicationContext(), Storage.KEY_SERVER_ID, Utility.getServerID(spServers.getSelectedItem().toString()));

        //check db local apakah usernya ada ?
        long countUser = this.realm.where(MstSecUser.class).equalTo("userName", username).count();

        if (countUser < 1) {
            boolean isOnline = NetUtil.isConnected(this);

            if (isOnline) {
                loginOnline(username, password);
            } else {
                Utility.showDialog(this, "Offline", getString(R.string.error_offline));
            }
        } else {
/*
            String lastUsername = Storage.getPreference(getApplicationContext(), Storage.KEY_USER_NAME_LAST);

            if (!username.equalsIgnoreCase(lastUsername)) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Reset Data");
                alertDialogBuilder.setMessage("You are using different account, previous data will be reset.\nDo you want to login as " + username + " ?");
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (realm != null) {

                            realm.executeTransactionAsync(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    realm.deleteAll();
                                }
                            }, new Realm.Transaction.OnSuccess() {
                                @Override
                                public void onSuccess() {

                                    Date lastMorning = (Date) Storage.getObjPreference(getApplicationContext(), Storage.KEY_USER_LAST_DAY, Date.class);
                                    if (lastMorning == null) {
                                        loginOffline(username, password);
                                    } else if (Utility.isSameDay(lastMorning, new Date())) {
                                        loginOffline(username, password);
                                    } else
                                        loginOnline(username, password);

                                }
                            });

                        }

                    }
                });

                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alertDialogBuilder.show();

                return;
            }

            Date lastMorning = (Date) Storage.getObjPreference(getApplicationContext(), Storage.KEY_USER_LAST_DAY, Date.class);
            if (lastMorning == null) {
                loginOffline(username, password);
            } else if (Utility.isSameDay(lastMorning, new Date())) {
                loginOffline(username, password);
            } else
                loginOnline(username, password);

            */
        }

    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    private void retrieveServerInfo(final OnPostRetrieveServerInfo listener) throws Exception {
        ApiInterface fastService = getAPIService();

        Call<ResponseServerInfo> call = fastService.getServerInfo();
        call.enqueue(new Callback<ResponseServerInfo>() {
            @Override
            public void onResponse(Call<ResponseServerInfo> call, Response<ResponseServerInfo> response) {
                if (!response.isSuccessful()) {
                    return;

                }

                final ResponseServerInfo responseServerInfo = response.body();

                if (responseServerInfo.getError() != null) {
                    return;
                }

                LoginActivity.this.realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.delete(ServerInfo.class);
                        realm.copyToRealm(responseServerInfo.getData());

                        if (listener != null) {
                            listener.onSuccess(responseServerInfo.getData());
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<ResponseServerInfo> call, Throwable t) {
                Utility.disableScreen(LoginActivity.this, false);

                if (listener != null) {
                    listener.onFailure(t);
                }

                Utility.showDialog(LoginActivity.this, "Error", t.getMessage());
//                throw new RuntimeException("Failure retrieve server information");
            }
        });

    }

    private void loginOnline(final String username, final String password) {
        Utility.disableScreen(this, true);

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Logging in...");
        mProgressDialog.show();

        try {
            retrieveServerInfo(new OnPostRetrieveServerInfo() {
                @Override
                public void onSuccess(ServerInfo serverInfo) {
                    ApiInterface loginService = getAPIService();

                    RequestLogin request = new RequestLogin();
                    request.setId(username);
                    request.setPwd(password);

                    fillRequest(Utility.ACTION_LOGIN, request);

                    Call<ResponseLogin> call = loginService.login(request);
                    call.enqueue(new Callback<ResponseLogin>() {
                        @Override
                        public void onResponse(Call<ResponseLogin> call, Response<ResponseLogin> response) {
                            Utility.disableScreen(LoginActivity.this, false);

                            if (response.isSuccessful()) {

                                final ResponseLogin respLogin = response.body();
                                Log.e("eric.onResponse", respLogin.toString());

                                if (respLogin.getError() != null) {
                                    Utility.showDialog(LoginActivity.this, "Error (" + respLogin.getError().getErrorCode() + ")", respLogin.getError().getErrorDesc());
                                    if (mProgressDialog.isShowing())
                                        mProgressDialog.dismiss();

                                } else {
                                    // dump
                                    LoginActivity.this.realm.executeTransactionAsync(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            long count = realm.where(MstUser.class).count();
                                            if (count > 0) {
                                                realm.delete(MstUser.class);
                                            }
                                            count = realm.where(MstSecUser.class).count();
                                            if (count > 0) {
                                                realm.delete(MstSecUser.class);
                                            }
//                                            realm.copyToRealm(respLogin.getData().getUser());
//                                            realm.copyToRealm(respLogin.getData().getSecUser());

                                        }
                                    }, new Realm.Transaction.OnSuccess() {
                                        @Override
                                        public void onSuccess() {
                                            if (mProgressDialog.isShowing())
                                                mProgressDialog.dismiss();

                                            Storage.saveObjPreference(getApplicationContext(), Storage.KEY_USER, respLogin.getData());

                                            // able to control nextday shpuld re-login to server
                                            Storage.saveObjPreference(getApplicationContext(), Storage.KEY_USER_LAST_DAY, new Date());

                                            // final check
                                            startMainActivity();
                                        }
                                    }, new Realm.Transaction.OnError() {
                                        @Override
                                        public void onError(Throwable error) {
                                            if (mProgressDialog.isShowing())
                                                mProgressDialog.dismiss();

                                            Utility.showDialog(LoginActivity.this, "Database Problem", getString(R.string.error_contact_admin));
                                        }
                                    });
                                }
                            } else {
                                if (mProgressDialog.isShowing())
                                    mProgressDialog.dismiss();

                                int statusCode = response.code();

                                // handle request errors yourself
                                ResponseBody errorBody = response.errorBody();

                                try {
                                    Utility.showDialog(LoginActivity.this, "Server Problem (" + statusCode + ")", errorBody.string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                        }

                        @Override
                        public void onFailure(Call<ResponseLogin> call, Throwable t) {
                            Utility.disableScreen(LoginActivity.this, false);

                            Log.e("fast", t.getMessage(), t);

                            if (mProgressDialog.isShowing())
                                mProgressDialog.dismiss();

                            Utility.showDialog(LoginActivity.this, "Server Problem", t.getMessage());
                        }
                    });

                }

                @Override
                public void onFailure(Throwable throwable) {
                    Utility.disableScreen(LoginActivity.this, false);

                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();

                }
            });
        } catch (Exception e) {
            e.printStackTrace();

            if (mProgressDialog.isShowing())
                mProgressDialog.dismiss();

        }

    }

    private void startMainActivity() {
//        final String username = mUserNameView.getText().toString();
//        Storage.savePreference(getApplicationContext(), Storage.KEY_USER_NAME_LAST,  username);
        if (cbRememberPwd.isChecked()) {
            final String password = mPasswordView.getText().toString();

            String cbRememberPwds = String.valueOf(cbRememberPwd.isChecked());

            Storage.savePreference(getApplicationContext(), Storage.KEY_PASSWORD_REMEMBER, cbRememberPwds);
            Storage.savePreference(getApplicationContext(), Storage.KEY_PASSWORD_LAST, password);

        }
        startActivity(new Intent(getApplicationContext(), MainActivity.class));

        finish();
    }

    private void loginOffline(String username, String password) {

        String logoutDate = Storage.getPreference(getApplicationContext(), Storage.KEY_LOGOUT_DATE);

        if (TextUtils.isEmpty(logoutDate)) {
            resetData();

            Utility.showDialog(this, "Logout Warning", "Sorry, You are not logged out correctly last time.\n" +
                    "Please refresh your LKP");
        }

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Logging in...");
        mProgressDialog.show();

        UserData prevUserData = (UserData) Storage.getObjPreference(getApplicationContext(), Storage.KEY_USER, UserData.class);

//        String pwd = prevUserData.getUserPwd() == null ? "" : prevUserData.getUserPwd();

        if (prevUserData != null && prevUserData.getUserId().equals(username) && prevUserData.getUserPwd().equals(password)) {
            startMainActivity();
        } else {
            Utility.showDialog(this, "Invalid Login", getString(R.string.error_invalid_login));
        }
        /*
        if (prevUserData == null || !pwd.equals(password)) {
            Utility.showDialog(this, "Invalid Login", getString(R.string.error_invalid_login));
        } else {
                startMainActivity();
        }*/

        if (mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }

    private void loginOffline2(String username, String password) {

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Logging in...");
        mProgressDialog.show();

        final MstSecUser usr = this.realm.where(MstSecUser.class).contains("userName", username).findFirst();

        String pwd = usr.getUserPwd() == null ? "" : usr.getUserPwd();

        if (usr == null || !pwd.equals(password)) {
            Utility.showDialog(this, "Invalid Login", getString(R.string.error_invalid_login));
        } else {

            startMainActivity();

        }

        if (mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }

    @OnClick(R.id.btnTestGPS)
    public void onClickTestGPS() {


    }

    @OnClick(R.id.btnGetLKPUser)
    public void onClickGetLKPUser() {

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Get Any LKP User...");
        mProgressDialog.show();

        ApiInterface fastService = getAPIService();

        Call<ResponseUserPwd> call = fastService.getAnyLKPUser();
        call.enqueue(new Callback<ResponseUserPwd>() {
            @Override
            public void onResponse(Call<ResponseUserPwd> call, Response<ResponseUserPwd> response) {

                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                final ResponseUserPwd resp = response.body();

                if (resp != null && resp.getError() == null) {
                    if (resp.getData() != null) {
                        mUserNameView.setText(resp.getData()[0]);
                        mPasswordView.setText(resp.getData()[1]);
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseUserPwd> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
            }
        });

    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Login Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    public class ServerAdapter extends ArrayAdapter<String> {
        private Context ctx;
        private List<String> list;


        public ServerAdapter(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
            this.ctx = context;
            this.list = objects;

        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public String getItem(int position) {
            return list.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv = new TextView(this.ctx);
//            TextView tv = (TextView) convertView.findViewById(R.id.nama);
            tv.setPadding(10, 20, 10, 20);
            tv.setTextColor(Color.BLACK);
//            tv.setText(list.get(position).getRvbNo());
            tv.setText(list.get(position));
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);

            return tv;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView label = new TextView(this.ctx);
            label.setPadding(10, 20, 10, 20);
            label.setText(list.get(position));
            label.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);

            return label;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }

}
