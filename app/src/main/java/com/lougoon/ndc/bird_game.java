package com.lougoon.ndc;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class bird_game extends AppCompatActivity {

    private Handler handler = new Handler();
    private FirebaseAuth auth;
    private Boolean game_already_started,compteur,distance,pub_load;
    private TextView score_text,BestScore;
    private InterstitialAd mInterstitialAd;
    private static final int DELAY_MILLIS = 10;
    private static final String TAG = "pub";
    private int gravity = 1;
    private ProgressBar progressBar;
    private int score,screenWidth,screenHeight,velocity,flappyY,milieu_screen,milieu_screen_h;
    private ImageView flappy,cara_top_1,cara_bottom_1,cara_top_2,cara_bottom_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bird_game);
        BestScore = findViewById(R.id.best_score);
        progressBar = findViewById(R.id.progressBar);
        ConstraintLayout backgroundView = findViewById(R.id.view_clic);
        auth = FirebaseAuth.getInstance();
        flappy = findViewById(R.id.flappy);
        cara_top_1 = findViewById(R.id.cara_top_1);
        cara_bottom_1 = findViewById(R.id.cara_bottom_1);
        cara_top_2 = findViewById(R.id.cara_top_2);
        cara_bottom_2 = findViewById(R.id.cara_bottom_2);
        score_text = findViewById(R.id.score_text);
        game_already_started = false;
        // preparation des pubs
        pub_load = false;
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {}
        });
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                            @Override
                            public void onAdClicked() {
                                // Called when a click is recorded for an ad.
                                Log.d(TAG, "Ad was clicked.");
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when ad is dismissed.
                                // Set the ad reference to null so you don't show the ad a second time.
                                Log.d(TAG, "Ad dismissed fullscreen content.");
                                mInterstitialAd = null;
                                startActivity(new Intent(bird_game.this,EndgameBird.class));
                                finish();
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when ad fails to show.
                                Log.e(TAG, "Ad failed to show fullscreen content.");
                                mInterstitialAd = null;
                            }

                            @Override
                            public void onAdImpression() {
                                // Called when an impression is recorded for an ad.
                                Log.d(TAG, "Ad recorded an impression.");
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when ad is shown.
                                Log.d(TAG, "Ad showed fullscreen content.");
                            }
                        });
                        Log.i(TAG, "onAdLoaded");
                        progressBar.setVisibility(View.GONE);
                        BestScore.setVisibility(View.VISIBLE);
                        flappy.setVisibility(View.VISIBLE);
                        score_text.setVisibility(View.VISIBLE);
                        pub_load = true;
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.d(TAG, loadAdError.toString());
                        mInterstitialAd = null;
                    }
                });
        // fin pub

        if (Package_ndc.get_in_preference(this, "bestscore") != null) {
            String bestscore_string = Package_ndc.get_in_preference(this, "bestscore");
            BestScore.setText("Best score : " + bestscore_string);
        }

        // Get screen dimensions
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        milieu_screen= screenWidth/2;
        screenHeight = displayMetrics.heightPixels;
        milieu_screen_h = screenHeight/2;

        backgroundView.setOnClickListener(view -> {
            if (pub_load) {
                if (!game_already_started) {
                    game_already_started = true;
                    start_game();
                }
                velocity= -19;
            }

        });
    }
    @Override
    protected void onPause() {
        super.onPause();
        stopBackStuffLoop();
    }



    private void set_dimension(ImageView imageView,int height){

        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        layoutParams.width = 200; // en pixels
        layoutParams.height = height; // en pixels
        imageView.setLayoutParams(layoutParams);
    }

    private void start_game(){

        distance = false;
        compteur = false;

        flappyY = milieu_screen_h;
        flappy.setY(milieu_screen_h);

        initialisation_cara(cara_top_1,cara_bottom_1);
        initialisation_cara(cara_top_2,cara_bottom_2);
        startBackStuffLoop();
    }
    private Runnable backStuffRunnable = new Runnable() {
        @Override
        public void run() {
            flappyY += velocity;
            velocity += gravity;
            flappy.setY(flappyY);
            limite_screen();
            zone_point();
            cara_collision(cara_top_1, cara_bottom_1);
            cara_collision(cara_top_2, cara_bottom_2);
            avancer_cara();

            handler.postDelayed(this, DELAY_MILLIS);
        }
    };

    private void startBackStuffLoop() {
        handler.postDelayed(backStuffRunnable, DELAY_MILLIS);
    }

    private void stopBackStuffLoop() {
        handler.removeCallbacks(backStuffRunnable);
    }
    private void zone_point(){
        // hors de zone de point reinitialise la valeur de compteur sur false
        if (flappy.getX()<cara_top_1.getX() || flappy.getX()>cara_top_1.getX() + cara_top_1.getWidth()){
            if (flappy.getX()<cara_top_2.getX() || flappy.getX()>cara_top_2.getX() + cara_top_2.getWidth()){
                compteur = false;
            }
        }
    }
    private void limite_screen(){
        if (flappy.getY()>screenHeight || flappy.getY()<0){
            endgame();
        }

    }

    private void cara_collision(ImageView cara_top,ImageView cara_bottom) {

        if (flappy.getX() < cara_top.getX() + cara_top.getWidth() &&
                flappy.getX() + flappy.getWidth() > cara_top.getX() &&
                (flappy.getY() < cara_top.getHeight() || flappy.getY() + flappy.getHeight() > cara_bottom.getY())) {
            endgame();
        }
        // zone de point on ajoute un point et ensuite on definie compteur false pour plus attribué de point
        if (flappy.getX()>cara_top.getX() && flappy.getX()<cara_top.getX() + cara_top.getWidth()){
            if (!compteur) {
                score+=1;
                score_text.setText("score : "+ score);

            }
            compteur = true;
        }
        if (cara_top.getX()+ 2*cara_top.getWidth()-100<0){
            initialisation_cara(cara_top,cara_bottom);
        }
    }
    private void avancer_cara(){
        if (milieu_screen-cara_bottom_1.getWidth()+100>cara_bottom_1.getX()) {
            distance = true;
        }
        if (distance) {
            cara_top_2.setX(cara_top_2.getX() - calculateVelocity(score));
            cara_bottom_2.setX(cara_top_2.getX());
        }
        cara_top_1.setX(cara_top_1.getX() - calculateVelocity(score));
        cara_bottom_1.setX(cara_top_1.getX());
    }
    private int calculateVelocity(int score) {
        int initialvelocity = 4;
        int add = 1;
        int addspeed = (score / 5 ) * add;
        if (addspeed >= 6) {
            return initialvelocity + 6;
        }
        return initialvelocity + addspeed;
    }
    private void endgame(){

        save_best_score();
        Package_ndc.save_in_preference(this,Integer.toString(score),"last_score");
        stopBackStuffLoop();
        // lancement pub
        Random random = new Random(System.currentTimeMillis());
        int nombreAleatoire = random.nextInt(2) + 1;
        if (mInterstitialAd != null && nombreAleatoire ==1) {
            mInterstitialAd.show(bird_game.this);
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
            startActivity(new Intent(this,EndgameBird.class));
            finish();
        }

    }
    private void initialisation_cara(ImageView cara_top,ImageView cara_bottom){
        Random random = new Random();
        int space = 300;

        // Générer un chiffre aleatoire en fct da la hauteur
        int min = 500;
        int max = screenHeight - 500;
        int random_height = random.nextInt(max - min) + min;

        //cara initialisation
        set_dimension(cara_top,random_height-space);
        set_dimension(cara_bottom,screenHeight-random_height-space);
        cara_bottom.setX(screenWidth+100);
        cara_top.setX(screenWidth+100);
    }
    private void save_best_score() {
        String bestscore_string = Package_ndc.get_in_preference(this, "bestscore");

        // Gestion du cas où le meilleur score n'a pas encore été enregistré
        if (bestscore_string == null) {
            Package_ndc.save_in_preference(this, Integer.toString(score), "bestscore");
            save_bScore_firebase();
            BestScore.setText("Best score : " + score);
        } else {
            int bestscore_int = Integer.parseInt(bestscore_string);

            // Mettre à jour le meilleur score si le nouveau score est plus élevé
            if (score > bestscore_int) {
                Package_ndc.save_in_preference(this, Integer.toString(score), "bestscore");
                save_bScore_firebase();
                BestScore.setText("Best score : " + score);
            }
        }
    }
    private void save_bScore_firebase() {
        auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Créez un objet Map pour stocker les données
        Map<String, Object> userData = new HashMap<>();
        userData.put("best_score", score);

        // Ajoutez le document à la collection "utilisateurs"
        db.collection("utilisateurs").document(userId)
                .update(userData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Document créé avec succès

                        Log.d("ajouter best score", "success");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Gestion des erreurs
                        Log.d("ajouter best score", "onFailure: ");
                    }
                });

    }

}