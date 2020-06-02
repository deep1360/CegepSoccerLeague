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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class SelectMatchFragment extends Fragment {


    private FirebaseAuth mAuth;
    private FirebaseUser user;
    public NavController HomeNavController;
    private Context context;
    private FirebaseFirestore db;

    private RecyclerView played_matches_list_recycler_view;
    private ArrayList<Schedules_List_model> schedulesArrayList;
    private Select_Match_Rec_adapter selectMatch_rec_adapter;

    public SelectMatchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_select_match, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity().getApplicationContext();
        HomeNavController = Navigation.findNavController(getActivity(), R.id.home_host_fragment);

        played_matches_list_recycler_view = view.findViewById(R.id.played_matches_list_recycler_view);

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

        selectMatch_rec_adapter = new Select_Match_Rec_adapter(schedulesArrayList,context);
        played_matches_list_recycler_view.setLayoutManager(new LinearLayoutManager(context));
        played_matches_list_recycler_view.setAdapter(selectMatch_rec_adapter);

        selectMatch_rec_adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
                int position = viewHolder.getAdapterPosition();
                HomeNavController.navigate(R.id.addScoreboardFragment);
            }
        });

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        //accessing current user
        user = mAuth.getCurrentUser();
        // Access a Cloud Firestore instance from your Fragment
        db = FirebaseFirestore.getInstance();
    }
}
