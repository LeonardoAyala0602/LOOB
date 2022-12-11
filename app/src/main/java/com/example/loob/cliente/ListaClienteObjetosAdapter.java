package com.example.loob.cliente;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.loob.R;
import com.example.loob.admin.ListaObjetosAdapter;
import com.example.loob.dto.ObjetoDTO;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ListaClienteObjetosAdapter extends RecyclerView.Adapter<ListaClienteObjetosAdapter.ObjetosViewHolder>{

    private ArrayList<ObjetoDTO> listaObjetos ;
    private Context context;
    private OnItemClickListener detalle;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public ArrayList<ObjetoDTO> getListaObjetos() {
        return listaObjetos;
    }

    public void setListaObjetos(ArrayList<ObjetoDTO> listaObjetos) {
        this.listaObjetos = listaObjetos;
    }

    public OnItemClickListener getDetalle() {
        return detalle;
    }

    public void setDetalle(OnItemClickListener detalle) {
        this.detalle = detalle;
    }

    public interface OnItemClickListener{
        void OnItemClick(int position);
    }

    public class ObjetosViewHolder extends RecyclerView.ViewHolder{
        ObjetoDTO objetoDTO;
        public ObjetosViewHolder(@NonNull View itemView){
            super(itemView);
            Button button = itemView.findViewById(R.id.button9);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    detalle.OnItemClick(getAdapterPosition());
                }
            });
        }
    }

    @NonNull
    @Override
    public ObjetosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(context).inflate(R.layout.listaclienteobjeto_rv,parent,false);
        return new ObjetosViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ObjetosViewHolder holder, int position) {
        ObjetoDTO d= listaObjetos.get(position);
        holder.objetoDTO=d;
        TextView textViewNombre= holder.itemView.findViewById(R.id.textView23);
        textViewNombre.setText(d.getNombre());
        ImageView imageView = holder.itemView.findViewById(R.id.imageView4);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("objetos/"+d.getId()+"/photo.jpg");
        Glide.with(context).load(storageReference).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(imageView);


    }

    @Override
    public int getItemCount() {
        return listaObjetos.size();
    }
}
