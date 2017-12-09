package com.italo.bikepooling;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.italo.bikepooling.data.FeedItem;

import java.util.List;


public class TesteActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private List<FeedItem> feedItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teste);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        DatabaseReference aa = mDatabase.child("feed");

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> childDataSnapshot = dataSnapshot.child("feed").getChildren();
                while (childDataSnapshot.iterator().hasNext()) {
                    DataSnapshot data = childDataSnapshot.iterator().next();
                    feedItems.add(data.getValue(FeedItem.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
