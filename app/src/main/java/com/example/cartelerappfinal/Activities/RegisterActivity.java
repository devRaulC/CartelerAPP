package com.example.cartelerappfinal.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cartelerappfinal.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class RegisterActivity extends AppCompatActivity {

    private static final int PERMISIONREQUESTCODE = 1;
    private static final int GALLERYREQUESTCODE = 2;

    private TextView userName, userEmail, userPassword,userPassword2;
    private ProgressBar regProgressBar;
    private Button regButton;
    private ImageView ivUserPhoto;
    private Uri selectedImgUri;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);



        userName = findViewById(R.id.tvRegisterName);
        userEmail = findViewById(R.id.tvRegisterMail);
        userPassword = findViewById(R.id.tvRegisterPassword);
        userPassword2 = findViewById(R.id.tvRegisterPassword2);

        regProgressBar = findViewById(R.id.progressBarRegister);
        regProgressBar.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();

        regButton = findViewById(R.id.btnRegister);
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                regButton.setVisibility(View.INVISIBLE);
                regProgressBar.setVisibility(View.VISIBLE);
                final String name = userName.getText().toString();
                final String email = userEmail.getText().toString();
                final String password = userPassword.getText().toString();
                final String password2 = userPassword2.getText().toString();

                if(name.isEmpty() || email.isEmpty() || password.isEmpty() || !password2.equals(password)){

                    showMessage("Error, verifique los campos");
                    regButton.setVisibility(View.VISIBLE);
                    regProgressBar.setVisibility(View.INVISIBLE);
                } else {

                    createUserAccount(name, email, password);
                    updateUserInfo(name, selectedImgUri, mAuth.getCurrentUser());
                }

            }
        });

        ivUserPhoto = findViewById(R.id.ivRegisterPhoto);
        ivUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= 22){

                    requestPermission();
                } else {

                    openGallery();
                }
            }
        });
    }



    //MENSAJE DE ALERTA QUE PIDE QUE INSERTE LOS DATOS DE REGISTRO CORRECTAMENTE
    private void showMessage(String message) {
        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();
    }


    //CREA UN USUARIO EN FIREBASE
    private void createUserAccount(String name, String email, String password) {

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    showMessage("Cuenta creada con éxito");
                } else {
                    showMessage("Error en la creación de la cuenta. "+task.getException().getMessage());
                    regButton.setVisibility(View.VISIBLE);
                    //regProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }


    //ACTUALIZA EL PERFIL CON EL NOMBRE Y LA FOTO DEL USUARIO
    private void updateUserInfo(final String name, Uri selectedImgUri, final FirebaseUser currentUser) {

        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("fotos_usuarios");
        final StorageReference imageFilePath = mStorage.child(selectedImgUri.getLastPathSegment());

        imageFilePath.putFile(selectedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        UserProfileChangeRequest profileUpdate = new  UserProfileChangeRequest.Builder()
                                .setDisplayName(name).setPhotoUri(uri).build();

                        currentUser.updateProfile(profileUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()){
                                    showMessage("Registro completado");
                                    updateUI();
                                }
                            }
                        });
                    }
                });
            }
        });
    }


    //ENVÍA A LA PANTALLA DE HOME AL COMPLETAR UN REGISTRO
    private void updateUI() {

        Intent homeActivityIntent = new Intent(getApplicationContext(), NavDrawerActivity.class);
        startActivity(homeActivityIntent);
        finish();
    }


    //SOLICITAR PERMISOS PARA ACCEDER AL ALMACENAMIENTO DEL MOVIL CUANDO QUERAMOS CAMBIAR LA IMAGEN.
    private void requestPermission() {

        if(ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED) {

            if(ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)){

                Toast.makeText(RegisterActivity.this, "Por favor acepta los permisos requeridos", Toast.LENGTH_SHORT).show();
            }else {

                ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PERMISIONREQUESTCODE);
            }
        } else
            openGallery();
    }


    //ABRIR LA GALERÍA DE FOTOS
    private void openGallery() {

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERYREQUESTCODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == GALLERYREQUESTCODE && data != null){
            //el usuario ha seleccionado la imagen
            selectedImgUri = data.getData();
            ivUserPhoto.setImageURI(selectedImgUri);
        }
    }
}
