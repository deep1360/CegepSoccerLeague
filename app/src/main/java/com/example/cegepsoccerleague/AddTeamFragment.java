package com.example.cegepsoccerleague;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import com.google.android.material.textfield.TextInputEditText;
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
public class AddTeamFragment extends Fragment implements View.OnClickListener{

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private Context context;
    public NavController HomeNavController;
    private MaterialButton add_team_btn;
    private TextView add_team_icon_txt;
    private ImageView add_team_icon_img_view;
    private MaterialCardView add_team_icon_cv;
    public Uri image_uri;
    private TextInputLayout team_name_layout, tm_firstname, tm_lastname, tm_contact_num, tm_email, tm_password, tm_confirm_password;
    private String league_id;

    public AddTeamFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_team, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity().getApplicationContext();
        HomeNavController = Navigation.findNavController(getActivity(), R.id.home_host_fragment);

        //Getting Selected League Id
        if(getArguments()!=null){
            league_id = getArguments().getString("league_id");
        }

        team_name_layout = view.findViewById(R.id.team_name_layout);
        tm_firstname = view.findViewById(R.id.tm_firstname);
        tm_lastname = view.findViewById(R.id.tm_lastname);
        tm_contact_num = view.findViewById(R.id.tm_contact_num);
        tm_email = view.findViewById(R.id.tm_email);
        tm_password = view.findViewById(R.id.tm_password);
        tm_confirm_password = view.findViewById(R.id.tm_confirm_password);
        add_team_btn = view.findViewById(R.id.add_team_btn);
        add_team_icon_txt = view.findViewById(R.id.add_team_icon_txt);
        add_team_icon_img_view = view.findViewById(R.id.add_team_icon_img_view);
        add_team_icon_cv = view.findViewById(R.id.add_team_icon_cv);

        add_team_btn.setOnClickListener(this);
        add_team_icon_txt.setOnClickListener(this);
        add_team_icon_cv.setOnClickListener(this);


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();
        //Get Current User refernece
        user = mAuth.getCurrentUser();

    }

    @Override
    public void onClick(View view) {
        if (view == add_team_icon_cv | view == add_team_icon_txt){
            SelectProfilePic();
        }
        else if(view == add_team_btn){

            if(TextUtils.isEmpty(team_name_layout.getEditText().getText().toString().trim())){
                team_name_layout.setError("Required fields are missing!");
            }
            else if(TextUtils.isEmpty(tm_firstname.getEditText().getText().toString().trim())){
                tm_firstname.setError("Required fields are missing!");
            }
            else if(TextUtils.isEmpty(tm_lastname.getEditText().getText().toString().trim())){
                tm_lastname.setError("Required fields are missing!");
            }
            else if(TextUtils.isEmpty(tm_contact_num.getEditText().getText().toString().trim())){
                tm_contact_num.setError("Required fields are missing!");
            }
            else if(!TextUtils.isDigitsOnly(tm_contact_num.getEditText().getText().toString().trim()) | tm_contact_num.getEditText().getText().toString().trim().length()!=10){
                tm_contact_num.setError("Please enter valid contact number!");
            }
            else if(!isEmailValid(tm_email.getEditText().getText().toString().trim())){
                tm_email.setError("Please enter valid email!");
            }
            else if(TextUtils.isEmpty(tm_password.getEditText().getText().toString().trim())){
                tm_password.setError("Required Fields are missing!");
            }
            else if(tm_password.getEditText().getText().toString().trim().length()<6){
                tm_password.setError("Password must be 6 characters or long!");
            }
            else if(!tm_password.getEditText().getText().toString().trim().equals(tm_confirm_password.getEditText().getText().toString().trim())){
                tm_confirm_password.setError("Confirm password doesn't match with password!");
            }
            else {
                add_team_btn.setEnabled(false);
                if(image_uri!=null){
                    new EncodeImage(image_uri).execute();
                }
                else {
                    String encoded_league_icon = "No Icon";
                    AddTeam(encoded_league_icon);
                }
            }
        }
    }

    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
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
                    add_team_icon_img_view.setImageURI(image_uri);
                    break;
                case 2:
                    //data.getData returns the content URI for the selected Image
                    image_uri = data.getData();
                    add_team_icon_img_view.setImageURI(image_uri);
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
                String encoded_league_icon = result;
                //AddTeam(encoded_league_icon);
            }
        }

    }

    // Adding Team in database
    private void AddTeam(final String encoded_league_icon) {

        final String team_name = team_name_layout.getEditText().getText().toString().trim();
        final String first_name = tm_firstname.getEditText().getText().toString().trim();
        final String last_name = tm_lastname.getEditText().getText().toString().trim();
        final String email = tm_email.getEditText().getText().toString().trim();
        final String team_contact_number = tm_contact_num.getEditText().getText().toString().trim();
        String password = tm_password.getEditText().getText().toString().trim();

        final FirebaseAuth mAuth2 = FirebaseAuth.getInstance();
        //Creating Team manager in Firebase Authentication
        mAuth2.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // Sign Up success, Create User in Cloud Firestore
                            final FirebaseUser user = mAuth2.getCurrentUser();
                            // Create a new user with a first and last name and user_type
                            final Map<String, Object> usermap = new HashMap<>();
                            usermap.put("first_name", first_name);
                            usermap.put("last_name", last_name);
                            usermap.put("email",email);
                            usermap.put("user_type", "TM");

                            // Add a new document in database with team manager's generated ID in authentication
                            db.collection("users")
                                    .document(user.getUid())
                                    .set(usermap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            //Add New Team in Database
                                            // Create a new team data object
                                            Map<String, Object> team_data = new HashMap<>();
                                            team_data.put("team_name", team_name);
                                            team_data.put("team_icon", encoded_league_icon);
                                            team_data.put("team_manager_id", user.getUid());
                                            team_data.put("team_manager_contact",team_contact_number);
                                            team_data.put("league_id",league_id);

                                            // Add a new document with auto generated ID
                                            db.collection("teams")
                                                    .add(team_data)
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {
                                                            mAuth2.signOut();
                                                            mAuth.signInWithEmailAndPassword(PreferenceData.getUseremail(context), PreferenceData.getUserpassword(context))
                                                                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                                                            if (task.isSuccessful()) {
                                                                                // Sign in success, update UI with the signed-in user's information
                                                                                add_team_btn.setEnabled(true);
                                                                                Toast.makeText(context, "Team Added Successfully!", Toast.LENGTH_LONG).show();
                                                                                HomeNavController.popBackStack();
                                                                            } else {
                                                                                // If sign in fails, display a message to the user.
                                                                                Toast.makeText(context,"Something went wrong! Please Try Again", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            add_team_btn.setEnabled(true);
                                                            Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                                        }
                                                    });


                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    user.delete()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                    }
                                                }
                                            });
                                    Toast.makeText(context,"Team Manager Datastore: " + e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                                    add_team_btn.setEnabled(true);
                                }
                            });


                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(context,"Team Manager Authetication: " + task.getException().getLocalizedMessage(),Toast.LENGTH_LONG).show();
                            add_team_btn.setEnabled(true);
                        }
                    }
                });
    }

}
