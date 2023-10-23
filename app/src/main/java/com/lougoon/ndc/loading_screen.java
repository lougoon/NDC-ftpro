package com.lougoon.ndc;

import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class loading_screen extends AppCompatActivity {

    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_screen);

        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.anim_logo);

        VideoView simpleVideoView = findViewById(R.id.videoView); // initiate a video view
        simpleVideoView.setVideoURI(uri);
        simpleVideoView.start();

        /*
         On crée les instructions pour rediriger
        Runnable runnable = () -> {
            Intent intent = new Intent(getApplicationContext(),Login_activity.class);
            startActivity(intent);
            finish();
        };

        // Le faire avec un délai (en milli secondes)
        new Handler().postDelayed(runnable,2000);

         */
    }
}