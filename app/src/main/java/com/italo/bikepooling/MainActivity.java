package com.italo.bikepooling;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.italo.bikepooling.adapter.FeedListAdapter;
import com.italo.bikepooling.data.FeedItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private ListView listView;
    private FeedListAdapter listAdapter;
    private List<FeedItem> feedItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        FloatingActionButton fab = findViewById(R.id.fab);
        listView = findViewById(R.id.list);
        feedItems = new ArrayList<>();
        listAdapter = new FeedListAdapter(this, feedItems);
        listView.setAdapter(listAdapter);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddEventActivity.class);
                startActivity(intent);

/*                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> childDataSnapshot = dataSnapshot.child("feed").getChildren();

                while (childDataSnapshot.iterator().hasNext()) {
                    DataSnapshot data = childDataSnapshot.iterator().next();
                    feedItems.add(data.getValue(FeedItem.class));
                }

                listAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
