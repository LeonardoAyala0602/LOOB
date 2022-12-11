package com.example.loob.cliente;

import android.content.Context;
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
import com.example.loob.dto.ObjetoDTO;
import com.example.loob.dto.SolicitudDTO;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.SolicitudesViewHolder>{

    private ArrayList<SolicitudDTO> listaSolicitudes;
    private Context context;

    public ArrayList<SolicitudDTO> getListaSolicitudes() {
        return listaSolicitudes;
    }

    public void setListaSolicitudes(ArrayList<SolicitudDTO> listaSolicitudes) {
        this.listaSolicitudes = listaSolicitudes;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public class SolicitudesViewHolder extends RecyclerView.ViewHolder{
        SolicitudDTO solicitudDTO;
        public SolicitudesViewHolder(@NonNull View itemView){
            super(itemView);
        }
    }

    @NonNull
    @Override
    public HistorialAdapter.SolicitudesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(context).inflate(R.layout.historial_rv,parent,false);
        return new HistorialAdapter.SolicitudesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(HistorialAdapter.SolicitudesViewHolder holder, int position) {
        SolicitudDTO s = listaSolicitudes.get(position);
        holder.solicitudDTO = s;
        TextView textNombre = holder.itemView.findViewById(R.id.textNombreS);
        TextView textDescripcion = holder.itemView.findViewById(R.id.textDescripcionS);
        TextView textCodigo = holder.itemView.findViewById(R.id.textCodigoS);
        TextView textEstado = holder.itemView.findViewById(R.id.textEstadoS);
        textNombre.setText(s.getNombreObjeto());
        textDescripcion.setText(s.getDescripcion());
        textCodigo.setText(s.getCodigoPUCP());
        textEstado.setText(s.getEstado());
    }

    @Override
    public int getItemCount() {
        return listaSolicitudes.size();
    }
}
