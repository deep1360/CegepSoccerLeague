package com.example.cegepsoccerleague;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListOfSchedulesFragment extends Fragment implements View.OnClickListener{

    FloatingActionButton create_schedule_btn;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    public NavController HomeNavController;
    private Context context;
    private FirebaseFirestore db;

    private RecyclerView schedules_list_recycler_view;
    private ArrayList<Schedules_List_model> schedulesArrayList;
    private Schedules_Rec_adapter schedules_rec_adapter;


    public ListOfSchedulesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_of_schedules, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity().getApplicationContext();
        HomeNavController = Navigation.findNavController(getActivity(), R.id.home_host_fragment);

        create_schedule_btn = view.findViewById(R.id.create_schedule_btn);
        schedules_list_recycler_view = view.findViewById(R.id.schedules_list_recycler_view);

        create_schedule_btn.setOnClickListener(this);


        /*--------Your Recipes Adapter Configuration--------*/
        schedulesArrayList = new ArrayList<Schedules_List_model>();
        schedulesArrayList.add(new Schedules_List_model("1","Canada","2020-06-20","14:00","1",
                "1","Barcelona","No Icon",
                "1","Manchester","No Icon"));
        schedulesArrayList.add(new Schedules_List_model("1","Canada","2020-06-24","10:00","1",
                "1","Arsenal FC","No Icon",
                "1","Chelsea FC","No Icon"));
        schedulesArrayList.add(new Schedules_List_model("1","Canada","2020-06-26","9:00","1",
                "1","Club SOCCER","No Icon",
                "1","FCB","No Icon"));

        schedules_rec_adapter = new Schedules_Rec_adapter(schedulesArrayList,context);
        schedules_list_recycler_view.setLayoutManager(new LinearLayoutManager(context));
        schedules_list_recycler_view.setAdapter(schedules_rec_adapter);

        schedules_rec_adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
                int position = viewHolder.getAdapterPosition();
            }
        });

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        //accessing current user
        user = mAuth.getCurrentUser();
        // Access a Cloud Firestore instance from your Fragment
        db = FirebaseFirestore.getInstance();

        if(getArguments()!=null){
            if(getArguments().getString("from")!=null && getArguments().getString("from").equals("league Features")){
                create_schedule_btn.setVisibility(View.VISIBLE);
            }
        }
        else {
            create_schedule_btn.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        if(view == create_schedule_btn){
            HomeNavController.navigate(R.id.addScheduleFragment);
        }
    }
}
