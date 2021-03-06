package com.italo.bikepooling;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.italo.bikepooling.data.FeedItem;
import com.italo.bikepooling.response.MapsAPI;
import com.italo.bikepooling.service.GoogleMapsService;
import com.italo.bikepooling.service.NetworkService;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddEventActivity extends AppCompatActivity implements Callback<MapsAPI>, OnMapReadyCallback, View.OnClickListener {

    private DatabaseReference mDatabase;
    private FirebaseStorage storage;
    private Button btCriarEvento;
    private FeedItem feedItem;
    private GoogleMap mMap;
    private LatLng origin;
    private LatLng dest;
    private ArrayList<LatLng> markerPoints;
    private TextView showDistance, showDuration;
    private Polyline line;
    private GoogleMapsService googleMapsService;
    private EditText etData, etTime, etDescricao;
    private DatePickerDialog dataPickerDialog;
    private TimePickerDialog horaPickerDialog;
    private SimpleDateFormat dataFormatter;
    private SupportMapFragment mapFragment;
    private int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        findViewsById();
        setDateField();
        setTimeField();
        checkWriteExternalStoragePermission();

        storage = FirebaseStorage.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        markerPoints = new ArrayList<>();

    }

    private void checkWriteExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        }
    }

    private void findViewsById() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        btCriarEvento = findViewById(R.id.novo_evento);
        btCriarEvento.setOnClickListener(this);
        showDuration = findViewById(R.id.show_duration);
        showDistance = findViewById(R.id.show_distance_time);
        etData = findViewById(R.id.et_data);
        etData.setInputType(InputType.TYPE_NULL);
        etTime = findViewById(R.id.et_hora);
        etDescricao = findViewById(R.id.et_descricao);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng Model_Town = new LatLng(-29.7608, -51.1522);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(Model_Town));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(13));

        googleMapsService = NetworkService.getInstance().getGoogleMapsService();

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                if (markerPoints.size() > 1) {
                    mMap.clear();
                    markerPoints.clear();
                    markerPoints = new ArrayList<>();
                    showDistance.setText("");
                    showDuration.setText("");
                }

                markerPoints.add(point);

                MarkerOptions options = new MarkerOptions();

                options.position(point);

                if (markerPoints.size() == 1) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                } else if (markerPoints.size() == 2) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }

                mMap.addMarker(options);

                if (markerPoints.size() >= 2) {
                    origin = markerPoints.get(0);
                    dest = markerPoints.get(1);

                    mapScaleResize();

                    googleMapsService.getDistanceDuration(
                            "metric",
                            origin.latitude + "," + origin.longitude,
                            dest.latitude + "," + dest.longitude,
                            "bicycling",
                            "pt-BR").enqueue(AddEventActivity.this);
                }
            }
        });

    }

    private void mapScaleResize() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (int i = 0; i < markerPoints.size(); i++) {
            builder.include(markerPoints.get(i));
        }
        LatLngBounds bounds = builder.build();

        int padding = 150; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.animateCamera(cu);
    }

    @Override
    public void onResponse(Call<MapsAPI> call, Response<MapsAPI> response) {
        try {
            if (line != null) {
                line.remove();
            }
            for (int i = 0; i < response.body().getRoutes().size(); i++) {
                String distance = response.body().getRoutes().get(i).getLegs().get(i).getDistance().getText();
                String time = formatSecondsToHoursMinutesSeconds(response.body().getRoutes().get(i).getLegs().get(i).getDuration().getValue());
                String encodedString = response.body().getRoutes().get(0).getOverviewPolyline().getPoints();

                showDistance.setText(distance);
                showDuration.setText(time);
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

    private String formatSecondsToHoursMinutesSeconds(Integer time) {
        Integer horas = time / 3600;
        Integer minutos = (time % 3600) / 60;
        Integer segundos = time % 60;
        return String.format("%02d:%02d:%02d", horas, minutos, segundos);
    }

    @Override
    public void onFailure(Call<MapsAPI> call, Throwable t) {
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

    private void setDateField() {
        etData.setOnClickListener(this);
        dataFormatter = new SimpleDateFormat("dd/MM", Locale.getDefault());
        Calendar newCalendar = Calendar.getInstance();

        dataPickerDialog = new DatePickerDialog(this, AlertDialog.THEME_HOLO_DARK, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                etData.setText(dataFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    private void setTimeField() {
        etTime.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();

        horaPickerDialog = new TimePickerDialog(this, AlertDialog.THEME_HOLO_DARK, new TimePickerDialog.OnTimeSetListener() {

            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                etTime.setText(hourOfDay + ":" + minute);
            }
        }, newCalendar.get(Calendar.HOUR_OF_DAY), newCalendar.get(Calendar.MINUTE), true);
    }

    @Override
    public void onClick(View v) {
        if (v == etData) {
            dataPickerDialog.show();
        } else if (v == etTime) {
            horaPickerDialog.show();
        } else if (v == btCriarEvento) {
            adicionarEventoDatabase();
        }
    }

    private void adicionarEventoDatabase() {
        CaptureMapScreen();

        feedItem = new FeedItem();
        feedItem.setData(etData.getText().toString());
        feedItem.setHora(etTime.getText().toString());
        feedItem.setDistancia(showDistance.getText().toString());
        feedItem.setTempoEstimado(showDuration.getText().toString());
        feedItem.setNome("Mocked User");
        feedItem.setTimeStamp(String.valueOf(new Date().getTime()));
        feedItem.setDescricao(etDescricao.getText().toString());
        feedItem.setImagemProfile("https://image.freepik.com/free-icon/male-user-silhouette_318-35708.jpg");

        getImageRouteFromGoogleStorage();
    }

    private void backToMainActivity() {
        Intent intent = new Intent(AddEventActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void getImageRouteFromGoogleStorage() {
        storage.getReference().child("routes" + path).getDownloadUrl().addOnSuccessListener((new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                feedItem.setImagemRota(uri.toString());
                mDatabase.child("feed").push().setValue(feedItem);
                Toast.makeText(AddEventActivity.this, "Evento criado com sucesso!", Toast.LENGTH_SHORT).show();
                backToMainActivity();
            }
        })).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("tag", "Erro");
            }
        });
    }

    private void CaptureMapScreen() {
        GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
            Bitmap bitmap;


            @Override
            public void onSnapshotReady(Bitmap snapshot) {
                bitmap = snapshot;
                try {
                    StorageReference storageRef = storage.getReference();
                    path = "/RouteImage" + System.currentTimeMillis() + ".png";
                    File storageDir = Environment.getExternalStorageDirectory();
                    File file = new File(storageDir, Environment.DIRECTORY_DOWNLOADS + path);
                    FileOutputStream out = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                    Uri fileUrl = Uri.fromFile(file);
                    storageRef.child("routes" + path).putFile(fileUrl);
                } catch (Exception e) {
                    Log.e("ADD EVENTO BANCO", "Erro!");
                    e.printStackTrace();
                }
            }
        };
        mMap.snapshot(callback);
    }

    @Override
    public void onBackPressed() {
        backToMainActivity();
    }

}
