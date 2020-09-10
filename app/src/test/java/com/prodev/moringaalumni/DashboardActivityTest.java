package com.prodev.moringaalumni;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

class DashboardActivityTest {

    @Test
    void onCreate() {


    }



    @Test
    void firebaseUpdatesToken() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Tokens");

        assertEquals("Tokens", FirebaseDatabase.getInstance("Tokens"));
    }

    @Test
    void onBackPressed() {
    }

    @Test
    void onStart() {
    }
}