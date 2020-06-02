package com.example.cegepsoccerleague;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


/**
 * A simple {@link Fragment} subclass.
 */
public class ScoreboardInfoFragment extends Fragment {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private Context context;
    public NavController HomeNavController;
    private ImageView sb_info_team1_img_view, sb_info_team2_img_view;
    private TextView sb_info_date, sb_info_time, sb_info_team1_name, sb_info_team2_name, sb_info_win_team_name,
            sb_info_team1_goals, sb_info_team2_goals,
            sb_info_team1_fouls, sb_info_team2_fouls,
            sb_info_team1_corners, sb_info_team2_corners,
            sb_info_team1_kicks, sb_info_team2_kicks,
            sb_info_team1_goal_saved, sb_info_team2_goal_saved;


    public ScoreboardInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scoreboard_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity().getApplicationContext();
        HomeNavController = Navigation.findNavController(getActivity(), R.id.home_host_fragment);

        sb_info_team1_img_view = view.findViewById(R.id.sb_info_team1_img_view);
        sb_info_team2_img_view = view.findViewById(R.id.sb_info_team2_img_view);
        sb_info_date = view.findViewById(R.id.sb_info_date);
        sb_info_time = view.findViewById(R.id.sb_info_time);
        sb_info_team1_name = view.findViewById(R.id.sb_info_team1_name);
        sb_info_team2_name = view.findViewById(R.id.sb_info_team2_name);
        sb_info_win_team_name = view.findViewById(R.id.sb_info_win_team_name);
        sb_info_team1_goals = view.findViewById(R.id.sb_info_team1_goals);
        sb_info_team2_goals = view.findViewById(R.id.sb_info_team2_goals);
        sb_info_team1_fouls = view.findViewById(R.id.sb_info_team1_fouls);
        sb_info_team2_fouls = view.findViewById(R.id.sb_info_team2_fouls);
        sb_info_team1_corners = view.findViewById(R.id.sb_info_team1_corners);
        sb_info_team2_corners = view.findViewById(R.id.sb_info_team2_corners);
        sb_info_team1_kicks = view.findViewById(R.id.sb_info_team1_kicks);
        sb_info_team2_kicks = view.findViewById(R.id.sb_info_team2_kicks);
        sb_info_team1_goal_saved = view.findViewById(R.id.sb_info_team1_goal_saved);
        sb_info_team2_goal_saved = view.findViewById(R.id.sb_info_team2_goal_saved);
        
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();
        //Get Current User refernece
        user = mAuth.getCurrentUser();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null) {
            if(getArguments().getString("from")!=null && getArguments().getString("from").equals("league Features")){
                setHasOptionsMenu(true);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.schedule_info_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId()== R.id.edit_option){
            HomeNavController.navigate(R.id.addScoreboardFragment);
        }
        else if(item.getItemId()== R.id.delete_option){
            DeleteScore();
        }

        return super.onOptionsItemSelected(item);
    }

    private void DeleteScore(){
        /*------------ Configuration of Alert Dialog----------*/
        AlertDialog.Builder DAC_builder = new AlertDialog.Builder(getActivity())
                .setView(R.layout.delete_score_dialog_layout);
        final AlertDialog Delete_Player_Dialog = DAC_builder.create();
        Delete_Player_Dialog.show();
        MaterialButton delete_cancel_btn = Delete_Player_Dialog.findViewById(R.id.delete_cancel_btn);
        MaterialButton delelte_continue_btn = Delete_Player_Dialog.findViewById(R.id.delelte_continue_btn);

        delete_cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Delete_Player_Dialog.dismiss();
            }
        });

        delelte_continue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Delete_Player_Dialog.dismiss();
                HomeNavController.popBackStack();
            }
        });
    }
}
