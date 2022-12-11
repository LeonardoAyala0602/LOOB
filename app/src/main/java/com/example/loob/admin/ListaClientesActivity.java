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
import com.example.loob.dto.ObjetoDTO;
import com.example.loob.dto.UsuarioDTO;
import com.example.loob.login.LoginActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListaClientesActivity extends AppCompatActivity {

    ArrayList<UsuarioDTO> listaUsuarios = new ArrayList<>();
    ListaClientesAdapter adapter = new ListaClientesAdapter();
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseDatabase firebaseDatabase =  FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();

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
        setContentView(R.layout.activity_lista_clientes);
        firebaseAuth = FirebaseAuth.getInstance();
        //DRAWER
        drawerLayout = findViewById(R.id.drawer_layout_admin);
        navigationView = findViewById(R.id.nav_view_admin);
        actionBarDrawerToggle = new ActionBarDrawerToggle(ListaClientesActivity.this,drawerLayout,R.string.open,R.string.close);
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
                        Toast.makeText(ListaClientesActivity.this, "Listar", Toast.LENGTH_SHORT).show();
                        Intent intent =  new Intent(ListaClientesActivity.this, ListaObjetosActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.btnSolicitudes:
                        Toast.makeText(ListaClientesActivity.this, "Solicitudes", Toast.LENGTH_SHORT).show();
                        Intent intent1 =  new Intent(ListaClientesActivity.this, ListaSolicitudesActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.btnLogOut:
                        Toast.makeText(ListaClientesActivity.this, "LogOut", Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                        Intent intent3 =  new Intent(ListaClientesActivity.this, LoginActivity.class);
                        startActivity(intent3);
                        finish();
                        break;
                }
                return false;
            }
        });
        //VISTA
        databaseReference.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot children : snapshot.getChildren()){
                    if(children.child("rol").getValue(String.class).equals("ROL_USER")){
                        UsuarioDTO tiUserDTO = children.getValue(UsuarioDTO.class);
                        listaUsuarios.add(tiUserDTO);
                    }
                }
                adapter.setListaUsuarios(listaUsuarios);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        adapter.setContext(ListaClientesActivity.this);
        RecyclerView recyclerView = findViewById(R.id.recycleViewUsuarios);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(ListaClientesActivity.this));
    }
}