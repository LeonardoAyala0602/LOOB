package com.example.loob.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loob.R;
import com.example.loob.dto.SolicitudDTO;

import java.util.ArrayList;

public class ListaSolicitudesAdapter extends RecyclerView.Adapter<ListaSolicitudesAdapter.SolicitudViewHolder>{

    private ArrayList<SolicitudDTO> listaSolicitudes;
    private Context context;
    private OnItemClickListener aprobar;
    private OnItemClickListener rechazar;

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

    public OnItemClickListener getAprobar() {
        return aprobar;
    }

    public void setAprobar(OnItemClickListener aprobar) {
        this.aprobar = aprobar;
    }

    public OnItemClickListener getRechazar() {
        return rechazar;
    }

    public void setRechazar(OnItemClickListener rechazar) {
        this.rechazar = rechazar;
    }

    public interface OnItemClickListener{
        void OnItemClick(int position);
    }

    public class SolicitudViewHolder extends RecyclerView.ViewHolder{
        SolicitudDTO solicitud;

        public SolicitudViewHolder(@NonNull View itemView, OnItemClickListener aprobar, OnItemClickListener rechazar){
            super(itemView);
            Button btnAprobar = itemView.findViewById(R.id.btnAprobar);
            btnAprobar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    aprobar.OnItemClick(getAdapterPosition());
                }
            });
            Button btnRechazar = itemView.findViewById(R.id.btnRechazar);
            btnRechazar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    rechazar.OnItemClick(getAdapterPosition());
                }
            });
        }
    }

    @NonNull
    @Override
    public SolicitudViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.listasolicitudes_rv, parent,false);
        return new SolicitudViewHolder(itemView, aprobar, rechazar);
    }

    @Override
    public void onBindViewHolder(SolicitudViewHolder holder, int position) {
        SolicitudDTO s = listaSolicitudes.get(position);
        holder.solicitud = s;
        TextView textNombre = holder.itemView.findViewById(R.id.textNombreP);
        TextView textDescripcion = holder.itemView.findViewById(R.id.editTextDescripcion);
        TextView textDni = holder.itemView.findViewById(R.id.textDni);
        TextView textCodigo = holder.itemView.findViewById(R.id.textCodigoPUCP);
        TextView textEstado = holder.itemView.findViewById(R.id.textEstado);
        textNombre.setText(s.getNombreObjeto());
        textDescripcion.setText(s.getDescripcion());
        textDni.setText(s.getDni());
        textCodigo.setText(s.getCodigoPUCP());
        textEstado.setText(s.getEstado());
    }

    @Override
    public int getItemCount() {
            return listaSolicitudes.size();
        }
}
