package com.lougoon.ndc;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login_fragment extends Fragment {

    View view;
    private EditText loginEmail, loginPassword;
    private TextView signupRedirectText;
    private Button loginButton;
    private FirebaseAuth auth;
    TextView forgotPassword;
    ProgressBar progressBar;
    CardView loginCard;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.login_fragment, container, false);

        loginEmail = view.findViewById(R.id.login_email);
        loginPassword = view.findViewById(R.id.login_password);
        loginButton = view.findViewById(R.id.login_button);
        signupRedirectText = view.findViewById(R.id.signUpRedirectText);
        forgotPassword = view.findViewById(R.id.forgot_password);
        auth = FirebaseAuth.getInstance();
        progressBar = view.findViewById(R.id.progressBar);
        loginCard = view.findViewById(R.id.loginCard);

        // Vérifier automatiquement la connexion lorsque l'application est ouverte
        if (!Package_ndc.isInternetConnected(view.getContext())) {
            Toast.makeText(view.getContext(), "Pas de connexion Internet", Toast.LENGTH_SHORT).show();
        }

        checkAndAutoLogin();

        loginButton.setOnClickListener(v -> {
            String email = loginEmail.getText().toString();
            String pass = loginPassword.getText().toString();
            if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                if (!pass.isEmpty()) {
                    auth.signInWithEmailAndPassword(email, pass)
                            .addOnSuccessListener(authResult -> {
                                FirebaseUser user = auth.getCurrentUser();
                                if (user != null) {
                                    if (user.isEmailVerified()) {
                                        user.getIdToken(true).addOnCompleteListener(tokenTask -> {
                                            if (tokenTask.isSuccessful()) {
                                                String refreshedToken = tokenTask.getResult().getToken();
                                                // Enregistrez le nouveau token d'authentification localement
                                                Package_ndc.save_in_preference(view.getContext(), refreshedToken, "user_token");
                                                view.getContext().startActivity(new Intent(view.getContext(), MainActivity.class));
                                                if (getActivity() != null) {
                                                    getActivity().finish();
                                                }
                                            } else {
                                                // Gérer l'échec de la récupération du token
                                                // ...
                                            }
                                        });
                                    } else {
                                        Toast.makeText(view.getContext(), "Email not verified", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).addOnFailureListener(e -> Toast.makeText(view.getContext(), "Login Failed", Toast.LENGTH_SHORT).show());
                } else {
                    loginPassword.setError("Empty fields are not allowed");
                }
            } else if (email.isEmpty()) {
                loginEmail.setError("Empty fields are not allowed");
            } else {
                loginEmail.setError("Please enter correct email");
            }
        });
        signupRedirectText.setOnClickListener(v -> replace_fragment());
        return view;
    }

    private void checkAndAutoLogin() {
        String userToken = Package_ndc.get_in_preference(view.getContext(),"user_token");
        progressBar.setVisibility(View.VISIBLE);

        if (userToken != null) {
            // Tentez la connexion automatique avec le token sauvegardé localement
            auth.signInWithCustomToken(userToken) // methode Asynchrome attention !!
                    .addOnSuccessListener(authResult -> {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            if (user.isEmailVerified()) {
                                view.getContext().startActivity(new Intent(view.getContext(), MainActivity.class));
                                if (getActivity() != null) {
                                    getActivity().finish();
                                }
                            } else {
                                // action a faire si auto login n'a pas marché Toast.make ??
                                Handler handler = new Handler(Looper.getMainLooper());
                                handler.post(() -> {
                                    // Mettez à jour l'interface utilisateur ici
                                    progressBar.setVisibility(View.GONE);
                                    loginCard.setVisibility(View.VISIBLE);
                                });
                            }
                        }

                    }).addOnFailureListener(e -> {
                        // La connexion automatique a échoué ou le token sauvegardé localement n'est plus valide
                        // Essayer de rafraîchir le token avec le token de rafraîchissement
                        FirebaseUser currentUser = auth.getCurrentUser();
                        if (currentUser != null) {
                            currentUser.getIdToken(true).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    String refreshedToken = task.getResult().getToken();
                                    // Enregistrez le nouveau token d'ID rafraîchi localement
                                    Package_ndc.save_in_preference(view.getContext(),refreshedToken,"user_token");
                                    // Connectez l'utilisateur avec le nouveau token d'ID
                                    if (currentUser.isEmailVerified()) {
                                        view.getContext().startActivity(new Intent(view.getContext(), MainActivity.class));
                                        if (getActivity() != null) {
                                            getActivity().finish();
                                        }

                                    } else {
                                        Handler handler = new Handler(Looper.getMainLooper());
                                        handler.post(() -> {
                                            // Mettez à jour l'interface utilisateur ici
                                            progressBar.setVisibility(View.GONE);
                                            loginCard.setVisibility(View.VISIBLE);
                                        });
                                    }
                                } else {
                                    // La tentative de rafraîchissement du token a échoué
                                    if (Package_ndc.isInternetConnected(view.getContext())) {
                                        Handler handler = new Handler(Looper.getMainLooper());
                                        handler.post(() -> {
                                            // Mettez à jour l'interface utilisateur ici
                                            progressBar.setVisibility(View.GONE);
                                            loginCard.setVisibility(View.VISIBLE);
                                        });
                                    } else {
                                        // L'appareil n'est pas connecté à Internet
                                        Toast.makeText(view.getContext(), "Pas de connexion Internet", Toast.LENGTH_SHORT).show();
                                        Handler handler = new Handler(Looper.getMainLooper());
                                        handler.post(() -> {
                                            // Mettez à jour l'interface utilisateur ici
                                            progressBar.setVisibility(View.GONE);
                                            loginCard.setVisibility(View.VISIBLE);
                                        });
                                    }

                                }
                            });
                        } else {
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(() -> {
                                // Mettez à jour l'interface utilisateur ici
                                progressBar.setVisibility(View.GONE);
                                loginCard.setVisibility(View.VISIBLE);
                            });
                        }
                    });
        } else {

            Handler handler = new Handler();
            handler.postDelayed(() -> {
                progressBar.setVisibility(View.GONE);
                loginCard.setVisibility(View.VISIBLE);
            }, 2000); // Attendre 2000 millisecondes (2 secondes)
        }



    }

    private void replace_fragment() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        sign_in_fragment newFragment = new sign_in_fragment();

        fragmentTransaction.replace(R.id.fragment_space_login, newFragment);
        fragmentTransaction.addToBackStack(null); // permet de garder l'historique des pages vu avant et pouvoir revenir en arriere
        fragmentTransaction.commit();

    }
}