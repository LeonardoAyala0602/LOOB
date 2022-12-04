package com.example.loob.admin;

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
import android.widget.Toast;

import com.example.loob.R;
import com.example.loob.dto.SolicitudDTO;
import com.example.loob.login.LoginActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListaSolicitudesActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
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
        setContentView(R.layout.activity_lista_solicitudes);
        firebaseDatabase= FirebaseDatabase.getInstance();

        drawerLayout = findViewById(R.id.drawer_layout_admin);
        navigationView = findViewById(R.id.nav_view_admin);
        actionBarDrawerToggle = new ActionBarDrawerToggle(ListaSolicitudesActivity.this,drawerLayout,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Log.d("mensaje","ENTRA AQUí");
                switch (item.getItemId()){
                    case R.id.btnListarObjetos:
                        Toast.makeText(ListaSolicitudesActivity.this, "Listar", Toast.LENGTH_SHORT).show();
                        Intent intent =  new Intent(ListaSolicitudesActivity.this, ListaObjetosActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.btnSolicitudes:
                        Toast.makeText(ListaSolicitudesActivity.this, "Pedidos", Toast.LENGTH_SHORT).show();
                        Intent intent1 =  new Intent(ListaSolicitudesActivity.this, ListaSolicitudesActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.btnUsuarios:
                        Toast.makeText(ListaSolicitudesActivity.this, "Ver Perfil", Toast.LENGTH_SHORT).show();
                        Intent intent2 =  new Intent(ListaSolicitudesActivity.this, ListaClientesActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.btnLogOut:
                        Toast.makeText(ListaSolicitudesActivity.this, "LogOut", Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                        Intent intent3 =  new Intent(ListaSolicitudesActivity.this, LoginActivity.class);
                        startActivity(intent3);
                        finish();
                        break;
                }
                return false;
            }
        });
        RecyclerView recyclerView = findViewById(R.id.recyclerSolicitudes);
        ListaSolicitudesAdapter adapter = new ListaSolicitudesAdapter();
        adapter.setContext(ListaSolicitudesActivity.this);

        DatabaseReference databaseReference = firebaseDatabase.getReference().child("solicitudes");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot children : snapshot.getChildren() ){
                    SolicitudDTO solicitud = children.getValue(SolicitudDTO.class);
                    solicitud.setId(children.getKey());
                    listaSolicitudes.add(solicitud);
                    adapter.setListaSolicitudes(listaSolicitudes);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(ListaSolicitudesActivity.this));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        adapter.setAprobar(new ListaSolicitudesAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                Intent intent =  new Intent(ListaSolicitudesActivity.this, UbicacionActivity.class);
                intent.putExtra("solicitud", adapter.getListaSolicitudes().get(position));
                startActivity(intent);
            }
        });
        adapter.setRechazar(new ListaSolicitudesAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                Intent intent =  new Intent(ListaSolicitudesActivity.this, RechazoActivity.class);
                intent.putExtra("solicitud", adapter.getListaSolicitudes().get(position));
                startActivity(intent);
            }
        });
    }
}