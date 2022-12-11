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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.loob.R;
import com.example.loob.dto.ObjetoDTO;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class EditarObjetoActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ClipData clipDataAbrirGaleria;
    ArrayList<Uri> clipDataTomarFoto;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference();
    int anioActual;
    int mesActual;
    int diaActual;
    int anioElegido;
    int mesElegido;
    int diaElegido;
    ActivityResultLauncher<Intent> openDocumentLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    ImageView imageView = findViewById(R.id.imageView2);
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
                                Toast.makeText(EditarObjetoActivity.this, "Objeto agregado correctamente", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(EditarObjetoActivity.this,ListaObjetosActivity.class);
                                startActivity(intent);
                                finish();
                            }else{
                                Toast.makeText(EditarObjetoActivity.this, "Deben ser más de 3 imagenes", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(EditarObjetoActivity.this, "Dispositivo agregado correctamente", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(EditarObjetoActivity.this,ListaObjetosActivity.class);
                                startActivity(intent);
                                finish();
                            }else{
                                Toast.makeText(EditarObjetoActivity.this, "Deben ser más de 3 imagenes", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(EditarObjetoActivity.this, "Objeto editado correctamente", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(EditarObjetoActivity.this,ListaObjetosActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }else{
                            Toast.makeText(EditarObjetoActivity.this, "No hay imagen adjunta", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
        Button btnFoto = findViewById(R.id.button2);
        btnFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent1.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                intent1.setType("image/jpeg");
                openDocumentLauncher.launch(intent1);
            }
        });
        Button btnTomarFoto = findViewById(R.id.button3);
        btnTomarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                tomarFoto.launch(intent1);
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