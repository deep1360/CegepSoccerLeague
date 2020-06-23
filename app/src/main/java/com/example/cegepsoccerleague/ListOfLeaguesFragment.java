package com.example.cegepsoccerleague;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListOfLeaguesFragment extends Fragment implements View.OnClickListener{

    FloatingActionButton create_league_btn;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    public NavController HomeNavController;
    private Context context;
    private FirebaseFirestore db;

    private RecyclerView leagues_list_recycler_view;
    private ArrayList<Leagues_List_model> LeaguesArrayList;
    private Leagues_Rec_adapter leagues_rec_adapter;
    private static ProgressDialog progressDialog;


    public ListOfLeaguesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_of_leagues, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity().getApplicationContext();
        HomeNavController = Navigation.findNavController(getActivity(), R.id.home_host_fragment);

        create_league_btn = view.findViewById(R.id.create_league_btn);
        leagues_list_recycler_view = view.findViewById(R.id.leagues_list_recycler_view);

        create_league_btn.setOnClickListener(this);


        /*--------Your Recipes Adapter Configuration--------*/
        LeaguesArrayList = new ArrayList<Leagues_List_model>();
        leagues_rec_adapter = new Leagues_Rec_adapter(LeaguesArrayList,context);
        leagues_list_recycler_view.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        leagues_list_recycler_view.setAdapter(leagues_rec_adapter);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        //accessing current user
        user = mAuth.getCurrentUser();
        // Access a Cloud Firestore instance from your Fragment
        db = FirebaseFirestore.getInstance();

        if(getArguments()!=null){
            if(getArguments().getString("from").equals("your-leagues")) {
                create_league_btn.setVisibility(View.VISIBLE);
            }
        }
        else {
            create_league_btn.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getLeauges();

    }

    private void getLeauges(){
        if(progressDialog!=null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(true);
        if(getArguments()!=null) {
            progressDialog.setMessage("Fetching Leagues");
        }
        else {
            progressDialog.setMessage("Processing");
        }
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setProgress(0);
        progressDialog.show();
        //Clearing List Items Before Adding it
        LeaguesArrayList.clear();
        // Create a reference to the cities collection
        CollectionReference leaguesRef = db.collection("leagues");

        // Create a query against the collection.
        Query query = leaguesRef;
        if(getArguments()!=null){
            if(getArguments().getString("from").equals("your-leagues")) {
                query = leaguesRef.whereEqualTo("league_manager_id", user.getUid());
            }
        }

        //Fetching data and assigning it to Recycler View to Display On Screen
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        LeaguesArrayList.add(new Leagues_List_model(document.getId(),
                                document.get("league_name").toString(),
                                document.get("league_icon").toString(),
                                document.get("league_manager_id").toString()));
                    }
                    leagues_rec_adapter.notifyDataSetChanged();
                    leagues_rec_adapter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
                            int position = viewHolder.getAdapterPosition();
                            Bundle dataBundle = new Bundle();
                            dataBundle.putString("league_id", LeaguesArrayList.get(position).getLeague_id());
                            dataBundle.putString("league_name", LeaguesArrayList.get(position).getLeague_name());
                            dataBundle.putString("league_icon", LeaguesArrayList.get(position).getLeague_icon());
                            if(getArguments()!=null){
                                if(getArguments().getString("from").equals("your-leagues")) {
                                    HomeNavController.navigate(R.id.leagueFeaturesFragment,dataBundle);
                                }
                            }
                            else {
                                dataBundle.putString("from","guest dashboard");
                                HomeNavController.navigate(R.id.listOfTeamsFragment,dataBundle);
                            }
                        }
                    });
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(context,task.getException().getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view == create_league_btn){
            HomeNavController.navigate(R.id.addLeagueFragment);
        }
    }
}
