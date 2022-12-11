package com.example.loob.cliente;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.loob.R;
import com.example.loob.dto.UsuarioDTO;
import com.example.loob.login.LoginActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

public class PerfilActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ImageView imageView = findViewById(R.id.imageFoto);
        DatabaseReference databaseReference  = firebaseDatabase.getReference().child("users").child(firebaseAuth.getCurrentUser().getUid());
        databaseReference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                UsuarioDTO tiUserDTO = dataSnapshot.getValue(UsuarioDTO.class);
                Glide.with(PerfilActivity.this).load(FirebaseStorage.getInstance().getReference().child("users/"+tiUserDTO.getDNI()+"/photo.jpg"))
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(imageView);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        drawerLayout = findViewById(R.id.drawer_layout_user);
        navigationView = findViewById(R.id.nav_view_user);
        actionBarDrawerToggle = new ActionBarDrawerToggle(PerfilActivity.this,drawerLayout,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView nombret = findViewById(R.id.textNombreU);
        TextView dni = findViewById(R.id.textDniU);
        FirebaseDatabase.getInstance().getReference().child("users").child(firebaseAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                UsuarioDTO tiUserDTO =  dataSnapshot.getValue(UsuarioDTO.class);
                nombret.setText(tiUserDTO.getCodigoPUCP());
                dni.setText(tiUserDTO.getCorreo());
            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Log.d("mensaje","ENTRA AQUí");
                switch (item.getItemId()){
                    case R.id.btnListarObjetosP:
                        Toast.makeText(PerfilActivity.this, "Listar", Toast.LENGTH_SHORT).show();
                        Intent intent =  new Intent(PerfilActivity.this, ListaClienteObjetosActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.btnSolicitudes:
                        Toast.makeText(PerfilActivity.this, "Solicitudes", Toast.LENGTH_SHORT).show();
                        Intent intent1 =  new Intent(PerfilActivity.this, HistorialActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.btnVerPerfil:
                        Toast.makeText(PerfilActivity.this, "Perfil", Toast.LENGTH_SHORT).show();
                        Intent intent2 =  new Intent(PerfilActivity.this, PerfilActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.btnCerrar:
                        Toast.makeText(PerfilActivity.this, "LogOut", Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                        Intent intent3 =  new Intent(PerfilActivity.this, LoginActivity.class);
                        startActivity(intent3);
                        finish();
                        break;
                }
                return false;
            }
        });
        Button btnEdit = findViewById(R.id.btnEditFoto);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(PerfilActivity.this, EditarPerfilActivity.class);
                startActivity(intent);
            }
        });
    }
}