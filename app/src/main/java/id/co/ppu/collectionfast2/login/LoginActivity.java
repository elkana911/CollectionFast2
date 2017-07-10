package id.co.ppu.collectionfast2.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import id.co.ppu.collectionfast2.BuildConfig;
import id.co.ppu.collectionfast2.MainActivity;
import id.co.ppu.collectionfast2.R;
import id.co.ppu.collectionfast2.component.BasicActivity;
import id.co.ppu.collectionfast2.exceptions.ExpiredException;
import id.co.ppu.collectionfast2.listener.OnPostRetrieveServerInfo;
import id.co.ppu.collectionfast2.listener.OnSuccessError;
import id.co.ppu.collectionfast2.pojo.ServerInfo;
import id.co.ppu.collectionfast2.pojo.master.MstSecUser;
import id.co.ppu.collectionfast2.pojo.master.MstUser;
import id.co.ppu.collectionfast2.pojo.trn.TrnLDVHeader;
import id.co.ppu.collectionfast2.rest.APIClientBuilder;
import id.co.ppu.collectionfast2.rest.APInterface;
import id.co.ppu.collectionfast2.rest.request.RequestLogin;
import id.co.ppu.collectionfast2.rest.response.ResponseLogin;
import id.co.ppu.collectionfast2.rest.response.ResponseServerInfo;
import id.co.ppu.collectionfast2.rest.response.ResponseUserPwd;
import id.co.ppu.collectionfast2.util.DataUtil;
import id.co.ppu.collectionfast2.util.DemoUtil;
import id.co.ppu.collectionfast2.util.DeviceUtil;
import id.co.ppu.collectionfast2.util.NetUtil;
import id.co.ppu.collectionfast2.util.RootUtil;
import id.co.ppu.collectionfast2.util.Storage;
import id.co.ppu.collectionfast2.util.UserUtil;
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

    @BindView(R.id.llServerDev)
    LinearLayout llServerDev;

    @BindView(R.id.imageLogo)
    ImageView imageLogo;

    @BindView(R.id.btnGetLKPUser)
    Button btnGetLKPUser;

    @BindView(R.id.etServerDevIP)
    EditText etServerDevIP;

    @BindView(R.id.etServerDevPort)
    EditText etServerDevPort;

    @BindView(R.id.tvVersion)
    TextView tvVersion;

    @BindView(R.id.tvDebug)
    TextView tvDebug;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    public APInterface getAPIInterface() {
        return
                APIClientBuilder.create(APInterface.class, Utility.buildUrl(Utility.getServerID(spServers.getSelectedItem().toString())));
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        Date loginDate = Storage.getPrefAsDate(Storage.KEY_LOGIN_DATE, null);
//        Date loginDate = (Date) Storage.getPreference(Storage.KEY_LOGIN_DATE, null);
//        String loginDate = Storage.getPreference(getApplicationContext(), Storage.KEY_LOGIN_DATE);

        if (loginDate != null) {
//        if (!TextUtils.isEmpty(loginDate)) {
//            LoginInfo prevUser = (LoginInfo) Storage.getPreference(Storage.KEY_USER, null);
//            UserData prevUser = (UserData) Storage.getObjPreference(getApplicationContext(), Storage.KEY_USER, UserData.class);
            String userId = Storage.getPref(Storage.KEY_USERID, null);
            String userPwd = Storage.getPref(Storage.KEY_PASSWORD, null);

//            if (prevUser != null)
            if (userId != null && userPwd != null)
                loginOffline(userId, userPwd);
//                loginOffline(prevUser.getUserId(), prevUser.getUserPwd());

        } else {
            boolean b = DataUtil.isMasterDataDownloaded(this, this.realm);
        }

        tilUsername.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_left));
        tilPassword.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_right));


        DeviceUtil.askPhoneAndStoragePermission(this);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

