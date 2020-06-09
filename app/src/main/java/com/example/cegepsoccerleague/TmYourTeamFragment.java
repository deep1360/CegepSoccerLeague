package com.example.cegepsoccerleague;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


/**
 * A simple {@link Fragment} subclass.
 */
public class TmYourTeamFragment extends Fragment implements View.OnClickListener{

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private Context context;
    public NavController HomeNavController;
    private ImageView team_info_img_view;
    private TextView team_info_email, team_info_contact_num, team_info_name;
    private MaterialButton tm_info_update_btn, tm_info_view_player_btn;
    String team_id = "",team_icon="",league_id="";

    public TmYourTeamFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tm_your_team, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity().getApplicationContext();
        HomeNavController = Navigation.findNavController(getActivity(), R.id.home_host_fragment);

        team_info_img_view = view.findViewById(R.id.team_info_img_view);
        team_info_email = view.findViewById(R.id.team_info_email);
        team_info_contact_num = view.findViewById(R.id.team_info_contact_num);
        team_info_name = view.findViewById(R.id.team_info_name);
        tm_info_update_btn = view.findViewById(R.id.tm_info_update_btn);
        tm_info_view_player_btn = view.findViewById(R.id.tm_info_view_player_btn);

        tm_info_view_player_btn.setOnClickListener(this);
        tm_info_update_btn.setOnClickListener(this);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();
        //Get Current User refernece
        user = mAuth.getCurrentUser();

    }

    @Override
    public void onResume() {
        super.onResume();
        getTeamDetails();
    }

    @Override
    public void onClick(View view) {

        if(view==tm_info_update_btn){
            Bundle dataBundle = new Bundle();
            dataBundle.putString("team_id",team_id);
            dataBundle.putString("team_name",team_info_name.getText().toString());
            dataBundle.putString("team_manager_contact",team_info_contact_num.getText().toString());
            dataBundle.putString("team_manager_id",user.getUid());
            dataBundle.putString("team_icon",team_icon);
            dataBundle.putString("league_id",league_id);
            HomeNavController.navigate(R.id.updateTeamInfoFragment,dataBundle);
        }
        else if(view == tm_info_view_player_btn){
            Bundle dataBundle = new Bundle();
            dataBundle.putString("team_id",team_id);
            dataBundle.putString("from","TM Your Team");
            HomeNavController.navigate(R.id.listOfPlayersFragment,dataBundle);
        }

    }

    private void getTeamDetails(){
        db.collection("teams").whereEqualTo("team_manager_id",user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                team_id = document.getId();
                                team_info_name.setText(document.getData().get("team_name").toString());
                                team_info_contact_num.setText(document.getData().get("team_manager_contact").toString());
                                team_info_email.setText(user.getEmail());
                                team_icon = document.getData().get("team_icon").toString();
                                league_id = document.getData().get("league_id").toString();
                                if(!document.getData().get("team_icon").toString().equals("No Icon")){
                                    byte[] decodedString = Base64.decode(document.getData().get("team_icon").toString(), Base64.DEFAULT);
                                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                    team_info_img_view.setImageBitmap(decodedByte);
                                }
                            }
                        } else {
                            Toast.makeText(context,task.getException().getLocalizedMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
