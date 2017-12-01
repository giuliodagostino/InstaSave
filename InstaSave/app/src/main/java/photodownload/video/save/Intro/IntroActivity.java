package photodownload.video.save.Intro;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import photodownload.video.save.R;
import photodownload.video.save.Utils.Constants;

/**
 * Created by Tushar on 8/26/2017.
 */

public class IntroActivity extends AppIntro {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(AppIntroFragment.newInstance("STEP 1", "Tap to open instagram.", R.drawable.instaintro1, Color.parseColor("#e91e63")));
        addSlide(AppIntroFragment.newInstance("STEP 2", "Tap on more option icon over instagram post.", R.drawable.instaintro2, Color.parseColor("#4caf50")));
        addSlide(AppIntroFragment.newInstance("STEP 3", "Tap on 'Copy Share URL' option. NOTE : If users profile is private 'Copy share URL' option is not available", R.drawable.instaintro3, Color.parseColor("#00bcd4")));
        addSlide(AppIntroFragment.newInstance("STEP 4", "Yeah! Photo or Video is Automatically Saved to your device.", R.drawable.instaintro4, Color.parseColor("#9c27b0")));
        showSkipButton(false);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {

    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        SharedPreferences.Editor editor = getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE).edit();
        editor.putBoolean("AppIntro", false);
        editor.commit();
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
    }
}
