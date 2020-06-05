package com.example.cegepsoccerleague;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class LgmTeamInfoFragment extends Fragment implements View.OnClickListener{

    FloatingActionButton delete_team_btn;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    public NavController HomeNavController;
    private Context context;
    private FirebaseFirestore db;
    private LinearLayout lgm_team_players_layout;
    private ImageView lgm_team_info_img_view;
    private TextView lgm_team_info_name, lgm_team_manager_name, lgm_team_manager_contact, lgm_team_manager_email;
    String team_id = "",team_manager_id="";


    public LgmTeamInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lgm_team_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity().getApplicationContext();
        HomeNavController = Navigation.findNavController(getActivity(), R.id.home_host_fragment);
        delete_team_btn = view.findViewById(R.id.delete_team_btn);
        lgm_team_players_layout = view.findViewById(R.id.lgm_team_players_layout);
        lgm_team_info_img_view = view.findViewById(R.id.lgm_team_info_img_view);
        lgm_team_manager_name = view.findViewById(R.id.lgm_team_manager_name);
        lgm_team_manager_contact = view.findViewById(R.id.lgm_team_manager_contact);
        lgm_team_info_name = view.findViewById(R.id.lgm_team_info_name);
        lgm_team_manager_email = view.findViewById(R.id.lgm_team_manager_email);

        delete_team_btn.setOnClickListener(this);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        //accessing current user
        user = mAuth.getCurrentUser();
        // Access a Cloud Firestore instance from your Fragment
        db = FirebaseFirestore.getInstance();

        if(getArguments()!=null){
            team_id = getArguments().getString("team_id");
            team_manager_id = getArguments().getString("team_manager_id");
            lgm_team_info_name.setText(getArguments().getString("team_name"));
            lgm_team_manager_name.setText(getArguments().getString("team_manager_name"));
            lgm_team_manager_contact.setText(getArguments().getString("team_manager_contact"));
            lgm_team_manager_email.setText(getArguments().getString("team_manager_email"));
            if(!getArguments().getString("team_icon").equals("No Icon")){
                byte[] decodedString = Base64.decode(getArguments().getString("team_icon"), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                lgm_team_info_img_view.setImageBitmap(decodedByte);
            }
        }

        CreatePlayersListView();

    }

    @Override
    public void onClick(View view) {

        if(view==delete_team_btn){
            DeleteTeam();
        }

    }

    private void DeleteTeam(){
        /*------------ Configuration of Guest Login Dialog----------*/
        AlertDialog.Builder DAC_builder = new AlertDialog.Builder(getActivity())
                .setView(R.layout.delete_team_dialog_layout);
        final AlertDialog Delete_Player_Dialog = DAC_builder.create();
        Delete_Player_Dialog.show();
        MaterialButton delete_cancel_btn = Delete_Player_Dialog.findViewById(R.id.delete_cancel_btn);
        MaterialButton delelte_continue_btn = Delete_Player_Dialog.findViewById(R.id.delelte_continue_btn);

        delete_cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Delete_Player_Dialog.dismiss();
            }
        });

        delelte_continue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Delete_Player_Dialog.dismiss();
                HomeNavController.popBackStack();
            }
        });
    }

    private void CreatePlayersListView(){

        lgm_team_players_layout.removeAllViews();
        for (int i=0;i<6;i++) {
            ConstraintLayout childLayout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.lgm_team_player_view, null);
            ImageView playerImage = childLayout.findViewById(R.id.lgm_player_img_view);
            TextView playerName = childLayout.findViewById(R.id.lgm_player_name);

            lgm_team_players_layout.addView(childLayout);
        }
    }
}
