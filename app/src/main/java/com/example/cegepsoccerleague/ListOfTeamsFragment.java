package com.example.cegepsoccerleague;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListOfTeamsFragment extends Fragment implements View.OnClickListener{

    FloatingActionButton create_team_btn;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    public NavController HomeNavController;
    private Context context;
    private FirebaseFirestore db;
    private String league_id;
    public Toolbar HomeToolbar;
    JSONObject userObject;



    private RecyclerView teams_list_recycler_view;
    private ArrayList<Teams_List_model> TeamsArrayList;
    private Teams_Rec_adapter teams_rec_adapter;


    public ListOfTeamsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_of_teams, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity().getApplicationContext();
        HomeNavController = Navigation.findNavController(getActivity(), R.id.home_host_fragment);
        HomeToolbar = getActivity().findViewById(R.id.home_toolbar);
        userObject= new JSONObject();
        create_team_btn = view.findViewById(R.id.create_team_btn);
        teams_list_recycler_view = view.findViewById(R.id.teams_list_recycler_view);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        //accessing current user
        user = mAuth.getCurrentUser();
        // Access a Cloud Firestore instance from your Fragment
        db = FirebaseFirestore.getInstance();

        //Getting Selected League Id
        if(getArguments()!=null){
            HomeToolbar.setTitle(getArguments().getString("league_name")+" Teams");
            league_id = getArguments().getString("league_id");
            if(getArguments().getString("from")!=null && getArguments().getString("from").equals("guest dashboard")){
                create_team_btn.setVisibility(View.GONE);
            }
        }


        /*--------Your Recipes Adapter Configuration--------*/
        TeamsArrayList = new ArrayList<Teams_List_model>();
        teams_rec_adapter = new Teams_Rec_adapter(TeamsArrayList,context);
        teams_list_recycler_view.setLayoutManager(new LinearLayoutManager(context));
        teams_list_recycler_view.setAdapter(teams_rec_adapter);

        create_team_btn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if(view == create_team_btn){
            Bundle dataBundle = new Bundle();
            dataBundle.putString("league_id",league_id);
            HomeNavController.navigate(R.id.addTeamFragment,dataBundle);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getUsers();
    }

    private void getUsers() {
        db.collection("users").whereEqualTo("user_type","TM").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        JSONObject object = new JSONObject(document.getData());
                        try {
                            userObject.put(document.getId(),object);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    getTeams();
                } else {
                    Toast.makeText(context,task.getException().getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void getTeams(){
        //Clearing List Items Before Adding it
        TeamsArrayList.clear();

        //Fetching data and assigning it to Recycler View to Display On Screen
        db.collection("teams").whereEqualTo("league_id",league_id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        try {

                            JSONObject object = (JSONObject) userObject.get(document.get("team_manager_id").toString());
                            TeamsArrayList.add(new Teams_List_model(document.getId(),
                                    document.get("team_name").toString(),
                                    document.get("team_icon").toString(),
                                    document.get("team_manager_id").toString(),
                                    object.get("first_name")+" "+object.get("last_name"),
                                    document.get("team_manager_contact").toString(),
                                    object.get("email").toString(),
                                    object.get("password").toString(),
                                    document.get("league_id").toString()));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    teams_rec_adapter.notifyDataSetChanged();
                    teams_rec_adapter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
                            int position = viewHolder.getAdapterPosition();
                            Bundle dataBundle = new Bundle();
                            if(getArguments()!=null){
                                if(getArguments().getString("from")!=null && getArguments().getString("from").equals("guest dashboard")){
                                    dataBundle.putString("from","guest dashboard");
                                    dataBundle.putString("team_id",TeamsArrayList.get(position).getTeam_id());
                                    HomeNavController.navigate(R.id.listOfPlayersFragment,dataBundle);
                                }
                                else {
                                    dataBundle.putString("team_id",TeamsArrayList.get(position).getTeam_id());
                                    dataBundle.putString("team_name",TeamsArrayList.get(position).getTeam_name());
                                    dataBundle.putString("team_icon",TeamsArrayList.get(position).getTeam_icon());
                                    dataBundle.putString("team_manager_id",TeamsArrayList.get(position).getTeam_manager_id());
                                    dataBundle.putString("team_manager_name",TeamsArrayList.get(position).getTeam_manager_name());
                                    dataBundle.putString("team_manager_contact",TeamsArrayList.get(position).getTeam_manager_contact());
                                    dataBundle.putString("team_manager_email",TeamsArrayList.get(position).getTeam_manager_email());
                                    dataBundle.putString("team_manager_password",TeamsArrayList.get(position).getTeam_manager_password());
                                    HomeNavController.navigate(R.id.lgmTeamInfoFragment,dataBundle);
                                }
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
