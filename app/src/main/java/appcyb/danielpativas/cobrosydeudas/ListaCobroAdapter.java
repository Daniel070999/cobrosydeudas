package appcyb.danielpativas.cobrosydeudas;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import appcyb.danielpativas.cobrosydeudas.entidades.Cobro;

import java.util.ArrayList;


/**
 * Created by CHENAO on 8/07/2017.
 */

public class ListaCobroAdapter extends RecyclerView.Adapter<ListaCobroAdapter.PersonasViewHolder> implements View.OnClickListener{

    private  Context context;
    ArrayList<Cobro> listaUsuario;
    ArrayList<Cobro> originallistaUsuario;
    private View.OnClickListener listener;

    public ListaCobroAdapter(ArrayList<Cobro> listaUsuario, Context context) {
        this.listaUsuario = listaUsuario;
        this.context = context;
        this.originallistaUsuario = new ArrayList<>();
        originallistaUsuario.addAll(listaUsuario);
    }

    @Override
    public PersonasViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_item_cobros,null,false);
        view.setOnClickListener(this);
        return new PersonasViewHolder(view);
    }



    @Override
    public void onBindViewHolder(PersonasViewHolder holder, int position) {
        holder.cv.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_transition));
        holder.nombre.setText(listaUsuario.get(position).getNombre());
        holder.fechacobro.setText(listaUsuario.get(position).getFechacobro());
        holder.cantidad.setText(listaUsuario.get(position).getCantidad());
        holder.estado.setText(listaUsuario.get(position).getEstado());
        holder.tipo.setText(listaUsuario.get(position).getTipo());
        String estado = listaUsuario.get(position).getEstado();
        if (estado.equals("Activo")){
            holder.estado.setTextColor(Color.parseColor("#229954"));
        }else if (estado.equals("Vencido")){
            holder.estado.setTextColor(Color.parseColor("#CB4335"));
        }else if (estado.equals("Eliminado")){
            holder.estado.setTextColor(Color.parseColor("#FA0505"));
        }


    }

    @Override
    public int getItemCount() {
        return listaUsuario.size();
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

    public class PersonasViewHolder extends RecyclerView.ViewHolder{

        TextView nombre,fechacobro,cantidad, estado, tipo;
        CardView cv;

        public PersonasViewHolder(View itemView) {
            super(itemView);
            nombre = (TextView) itemView.findViewById(R.id.txtnombrecarview);
            fechacobro = (TextView) itemView.findViewById(R.id.txtfechacobrocardview);
            cantidad = (TextView) itemView.findViewById(R.id.txtcantidadcardview);
            estado = (TextView) itemView.findViewById(R.id.txtestadocardview);
            tipo = (TextView) itemView.findViewById(R.id.txttipo);
            cv = itemView.findViewById(R.id.cardView);
        }



    }
}
