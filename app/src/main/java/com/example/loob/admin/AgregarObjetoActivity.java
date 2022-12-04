package com.example.loob.admin;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.loob.R;
import com.example.loob.dto.ObjetoDTO;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;

public class AgregarObjetoActivity extends AppCompatActivity {

    String otro = "";
    int anioActual;
    int mesActual;
    int diaActual;
    int anioElegido;
    int mesElegido;
    int diaElegido;
    FirebaseDatabase firebaseDatabase;
    Uri uri;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser user = firebaseAuth.getCurrentUser();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference();
    ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK){
                    uri = result.getData().getData();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_objeto);
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();

        EditText textNombre = findViewById(R.id.textNombreA);
        EditText textHidden = findViewById(R.id.editTextHidden);
        String[] listaTipo = {"Dispositivo","Identificacion","Util escolar","Efectivo","Prenda", "Otro"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(AgregarObjetoActivity.this, android.R.layout.simple_spinner_dropdown_item,listaTipo);
        Spinner spinnerTipo = findViewById(R.id.spinnerTipo);
        spinnerTipo.setAdapter(adapter1);
        spinnerTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String tipo = spinnerTipo.getSelectedItem().toString();
                if(tipo.equals("Otro")){
                    textHidden.setEnabled(true);
                }else{
                    textHidden.setEnabled(false);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        EditText textMarca = findViewById(R.id.textMarcaA);
        EditText textCaracteristicas = findViewById(R.id.textCaracteristicasA);
        EditText textFecha = findViewById(R.id.textFechaA);
        Button btnCalendar = findViewById(R.id.btnCalendar);
        btnCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int anio = calendar.get(Calendar.YEAR);
                int mes = calendar.get(Calendar.MONTH);
                int dia = calendar.get(Calendar.DAY_OF_MONTH);
                diaActual = dia;
                mesActual = mes;
                anioActual = anio;
                DatePickerDialog datePickerDialog = new DatePickerDialog(AgregarObjetoActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        String fecha = i2 + "/" + (i1+1) + "/" + i;
                        anioElegido = i;
                        mesElegido = i1;
                        diaElegido = i2;
                        textFecha.setText(fecha);
                    }
                },anio,mes,dia);
                datePickerDialog.show();
            }
        });
        Button btnAgregar = findViewById(R.id.btnAgregarDispositivo);
        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nombre = textNombre.getText().toString();
                String tipo = spinnerTipo.getSelectedItem().toString();
                String marca = textMarca.getText().toString();
                String caracteristicas = textCaracteristicas.getText().toString();
                String fecha = textFecha.getText().toString();
                if (nombre.trim().isEmpty()) {
                    Toast.makeText(AgregarObjetoActivity.this, "Nombre no puede ser vacio", Toast.LENGTH_SHORT).show();
                } else if (marca.trim().isEmpty()) {
                    Toast.makeText(AgregarObjetoActivity.this, "Marca no puede ser vacio", Toast.LENGTH_SHORT).show();
                } else if (caracteristicas.trim().isEmpty()) {
                    Toast.makeText(AgregarObjetoActivity.this, "Caracteristicas no puede ser vacio", Toast.LENGTH_SHORT).show();
                } else if (fecha.trim().isEmpty()) {
                    Toast.makeText(AgregarObjetoActivity.this, "Fecha no puede ser vacio", Toast.LENGTH_SHORT).show();
                } else if (anioActual<anioElegido) {
                    Toast.makeText(AgregarObjetoActivity.this, "Fecha no puede ser despues de la actual", Toast.LENGTH_SHORT).show();
                } else if ((anioActual==anioElegido)&&(mesActual<mesElegido)){
                    Toast.makeText(AgregarObjetoActivity.this, "Fecha no puede ser despues de la actual", Toast.LENGTH_SHORT).show();
                } else if ((anioActual==anioElegido)&&(mesActual==mesElegido)&&(diaActual<diaElegido)) {
                    Toast.makeText(AgregarObjetoActivity.this, "Fecha no puede ser despues de la actual", Toast.LENGTH_SHORT).show();
                }else if (tipo.equals("Otro")){
                    otro = textHidden.getText().toString();
                    if(otro.trim().isEmpty()){
                        textHidden.setError("No puede ser vacío");
                        textHidden.requestFocus();
                    }else{
                        tipo = otro;
                        ObjetoDTO objeto = new ObjetoDTO();
                        objeto.setNombre(nombre);
                        objeto.setTipo(tipo);
                        objeto.setMarca(marca);
                        objeto.setCaracteristicas(caracteristicas);
                        objeto.setFecha(fecha);
                        databaseReference.child("objetos").push().setValue(objeto);
                        Toast.makeText(AgregarObjetoActivity.this, "Objeto agregado correctamente", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AgregarObjetoActivity.this,ListaObjetosActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }else{
                    ObjetoDTO objeto = new ObjetoDTO();
                    objeto.setNombre(nombre);
                    objeto.setTipo(tipo);
                    objeto.setMarca(marca);
                    objeto.setCaracteristicas(caracteristicas);
                    objeto.setFecha(fecha);
                    databaseReference.child("objetos").push().setValue(objeto);
                    Toast.makeText(AgregarObjetoActivity.this, "Objeto agregado correctamente", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AgregarObjetoActivity.this,ListaObjetosActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
        Button btnCancelar = findViewById(R.id.btnCancelarA);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AgregarObjetoActivity.this,ListaObjetosActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}