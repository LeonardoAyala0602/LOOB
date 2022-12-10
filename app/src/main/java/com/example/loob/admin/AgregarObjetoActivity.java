package com.example.loob.admin;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.loob.R;
import com.example.loob.dto.ObjetoDTO;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
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
    ClipData clipDataAbrirGaleria;
    ArrayList<Uri> clipDataTomarFoto;
    Uri uri;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser user = firebaseAuth.getCurrentUser();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference();
    ActivityResultLauncher<Intent> openDocumentLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    ImageView imageView = findViewById(R.id.imageDispositivoA);
                    Intent intent = result.getData();
                    if(intent.getClipData() != null){
                        if(clipDataAbrirGaleria != null){
                            int i=intent.getClipData().getItemCount();
                            for(int j=0;j<i;j++){
                                clipDataAbrirGaleria.addItem(intent.getClipData().getItemAt(j));
                            }
                        }else{
                            clipDataAbrirGaleria = intent.getClipData();
                        }
                    }else{
                        ClipData.Item item =  new ClipData.Item(intent.getData());
                        if(clipDataAbrirGaleria != null){
                            clipDataAbrirGaleria.addItem(item);
                        }else{
                            ClipDescription clipDescription = new ClipDescription(null, new String[]{"image/jpeg"});
                            clipDataAbrirGaleria =  new ClipData(clipDescription,item);
                        }
                    }
                }
            }
    );
    ActivityResultLauncher<Intent> tomarFoto = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    ImageView imageView = findViewById(R.id.imageDispositivoA);
                    Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,bytes);
                    String path =  MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(),bitmap,"val",null);
                    Uri uri = Uri.parse(path);
                    if(clipDataTomarFoto != null){
                        clipDataTomarFoto.add(uri);
                    }else{
                        clipDataTomarFoto =  new ArrayList<>();
                        clipDataTomarFoto.add(uri);
                    }
                }
            }
    );

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
                        objeto.setId(databaseReference.child("objetos").push().getKey());
                        if(clipDataAbrirGaleria != null){
                            if(clipDataTomarFoto!=null){
                                int k = clipDataAbrirGaleria.getItemCount();
                                int x = clipDataTomarFoto.size();
                                if(k + x>=3){
                                    for(int j = 0; j<k;j++){
                                        Uri uri = clipDataAbrirGaleria.getItemAt(j).getUri();
                                        if(j==0){
                                            storageReference.child("objetos").child(objeto.getId() + "/photo.jpg").putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                }
                                            });
                                        }else{
                                            storageReference.child("objetos").child(objeto.getId() + "/photo"+j+".jpg").putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                }
                                            });
                                        }
                                    }
                                    for(int j = 0; j<x;j++){
                                        Uri uri = clipDataTomarFoto.get(j);
                                        if(j==0){
                                            storageReference.child("objetos").child(objeto.getId() + "/photo"+k+".jpg").putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                }
                                            });
                                        }else{
                                            storageReference.child("objetos").child(objeto.getId() + "/photo"+(k+j)+".jpg").putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                }
                                            });
                                        }
                                    }
                                    databaseReference.child("objetos").child(objeto.getId()).setValue(objeto);
                                    Toast.makeText(AgregarObjetoActivity.this, "Objeto agregado correctamente", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(AgregarObjetoActivity.this,ListaObjetosActivity.class);
                                    startActivity(intent);
                                    finish();
                                }else{
                                    Toast.makeText(AgregarObjetoActivity.this, "Deben ser más de 3 imagenes", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                int i = clipDataAbrirGaleria.getItemCount();
                                Log.d("NUMERO",String.valueOf(i));
                                if(i>=3){
                                    for(int j = 0; j<i;j++){
                                        Uri uri = clipDataAbrirGaleria.getItemAt(j).getUri();
                                        if(j==0){
                                            storageReference.child("objetos").child(objeto.getId() + "/photo.jpg").putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                }
                                            });
                                        }else{
                                            storageReference.child("objetos").child(objeto.getId() + "/photo"+j+".jpg").putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                }
                                            });
                                        }
                                    }
                                    databaseReference.child("objetos").child(objeto.getId()).setValue(objeto);
                                    Toast.makeText(AgregarObjetoActivity.this, "Dispositivo agregado correctamente", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(AgregarObjetoActivity.this,ListaObjetosActivity.class);
                                    startActivity(intent);
                                    finish();
                                }else{
                                    Toast.makeText(AgregarObjetoActivity.this, "Deben ser más de 3 imagenes", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }else{
                            if(clipDataTomarFoto != null){
                                int x = clipDataTomarFoto.size();
                                if(x>=3){
                                    for(int j = 0; j<x;j++){
                                        Uri uri = clipDataTomarFoto.get(j);
                                        if(j==0){
                                            storageReference.child("objetos").child(objeto.getId() + "/photo"+".jpg").putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                }
                                            });
                                        }else{
                                            storageReference.child("objetos").child(objeto.getId() + "/photo"+j+".jpg").putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                }
                                            });
                                        }
                                    }
                                    databaseReference.child("objetos").child(objeto.getId()).setValue(objeto);
                                    Toast.makeText(AgregarObjetoActivity.this, "Objeto agregado correctamente", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(AgregarObjetoActivity.this,ListaObjetosActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }else{
                                Toast.makeText(AgregarObjetoActivity.this, "No hay imagen adjunta", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }else{
                    ObjetoDTO objeto = new ObjetoDTO();
                    objeto.setNombre(nombre);
                    objeto.setTipo(tipo);
                    objeto.setMarca(marca);
                    objeto.setCaracteristicas(caracteristicas);
                    objeto.setFecha(fecha);
                    objeto.setId(databaseReference.child("objetos").push().getKey());
                    if(clipDataAbrirGaleria != null){
                        if(clipDataTomarFoto!=null){
                            int k = clipDataAbrirGaleria.getItemCount();
                            int x = clipDataTomarFoto.size();
                            if(k + x>=3){
                                for(int j = 0; j<k;j++){
                                    Uri uri = clipDataAbrirGaleria.getItemAt(j).getUri();
                                    if(j==0){
                                        storageReference.child("objetos").child(objeto.getId() + "/photo.jpg").putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            }
                                        });
                                    }else{
                                        storageReference.child("objetos").child(objeto.getId() + "/photo"+j+".jpg").putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            }
                                        });
                                    }
                                }
                                for(int j = 0; j<x;j++){
                                    Uri uri = clipDataTomarFoto.get(j);
                                    if(j==0){
                                        storageReference.child("objetos").child(objeto.getId() + "/photo"+k+".jpg").putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            }
                                        });
                                    }else{
                                        storageReference.child("objetos").child(objeto.getId() + "/photo"+(k+j)+".jpg").putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            }
                                        });
                                    }
                                }
                                databaseReference.child("objetos").child(objeto.getId()).setValue(objeto);
                                Toast.makeText(AgregarObjetoActivity.this, "Objeto agregado correctamente", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(AgregarObjetoActivity.this,ListaObjetosActivity.class);
                                startActivity(intent);
                                finish();
                            }else{
                                Toast.makeText(AgregarObjetoActivity.this, "Deben ser más de 3 imagenes", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            int i = clipDataAbrirGaleria.getItemCount();
                            Log.d("NUMERO",String.valueOf(i));
                            if(i>=3){
                                for(int j = 0; j<i;j++){
                                    Uri uri = clipDataAbrirGaleria.getItemAt(j).getUri();
                                    if(j==0){
                                        storageReference.child("objetos").child(objeto.getId() + "/photo.jpg").putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            }
                                        });
                                    }else{
                                        storageReference.child("objetos").child(objeto.getId() + "/photo"+j+".jpg").putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            }
                                        });
                                    }
                                }
                                databaseReference.child("objetos").child(objeto.getId()).setValue(objeto);
                                Toast.makeText(AgregarObjetoActivity.this, "Dispositivo agregado correctamente", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(AgregarObjetoActivity.this,ListaObjetosActivity.class);
                                startActivity(intent);
                                finish();
                            }else{
                                Toast.makeText(AgregarObjetoActivity.this, "Deben ser más de 3 imagenes", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }else{
                        if(clipDataTomarFoto != null){
                            int x = clipDataTomarFoto.size();
                            if(x>=3){
                                for(int j = 0; j<x;j++){
                                    Uri uri = clipDataTomarFoto.get(j);
                                    if(j==0){
                                        storageReference.child("objetos").child(objeto.getId() + "/photo"+".jpg").putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            }
                                        });
                                    }else{
                                        storageReference.child("objetos").child(objeto.getId() + "/photo"+j+".jpg").putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            }
                                        });
                                    }
                                }
                                databaseReference.child("objetos").child(objeto.getId()).setValue(objeto);
                                Toast.makeText(AgregarObjetoActivity.this, "Objeto agregado correctamente", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(AgregarObjetoActivity.this,ListaObjetosActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }else{
                            Toast.makeText(AgregarObjetoActivity.this, "No hay imagen adjunta", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
        Button btnImportar = findViewById(R.id.btnImportarDispositivo);
        btnImportar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                intent.setType("image/jpeg");
                openDocumentLauncher.launch(intent);
            }
        });
        Button btnTomarFoto = findViewById(R.id.btnTomarFoto);
        btnTomarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                tomarFoto.launch(intent);
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