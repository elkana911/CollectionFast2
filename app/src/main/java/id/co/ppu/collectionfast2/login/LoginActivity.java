package id.co.ppu.collectionfast2.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.widget.EditText;
import android.widget.TextView;

import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.co.ppu.collectionfast2.MainActivity;
import id.co.ppu.collectionfast2.R;
import id.co.ppu.collectionfast2.pojo.MstSecUser;
import id.co.ppu.collectionfast2.pojo.MstUser;
import id.co.ppu.collectionfast2.rest.ApiInterface;
import id.co.ppu.collectionfast2.rest.ServiceGenerator;
import id.co.ppu.collectionfast2.rest.request.RequestLogin;
import id.co.ppu.collectionfast2.rest.response.ResponseLogin;
import id.co.ppu.collectionfast2.rest.response.ResponseServerInfo;
import id.co.ppu.collectionfast2.util.DataUtil;
import id.co.ppu.collectionfast2.util.NetUtil;
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

    @BindView(R.id.password)
    EditText mPasswordView;

    @BindView(R.id.spServers)
    MaterialBetterSpinner spServers;

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

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
        spServers.setText(Utility.getServerName(x));

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

        try {
            DataUtil.retrieveMasterFromServerBackground(this.realm, getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.realm.close();
        this.realm = null;
    }

    @OnClick(R.id.sign_in_button)
    public void onSignInClick() {
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
    private void attemptLogin() throws Exception{

        Date sysDate = new Date();
        if (sysDate.after(Utility.convertStringToDate(Utility.DATE_EXPIRED_YYYYMMDD, "yyyyMMdd"))){
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

        Storage.savePreferenceAsInt(getApplicationContext(), Storage.KEY_SERVER_ID, Utility.getServerID(spServers.getText().toString()));

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

            // enable this code on production
            Date lastMorning = (Date)Storage.getObjPreference(getApplicationContext(), Storage.KEY_USER_LAST_MORNING, Date.class);
            if (lastMorning == null) {
                loginOffline(username, password);
            }else if (Utility.isSameDay(lastMorning, new Date())) {
                loginOffline(username, password);
            }else
                // reset data first ?
                loginOnline(username, password);
        }

    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    private void retrieveServerInfo() throws Exception{
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
                            }
                        });

                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseServerInfo> call, Throwable t) {
                throw new RuntimeException("Failure retrieve server information");
            }
        });

    }

    private void loginOnline(String username, String password){
        Utility.disableScreen(this, true);

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Logging in...");
        mProgressDialog.show();

        try {
            retrieveServerInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ApiInterface loginService =
                ServiceGenerator.createService(ApiInterface.class, Utility.buildUrl(Utility.getServerID(spServers.getText().toString())));

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
                                realm.copyToRealm(respLogin.getData().getActiveContracts());

                            }
                        }, new Realm.Transaction.OnSuccess() {
                            @Override
                            public void onSuccess() {
                                if (mProgressDialog.isShowing())
                                    mProgressDialog.dismiss();

                                Storage.saveObjPreference(getApplicationContext(), "user", respLogin.getData());

                                // able to control nextday shpuld re-login to server
                                Storage.saveObjPreference(getApplicationContext(), "lastMorning", new Date());

                                // final check
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
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

    private void loginOffline(String username, String password) {

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Logging in...");
        mProgressDialog.show();

        final MstSecUser usr = this.realm.where(MstSecUser.class).contains("userName", username).findFirst();

        if (usr == null || !usr.getUserPwd().equals(password)) {
            Utility.showDialog(this, "Invalid Login", getString(R.string.error_invalid_login));
        } else {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

        if (mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }

    @OnClick(R.id.btnTestGPS)
    public void onClickTestGPS(){


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
            tv.setPadding(10,20,10,20);
            tv.setTextColor(Color.BLACK);
//            tv.setText(list.get(position).getRvbNo());
            tv.setText(list.get(position));
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);

            return tv;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView label = new TextView(this.ctx);
            label.setTextColor(Color.BLACK);
            label.setText(list.get(position));

            return label;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }

}
