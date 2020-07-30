package com.example.cartelerappfinal.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.cartelerappfinal.Fragments.MovieFragment;
import com.example.cartelerappfinal.Fragments.ProfileFragment;
import com.example.cartelerappfinal.Fragments.TheatreFragment;
import com.example.cartelerappfinal.R;

import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class NavDrawerActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    Dialog popAddMovie;
    ImageView popupAddBtn,popupMovieCover ;
    TextView popupMovieTitle, popupSynopsis, popupAge, popupGenre;

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_draw_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        //inicializar el popup de películas
        iniPopup();

        FloatingActionButton fab = findViewById(R.id.fbAddMovie);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popAddMovie.show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);


        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_theatres, R.id.nav_logout)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        //GESTIONA LA SELECCIÓN DEL MENÚ
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {


                if (destination.getId() == R.id.nav_home){
                    getSupportActionBar().setTitle("Películas");
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, new MovieFragment()).commit();
                }
                if (destination.getId() == R.id.nav_theatres){
                    getSupportActionBar().setTitle("Cines");
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, new TheatreFragment()).commit();
                }
                if (destination.getId() == R.id.nav_profile){
                    getSupportActionBar().setTitle("Perfil");
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, new ProfileFragment()).commit();
                }
                if (destination.getId() == R.id.nav_logout){

                    FirebaseAuth.getInstance().signOut();
                    Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(loginActivity);
                    finish();
                }
            }
        });

        updateNavHeader();
    }

    //INICIALIZA EL BOTÓN DE AÑADIR PELÍCULAS Y LANZA EL POPUP PARA EDITAR LA FICHA
    private void iniPopup() {

        popAddMovie = new Dialog(this);

        popAddMovie.setContentView(R.layout.popup_add_movie);
        popAddMovie.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popAddMovie.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT);
        popAddMovie.getWindow().getAttributes().gravity = Gravity.TOP;

        //INICIAR LOS PARÁMETROS DEL POPUP PARA AÑADIR
        popupMovieTitle = popAddMovie.findViewById(R.id.etTitulo);
        popupGenre = popAddMovie.findViewById(R.id.etGenero);
        popupAge = popAddMovie.findViewById(R.id.etEdad);
        popupSynopsis = popAddMovie.findViewById(R.id.etSinopsis);

        //LISTENER DEL BOTÓN DE CARGAR IMAGEN PARA SUBIR
        popupMovieCover = popAddMovie.findViewById(R.id.ivCartel);
        //LISTENER DEL BOTÓN DE GALERÍA
        /*
        popupMovieCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(NavDrawerActivity.this, "YOLOOO", Toast.LENGTH_SHORT).show();
            }
        });*/

        popupAddBtn = popAddMovie.findViewById(R.id.fbAddMovie);
        //LISTENER DEL BOTÓN DE ENVIAR
        /*
        popupAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupAddBtn.setVisibility(View.INVISIBLE);
            }
        });*/

        Glide.with(NavDrawerActivity.this).load(currentUser.getPhotoUrl()).into(popupMovieCover);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav__drawer__home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }


    //RECARGA LA CABECERA DEL MENÚ DEL NAVIGATION DRAWER CON LA INFORMACIÓN DEL USUARIO
    public void updateNavHeader(){
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUserName = headerView.findViewById(R.id.nav_user_name);
        TextView navUserMail = headerView.findViewById(R.id.nav_user_mail);
        ImageView navUserPhoto = headerView.findViewById(R.id.nav_user_photo);

        navUserName.setText("Ha iniciado sesión como:");
        navUserMail.setText(currentUser.getEmail());

        //Glide.with(NavDrawerActivity.this).load(currentUser.getPhotoUrl()).into(navUserPhoto);
        //TODO: cambiar la imagen, no engancha el url
        Glide.with(NavDrawerActivity.this).load("https://pm1.narvii.com/7144/14993b8696d6a2fff3e1b028bb3171fb8d26bd46r1-1200-600v2_128.jpg").into(navUserPhoto);
    }
}
