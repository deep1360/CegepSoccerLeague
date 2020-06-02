package com.example.cegepsoccerleague;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


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
}
