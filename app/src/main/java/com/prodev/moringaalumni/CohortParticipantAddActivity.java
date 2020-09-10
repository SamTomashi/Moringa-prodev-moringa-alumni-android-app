package com.prodev.moringaalumni;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.prodev.moringaalumni.Adapters.AdapterParticipantAdd;
import com.prodev.moringaalumni.Models.ModelUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CohortParticipantAddActivity extends AppCompatActivity {

    private RecyclerView usersRV;
    private ActionBar actionBar;

    private FirebaseAuth firebaseAuth;
    private String groupId,myGrouprole;

    private ArrayList<ModelUser> userslist;
    private AdapterParticipantAdd adapterParticipantAdd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_participant_add);

        actionBar=getSupportActionBar();
        actionBar.setTitle("Add Participant");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        usersRV=findViewById(R.id.usersRV);

        firebaseAuth=FirebaseAuth.getInstance();

        groupId=getIntent().getStringExtra("groupId");

        loadGroupInfo();

    }

    private void getAllUsers() {

        userslist=new ArrayList<>();
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userslist.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    ModelUser modelUser =ds.getValue(ModelUser.class);
                    if (!firebaseAuth.getUid().equals(modelUser.getUid())){
                        userslist.add(modelUser);

                    }
                }
                adapterParticipantAdd=new AdapterParticipantAdd( CohortParticipantAddActivity.this,userslist,""+groupId,""+myGrouprole);
                usersRV.setAdapter(adapterParticipantAdd);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadGroupInfo()  {
        final DatabaseReference ref1= FirebaseDatabase.getInstance().getReference("Groups");

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Groups");
        ref.orderByChild("groupId").equalTo(groupId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds:snapshot.getChildren()){
                            final String groupId=""+ds.child("groupId").getValue();
                            final String groupTitle=""+ds.child("groupTitle").getValue();
                            String groupDescription=""+ds.child("groupDescription").getValue();
                            String groupIcon=""+ds.child("groupIcon").getValue();
                            String createdBy=""+ds.child("createdBy").getValue();
                            String timestamp=""+ds.child("timestamp").getValue();

                            actionBar.setTitle("Add Participants");

                            ref1.child(groupId).child("Participants").child(firebaseAuth.getUid())
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()){
                                                myGrouprole=""+snapshot.child("role").getValue();
                                                actionBar.setTitle(groupTitle+"("+myGrouprole+")");

                                                getAllUsers();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}