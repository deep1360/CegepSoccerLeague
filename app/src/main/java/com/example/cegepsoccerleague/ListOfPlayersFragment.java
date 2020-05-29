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
        playersArrayList.add(new Players_List_model("1","Lionel","Messi","32","Forward","No Icon","1"));
        playersArrayList.add(new Players_List_model("2","Cristiano","Ronaldo","35","Midfilder","No Icon","1"));
        playersArrayList.add(new Players_List_model("3","Xavi","","40","Forward","No Icon","1"));

        players_rec_adapter = new Players_Rec_adapter(playersArrayList,context);
        players_list_recycler_view.setLayoutManager(new LinearLayoutManager(context));
        players_list_recycler_view.setAdapter(players_rec_adapter);

        players_rec_adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
                int position = viewHolder.getAdapterPosition();
                if(getArguments()!=null){
                    if(getArguments().getString("from")!=null && getArguments().getString("from").equals("guest dashboard")){
                    }
                }
                else {
                    HomeNavController.navigate(R.id.playerInfoFragment);
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
            if(getArguments().getString("from")!=null && getArguments().getString("from").equals("guest dashboard")){
                create_player_btn.setVisibility(View.GONE);
            }
        }
        else {
            create_player_btn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        if(view == create_player_btn){
            HomeNavController.navigate(R.id.addPlayerFragment);
        }
    }
}
