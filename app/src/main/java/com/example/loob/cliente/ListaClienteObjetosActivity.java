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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loob.R;
import com.example.loob.admin.ListaObjetosActivity;
import com.example.loob.admin.ListaSolicitudesActivity;
import com.example.loob.dto.ObjetoDTO;
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

public class ListaClienteObjetosActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference();
    ArrayList<ObjetoDTO> listaObjeto = new ArrayList<>();

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
        setContentView(R.layout.activity_lista_cliente_objetos);

        firebaseDatabase= FirebaseDatabase.getInstance();

        drawerLayout = findViewById(R.id.drawer_layout_user);
        navigationView = findViewById(R.id.nav_view_user);

        actionBarDrawerToggle = new ActionBarDrawerToggle(ListaClienteObjetosActivity.this,drawerLayout,R.string.open,R.string.close);
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
                        Toast.makeText(ListaClienteObjetosActivity.this, "Listar", Toast.LENGTH_SHORT).show();
                        Intent intent =  new Intent(ListaClienteObjetosActivity.this, ListaClienteObjetosActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.btnSolicitudes:
                        Toast.makeText(ListaClienteObjetosActivity.this, "Solicitudes", Toast.LENGTH_SHORT).show();
                        Intent intent1 =  new Intent(ListaClienteObjetosActivity.this, HistorialActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.btnVerPerfil:
                        Toast.makeText(ListaClienteObjetosActivity.this, "Perfil", Toast.LENGTH_SHORT).show();
                        Intent intent2 =  new Intent(ListaClienteObjetosActivity.this, PerfilActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.btnCerrar:
                        Toast.makeText(ListaClienteObjetosActivity.this, "LogOut", Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                        Intent intent3 =  new Intent(ListaClienteObjetosActivity.this, LoginActivity.class);
                        startActivity(intent3);
                        finish();
                        break;
                }
                return false;
            }
        });

        RecyclerView recyclerView = findViewById(R.id.clienteRecycleDisp);
        ListaClienteObjetosAdapter adapter = new ListaClienteObjetosAdapter();
        adapter.setContext(ListaClienteObjetosActivity.this);

        DatabaseReference databaseReference = firebaseDatabase.getReference().child("objetos");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot children : snapshot.getChildren() ){
                    ObjetoDTO actividad = children.getValue(ObjetoDTO.class);
                    listaObjeto.add(actividad);
                    adapter.setListaObjetos(listaObjeto);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(ListaClienteObjetosActivity.this));

                }
                if(listaObjeto.size() == 0){
                    ((TextView) findViewById(R.id.textView18)).setText("No hay objetos");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        adapter.setDetalle(new ListaClienteObjetosAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                Intent intent = new Intent(ListaClienteObjetosActivity.this,DetallesActivity.class);
                intent.putExtra("objeto",adapter.getListaObjetos().get(position));
                startActivity(intent);
            }
        });


    }
}