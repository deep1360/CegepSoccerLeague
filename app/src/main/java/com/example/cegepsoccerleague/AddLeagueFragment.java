package com.example.cegepsoccerleague;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
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
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddLeagueFragment extends Fragment implements View.OnClickListener{

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private Context context;
    public NavController HomeNavController;
    private TextInputLayout league_name_layout;
    private TextInputEditText league_name_edit_txt;
    private MaterialButton add_league_btn;
    private TextView add_league_icon_txt;
    private ImageView add_league_icon_img_view;
    private MaterialCardView add_league_icon_cv;
    public Uri image_uri;
    Bundle dataBundle;
    public Toolbar HomeToolbar;
    private boolean league_has_icon = false;
    private static ProgressDialog progressDialog;


    public AddLeagueFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_league, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity().getApplicationContext();
        HomeNavController = Navigation.findNavController(getActivity(), R.id.home_host_fragment);
        HomeToolbar = getActivity().findViewById(R.id.home_toolbar);

        league_name_layout = view.findViewById(R.id.league_name_layout);
        league_name_edit_txt = view.findViewById(R.id.league_name_edit_txt);
        add_league_btn = view.findViewById(R.id.add_league_btn);
        add_league_icon_txt = view.findViewById(R.id.add_league_icon_txt);
        add_league_icon_img_view = view.findViewById(R.id.add_league_icon_img_view);
        add_league_icon_cv = view.findViewById(R.id.add_league_icon_cv);

        add_league_btn.setOnClickListener(this);
        add_league_icon_txt.setOnClickListener(this);
        add_league_icon_cv.setOnClickListener(this);


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();
        //Get Current User refernece
        user = mAuth.getCurrentUser();
        if(getArguments()!=null) {
            if (getArguments().getString("from") != null && getArguments().getString("from").equals("update league")) {
                dataBundle = getArguments();
                HomeToolbar.setTitle("Update League");
                league_name_layout.getEditText().setText(getArguments().getString("league_name"));
                add_league_btn.setText("Update League");

                if (!dataBundle.getString("league_icon").equals("No Icon")) {
                    byte[] decodedString = Base64.decode(dataBundle.getString("league_icon"), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    add_league_icon_img_view.setImageBitmap(decodedByte);
                    league_has_icon = true;
                }
            }
        }

    }

    @Override
    public void onClick(View view) {
        if (view == add_league_icon_cv | view == add_league_icon_txt){
            if(league_has_icon){
                RemovePhotoDialog();
            }
            else if(image_uri!=null){
                RemovePhotoDialog();
            }
            else {
                SelectProfilePic();
            }
        }
        else if(view == add_league_btn){

            if(TextUtils.isEmpty(league_name_layout.getEditText().getText().toString().trim())){
                league_name_layout.setError("Required fields are missing!");
            }
            else {
                league_name_layout.setErrorEnabled(false);
                add_league_btn.setEnabled(false);
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setCancelable(false);
                if(getArguments()!=null){
                    progressDialog.setMessage("Updating League");
                }
                else {
                    progressDialog.setMessage("Creating New League");
                }
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setProgress(0);
                progressDialog.show();
                if(image_uri!=null){
                    new EncodeImage(image_uri).execute();
                }
                else if(league_has_icon){
                    AddLeague(dataBundle.getString("league_icon"));
                }
                else {
                    String encoded_league_icon = "No Icon";
                    AddLeague(encoded_league_icon);
                }
            }
        }

    }

    private void RemovePhotoDialog() {
        final CharSequence[] options = {"Remove Photo", "Select Other Photo", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Change Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Remove Photo")) {
                    image_uri = null;
                    league_has_icon = false;
                    add_league_icon_img_view.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.add_league_icon));
                } else if (options[item].equals("Select Other Photo")) {
                    dialog.dismiss();
                    SelectProfilePic();

                } else if (options[item].equals("Cancel")) {

                    dialog.dismiss();
                }
            }
        });
        builder.show();
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
                    league_has_icon = false;
                    add_league_icon_img_view.setImageURI(image_uri);
                    break;
                case 2:
                    league_has_icon = false;
                    //data.getData returns the content URI for the selected Image
                    image_uri = data.getData();
                    add_league_icon_img_view.setImageURI(image_uri);
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
                AddLeague(encoded_league_icon);
            }
            else {
                progressDialog.dismiss();
                Toast.makeText(context,"Something Went Wrong!\nPlease Try Again..",Toast.LENGTH_LONG).show();
            }
        }

    }

    // Adding League in database
    private void AddLeague(String encoded_league_icon) {
        String league_name = league_name_layout.getEditText().getText().toString().trim();
        String league_manager_id = user.getUid();

        // Create a new league data object
        final Map<String, Object> league_data = new HashMap<>();
        league_data.put("league_name", league_name);
        league_data.put("league_icon", encoded_league_icon);
        league_data.put("league_manager_id", league_manager_id);

        if(getArguments()!=null){
            db.collection("leagues").document(dataBundle.getString("league_id"))
                    .set(league_data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressDialog.dismiss();
                            add_league_btn.setEnabled(true);
                            Toast.makeText(context, "League Updated Successfully!", Toast.LENGTH_LONG).show();
                            HomeNavController.popBackStack();
                            HomeNavController.popBackStack();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            add_league_btn.setEnabled(true);
                            Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }
        else {
            db.collection("leagues").whereEqualTo("league_name",league_name).whereEqualTo("league_manager_id",league_manager_id)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    QuerySnapshot document = task.getResult();
                    if (document.size()>0){
                        progressDialog.dismiss();
                        add_league_btn.setEnabled(true);
                        league_name_layout.setError("League name already exist!");
                    }
                    else {
                        // Add a new document with auto generated ID
                        db.collection("leagues")
                                .add(league_data)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        progressDialog.dismiss();
                                        add_league_btn.setEnabled(true);
                                        Toast.makeText(context, "League Created Successfully!", Toast.LENGTH_LONG).show();
                                        HomeNavController.popBackStack();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        add_league_btn.setEnabled(true);
                                        Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                }
            });

        }
    }

}