//        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;

        tvVersion.setText("v" + versionName);


        if (getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE) {
            imageLogo.setVisibility(View.GONE);
        }

        if (Utility.DEVELOPER_MODE) {
            btnGetLKPUser.setVisibility(View.VISIBLE);
            tvDebug.setVisibility(View.VISIBLE);
        }

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        List<String> servers = new ArrayList<>();
        for (int i = 0; i < Utility.SERVERS.length; i++) {

            if (!Utility.DEVELOPER_MODE) {
                if (Utility.SERVERS[i][0].startsWith("local")
                        || Utility.SERVERS[i][0].startsWith("dev-")
                        )
                    continue;
            }

            servers.add(Utility.SERVERS[i][0]);
        }

        ServerAdapter arrayAdapter = new ServerAdapter(this, android.R.layout.simple_spinner_item, servers);
        spServers.setAdapter(arrayAdapter);
        spServers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String itemAtPosition = (String) adapterView.getItemAtPosition(i);

                if (itemAtPosition.startsWith("dev")) {
                    llServerDev.setVisibility(View.VISIBLE);
                } else
                    llServerDev.setVisibility(View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

//        LoginInfo prevUser = (LoginInfo) Storage.getPreference(Storage.KEY_USER, null);

//        UserData prevUserData = (UserData) Storage.getObjPreference(getApplicationContext(), Storage.KEY_USER, UserData.class);

        String userId = Storage.getPref(Storage.KEY_USERID, null);

        if (userId != null)
            mUserNameView.setText(userId);

        String password_rem = Storage.getPref(Storage.KEY_PASSWORD_REMEMBER, null);
//        String password_rem = (String) Storage.getPreference(Storage.KEY_PASSWORD_REMEMBER, null);
//            String password_rem = Storage.getPreference(getApplicationContext(), Storage.KEY_PASSWORD_REMEMBER);

        if (!TextUtils.isEmpty(password_rem)) {
            if (password_rem.equalsIgnoreCase("true")) {
                cbRememberPwd.setChecked(true);

                // load user & password
                String lastPwd = Storage.getPref(Storage.KEY_PASSWORD_LAST, null);
//                String lastPwd = Storage.getPreference(getApplicationContext(), Storage.KEY_PASSWORD_LAST);

                String username = mUserNameView.getText().toString();
                if (userId != null && username.equalsIgnoreCase(userId)) {
                    mPasswordView.setText(lastPwd);
                } else
                    mPasswordView.setText(null);
            }
        }

        String ipDev = Storage.getPref(Storage.KEY_SERVER_DEV_IP, null);
        String portDev = Storage.getPref(Storage.KEY_SERVER_DEV_PORT, null);
//            String ipDev = Storage.getPreference(getApplicationContext(), Storage.KEY_SERVER_DEV_IP);
//            String portDev = Storage.getPreference(getApplicationContext(), Storage.KEY_SERVER_DEV_PORT);

        if (TextUtils.isEmpty(ipDev))
            ipDev = Utility.SERVER_DEV_IP; // Utility.SERVERS[Utility.getServerID(Utility.SERVER_DEV_NAME)][1];

        if (TextUtils.isEmpty(portDev))
            portDev = Utility.SERVER_DEV_PORT; // SERVERS[Utility.getServerID(Utility.SERVER_DEV_NAME)][2];

        etServerDevIP.setText(ipDev);
        etServerDevPort.setText(portDev);

        int x = Storage.getPrefAsInt(Storage.KEY_SERVER_ID, new Integer(0));
//        int x = Storage.getPreferenceAsInt(getApplicationContext(), Storage.KEY_SERVER_ID, 0);
        Utility.setSpinnerAsString(spServers, Utility.getServerName(x));

    }

    private void signIn() {
        try {

            String selectedServer = Utility.getServerName(spServers.getSelectedItemPosition());
            if (selectedServer.startsWith("dev")) {
                int id = Utility.getServerID(selectedServer);
                Utility.SERVERS[id][1] = etServerDevIP.getText().toString();
                Utility.SERVERS[id][2] = etServerDevPort.getText().toString();
            }

            final ProgressDialog mProgressDialog = Utility.createAndShowProgressDialog(this, "Checking version...");

            isLatestVersion(new OnSuccessError() {
                @Override
                public void onSuccess(String msg) {

                    Utility.dismissDialog(mProgressDialog);

                    try {
                        attemptLogin();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Throwable throwable) {
                    Utility.dismissDialog(mProgressDialog);

                    Utility.throwableHandler(LoginActivity.this, throwable, true);

                }

                @Override
                public void onSkip() {
                    Utility.dismissDialog(mProgressDialog);

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

    }

    @OnClick(R.id.sign_in_button)
    public void onSignInClick() {

        if (RootUtil.isDeviceRooted()) {
            if (!Utility.DEVELOPER_MODE) {
                Utility.showDialog(this, "Rooted", getString(R.string.error_rooted));
                resetData();
                return;
            }
        }

        DeviceUtil.askCameraContactsAndLocationPermission(this);

//        signIn();
    }

    private void attemptLogin() throws Exception {
/*
        disabled due to complaints
        Date sysDate = new Date();
        if (sysDate.after(Utility.convertStringToDate(Utility.DATE_EXPIRED_YYYYMMDD, "yyyyMMdd"))) {
//            Utility.createAndShowProgressDialog(this, "Expired App", "This application version is expired. Please update from the latest");
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

        Storage.savePref(Storage.KEY_SERVER_ID, String.valueOf(Utility.getServerID(spServers.getSelectedItem().toString())));
//        Storage.savePreferenceAsInt(Storage.KEY_SERVER_ID, Utility.getServerID(spServers.getSelectedItem().toString()));


//        Storage.savePreferenceAsInt(getApplicationContext(), Storage.KEY_SERVER_ID, Utility.getServerID(spServers.getSelectedItem().toString()));

//        UserData prevUser = (UserData) Storage.getPreference(Storage.KEY_USER, null);
//        UserData prevUser = (UserData) Storage.getObjPreference(getApplicationContext(), Storage.KEY_USER, UserData.class);
        String prevUserId = Storage.getPref(Storage.KEY_USERID, null);

        if (prevUserId == null) {

            loginOnline(username, password);

        } else {
            if (!username.equalsIgnoreCase(prevUserId)) {

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

                Date lastLogin = Storage.getPrefAsDate(Storage.KEY_LOGIN_DATE, null);
//                Date lastMorning = (Date) Storage.getObjPreference(getApplicationContext(), Storage.KEY_USER_LAST_DAY, Date.class);
                if (lastLogin == null) {
                    loginOffline(username, password);
                } else if (Utility.isSameDay(lastLogin, new Date())) {
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

            if (listener != null) {
                listener.onSkip();
            }

            return;
        }

        int versionCode = BuildConfig.VERSION_CODE;
        final String versionName = BuildConfig.VERSION_NAME;

        APInterface api = getAPIInterface();

        Call<ResponseBody> call = api.getAppVersion(versionName);

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

                                // WARNING ! krn update aplikasi bisa mengakibatkan data transaksi realm hilang, maka dianggap sukses jika ada transaksi
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

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 3;   // need to allow demo as password
    }

    private void retrieveServerInfo(final OnPostRetrieveServerInfo listener) throws Exception {

        APInterface api = getAPIInterface();

        Call<ResponseServerInfo> call = api.getServerInfo();
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
            }
        });

    }

    private void loginOnline(final String username, final String password) {

        boolean isOnline = NetUtil.isConnected(this);

        if (!isOnline) {

            /*
                hardcode, if user is "demo"
                make sure to use demo, flightmode is activated
                surely you can only see dummy data in offline mode.
                */
            if (UserUtil.userIsDemo(username, password)) {
                final ServerInfo si = new ServerInfo();
                si.setServerDate(new Date());

                LoginActivity.this.realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.delete(ServerInfo.class);
                        realm.copyToRealm(si);

                    }
                });

                // to replace demouser
                DemoUtil.buildDemoLoginInfo();
                Storage.savePrefAsDate(Storage.KEY_LOGIN_DATE, new Date());

                //will be removed soon
//                Storage.saveObjPreference(getApplicationContext(), Storage.KEY_USER, DemoUtil.buildDemoUser());

                //will be removed soon
                // able to control nextday shpuld re-login to server
//                Storage.saveObjPreference(getApplicationContext(), Storage.KEY_USER_LAST_DAY, new Date());

                // final check
                startMainActivity();
            } else
                Utility.showDialog(this, "Offline", getString(R.string.error_offline));

            return;
        }

        Utility.disableScreen(this, true);

        final ProgressDialog mProgressDialog = Utility.createAndShowProgressDialog(this, "Logging in... ");

        try {

            retrieveServerInfo(new OnPostRetrieveServerInfo() {
                @Override
                public void onSuccess(ServerInfo serverInfo) {
                    APInterface api = getAPIInterface();

                    RequestLogin request = new RequestLogin();
                    request.setId(username);
                    request.setPwd(password);

                    fillRequest(Utility.ACTION_LOGIN, request);

                    Call<ResponseLogin> call = api.login(request);
                    call.enqueue(new Callback<ResponseLogin>() {
                        @Override
                        public void onResponse(Call<ResponseLogin> call, Response<ResponseLogin> response) {
                            Utility.disableScreen(LoginActivity.this, false);

                            if (response.isSuccessful()) {

                                final ResponseLogin respLogin = response.body();
                                Log.e("eric.onResponse", respLogin.toString());

                                if (respLogin.getError() != null) {
                                    Utility.showDialog(LoginActivity.this, "Error (" + respLogin.getError().getErrorCode() + ")", respLogin.getError().getErrorDesc());

                                    Utility.dismissDialog(mProgressDialog);

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

                                        }
                                    }, new Realm.Transaction.OnSuccess() {
                                        @Override
                                        public void onSuccess() {
                                            // to replace Storage.KEY_USER
                                            DataUtil.convertUserDataToLoginInfo(respLogin.getData());
                                            Storage.savePrefAsDate(Storage.KEY_LOGIN_DATE, new Date());

                                            Utility.dismissDialog(mProgressDialog);

//                                            Storage.savePreference(Storage.KEY_USER, respLogin.getData());

                                            //will be removed soon
//                                            Storage.saveObjPreference(getApplicationContext(), Storage.KEY_USER, respLogin.getData());

                                            //will be removed soon
                                            // able to control nextday shpuld re-login to server
//                                            Storage.saveObjPreference(getApplicationContext(), Storage.KEY_USER_LAST_DAY, new Date());

                                            // final check
                                            startMainActivity();

                                        }
                                    }, new Realm.Transaction.OnError() {
                                        @Override
                                        public void onError(Throwable error) {
                                            Utility.dismissDialog(mProgressDialog);

                                            Utility.showDialog(LoginActivity.this, "Database Problem", getString(R.string.error_contact_admin));
                                        }
                                    });
                                }
                            } else {
                                Utility.dismissDialog(mProgressDialog);

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

                            Utility.dismissDialog(mProgressDialog);

                            Utility.showDialog(LoginActivity.this, "Server Problem", t.getMessage());
                        }
                    });

                }

                @Override
                public void onFailure(Throwable throwable) {
                    Utility.disableScreen(LoginActivity.this, false);

                    Utility.dismissDialog(mProgressDialog);

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Utility.dismissDialog(mProgressDialog);
        }

    }

    private void startMainActivity() {
        final String ipDev = etServerDevIP.getText().toString();
        final String portDev = etServerDevPort.getText().toString();

        Storage.savePref(Storage.KEY_SERVER_DEV_IP, ipDev);
        Storage.savePref(Storage.KEY_SERVER_DEV_PORT, portDev);

        // 31may17 will be replace see loginInfo.serverDevIP
//        Storage.savePreference(getApplicationContext(), Storage.KEY_SERVER_DEV_IP, ipDev);
        // 31may17 will be replace see loginInfo.serverDevPort
//        Storage.savePreference(getApplicationContext(), Storage.KEY_SERVER_DEV_PORT, portDev);

        if (cbRememberPwd.isChecked()) {
            final String password = mPasswordView.getText().toString();

            String cbRememberPwds = String.valueOf(cbRememberPwd.isChecked());

            Storage.savePref(Storage.KEY_PASSWORD_REMEMBER, cbRememberPwds);
            Storage.savePref(Storage.KEY_PASSWORD_LAST, password);

            // 31may17 will be replace see loginInfo.passwordRemember
//            Storage.savePreference(getApplicationContext(), Storage.KEY_PASSWORD_REMEMBER, cbRememberPwds);
            // 31may17 will be replace see loginInfo.passwordLast
//            Storage.savePreference(getApplicationContext(), Storage.KEY_PASSWORD_LAST, password);

        }
        startActivity(new Intent(getApplicationContext(), MainActivity.class));

        finish();
    }

    private void loginOffline(@NonNull String username, @NonNull String password) {

        Date logoutDate = Storage.getPrefAsDate(Storage.KEY_LOGOUT_DATE, null);

//        String logoutDate = Storage.getPreference(getApplicationContext(), Storage.KEY_LOGOUT_DATE);

        if (logoutDate == null) {
//        if (TextUtils.isEmpty(logoutDate)) {
            resetData();

            Utility.showDialog(this, "Logout Warning", getString(R.string.error_uncleaned_exit));
            return;
        }

        String userId = Storage.getPref(Storage.KEY_USERID, null);
        String userPwd = Storage.getPref(Storage.KEY_PASSWORD, null);
//        LoginInfo user = (LoginInfo) Storage.getPreference(Storage.KEY_USERID, null);
//        UserData user = (UserData) Storage.getObjPreference(getApplicationContext(), Storage.KEY_USER, UserData.class);

        if (userId != null && userPwd != null && userId.equals(username) && userPwd.equals(password)) {
            startMainActivity();
        } else {
            Utility.showDialog(this, "Invalid Login", getString(R.string.error_invalid_login));
        }

    }

    @OnClick(R.id.btnGetLKPUser)
    public void onClickGetLKPUser() {

        final ProgressDialog mProgressDialog = Utility.createAndShowProgressDialog(this, "Get Any LKP User...");

        APInterface api = getAPIInterface();

        Call<ResponseUserPwd> call = api.getAnyLKPUser();
        call.enqueue(new Callback<ResponseUserPwd>() {
            @Override
            public void onResponse(Call<ResponseUserPwd> call, Response<ResponseUserPwd> response) {

                Utility.dismissDialog(mProgressDialog);

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
                Utility.dismissDialog(mProgressDialog);
            }
        });

    }


    @OnLongClick(R.id.rlRadana)
    public boolean onLongClickRadana(View view) {
        Toast.makeText(this, getDebugInfo(), Toast.LENGTH_SHORT).show();

        //TODO: not working to stop animation?
        //        view.setAnimation(null);
//        View v = findViewById(R.id.rlRadana);
//        v.clearAnimation();
        return true;
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case DeviceUtil.MY_PERMISSIONS_REQUEST_PHONE_AND_STORAGE: {
                if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    if (Utility.DEVELOPER_MODE) {
                        // need permission phone state
                        tvDebug.setText(getDebugInfo());
                    }

                } else {
                    finish();
                }

                return;
            }
            case DeviceUtil.MY_PERMISSIONS_REQUEST_CAMERA_AND_CONTACTS_AND_LOCATION: {
                if (grantResults.length > 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    signIn();
                } else {
                    Toast.makeText(this, getString(R.string.error_permit_mandatory), Toast.LENGTH_LONG).show();
                }

                return;
            }
        }
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
            tv.setPadding(10, 20, 10, 20);
            tv.setTextColor(Color.BLACK);
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
