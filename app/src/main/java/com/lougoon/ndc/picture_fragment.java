package com.lougoon.ndc;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class picture_fragment extends Fragment {

    View view;
    private ImageButton login_btn;
    private ImageView info_btn, pic_add_btn;
    private ScrollView rules;
    private FirebaseAuth auth;
    String[] required_permissions = new String[]{
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.CAMERA
    };
    boolean is_storage_image_permitted = false;
    boolean is_camera_access_permitted = false;
    String TAG = "Permission";
    private Uri uri_for_camera;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_picture_fragment, container, false);

        login_btn = view.findViewById(R.id.login_btn);
        info_btn = view.findViewById(R.id.info_btn);
        rules = view.findViewById(R.id.rules);
        pic_add_btn = view.findViewById(R.id.pic_add_btn);

        login_btn.setOnClickListener(view -> {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            gestion_du_compte_fragment newFragment = new gestion_du_compte_fragment();

            fragmentTransaction.add(R.id.fragment_space, newFragment);
            fragmentTransaction.commit();
        });

        info_btn.setOnClickListener(view -> {
            if (rules.getVisibility() == View.INVISIBLE) {
                rules.setVisibility(View.VISIBLE);
            } else {
                rules.setVisibility(View.INVISIBLE);
            }
        });

        pic_add_btn.setOnClickListener(view -> {
            if(is_camera_access_permitted){
                openCamera();
            } else {
                requestPermissionCameraAccess();
                if(is_camera_access_permitted){
                    openCamera();
                }
            }

        });

        return view;
    }
    private void requestPermissionStorageImages() {
        if (ContextCompat.checkSelfPermission(view.getContext(),required_permissions[0]) == PackageManager.PERMISSION_GRANTED){
            Log.d(TAG,required_permissions[0] + "access");
            is_storage_image_permitted = true;
            requestPermissionCameraAccess();
        } else {
            request_permission_launcher_storage_images.launch(required_permissions[0]);
        }
    }
    private ActivityResultLauncher<String> request_permission_launcher_storage_images = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
        isGranted -> {
            if (isGranted) {
                Log.d(TAG, required_permissions[0] +" Granted");
                is_storage_image_permitted = true;
            } else {
                Log.d(TAG, required_permissions[0] + " Not Granted");
                is_storage_image_permitted = false;
            }
            requestPermissionCameraAccess();
        });
    private void requestPermissionCameraAccess() {
        if (ContextCompat.checkSelfPermission(view.getContext(),required_permissions[1]) == PackageManager.PERMISSION_GRANTED){
            Log.d(TAG,required_permissions[1] + "access");
            is_camera_access_permitted = true;
        } else {
            request_permission_launcher_camera_access.launch(required_permissions[1]);
        }
    }
    private ActivityResultLauncher<String> request_permission_launcher_camera_access = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    Log.d(TAG, required_permissions[1] +" Granted");
                    is_camera_access_permitted = true;
                } else {
                    Log.d(TAG, required_permissions[1] + " Not Granted");
                    is_camera_access_permitted = false;
                }

            });
    private ActivityResultLauncher<Intent> launcher_for_camera =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK) {
                            sendtofirebase(uri_for_camera);
                            // photo prise avec le résultat envoyé sur Firebase
                        }
                    });
    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"ndc-app");
        values.put(MediaStore.Images.Media.DESCRIPTION,"ndc-app-2024");

        Activity activity = getActivity();
        if (activity != null) {
            uri_for_camera = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            if (uri_for_camera != null) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri_for_camera);
                launcher_for_camera.launch(cameraIntent);
            } else {

            }
        } else {

        }
    }
    private void sendtofirebase(Uri uri_pic) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            // L'utilisateur est correctement authentifié
            // Récupérez l'instance de Firebase Storage
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference imagesRef = storageRef.child(System.currentTimeMillis() + "-" + getCurrentUserId() + ".jpg");
            String imagePath = getRealPathFromURI(uri_pic);

            try {
                InputStream stream = new FileInputStream(imagePath);
                UploadTask uploadTask = imagesRef.putStream(stream);

                // Gérez le succès ou l'échec de l'opération de téléchargement
                uploadTask.addOnFailureListener(exception -> {
                    // Gestion de l'échec du téléchargement
                }).addOnSuccessListener(taskSnapshot -> {
                    // Gestion du succès du téléchargement
                    imagesRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadUrl = uri.toString();
                        // Utilisez l'URL de téléchargement ici
                    });
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            // L'utilisateur n'est pas authentifié, gérez ce cas ici
        }
    }
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Activity activity = getActivity();
        if (activity != null) {
            Cursor cursor = activity.getContentResolver().query(contentUri, proj, null, null, null);
            if (cursor != null) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                String filePath = cursor.getString(column_index);
                cursor.close();
                return filePath;
            } else {
                // curseur est null
                return null;
            }
        } else {
            // activité est null
            return null;
        }
    }
    private String getCurrentUserId() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUid();
        } else {
            // Si l'utilisateur n'est pas authentifié, vous pouvez renvoyer null ou gérer le cas d'erreur ici
            return null;
        }
    }


}
