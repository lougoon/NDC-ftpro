package com.lougoon.ndc;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;


public class game_fragment extends Fragment {

    private View view;
    private Button bird_game_btn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_game_fragment, container, false);
        bird_game_btn = view.findViewById(R.id.bird_game_btn);

        bird_game_btn.setOnClickListener(view ->{

            startActivity(new Intent(view.getContext(),bird_game.class));
            getActivity().finish();

        });
        
        return view;
    }
}