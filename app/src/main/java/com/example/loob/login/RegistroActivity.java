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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistroActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        //Confirmamos que están los datos llenos
        EditText codigoPUCP = findViewById(R.id.editCodigoPUCPRegister);
        EditText dni = findViewById(R.id.editDNI);
        EditText email = findViewById(R.id.editEmail);
        EditText ocupacion = findViewById(R.id.editOcupacion);
        EditText password = findViewById(R.id.editPassword);
        EditText confirm_password = findViewById(R.id.editConfirmPassword);

        //Boton para regresar a Logueo
        ((Button) findViewById(R.id.btnReturnLoginForgot)).setOnClickListener(view -> {
            Intent intent =  new Intent(RegistroActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        });

        //Inicializamos un booleano
        ((Button) findViewById(R.id.btnRegistrarse)).setOnClickListener(view -> {
            boolean bool = true;
            if(codigoPUCP.getText().toString().isEmpty()){
                codigoPUCP.setError("Ingrese su codigo PUCP");
                bool = false;
            }
            if(dni.getText().toString().isEmpty()){
                dni.setError("Ingrese su codigo DNI");
                bool = false;
            }
            if(email.getText().toString().isEmpty()){
                email.setError("Ingrese su email");
                bool = false;
            }else{
                if(email.getText().toString().contains("@pucp.edu.pe") || email.getText().toString().contains("@pucp.pe")){
                    Toast.makeText(RegistroActivity.this, "Bienvenido Usuario PUCP", Toast.LENGTH_SHORT).show();
                }else{
                    email.setError("Aplicacion solo valida para usuarios del dominio PUCP");
                    bool = false;
                }
            }
            if(ocupacion.getText().toString().isEmpty()){
                ocupacion.setError("Ingrese su ocupacion");
                bool = false;
            }
            if(password.getText().toString().isEmpty()){
                password.setError("Ingrese su contraseña");
                bool = false;
            }
            if(confirm_password.getText().toString().isEmpty()){
                confirm_password.setError("Ingrese nuevamente su contraseña");
                bool = false;
            }
            if(!password.getText().toString().equals(confirm_password.getText().toString())){
                confirm_password.setError("Las contraseñas no coinciden");
                bool = false;
            }
            if(bool){
                firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            UsuarioDTO usuarioDTO = new UsuarioDTO(codigoPUCP.getText().toString(),email.getText().toString(),dni.getText().toString(),ocupacion.getText().toString(),"ROL_USER");
                            databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).setValue(usuarioDTO).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(RegistroActivity.this,"Cuenta creada exitosamente, inicie sesión",Toast.LENGTH_SHORT).show();
                                        firebaseAuth.getCurrentUser().sendEmailVerification();
                                        Intent intent = new Intent(RegistroActivity.this,LoginActivity.class);
                                        firebaseAuth.signOut();
                                        startActivity(intent);
                                        finish();
                                    }else{
                                        Toast.makeText(RegistroActivity.this,"Error al guardar datos de usuario",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }else{
                            Toast.makeText(RegistroActivity.this,"Error al crear Usuario",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }else{
                Toast.makeText(RegistroActivity.this, "Verifique sus datos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}