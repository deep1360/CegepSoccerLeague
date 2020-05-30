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
        TeamsArrayList.add(new Teams_List_model("1","Cegep Team 1","No Icon","12","Aman Singh","1234567890","dsfghjkjh"));
        TeamsArrayList.add(new Teams_List_model("2","Cegep Team 2","No Icon","12","Parm Kaur","1234567890","dsfghjkjh"));
        TeamsArrayList.add(new Teams_List_model("3","Cegep Team 3","No Icon","12","Sandeep Sidhu","1234567890","dsfghjkjh"));
        TeamsArrayList.add(new Teams_List_model("4","Cegep Team 4","No Icon","12","Harbir Singh","1234567890","dsfghjkjh"));
        teams_rec_adapter = new Teams_Rec_adapter(TeamsArrayList,context);
        teams_list_recycler_view.setLayoutManager(new LinearLayoutManager(context));
        teams_list_recycler_view.setAdapter(teams_rec_adapter);

        create_team_btn.setOnClickListener(this);

        teams_rec_adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle dataBundle = new Bundle();
                if(getArguments()!=null){
                    if(getArguments().getString("from")!=null && getArguments().getString("from").equals("guest dashboard")){
                        dataBundle.putString("from","guest dashboard");
                        HomeNavController.navigate(R.id.listOfPlayersFragment,dataBundle);
                    }
                    else {
                        HomeNavController.navigate(R.id.lgmTeamInfoFragment);
                    }
                }
            }
        });

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
    }
}
