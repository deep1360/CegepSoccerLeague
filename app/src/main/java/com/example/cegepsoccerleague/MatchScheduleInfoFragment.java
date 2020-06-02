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
public class MatchScheduleInfoFragment extends Fragment{

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private Context context;
    public NavController HomeNavController;
    private ImageView schedule_team1_info_img_view, schedule_team2_info_img_view;
    private TextView schedule_info_date, schedule_info_time, schedule_info_location_txt_view, schedule_team1_info_name,
            schedule_team2_info_name, si_team1_player_text_view, si_team2_player_text_view;

    public MatchScheduleInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_match_schedule_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity().getApplicationContext();
        HomeNavController = Navigation.findNavController(getActivity(), R.id.home_host_fragment);

        schedule_team1_info_img_view = view.findViewById(R.id.schedule_team1_info_img_view);
        schedule_team2_info_img_view = view.findViewById(R.id.schedule_team2_info_img_view);
        schedule_info_date = view.findViewById(R.id.schedule_info_date);
        schedule_info_time = view.findViewById(R.id.schedule_info_time);
        schedule_info_location_txt_view = view.findViewById(R.id.schedule_info_location_txt_view);
        schedule_team1_info_name = view.findViewById(R.id.schedule_team1_info_name);
        schedule_team2_info_name = view.findViewById(R.id.schedule_team2_info_name);
        si_team1_player_text_view = view.findViewById(R.id.si_team1_player_text_view);
        si_team2_player_text_view = view.findViewById(R.id.si_team2_player_text_view);

        si_team1_player_text_view.setText("Xavi\nCristiano Ronaldo\nLionel Messi\nAndres Iniesta\nZlatan Ibrahimovic\nRadamel Falcao\nRobin van Persie");
        si_team2_player_text_view.setText("Andrea Pirlo\nYaya Toure\nEdinson Cavani\nSergio Aguero\nIker Casillas\nNeymar\nSergio Busquets");


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
            HomeNavController.navigate(R.id.addScheduleFragment);
        }
        else if(item.getItemId()== R.id.delete_option){
            DeleteSchedule();
        }

        return super.onOptionsItemSelected(item);
    }

    private void DeleteSchedule(){
        /*------------ Configuration of Alert Dialog----------*/
        AlertDialog.Builder DAC_builder = new AlertDialog.Builder(getActivity())
                .setView(R.layout.delete_schedule_dialog_layout);
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
