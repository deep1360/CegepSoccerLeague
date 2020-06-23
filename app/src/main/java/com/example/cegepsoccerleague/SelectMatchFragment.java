package com.example.cegepsoccerleague;

import android.app.ProgressDialog;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


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

    Bundle dataBundle;
    JSONObject teamsObject;
    private static ProgressDialog progressDialog;


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
        teamsObject = new JSONObject();
        played_matches_list_recycler_view = view.findViewById(R.id.played_matches_list_recycler_view);

        /*--------Your Recipes Adapter Configuration--------*/
        schedulesArrayList = new ArrayList<Schedules_List_model>();

        selectMatch_rec_adapter = new Select_Match_Rec_adapter(schedulesArrayList,context);
        played_matches_list_recycler_view.setLayoutManager(new LinearLayoutManager(context));
        played_matches_list_recycler_view.setAdapter(selectMatch_rec_adapter);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        //accessing current user
        user = mAuth.getCurrentUser();
        // Access a Cloud Firestore instance from your Fragment
        db = FirebaseFirestore.getInstance();

        if(getArguments()!=null){
            dataBundle = getArguments();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        /*-------------First getting Teams and then in getTeams Method getSchedules------------*/
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
                    getSchedules();
                } else {
                    Toast.makeText(context, task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    /*----------Get List Of Schedules-----------*/
    private void getSchedules() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);

        progressDialog.setMessage("Fetching Matches");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setProgress(0);
        progressDialog.show();
        //Clearing List Items Before Adding it
        schedulesArrayList.clear();
        // Create a reference to the Schedules collection
        CollectionReference schedulesRef = db.collection("schedules");

        // Create a query against the collection.
        Query query = schedulesRef;
        if (getArguments() != null) {
            if (dataBundle.getString("from").equals("league Features")) {
                query = schedulesRef.whereEqualTo("league_id", dataBundle.getString("league_id"));
            }
        }

        //Fetching data and assigning it to Recycler View to Display On Screen
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Calendar c = Calendar.getInstance();
                    int yy = c.get(Calendar.YEAR);
                    int mm = c.get(Calendar.MONTH) + 1;
                    int dd = c.get(Calendar.DAY_OF_MONTH);

                    for (QueryDocumentSnapshot document : task.getResult()) {

                        String match_date = document.get("match_date").toString();
                        String date[] = match_date.split("-");

                        try {
                            JSONObject Team1object = (JSONObject) teamsObject.get(document.get("team1_id").toString());
                            JSONObject Team2object = (JSONObject) teamsObject.get(document.get("team2_id").toString());

                            if(Integer.parseInt(date[0]) < yy){
                                schedulesArrayList.add(new Schedules_List_model(document.getId(),
                                        document.get("match_location").toString(),
                                        document.get("match_date").toString(),
                                        document.get("match_time").toString(),
                                        document.get("league_id").toString(),
                                        document.get("team1_id").toString(),
                                        Team1object.getString("team_name"),
                                        Team1object.getString("team_icon"),
                                        document.get("team2_id").toString(),
                                        Team2object.getString("team_name"),
                                        Team2object.getString("team_icon")));
                            }
                            else if(Integer.parseInt(date[0]) == yy){
                                if(Integer.parseInt(date[1]) < mm){
                                    schedulesArrayList.add(new Schedules_List_model(document.getId(),
                                            document.get("match_location").toString(),
                                            document.get("match_date").toString(),
                                            document.get("match_time").toString(),
                                            document.get("league_id").toString(),
                                            document.get("team1_id").toString(),
                                            Team1object.getString("team_name"),
                                            Team1object.getString("team_icon"),
                                            document.get("team2_id").toString(),
                                            Team2object.getString("team_name"),
                                            Team2object.getString("team_icon")));
                                }
                                else if(Integer.parseInt(date[1])==mm){
                                    if(Integer.parseInt(date[2])<dd){
                                        schedulesArrayList.add(new Schedules_List_model(document.getId(),
                                                document.get("match_location").toString(),
                                                document.get("match_date").toString(),
                                                document.get("match_time").toString(),
                                                document.get("league_id").toString(),
                                                document.get("team1_id").toString(),
                                                Team1object.getString("team_name"),
                                                Team1object.getString("team_icon"),
                                                document.get("team2_id").toString(),
                                                Team2object.getString("team_name"),
                                                Team2object.getString("team_icon")));
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if(schedulesArrayList.size() == 0){
                        Toast.makeText(context,"No Matches available for adding socre!",Toast.LENGTH_LONG).show();
                    }
                    selectMatch_rec_adapter.notifyDataSetChanged();
                    selectMatch_rec_adapter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
                            int position = viewHolder.getAdapterPosition();
                            Bundle dataBundle = new Bundle();
                            dataBundle.putString("match_id",schedulesArrayList.get(position).getMatch_id());
                            dataBundle.putString("match_date",schedulesArrayList.get(position).getMatch_date());
                            dataBundle.putString("match_time",schedulesArrayList.get(position).getMatch_time());
                            dataBundle.putString("match_location",schedulesArrayList.get(position).getMatch_location());
                            dataBundle.putString("team1_id",schedulesArrayList.get(position).getTeam1_id());
                            dataBundle.putString("team1_name",schedulesArrayList.get(position).getTeam1_name());
                            dataBundle.putString("team1_icon",schedulesArrayList.get(position).getTeam1_icon());
                            dataBundle.putString("team2_id",schedulesArrayList.get(position).getTeam2_id());
                            dataBundle.putString("team2_name",schedulesArrayList.get(position).getTeam2_name());
                            dataBundle.putString("team2_icon",schedulesArrayList.get(position).getTeam2_icon());
                            dataBundle.putString("league_id",schedulesArrayList.get(position).getLeague_id());

                            HomeNavController.navigate(R.id.addScoreboardFragment,dataBundle);
                        }
                    });
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(context, task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
