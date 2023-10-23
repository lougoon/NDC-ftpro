package com.lougoon.ndc;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class EndgameBird extends AppCompatActivity {
    private TextView score,bestscore;
    private Button restart,pub,exit;

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

        restart.setOnClickListener(view -> {
            startActivity(new Intent(this,bird_game.class));
            finish();
        });

        exit.setOnClickListener(view -> {
            startActivity(new Intent(this,MainActivity.class));
            finish();
        });
    }
}