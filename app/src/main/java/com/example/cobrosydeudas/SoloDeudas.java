package com.example.cobrosydeudas;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cobrosydeudas.entidades.Cobro;
import com.example.cobrosydeudas.entidades.RegistrodeCobro;
import com.example.cobrosydeudas.utilidades.Utilidades;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.thecode.aestheticdialogs.AestheticDialog;
import com.thecode.aestheticdialogs.DialogAnimation;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SoloDeudas extends AppCompatActivity {

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    ArrayList<Cobro> listaCobro;
    BDDcobrosHelper conn;
    BDDcobrosHelper myDB;
    EditText buscarsolodeudas;
    TextView contactosdeudas;

    DialogStyle dialogStyle;
    DialogType dialogType;
    DialogAnimation dialogAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solo_deudas);
        Toolbar toolbar = findViewById(R.id.toolbarsolodeudas);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        cargarComponentes();
        listarSoloDeudas();
        recargar();
        buscarSoloDeudaMetodo();
    }

    public void cargarComponentes(){
        swipeRefreshLayout = findViewById(R.id.refreshLayoutSoloDeudas);
        conn = new BDDcobrosHelper(this, "bd_cobros", null, 1);
        myDB = new BDDcobrosHelper(getApplicationContext(), "db_cobros", null, 1);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerviewSoloDeudas);
        buscarsolodeudas = findViewById(R.id.txtbuscarsolodeudas);
    }

    private void msglistaactualizada() {
        dialogStyle = DialogStyle.TOASTER;
        dialogType = DialogType.SUCCESS;
        dialogAnimation = DialogAnimation.IN_OUT;
        AestheticDialog.Builder builder = new AestheticDialog.Builder(this, dialogStyle, dialogType);
        builder.setTitle("Actualizado");
        builder.setMessage("Su lista de deudas ha sido actualizada");
        builder.setDuration(1500);
        builder.setAnimation(dialogAnimation);
        builder.show();
    }



    private void buscarSoloDeudaMetodo() {
        buscarsolodeudas.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
    }

    public void filter(String palabra){
        try {
            String solodeuda = "Deuda";
            SQLiteDatabase db = conn.getReadableDatabase();
            listaCobro = new ArrayList<>();
            Cobro usuario = null;
            Cursor cursor = db.rawQuery("SELECT * FROM " + Utilidades.TABLA_COBRO+"" +
                    " WHERE ("+ Utilidades.CAMPO_NOMBRE+ " like '"+palabra+"%' "+"" +
                    " OR "+ Utilidades.CAMPO_APELLIDO+ " like '"+palabra+"%') AND "+Utilidades.CAMPO_TIPO+ " = '"+ solodeuda+"'",null);
            while (cursor.moveToNext()) {
                usuario = new Cobro();
                usuario.setId(cursor.getInt(cursor.getColumnIndex("id")));
                usuario.setNombre(cursor.getString(cursor.getColumnIndex("nombre")) + " " + cursor.getString(cursor.getColumnIndex("apellido")));
                usuario.setFechacobro(cursor.getString(cursor.getColumnIndex("fechacobro")));
                usuario.setCantidad(cursor.getString(cursor.getColumnIndex("cantidad")));
                usuario.setEstado(cursor.getString(cursor.getColumnIndex("estado")));
                usuario.setTipo(cursor.getString(cursor.getColumnIndex("tipo")));
                listaCobro.add(usuario);
            }
            ListaCobroAdapter adapter = new ListaCobroAdapter(listaCobro, this);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Integer iddetalle = listaCobro.get(recyclerView.getChildAdapterPosition(view)).getId();
                    String tip = listaCobro.get(recyclerView.getChildAdapterPosition(view)).getTipo();
                    Intent intent = new Intent(SoloDeudas.this, DetallesCobro.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("iddetalle", String.valueOf(iddetalle));
                    bundle.putString("tipo", tip);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);

                }
            });
            recyclerView.setAdapter(adapter);
        }catch (Exception e){
            Toast.makeText(this, "El cobro no existe", Toast.LENGTH_SHORT).show();
            Log.e("error",e.getMessage().toString());
        }
    }

    public void recargar() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listarSoloDeudas();
                swipeRefreshLayout.setRefreshing(false);
                msglistaactualizada();
            }
        });
    }

    public void listarSoloDeudas(){
        SQLiteDatabase db = conn.getReadableDatabase();
        listaCobro = new ArrayList<>();
        String solodeuda = "Deuda";
        Cobro usuario = null;
        String id = null;
        String tip = null;
        Cursor cursor = db.rawQuery("SELECT * FROM " + Utilidades.TABLA_COBRO +" WHERE "+Utilidades.CAMPO_TIPO+ " = '"+solodeuda+"'", null);
        while (cursor.moveToNext()) {
            usuario = new Cobro();
            id = usuario.setId(cursor.getInt(cursor.getColumnIndex("id")));
            usuario.setNombre(cursor.getString(cursor.getColumnIndex("nombre")) + " " + cursor.getString(cursor.getColumnIndex("apellido")));
            usuario.setFechacobro(cursor.getString(cursor.getColumnIndex("fechacobro")));
            usuario.setCantidad(cursor.getString(cursor.getColumnIndex("cantidad")));
            usuario.setEstado(cursor.getString(cursor.getColumnIndex("estado")));
            usuario.setTipo(cursor.getString(cursor.getColumnIndex("tipo")));
            listaCobro.add(usuario);
        }
        ListaCobroAdapter adapter = new ListaCobroAdapter(listaCobro, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer iddetalle = listaCobro.get(recyclerView.getChildAdapterPosition(view)).getId();
                String tip = listaCobro.get(recyclerView.getChildAdapterPosition(view)).getTipo();
                Intent intent = new Intent(SoloDeudas.this, DetallesCobro.class);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK){
            Uri uri = data.getData();
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()){
                int indiceNumero = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String numero = cursor.getString(indiceNumero);
                //numero = numero.replace("+593", "0").replace(" ", "");
                contactosdeudas.setText(numero);
            }
        }
    }



    public void listarDeudasVencidos(View view) {
        SQLiteDatabase db = conn.getReadableDatabase();
        listaCobro = new ArrayList<>();
        String solodeuda = "Deuda";
        Cobro usuario = null;
        String id = null;
        String tip = null;
        Cursor cursor = db.rawQuery("SELECT * FROM " + Utilidades.TABLA_COBRO +" WHERE "+Utilidades.CAMPO_TIPO+ " = '"+solodeuda+"' AND "+Utilidades.CAMPO_ESTADO +" = 'Vencido'", null);
        while (cursor.moveToNext()) {
            usuario = new Cobro();
            id = usuario.setId(cursor.getInt(cursor.getColumnIndex("id")));
            usuario.setNombre(cursor.getString(cursor.getColumnIndex("nombre")) + " " + cursor.getString(cursor.getColumnIndex("apellido")));
            usuario.setFechacobro(cursor.getString(cursor.getColumnIndex("fechacobro")));
            usuario.setCantidad(cursor.getString(cursor.getColumnIndex("cantidad")));
            usuario.setEstado(cursor.getString(cursor.getColumnIndex("estado")));
            usuario.setTipo(cursor.getString(cursor.getColumnIndex("tipo")));
            listaCobro.add(usuario);
        }
        ListaCobroAdapter adapter = new ListaCobroAdapter(listaCobro, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer iddetalle = listaCobro.get(recyclerView.getChildAdapterPosition(view)).getId();
                String tip = listaCobro.get(recyclerView.getChildAdapterPosition(view)).getTipo();
                Intent intent = new Intent(SoloDeudas.this, DetallesCobro.class);
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

    public void listarDeudasActivos(View view) {
        SQLiteDatabase db = conn.getReadableDatabase();
        listaCobro = new ArrayList<>();
        String solodeuda = "Deuda";
        Cobro usuario = null;
        String id = null;
        String tip = null;
        Cursor cursor = db.rawQuery("SELECT * FROM " + Utilidades.TABLA_COBRO +" WHERE "+Utilidades.CAMPO_TIPO+ " = '"+solodeuda+"' AND "+Utilidades.CAMPO_ESTADO +" = 'Activo'", null);
        while (cursor.moveToNext()) {
            usuario = new Cobro();
            id = usuario.setId(cursor.getInt(cursor.getColumnIndex("id")));
            usuario.setNombre(cursor.getString(cursor.getColumnIndex("nombre")) + " " + cursor.getString(cursor.getColumnIndex("apellido")));
            usuario.setFechacobro(cursor.getString(cursor.getColumnIndex("fechacobro")));
            usuario.setCantidad(cursor.getString(cursor.getColumnIndex("cantidad")));
            usuario.setEstado(cursor.getString(cursor.getColumnIndex("estado")));
            usuario.setTipo(cursor.getString(cursor.getColumnIndex("tipo")));
            listaCobro.add(usuario);
        }
        ListaCobroAdapter adapter = new ListaCobroAdapter(listaCobro, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer iddetalle = listaCobro.get(recyclerView.getChildAdapterPosition(view)).getId();
                String tip = listaCobro.get(recyclerView.getChildAdapterPosition(view)).getTipo();
                Intent intent = new Intent(SoloDeudas.this, DetallesCobro.class);
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
        Intent intent = new Intent(this , Todos.class);
        startActivity(intent);
        overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
    }


}