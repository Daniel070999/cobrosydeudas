package appcyb.danielpativas.cobrosydeudas;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import appcyb.danielpativas.cobrosydeudas.entidades.RegistrodeCobro;

import java.util.ArrayList;

public class ListaRegristroAdapter extends RecyclerView.Adapter<ListaRegristroAdapter.RegistroViewHolder> implements View.OnClickListener{

    ArrayList<RegistrodeCobro> listaRegistro;
    private View.OnClickListener listener;
    private  Context context;

    public ListaRegristroAdapter (ArrayList<RegistrodeCobro> listaRegistro, Context context){
        this.listaRegistro = listaRegistro;
        this.context = context;
    }


    @NonNull
    @Override
    public RegistroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_item_registro_cobros, null, false);
        view.setOnClickListener(this);
        return new RegistroViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListaRegristroAdapter.RegistroViewHolder holder, int position) {
        holder.cv.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_transition_reg));
        holder.fecha.setText(listaRegistro.get(position).getFechaaumentada());
        holder.cantidad.setText(listaRegistro.get(position).getCantidadaumentada());
        holder.nuevacantidad.setText(listaRegistro.get(position).getNuevacantidadaumentada());
        holder.descripcion.setText(listaRegistro.get(position).getDescripcionaumento());
        holder.accion.setText(listaRegistro.get(position).getAccionregistro());

        String cambiarsigno = listaRegistro.get(position).getAccionregistro();
        if (cambiarsigno.equals("Cantidad aumentada:")){
            holder.accion.setText("+");
            holder.accion.setTextColor(Color.parseColor("#229954"));
            holder.cantidad.setTextColor(Color.parseColor("#229954"));
        }else if (cambiarsigno.equals("Pago registrado:")){
            holder.accion.setText("-");
            holder.accion.setTextColor(Color.parseColor("#CB4335"));
            holder.cantidad.setTextColor(Color.parseColor("#CB4335"));
        }


    }

    @Override
    public int getItemCount() {
        return listaRegistro.size();
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener=listener;
    }

    @Override
    public void onClick(View view) {
        if(listener!=null){
            listener.onClick(view);
        }
    }

    public class RegistroViewHolder extends RecyclerView.ViewHolder {
        TextView fecha, cantidad, nuevacantidad, descripcion, accion;
        CardView cv;
        public RegistroViewHolder(@NonNull View itemView) {
            super(itemView);
            fecha = (TextView) itemView.findViewById(R.id.txtfecharegistro);
            cantidad = (TextView) itemView.findViewById(R.id.txtcantidadagregadaregistro);
            nuevacantidad = (TextView) itemView.findViewById(R.id.txtcantiddactualregistro);
            descripcion = (TextView) itemView.findViewById(R.id.txtdescripcionderegistro);
            accion = (TextView) itemView.findViewById(R.id.txtaccionregistro);
            cv = (CardView) itemView.findViewById(R.id.cardViewregistros);
        }
    }
}