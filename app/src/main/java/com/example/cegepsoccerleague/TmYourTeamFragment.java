package com.example.cegepsoccerleague;

import android.content.Context;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


/**
 * A simple {@link Fragment} subclass.
 */
public class TmYourTeamFragment extends Fragment implements View.OnClickListener{

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private Context context;
    public NavController HomeNavController;
    private ImageView team_info_img_view;
    private TextView team_info_email, team_info_contact_num, team_info_name;
    private MaterialButton tm_info_update_btn, tm_info_view_player_btn;


    public TmYourTeamFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tm_your_team, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity().getApplicationContext();
        HomeNavController = Navigation.findNavController(getActivity(), R.id.home_host_fragment);

        team_info_img_view = view.findViewById(R.id.team_info_img_view);
        team_info_email = view.findViewById(R.id.team_info_email);
        team_info_contact_num = view.findViewById(R.id.team_info_contact_num);
        team_info_name = view.findViewById(R.id.team_info_name);
        tm_info_update_btn = view.findViewById(R.id.tm_info_update_btn);
        tm_info_view_player_btn = view.findViewById(R.id.tm_info_view_player_btn);

        tm_info_view_player_btn.setOnClickListener(this);
        tm_info_update_btn.setOnClickListener(this);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();
        //Get Current User refernece
        user = mAuth.getCurrentUser();


    }

    @Override
    public void onClick(View view) {

        if(view==tm_info_update_btn){
            HomeNavController.navigate(R.id.updateTeamInfoFragment);
        }
        else if(view == tm_info_view_player_btn){
            HomeNavController.navigate(R.id.listOfPlayersFragment);
        }

    }
}
