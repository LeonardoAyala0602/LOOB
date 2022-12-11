package com.example.loob.cliente;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loob.R;
import com.example.loob.dto.ObjetoDTO;
import com.example.loob.dto.SolicitudDTO;
import com.example.loob.login.LoginActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class HistorialActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference();
    ArrayList<SolicitudDTO> listaSolicitudes = new ArrayList<>();

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);
        firebaseDatabase= FirebaseDatabase.getInstance();

        drawerLayout = findViewById(R.id.drawer_layout_user);
        navigationView = findViewById(R.id.nav_view_user);

        actionBarDrawerToggle = new ActionBarDrawerToggle(HistorialActivity.this,drawerLayout,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Log.d("mensaje","ENTRA AQUí");
                switch (item.getItemId()){
                    case R.id.btnListarObjetosP:
                        Toast.makeText(HistorialActivity.this, "Listar", Toast.LENGTH_SHORT).show();
                        Intent intent =  new Intent(HistorialActivity.this, ListaClienteObjetosActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.btnSolicitudes:
                        Toast.makeText(HistorialActivity.this, "Solicitudes", Toast.LENGTH_SHORT).show();
                        Intent intent1 =  new Intent(HistorialActivity.this, HistorialActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.btnVerPerfil:
                        Toast.makeText(HistorialActivity.this, "Perfil", Toast.LENGTH_SHORT).show();
                        Intent intent2 =  new Intent(HistorialActivity.this, PerfilActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.btnCerrar:
                        Toast.makeText(HistorialActivity.this, "LogOut", Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                        Intent intent3 =  new Intent(HistorialActivity.this, LoginActivity.class);
                        startActivity(intent3);
                        finish();
                        break;
                }
                return false;
            }
        });
        RecyclerView recyclerView = findViewById(R.id.recyclerPedidos);
        HistorialAdapter adapter = new HistorialAdapter();
        adapter.setContext(HistorialActivity.this);

        DatabaseReference databaseReference = firebaseDatabase.getReference().child("solicitudes");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot children : snapshot.getChildren() ){
                    SolicitudDTO actividad = children.getValue(SolicitudDTO.class);
                    listaSolicitudes.add(actividad);
                    adapter.setListaSolicitudes(listaSolicitudes);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(HistorialActivity.this));

                }
                if(listaSolicitudes.size() == 0){
                    ((TextView) findViewById(R.id.textView18)).setText("No hay solicitudes");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}