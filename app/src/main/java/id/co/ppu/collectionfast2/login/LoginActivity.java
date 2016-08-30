package id.co.ppu.collectionfast2.login;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.co.ppu.collectionfast2.MainActivity;
import id.co.ppu.collectionfast2.R;
import id.co.ppu.collectionfast2.pojo.User;
import id.co.ppu.collectionfast2.rest.ApiInterface;
import id.co.ppu.collectionfast2.rest.ServiceGenerator;
import id.co.ppu.collectionfast2.rest.request.RequestLogin;
import id.co.ppu.collectionfast2.rest.response.ResponseLogin;
import id.co.ppu.collectionfast2.util.NetUtil;
import id.co.ppu.collectionfast2.util.Storage;
import id.co.ppu.collectionfast2.util.Utility;
import io.realm.Realm;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    // UI references.
    @BindView(R.id.username)
    AutoCompleteTextView mUserNameView;

    @BindView(R.id.password)
    EditText mPasswordView;

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
                    attemptLogin();
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

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.realm.close();
        this.realm = null;
    }

    @OnClick(R.id.sign_in_button)
    public void onSignInClick() {
        attemptLogin();
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
    private void attemptLogin() {
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

        //check db local apakah usernya ada ?
        long countUser = this.realm.where(User.class).equalTo("userName", username).count();

        if (countUser < 1) {
            boolean isOnline = NetUtil.isConnected(this);

            if (isOnline) {
                loginOnline(username, password);
            } else {
                Utility.showDialog(this, "Offline", getString(R.string.error_offline));
            }
        } else {
            loginOffline(username, password);
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

    private void loginOnline(String username, String password) {
        Utility.disableScreen(this, true);

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Logging in...");
        mProgressDialog.show();

        ApiInterface loginService =
                ServiceGenerator.createService(ApiInterface.class, Utility.buildUrl());

        RequestLogin request = new RequestLogin();
        request.setId(username);
        request.setPwd(password);

        Call<ResponseLogin> call = loginService.login(request);
        call.enqueue(new Callback<ResponseLogin>() {
            @Override
            public void onResponse(Call<ResponseLogin> call, Response<ResponseLogin> response) {
                Utility.disableScreen(LoginActivity.this, false);

                if (response.isSuccessful()) {

                    ResponseLogin respLogin = response.body();
                    Log.e("eric.onResponse", respLogin.toString());

                    if (respLogin.getError() != null) {
                        Utility.showDialog(LoginActivity.this, "Error (" + respLogin.getError().getErrorCode() + ")", respLogin.getError().getErrorDesc());

                    } else {
                        Utility.saveObjPreference(getApplicationContext(), "user", respLogin.getData());

//                            tvGetLogin.setText(Utility.getObjPreference(getApplicationContext(), "user", User.class).toString() + "\n\n" + sw.stopAndGetAsString());
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                } else {
                    int statusCode = response.code();

                    // handle request errors yourself
                    ResponseBody errorBody = response.errorBody();

                    try {
                        Utility.showDialog(LoginActivity.this, "Server Problem (" + statusCode + ")", errorBody.string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
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

    }
}
