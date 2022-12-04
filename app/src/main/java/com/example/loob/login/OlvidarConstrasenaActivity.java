package com.example.loob.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.loob.R;
import com.example.loob.dto.UsuarioDTO;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OlvidarConstrasenaActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_olvidar_constrasena);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        ((Button) findViewById(R.id.btnReturnLoginForgot)).setOnClickListener(view -> {
            Intent intent =  new Intent(OlvidarConstrasenaActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        });
        ((Button) findViewById(R.id.btnEnviarCorreo)).setOnClickListener(view -> {
            EditText codigoPUCP = findViewById(R.id.editCodigoPUCPRecuperar);
            boolean bool = true;
            if(codigoPUCP.getText().toString().isEmpty()){
                codigoPUCP.setError("Ingrese un codigo");
                bool = false;
            }
            if(bool) {
                databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            UsuarioDTO usuarioDTO = dataSnapshot.getValue(UsuarioDTO.class);
                            if (usuarioDTO.getRol().equals("ROL_USER")) {
                                if (usuarioDTO.getCodigoPUCP().equals(codigoPUCP.getText().toString())) {
                                    firebaseAuth.sendPasswordResetEmail(usuarioDTO.getCorreo()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(OlvidarConstrasenaActivity.this, "Se le ha enviado un correo de recuperación", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(OlvidarConstrasenaActivity.this,LoginActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }else {
                                                Toast.makeText(OlvidarConstrasenaActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }else{
                                    Toast.makeText(OlvidarConstrasenaActivity.this,"HOLA2",Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(OlvidarConstrasenaActivity.this, "HOLA", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(OlvidarConstrasenaActivity.this, "Error al encontrar usuario", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}