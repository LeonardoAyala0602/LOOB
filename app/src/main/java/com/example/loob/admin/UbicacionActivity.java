package com.example.loob.admin;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loob.R;
import com.example.loob.dto.SolicitudDTO;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class UbicacionActivity extends AppCompatActivity {

    private FusedLocationProviderClient client;
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private ActivityResultLauncher<String[]> requestPermissonForLocation;
    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;
    private Geocoder geocoder;
    private double selectedLat,selectedLong;
    private List<Address> listAddress;
    private String selectedAddress;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubicacion);
        Intent intent = getIntent();
        SolicitudDTO solicitud = (SolicitudDTO) intent.getSerializableExtra("solicitud");
        String nombre = solicitud.getNombreObjeto();
        TextView textNombre = findViewById(R.id.textNombreUbicacion);
        textNombre.setText(nombre);
        Button btnAceptarReserva =  findViewById(R.id.btnAceptarReserva);
        btnAceptarReserva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("msg",solicitud.getEstado());
                solicitud.setEstado("aceptado");
                databaseReference.child("solicitudes").child(solicitud.getId()).setValue(solicitud).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse("mailto:"));
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {solicitud.getCorreoPucp()});
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Aceptacion de solicitud");
                        intent.putExtra(Intent.EXTRA_TEXT, "Las coordenadas del encuentro son las siguientes: LATITUD (" + String.valueOf(selectedLat) + ")  LONGITUD (" + String.valueOf(selectedLong)+ ")");
                        startActivity(intent);
                        finish();
                    }
                });

            }
        });

        Button btnCancelar = findViewById(R.id.btnCancelarPedido);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(UbicacionActivity.this, ListaSolicitudesActivity.class);
                startActivity(intent);
            }
        });

        requestPermissonForLocation = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                    Boolean fineLocationGranted = result.get(Manifest.permission.ACCESS_FINE_LOCATION);
                    Boolean coarseLocationGranted = result.get(Manifest.permission.ACCESS_COARSE_LOCATION);
                    if (fineLocationGranted != null && fineLocationGranted) {
                        Log.d("msg", "Permiso de ubicación precisa concedido");
                        mostrarUbicacion();
                    } else if (coarseLocationGranted != null && coarseLocationGranted) {
                        Log.d("msg", "Permiso de ubicación aproximada concedido");
                    } else {
                        Log.d("msg", "Ningun permiso concedido");
                    }
                }
        );

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        client = LocationServices.getFusedLocationProviderClient(UbicacionActivity.this);
        mostrarUbicacion();
    }

    public void mostrarUbicacion() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            Task<Location> task = client.getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(final Location location) {
                    if(location != null){
                        mapFragment.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(@NonNull GoogleMap googleMap) {
                                mMap = googleMap;
                                LatLng latLng = new LatLng(-12.068451806069982, -77.078017870113);
                                selectedLat = -12.068451806069982;
                                selectedLong = -77.078017870113;
                                getAddress(selectedLat,selectedLong);
                                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                    @Override
                                    public void onMapClick(@NonNull LatLng latLng) {
                                        checkConnection();
                                        if(networkInfo.isAvailable() && networkInfo.isConnected()){
                                            selectedLat = latLng.latitude;
                                            selectedLong = latLng.longitude;
                                            getAddress(selectedLat,selectedLong);
                                        }else{
                                            Toast.makeText(UbicacionActivity.this,"Corrobora tu conexion",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                    }
                }
            });
        }else{
            requestPermissonForLocation.launch(new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
            });
        }
    }

    private void checkConnection(){
        connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
    }
    private void getAddress(double mLat,double mLong){
        geocoder = new Geocoder(UbicacionActivity.this, Locale.getDefault());
        if(mLat != 0){
            try {
                listAddress = geocoder.getFromLocation(mLat,mLong,1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(listAddress != null){
                String mAddress = listAddress.get(0).getAddressLine(0);
                selectedAddress  = mAddress;
                if(mAddress != null){
                    MarkerOptions markerOptions = new MarkerOptions();
                    LatLng latLng = new LatLng(mLat,mLong);
                    markerOptions.position(latLng).title(selectedAddress);
                    mMap.clear();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,13));
                    mMap.addMarker(markerOptions).showInfoWindow();
                }else{
                    Toast.makeText(UbicacionActivity.this, "Algo salió mal", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(UbicacionActivity.this, "Algo salió mal", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(UbicacionActivity.this, "Lat null", Toast.LENGTH_SHORT).show();
        }
    }
}