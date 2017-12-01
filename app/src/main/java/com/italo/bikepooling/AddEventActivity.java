package com.italo.bikepooling;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.italo.bikepooling.data.FeedItem;

import java.util.Date;

public class AddEventActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private Button button;
    private FeedItem feedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        button = findViewById(R.id.inserir);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedItem = new FeedItem();
                feedItem.setImage("http://torcedores.uol.com.br/content/uploads/2014/08/gremio.png");
                feedItem.setName("Tricolor");
                feedItem.setProfilePic("http://www.gremiopedia.com/images/thumb/d/dd/Mascote_Gr%C3%AAmio_4_2000.png/130px-Mascote_Gr%C3%AAmio_4_2000.png");
                feedItem.setStatus("Grêmio Tricampeão da Libertadores");
                feedItem.setTimeStamp(String.valueOf(new Date().getTime()));
                feedItem.setUrl("http://www.gremio.net");
                mDatabase.child("feed").push().setValue(feedItem);
            }
        });
    }
}
