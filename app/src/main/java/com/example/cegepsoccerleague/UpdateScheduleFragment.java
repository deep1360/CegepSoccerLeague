package com.example.cegepsoccerleague;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.ImageView;
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
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class UpdateScheduleFragment extends Fragment implements View.OnClickListener{

    public NavController HomeNavController;
    Bundle dataBundle;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private Context context;
    private TextView update_match_date_txt_view, update_match_date_error, update_match_time_txt_view, update_match_time_error, us_team1_name, us_team2_name;
    private TextInputLayout update_match_location_layout;
    private MaterialButton update_schedule_btn;
    private TextView update_team1_team2_error_txt;
    private ImageView us_team1_img_view, us_team2_img_view;

    public UpdateScheduleFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_schedule, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity().getApplicationContext();
        HomeNavController = Navigation.findNavController(getActivity(), R.id.home_host_fragment);


        update_match_date_txt_view = view.findViewById(R.id.update_match_date_txt_view);
        update_match_date_error = view.findViewById(R.id.update_match_date_error);
        update_match_time_txt_view = view.findViewById(R.id.update_match_time_txt_view);
        update_match_time_error = view.findViewById(R.id.update_match_time_error);
        update_match_location_layout = view.findViewById(R.id.update_match_location_layout);
        update_schedule_btn = view.findViewById(R.id.update_schedule_btn);
        update_team1_team2_error_txt = view.findViewById(R.id.update_team1_team2_error_txt);
        us_team1_img_view = view.findViewById(R.id.us_team1_img_view);
        us_team2_img_view = view.findViewById(R.id.us_team2_img_view);
        us_team1_name = view.findViewById(R.id.us_team1_name);
        us_team2_name = view.findViewById(R.id.us_team2_name);


        update_match_date_txt_view.setOnClickListener(this);
        update_match_time_txt_view.setOnClickListener(this);
        update_schedule_btn.setOnClickListener(this);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();
        //Get Current User refernece
        user = mAuth.getCurrentUser();


        if (getArguments() != null) {
            dataBundle = getArguments();
            /*----------- Displaying all Data of Schedule On Screen --------------*/
            update_match_date_txt_view.setText(getArguments().getString("match_date"));
            update_match_time_txt_view.setText(getArguments().getString("match_time"));
            update_match_location_layout.getEditText().setText(getArguments().getString("match_location"));
            us_team1_name.setText(dataBundle.getString("team1_name"));
            us_team2_name.setText(dataBundle.getString("team2_name"));
            /*----------- Decoding Base64 Image String and Displaying it On Image View-----------*/
            if(!getArguments().getString("team1_icon").equals("No Icon")){
                byte[] decodedString = Base64.decode(getArguments().getString("team1_icon"), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                us_team1_img_view.setImageBitmap(decodedByte);
            }

            if(!getArguments().getString("team2_icon").equals("No Icon")){
                byte[] decodedString = Base64.decode(getArguments().getString("team2_icon"), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                us_team2_img_view.setImageBitmap(decodedByte);
            }

        }


    }

    @Override
    public void onClick(View view) {
        if (view == update_match_date_txt_view) {
            update_match_time_error.setVisibility(View.GONE);
            update_match_date_error.setVisibility(View.GONE);

            DatePickerDialog datePicker;
            final Calendar calendar = Calendar.getInstance();
            int yy = calendar.get(Calendar.YEAR);
            int mm = calendar.get(Calendar.MONTH);
            int dd = calendar.get(Calendar.DAY_OF_MONTH);

            datePicker = new DatePickerDialog(getActivity(), R.style.PickerTheme, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int date) {
                    String match_date = year + "-" + (month + 1) + "-" + date;
                    update_match_date_txt_view.setText(match_date);
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
        else if (view == update_match_time_txt_view) {
            update_match_time_error.setVisibility(View.GONE);
            if (update_match_date_txt_view.getText().toString().equals("Select Date")) {
                update_match_time_error.setVisibility(View.VISIBLE);
                update_match_time_error.setText("Please select date first!");
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
                    update_match_time_txt_view.setText(match_time);
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
        else if (view == update_schedule_btn) {
            update_team1_team2_error_txt.setVisibility(View.GONE);
            update_match_date_error.setVisibility(View.GONE);
            update_match_time_error.setVisibility(View.GONE);
            update_match_location_layout.setErrorEnabled(false);

            Calendar c = Calendar.getInstance();
            int yy = c.get(Calendar.YEAR);
            int mm = c.get(Calendar.MONTH) + 1;
            int dd = c.get(Calendar.DAY_OF_MONTH);
            int Hour = c.get(Calendar.HOUR_OF_DAY);
            int Minute = c.get(Calendar.MINUTE);
            final String today_date = yy + "-" + mm + "-" + dd;

            if (update_match_date_txt_view.getText().toString().equals("Select Date")) {
                update_match_date_error.setVisibility(View.VISIBLE);
            } else if (update_match_time_txt_view.getText().toString().equals("Select Time")) {
                update_match_time_error.setVisibility(View.VISIBLE);
                update_match_time_error.setText("Required fields are missing!");
            } else if (TextUtils.isEmpty(update_match_location_layout.getEditText().getText().toString().trim())) {
                update_match_location_layout.setError("Required fields are missing!");
            } else if (update_match_date_txt_view.getText().toString().equals(today_date)) {
                int selected_hour = Integer.parseInt(update_match_time_txt_view.getText().toString().split(":")[0]);
                int selected_minute = Integer.parseInt(update_match_time_txt_view.getText().toString().split(":")[1]);
                if (selected_hour > Hour) {
                    UpdateSchedule();
                }
                else if(selected_hour == Hour){
                    if (selected_minute < Minute) {
                        update_match_time_error.setVisibility(View.VISIBLE);
                        update_match_time_error.setText("Please enter valid time!");
                    } else {
                        UpdateSchedule();
                    }
                }
                else {
                    update_match_time_error.setVisibility(View.VISIBLE);
                    update_match_time_error.setText("Please enter valid time!");
                }
            } else {
                UpdateSchedule();
            }
        }
    }

    /*----------- Update Schedule Method Allows to Update Only Date Time and Location -----------*/
    private void UpdateSchedule(){
        final String match_date = update_match_date_txt_view.getText().toString().trim();
        final String match_time = update_match_time_txt_view.getText().toString().trim();
        final String match_location = update_match_location_layout.getEditText().getText().toString().trim();
        final String team1_id = dataBundle.getString("team1_id");
        final String team2_id = dataBundle.getString("team2_id");
        final String league_id = dataBundle.getString("league_id");
        update_schedule_btn.setEnabled(false);
        db.collection("schedules")
                .whereIn("team1_id", Arrays.asList(team1_id, team2_id))
                .whereEqualTo("match_date", match_date)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().size() > 0) {
                        update_team1_team2_error_txt.setVisibility(View.VISIBLE);
                        update_schedule_btn.setEnabled(true);
                    } else {
                        db.collection("schedules")
                                .whereIn("team2_id", Arrays.asList(team1_id, team2_id))
                                .whereEqualTo("match_date", match_date)
                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult().size() > 0) {
                                        update_team1_team2_error_txt.setVisibility(View.VISIBLE);
                                        update_schedule_btn.setEnabled(true);
                                    } else {
                                        //Creating a data object to add schedule in database
                                        final Map<String, Object> schedule_data = new HashMap<>();
                                        schedule_data.put("match_date", match_date);
                                        schedule_data.put("match_time", match_time);
                                        schedule_data.put("match_location", match_location);
                                        schedule_data.put("team1_id", team1_id);
                                        schedule_data.put("team2_id", team2_id);
                                        schedule_data.put("league_id", league_id);

                                        //Updating Match Schedule in database
                                        db.collection("schedules").document(dataBundle.getString("match_id"))
                                                .set(schedule_data)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        update_schedule_btn.setEnabled(true);
                                                        Toast.makeText(context, "Schedule Added Successfully!", Toast.LENGTH_LONG).show();
                                                        HomeNavController.popBackStack();
                                                        HomeNavController.popBackStack();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        update_schedule_btn.setEnabled(true);
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
