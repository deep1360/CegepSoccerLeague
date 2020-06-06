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
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListOfPlayersFragment extends Fragment implements View.OnClickListener{

    FloatingActionButton create_player_btn;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    public NavController HomeNavController;
    private Context context;
    private FirebaseFirestore db;

    private RecyclerView players_list_recycler_view;
    private ArrayList<Players_List_model> playersArrayList;
    private Players_Rec_adapter players_rec_adapter;
    private String team_id = "";

    public ListOfPlayersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_of_players, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity().getApplicationContext();
        HomeNavController = Navigation.findNavController(getActivity(), R.id.home_host_fragment);

        create_player_btn = view.findViewById(R.id.create_player_btn);
        players_list_recycler_view = view.findViewById(R.id.players_list_recycler_view);

        create_player_btn.setOnClickListener(this);


        /*--------Your Recipes Adapter Configuration--------*/
        playersArrayList = new ArrayList<Players_List_model>();
        players_rec_adapter = new Players_Rec_adapter(playersArrayList,context);
        players_list_recycler_view.setLayoutManager(new LinearLayoutManager(context));
        players_list_recycler_view.setAdapter(players_rec_adapter);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        //accessing current user
        user = mAuth.getCurrentUser();
        // Access a Cloud Firestore instance from your Fragment
        db = FirebaseFirestore.getInstance();

        if(getArguments()!=null){
            if(getArguments().getString("from")!=null && getArguments().getString("from").equals("guest dashboard")){
                team_id = getArguments().getString("team_id");
                create_player_btn.setVisibility(View.GONE);
            }
            else if(getArguments().getString("from")!=null && getArguments().getString("from").equals("TM Your Team")){
                team_id = getArguments().getString("team_id");
                create_player_btn.setVisibility(View.VISIBLE);
            }
            else {
                create_player_btn.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View view) {
        if(view == create_player_btn){
            Bundle dataBundle = new Bundle();
            dataBundle.putString("team_id",team_id);
            HomeNavController.navigate(R.id.addPlayerFragment,dataBundle);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPlayers();
    }

    private void getPlayers(){
        //Clearing List Items Before Adding it
        playersArrayList.clear();

        //Fetching data and assigning it to Recycler View to Display On Screen
        db.collection("players").whereEqualTo("team_id",team_id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        playersArrayList.add(new Players_List_model(document.getId(),
                                document.getData().get("first_name").toString(),
                                document.getData().get("last_name").toString(),
                                document.getData().get("age").toString(),
                                document.getData().get("position").toString(),
                                document.getData().get("player_icon").toString(),
                                document.getData().get("team_id").toString()));

                    }
                    players_rec_adapter.notifyDataSetChanged();

                    players_rec_adapter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
                            int position = viewHolder.getAdapterPosition();
                            if(getArguments()!=null){
                                if(getArguments().getString("from")!=null && getArguments().getString("from").equals("guest dashboard")){
                                }
                                else {
                                    Bundle dataBundle = new Bundle();
                                    dataBundle.putString("player_id",playersArrayList.get(position).getPlayer_id());
                                    dataBundle.putString("first_name",playersArrayList.get(position).getPlayer_first_name());
                                    dataBundle.putString("last_name",playersArrayList.get(position).getPlayer_last_name());
                                    dataBundle.putString("age",playersArrayList.get(position).getPlaer_age());
                                    dataBundle.putString("position",playersArrayList.get(position).getPlayer_position());
                                    dataBundle.putString("player_icon",playersArrayList.get(position).getPlayer_icon());
                                    dataBundle.putString("team_id",playersArrayList.get(position).getTeam_id());
                                    HomeNavController.navigate(R.id.playerInfoFragment,dataBundle);
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
