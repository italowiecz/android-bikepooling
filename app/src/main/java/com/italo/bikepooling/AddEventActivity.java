package com.italo.bikepooling;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DatabaseReference;
import com.italo.bikepooling.data.FeedItem;
import com.italo.bikepooling.response.Example;
import com.italo.bikepooling.service.GoogleMapsService;
import com.italo.bikepooling.service.NetworkService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddEventActivity extends AppCompatActivity implements Callback<Example>, OnMapReadyCallback {

    private DatabaseReference mDatabase;
    private Button button;
    private FeedItem feedItem;

    private GoogleMap mMap;
    LatLng origin;
    LatLng dest;
    ArrayList<LatLng> MarkerPoints;
    TextView showDistance;
    TextView showDuration;
    Polyline line;

    private GoogleMapsService googleMapsService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);


        showDuration = findViewById(R.id.show_duration);
        showDistance = findViewById(R.id.show_distance_time);

        // Initializing
        MarkerPoints = new ArrayList<>();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

/*        mDatabase = FirebaseDatabase.getInstance().getReference();

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
        });*/
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng Model_Town = new LatLng(-29.7608, -51.1522);
        mMap.addMarker(new MarkerOptions().position(Model_Town));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(Model_Town));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(13));

        googleMapsService = NetworkService.getInstance().getGoogleMapsService();

        // Setting onclick event listener for the map
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                // clearing map and generating new marker points if user clicks on map more than two times
                if (MarkerPoints.size() > 1) {
                    mMap.clear();
                    MarkerPoints.clear();
                    MarkerPoints = new ArrayList<>();
                    showDistance.setText("");
                    showDuration.setText("");
                }

                // Adding new item to the ArrayList
                MarkerPoints.add(point);

                // Creating MarkerOptions
                MarkerOptions options = new MarkerOptions();

                // Setting the position of the marker
                options.position(point);

                /**
                 * For the start location, the color of marker is GREEN and
                 * for the end location, the color of marker is RED.
                 */
                if (MarkerPoints.size() == 1) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                } else if (MarkerPoints.size() == 2) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }


                // Add new marker to the Google Map Android API V2
                mMap.addMarker(options);

                // Checks, whether start and end locations are captured
                if (MarkerPoints.size() >= 2) {
                    origin = MarkerPoints.get(0);
                    dest = MarkerPoints.get(1);

                    googleMapsService.getDistanceDuration(
                            "metric",
                            origin.latitude + "," + origin.longitude,
                            dest.latitude + "," + dest.longitude,
                            "bicycling").enqueue(AddEventActivity.this);
                }
            }
        });



/*        Button btnDriving = findViewById(R.id.btnDriving);
        btnDriving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleMapsService.getDistanceDuration(
                        "metric",
                        origin.latitude + "," + origin.longitude,
                        dest.latitude + "," + dest.longitude,
                        "driving").enqueue(AddEventActivity.this);
            }
        });

        Button btnWalk = findViewById(R.id.btnWalk);
        btnWalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleMapsService.getDistanceDuration(
                        "metric",
                        origin.latitude + "," + origin.longitude,
                        dest.latitude + "," + dest.longitude,
                        "walking").enqueue(AddEventActivity.this);
            }
        });*/

    }

    @Override
    public void onResponse(Call<Example> call, Response<Example> response) {
        try {
            //Remove previous line from map
            if (line != null) {
                line.remove();
            }
            // This loop will go through all the results and add marker on each location.
            for (int i = 0; i < response.body().getRoutes().size(); i++) {
                String distance = response.body().getRoutes().get(i).getLegs().get(i).getDistance().getText();
                String time = response.body().getRoutes().get(i).getLegs().get(i).getDuration().getText();
                showDistance.setText(distance);
                showDuration.setText(time);
                String encodedString = response.body().getRoutes().get(0).getOverviewPolyline().getPoints();
                List<LatLng> list = decodePoly(encodedString);
                line = mMap.addPolyline(new PolylineOptions()
                        .addAll(list)
                        .width(8)
                        .color(Color.RED)
                        .geodesic(true)
                );
            }
        } catch (Exception e) {
            Log.d("onResponse", "There is an error");
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(Call<Example> call, Throwable t) {
    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

}
