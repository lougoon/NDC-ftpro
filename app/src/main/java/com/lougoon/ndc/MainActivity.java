package com.lougoon.ndc;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    private ImageView homebtn,linkbtn,picturebtn,gamebtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        homebtn = findViewById(R.id.btn_home_fragment);
        linkbtn = findViewById(R.id.btn_link_fragment);
        picturebtn = findViewById(R.id.btn_picture_fragment);
        gamebtn = findViewById(R.id.btn_game_fragment);

        replacefragment(new home_fragment(),homebtn);

        homebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                replacefragment(new home_fragment(),homebtn);
            }
        });

        linkbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                replacefragment(new link_fragment(),linkbtn);
            }
        });

        picturebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                replacefragment(new picture_fragment(),picturebtn);

            }
        });

        gamebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                replacefragment(new game_fragment(),gamebtn);

            }
        });
    }




    private void replacefragment(Fragment fragment,ImageView btn) {

        homebtn.setImageResource(R.drawable.home_btn);
        linkbtn.setImageResource(R.drawable.link_btn);
        picturebtn.setImageResource(R.drawable.camera_btn);
        gamebtn.setImageResource(R.drawable.game_btn);

        if(btn == homebtn){
            homebtn.setImageResource(R.drawable.home_btn_2);
        }

        if(btn == linkbtn){
            linkbtn.setImageResource(R.drawable.link_btn_2);
        }

        if(btn == picturebtn){
            picturebtn.setImageResource(R.drawable.camera_btn_2);
        }

        if(btn == gamebtn){
            gamebtn.setImageResource(R.drawable.game_btn_2);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_space,fragment);
        fragmentTransaction.commit();
    }

}