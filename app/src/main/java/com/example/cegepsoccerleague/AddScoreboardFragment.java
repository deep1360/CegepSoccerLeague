package com.example.cegepsoccerleague;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddScoreboardFragment extends Fragment implements View.OnClickListener{

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private Context context;
    public NavController HomeNavController;
    private ImageView add_sb_team1_img_view, add_sb_team2_img_view;
    private TextView add_sb_date, add_sb_time, add_sb_team1_name, add_sb_team2_name;
    private TextInputLayout add_sb_team1_goals, add_sb_team2_goals,
            add_sb_team1_fouls, add_sb_team2_fouls,
            add_sb_team1_corners, add_sb_team2_corners,
            add_sb_team1_kicks, add_sb_team2_kicks,
            add_sb_team1_goal_saved, add_sb_team2_goal_saved;
    private MaterialButton add_new_score_btn;


    public AddScoreboardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_scoreboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity().getApplicationContext();
        HomeNavController = Navigation.findNavController(getActivity(), R.id.home_host_fragment);

        add_sb_team1_img_view = view.findViewById(R.id.add_sb_team1_img_view);
        add_sb_team2_img_view = view.findViewById(R.id.add_sb_team2_img_view);
        add_sb_date = view.findViewById(R.id.add_sb_date);
        add_sb_time = view.findViewById(R.id.add_sb_time);
        add_sb_team1_name = view.findViewById(R.id.add_sb_team1_name);
        add_sb_team2_name = view.findViewById(R.id.add_sb_team2_name);

        add_sb_team1_goals = view.findViewById(R.id.add_sb_team1_goals);
        add_sb_team2_goals = view.findViewById(R.id.add_sb_team2_goals);
        add_sb_team1_fouls = view.findViewById(R.id.add_sb_team1_fouls);
        add_sb_team2_fouls = view.findViewById(R.id.add_sb_team2_fouls);
        add_sb_team1_corners = view.findViewById(R.id.add_sb_team1_corners);
        add_sb_team2_corners = view.findViewById(R.id.add_sb_team2_corners);
        add_sb_team1_kicks = view.findViewById(R.id.add_sb_team1_kicks);
        add_sb_team2_kicks = view.findViewById(R.id.add_sb_team2_kicks);
        add_sb_team1_goal_saved = view.findViewById(R.id.add_sb_team1_goal_saved);
        add_sb_team2_goal_saved = view.findViewById(R.id.add_sb_team2_goal_saved);
        add_new_score_btn = view.findViewById(R.id.add_new_score_btn);
        add_new_score_btn.setOnClickListener(this);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();
        //Get Current User refernece
        user = mAuth.getCurrentUser();
    }

    @Override
    public void onClick(View view) {
        if(view==add_new_score_btn){
            add_sb_team1_goals.setErrorEnabled(false);
            add_sb_team2_goals.setErrorEnabled(false);
            add_sb_team1_fouls.setErrorEnabled(false);
            add_sb_team2_fouls.setErrorEnabled(false);
            add_sb_team1_corners.setErrorEnabled(false);
            add_sb_team2_corners.setErrorEnabled(false);
            add_sb_team1_kicks.setErrorEnabled(false);
            add_sb_team2_kicks.setErrorEnabled(false);
            add_sb_team1_goal_saved.setErrorEnabled(false);
            add_sb_team2_goal_saved.setErrorEnabled(false);
            if(TextUtils.isEmpty(add_sb_team1_goals.getEditText().getText().toString().trim())){
                add_sb_team1_goals.setError(" ");
            }
            else if(TextUtils.isEmpty(add_sb_team2_goals.getEditText().getText().toString().trim())){
                add_sb_team2_goals.setError(" ");
            }
            else if(TextUtils.isEmpty(add_sb_team1_fouls.getEditText().getText().toString().trim())){
                add_sb_team1_fouls.setError(" ");
            }
            else if(TextUtils.isEmpty(add_sb_team2_fouls.getEditText().getText().toString().trim())){
                add_sb_team2_fouls.setError(" ");
            }
            else if(TextUtils.isEmpty(add_sb_team1_corners.getEditText().getText().toString().trim())){
                add_sb_team1_corners.setError(" ");
            }
            else if(TextUtils.isEmpty(add_sb_team2_corners.getEditText().getText().toString().trim())){
                add_sb_team2_corners.setError(" ");
            }
            else if(TextUtils.isEmpty(add_sb_team1_kicks.getEditText().getText().toString().trim())){
                add_sb_team1_kicks.setError(" ");
            }
            else if(TextUtils.isEmpty(add_sb_team2_kicks.getEditText().getText().toString().trim())){
                add_sb_team2_kicks.setError(" ");
            }
            else if(TextUtils.isEmpty(add_sb_team1_goal_saved.getEditText().getText().toString().trim())){
                add_sb_team1_goal_saved.setError(" ");
            }
            else if(TextUtils.isEmpty(add_sb_team2_goal_saved.getEditText().getText().toString().trim())){
                add_sb_team2_goal_saved.setError(" ");
            }
        }

    }
}
