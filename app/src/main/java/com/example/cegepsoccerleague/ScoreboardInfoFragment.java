package com.example.cegepsoccerleague;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
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

    private Bundle dataBundle;

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

        if(getArguments()!=null){
            dataBundle = getArguments();
            sb_info_date.setText(dataBundle.getString("match_date"));
            sb_info_time.setText(dataBundle.getString("match_time"));
            sb_info_team1_name.setText(dataBundle.getString("team1_name"));
            sb_info_team2_name.setText(dataBundle.getString("team2_name"));
            /*----------- Decoding Base64 Image String and Displaying it On Image View-----------*/
            if(!getArguments().getString("team1_icon").equals("No Icon")){
                byte[] decodedString = Base64.decode(getArguments().getString("team1_icon"), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                sb_info_team1_img_view.setImageBitmap(decodedByte);
            }

            if(!getArguments().getString("team2_icon").equals("No Icon")){
                byte[] decodedString = Base64.decode(getArguments().getString("team2_icon"), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                sb_info_team2_img_view.setImageBitmap(decodedByte);
            }

            sb_info_team1_goals.setText(dataBundle.getString("team1_goals"));
            sb_info_team1_fouls.setText(dataBundle.getString("team1_fouls"));
            sb_info_team1_kicks.setText(dataBundle.getString("team1_freeKicks"));
            sb_info_team1_corners.setText(dataBundle.getString("team1_corners"));
            sb_info_team1_goal_saved.setText(dataBundle.getString("team1_goalSaved"));

            sb_info_team2_goals.setText(dataBundle.getString("team2_goals"));
            sb_info_team2_fouls.setText(dataBundle.getString("team2_fouls"));
            sb_info_team2_kicks.setText(dataBundle.getString("team2_freeKicks"));
            sb_info_team2_corners.setText(dataBundle.getString("team2_corners"));
            sb_info_team2_goal_saved.setText(dataBundle.getString("team2_goalSaved"));

            /*---------- Checking for who won the match and displaying its name ----------*/
            if(Integer.parseInt(dataBundle.getString("team1_goals"))>Integer.parseInt(dataBundle.getString("team2_goals"))){
                sb_info_win_team_name.setText(dataBundle.getString("team1_name")+ " won the match!");
            }
            else if(Integer.parseInt(dataBundle.getString("team1_goals"))<Integer.parseInt(dataBundle.getString("team2_goals"))){
                sb_info_win_team_name.setText(dataBundle.getString("team2_name")+ " won the match!");
            }
            else {
                sb_info_win_team_name.setText("MATCH TIED!!!");
            }

        }
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

        if (item.getItemId()==R.id.edit_option){
            HomeNavController.navigate(R.id.addScoreboardFragment,dataBundle);
        }
        else if(item.getItemId()==R.id.delete_option){
            DeleteScore();
        }

        return super.onOptionsItemSelected(item);
    }

    private void DeleteScore(){
        /*------------ Configuration of Alert Dialog----------*/
        AlertDialog.Builder DAC_builder = new AlertDialog.Builder(getActivity())
                .setView(R.layout.delete_score_dialog_layout);
        final AlertDialog Delete_Score_Dialog = DAC_builder.create();
        Delete_Score_Dialog.show();
        final MaterialButton delete_cancel_btn = Delete_Score_Dialog.findViewById(R.id.delete_cancel_btn);
        final MaterialButton delelte_continue_btn = Delete_Score_Dialog.findViewById(R.id.delelte_continue_btn);

        delete_cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Delete_Score_Dialog.dismiss();
            }
        });

        delelte_continue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delelte_continue_btn.setEnabled(false);
                delete_cancel_btn.setEnabled(false);

                db.collection("scores").document(dataBundle.getString("score_id"))
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(context,"Score Deleted Successfully!",Toast.LENGTH_LONG).show();
                                delelte_continue_btn.setEnabled(true);
                                delete_cancel_btn.setEnabled(true);
                                Delete_Score_Dialog.dismiss();
                                HomeNavController.popBackStack();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                                delelte_continue_btn.setEnabled(true);
                                delete_cancel_btn.setEnabled(true);
                                Delete_Score_Dialog.dismiss();
                            }
                        });
            }
        });
    }
}
