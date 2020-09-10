package com.prodev.moringaalumni;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CohortEditActivity extends AppCompatActivity {

    private ActionBar actionBar;
    private String groupId;
    private FirebaseAuth firebaseAuth;
    @BindView ( R.id.groupIconIv )ImageView groupIconIv;
    @BindView ( R.id.groupTitleET )EditText groupTitleET;
    @BindView ( R.id.groupDescriptionEt )EditText groupDescriptionEt;
    @BindView ( R.id. updateGroupBtn)FloatingActionButton updateGroupBtn;
//    ImageView groupIconIv;
//    EditText groupTitleET,groupDescriptionEt;
//    FloatingActionButton updateGroupBtn;

    private static final int CAMERA_REQUEST_CODE=100;
    private static final int STORAGE_REQUEST_CODE=200;

    private static final int IMAGE_PICK_CAMERA_CODE=300;
    private static final int IMAGE_PICK_GALLERY_CODE=400;

    String[] cameraPermission;
    String[] storagePermission;

    private Uri image_uri=null;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_edit);
        ButterKnife.bind ( this );

        actionBar=getSupportActionBar();
        actionBar.setTitle("Edit Cohort");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

//        groupIconIv=findViewById(R.id.groupIconIv);
//        groupTitleET=findViewById(R.id.groupTitleET);
//        groupDescriptionEt=findViewById(R.id.groupDescriptionEt);
//        updateGroupBtn=findViewById(R.id.updateGroupBtn);

        groupId=getIntent().getStringExtra("groupId");

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        cameraPermission=new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        firebaseAuth=FirebaseAuth.getInstance();
        checkUser();
        loadGroupInfo();

        groupIconIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePicDialog();

            }
        });

        updateGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startUpdatingGroup();
            }
        });

    }

    private void startUpdatingGroup() {
        final String groupTitle=groupTitleET.getText().toString().trim();
        final String groupDescription=groupDescriptionEt.getText().toString().trim();

        if (TextUtils.isEmpty(groupTitle)){
            Toast.makeText(this, "Cohort Title is required....", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.setMessage("Updating Cohort Info....");
        progressDialog.show();

        if (image_uri==null){

            HashMap<String,Object> hashMap=new HashMap<>();
            hashMap.put( "groupTitle",groupTitle);
            hashMap.put( "groupDescription",groupDescription);

            DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Groups");
            ref.child(groupId).updateChildren(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressDialog.dismiss();
                            Toast.makeText( CohortEditActivity.this, "Cohort Info Updated.....", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText( CohortEditActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }
        else {

            final String timestamp=""+System.currentTimeMillis();
            String fileNameAndPath="Group_Imgs/"+"image"+"_"+timestamp;

            StorageReference storageReference= FirebaseStorage.getInstance().getReference(fileNameAndPath);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> p_UriTask=taskSnapshot.getStorage().getDownloadUrl();
                            while (!p_UriTask.isSuccessful());

                            Uri p_downloadUri=p_UriTask.getResult();
                            if (p_UriTask.isSuccessful()){

                                HashMap<String,Object> hashMap=new HashMap<>();
                                hashMap.put( "groupTitle",groupTitle);
                                hashMap.put( "groupDescription",groupDescription);
                                hashMap.put( "groupIcon",""+p_downloadUri);

                                DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Groups");
                                ref.child(groupId).updateChildren(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                progressDialog.dismiss();
                                                Toast.makeText( CohortEditActivity.this, "Cohort Info Updated.....", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                Toast.makeText( CohortEditActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });


                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText( CohortEditActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void loadGroupInfo() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Groups");
        ref.orderByChild("groupId").equalTo(groupId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds:snapshot.getChildren()){
                            String groupId=""+ds.child("groupId").getValue();
                            String groupTitle=""+ds.child("groupTitle").getValue();
                            String groupDescription=""+ds.child("groupDescription").getValue();
                            String groupIcon=""+ds.child("groupIcon").getValue();
                            String createdBy=""+ds.child("createdBy").getValue();
                            String timestamp=""+ds.child("timestamp").getValue();

                            Calendar cal=Calendar.getInstance(Locale.ENGLISH);
                            cal.setTimeInMillis(Long.parseLong(timestamp));
                            String dateTime= DateFormat.format("dd/MM/yyyy hh:mm aa",cal).toString();

                            groupTitleET.setText(groupTitle);
                            groupDescriptionEt.setText(groupDescription);

                            try {
                                Picasso.get().load(groupIcon).placeholder(R.drawable.ic_groupicon_primary).into(groupIconIv);
                            }catch (Exception e){
                                groupIconIv.setImageResource(R.drawable.ic_groupicon_primary);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void showImagePicDialog() {
        String option[]={"Camera","Gallery"};

        AlertDialog.Builder  builder=new AlertDialog.Builder(this);
        builder.setTitle("Choose Image From");
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i==0){
                    if (!checkCameraPermission()){
                        requestCameraPermission();
                    }else {
                        pickFromCamera();
                    }

                }
                if (i==1){
                    if (!checkStoragePermission()){
                        requestStoragePermission();
                    }else {
                        pickFromGallery();
                    }

                }

            }
        });
        builder.create().show();
    }

    private boolean checkCameraPermission(){

        boolean result= ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)
                ==(PackageManager.PERMISSION_GRANTED);

        boolean result1= ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ==(PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }
    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this,cameraPermission,CAMERA_REQUEST_CODE);
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,storagePermission,STORAGE_REQUEST_CODE);
    }
    private boolean checkStoragePermission() {
        boolean result= ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ==(PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void pickFromCamera() {
        ContentValues values=new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"Group Image Icon Title");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Group Image Icon Description");

        image_uri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(cameraIntent,IMAGE_PICK_CAMERA_CODE);
    }
    private void pickFromGallery() {
        Intent galleryIntent=new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,IMAGE_PICK_GALLERY_CODE);
    }

    private void checkUser() {
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if (user!=null){
            actionBar.setSubtitle(user.getEmail());
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case CAMERA_REQUEST_CODE:
            {
                if (grantResults.length>0){
                    boolean cameraAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted=grantResults[1]==PackageManager.PERMISSION_GRANTED;

                    if (cameraAccepted&&writeStorageAccepted){
                        pickFromCamera();
                    }else {
                        Toast.makeText(this, "Please Enable Camera & Storage Permission", Toast.LENGTH_SHORT).show();
                    }
                }else {

                }

            }
            break;
            case STORAGE_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean writeStorageAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;

                    if (writeStorageAccepted){
                        pickFromGallery();
                    }
                    else {
                        Toast.makeText(this, "Please Enable  Storage Permission", Toast.LENGTH_SHORT).show();
                    }
                }else {

                }

            }
            break;
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode==RESULT_OK){
            if (requestCode==IMAGE_PICK_GALLERY_CODE){

                image_uri=data.getData();
                groupIconIv.setImageURI(image_uri);
            }
            else if (requestCode==IMAGE_PICK_CAMERA_CODE){

                groupIconIv.setImageURI(image_uri);


            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public boolean onNavigateUp() {
        onBackPressed();
        return super.onNavigateUp();
    }
}