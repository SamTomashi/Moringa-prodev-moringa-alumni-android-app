package com.prodev.moringaalumni.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.prodev.moringaalumni.Adapters.AdapterUsers;
import com.prodev.moringaalumni.CohortCreateActivity;
import com.prodev.moringaalumni.MainActivity;
import com.prodev.moringaalumni.Models.ModelUser;
import com.prodev.moringaalumni.R;
import com.prodev.moringaalumni.SettingActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.*;

public class UsersFragment extends Fragment {



    RecyclerView  recyclerView;
    AdapterUsers adapterUsers;
    List<ModelUser>usersList;

    FirebaseAuth firebaseAuth;


    public UsersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_users, container, false);

        firebaseAuth=FirebaseAuth.getInstance();

        recyclerView=view.findViewById(R.id.user_recycler);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        usersList=new ArrayList<>();
        getAllUser();

        return view;
    }

    private void getAllUser() {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                usersList.clear();
                for (DataSnapshot  ds:snapshot.getChildren()){
                    ModelUser modelUser =ds.getValue(ModelUser.class);

                    assert modelUser != null;
                    if (!Objects.equals(modelUser.getUid(), requireNonNull(firebaseUser).getUid())){
                        usersList.add(modelUser);

                    }
                    adapterUsers=new AdapterUsers(getActivity(),usersList);
                    recyclerView.setAdapter(adapterUsers);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void searchUsers(final String query) {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                usersList.clear();
                for (DataSnapshot  ds:snapshot.getChildren()){
                    ModelUser modelUser =ds.getValue(ModelUser.class);

                    if (!firebaseUser.getUid().equals(firebaseUser.getUid())){

                        if (modelUser.getName().toLowerCase().contains(query.toLowerCase()) ||
                                modelUser.getEmail().toLowerCase().contains(query.toLowerCase())){

                            usersList.add(modelUser);

                        }


                    }
                    adapterUsers=new AdapterUsers(getActivity(),usersList);

                    adapterUsers.notifyDataSetChanged();

                    recyclerView.setAdapter(adapterUsers);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }




    private void checkUserStatus(){

        FirebaseUser user=firebaseAuth.getCurrentUser();

        if (user!=null)
        {

//            mProfile.setText(user.getEmail());

        }
        else {
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main,menu);

        menu.findItem(R.id.action_add_post).setVisible(false);
        menu.findItem(R.id.action_add_participant).setVisible(false);
        menu.findItem(R.id.action_groupinfo).setVisible(false);

        MenuItem item=menu.findItem(R.id.action_search);
        SearchView searchView= (SearchView) MenuItemCompat.getActionView(item);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (!TextUtils.isEmpty(s.trim())){
                    searchUsers(s);

                }else {
                    getAllUser();

                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                if (!TextUtils.isEmpty(s.trim())){
                    searchUsers(s);

                }else {
                    getAllUser();

                }

                return false;
            }
        });

        super.onCreateOptionsMenu(menu,inflater);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if (id==R.id.action_logout){

            firebaseAuth.signOut();
            checkUserStatus();
        }
        else if(id==R.id.action_settings){
            startActivity(new Intent(getContext(), SettingActivity.class));
        }
        else if(id==R.id.action_create_group){
            startActivity(new Intent(getContext(), CohortCreateActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

}