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
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.cobrosydeudas.entidades.Cobro;
import com.example.cobrosydeudas.entidades.RegistrodeCobro;
import com.example.cobrosydeudas.utilidades.Utilidades;
import com.thecode.aestheticdialogs.DialogAnimation;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;

import java.util.ArrayList;

public class Papelera extends AppCompatActivity {


    RecyclerView recyclerView;
    ArrayList<Cobro> listaCobroEliminado;
    BDDcobrosHelper conn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_papelera);
        Toolbar toolbar = findViewById(R.id.toobarpapelera);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        conn = new BDDcobrosHelper(this, "bd_cobros", null, 1);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerviewListapapelera);

        cargarListPapelera();
    }

    public void cargarListPapelera() {
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
                Integer iddetalle = listaCobroEliminado.get(recyclerView.getChildAdapterPosition(view)).getId();
                String tip = listaCobroEliminado.get(recyclerView.getChildAdapterPosition(view)).getTipo();
                Intent intent = new Intent(Papelera.this, DetallesCobro.class);
                Bundle bundle = new Bundle();
                bundle.putString("iddetalle", String.valueOf(iddetalle));
                bundle.putString("tipo", tip);
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
            }
        });

        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, Todos.class);
        startActivity(intent);
        overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
    }
}