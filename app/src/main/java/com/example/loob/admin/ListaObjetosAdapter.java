package com.example.loob.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.loob.R;
import com.example.loob.dto.ObjetoDTO;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ListaObjetosAdapter extends RecyclerView.Adapter<ListaObjetosAdapter.ObjetosViewHolder>{

    private ArrayList<ObjetoDTO> listaObjetos;
    private Context context;
    private OnItemClickListener editar;
    private OnItemClickListener borrar;

    public ArrayList<ObjetoDTO> getListaObjetos() {
        return listaObjetos;
    }

    public void setListaObjetos(ArrayList<ObjetoDTO> listaObjetos) {
        this.listaObjetos = listaObjetos;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public OnItemClickListener getEditar() {
        return editar;
    }

    public void setEditar(OnItemClickListener editar) {
        this.editar = editar;
    }

    public OnItemClickListener getBorrar() {
        return borrar;
    }

    public void setBorrar(OnItemClickListener borrar) {
        this.borrar = borrar;
    }

    public interface OnItemClickListener{
        void OnItemClick(int position);
    }

    public class ObjetosViewHolder extends RecyclerView.ViewHolder{
        ObjetoDTO objeto;
        public ObjetosViewHolder(@NonNull View itemView, OnItemClickListener editar, OnItemClickListener borrar){
            super(itemView);
            Button btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEditar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editar.OnItemClick(getAdapterPosition());
                }
            });
            Button btnBorrar = itemView.findViewById(R.id.btnBorrar);
            btnBorrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    borrar.OnItemClick(getAdapterPosition());
                }
            });
        }
    }

    @NonNull
    @Override
    public ObjetosViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(context).inflate(R.layout.listaobjetos_rv, parent,false);
        return new ObjetosViewHolder(itemView,editar,borrar);
    }

    @Override
    public void onBindViewHolder(ObjetosViewHolder holder, int position) {
        ObjetoDTO o = listaObjetos.get(position);
        holder.objeto = o;
        TextView textNombre = holder.itemView.findViewById(R.id.textNombre);
        TextView textTipo = holder.itemView.findViewById(R.id.textTipo);
        TextView textMarca = holder.itemView.findViewById(R.id.textMarca);
        TextView textCaracteristicas = holder.itemView.findViewById(R.id.textCaracteristicas);
        TextView textIncluye = holder.itemView.findViewById(R.id.textFecha);
        ImageView imageView = holder.itemView.findViewById(R.id.imageObjeto);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("objetos/"+o.getId()+"/photo.jpg");
        Glide.with(context).load(storageReference).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(imageView);

        textNombre.setText(o.getNombre());
        textTipo.setText(o.getTipo());
        textMarca.setText(o.getMarca());
        textCaracteristicas.setText(o.getCaracteristicas());
        textIncluye.setText(o.getFecha());

    }

    @Override
    public int getItemCount(){
        return listaObjetos.size();
    }

}
