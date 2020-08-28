package com.prodev.moringaalumni;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    // views from xml
    ImageView avatarTv, coverTv;
    TextView nameTv, emailTv, phoneTv;
    FloatingActionButton fab;

    //progress dialog
    ProgressDialog pd;


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

    private void showEditProfileDialog() {
        // show dialog containing options
        // edit profile picture
        //edit cover photo
        // edit name
        // edit phone

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
                    showImagePicDialog();
                }
                else if (which == 1){
                    //edit cover clicked
                    pd.setMessage("Updating Cover Photo");
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
        builder.setTitle("Choose Action");
        //set items dialog
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                //handle dialog item clicks
                if (which == 0){
                    //edit profile clicked
                    pd.setMessage("Updating Profile Picture");
                    showImagePicDialog();
                }
                else if (which == 1){
                    //edit cover clicked
                    pd.setMessage("Updating Cover Photo");
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
}