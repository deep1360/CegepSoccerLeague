package com.example.cegepsoccerleague;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ScoreboardFragment extends Fragment implements View.OnClickListener{

    FloatingActionButton create_score_btn;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    public NavController HomeNavController;
    private Context context;
    private FirebaseFirestore db;

    private RecyclerView scoreboards_list_recycler_view;
    private ArrayList<Scoreboards_List_model> scoreboardsArrayList;
    private Scoreboards_Rec_adapter scoreboards_rec_adapter;

    public ScoreboardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scoreboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity().getApplicationContext();
        HomeNavController = Navigation.findNavController(getActivity(), R.id.home_host_fragment);

        create_score_btn = view.findViewById(R.id.create_score_btn);
        scoreboards_list_recycler_view = view.findViewById(R.id.scoreboards_list_recycler_view);

        create_score_btn.setOnClickListener(this);


        /*--------Your Recipes Adapter Configuration--------*/
        scoreboardsArrayList = new ArrayList<Scoreboards_List_model>();
        scoreboardsArrayList.add(new Scoreboards_List_model("1","2020-05-29",
                "1","No Icon","Worriers FC",
                "2","No Icon","Cegep Club",
                "3","2","1","2","2",
                "2","2","1","2","2"));
        scoreboardsArrayList.add(new Scoreboards_List_model("1","2020-05-29",
                "1","No Icon","Cegep Worriers",
                "2","No Icon","Barcelona Club",
                "2","2","1","2","2",
                "3","2","1","2","2"));
        scoreboardsArrayList.add(new Scoreboards_List_model("1","2020-05-29",
                "1","No Icon","Cegep Gim FC",
                "2","No Icon","Club Worriers",
                "4","2","1","2","2",
                "1","2","1","2","2"));
        scoreboardsArrayList.add(new Scoreboards_List_model("1","2020-05-29",
                "1","No Icon","FC Champions",
                "2","No Icon","Cegep Club",
                "1","2","1","2","2",
                "3","2","1","2","2"));
        scoreboards_rec_adapter = new Scoreboards_Rec_adapter(scoreboardsArrayList,context);
        scoreboards_list_recycler_view.setLayoutManager(new LinearLayoutManager(context));
        scoreboards_list_recycler_view.setAdapter(scoreboards_rec_adapter);

        scoreboards_rec_adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
                int position = viewHolder.getAdapterPosition();
                if(getArguments()!=null){
                    if(getArguments().getString("from")!=null && getArguments().getString("from").equals("league Features")){
                        Bundle dataBundle = new Bundle();
                        dataBundle.putString("from","league Features");
                        HomeNavController.navigate(R.id.scoreboardInfoFragment,dataBundle);
                    }
                }
                else {
                    HomeNavController.navigate(R.id.scoreboardInfoFragment);
                }
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
                create_score_btn.setVisibility(View.VISIBLE);
            }
        }
        else {
            create_score_btn.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        if(view == create_score_btn){
            HomeNavController.navigate(R.id.selectMatchFragment);
        }
    }
}
