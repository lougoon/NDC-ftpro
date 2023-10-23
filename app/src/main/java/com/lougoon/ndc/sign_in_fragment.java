package com.lougoon.ndc;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class sign_in_fragment extends Fragment {

    private FirebaseAuth auth;
    private EditText signupEmail, signupPassword,pseudo;
    private Button signupButton;
    private TextView loginRedirectText;

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.sign_in_fragment, container, false);

        auth = FirebaseAuth.getInstance();
        signupEmail = view.findViewById(R.id.signup_email);
        signupPassword = view.findViewById(R.id.signup_password);
        signupButton = view.findViewById(R.id.signup_button);
        loginRedirectText = view.findViewById(R.id.loginRedirectText);
        pseudo = view.findViewById(R.id.pseudo);

        signupButton.setOnClickListener(view -> {
            String user = signupEmail.getText().toString().trim();
            String pass = signupPassword.getText().toString().trim();
            String pseu = pseudo.getText().toString().trim();
            if (user.isEmpty()){
                signupEmail.setError("Email can't be empty");
            } else {
                if (pseu.isEmpty()){
                    pseudo.setError("Pseudo can't be empty");
                } else {
                    if (pass.isEmpty()){
                        signupPassword.setError("Password can't be empty");
                    } else{
                        auth.createUserWithEmailAndPassword(user, pass).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(view.getContext(), "SignUp Successful", Toast.LENGTH_SHORT).show();
                                // Envoyer un e-mail de vérification à l'utilisateur
                                sendEmailVerification();
                                FirebaseUser firebaseUser = auth.getCurrentUser();
                                if (firebaseUser != null) {
                                    // Obtenez le token d'authentification pour l'utilisateur
                                    firebaseUser.getIdToken(true).addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            String userToken = task1.getResult().getToken();
                                            // Enregistrez le token d'authentification localement pour la connexion automatique
                                            Package_ndc.save_in_preference(view.getContext(),userToken,"user_token");
                                        } else {
                                            // Gérer l'échec de la récupération du token
                                            if (Package_ndc.isInternetConnected(view.getContext())) {
                                                // L'appareil est connecté à Internet
                                                Toast.makeText(view.getContext(), "backup token failed", Toast.LENGTH_SHORT).show();
                                            } else {
                                                // L'appareil n'est pas connecté à Internet
                                                Toast.makeText(view.getContext(), "Pas de connexion Internet", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        creation_doc_utilisateur(pseu,user);
                                        // vers le fragment verif email
                                        replace_fragment();

                                    });
                                }
                            } else {
                                Toast.makeText(view.getContext(), "Sign in Failed" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }


        });
        loginRedirectText.setOnClickListener(view -> {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            Login_fragment newFragment = new Login_fragment();

            fragmentTransaction.replace(R.id.fragment_space_login, newFragment);
            fragmentTransaction.addToBackStack(null); // permet de garder l'historique des pages vu avant et pouvoir revenir en arriere
            fragmentTransaction.commit();

        });

        return view;
    }

    private void creation_doc_utilisateur(String pseudo, String email) {
        String userId = auth.getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Créez un objet Map pour stocker les données
        Map<String, Object> userData = new HashMap<>();
        userData.put("pseudo", pseudo);
        userData.put("email", email);
        Package_ndc.save_in_preference(view.getContext(),pseudo,"PSEUDO");
        Package_ndc.save_in_preference(view.getContext(),email,"EMAIL");

        // Ajoutez le document à la collection "utilisateurs"
        db.collection("utilisateurs").document(userId)
                .set(userData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Document créé avec succès

                        Log.d("creation dossier", "success");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Gestion des erreurs
                        Log.d("creation dossier", "onFailure: ");
                    }
                });

    }
    //envoie le mail de verif
    private void sendEmailVerification() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(view.getContext(), "email sent to " + user.getEmail(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(view.getContext(), "Failed check the connection", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void replace_fragment() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Verif_email_fragment newFragment = new Verif_email_fragment();

        fragmentTransaction.replace(R.id.fragment_space_login, newFragment);
        fragmentTransaction.addToBackStack(null); // permet de garder l'historique des pages vu avant et pouvoir revenir en arriere
        fragmentTransaction.commit();

    }
}