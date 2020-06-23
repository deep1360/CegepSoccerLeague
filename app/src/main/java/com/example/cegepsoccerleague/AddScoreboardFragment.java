package com.example.cegepsoccerleague;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


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
    private Bundle dataBundle;
    public Toolbar HomeToolbar;
    private static ProgressDialog progressDialog;

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
        HomeToolbar = getActivity().findViewById(R.id.home_toolbar);

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

        if(getArguments()!=null){
            dataBundle = getArguments();
            add_sb_team1_name.setText(dataBundle.getString("team1_name"));
            add_sb_team2_name.setText(dataBundle.getString("team2_name"));
            add_sb_date.setText(dataBundle.getString("match_date"));
            add_sb_time.setText(dataBundle.getString("match_time"));

            if(!dataBundle.getString("team1_icon").equals("No Icon")){
                byte[] decodedString = Base64.decode(dataBundle.getString("team1_icon"), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                add_sb_team1_img_view.setImageBitmap(decodedByte);
            }

            if(!dataBundle.getString("team2_icon").equals("No Icon")){
                byte[] decodedString = Base64.decode(dataBundle.getString("team2_icon"), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                add_sb_team2_img_view.setImageBitmap(decodedByte);
            }

            if(dataBundle.getString("from")!=null && dataBundle.getString("from").equals("update-score")){
                HomeToolbar.setTitle("Update Score");
                add_new_score_btn.setText("Update Match Score");

                add_sb_team1_goals.getEditText().setText(dataBundle.getString("team1_goals"));
                add_sb_team1_fouls.getEditText().setText(dataBundle.getString("team1_fouls"));
                add_sb_team1_kicks.getEditText().setText(dataBundle.getString("team1_freeKicks"));
                add_sb_team1_corners.getEditText().setText(dataBundle.getString("team1_corners"));
                add_sb_team1_goal_saved.getEditText().setText(dataBundle.getString("team1_goalSaved"));

                add_sb_team2_goals.getEditText().setText(dataBundle.getString("team2_goals"));
                add_sb_team2_fouls.getEditText().setText(dataBundle.getString("team2_fouls"));
                add_sb_team2_kicks.getEditText().setText(dataBundle.getString("team2_freeKicks"));
                add_sb_team2_corners.getEditText().setText(dataBundle.getString("team2_corners"));
                add_sb_team2_goal_saved.getEditText().setText(dataBundle.getString("team2_goalSaved"));
            }
        }
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
            else {
                addScore();
            }
        }

    }

    private void addScore() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);

        if(getArguments().getString("from")!=null && getArguments().getString("from").equals("update-score")){
            progressDialog.setMessage("Updating Score");
        }
        else {
            progressDialog.setMessage("Creating New Score");
        }
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setProgress(0);
        progressDialog.show();
        add_new_score_btn.setEnabled(false);
        String team1_id = dataBundle.getString("team1_id");
        String team2_id = dataBundle.getString("team2_id");
        final String match_id = dataBundle.getString("match_id");
        String league_id = dataBundle.getString("league_id");
        String match_date = dataBundle.getString("match_date");
        String match_time = dataBundle.getString("match_time");
        String match_location = dataBundle.getString("match_location");
        String team1_goals = add_sb_team1_goals.getEditText().getText().toString();
        String team1_fouls = add_sb_team1_fouls.getEditText().getText().toString();
        String team1_freeKicks = add_sb_team1_kicks.getEditText().getText().toString();
        String team1_corners = add_sb_team1_corners.getEditText().getText().toString();
        String team1_goalSaved = add_sb_team1_goal_saved.getEditText().getText().toString();

        String team2_goals = add_sb_team2_goals.getEditText().getText().toString();
        String team2_fouls = add_sb_team2_fouls.getEditText().getText().toString();
        String team2_freeKicks = add_sb_team2_kicks.getEditText().getText().toString();
        String team2_corners = add_sb_team2_corners.getEditText().getText().toString();
        String team2_goalSaved = add_sb_team2_goal_saved.getEditText().getText().toString();

        //Creating a data object to add in database
        final Map<String, Object> score_data = new HashMap<>();
        score_data.put("match_id", match_id);
        score_data.put("league_id", league_id);
        score_data.put("team1_id",team1_id);
        score_data.put("team2_id", team2_id);
        score_data.put("team1_goals",team1_goals);
        score_data.put("team1_fouls",team1_fouls);
        score_data.put("team1_freeKicks",team1_freeKicks);
        score_data.put("team1_corners",team1_corners);
        score_data.put("team1_goalSaved",team1_goalSaved);
        score_data.put("team2_goals",team2_goals);
        score_data.put("team2_fouls",team2_fouls);
        score_data.put("team2_freeKicks",team2_freeKicks);
        score_data.put("team2_corners",team2_corners);
        score_data.put("team2_goalSaved",team2_goalSaved);
        score_data.put("match_date",match_date);
        score_data.put("match_time",match_time);
        score_data.put("match_location",match_location);

        if(dataBundle.getString("from")!=null && dataBundle.getString("from").equals("update-score")) {
            db.collection("scores").document(dataBundle.getString("score_id"))
                    .set(score_data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressDialog.dismiss();
                            add_new_score_btn.setEnabled(true);
                            Toast.makeText(context, "Player Updated Successfully!", Toast.LENGTH_LONG).show();
                            HomeNavController.popBackStack();
                            HomeNavController.popBackStack();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            add_new_score_btn.setEnabled(true);
                            Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }
        else {
            // Add a new document with auto generated ID
            db.collection("scores")
                    .add(score_data)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            progressDialog.dismiss();
                            db.collection("schedules").document(match_id).delete();
                            add_new_score_btn.setEnabled(true);
                            Toast.makeText(context, "Score Added Successfully!", Toast.LENGTH_LONG).show();
                            HomeNavController.popBackStack();
                            HomeNavController.popBackStack();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            add_new_score_btn.setEnabled(true);
                            Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }


    }
}
