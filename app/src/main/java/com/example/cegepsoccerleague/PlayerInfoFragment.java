package com.example.cegepsoccerleague;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlayerInfoFragment extends Fragment implements View.OnClickListener{

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private Context context;
    public NavController HomeNavController;
    private ImageView player_info_img_view;
    private TextView player_info_age, player_info_position, player_info_name;
    private MaterialButton edit_player_info_btn, delete_player_btn;
    private String player_id="";


    public PlayerInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_player_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity().getApplicationContext();
        HomeNavController = Navigation.findNavController(getActivity(), R.id.home_host_fragment);

        player_info_img_view = view.findViewById(R.id.player_info_img_view);
        player_info_age = view.findViewById(R.id.player_info_age);
        player_info_position = view.findViewById(R.id.player_info_position);
        player_info_name = view.findViewById(R.id.player_info_name);
        edit_player_info_btn = view.findViewById(R.id.edit_player_info_btn);
        delete_player_btn = view.findViewById(R.id.delete_player_btn);

        delete_player_btn.setOnClickListener(this);
        edit_player_info_btn.setOnClickListener(this);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();
        //Get Current User refernece
        user = mAuth.getCurrentUser();

        if(getArguments()!=null){
            player_id = getArguments().getString("player_id");
            player_info_name.setText(getArguments().getString("first_name")+" "+getArguments().getString("last_name"));
            player_info_age.setText("Age : "+getArguments().getString("age"));
            player_info_position.setText("Position : "+getArguments().getString("position"));
            if(!getArguments().getString("player_icon").equals("No Icon")){
                byte[] decodedString = Base64.decode(getArguments().getString("player_icon"), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                player_info_img_view.setImageBitmap(decodedByte);
            }
        }

    }

    @Override
    public void onClick(View view) {
        if(view==edit_player_info_btn){
            Bundle dataBundle = new Bundle();
            dataBundle.putString("from","update player");
            dataBundle.putString("player_id",getArguments().getString("player_id"));
            dataBundle.putString("first_name",getArguments().getString("first_name"));
            dataBundle.putString("last_name",getArguments().getString("last_name"));
            dataBundle.putString("age",getArguments().getString("age"));
            dataBundle.putString("position",getArguments().getString("position"));
            dataBundle.putString("player_icon",getArguments().getString("player_icon"));
            dataBundle.putString("team_id",getArguments().getString("team_id"));
            HomeNavController.navigate(R.id.addPlayerFragment,dataBundle);
        }
        else if(view == delete_player_btn){
            DeletePlayer();
        }
    }

    private void DeletePlayer(){
        /*------------ Configuration of Guest Login Dialog----------*/
        AlertDialog.Builder DAC_builder = new AlertDialog.Builder(getActivity())
                .setView(R.layout.delete_player_dialog_layout);
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
                db.collection("players").document(player_id)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(context,"Player removed from team successfully!",Toast.LENGTH_LONG).show();
                                Delete_Player_Dialog.dismiss();
                                HomeNavController.popBackStack();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                                Delete_Player_Dialog.dismiss();
                            }
                        });
            }
        });
    }
}
