package com.prodev.moringaalumni;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    //storage
    StorageReference storageReference;
    // path where images of user profile and cover will be stored
    String storagePath = "Users_Profile_Cover_Imgs/";


    // views from xml
    ImageView avatarTv, coverTv;
    TextView nameTv, emailTv, phoneTv;
    FloatingActionButton fab;

    //progress dialog
    ProgressDialog pd;

    //permissions constants
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;

    //ARRAYS of permission to be requseted
    String cameraPermissions[];
    String storagePermission[];

    //uri of picked image
    Uri image_uri;


    // for checking  profile or cover photo
    String profileOrCoverPhoto;
    public ProfileFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //init firebase
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

        // init arrays of permissions
        cameraPermissions = new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //init views
        avatarTv = view.findViewById(R.id.avatarIv);
        coverTv = view.findViewById(R.id.coverTv);
        nameTv = view.findViewById(R.id.nameTv);
        emailTv = view.findViewById(R.id.emailTv);
        phoneTv = view.findViewById(R.id.phoneTv);
        fab = view.findViewById(R.id.fab);

        //init progress dialog
        pd = new ProgressDialog(getActivity());

        // get info of the currently signed in user using user's email
        // using  orderByChild query to show details from node
        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // check until we get the required data
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    // get data
                    String name = "" + ds.child("name").getValue();
                    String email = "" + ds.child("email").getValue();
                    String phone = "" + ds.child("phone").getValue();
                    String image = "" + ds.child("image").getValue();
                    String cover = "" + ds.child("cover").getValue();

                    //set data
                    nameTv.setText(name);
                    emailTv.setText(email);
                    phoneTv.setText(phone);
                    try {
                        // if image received then set
                        Picasso.get().load(image).into(avatarTv);
                    }
                    catch (Exception e){
                        // if there is any exception while getting image then set default
                        Picasso.get().load(R.drawable.ic_default_img_white).into(avatarTv);
                    }
                    try {
                        // if image received then set
                        Picasso.get().load(cover).into(coverTv);
                    }
                    catch (Exception e){
                        // if there is any exception while getting image then set default

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // fab button click
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditProfileDialog();

            }
        });
        return view;
    }
    private boolean checkStoragePermission(){
        //check if storage perm is allowed or not
        // return true if anabled
        //return false if not
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);

                return result;
    }
    private void requestStoragePermission(){
        //request runtime storage permission
        ActivityCompat.requestPermissions(getActivity(), storagePermission, STORAGE_REQUEST_CODE);
    }


    private boolean checkcameraPermission(){
        //check if storage perm is allowed or not
        // return true if anabled
        //return false if not
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }
    private void requestCameraPermission(){
        //request runtime storage permission
        ActivityCompat.requestPermissions(getActivity(), cameraPermissions, CAMERA_REQUEST_CODE);
    }

    private void showEditProfileDialog() {

        //options to show dialog
        String options[] = {"Edit Profile Picture", "Edit Cover Photo", "Edit Name", "Edit Phone"};
        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // set title
        builder.setTitle("Choose Action");
        //set items dialog
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                //handle dialog item clicks
                if (which == 0){
                    //edit profile clicked
                    pd.setMessage("Updating Profile Picture");
                    profileOrCoverPhoto = "cover";// i.e changing cover photo, make sure to assign same value
                    showImagePicDialog();
                }
                else if (which == 1){
                    //edit cover clicked
                    pd.setMessage("Updating Cover Photo");
                    profileOrCoverPhoto = "cover";// i.e changing cover photo, make sure to assign same value
                    showImagePicDialog();
                }
                else if (which == 2){
                    //edit name clicked
                    pd.setMessage("Updating Name");
                }
                else if (which == 3){
                    //edit phone clicked
                    pd.setMessage("Updating Phone");
                }
            }
        });
        //create and show dialog
        builder.create().show();
    }

    private void showImagePicDialog() {
        //show diallog contaning options Camera and gallery to pick the image
        String options[] = {"Camera", "Gallery"};
        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // set title
        builder.setTitle("Pick Image From");
        //set items dialog
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                //handle dialog item clicks
                if (which == 0){
                    //Camera clicked

                    if(!checkcameraPermission()){
                        requestCameraPermission();
                    }
                    else {
                        pickFromCamera();
                    }

                }
                else if (which == 1){
                    //Gallery clicked
                    if(!checkStoragePermission()){
                        requestStoragePermission();
                    }
                    else {
                        pickFromGallery();
                    }
                }
            }
        });
        //create and show dialog
        builder.create().show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // This method called when user press Allow or deny from permmisipon req dialog
        // handling perm cases

        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                //picking from camera, first check if camera and storage permissions allowed or not
                if(grantResults.length >0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted){
                        //permission enabled
                        pickFromCamera();
                    }
                    else {
                        //permission denied
                        Toast.makeText(getActivity(), "Please Enable Camera & Storage Permision", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            case STORAGE_REQUEST_CODE:{
                //picking from gallery, first check if camera and storage permissions allowed or not
                if(grantResults.length >0){
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted){
                        //permission enabled
                        pickFromGallery();
                    }
                    else {
                        //permission denied
                        Toast.makeText(getActivity(), "Please Enable Camera & Storage Permision", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //this method will be called after picking image from camera gallery
        if(resultCode == RESULT_OK){

            if(requestCode == IMAGE_PICK_GALLERY_CODE){
                //image is picked from gallery, get uri of image
                image_uri= data.getData();

                uploadProfileCoverPhoto(image_uri);
            }
            if(requestCode == IMAGE_PICK_GALLERY_CODE){
                //image is picked from gallery, get uri of image

                uploadProfileCoverPhoto(image_uri);
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadProfileCoverPhoto(Uri image_uri) {
        // show progress
        pd.show();
        // a function for both profile picture and cover photo
        // image is key in each user containing url of user's profile pictures cover is the key in
        //in each user
        //path and name of image to be stored in FB storage

        String filePathAndName = storagePath+ ""+ profileOrCoverPhoto + "_"+ user.getUid();

        StorageReference storageReference2nd = storageReference.child(filePathAndName);
        storageReference2nd.putFile(image_uri);


    }

    private void pickFromCamera() {
        //intent for picking image from device camera
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temporary Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");

        // put image uri
        image_uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        //intent to start camera
        Intent cameraIntent = new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);

    }

    private void pickFromGallery() {
        //pick from gallery
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);

    }


}