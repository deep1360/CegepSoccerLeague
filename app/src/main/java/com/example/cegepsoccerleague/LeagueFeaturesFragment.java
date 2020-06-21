package com.example.cegepsoccerleague;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class LeagueFeaturesFragment extends Fragment implements View.OnClickListener{

    private MaterialCardView lgm_ft_your_teams_cv,lgm_ft_schedule_cv,lgm_ft_scoreboard_cv;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    public NavController HomeNavController;
    private Context context;
    private FirebaseFirestore db;
    public Toolbar HomeToolbar;
    private String league_id,league_name;
    private Bundle dataBundle;


    public LeagueFeaturesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_league_features, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity().getApplicationContext();
        HomeNavController = Navigation.findNavController(getActivity(), R.id.home_host_fragment);
        HomeToolbar = getActivity().findViewById(R.id.home_toolbar);

        //Assigning name of the league as a title of Activity's toolbar
        if(getArguments()!=null){
            dataBundle = getArguments();
            HomeToolbar.setTitle(getArguments().getString("league_name"));
            league_id = getArguments().getString("league_id");
            league_name = getArguments().getString("league_name");
        }

        lgm_ft_your_teams_cv = view.findViewById(R.id.lgm_ft_your_teams_cv);
        lgm_ft_schedule_cv = view.findViewById(R.id.lgm_ft_schedule_cv);
        lgm_ft_scoreboard_cv = view.findViewById(R.id.lgm_ft_scoreboard_cv);

        lgm_ft_your_teams_cv.setOnClickListener(this);
        lgm_ft_schedule_cv.setOnClickListener(this);
        lgm_ft_scoreboard_cv.setOnClickListener(this);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();
        //Get Current User refernece
        user = mAuth.getCurrentUser();
    }

    @Override
    public void onClick(View view) {
        if(view==lgm_ft_your_teams_cv){
            Bundle dataBundle = new Bundle();
            dataBundle.putString("league_id",league_id);
            dataBundle.putString("league_name",league_name);
            HomeNavController.navigate(R.id.listOfTeamsFragment,dataBundle);
        }
        else if(view==lgm_ft_schedule_cv){
            Bundle dataBundle = new Bundle();
            dataBundle.putString("league_id",league_id);
            dataBundle.putString("league_name",league_name);
            dataBundle.putString("from","league Features");
            HomeNavController.navigate(R.id.listOfSchedulesFragment,dataBundle);
        }
        else if(view==lgm_ft_scoreboard_cv){
            Bundle dataBundle = new Bundle();
            dataBundle.putString("league_id",league_id);
            dataBundle.putString("league_name",league_name);
            dataBundle.putString("from","league Features");
            HomeNavController.navigate(R.id.scoreboardFragment,dataBundle);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*----------Hiding Edit and Delete Button For Guest Users------------*/
        if(getArguments()!=null) {
            setHasOptionsMenu(true);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.schedule_info_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId()==R.id.edit_option){
            dataBundle.putString("from","update league");
            HomeNavController.navigate(R.id.addLeagueFragment,dataBundle);
        }
        else if(item.getItemId()==R.id.delete_option){
            DeleteLeague();
        }

        return super.onOptionsItemSelected(item);
    }

    private void DeleteLeague() {
        /*------------ Configuration of Guest Login Dialog----------*/
        AlertDialog.Builder dialog_builder = new AlertDialog.Builder(getActivity())
                .setView(R.layout.delete_league_dialog);
        final AlertDialog Delete_Dialog = dialog_builder.create();
        Delete_Dialog.show();
        final MaterialButton delete_cancel_btn = Delete_Dialog.findViewById(R.id.delete_cancel_btn);
        final MaterialButton delelte_continue_btn = Delete_Dialog.findViewById(R.id.delelte_continue_btn);

        delete_cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Delete_Dialog.dismiss();
            }
        });

        delelte_continue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delelte_continue_btn.setEnabled(false);
                delete_cancel_btn.setEnabled(false);
                final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Deleting League");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setProgress(0);
                Delete_Dialog.dismiss();
                progressDialog.show();


                /*------------ Deleting All Schedules --------------*/
                db.collection("schedules").whereEqualTo("league_id",league_id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                db.collection("schedules").document(document.getId()).delete();
                            }
                        }
                    }
                });

                /*------------ Deleting All Scores --------------*/
                db.collection("scores").whereEqualTo("league_id",league_id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                db.collection("scores").document(document.getId()).delete();
                            }
                        }
                    }
                });

                /*------------ Deleting All Teams and Team Managers --------------*/
                db.collection("teams").whereEqualTo("league_id",league_id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            final QuerySnapshot TeamSnapshot = task.getResult();
                            int count = 0;
                            for (final QueryDocumentSnapshot TeamDocument : task.getResult()) {
                                count++;
                                /*------------ Deleting All Playes Of The Team --------------*/
                                db.collection("players").whereEqualTo("team_id",TeamDocument.getId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                db.collection("players").document(document.getId()).delete();
                                            }
                                        }
                                    }
                                });

                                /*------------ Deleting All Team Managers --------------*/
                                final int finalCount = count;
                                db.collection("users").document(TeamDocument.get("team_manager_id").toString()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        DocumentSnapshot User_ds = task.getResult();
                                        if (User_ds.exists()) {
                                            db.collection("users").document(User_ds.getId()).delete();
                                            db.collection("teams").document(TeamDocument.getId()).delete();
                                            /*------------ Deleting All Team Managers From Authentication --------------*/
                                            mAuth.signInWithEmailAndPassword(User_ds.getString("email"),User_ds.getString("password")).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if (task.isSuccessful()){
                                                        mAuth.getCurrentUser().delete();

                                                        if(finalCount == TeamSnapshot.size()) {
                                                            db.collection("leagues").document(league_id).delete();
                                                            mAuth.signInWithEmailAndPassword(PreferenceData.getUseremail(context), PreferenceData.getUserpassword(context)).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                                    if (task.isSuccessful()) {
                                                                        user = mAuth.getCurrentUser();
                                                                        progressDialog.dismiss();
                                                                        Toast.makeText(context, "League Deleted Successfully!", Toast.LENGTH_LONG).show();
                                                                        delelte_continue_btn.setEnabled(true);
                                                                        delete_cancel_btn.setEnabled(true);
                                                                        HomeNavController.popBackStack();
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }
                    }
                });
            }
        });
    }
}
