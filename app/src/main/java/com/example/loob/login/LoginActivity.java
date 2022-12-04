package com.example.loob.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loob.R;
import com.example.loob.admin.ListaObjetosActivity;
import com.example.loob.cliente.ListaClienteObjetosActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() != null){
            databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    Intent intent = null;
                    if(dataSnapshot.child("rol").getValue(String.class).equals("ROL_USER")){
                        intent = new Intent(LoginActivity.this, ListaClienteObjetosActivity.class);
                        Toast.makeText(LoginActivity.this,"USUARIO",Toast.LENGTH_SHORT).show();
                    }
                    if(dataSnapshot.child("rol").getValue(String.class).equals("ROL_ADMIN")){
                        intent = new Intent(LoginActivity.this, ListaObjetosActivity.class);
                        Toast.makeText(LoginActivity.this,"ADMIN",Toast.LENGTH_SHORT).show();
                    }
                    if(intent != null){
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(LoginActivity.this,"ERROR AL REDIRECCIONAR POR USUARIO",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            ((Button) findViewById(R.id.btnRegister)).setOnClickListener(view -> {
                Intent intent = new Intent(LoginActivity.this,RegistroActivity.class);
                startActivity(intent);
            });
            ((Button) findViewById(R.id.btnLogin)).setOnClickListener(view -> {
                EditText email = findViewById(R.id.editEmailLogin);
                EditText contrasena = findViewById(R.id.editPasswordLogin);
                //
                boolean bool=true;
                if(email.getText().toString().isEmpty()){
                    email.setError("Ingrese un email");
                    bool =false;
                }
                if(contrasena.getText().toString().isEmpty()){
                    contrasena.setError("Ingrese una contrasena");
                    bool =false;
                }
                if(bool){
                    firebaseAuth.signInWithEmailAndPassword(email.getText().toString(),contrasena.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                if(firebaseAuth.getCurrentUser().isEmailVerified()){
                                    databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                        @Override
                                        public void onSuccess(DataSnapshot dataSnapshot) {
                                            Intent intent = null;
                                            if(dataSnapshot.child("rol").getValue(String.class).equals("ROL_USER")){
                                                intent = new Intent(LoginActivity.this, ListaClienteObjetosActivity.class);
                                                Toast.makeText(LoginActivity.this,"USUARIO",Toast.LENGTH_SHORT).show();
                                            }
                                            if(dataSnapshot.child("rol").getValue(String.class).equals("ROL_ADMIN")){
                                                intent = new Intent(LoginActivity.this,ListaObjetosActivity.class);
                                                Toast.makeText(LoginActivity.this,"ADMIN",Toast.LENGTH_SHORT).show();
                                            }
                                            if(intent != null){
                                                startActivity(intent);
                                                finish();
                                            }else{
                                                Toast.makeText(LoginActivity.this,"Error al redireccionar por usuario",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                }else{
                                    Toast.makeText(LoginActivity.this, "Confirme su correo electronico", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(LoginActivity.this, "Verifique sus credenciales", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        }
        ((TextView) findViewById(R.id.textForgot)).setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this,OlvidarConstrasenaActivity.class);
            startActivity(intent);
        });

    }
}