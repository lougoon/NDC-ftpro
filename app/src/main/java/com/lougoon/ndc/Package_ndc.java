package com.lougoon.ndc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Package_ndc {

    public static boolean isInternetConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static void save_in_preference(Context context, String info_a_safe, String key_preference) {
        SharedPreferences preferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key_preference, info_a_safe);
        editor.apply();
    }

    public static String get_in_preference(Context context, String key_preference) {
        SharedPreferences prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return prefs.getString(key_preference, null);
    }


    public static void connecter_et_supp(EditText email_txt, EditText password_txt, Context context) {
        //tester si on mets le compte d'un autre si ca fait un bug ou pas
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String email = email_txt.getText().toString();
        String pass = password_txt.getText().toString();
        if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if (!pass.isEmpty()) {
                auth.signInWithEmailAndPassword(email, pass)
                        .addOnSuccessListener(authResult -> {
                            FirebaseUser user = auth.getCurrentUser();
                            if (user != null) {
                                if (user.isEmailVerified()) {
                                    suppression_compte(context);

                                } else {
                                    Toast.makeText(context, "Email not verified", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(e -> Toast.makeText(context, "Login Failed", Toast.LENGTH_SHORT).show());
            } else {
                password_txt.setError("Empty fields are not allowed");
            }
        } else if (email.isEmpty()) {
            email_txt.setError("Empty fields are not allowed");
        } else {
            email_txt.setError("Please enter correct email");
        }
    }

    public static void suppression_compte(Context context) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (user != null) {
            // Supprimez les données de l'utilisateur de Firestore
            DocumentReference userRef = db.collection("utilisateurs").document(user.getUid());
            userRef.delete()
                    .addOnSuccessListener(aVoid -> {
                        // supprimez le compte Firebase Auth
                        user.delete()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        // Compte supprimé avec succès
                                        Toast.makeText(context, "Compte supprimé avec succès.", Toast.LENGTH_SHORT).show();
                                        context.startActivity(new Intent(context, Activity_Login.class));
                                        ((Activity) context).finish();

                                    } else {
                                        // Erreur lors de la suppression du compte
                                        Toast.makeText(context, "Erreur lors de la suppression", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    })
                    .addOnFailureListener(e -> {
                        // Erreur lors de la suppression des données de l'utilisateur de Firestore
                        Toast.makeText(context, "Erreur lors de la suppression des données", Toast.LENGTH_SHORT).show();
                    });
        } else {
            // L'utilisateur n'est pas connecté ou l'objet FirebaseUser est nul
            Toast.makeText(context, "Utilisateur non connecté.", Toast.LENGTH_SHORT).show();
        }

    }

}



