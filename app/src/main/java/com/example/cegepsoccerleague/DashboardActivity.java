package com.example.cegepsoccerleague;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    public Toolbar HomeToolbar;
    public DrawerLayout HomedrawerLayout;
    public NavigationView HomeNavigationView;
    public NavController HomeNavController;
    public TextView username;
    public ImageView userImage;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();

        setupNavigation();

    }

    //Setting Up Navigation Drawer
    public void setupNavigation(){

        HomeToolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(HomeToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        HomedrawerLayout = findViewById(R.id.home_drawer_layout);

        HomeNavigationView = findViewById(R.id.home_navigationview);
        username = HomeNavigationView.getHeaderView(0).findViewById(R.id.header_user_name);
        userImage = HomeNavigationView.getHeaderView(0).findViewById(R.id.header_user_icon);
        HomeNavigationView.getMenu().clear();

        if(mAuth.getCurrentUser()!=null){
            user = mAuth.getCurrentUser();
            getUserDetails(user.getUid());
        }
        else {

        }

        HomeNavController = Navigation.findNavController(this,R.id.home_host_fragment);

        NavigationUI.setupActionBarWithNavController(this,HomeNavController,HomedrawerLayout);
        NavigationUI.setupWithNavController(HomeNavigationView,HomeNavController);

        HomeNavigationView.setNavigationItemSelectedListener(this);
    }

    private void getUserDetails(String uid) {
        db.collection("users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    username.setText("Welcome\n"+document.getData().get("first_name")+" "+document.getData().get("last_name"));
                    if (document.exists()) {
                        if(document.getData().get("user_type").equals("LM")){
                            HomeNavigationView.getMenu().clear();
                            HomeNavigationView.inflateMenu(R.menu.legue_manager_menu);
                        }
                        else if(document.getData().get("user_type").equals("TM")){
                        }
                    }
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(Navigation.findNavController(this,R.id.home_host_fragment),HomedrawerLayout);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        HomedrawerLayout.closeDrawers();

        switch (item.getItemId()){
            case R.id.lgm_leagues:
                break;
            case R.id.lgm_profile:
                if(HomeNavController.getCurrentDestination().getId()==R.id.homeFragment) {
                    HomeNavController.navigate(R.id.updateProfileFragment);
                }
                break;
            case R.id.lgm_logout:
                mAuth.signOut();
                startActivity(new Intent(DashboardActivity.this,LoginActivity.class));
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {

        //If Navigation drawer is open the close it on back press
        if (HomedrawerLayout.isDrawerOpen(GravityCompat.START)){

            HomedrawerLayout.closeDrawer(GravityCompat.START);

        }else {
            super.onBackPressed();
        }
    }
}
