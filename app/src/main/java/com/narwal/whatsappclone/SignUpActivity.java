package com.narwal.whatsappclone;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.narwal.whatsappclone.model.User;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";
    final FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    Button btnNext;
    ShapeableImageView imgUser;
    EditText etName;
    String downloadUrl = "";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        etName = findViewById(R.id.nameEt);
        imgUser = findViewById(R.id.userImgView);
        btnNext = findViewById(R.id.nextBtn);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnNext.setEnabled(false);
                String name = etName.getText().toString();
                if (name.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Name can not be empty!", Toast.LENGTH_SHORT).show();
                } else if (downloadUrl.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please select an image.", Toast.LENGTH_SHORT).show();
                } else {
                    User user = new User(name, downloadUrl, downloadUrl, firebaseAuth.getUid());
                    DocumentReference documentReference = firebaseFirestore.collection(getString(R.string.userRefKey)).document(Objects.requireNonNull(firebaseAuth.getUid()));
                    documentReference
                            .set(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(SignUpActivity.this, "Sign up complete!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                }
            }
        });
        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermissionForImageSelection();
                //TODO : Firebase Extension - Thumbnail Image
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermissionForImageSelection() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED &&
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {

            String[] permissionRead = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
//            String [] permissionWrite = new String[]{};
            requestPermissions(permissionRead, 1001);

        } else {
            pickImage();
        }
    }

    private void pickImage() {
        Intent picker = new Intent(Intent.ACTION_PICK);
        picker.setType("image/*");
        startActivityForResult(Intent.createChooser(picker, "Select Image"), 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 100) {
            Uri uri = data.getData();
            imgUser.setImageURI(uri);
            uploadImage(uri);
        }

    }

    private void uploadImage(Uri uri) {
        btnNext.setEnabled(false);
        final StorageReference reference = firebaseStorage.getReference().child("uploads/" + firebaseAuth.getUid());
        UploadTask task = reference.putFile(uri);
//        .continueWith(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//            @Override
//            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                Log.d(TAG, "then: " + reference.toString());
//                if (!task.isSuccessful()){
//                    throw new Exception(task.getException());
//                }else{
//                    return reference.getDownloadUrl();
//                }
//            }
//        })
        task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                downloadUrl = uri.toString();
                                Log.d(TAG, "onSuccess: downloadUrl = " + downloadUrl);
                                btnNext.setEnabled(true);
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: " + e.getLocalizedMessage());
            }
        });
    }
}