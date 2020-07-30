package com.example.cartelerappfinal.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.cartelerappfinal.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText userEmail, userPassword;
    ProgressBar loginProgressBar;
    Button btnLogin;

    FirebaseAuth mAuth;
    Intent homeActivity;

    ImageView loginPhoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userEmail = findViewById(R.id.tvLoginMail);
        userPassword = findViewById(R.id.tvLoginPassword);

        loginProgressBar = findViewById(R.id.progressBarLogin);
        loginProgressBar.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();
        homeActivity = new Intent(this, NavDrawerActivity.class);

        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginProgressBar.setVisibility(View.VISIBLE);
                btnLogin.setVisibility(View.INVISIBLE);

                final String mail = userEmail.getText().toString();
                final String password = userPassword.getText().toString();
                
                if(mail.isEmpty() || password.isEmpty()) {
                    showMessage("Por favor, verifica todos los campos");
                    btnLogin.setVisibility(View.VISIBLE);
                    loginProgressBar.setVisibility(View.INVISIBLE);

                }else {

                    signIn(mail, password);
                }
            }
        });


        loginPhoto = findViewById(R.id.ivLoginPhoto);
        loginPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent registerActivityIntent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(registerActivityIntent);
                finish();
            }
        });


    }


    //GESTIONA EL INICIO DE SESIÓN
    private void signIn(String mail, String password) {

        mAuth.signInWithEmailAndPassword(mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    loginProgressBar.setVisibility(View.INVISIBLE);
                    btnLogin.setVisibility(View.VISIBLE);
                    updateUI();

                }else {
                    showMessage("Datos incorrectos. Por favor vuelve a intentarlo");
                    btnLogin.setVisibility(View.VISIBLE);
                    loginProgressBar.setVisibility(View.INVISIBLE);
                }

            }
        });

    }


    //MENSAJE DE ALERTA QUE PIDE QUE INSERTE LOS DATOS DE LOGIN CORRECTAMENTE
    private void updateUI() {
        startActivity(homeActivity);
        finish();
    }


    //MENSAJE DE ALERTA QUE PIDE QUE INSERTE LOS DATOS DE INICIO DE SESIÓN CORRECTAMENTE
    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }


    //LÓGICA PARA DETECTAR SI EL USUARIO YA ESTÁ CONECTADO Y REDIRIGIRLO A LA PAGINA HOME
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();

        if(user != null){
            updateUI();

        }

    }
}
