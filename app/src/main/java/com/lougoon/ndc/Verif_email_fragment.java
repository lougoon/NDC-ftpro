package com.lougoon.ndc;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


public class Verif_email_fragment extends Fragment {
    View view;
    private Button verif_confirm_btn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.verif_email_fragment, container, false);
        //futur fonction affichage de l'adresse mail dans le texte xml quand j'ai le time
        verif_confirm_btn = view.findViewById(R.id.verif_btn_confirm);

        verif_confirm_btn.setOnClickListener(view -> {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            Login_fragment newFragment = new Login_fragment();

            fragmentTransaction.add(R.id.fragment_space_login, newFragment);
            fragmentTransaction.addToBackStack(null); // permet de garder l'historique des pages vu avant et pouvoir revenir en arriere
            fragmentTransaction.commit();

        });
        return view;
    }
}