package com.lougoon.ndc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EndgameBird extends AppCompatActivity {
    private TextView score,bestscore;
    private Button restart,pub,exit;
    private RewardedAd rewardedAd;
    private final String TAG = "MainActivity";
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_endgame_bird);
        score = findViewById(R.id.score);
        bestscore = findViewById(R.id.best_score);
        restart = findViewById(R.id.restart_btn);
        pub = findViewById(R.id.pub);
        exit = findViewById(R.id.exit);
        score.setText("Score : " + Package_ndc.get_in_preference(this,"last_score"));
        bestscore.setText("Bestscore : "+ Package_ndc.get_in_preference(this,"bestscore"));
        pub.setText("Loading...");

        restart.setOnClickListener(view -> {
            startActivity(new Intent(this,bird_game.class));
            finish();
        });

        exit.setOnClickListener(view -> {
            startActivity(new Intent(this,MainActivity.class));
            finish();
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(this, "ca-app-pub-3940256099942544/5224354917",
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.d(TAG, loadAdError.toString());
                        rewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd ad) {
                        rewardedAd = ad;
                        Log.d(TAG, "Ad was loaded.");
                        rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
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
                                rewardedAd = null;
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when ad fails to show.
                                Log.e(TAG, "Ad failed to show fullscreen content.");
                                rewardedAd = null;
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
                        // pub pret pour etre regarde
                        pub.setText("pub +20");
                    }
                });

        pub.setOnClickListener(view -> {
            if (rewardedAd != null) {
                Activity activityContext = EndgameBird.this;
                rewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                        // Handle the reward.
                        Log.d(TAG, "The user earned the reward.");
                        pub.setVisibility(View.GONE);
                        int rewardAmount = rewardItem.getAmount();
                        int last_score_int = Integer.parseInt(Package_ndc.get_in_preference(EndgameBird.this,"last_score"));
                        int new_score = rewardAmount + last_score_int;
                        save_best_score(new_score);
                        score.setText("Score : "+ new_score);
                    }
                });
            } else {
                Log.d(TAG, "The rewarded ad wasn't ready yet.");
            }
        });
    }
    private void save_best_score(int score) {
        String bestscore_string = Package_ndc.get_in_preference(this, "bestscore");

        // Gestion du cas où le meilleur score n'a pas encore été enregistré
        if (bestscore_string == null) {
            Package_ndc.save_in_preference(EndgameBird.this, Integer.toString(score), "bestscore");
            save_bScore_firebase(score);
            bestscore.setText("Best score : " + score);
        } else {
            int bestscore_int = Integer.parseInt(bestscore_string);

            // Mettre à jour le meilleur score si le nouveau score est plus élevé
            if (score > bestscore_int) {
                Package_ndc.save_in_preference(EndgameBird.this, Integer.toString(score), "bestscore");
                save_bScore_firebase(score);
                bestscore.setText("Best score : " + score);
            }
        }
    }
    private void save_bScore_firebase(int score) {
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