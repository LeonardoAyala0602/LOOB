package com.example.loob.cliente;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.loob.R;
import com.example.loob.dto.UsuarioDTO;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class EditarPerfilActivity extends AppCompatActivity {

    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    StorageReference storageReference;
    Uri uri;
    private String dni;

    ActivityResultLauncher<Intent> openDocumentLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    ImageView imageView = findViewById(R.id.imageFotoEdit);
                    uri = result.getData().getData();
                    Glide.with(EditarPerfilActivity.this).load(uri)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(imageView);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);
        Button btnImportar = findViewById(R.id.btnImportarFotoEdit);
        DatabaseReference databaseReference  = firebaseDatabase.getReference().child("users").child(firebaseAuth.getCurrentUser().getUid());
        storageReference = firebaseStorage.getReference();
        btnImportar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("image/jpeg");
                openDocumentLauncher.launch(intent);
            }
        });
        Button btnEdit = findViewById(R.id.btnEditarFotoEdit);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(uri != null){
                    storageReference.child("users").child(dni + "/photo.jpg").putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Intent intent =  new Intent(EditarPerfilActivity.this,PerfilActivity.class);
                            startActivity(intent);
                        }
                    });
                }else{
                    Toast.makeText(EditarPerfilActivity.this, "No hay imagen adjunta", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ImageView imageView = findViewById(R.id.imageFotoEdit);
        databaseReference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                UsuarioDTO tiUserDTO = dataSnapshot.getValue(UsuarioDTO.class);
                dni = tiUserDTO.getDNI();
                Glide.with(EditarPerfilActivity.this).load(FirebaseStorage.getInstance().getReference().child("users/"+tiUserDTO.getDNI()+"/photo.jpg"))
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(imageView);

            }
        });

        Button btnCancelar = findViewById(R.id.btnCancelarFotoEdit);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(EditarPerfilActivity.this, PerfilActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}