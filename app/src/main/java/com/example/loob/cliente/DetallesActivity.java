package com.example.loob.cliente;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loob.R;
import com.example.loob.admin.AgregarObjetoActivity;
import com.example.loob.admin.ListaObjetosActivity;
import com.example.loob.admin.ListaSolicitudesActivity;
import com.example.loob.admin.RechazoActivity;
import com.example.loob.dto.ObjetoDTO;
import com.example.loob.dto.SolicitudDTO;
import com.example.loob.dto.UsuarioDTO;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DetallesActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles);
        firebaseDatabase= FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        Intent intent = getIntent();
        ObjetoDTO objeto = (ObjetoDTO) intent.getSerializableExtra("objeto");
        String nombre = objeto.getNombre();
        TextView textNombre = findViewById(R.id.textNombreObjeto);
        textNombre.setText(nombre);
        EditText textRechazo = findViewById(R.id.editTextDescripcion);
        Button btnEnviar = findViewById(R.id.btnEnviarS);
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String descripcion = textRechazo.getText().toString();
                if (descripcion.trim().isEmpty()) {
                    Toast.makeText(DetallesActivity.this, "Ingrese una descripcion", Toast.LENGTH_SHORT).show();
                }else{
                    SolicitudDTO solicitud = new SolicitudDTO();
                    firebaseDatabase.getReference().child("users").child(firebaseAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            UsuarioDTO usuario =  dataSnapshot.getValue(UsuarioDTO.class);
                            solicitud.setCodigoPUCP(usuario.getCodigoPUCP());
                            solicitud.setCorreoPucp(usuario.getCorreo());
                            solicitud.setDni(usuario.getDNI());
                            solicitud.setDescripcion(descripcion);
                            solicitud.setEstado("pendiente");
                            solicitud.setNombreObjeto(nombre);
                            solicitud.setId(databaseReference.child("solicitudes").push().getKey());
                            databaseReference.child("solicitudes").child(solicitud.getId()).setValue(solicitud);
                            Toast.makeText(DetallesActivity.this, "Solicitud emitida correctamente", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(DetallesActivity.this, ListaClienteObjetosActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                }
            }
        });
        Button btnCancelar = findViewById(R.id.btnCancelarS);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(DetallesActivity.this, ListaClienteObjetosActivity.class);
                startActivity(intent);
            }
        });
    }
}