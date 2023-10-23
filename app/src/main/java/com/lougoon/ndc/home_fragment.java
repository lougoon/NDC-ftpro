package com.lougoon.ndc;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;


public class home_fragment extends Fragment {

    View view;
    TextView decomptetext;
    ConstraintLayout teaser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home_fragment, container, false);

        // Date future pour le décompte
        Calendar futureDate = Calendar.getInstance();
        futureDate.set(2024, Calendar.JULY, 1, 14, 0, 0);

        // Obtenir la date actuelle
        Calendar currentDate = Calendar.getInstance();

        // Calculer la différence en millisecondes entre les deux dates
        long diffInMillis = futureDate.getTimeInMillis() - currentDate.getTimeInMillis();

        // Démarrer le CountDownTimer pour afficher le décompte
        new CountDownTimer(diffInMillis, 1000) {

            public void onTick(long millisUntilFinished) {
                // Calculer les jours, heures, minutes et secondes restantes
                long days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished);
                millisUntilFinished -= TimeUnit.DAYS.toMillis(days);
                long hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                millisUntilFinished -= TimeUnit.HOURS.toMillis(hours);
                long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                millisUntilFinished -= TimeUnit.MINUTES.toMillis(minutes);
                long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);

                // Créer la chaîne de caractères avec le décompte
                String countdownString = days + " days " + hours + "h " + minutes + "min " + seconds + "s";

                // Afficher la chaîne dans un TextView ou utiliser-la comme vous le souhaitez
                decomptetext = view.findViewById(R.id.decompte);
                decomptetext.setText(countdownString);
            }

            public void onFinish() {
                // Action à effectuer lorsque le décompte est terminé (la date future est atteinte)
                TextView decomptetext = view.findViewById(R.id.decompte);
                decomptetext.setText("Décompte terminé !"); // mettre rick astley link youtube
            }
        }.start();



        return view;


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        teaser=(ConstraintLayout) getView().findViewById(R.id.teaser);
        teaser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=JEsUsD9GFqc"));
                launchIntent.setPackage("com.youtube.android");

                if(launchIntent.resolveActivity(getActivity().getPackageManager()) != null) {

                    startActivity(launchIntent);
                    getActivity().finish();
                }

                else {
                    launchIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=JEsUsD9GFqc"));
                    startActivity(launchIntent);
                    getActivity().finish();
                }

            }
        });


    }
}