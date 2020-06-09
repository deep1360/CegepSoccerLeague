package com.example.cegepsoccerleague;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class UpdateTeamInfoFragment extends Fragment implements View.OnClickListener{

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private Context context;
    public NavController HomeNavController;
    private ImageView update_team_icon_img_view;
    private TextView update_team_icon_txt;
    private MaterialCardView update_team_icon_cv;
    private TextInputLayout update_team_name_layout, update_team_contact_layout;
    private MaterialButton update_team_info_btn;
    public Uri image_uri;
    String team_id = "",team_icon="",league_id="";
    boolean team_has_icon =false;

    public UpdateTeamInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_team_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity().getApplicationContext();
        HomeNavController = Navigation.findNavController(getActivity(), R.id.home_host_fragment);

        update_team_icon_img_view = view.findViewById(R.id.update_team_icon_img_view);
        update_team_icon_txt = view.findViewById(R.id.update_team_icon_txt);
        update_team_icon_cv = view.findViewById(R.id.update_team_icon_cv);
        update_team_name_layout = view.findViewById(R.id.update_team_name_layout);
        update_team_contact_layout = view.findViewById(R.id.update_team_contact_layout);
        update_team_info_btn = view.findViewById(R.id.update_team_info_btn);

        update_team_info_btn.setOnClickListener(this);
        update_team_icon_cv.setOnClickListener(this);
        update_team_icon_txt.setOnClickListener(this);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();
        //Get Current User refernece
        user = mAuth.getCurrentUser();

        if(getArguments()!=null){
            team_id = getArguments().getString("team_id");
            team_icon = getArguments().getString("team_icon");
            league_id = getArguments().getString("league_id");
            update_team_name_layout.getEditText().setText(getArguments().getString("team_name"));
            update_team_contact_layout.getEditText().setText(getArguments().getString("team_manager_contact"));

            if(!team_icon.equals("No Icon")){
                team_has_icon = true;
                byte[] decodedString = Base64.decode(team_icon, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                update_team_icon_img_view.setImageBitmap(decodedByte);
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view == update_team_icon_cv | view == update_team_icon_txt){
            SelectProfilePic();
        }
        else if(view == update_team_info_btn){
            update_team_name_layout.setErrorEnabled(false);
            update_team_contact_layout.setErrorEnabled(false);
            update_team_name_layout.setError(null);
            update_team_contact_layout.setError(null);

            if(TextUtils.isEmpty(update_team_name_layout.getEditText().getText().toString().trim())){
                update_team_name_layout.setError("Required fields are missing!");
            }
            else if(TextUtils.isEmpty(update_team_contact_layout.getEditText().getText().toString().trim())){
                update_team_contact_layout.setError("Required fields are missing!");
            }
            else if(!TextUtils.isDigitsOnly(update_team_contact_layout.getEditText().getText().toString().trim()) | update_team_contact_layout.getEditText().getText().toString().trim().length()!=10){
                update_team_contact_layout.setError("Please enter valid contact number!");
            }
            else {

                update_team_info_btn.setEnabled(false);
                if(image_uri!=null){
                    new EncodeImage(image_uri).execute();
                }
                else if(team_has_icon){
                    updateTeamInfo(team_icon);
                }
                else {
                    String encoded_team_icon = "No Icon";
                    updateTeamInfo(encoded_team_icon);
                }
            }
        }
    }

    /*-------- Below Code is for selecting image from galary or camera -----------*/

    private void SelectProfilePic() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (getActivity().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                                getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                            String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                            requestPermissions(permission, 1000);
                        } else {
                            openCamera();
                        }
                    } else {
                        openCamera();
                    }
                } else if (options[item].equals("Choose from Gallery")) {

                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");

                    startActivityForResult(Intent.createChooser(intent,"Select Image"), 2);

                } else if (options[item].equals("Cancel")) {

                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
        image_uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        //Camera intent
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(takePictureIntent, 1);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1000:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    //permisiion from pop up was denied.
                    Toast.makeText(getActivity().getApplicationContext(), "Permission Denied...", Toast.LENGTH_LONG).show();
                }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // Result code is RESULT_OK only if the user selects an Image
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 1:
                    update_team_icon_img_view.setImageURI(image_uri);
                    break;
                case 2:
                    //data.getData returns the content URI for the selected Image
                    image_uri = data.getData();
                    update_team_icon_img_view.setImageURI(image_uri);
                    break;
            }

        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    /*---------- Below Code is for converting image into Base64 Format to store in database as string ---------*/
    public class EncodeImage extends AsyncTask<Void, Void, String> {
        Uri uri;
        String encodedImage = "";
        public EncodeImage(Uri uri){
            this.uri = uri;
        }
        @Override
        protected String doInBackground(Void... params){

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), image_uri);
                InputStream input = context.getContentResolver().openInputStream(image_uri);
                ExifInterface ei;
                if (Build.VERSION.SDK_INT > 23) {
                    ei = new ExifInterface(input);
                }
                else {
                    ei = new ExifInterface(image_uri.getPath());
                }
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);

                Bitmap rotatedBitmap = null;
                switch(orientation) {

                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotatedBitmap = rotateImage(bitmap, 90);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotatedBitmap = rotateImage(bitmap, 180);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotatedBitmap = rotateImage(bitmap, 270);
                        break;

                    case ExifInterface.ORIENTATION_NORMAL:
                    default:
                        rotatedBitmap = bitmap;
                }

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputStream);
                byte[] byteArray = outputStream.toByteArray();
                encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return encodedImage;

        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            if(!result.equals("")) {
                String encoded_team_icon = result;
                updateTeamInfo(encoded_team_icon);
            }
        }

    }

    private void updateTeamInfo(String encoded_team_icon){

        String team_name = update_team_name_layout.getEditText().getText().toString().trim();
        String team_contact_number = update_team_contact_layout.getEditText().getText().toString().trim();
        Map<String, Object> team_data = new HashMap<>();
        team_data.put("team_name", team_name);
        team_data.put("team_icon", encoded_team_icon);
        team_data.put("team_manager_id", user.getUid());
        team_data.put("team_manager_contact",team_contact_number);
        team_data.put("league_id",league_id);

        // Add a new document with auto generated ID
        db.collection("teams").document(team_id)
                .set(team_data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        update_team_info_btn.setEnabled(true);
                        Toast.makeText(context, "Team Updated Successfully!", Toast.LENGTH_LONG).show();
                        HomeNavController.popBackStack();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        update_team_info_btn.setEnabled(true);
                        Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
