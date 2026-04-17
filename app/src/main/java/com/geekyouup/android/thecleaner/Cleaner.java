package com.geekyouup.android.thecleaner;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;

public class Cleaner extends Activity {
    private RelativeLayout mView;
    private WallpaperManager mWPM;

    float xPos = 0;
    float xWP = 0.0f;
    float yPos = 0;
    float yWP = 0.5f;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().setDecorFitsSystemWindows(false);
            android.view.WindowInsetsController controller = getWindow().getInsetsController();
            if (controller != null) {
                controller.hide(android.view.WindowInsets.Type.systemBars());
                controller.setSystemBarsBehavior(
                        android.view.WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } else {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }

        mView = (RelativeLayout) findViewById(R.id.mainview);
        mView.setFocusableInTouchMode(false);
        mWPM = WallpaperManager.getInstance(this);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        // Sync our local default offset with the system wallpaper manager as soon as
        // the window is attached.
        // This prevents the wallpaper from snapping/jumping when the user first touches
        // the screen.
        if (mView != null && mView.getWindowToken() != null && mWPM != null) {
            mWPM.setWallpaperOffsets(mView.getWindowToken(), xWP, yWP);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        AlphaAnimation aa = new AlphaAnimation(1.0f, 0.0f);
        aa.setDuration(1000);
        mView.startAnimation(aa);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            xPos = event.getX();
            yPos = event.getY();
            // kill app if touched in bottom right corner
            if (xPos > mView.getWidth() - 50 && event.getY() > mView.getHeight() - 50)
                finish();
            else {
                mWPM.sendWallpaperCommand(mView.getWindowToken(), WallpaperManager.COMMAND_TAP, (int) xPos,
                        (int) event.getY(), 0, null);
            }
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            float width = mView.getWidth();
            if (width <= 0) width = 1080f; // fallback
            xWP = xWP + (xPos - event.getX()) / width;
            xPos = event.getX();
            if (xWP < 0)
                xWP = 0;
            if (xWP > 1)
                xWP = 1;

            float height = mView.getHeight();
            if (height <= 0) height = 1920f; // fallback
            yWP = yWP + (yPos - event.getY()) / height;
            yPos = event.getY();
            if (yWP < 0)
                yWP = 0;
            if (yWP > 1)
                yWP = 1;
            mWPM.setWallpaperOffsets(mView.getWindowToken(), xWP, yWP);
        }

        return super.onTouchEvent(event);
    }

}