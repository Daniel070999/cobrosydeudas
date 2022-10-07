package com.example.cobrosydeudas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.cobrosydeudas.entidades.Cobro;
import com.example.cobrosydeudas.entidades.RegistrodeCobro;
import com.example.cobrosydeudas.utilidades.Utilidades;
import com.thecode.aestheticdialogs.DialogAnimation;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Papelera extends AppCompatActivity {


    RecyclerView recyclerView;
    ArrayList<Cobro> listaCobroEliminado;
    BDDcobrosHelper conn;
    ArrayList<Cobro> listaCobro;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_papelera);
        Toolbar toolbar = findViewById(R.id.toobarpapelera);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        conn = new BDDcobrosHelper(this, "bd_cobros", null, 1);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerviewListapapelera);

        cargarListaPapelera();
    }

    public void cargarListaPapelera() {
        SQLiteDatabase db = conn.getReadableDatabase();
        listaCobroEliminado = new ArrayList<>();
        Cobro usuario = null;
        String id = null;
        String tip = null;
        String estado = "Eliminado";
        Cursor cursor = db.rawQuery("SELECT * FROM " + Utilidades.TABLA_COBRO + " WHERE "+ Utilidades.CAMPO_ESTADO + " = " + "'"+estado+"'", null);
        while (cursor.moveToNext()) {
            usuario = new Cobro();
            id = usuario.setId(cursor.getInt(cursor.getColumnIndex("id")));
            usuario.setNombre(cursor.getString(cursor.getColumnIndex("nombre")) + " " + cursor.getString(cursor.getColumnIndex("apellido")));
            usuario.setFechacobro(cursor.getString(cursor.getColumnIndex("fechacobro")));
            usuario.setCantidad(cursor.getString(cursor.getColumnIndex("cantidad")));
            usuario.setEstado(cursor.getString(cursor.getColumnIndex("estado")));
            usuario.setTipo(cursor.getString(cursor.getColumnIndex("tipo")));
            listaCobroEliminado.add(usuario);
        }
        ListaCobroAdapter adapter = new ListaCobroAdapter(listaCobroEliminado, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer idcobro = listaCobroEliminado.get(recyclerView.getChildAdapterPosition(view)).getId();
                String fecharegistrada = listaCobroEliminado.get(recyclerView.getChildAdapterPosition(view)).getFechacobro();
                String nombreusuario = listaCobroEliminado.get(recyclerView.getChildAdapterPosition(view)).getNombre();
                String apellidousuario = listaCobroEliminado.get(recyclerView.getChildAdapterPosition(view)).getApellido();



                Dialog dialog;
                dialog = new Dialog(Papelera.this);
                dialog.setContentView(R.layout.dialogo_papelera);
                Button eliminar = dialog.findViewById(R.id.btneliminardefinitivo);
                Button restaurar = dialog.findViewById(R.id.btnrestaurar);
                TextView usuario = dialog.findViewById(R.id.txtusuarioenpapelera);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialogo_fondo));
                }
                dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
                dialog.show();

                usuario.setText(nombreusuario + "" +apellidousuario);

                eliminar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        eliminarCobro(idcobro);
                        dialog.dismiss();

                    }
                });
                restaurar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        restaurar(idcobro, fecharegistrada);
                        dialog.dismiss();
                    }
                });


            }
        });

        recyclerView.setAdapter(adapter);
    }
    public void restaurar(Integer idCobro, String fecha){
        try {
            String estado = null;
            Date fecharegistrada = null;
            Date fechaactual = null;
            DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            String fechaactualuno = String.valueOf(android.text.format.DateFormat.format("dd/MM/yyyy", new java.util.Date()));

            fechaactual = format.parse(fechaactualuno);
            fecharegistrada = format.parse(fecha);

            if (fecharegistrada.compareTo(fechaactual) <= 0){
                estado = "Vencido";
            }else if(fecharegistrada.compareTo(fechaactual) > 0){
                estado = "Activo";
            }
                SQLiteDatabase db = conn.getReadableDatabase();
                listaCobro = new ArrayList<>();
                Cobro usuario = null;
                Cursor cursor = db.rawQuery("UPDATE " + Utilidades.TABLA_COBRO+  " SET " + Utilidades.CAMPO_ESTADO+ " = " +
                        "'"+estado+"'" +  " WHERE " +Utilidades.CAMPO_ID + " = " +
                        ""+ idCobro, null);
                while (cursor.moveToNext()) {
                    usuario = new Cobro();
                    listaCobro.add(usuario);
                }
                db.close();

                Intent intent = new Intent(Papelera.this, Todos.class);
                startActivity(intent);
                overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    public void eliminarCobro(Integer idcobro){
        String idCobro = idcobro.toString();

                SQLiteDatabase db = conn.getReadableDatabase();
                listaCobro = new ArrayList<>();
                Cobro usuario = null;
                Cursor cursor = db.rawQuery("DELETE FROM " + Utilidades.TABLA_COBRO+ " WHERE " +Utilidades.CAMPO_ID + " = "
                        + idCobro, null);
                while (cursor.moveToNext()) {
                    usuario = new Cobro();
                    listaCobro.add(usuario);
                }
                Cursor cursoregistro = db.rawQuery("DELETE FROM " + Utilidades.TABLA_REGISTRO_COBRO+ " WHERE "
                        +Utilidades.CAMPO_REGISTRO_IDCOBRO + " = "+ idCobro, null);
                while (cursoregistro.moveToNext()) {
                    usuario = new Cobro();
                    listaCobro.add(usuario);
                }
                db.close();

                Intent intent = new Intent(Papelera.this, Todos.class);
                startActivity(intent);
                overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, Todos.class);
        startActivity(intent);
        overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
    }
}