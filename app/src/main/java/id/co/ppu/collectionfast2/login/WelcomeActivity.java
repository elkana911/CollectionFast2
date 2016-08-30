package id.co.ppu.collectionfast2.login;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntro2Fragment;

import id.co.ppu.collectionfast2.R;

public class WelcomeActivity extends AppIntro2 {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(AppIntro2Fragment.newInstance("Welcome Collector !", Html.fromHtml("<strong>Start your day with a smile...</strong><br><i></i>"), R.drawable.man, Color.WHITE));

        setImageSkipButton(null);
        setProgressButtonEnabled(true);
        setProgressIndicator();

        setGoBackLock(true);
        setSwipeLock(true);
        setNextPageSwipeLock(false);
        showStatusBar(true);

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permisssion in Manifest.
        setVibrate(false);
        setVibrateIntensity(30);

    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        finish();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
    }

}
