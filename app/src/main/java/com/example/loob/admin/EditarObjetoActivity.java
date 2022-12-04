package com.example.loob.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.loob.R;
import com.example.loob.dto.ObjetoDTO;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class EditarObjetoActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    int anioActual;
    int mesActual;
    int diaActual;
    int anioElegido;
    int mesElegido;
    int diaElegido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_objeto);
        Intent intent = getIntent();
        ObjetoDTO objeto = (ObjetoDTO) intent.getSerializableExtra("objeto");
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        EditText textMarca = findViewById(R.id.textMarcaEdit);
        textMarca.setText(objeto.getMarca());
        EditText textCaracteristicas = findViewById(R.id.textCaracteristicasEdit);
        textCaracteristicas.setText(objeto.getCaracteristicas());
        EditText textFecha = findViewById(R.id.textFechaE);
        textFecha.setText(objeto.getFecha());
        textFecha.setEnabled(false);

        Button btnCalendar = findViewById(R.id.btnCalendarE);
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
                DatePickerDialog datePickerDialog = new DatePickerDialog(EditarObjetoActivity.this, new DatePickerDialog.OnDateSetListener() {
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

        Button btnEditar = findViewById(R.id.btnEdit);
        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String marca = textMarca.getText().toString();
                String caracteristicas = textCaracteristicas.getText().toString();
                String fecha = textFecha.getText().toString();
                if (marca.trim().isEmpty()) {
                    Toast.makeText(EditarObjetoActivity.this, "Marca no puede ser vacio", Toast.LENGTH_SHORT).show();
                } else if (caracteristicas.trim().isEmpty()) {
                    Toast.makeText(EditarObjetoActivity.this, "Caracteristicas no puede ser vacio", Toast.LENGTH_SHORT).show();
                } else if (fecha.trim().isEmpty()) {
                    Toast.makeText(EditarObjetoActivity.this, "Incluye no puede ser vacio", Toast.LENGTH_SHORT).show();
                } else if (anioActual<anioElegido) {
                    Toast.makeText(EditarObjetoActivity.this, "Fecha no puede ser despues de la actual", Toast.LENGTH_SHORT).show();
                } else if ((anioActual==anioElegido)&&(mesActual<mesElegido)){
                    Toast.makeText(EditarObjetoActivity.this, "Fecha no puede ser despues de la actual", Toast.LENGTH_SHORT).show();
                } else if ((anioActual==anioElegido)&&(mesActual==mesElegido)&&(diaActual<diaElegido)) {
                    Toast.makeText(EditarObjetoActivity.this, "Fecha no puede ser despues de la actual", Toast.LENGTH_SHORT).show();
                } else {
                    objeto.setMarca(marca);
                    objeto.setCaracteristicas(caracteristicas);
                    objeto.setFecha(fecha);
                    databaseReference.child("objetos").child(objeto.getId()).setValue(objeto);
                    Toast.makeText(EditarObjetoActivity.this, "Objeto editado correctamente", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EditarObjetoActivity.this,ListaObjetosActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        Button btnCancelar = findViewById(R.id.btnCancelarEdit);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditarObjetoActivity.this,ListaObjetosActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}