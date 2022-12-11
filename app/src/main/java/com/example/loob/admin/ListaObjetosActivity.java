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
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.loob.R;
import com.example.loob.dto.ObjetoDTO;
import com.example.loob.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

public class ListaObjetosActivity extends AppCompatActivity{

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    ArrayList<ObjetoDTO> listaObjetos = new ArrayList<>();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference();
    ListaObjetosAdapter adapter;


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
        setContentView(R.layout.activity_lista_objetos);
        firebaseDatabase= FirebaseDatabase.getInstance();

        drawerLayout = findViewById(R.id.drawer_layout_admin);
        navigationView = findViewById(R.id.nav_view_admin);

        actionBarDrawerToggle = new ActionBarDrawerToggle(ListaObjetosActivity.this,drawerLayout,R.string.open,R.string.close);
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
                        Toast.makeText(ListaObjetosActivity.this, "Listar", Toast.LENGTH_SHORT).show();
                        Intent intent =  new Intent(ListaObjetosActivity.this, ListaObjetosActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.btnSolicitudes:
                        Toast.makeText(ListaObjetosActivity.this, "Solicitudes", Toast.LENGTH_SHORT).show();
                        Intent intent1 =  new Intent(ListaObjetosActivity.this, ListaSolicitudesActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.btnLogOut:
                        Toast.makeText(ListaObjetosActivity.this, "LogOut", Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                        Intent intent3 =  new Intent(ListaObjetosActivity.this, LoginActivity.class);
                        startActivity(intent3);
                        finish();
                        break;
                }
                return false;
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerObjetos);
        adapter = new ListaObjetosAdapter();
        adapter.setContext(ListaObjetosActivity.this);

        DatabaseReference databaseReference = firebaseDatabase.getReference().child("objetos");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot children : snapshot.getChildren() ){
                    ObjetoDTO actividad = children.getValue(ObjetoDTO.class);
                    actividad.setId(children.getKey());
                    Log.d("msg",children.getKey());
                    listaObjetos.add(actividad);
                    adapter.setListaObjetos(listaObjetos);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(ListaObjetosActivity.this));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        adapter.setEditar(new ListaObjetosAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                Intent intent = new Intent(ListaObjetosActivity.this,EditarObjetoActivity.class);
                intent.putExtra("objeto",adapter.getListaObjetos().get(position));
                startActivity(intent);
            }
        });
        adapter.setBorrar(new ListaObjetosAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                databaseReference.child(adapter.getListaObjetos().get(position).getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(ListaObjetosActivity.this,"Eliminado correctamente",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(ListaObjetosActivity.this,"Error al eliminar",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                adapter.getListaObjetos().remove(position);
                adapter.notifyItemRemoved(position);
            }
        });

        FloatingActionButton floatingActionButton = findViewById(R.id.floatingObjeto);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListaObjetosActivity.this, AgregarObjetoActivity.class);
                startActivity(intent);
            }
        });
    }

}