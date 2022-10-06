package com.example.cobrosydeudas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cobrosydeudas.entidades.Cobro;
import com.example.cobrosydeudas.utilidades.Utilidades;
import com.thecode.aestheticdialogs.DialogAnimation;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Datos extends AppCompatActivity {

    TextView totalcobros, totaldeudas;
    ArrayList<Cobro> listaCobro;
    BDDcobrosHelper conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos);
        Toolbar toolbar = findViewById(R.id.toolbardatos);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        cargarcomponentes();
        cargarTotalCobros();
        cargarTotalDeudas();
    }



    private void cargarcomponentes() {
        totalcobros = findViewById(R.id.txttotalcobros);
        totaldeudas = findViewById(R.id.txttotaldeudas);
        conn = new BDDcobrosHelper(this, "bd_cobros", null, 1);
    }

    private void cargarTotalCobros() {
        SQLiteDatabase db = conn.getReadableDatabase();
        listaCobro = new ArrayList<>();
        String solocobro = "Cobro";
        Double cantidad = 0.0;
        Double cantidadaux = 0.0;
        DecimalFormat formato = new DecimalFormat("#.00");
        Cursor cursor = db.rawQuery("SELECT "+Utilidades.CAMPO_CANTIDAD+" FROM " + Utilidades.TABLA_COBRO +" WHERE "+Utilidades.CAMPO_TIPO+ " = '"+solocobro+"'", null);
        while (cursor.moveToNext()) {
            cantidad = Double.valueOf(cursor.getString(cursor.getColumnIndex("cantidad")));
            Double suma = (cantidad+cantidadaux);
            cantidadaux = Double.valueOf(formato.format(suma).replace(",",".")+"");
        }
        totalcobros.setText("$"+cantidadaux);
    }
    private void cargarTotalDeudas() {
        SQLiteDatabase db = conn.getReadableDatabase();
        listaCobro = new ArrayList<>();
        String solodeuda = "Deuda";
        Double cantidad = 0.0;
        Double cantidadaux = 0.0;
        DecimalFormat formato = new DecimalFormat("#.00");
        Cursor cursor = db.rawQuery("SELECT "+Utilidades.CAMPO_CANTIDAD+" FROM " + Utilidades.TABLA_COBRO +" WHERE "+Utilidades.CAMPO_TIPO+ " = '"+solodeuda+"'", null);
        while (cursor.moveToNext()) {
            cantidad = Double.valueOf(cursor.getString(cursor.getColumnIndex("cantidad")));
            Double suma = (cantidad+cantidadaux);
            cantidadaux = Double.valueOf(formato.format(suma).replace(",",".")+"");
        }
        totaldeudas.setText("$"+cantidadaux);
    }
}