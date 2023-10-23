package com.lougoon.ndc;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;


public class gestion_du_compte_fragment extends Fragment {
    View view;
    private ImageButton quitter_gestion_compte;
    private TextView pseudo_text,email_text;
    private Button supp_compte_btn,deco_btn;
    private ConstraintLayout bar_navig;
    private EditText signup_email,signup_password;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_gestion_du_compte, container, false);

        // mettre le pseudo et email affiché
        pseudo_text = view.findViewById(R.id.pseudo_text);
        email_text = view.findViewById(R.id.email_text);
        bar_navig = getActivity().findViewById(R.id.bloc_bar);
        String email_pref = Package_ndc.get_in_preference(view.getContext(),"EMAIL");
        String pseudo_pref = Package_ndc.get_in_preference(view.getContext(),"PSEUDO");
        pseudo_text.setText(pseudo_pref);
        email_text.setText(email_pref);
        bar_navig.setVisibility(View.INVISIBLE);


        signup_password = view.findViewById(R.id.signup_password);
        supp_compte_btn = view.findViewById(R.id.supp_compte_btn);
        deco_btn = view.findViewById(R.id.deco_btn);
        quitter_gestion_compte = view.findViewById(R.id.quitter_gestion_compte);
        signup_email = view.findViewById(R.id.signup_email);


        supp_compte_btn.setOnClickListener(view -> {
            bar_navig.setVisibility(View.VISIBLE);
            Package_ndc.connecter_et_supp(signup_email,signup_password,view.getContext());
        });

        quitter_gestion_compte.setOnClickListener(view -> {
            bar_navig.setVisibility(View.VISIBLE);
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            picture_fragment newFragment = new picture_fragment();
            fragmentTransaction.replace(R.id.fragment_space, newFragment);
            fragmentTransaction.commit();
        });

        deco_btn.setOnClickListener(v -> {
            bar_navig.setVisibility(View.VISIBLE);
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(view.getContext(),Activity_Login.class));

        });

        return view;
    }
}