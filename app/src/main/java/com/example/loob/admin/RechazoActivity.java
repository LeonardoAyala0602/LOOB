package com.example.loob.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.loob.R;
import com.example.loob.dto.ObjetoDTO;
import com.example.loob.dto.SolicitudDTO;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RechazoActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rechazo);
        firebaseDatabase= FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference().child("solicitudes");

        Intent intent = getIntent();
        SolicitudDTO solicitud = (SolicitudDTO) intent.getSerializableExtra("solicitud");
        String nombre = solicitud.getNombreObjeto();
        TextView textNombre = findViewById(R.id.textNombreRechazo);
        textNombre.setText(nombre);
        EditText textRechazo = findViewById(R.id.editTextRechazo);
        Button btnEnviar = findViewById(R.id.btnEnviarRechazo);
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] {solicitud.getCorreoPucp()});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Rechazo de solicitud");
                intent.putExtra(Intent.EXTRA_TEXT, textRechazo.getText().toString());
                startActivity(intent);
                finish();
                solicitud.setEstado("rechazado");
                databaseReference.child(solicitud.getId()).setValue(solicitud);
            }
        });
        Button btnCancelar = findViewById(R.id.btnCancelarRechazo);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(RechazoActivity.this, ListaSolicitudesActivity.class);
                startActivity(intent);
            }
        });
    }
}