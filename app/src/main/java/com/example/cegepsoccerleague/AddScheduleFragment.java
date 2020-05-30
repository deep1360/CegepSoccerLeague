package com.example.cegepsoccerleague;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

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

import java.util.Calendar;

public class AddScheduleFragment extends Fragment implements View.OnClickListener{

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private Context context;
    public NavController HomeNavController;

    private TextView match_date_txt_view,match_date_error,match_time_txt_view,match_time_error;
    private TextInputLayout match_location_layout, match_team1_layout, match_team2_layout;
    private MaterialButton add_schedule_btn;

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

        match_date_txt_view.setOnClickListener(this);
        match_time_txt_view.setOnClickListener(this);
        add_schedule_btn.setOnClickListener(this);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();
        //Get Current User refernece
        user = mAuth.getCurrentUser();



    }

    @Override
    public void onClick(View view) {
        if(view == match_date_txt_view){
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
            datePicker.show();
            datePicker.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
            datePicker.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextSize(18);
            datePicker.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTypeface(datePicker.getButton(DatePickerDialog.BUTTON_NEGATIVE).getTypeface(),Typeface.BOLD);

            datePicker.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
            datePicker.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextSize(18);
            datePicker.getButton(DatePickerDialog.BUTTON_POSITIVE).setTypeface(datePicker.getButton(DatePickerDialog.BUTTON_POSITIVE).getTypeface(),Typeface.BOLD);

        }
        else if(view == match_time_txt_view){
            match_time_error.setVisibility(View.GONE);
            if(match_date_txt_view.getText().toString().equals("Select Date")){
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
            timePicker.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTypeface(timePicker.getButton(DatePickerDialog.BUTTON_NEGATIVE).getTypeface(),Typeface.BOLD);

            timePicker.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
            timePicker.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextSize(18);
            timePicker.getButton(DatePickerDialog.BUTTON_POSITIVE).setTypeface(timePicker.getButton(DatePickerDialog.BUTTON_POSITIVE).getTypeface(),Typeface.BOLD);

        }
        else if(view == add_schedule_btn){
            match_date_error.setVisibility(View.GONE);
            match_time_error.setVisibility(View.GONE);
            match_location_layout.setErrorEnabled(false);
            match_team1_layout.setErrorEnabled(false);
            match_team2_layout.setErrorEnabled(false);

            Calendar c = Calendar.getInstance();
            int yy = c.get(Calendar.YEAR);
            int mm = c.get(Calendar.MONTH)+1;
            int dd = c.get(Calendar.DAY_OF_MONTH);
            int Hour = c.get(Calendar.HOUR_OF_DAY);
            int Minute = c.get(Calendar.MINUTE);
            final String today_date = yy + "-" + mm + "-" + dd;

            if(match_date_txt_view.getText().toString().equals("Select Date")){
                match_date_error.setVisibility(View.VISIBLE);
            }
            else if(match_time_txt_view.getText().toString().equals("Select Time")){
                match_time_error.setVisibility(View.VISIBLE);
                match_time_error.setText("Required fields are missing!");
            }
            else if(TextUtils.isEmpty(match_location_layout.getEditText().getText().toString().trim())){
                match_location_layout.setError("Required fields are missing!");
            }
            else if(TextUtils.isEmpty(match_team1_layout.getEditText().getText().toString().trim())){
                match_team1_layout.setError("Required fields are missing!");
            }
            else if(TextUtils.isEmpty(match_team2_layout.getEditText().getText().toString().trim())){
                match_team2_layout.setError("Required fields are missing!");
            }
            else if(match_date_txt_view.getText().toString().equals(today_date)){
                int selected_hour = Integer.parseInt(match_time_txt_view.getText().toString().split(":")[0]);
                int selected_minute = Integer.parseInt(match_time_txt_view.getText().toString().split(":")[1]);
                if (selected_hour >= Hour){
                    if(selected_minute<Minute){
                        match_time_error.setVisibility(View.VISIBLE);
                        match_time_error.setText("Please enter valid time!");
                    }
                    else {
                        HomeNavController.popBackStack();
                    }
                }
                else {
                    match_time_error.setVisibility(View.VISIBLE);
                    match_time_error.setText("Please enter valid time!");
                }
            }


        }
    }
}
