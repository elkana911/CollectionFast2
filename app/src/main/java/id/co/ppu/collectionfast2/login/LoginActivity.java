package id.co.ppu.collectionfast2.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

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
import id.co.ppu.collectionfast2.MainActivity;
import id.co.ppu.collectionfast2.R;
import id.co.ppu.collectionfast2.listener.OnPostRetrieveServerInfo;
import id.co.ppu.collectionfast2.pojo.ServerInfo;
import id.co.ppu.collectionfast2.pojo.master.MstSecUser;
import id.co.ppu.collectionfast2.pojo.master.MstUser;
import id.co.ppu.collectionfast2.rest.ApiInterface;
import id.co.ppu.collectionfast2.rest.ServiceGenerator;
import id.co.ppu.collectionfast2.rest.request.RequestLogin;
import id.co.ppu.collectionfast2.rest.response.ResponseLogin;
import id.co.ppu.collectionfast2.rest.response.ResponseServerInfo;
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
public class LoginActivity extends AppCompatActivity {

    // UI references.
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

    private Realm realm;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        if (getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE) {
            imageLogo.setVisibility(View.GONE);
        }

        this.realm = Realm.getDefaultInstance();

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    try {
                        attemptLogin();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }
                return false;
            }
        });

        List<String> servers = new ArrayList<>();
        for (int i = 0; i < Utility.servers.length; i++) {
            servers.add(Utility.servers[i][0]);
        }

        ServerAdapter arrayAdapter = new ServerAdapter(this, android.R.layout.simple_spinner_item, servers);
        spServers.setAdapter(arrayAdapter);

        int x = Storage.getPreferenceAsInt(getApplicationContext(), Storage.KEY_SERVER_ID, 0);
        Utility.setSpinnerAsString(spServers, Utility.getServerName(x));

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

        boolean b = DataUtil.isMasterDataDownloaded(this, this.realm);


        String lastUsername = Storage.getPreference(getApplicationContext(), "lastUser");
        mUserNameView.setText(lastUsername);

        String password_rem = Storage.getPreference(getApplicationContext(), "password_rem");
        if (!TextUtils.isEmpty(password_rem)) {
            if (password_rem.equalsIgnoreCase("true")) {
                cbRememberPwd.setChecked(true);

                // load user & password
                String lastPwd = Storage.getPreference(getApplicationContext(), "lastPwd");

                if (mUserNameView.getText().toString().equalsIgnoreCase(lastUsername)) {
                    mPasswordView.setText(lastPwd);
                }else
                    mPasswordView.setText(null);
            }
        }

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.realm.close();
        this.realm = null;
    }

    @OnClick(R.id.sign_in_button)
    public void onSignInClick() {

        if (RootUtil.isDeviceRooted()) {
            Utility.showDialog(this, "Rooted", "This device is rooted. Unable to open application.");
            return;
        }

        try {
            attemptLogin();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        startActivity(new Intent(this, MainActivity.class));

    }

    @OnClick(R.id.sign_up_button)
    public void onSignUpClick() {
//        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() throws Exception {

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

            // TODO: enable this code on production
            Date lastMorning = (Date) Storage.getObjPreference(getApplicationContext(), Storage.KEY_USER_LAST_DAY, Date.class);
            if (lastMorning == null) {
                loginOffline(username, password);
            } else if (Utility.isSameDay(lastMorning, new Date())) {
                loginOffline(username, password);
            } else
                loginOnline(username, password);
        }

    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    private void retrieveServerInfo(final OnPostRetrieveServerInfo listener) throws Exception {
        ApiInterface fastService =
                ServiceGenerator.createService(ApiInterface.class, Utility.buildUrl(Storage.getPreferenceAsInt(getApplicationContext(), Storage.KEY_SERVER_ID, 0)));

        Call<ResponseServerInfo> call = fastService.getServerInfo();
        call.enqueue(new Callback<ResponseServerInfo>() {
            @Override
            public void onResponse(Call<ResponseServerInfo> call, Response<ResponseServerInfo> response) {
                if (response.isSuccessful()) {
                    final ResponseServerInfo responseServerInfo = response.body();

                    if (responseServerInfo.getError() == null) {
                        LoginActivity.this.realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                realm.copyToRealm(responseServerInfo.getData());

                                if (listener != null) {
                                    listener.onSuccess(responseServerInfo.getData());
                                }
                            }
                        });

                    }

                }
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
                    ApiInterface loginService =
                            ServiceGenerator.createService(ApiInterface.class, Utility.buildUrl(Utility.getServerID(spServers.getSelectedItem().toString())));

                    RequestLogin request = new RequestLogin();
                    request.setId(username);
                    request.setPwd(password);

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
                                            realm.copyToRealm(respLogin.getData().getUser());
                                            realm.copyToRealm(respLogin.getData().getSecUser());

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
        }

    }

    private void startMainActivity(){
        final String username = mUserNameView.getText().toString();
        Storage.savePreference(getApplicationContext(), "lastUser",  username);
        if (cbRememberPwd.isChecked()) {
            final String password = mPasswordView.getText().toString();

            String cbRememberPwds = String.valueOf(cbRememberPwd.isChecked());
            Storage.savePreference(getApplicationContext(), "password_rem", cbRememberPwds);
            Storage.savePreference(getApplicationContext(), "lastPwd", password);

        }
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
    private void loginOffline(String username, String password) {

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
