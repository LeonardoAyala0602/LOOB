package com.example.loob.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.loob.R;
import com.example.loob.dto.ObjetoDTO;
import com.example.loob.dto.SolicitudDTO;

public class UbicacionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubicacion);
        Intent intent = getIntent();
        SolicitudDTO solicitud = (SolicitudDTO) intent.getSerializableExtra("solicitud");
        String nombre = solicitud.getNombreObjeto();
        TextView textNombre = findViewById(R.id.textNombreUbicacion);
        textNombre.setText(nombre);
        Button btnCancelar = findViewById(R.id.btnCancelarPedido);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(UbicacionActivity.this, ListaSolicitudesActivity.class);
                startActivity(intent);
            }
        });
    }
}