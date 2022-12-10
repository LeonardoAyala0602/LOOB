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
import com.example.loob.dto.UsuarioDTO;

import java.util.ArrayList;

public class ListaClientesAdapter extends RecyclerView.Adapter<ListaClientesAdapter.UsuarioViewHolder>{

    private ArrayList<UsuarioDTO> listaUsuarios = new ArrayList<>();
    private Context context;

    public ArrayList<UsuarioDTO> getListaUsuarios() {
        return listaUsuarios;
    }

    public void setListaUsuarios(ArrayList<UsuarioDTO> listaUsuarios) {
        this.listaUsuarios = listaUsuarios;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public interface OnItemClickListener{
        void OnItemClick(int position);
    }

    public class UsuarioViewHolder extends RecyclerView.ViewHolder{
        UsuarioDTO usuario;
        public UsuarioViewHolder(@NonNull View itemView){
            super(itemView);
        }
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.listausuarios_rv,parent,false);
        return new UsuarioViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int position) {
        UsuarioDTO u = listaUsuarios.get(position);
        holder.usuario = u;
        TextView nombre = holder.itemView.findViewById(R.id.textNombreListaUsuarios);
        TextView dni = holder.itemView.findViewById(R.id.textDNIListaUsuarios);
        nombre.setText("Nombre: "+u.getCorreo());
        dni.setText("DNI: "+u.getDNI());
    }

    @Override
    public int getItemCount() {
        return listaUsuarios.size();
    }
}
