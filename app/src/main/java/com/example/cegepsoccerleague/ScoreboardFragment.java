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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ScoreboardFragment extends Fragment implements View.OnClickListener{

    FloatingActionButton create_score_btn;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    JSONObject teamsObject;
    public NavController HomeNavController;
    private Context context;
    private FirebaseFirestore db;

    private RecyclerView scoreboards_list_recycler_view;
    private ArrayList<Scoreboards_List_model> scoreboardsArrayList;
    private Scoreboards_Rec_adapter scoreboards_rec_adapter;
    Bundle dataBundle;

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
        teamsObject = new JSONObject();

        create_score_btn = view.findViewById(R.id.create_score_btn);
        scoreboards_list_recycler_view = view.findViewById(R.id.scoreboards_list_recycler_view);

        create_score_btn.setOnClickListener(this);


        /*--------Your Recipes Adapter Configuration--------*/
        scoreboardsArrayList = new ArrayList<Scoreboards_List_model>();
        scoreboards_rec_adapter = new Scoreboards_Rec_adapter(scoreboardsArrayList,context);
        scoreboards_list_recycler_view.setLayoutManager(new LinearLayoutManager(context));
        scoreboards_list_recycler_view.setAdapter(scoreboards_rec_adapter);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        //accessing current user
        user = mAuth.getCurrentUser();
        // Access a Cloud Firestore instance from your Fragment
        db = FirebaseFirestore.getInstance();
        if(getArguments()!=null){
            if(getArguments().getString("from")!=null && getArguments().getString("from").equals("league Features")){
                create_score_btn.setVisibility(View.VISIBLE);
                dataBundle = getArguments();
            }
        }
        else {
            create_score_btn.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        if(view == create_score_btn){
            HomeNavController.navigate(R.id.selectMatchFragment,dataBundle);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getTeams();
    }

    /*----------Getting Team Data and Creating a Json Object Of Teams------------*/
    private void getTeams() {
        db.collection("teams").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        JSONObject object = new JSONObject(document.getData());
                        try {
                            teamsObject.put(document.getId(), object);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    getScores();
                } else {
                    Toast.makeText(context, task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void getScores(){
        //Clearing List Items Before Adding it
        scoreboardsArrayList.clear();
        // Create a reference to the cities collection
        CollectionReference scoresRef = db.collection("scores");

        // Create a query against the collection.
        Query query = scoresRef;
        if(getArguments()!=null){
            if(getArguments().getString("from").equals("league Features")) {
                query = scoresRef.whereEqualTo("league_id", dataBundle.getString("league_id"));
            }
        }

        //Fetching data and assigning it to Recycler View to Display On Screen
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        try {
                            JSONObject Team1object = (JSONObject) teamsObject.get(document.get("team1_id").toString());
                            JSONObject Team2object = (JSONObject) teamsObject.get(document.get("team2_id").toString());

                            scoreboardsArrayList.add(new Scoreboards_List_model(document.getId(),
                                    document.get("match_id").toString(),document.get("match_date").toString(),document.get("match_location").toString(),
                                    document.get("match_time").toString(),document.get("league_id").toString(),
                                    document.get("team1_id").toString(),Team1object.getString("team_icon"),Team1object.getString("team_name"),
                                    document.get("team2_id").toString(),Team2object.getString("team_icon"),Team2object.getString("team_name"),
                                    document.get("team1_goals").toString(),document.get("team1_fouls").toString(),document.get("team1_freeKicks").toString(),document.get("team1_corners").toString(),document.get("team1_goalSaved").toString(),
                                    document.get("team2_goals").toString(),document.get("team2_fouls").toString(),document.get("team2_freeKicks").toString(),document.get("team2_corners").toString(),document.get("team2_goalSaved").toString()));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    scoreboards_rec_adapter.notifyDataSetChanged();
                    scoreboards_rec_adapter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
                            int position = viewHolder.getAdapterPosition();

                            Bundle dataBundle = new Bundle();
                            dataBundle.putString("score_id",scoreboardsArrayList.get(position).getScore_id());
                            dataBundle.putString("match_id",scoreboardsArrayList.get(position).getMatch_id());
                            dataBundle.putString("match_date",scoreboardsArrayList.get(position).getMatch_date());
                            dataBundle.putString("match_location",scoreboardsArrayList.get(position).getMatch_location());
                            dataBundle.putString("match_time",scoreboardsArrayList.get(position).getMatch_time());
                            dataBundle.putString("league_id",scoreboardsArrayList.get(position).getLeague_id());

                            dataBundle.putString("team1_id",scoreboardsArrayList.get(position).getTeam1_id());
                            dataBundle.putString("team1_name",scoreboardsArrayList.get(position).getTeam1_name());
                            dataBundle.putString("team1_icon",scoreboardsArrayList.get(position).getTeam1_icon());

                            dataBundle.putString("team2_id",scoreboardsArrayList.get(position).getTeam2_id());
                            dataBundle.putString("team2_name",scoreboardsArrayList.get(position).getTeam2_name());
                            dataBundle.putString("team2_icon",scoreboardsArrayList.get(position).getTeam2_icon());

                            dataBundle.putString("team1_goals",scoreboardsArrayList.get(position).getTeam1_goals());
                            dataBundle.putString("team1_fouls",scoreboardsArrayList.get(position).getTeam1_fouls());
                            dataBundle.putString("team1_freeKicks",scoreboardsArrayList.get(position).getTeam1_freeKicks());
                            dataBundle.putString("team1_corners",scoreboardsArrayList.get(position).getTeam1_corners());
                            dataBundle.putString("team1_goalSaved",scoreboardsArrayList.get(position).getTeam1_goalSaved());

                            dataBundle.putString("team2_goals",scoreboardsArrayList.get(position).getTeam2_goals());
                            dataBundle.putString("team2_fouls",scoreboardsArrayList.get(position).getTeam2_fouls());
                            dataBundle.putString("team2_freeKicks",scoreboardsArrayList.get(position).getTeam2_freeKicks());
                            dataBundle.putString("team2_corners",scoreboardsArrayList.get(position).getTeam2_corners());
                            dataBundle.putString("team2_goalSaved",scoreboardsArrayList.get(position).getTeam2_goalSaved());

                            if(getArguments()!=null){
                                if(getArguments().getString("from")!=null && getArguments().getString("from").equals("league Features")){

                                    dataBundle.putString("from","league Features");
                                    HomeNavController.navigate(R.id.scoreboardInfoFragment,dataBundle);
                                }
                            }
                            else {
                                HomeNavController.navigate(R.id.scoreboardInfoFragment,dataBundle);
                            }
                        }
                    });
                } else {
                    Toast.makeText(context,task.getException().getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
