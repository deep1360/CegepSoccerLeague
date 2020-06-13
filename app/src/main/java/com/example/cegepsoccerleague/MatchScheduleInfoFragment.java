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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;


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

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();
        //Get Current User refernece
        user = mAuth.getCurrentUser();

        if(getArguments()!=null){
            /*----------- Displaying all Data of Schedule On Screen --------------*/
            schedule_info_date.setText(getArguments().getString("match_date"));
            schedule_info_time.setText(getArguments().getString("match_time"));
            schedule_info_location_txt_view.setText(getArguments().getString("match_location"));
            schedule_team1_info_name.setText(getArguments().getString("team1_name"));
            schedule_team2_info_name.setText(getArguments().getString("team2_name"));
            /*----------- Decoding Base64 Image String and Displaying it On Image View-----------*/
            if(!getArguments().getString("team1_icon").equals("No Icon")){
                byte[] decodedString = Base64.decode(getArguments().getString("team1_icon"), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                schedule_team1_info_img_view.setImageBitmap(decodedByte);
            }

            if(!getArguments().getString("team2_icon").equals("No Icon")){
                byte[] decodedString = Base64.decode(getArguments().getString("team2_icon"), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                schedule_team2_info_img_view.setImageBitmap(decodedByte);
            }
            /*------------- Fetcing Players List From Database and Displaying it On Screen --------------*/
            db.collection("players").whereEqualTo("team_id",getArguments().getString("team1_id"))
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                String team1_players = "";
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    team1_players = team1_players+document.get("first_name")+" "+document.get("last_name")+"\n";
                                }
                                si_team1_player_text_view.setText(team1_players.toString().trim());
                            } else {
                                Toast.makeText(context,task.getException().getLocalizedMessage(),Toast.LENGTH_LONG).show();
                            }
                        }
                    });

            db.collection("players").whereEqualTo("team_id",getArguments().getString("team2_id"))
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                String team2_players = "";
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    team2_players = team2_players+document.get("first_name")+" "+document.get("last_name")+"\n";
                                }
                                si_team2_player_text_view.setText(team2_players.toString().trim());
                            } else {
                                Toast.makeText(context,task.getException().getLocalizedMessage(),Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*----------Hiding Edit and Delete Button For Guest Users------------*/
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
            Bundle dataBundle = getArguments();
            Calendar c = Calendar.getInstance();
            int yy = c.get(Calendar.YEAR);
            int mm = c.get(Calendar.MONTH) + 1;
            int dd = c.get(Calendar.DAY_OF_MONTH);
            String[] date = dataBundle.get("match_date").toString().split("-");
            if (Integer.parseInt(date[0]) > yy) {
                HomeNavController.navigate(R.id.updateScheduleFragment,dataBundle);
            }
            else if(Integer.parseInt(date[0]) == yy){
                if (Integer.parseInt(date[1]) > mm) {
                    HomeNavController.navigate(R.id.updateScheduleFragment,dataBundle);
                }
                else if(Integer.parseInt(date[1]) == mm){
                    if (Integer.parseInt(date[2]) >= dd) {
                        HomeNavController.navigate(R.id.updateScheduleFragment,dataBundle);
                    }
                    else {
                        Toast.makeText(context,"Match date is passed so It can not be updated!",Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(context,"Match date is passed so It can not be updated!",Toast.LENGTH_LONG).show();
                }
            }
            else {
                Toast.makeText(context,"Match date is passed so It can not be updated!",Toast.LENGTH_LONG).show();
            }
        }
        else if(item.getItemId()==R.id.delete_option){
            DeleteSchedule();
        }

        return super.onOptionsItemSelected(item);
    }

    private void DeleteSchedule(){
        /*------------ Configuration of Alert Dialog----------*/
        AlertDialog.Builder DAC_builder = new AlertDialog.Builder(getActivity())
                .setView(R.layout.delete_schedule_dialog_layout);
        final AlertDialog Delete_Schedule_Dialog = DAC_builder.create();
        Delete_Schedule_Dialog.show();
        final MaterialButton delete_cancel_btn = Delete_Schedule_Dialog.findViewById(R.id.delete_cancel_btn);
        final MaterialButton delelte_continue_btn = Delete_Schedule_Dialog.findViewById(R.id.delelte_continue_btn);

        delete_cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Delete_Schedule_Dialog.dismiss();
            }
        });

        delelte_continue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delelte_continue_btn.setEnabled(false);
                delete_cancel_btn.setEnabled(false);
                db.collection("schedules").document(getArguments().getString("match_id"))
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                delelte_continue_btn.setEnabled(true);
                                delete_cancel_btn.setEnabled(true);
                                Toast.makeText(context,"Schedule Deleted Successfully!",Toast.LENGTH_LONG).show();
                                Delete_Schedule_Dialog.dismiss();
                                HomeNavController.popBackStack();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                delelte_continue_btn.setEnabled(true);
                                delete_cancel_btn.setEnabled(true);
                                Toast.makeText(context,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                                Delete_Schedule_Dialog.dismiss();
                            }
                        });
            }
        });
    }
}
