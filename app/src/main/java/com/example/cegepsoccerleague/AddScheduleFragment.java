package com.example.cegepsoccerleague;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddScheduleFragment extends Fragment implements View.OnClickListener {

    public NavController HomeNavController;
    Bundle dataBundle;
    ArrayList<String> team_names,team2_names, team_ids;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private Context context;
    private AutoCompleteTextView select_team1_name_txtview, select_team2_name_txtview;
    private TextView match_date_txt_view, match_date_error, match_time_txt_view, match_time_error;
    private TextInputLayout match_location_layout, match_team1_layout, match_team2_layout;
    private MaterialButton add_schedule_btn;
    private TextView team1_team2_error_txt;
    private static ProgressDialog progressDialog;

    public AddScheduleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_schedule, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity().getApplicationContext();
        HomeNavController = Navigation.findNavController(getActivity(), R.id.home_host_fragment);


        match_date_txt_view = view.findViewById(R.id.match_date_txt_view);
        match_date_error = view.findViewById(R.id.match_date_error);
        match_time_txt_view = view.findViewById(R.id.match_time_txt_view);
        match_time_error = view.findViewById(R.id.match_time_error);
        match_location_layout = view.findViewById(R.id.match_location_layout);
        match_team1_layout = view.findViewById(R.id.match_team1_layout);
        match_team2_layout = view.findViewById(R.id.match_team2_layout);
        add_schedule_btn = view.findViewById(R.id.add_schedule_btn);
        select_team1_name_txtview = view.findViewById(R.id.select_team1_name_txtview);
        select_team2_name_txtview = view.findViewById(R.id.select_team2_name_txtview);
        team1_team2_error_txt = view.findViewById(R.id.team1_team2_error_txt);

        match_date_txt_view.setOnClickListener(this);
        match_time_txt_view.setOnClickListener(this);
        add_schedule_btn.setOnClickListener(this);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();
        //Get Current User refernece
        user = mAuth.getCurrentUser();


        team_names = new ArrayList<String>();
        team_ids = new ArrayList<String>();
        team2_names = new ArrayList<String>();
        if (getArguments() != null) {
            dataBundle = getArguments();
            getTeamNames(dataBundle.getString("league_id"));
        }


    }

    private void getTeamNames(String League_id) {
        db.collection("teams").whereEqualTo("league_id", League_id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        team_names.add(document.get("team_name").toString());
                        team_ids.add(document.getId());
                    }

                    /*--------Creating Adapter for Showing Drop Down Menu On Team 1 and Team 2 Selection-----------*/
                    final ArrayAdapter<String> TeamNameAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, team_names);
                    select_team1_name_txtview.setAdapter(TeamNameAdapter);
                    select_team1_name_txtview.setInputType(0);

                    select_team1_name_txtview.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            match_team1_layout.setErrorEnabled(false);
                            if (team_names.size() > 0) {
                                // show all suggestions
                                if (!select_team1_name_txtview.getText().toString().equals("")) {
                                    TeamNameAdapter.getFilter().filter(null);
                                }
                                select_team1_name_txtview.showDropDown();
                            }
                            return false;
                        }
                    });

                    select_team2_name_txtview.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            if(TextUtils.isEmpty(select_team1_name_txtview.getText().toString().trim()))
                            {
                                match_team1_layout.setError("Select Team1 First!");
                            }
                            else {
                                if(team_names.indexOf(select_team1_name_txtview.getText().toString().trim())!=-1){
                                    team2_names.clear();
                                    for(String team_name : team_names ){
                                        if(!team_name.equals(select_team1_name_txtview.getText().toString().trim())) {
                                            team2_names.add(team_name);
                                        }
                                    }
                                    Log.d("Team1_Size",String.valueOf(team_names.size()));

                                    final ArrayAdapter<String> Team2NameAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, team2_names);
                                    select_team2_name_txtview.setAdapter(Team2NameAdapter);
                                    select_team2_name_txtview.setInputType(0);

                                    if (team2_names.size() > 0) {
                                        // show all suggestions
                                        if (!select_team2_name_txtview.getText().toString().equals("")) {
                                            TeamNameAdapter.getFilter().filter(null);
                                        }
                                        select_team2_name_txtview.showDropDown();
                                    }
                                }
                                else {
                                    match_team1_layout.setError("Please Select Valid Team Name");
                                }
                            }
                            return false;
                        }
                    });
                    /*--------Show Dropdown Code Finishes Here-----*/

                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == match_date_txt_view) {
            match_time_error.setVisibility(View.GONE);
            match_date_error.setVisibility(View.GONE);

            DatePickerDialog datePicker;
            final Calendar calendar = Calendar.getInstance();
            int yy = calendar.get(Calendar.YEAR);
            int mm = calendar.get(Calendar.MONTH);
            int dd = calendar.get(Calendar.DAY_OF_MONTH);

            datePicker = new DatePickerDialog(getActivity(), R.style.PickerTheme, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int date) {
                    String match_date = year + "-" + (month + 1) + "-" + date;
                    match_date_txt_view.setText(match_date);
                }
            }, yy, mm, dd);
            datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            Calendar max_calender = Calendar.getInstance();
            max_calender.set(yy,mm+4,dd);
            datePicker.getDatePicker().setMaxDate(max_calender.getTimeInMillis()-1000);
            datePicker.show();
            datePicker.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
            datePicker.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextSize(18);
            datePicker.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTypeface(datePicker.getButton(DatePickerDialog.BUTTON_NEGATIVE).getTypeface(), Typeface.BOLD);

            datePicker.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
            datePicker.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextSize(18);
            datePicker.getButton(DatePickerDialog.BUTTON_POSITIVE).setTypeface(datePicker.getButton(DatePickerDialog.BUTTON_POSITIVE).getTypeface(), Typeface.BOLD);

        }
        else if (view == match_time_txt_view) {
            match_time_error.setVisibility(View.GONE);
            if (match_date_txt_view.getText().toString().equals("Select Date")) {
                match_time_error.setVisibility(View.VISIBLE);
                match_time_error.setText("Please select date first!");
                return;
            }
            TimePickerDialog timePicker;
            final Calendar calendar = Calendar.getInstance();
            timePicker = new TimePickerDialog(getActivity(), R.style.PickerTheme, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int Hour, int Minute) {
                    String H;
                    if (Hour < 10) {
                        H = "0" + Hour;
                    } else {
                        H = String.valueOf(Hour);
                    }

                    String M;
                    if (Minute < 10) {
                        M = "0" + Minute;
                    } else {
                        M = String.valueOf(Minute);
                    }

                    String match_time = H + ":" + M;
                    match_time_txt_view.setText(match_time);
                }
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
            timePicker.show();
            timePicker.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
            timePicker.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextSize(18);
            timePicker.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTypeface(timePicker.getButton(DatePickerDialog.BUTTON_NEGATIVE).getTypeface(), Typeface.BOLD);

            timePicker.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
            timePicker.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextSize(18);
            timePicker.getButton(DatePickerDialog.BUTTON_POSITIVE).setTypeface(timePicker.getButton(DatePickerDialog.BUTTON_POSITIVE).getTypeface(), Typeface.BOLD);

        }
        else if (view == add_schedule_btn) {
            team1_team2_error_txt.setVisibility(View.GONE);
            match_date_error.setVisibility(View.GONE);
            match_time_error.setVisibility(View.GONE);
            match_location_layout.setErrorEnabled(false);
            match_team1_layout.setErrorEnabled(false);
            match_team2_layout.setErrorEnabled(false);

            Calendar c = Calendar.getInstance();
            int yy = c.get(Calendar.YEAR);
            int mm = c.get(Calendar.MONTH) + 1;
            int dd = c.get(Calendar.DAY_OF_MONTH);
            int Hour = c.get(Calendar.HOUR_OF_DAY);
            int Minute = c.get(Calendar.MINUTE);
            final String today_date = yy + "-" + mm + "-" + dd;

            if (match_date_txt_view.getText().toString().equals("Select Date")) {
                match_date_error.setVisibility(View.VISIBLE);
            } else if (match_time_txt_view.getText().toString().equals("Select Time")) {
                match_time_error.setVisibility(View.VISIBLE);
                match_time_error.setText("Required fields are missing!");
            } else if (TextUtils.isEmpty(match_location_layout.getEditText().getText().toString().trim())) {
                match_location_layout.setError("Required fields are missing!");
            } else if (TextUtils.isEmpty(select_team1_name_txtview.getText().toString().trim())) {
                match_team1_layout.setError("Required fields are missing!");
            } else if (TextUtils.isEmpty(select_team2_name_txtview.getText().toString().trim())) {
                match_team2_layout.setError("Required fields are missing!");
            } else if (match_date_txt_view.getText().toString().equals(today_date)) {
                int selected_hour = Integer.parseInt(match_time_txt_view.getText().toString().split(":")[0]);
                int selected_minute = Integer.parseInt(match_time_txt_view.getText().toString().split(":")[1]);
                if (selected_hour > Hour) {
                    AddSchedule();
                }
                else if(selected_hour == Hour){
                    if (selected_minute < Minute) {
                        match_time_error.setVisibility(View.VISIBLE);
                        match_time_error.setText("Please enter valid time!");
                    } else {
                        AddSchedule();
                    }
                }
                else {
                    match_time_error.setVisibility(View.VISIBLE);
                    match_time_error.setText("Please enter valid time!");
                }
            } else {
                AddSchedule();
            }


        }
    }

    /*----------- Method Created For Adding Schedule---------------*/
    private void AddSchedule() {
        final String match_date = match_date_txt_view.getText().toString().trim();
        final String match_time = match_time_txt_view.getText().toString().trim();
        final String match_location = match_location_layout.getEditText().getText().toString().trim();
        if (select_team1_name_txtview.getText().toString().trim().equals(select_team2_name_txtview.getText().toString().trim())) {
            match_team2_layout.setError("Team 1 and Team 2 can not be same");
        } else {
            int team1index = team_names.indexOf(select_team1_name_txtview.getText().toString().trim());
            int team2index = team_names.indexOf(select_team2_name_txtview.getText().toString().trim());

            if (team1index == -1) {
                match_team1_layout.setError("Please Select Valid Team Name");
            } else if (team2index == -1) {
                match_team2_layout.setError("Please Select Valid Team Name");
            } else {
                final String team1_id = team_ids.get(team1index);
                final String team2_id = team_ids.get(team2index);
                add_schedule_btn.setEnabled(false);
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Creating New Team");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setProgress(0);
                progressDialog.show();
                db.collection("schedules")
                        .whereIn("team1_id", Arrays.asList(team1_id, team2_id))
                        .whereEqualTo("match_date", match_date)
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().size() > 0) {
                                team1_team2_error_txt.setVisibility(View.VISIBLE);
                                progressDialog.dismiss();
                                add_schedule_btn.setEnabled(true);
                            } else {
                                db.collection("schedules")
                                        .whereIn("team2_id", Arrays.asList(team1_id, team2_id))
                                        .whereEqualTo("match_date", match_date)
                                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            if (task.getResult().size() > 0) {
                                                team1_team2_error_txt.setVisibility(View.VISIBLE);
                                                progressDialog.dismiss();
                                                add_schedule_btn.setEnabled(true);
                                            } else {
                                                //Creating a data object to add schedule in database
                                                final Map<String, Object> schedule_data = new HashMap<>();
                                                schedule_data.put("match_date", match_date);
                                                schedule_data.put("match_time", match_time);
                                                schedule_data.put("match_location", match_location);
                                                schedule_data.put("team1_id", team1_id);
                                                schedule_data.put("team2_id", team2_id);
                                                schedule_data.put("league_id", dataBundle.getString("league_id"));

                                                //Adding Match Schedule in database

                                                db.collection("schedules")
                                                        .add(schedule_data)
                                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                            @Override
                                                            public void onSuccess(DocumentReference documentReference) {
                                                                add_schedule_btn.setEnabled(true);
                                                                progressDialog.dismiss();
                                                                Toast.makeText(context, "Schedule Added Successfully!", Toast.LENGTH_LONG).show();
                                                                HomeNavController.popBackStack();
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                progressDialog.dismiss();
                                                                add_schedule_btn.setEnabled(true);
                                                                Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                                            }
                                                        });
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    }
                });
            }
        }
    }
}