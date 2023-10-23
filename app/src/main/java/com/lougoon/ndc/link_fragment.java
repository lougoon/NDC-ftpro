package com.lougoon.ndc;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

public class link_fragment extends Fragment {

    View view;

    LinearLayout openFacebook;
    LinearLayout openInsta;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_link, container, false);
        resizeHorizontalMargins();
        return view;
    }
    private void resizeHorizontalMargins() {
        // Obtenir la largeur de l'écran en pixels
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        int screenWidth = outMetrics.widthPixels;

        // Définir la largeur de l'écran d'une tablette standard (par exemple, 600dp)
        int tabletScreenWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 600, getResources().getDisplayMetrics());

        // Comparer la largeur de l'écran avec celle d'une tablette standard
        if (screenWidth > tabletScreenWidth) {
            // Calculer les nouvelles marges horizontales (10% de la largeur de l'écran)
            int horizontalMargin = (int) (screenWidth * 0.15); // 10% de la largeur de l'écran

            // Réajuster les marges horizontales pour le LinearLayout Facebook
            LinearLayout facebookLayout = view.findViewById(R.id.facebook);
            ConstraintLayout.LayoutParams facebookParams = (ConstraintLayout.LayoutParams) facebookLayout.getLayoutParams();
            facebookParams.setMargins(horizontalMargin, facebookParams.topMargin, horizontalMargin, facebookParams.bottomMargin);
            facebookLayout.setLayoutParams(facebookParams);

            // Réajuster les marges horizontales pour le LinearLayout Instagram
            LinearLayout instagramLayout = view.findViewById(R.id.instagram);
            ConstraintLayout.LayoutParams instagramParams = (ConstraintLayout.LayoutParams) instagramLayout.getLayoutParams();
            instagramParams.setMargins(horizontalMargin, instagramParams.topMargin, horizontalMargin, instagramParams.bottomMargin);
            instagramLayout.setLayoutParams(instagramParams);
        }
    }



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        openInsta=(LinearLayout) getView().findViewById(R.id.instagram);
        openFacebook=(LinearLayout) getView().findViewById(R.id.facebook);
        openInsta.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent launchIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/nuitducompas/?igshid=ZWIzMWE5ZmU3Zg%3D%3D&fbclid=IwAR20zxAxW9hKpLTgEtz--J4m5HWMqQfoKNBui2pKBxUGNjk32Q4Xj0moDnI"));
                launchIntent.setPackage("com.instagram.android");

                if(launchIntent.resolveActivity(getActivity().getPackageManager()) != null) {

                    startActivity(launchIntent);
                    getActivity().finish();
                }

                else {
// Instagram app not found, open the profile on the web
                    launchIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/nuitducompas/?igshid=ZWIzMWE5ZmU3Zg%3D%3D&fbclid=IwAR20zxAxW9hKpLTgEtz--J4m5HWMqQfoKNBui2pKBxUGNjk32Q4Xj0moDnI"));
                    startActivity(launchIntent);
                    getActivity().finish();
                }
            }
        });
        openFacebook.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                goToFacebookPage("100063663776631");

            }
            private void goToFacebookPage(String id){
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/"+id));
                    startActivity(intent);
                } catch (ActivityNotFoundException e){
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/"+id));
                    startActivity(intent);
                }
            }
        });

    }
}